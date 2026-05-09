package ir.hienob.noveo.desktop.data

import ir.hienob.noveo.core.ui.NoveoHomeChat
import ir.hienob.noveo.core.ui.NoveoHomeMessage
import java.io.File
import java.io.InterruptedIOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONArray
import okio.BufferedSink
import org.json.JSONObject

private const val CLIENT_VERSION = "desktop"

internal data class DesktopSession(
    val userId: String,
    val token: String,
    val sessionId: String = "",
    val expiresAt: Long = 0L
)

internal data class DesktopHomeSnapshot(
    val session: DesktopSession,
    val currentUsername: String = "",
    val currentUserBio: String = "",
    val chats: List<NoveoHomeChat>,
    val messagesByChat: Map<String, List<NoveoHomeMessage>>,
    val totalUnreadCount: Int
)

internal data class DesktopUploadedFile(
    val url: String,
    val name: String,
    val type: String,
    val size: Long
)

internal class DesktopNoveoApi(
    private val client: OkHttpClient = OkHttpClient(),
    private val wsUrl: String = "wss://noveo.ir:8443/ws",
    private val origin: String = "https://noveo.ir"
) {
    fun login(handle: String, password: String): DesktopSession = auth(
        JSONObject()
            .put("type", "login_with_password")
            .put("username", handle)
            .put("password", password)
            .put("languageCode", Locale.getDefault().language.ifBlank { "en" })
            .put("clientInfo", clientInfoJson())
    )

    fun loadHome(session: DesktopSession): DesktopHomeSnapshot {
        val sync = sync(session)
        val chats = parseChats(sync.history, sync.usersById, session.userId)
        val messagesByChat = parseMessagesByChat(sync.history, sync.usersById, session.userId)
        val currentUser = sync.usersById[session.userId]
        return DesktopHomeSnapshot(
            session = session,
            currentUsername = currentUser?.username.orEmpty(),
            currentUserBio = currentUser?.bio.orEmpty(),
            chats = chats,
            messagesByChat = messagesByChat,
            totalUnreadCount = chats.sumOf { it.unreadCount }
        )
    }

    fun sendMessage(
        session: DesktopSession,
        chatId: String,
        text: String,
        replyToId: String? = null,
        file: DesktopUploadedFile? = null
    ) {
        val latch = CountDownLatch(1)
        val failure = AtomicReference<String?>(null)
        val done = AtomicBoolean(false)
        val contentObject = JSONObject().put("text", text.takeIf { it.isNotBlank() })
        if (file != null) {
            contentObject.put(
                "file",
                JSONObject()
                    .put("url", file.url)
                    .put("name", file.name)
                    .put("type", file.type)
                    .put("size", file.size)
            )
        }
        val content = contentObject.toString()
        val clientTempId = "desktop-${System.currentTimeMillis()}"
        val socket = client.newWebSocket(request(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(reconnect(session).toString())
            }

            override fun onMessage(webSocket: WebSocket, textMsg: String) {
                val msg = JSONObject(textMsg)
                when (msg.optString("type")) {
                    "login_success" -> {
                        webSocket.send(
                            JSONObject()
                                .put("type", "message")
                                .put("chatId", chatId)
                                .put("content", content)
                                .put("replyToId", replyToId)
                                .put("clientTempId", clientTempId)
                                .toString()
                        )
                    }
                    "new_message", "message_sent", "chat_history" -> {
                        if (done.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                    "auth_failed", "error" -> {
                        failure.set(msg.optString("message", "Unable to send"))
                        if (done.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                failure.set(fail(response, t, "sending message"))
                if (done.compareAndSet(false, true)) latch.countDown()
            }
        })
        val finished = latch.await(20, TimeUnit.SECONDS)
        socket.cancel()
        if (!finished) error("Send timeout")
        failure.get()?.let { error(it) }
    }


    fun uploadFile(
        session: DesktopSession,
        file: File,
        mimeType: String,
        onProgress: (Float) -> Unit
    ): DesktopUploadedFile {
        val totalBytes = file.length().coerceAtLeast(1L)
        val requestBody = object : RequestBody() {
            override fun contentType() = mimeType.toMediaType()
            override fun contentLength() = file.length()
            override fun writeTo(sink: BufferedSink) {
                file.inputStream().use { input ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var uploaded = 0L
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        if (Thread.currentThread().isInterrupted) throw InterruptedIOException("Upload canceled")
                        sink.write(buffer, 0, read)
                        uploaded += read
                        onProgress(uploaded.toFloat() / totalBytes.toFloat())
                    }
                }
            }
        }
        val multipart = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, requestBody)
            .build()
        val uploadRequest = Request.Builder()
            .url("https://noveo.ir:8443/upload/file")
            .header("X-User-ID", session.userId)
            .header("X-Auth-Token", session.token)
            .header("User-Agent", "NoveoKotlin/$CLIENT_VERSION")
            .header("X-Noveo-Client", "kotlin-desktop")
            .header("X-Noveo-Version", CLIENT_VERSION)
            .post(multipart)
            .build()
        client.newCall(uploadRequest).execute().use { response ->
            if (!response.isSuccessful) {
                val body = response.body?.string().orEmpty()
                error("Upload failed (${response.code}): $body")
            }
            val payload = JSONObject(response.body?.string().orEmpty())
            if (!payload.optBoolean("success", false)) error(payload.optString("error", "Upload failed"))
            val uploaded = payload.optJSONObject("file") ?: error("Missing file info in upload response")
            return DesktopUploadedFile(
                url = uploaded.optString("url").sanitizeServerString(),
                name = uploaded.optString("name").sanitizeServerString().ifBlank { file.name },
                type = uploaded.optString("type").sanitizeServerString().ifBlank { mimeType },
                size = uploaded.optLong("size", file.length())
            )
        }
    }

    fun forwardMessage(session: DesktopSession, targetChatId: String, message: NoveoHomeMessage) {
        val content = JSONObject()
        if (message.text.isNotBlank()) content.put("text", message.text)
        if (!message.attachmentUrl.isNullOrBlank() || !message.attachmentName.isNullOrBlank()) {
            val file = JSONObject()
            message.attachmentUrl?.takeIf { it.isNotBlank() }?.let { file.put("url", it) }
            message.attachmentName?.takeIf { it.isNotBlank() }?.let { file.put("name", it) }
            message.attachmentType?.takeIf { it.isNotBlank() }?.let { file.put("type", it) }
            if (file.length() > 0) content.put("file", file)
        }
        content.put(
            "forwardedInfo",
            JSONObject()
                .put("from", message.senderName)
                .put("originalTs", message.rawTimestamp)
        )
        sendChatAction(
            session,
            JSONObject()
                .put("type", "message")
                .put("chatId", targetChatId)
                .put("content", content.toString())
                .put("replyToId", JSONObject.NULL)
                .put("clientTempId", "desktop-forward-${System.currentTimeMillis()}")
        )
    }

    fun editMessage(session: DesktopSession, chatId: String, messageId: String, newText: String) {
        sendChatAction(
            session,
            JSONObject()
                .put("type", "edit_message")
                .put("chatId", chatId)
                .put("messageId", messageId)
                .put("newContent", newText)
        )
    }

    fun toggleReaction(session: DesktopSession, chatId: String, messageId: String, emoji: String) {
        sendChatAction(
            session,
            JSONObject()
                .put("type", "toggle_reaction")
                .put("chatId", chatId)
                .put("messageId", messageId)
                .put("reaction", emoji)
        )
    }

    fun deleteMessage(session: DesktopSession, chatId: String, messageId: String) {
        sendChatAction(
            session,
            JSONObject()
                .put("type", "delete_message")
                .put("chatId", chatId)
                .put("messageId", messageId)
        )
    }

    fun pinMessage(session: DesktopSession, chatId: String, messageId: String, pin: Boolean) {
        val payload = JSONObject()
            .put("type", if (pin) "pin_message" else "unpin_message")
            .put("chatId", chatId)
        if (pin) payload.put("messageId", messageId)
        sendChatAction(session, payload)
    }

    fun joinChat(session: DesktopSession, chatId: String) {
        val latch = CountDownLatch(1)
        val failure = AtomicReference<String?>(null)
        val done = AtomicBoolean(false)
        val socket = client.newWebSocket(request(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(reconnect(session).toString())
            }

            override fun onMessage(webSocket: WebSocket, textMsg: String) {
                val msg = JSONObject(textMsg)
                when (msg.optString("type")) {
                    "login_success" -> {
                        webSocket.send(
                            JSONObject()
                                .put("type", "join_channel")
                                .put("chatId", chatId)
                                .toString()
                        )
                        if (done.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                    "auth_failed", "error" -> {
                        failure.set(msg.optString("message", "Unable to join"))
                        if (done.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                failure.set(fail(response, t, "joining chat"))
                if (done.compareAndSet(false, true)) latch.countDown()
            }
        })
        val finished = latch.await(10, TimeUnit.SECONDS)
        socket.cancel()
        if (!finished) error("Join timeout")
        failure.get()?.let { error(it) }
    }

    fun leaveChat(session: DesktopSession, chatId: String) {
        val body = JSONObject()
            .put("action", "leave_chat")
            .put("chatId", chatId)
            .toString()
            .toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://noveo.ir:8443/chat/settings")
            .header("X-User-ID", session.userId)
            .header("X-Auth-Token", session.token)
            .post(body)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Leave failed (${response.code})")
        }
    }

    fun createChat(session: DesktopSession, name: String, type: String, handle: String?, bio: String?) {
        val body = JSONObject()
            .put("title", name)
            .put("chatType", type)
            .put("handle", handle)
            .put("bio", bio)
            .toString()
            .toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://noveo.ir:8443/chat/create")
            .header("X-User-ID", session.userId)
            .header("X-Auth-Token", session.token)
            .post(body)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Chat creation failed (${response.code})")
        }
    }

    fun updateProfile(session: DesktopSession, username: String, bio: String) {
        sendChatAction(
            session,
            JSONObject()
                .put("type", "update_profile")
                .put("username", username)
                .put("bio", bio)
        )
    }

    fun changePassword(session: DesktopSession, oldPassword: String, newPassword: String) {
        sendChatAction(
            session,
            JSONObject()
                .put("type", "change_password")
                .put("oldPassword", oldPassword)
                .put("newPassword", newPassword)
        )
    }

    fun deleteAccount(session: DesktopSession, password: String) {
        sendChatAction(
            session,
            JSONObject()
                .put("type", "delete_account")
                .put("password", password)
        )
    }

    fun searchPublic(session: DesktopSession, query: String): List<NoveoHomeChat> {
        val normalizedQuery = query.trim()
        if (normalizedQuery.length < 2) return emptyList()
        val encodedQuery = URLEncoder.encode(normalizedQuery, Charsets.UTF_8.name())
        val request = Request.Builder()
            .url("https://noveo.ir:8443/user/public-search?q=$encodedQuery")
            .header("X-User-ID", session.userId)
            .header("X-Auth-Token", session.token)
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Public search failed (${response.code})")
            val payload = JSONObject(response.body?.string().orEmpty().ifBlank { "{}" })
            val dataArray = when {
                payload.optJSONArray("users") != null -> payload.optJSONArray("users")
                payload.optJSONArray("results") != null -> payload.optJSONArray("results")
                payload.optJSONArray("data") != null -> payload.optJSONArray("data")
                else -> JSONArray()
            } ?: JSONArray()

            return buildList {
                for (index in 0 until dataArray.length()) {
                    val item = dataArray.optJSONObject(index) ?: continue
                    val chatType = item.optString("chatType", item.optString("type", "")).sanitizeServerString()
                    val userId = item.optString("userId").sanitizeServerString()
                        .ifBlank { item.optString("user_id").sanitizeServerString() }
                        .ifBlank { item.optString("id").sanitizeServerString().takeIf { chatType.isBlank() }.orEmpty() }
                    val rawChatId = item.optString("chatId").sanitizeServerString()
                        .ifBlank { item.optString("chat_id").sanitizeServerString() }
                        .ifBlank { item.optString("id").sanitizeServerString().takeIf { chatType.isNotBlank() }.orEmpty() }
                    val resolvedChatType = chatType.ifBlank { "private" }
                    val resolvedChatId = when {
                        rawChatId.isNotBlank() -> rawChatId
                        userId.isNotBlank() -> listOf(session.userId, userId).sorted().joinToString("_")
                        else -> ""
                    }
                    if (resolvedChatId.isBlank()) continue
                    val title = item.optString("title").sanitizeServerString()
                        .ifBlank { item.optString("username").sanitizeServerString() }
                        .ifBlank { item.optString("name").sanitizeServerString() }
                        .ifBlank { item.optString("handle").sanitizeServerString() }
                        .ifBlank { resolvedChatId }
                    val handle = item.optString("handle").sanitizeServerString()
                    val bio = item.optString("bio").sanitizeServerString()
                    val memberIds = parseStringList(item.optJSONArray("members")).ifEmpty {
                        if (userId.isNotBlank()) listOf(session.userId, userId) else emptyList()
                    }
                    val subtitle = handle.takeIf { it.isNotBlank() }?.let { "@$it" }
                        ?: bio.takeIf { it.isNotBlank() }
                        ?: item.optString("lastMessagePreview").sanitizeServerString()
                    val isOnline = item.optBoolean("online", false) || item.optBoolean("isOnline", false)
                    val lastSeen = item.optLongNullable("lastSeen", "last_seen", "lastSeenAt", "lastActive", "lastOnline")
                    val headerSubtitle = when {
                        isOnline -> "online"
                        resolvedChatType == "private" -> formatDesktopLastSeen(lastSeen)
                        memberIds.isNotEmpty() -> "${memberIds.size} members"
                        handle.isNotBlank() -> "@$handle"
                        bio.isNotBlank() -> bio
                        else -> resolvedChatType
                    }
                    add(
                        NoveoHomeChat(
                            id = resolvedChatId,
                            title = title,
                            subtitle = subtitle,
                            headerSubtitle = headerSubtitle,
                            time = "",
                            unreadCount = 0,
                            avatarInitial = title.take(1).ifBlank { "N" },
                            isOnline = isOnline,
                            isVerified = item.optBoolean("isVerified", false),
                            canChat = item.optBoolean("canChat", true),
                            chatType = resolvedChatType,
                            memberIds = memberIds
                        )
                    )
                }
            }
        }
    }

    private fun sendChatAction(session: DesktopSession, payload: JSONObject) {
        val latch = CountDownLatch(1)
        val failure = AtomicReference<String?>(null)
        val done = AtomicBoolean(false)
        val socket = client.newWebSocket(request(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(reconnect(session).toString())
            }

            override fun onMessage(webSocket: WebSocket, textMsg: String) {
                val msg = JSONObject(textMsg)
                when (msg.optString("type")) {
                    "login_success" -> {
                        webSocket.send(payload.toString())
                        if (done.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                    "auth_failed", "error" -> {
                        failure.set(msg.optString("message", "Action failed"))
                        if (done.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                failure.set(fail(response, t, "sending chat action"))
                if (done.compareAndSet(false, true)) latch.countDown()
            }
        })
        val finished = latch.await(10, TimeUnit.SECONDS)
        socket.cancel()
        if (!finished) error("Action timeout")
        failure.get()?.let { error(it) }
    }

    private fun auth(payload: JSONObject): DesktopSession {
        val latch = CountDownLatch(1)
        val result = AtomicReference<DesktopSession?>(null)
        val failure = AtomicReference<String?>(null)
        val done = AtomicBoolean(false)
        val socket = client.newWebSocket(request(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(payload.toString())
            }

            override fun onMessage(webSocket: WebSocket, textMsg: String) {
                val msg = JSONObject(textMsg)
                when (msg.optString("type")) {
                    "login_success" -> {
                        val user = msg.optJSONObject("user")
                        val userId = user?.optString("userId").orEmpty()
                        val token = msg.optString("token")
                        if (userId.isBlank() || token.isBlank()) {
                            failure.set("Missing session token")
                        } else {
                            result.set(DesktopSession(userId, token, msg.optString("sessionId"), msg.optLong("expiresAt", 0L)))
                        }
                        if (done.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                    "auth_failed" -> {
                        failure.set(msg.optString("message", "Authentication failed"))
                        if (done.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                failure.set(fail(response, t, "authenticating"))
                if (done.compareAndSet(false, true)) latch.countDown()
            }
        })
        val finished = latch.await(20, TimeUnit.SECONDS)
        socket.cancel()
        if (!finished) error("Auth timeout")
        failure.get()?.let { error(it) }
        return result.get() ?: error("Authentication failed")
    }

    private fun sync(session: DesktopSession): DesktopSyncSnapshot {
        val latch = CountDownLatch(1)
        val history = AtomicReference<JSONObject?>(null)
        val users = AtomicReference<Map<String, DesktopUser>>(emptyMap())
        val online = AtomicReference<Set<String>>(emptySet())
        val failure = AtomicReference<String?>(null)
        val done = AtomicBoolean(false)
        val socket = client.newWebSocket(request(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(reconnect(session).toString())
            }

            override fun onMessage(webSocket: WebSocket, textMsg: String) {
                val msg = JSONObject(textMsg)
                when (msg.optString("type")) {
                    "login_success" -> webSocket.send(JSONObject().put("type", "resync_state").toString())
                    "user_list_update" -> {
                        val parsed = parseUsers(msg)
                        users.set(parsed.first)
                        online.set(parsed.second)
                    }
                    "chat_history" -> {
                        history.set(msg)
                        if (done.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                    "auth_failed" -> {
                        failure.set(msg.optString("message", "Authentication failed"))
                        if (done.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                failure.set(fail(response, t, "loading chats"))
                if (done.compareAndSet(false, true)) latch.countDown()
            }
        })
        val finished = latch.await(20, TimeUnit.SECONDS)
        socket.cancel()
        if (!finished) error("Sync timeout")
        failure.get()?.let { error(it) }
        return DesktopSyncSnapshot(users.get(), online.get(), history.get() ?: JSONObject().put("chats", JSONArray()))
    }

    private fun request(): Request = Request.Builder()
        .url(wsUrl)
        .header("Origin", origin)
        .header("User-Agent", "NoveoKotlin/$CLIENT_VERSION")
        .header("X-Noveo-Client", "kotlin-desktop")
        .header("X-Noveo-Version", CLIENT_VERSION)
        .build()

    private fun reconnect(session: DesktopSession): JSONObject = JSONObject()
        .put("type", "reconnect")
        .put("userId", session.userId)
        .put("token", session.token)
        .put("sessionId", session.sessionId)
        .put("clientInfo", clientInfoJson())

    private fun clientInfoJson(): JSONObject = JSONObject()
        .put("client", "kotlin")
        .put("platform", "desktop")
        .put("version", CLIENT_VERSION)

    private fun fail(response: Response?, t: Throwable, context: String): String {
        val code = response?.code
        return when (code) {
            404 -> "Noveo realtime server was not found while $context (HTTP 404)."
            401, 403 -> "Noveo rejected the realtime connection while $context (HTTP $code)."
            else -> "Socket failure while $context: ${t.message ?: t.javaClass.simpleName}"
        }
    }
}

private data class DesktopSyncSnapshot(
    val usersById: Map<String, DesktopUser>,
    val onlineUserIds: Set<String>,
    val history: JSONObject
)

private data class DesktopUser(
    val id: String,
    val username: String,
    val avatarUrl: String? = null,
    val handle: String? = null,
    val isOnline: Boolean = false,
    val isVerified: Boolean = false,
    val bio: String = "",
    val lastSeen: Long? = null
)

private fun JSONObject.optLongNullable(vararg keys: String): Long? {
    for (key in keys) {
        if (!has(key) || isNull(key)) continue
        val raw = opt(key)
        val value = when (raw) {
            is Number -> raw.toLong()
            is String -> raw.trim().toLongOrNull() ?: continue
            else -> continue
        }
        if (value > 0L) return value
    }
    return null
}

private fun formatDesktopLastSeen(lastSeen: Long?): String {
    if (lastSeen == null || lastSeen <= 0L) return "last seen recently"
    val nowSeconds = System.currentTimeMillis() / 1000L
    val diff = (nowSeconds - lastSeen).coerceAtLeast(0L)
    return when {
        diff < 60L -> "just now"
        diff < 3600L -> "${diff / 60L} minutes ago"
        diff < 86400L -> {
            val date = java.time.Instant.ofEpochSecond(lastSeen).atZone(java.time.ZoneId.systemDefault())
            "last seen at ${date.toLocalTime().toString().take(5)}"
        }
        diff < 172800L -> "last seen yesterday"
        diff < 604800L -> "last seen ${diff / 86400L} days ago"
        diff < 2592000L -> "last seen ${diff / 604800L} weeks ago"
        diff < 31536000L -> "last seen ${diff / 2592000L} months ago"
        else -> "last seen a long time ago"
    }
}

private fun headerSubtitleForChat(chatType: String, memberIds: List<String>, usersById: Map<String, DesktopUser>, selfUserId: String): String {
    return if (chatType == "private") {
        val peer = memberIds.firstOrNull { it != selfUserId }?.let(usersById::get)
        when {
            peer?.isOnline == true -> "online"
            else -> formatDesktopLastSeen(peer?.lastSeen)
        }
    } else {
        "${memberIds.size} members"
    }
}

private fun parseUsers(payload: JSONObject): Pair<Map<String, DesktopUser>, Set<String>> {
    val onlineIds = mutableSetOf<String>()
    val onlineArray = payload.optJSONArray("online") ?: JSONArray()
    for (index in 0 until onlineArray.length()) {
        onlineArray.optString(index).sanitizeServerString().takeIf { it.isNotBlank() }?.let(onlineIds::add)
    }

    val users = mutableMapOf<String, DesktopUser>()
    val usersArray = payload.optJSONArray("users") ?: JSONArray()
    for (index in 0 until usersArray.length()) {
        val item = usersArray.optJSONObject(index) ?: continue
        val userId = item.optString("userId").sanitizeServerString().ifBlank { item.optString("id").sanitizeServerString() }
        if (userId.isBlank()) continue
        users[userId] = DesktopUser(
            id = userId,
            username = item.optString("username").sanitizeServerString().ifBlank { item.optString("name").sanitizeServerString().ifBlank { "Unknown" } },
            avatarUrl = resolveAssetUrl(item, "avatarUrl", "avatar", "photo", "image"),
            handle = item.optString("handle").sanitizeServerString().takeIf { it.isNotBlank() },
            isOnline = onlineIds.contains(userId) || item.optBoolean("online", false),
            isVerified = item.optBoolean("isVerified", false),
            bio = item.optString("bio").sanitizeServerString(),
            lastSeen = item.optLongNullable("lastSeen", "last_seen", "lastSeenAt", "lastActive", "lastOnline")
        )
    }
    return users to onlineIds
}

private fun parseChats(payload: JSONObject, usersById: Map<String, DesktopUser>, selfUserId: String): List<NoveoHomeChat> {
    val chatsArray = payload.optJSONArray("chats") ?: JSONArray()
    return buildList {
        for (index in 0 until chatsArray.length()) {
            val item = chatsArray.optJSONObject(index) ?: continue
            val chatId = item.optString("chatId").sanitizeServerString()
                .ifBlank { item.optString("chat_id").sanitizeServerString() }
                .ifBlank { item.optString("id").sanitizeServerString() }
            if (chatId.isBlank()) continue
            val memberIds = parseStringList(item.optJSONArray("members"))
            val messages = item.optJSONArray("messages") ?: JSONArray()
            val unreadCount = (0 until messages.length()).count { messageIndex ->
                val msg = messages.optJSONObject(messageIndex) ?: return@count false
                val senderId = msg.optString("senderId").ifBlank { msg.optString("sender") }
                if (senderId == selfUserId) return@count false
                !parseStringList(msg.optJSONArray("seenBy")).contains(selfUserId)
            }
            val lastMsg = if (messages.length() > 0) messages.optJSONObject(messages.length() - 1) else null
            val preview = lastMsg?.let { parseMessageText(it.opt("content")) }.orEmpty()
            val timestamp = lastMsg?.optLong("timestamp", 0L) ?: 0L
            val chatType = item.optString("chatType", item.optString("type", "private")).sanitizeServerString()
            val ownerId = item.optString("ownerId").sanitizeServerString()
            val canChat = when (chatType) {
                "channel" -> ownerId == selfUserId
                "group" -> item.optJSONObject("permissions")?.optBoolean("canSendMessages", true) ?: true
                else -> true
            }
            val headerSubtitle = headerSubtitleForChat(chatType.ifBlank { "private" }, memberIds, usersById, selfUserId)
            add(
                NoveoHomeChat(
                    id = chatId,
                    title = resolveChatTitle(item, usersById, memberIds, selfUserId),
                    subtitle = preview,
                    headerSubtitle = headerSubtitle,
                    time = formatTime(timestamp),
                    unreadCount = unreadCount,
                    isOnline = memberIds.any { usersById[it]?.isOnline == true && it != selfUserId },
                    isVerified = item.optBoolean("isVerified", false),
                    canChat = canChat,
                    chatType = chatType.ifBlank { "private" },
                    memberIds = memberIds
                )
            )
        }
    }.sortedByDescending { chat -> chatsArrayIndexTime(payload, chat.id) }
}

private fun chatsArrayIndexTime(payload: JSONObject, chatId: String): Long {
    val chatsArray = payload.optJSONArray("chats") ?: return 0L
    for (index in 0 until chatsArray.length()) {
        val item = chatsArray.optJSONObject(index) ?: continue
        val itemChatId = item.optString("chatId").sanitizeServerString()
            .ifBlank { item.optString("chat_id").sanitizeServerString() }
            .ifBlank { item.optString("id").sanitizeServerString() }
        if (itemChatId != chatId) continue
        val messages = item.optJSONArray("messages") ?: return 0L
        return if (messages.length() > 0) messages.optJSONObject(messages.length() - 1)?.optLong("timestamp", 0L) ?: 0L else 0L
    }
    return 0L
}

private fun parseMessagesByChat(payload: JSONObject, usersById: Map<String, DesktopUser>, selfUserId: String): Map<String, List<NoveoHomeMessage>> {
    val chatsArray = payload.optJSONArray("chats") ?: JSONArray()
    return buildMap {
        for (index in 0 until chatsArray.length()) {
            val item = chatsArray.optJSONObject(index) ?: continue
            val chatId = item.optString("chatId").sanitizeServerString()
                .ifBlank { item.optString("chat_id").sanitizeServerString() }
                .ifBlank { item.optString("id").sanitizeServerString() }
            if (chatId.isBlank()) continue
            val messagesArray = item.optJSONArray("messages") ?: JSONArray()
            val messages = buildList {
                for (messageIndex in 0 until messagesArray.length()) {
                    val message = messagesArray.optJSONObject(messageIndex) ?: continue
                    add(parseMessage(message, chatId, usersById, selfUserId))
                }
            }
            put(chatId, messages)
        }
    }
}

private fun parseMessage(message: JSONObject, chatId: String, usersById: Map<String, DesktopUser>, selfUserId: String): NoveoHomeMessage {
    val senderId = message.optString("senderId").sanitizeServerString().ifBlank { message.optString("sender").sanitizeServerString() }
    val messageId = message.optString("messageId").sanitizeServerString().ifBlank { message.optString("id").sanitizeServerString() }
    val timestamp = message.optLong("timestamp", message.optLong("createdAt", 0L))
    val content = parseMessageContent(message.opt("content"))
    val seenBy = parseStringList(message.optJSONArray("seenBy"))
    val replyToId = message.optString("replyToId").sanitizeServerString()
    val replyObject = message.optJSONObject("replyTo") ?: message.optJSONObject("reply")
    val forwardedObject = message.optJSONObject("forwardedInfo") ?: message.optJSONObject("forwarded")
    return NoveoHomeMessage(
        id = messageId.ifBlank { "${chatId}-$timestamp" },
        senderId = senderId,
        senderName = usersById[senderId]?.username ?: message.optString("senderName").sanitizeServerString().ifBlank { "Unknown" },
        text = content.text.ifBlank { if (content.attachmentName == null) " " else "" },
        time = formatTime(timestamp),
        rawTimestamp = timestamp,
        isOutgoing = senderId == selfUserId,
        pending = message.optBoolean("pending", false),
        edited = message.optLong("editedAt", 0L) > 0,
        forwarded = forwardedObject != null,
        seen = seenBy.any { it == selfUserId || it.isNotBlank() } && senderId == selfUserId,
        replyAuthor = replyObject?.optString("senderName")?.sanitizeServerString(),
        replyPreview = replyObject?.let { parseMessageContent(it.opt("content")).previewText },
        attachmentName = content.attachmentName,
        attachmentUrl = content.attachmentUrl,
        attachmentType = content.attachmentType,
        attachmentSizeLabel = content.attachmentSizeLabel,
        reactions = parseReactionCounts(message.opt("reactions")),
        botButtons = parseInlineKeyboard(content.inlineKeyboard ?: message.opt("inlineKeyboard") ?: message.opt("keyboard")),
        dateLabel = formatDateLabel(timestamp),
        isPinned = message.optBoolean("isPinned", false) || message.optBoolean("pinned", false),
        isSystem = senderId == "system" || message.optString("type") == "system"
    )
}

private data class ParsedMessageContent(
    val text: String = "",
    val previewText: String = "",
    val attachmentName: String? = null,
    val attachmentUrl: String? = null,
    val attachmentType: String? = null,
    val attachmentSizeLabel: String? = null,
    val inlineKeyboard: Any? = null
)

private fun parseMessageContent(raw: Any?): ParsedMessageContent {
    val payload = when (raw) {
        is JSONObject -> raw
        is String -> {
            val text = raw.sanitizeServerString()
            if (text.startsWith("{") || text.startsWith("[")) {
                runCatching { JSONObject(text) }.getOrNull() ?: return ParsedMessageContent(text = text, previewText = text)
            } else {
                return ParsedMessageContent(text = text, previewText = text)
            }
        }
        else -> return ParsedMessageContent(text = raw?.toString().sanitizeServerString(), previewText = raw?.toString().sanitizeServerString())
    }
    val text = payload.optString("text").sanitizeServerString()
    val inlineKeyboard = payload.opt("inlineKeyboard") ?: payload.opt("keyboard") ?: payload.opt("buttons")
    payload.optJSONObject("file")?.let { file ->
        val type = file.optString("type").sanitizeServerString()
        val name = file.optString("name").sanitizeServerString().ifBlank { file.optString("fileName").sanitizeServerString() }
        val url = resolveAssetUrl(file, "url", "src", "path", "downloadUrl", "fileUrl")
        val size = file.optLong("size", 0L).takeIf { it > 0 }?.let(::formatBytes)
        val preview = when {
            type.startsWith("image/", true) -> "Photo"
            type.startsWith("video/", true) -> "Video"
            type.startsWith("audio/", true) -> "Audio"
            name.isNotBlank() -> name
            else -> "File"
        }
        return ParsedMessageContent(
            text = text,
            previewText = text.ifBlank { preview },
            attachmentName = name.ifBlank { preview },
            attachmentUrl = url,
            attachmentType = type.ifBlank { preview },
            attachmentSizeLabel = size,
            inlineKeyboard = inlineKeyboard
        )
    }
    payload.optJSONObject("poll")?.let { return ParsedMessageContent(text = text, previewText = text.ifBlank { "Poll" }, inlineKeyboard = inlineKeyboard) }
    payload.optJSONObject("callLog")?.let { return ParsedMessageContent(text = text.ifBlank { "Voice Call" }, previewText = "Voice Call", inlineKeyboard = inlineKeyboard) }
    return ParsedMessageContent(text = text, previewText = text, inlineKeyboard = inlineKeyboard)
}

private fun parseMessageText(raw: Any?): String = parseMessageContent(raw).previewText

private fun parseReactionCounts(raw: Any?): Map<String, Int> {
    val result = linkedMapOf<String, Int>()
    when (raw) {
        is JSONObject -> {
            val keys = raw.keys()
            while (keys.hasNext()) {
                val emoji = keys.next()
                val value = raw.opt(emoji)
                val count = when (value) {
                    is JSONArray -> value.length()
                    is Number -> value.toInt()
                    is JSONObject -> value.optJSONArray("users")?.length() ?: value.optInt("count", 0)
                    else -> 0
                }
                if (emoji.isNotBlank() && count > 0) result[emoji] = count
            }
        }
        is JSONArray -> {
            for (index in 0 until raw.length()) {
                val item = raw.optJSONObject(index) ?: continue
                val emoji = item.optString("emoji").sanitizeServerString()
                val count = item.optJSONArray("users")?.length() ?: item.optInt("count", 0)
                if (emoji.isNotBlank() && count > 0) result[emoji] = count
            }
        }
    }
    return result
}

private fun parseInlineKeyboard(raw: Any?): List<List<String>> {
    val rows = mutableListOf<List<String>>()
    when (raw) {
        is JSONArray -> {
            for (rowIndex in 0 until raw.length()) {
                val rowValue = raw.opt(rowIndex)
                val row = when (rowValue) {
                    is JSONArray -> buildList {
                        for (buttonIndex in 0 until rowValue.length()) {
                            val button = rowValue.opt(buttonIndex)
                            when (button) {
                                is JSONObject -> button.optString("text").sanitizeServerString()
                                else -> button?.toString().sanitizeServerString()
                            }.takeIf { it.isNotBlank() }?.let(::add)
                        }
                    }
                    is JSONObject -> listOf(rowValue.optString("text").sanitizeServerString()).filter { it.isNotBlank() }
                    else -> listOf(rowValue?.toString().sanitizeServerString()).filter { it.isNotBlank() }
                }
                if (row.isNotEmpty()) rows += row
            }
        }
        is JSONObject -> {
            raw.optJSONArray("rows")?.let { return parseInlineKeyboard(it) }
            raw.optJSONArray("buttons")?.let { return parseInlineKeyboard(it) }
            raw.optString("text").sanitizeServerString().takeIf { it.isNotBlank() }?.let { rows += listOf(it) }
        }
    }
    return rows
}

private fun formatDateLabel(timestamp: Long): String {
    if (timestamp <= 0L) return ""
    val millis = if (timestamp < 10_000_000_000L) timestamp * 1000L else timestamp
    return SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(millis))
}

private fun formatBytes(bytes: Long): String {
    val units = listOf("B", "KB", "MB", "GB")
    var value = bytes.toDouble()
    var unit = 0
    while (value >= 1024.0 && unit < units.lastIndex) {
        value /= 1024.0
        unit += 1
    }
    return if (unit == 0) "${bytes} B" else String.format(Locale.US, "%.1f %s", value, units[unit])
}

private fun resolveChatTitle(chat: JSONObject, usersById: Map<String, DesktopUser>, memberIds: List<String>, selfUserId: String): String {
    chat.optString("chatName").sanitizeServerString().takeIf { it.isNotBlank() }?.let { return it }
    val chatType = chat.optString("chatType", chat.optString("type", "private")).sanitizeServerString()
    if (chatType == "private") {
        if (memberIds.size == 1 && memberIds.firstOrNull() == selfUserId) return "Saved Messages"
        return memberIds.firstOrNull { it != selfUserId }
            ?.let(usersById::get)
            ?.username
            ?.takeIf { it.isNotBlank() }
            ?: "Direct Message"
    }
    return chat.optString("title").sanitizeServerString().ifBlank { "Chat" }
}

private fun resolveAssetUrl(source: JSONObject, vararg keys: String): String? {
    for (key in keys) {
        val direct = source.optString(key).sanitizeServerString()
        if (direct.isNotBlank()) return direct
        val nested = source.optJSONObject(key) ?: continue
        listOf("url", "src", "path", "downloadUrl", "fileUrl", "thumbUrl", "thumbnailUrl")
            .map { nested.optString(it).sanitizeServerString() }
            .firstOrNull { it.isNotBlank() }
            ?.let { return it }
    }
    return null
}

private fun parseStringList(array: JSONArray?): List<String> {
    if (array == null) return emptyList()
    return buildList {
        for (index in 0 until array.length()) array.optString(index).sanitizeServerString().takeIf { it.isNotBlank() }?.let(::add)
    }
}

private fun formatTime(timestamp: Long): String {
    if (timestamp <= 0L) return ""
    val millis = if (timestamp < 10_000_000_000L) timestamp * 1000L else timestamp
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(millis))
}

private fun String?.sanitizeServerString(): String {
    val value = this?.trim().orEmpty()
    val lower = value.lowercase()
    return if (lower == "null" || lower == "undefined" || lower == "none") "" else value
}
