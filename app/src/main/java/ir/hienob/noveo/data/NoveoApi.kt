package ir.hienob.noveo.data

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONArray
import org.json.JSONObject

class NoveoApi(
    private val client: OkHttpClient = OkHttpClient(),
    private val wsUrl: String = "wss://noveo.ir:8443/ws",
    private val origin: String = "https://noveo.ir"
) {
    class UploadController {
        @Volatile
        var canceled: Boolean = false

        @Volatile
        var call: okhttp3.Call? = null

        fun cancel() {
            canceled = true
            call?.cancel()
        }
    }

    private val userAgent = "NoveoKotlin/${ir.hienob.noveo.BuildConfig.VERSION_NAME}"

    fun login(handle: String, password: String): Session = auth(
        JSONObject()
            .put("type", "login_with_password")
            .put("username", handle)
            .put("password", password)
            .put("languageCode", "en")
            .put("clientInfo", clientInfoJson())
    )
    fun signup(handle: String, password: String, captchaToken: String? = null): Session = auth(
        JSONObject()
            .put("type", "register")
            .put("username", handle)
            .put("password", password)
            .put("languageCode", "en")
            .put("clientInfo", clientInfoJson())
            .apply { if (captchaToken != null) put("captchaToken", captchaToken) }
    )

    fun createChat(session: Session, name: String, type: String, handle: String? = null, bio: String? = null, captchaToken: String? = null) {
        val url = "https://noveo.ir:8443/chat/create".toHttpUrl()
        val body = JSONObject()
            .put("title", name)
            .put("chatType", type)
            .put("handle", handle)
            .put("bio", bio)
            .apply { if (captchaToken != null) put("captchaToken", captchaToken) }
            .toString()
        val request = Request.Builder()
            .url(url)
            .header("X-User-ID", session.userId)
            .header("X-Auth-Token", session.token)
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Chat creation failed (${response.code})")
        }
    }

    fun joinChat(session: Session, chatId: String) {
        val url = "https://noveo.ir:8443/chat/join".toHttpUrl()
        val body = JSONObject().put("chatId", chatId).toString()
        val request = Request.Builder()
            .url(url)
            .header("X-User-ID", session.userId)
            .header("X-Auth-Token", session.token)
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Join failed (${response.code})")
        }
    }

    fun leaveChat(session: Session, chatId: String) {
        val url = "https://noveo.ir:8443/chat/settings".toHttpUrl()
        val body = JSONObject()
            .put("action", "leave_chat")
            .put("chatId", chatId)
            .toString()
        val request = Request.Builder()
            .url(url)
            .header("X-User-ID", session.userId)
            .header("X-Auth-Token", session.token)
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Leave failed (${response.code})")
        }
    }

    fun resolveHandle(session: Session, handle: String): JSONObject {
        val url = "https://noveo.ir:8443/chat/resolve-handle".toHttpUrl().newBuilder()
            .addQueryParameter("handle", handle)
            .build()
        val request = Request.Builder()
            .url(url)
            .header("X-User-ID", session.userId)
            .header("X-Auth-Token", session.token)
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Handle resolve failed (${response.code})")
            return JSONObject(response.body?.string().orEmpty())
        }
    }

    fun uploadFile(
        session: Session,
        fileData: ByteArray,
        fileName: String,
        mimeType: String,
        controller: UploadController = UploadController(),
        onProgress: (Float) -> Unit
    ): MessageFileAttachment {
        val url = "https://noveo.ir:8443/upload/file".toHttpUrl()
        
        // Custom request body to track progress
        val requestBody = object : okhttp3.RequestBody() {
            override fun contentType() = mimeType.toMediaType()
            override fun contentLength() = fileData.size.toLong()
            override fun writeTo(sink: okio.BufferedSink) {
                val total = contentLength()
                var uploaded = 0L
                val buffer = ByteArray(4096)
                val inputStream = fileData.inputStream()
                
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    if (controller.canceled) {
                        throw java.io.InterruptedIOException("Upload canceled")
                    }
                    sink.write(buffer, 0, read)
                    uploaded += read
                    onProgress(uploaded.toFloat() / total)
                }
            }
        }

        val multipartBody = okhttp3.MultipartBody.Builder()
            .setType(okhttp3.MultipartBody.FORM)
            .addFormDataPart("file", fileName, requestBody)
            .build()

        val request = Request.Builder()
            .url(url)
            .header("X-User-ID", session.userId)
            .header("X-Auth-Token", session.token)
            .header("User-Agent", userAgent)
            .header("X-Noveo-Client", "kotlin")
            .header("X-Noveo-Version", ir.hienob.noveo.BuildConfig.VERSION_NAME)
            .post(multipartBody)
            .build()

        val call = client.newCall(request)
        controller.call = call
        call.execute().use { response ->
            if (!response.isSuccessful) {
                val errBody = response.body?.string().orEmpty()
                error("Upload failed (${response.code}): $errBody")
            }
            val payload = JSONObject(response.body?.string().orEmpty())
            if (!payload.optBoolean("success", false)) error(payload.optString("error", "Unknown error"))
            
            val fileJson = payload.optJSONObject("file") ?: error("Missing file info in response")
            return MessageFileAttachment(
                url = fileJson.optString("url"),
                name = fileJson.optString("name"),
                type = fileJson.optString("type"),
                size = fileJson.optLong("size", 0L)
            )
        }
    }

    fun getStarsOverview(session: Session): Wallet {
        val url = "https://noveo.ir:8443/stars/overview".toHttpUrl()
        val request = Request.Builder()
            .url(url)
            .header("X-User-ID", session.userId)
            .header("X-Auth-Token", session.token)
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Stars load failed (${response.code})")
            val payload = JSONObject(response.body?.string().orEmpty())
            val walletJson = payload.optJSONObject("wallet") ?: error("Missing wallet data")
            val txArray = walletJson.optJSONArray("transactions") ?: JSONArray()
            val transactions = (0 until txArray.length()).map { i ->
                val tx = txArray.getJSONObject(i)
                Transaction(
                    id = tx.optString("transactionId"),
                    amountTenths = tx.optInt("amountTenths"),
                    balanceAfterTenths = tx.optInt("balanceAfterTenths"),
                    type = tx.optString("type"),
                    description = tx.optString("description"),
                    createdAt = tx.optLong("createdAt"),
                    relatedUserId = tx.optString("relatedUserId").takeIf { it.isNotBlank() && it != "null" }
                )
            }
            return Wallet(
                balanceTenths = walletJson.optInt("balanceTenths"),
                balanceLabel = walletJson.optString("balanceLabel", "0.00"),
                transactions = transactions
            )
        }
    }

    fun getHomeData(session: Session): HomeData {
        val sync = sync(session)
        return HomeData(sync.usersById, sync.onlineUserIds, parseChats(sync.history, sync.usersById, session.userId))
    }

    fun getMessages(session: Session, chatId: String): MessageLoadResult {
        val sync = sync(session)
        return MessageLoadResult(sync.usersById, parseMessagesForChat(sync.history, sync.usersById, chatId))
    }

    fun getContacts(session: Session): List<UserSummary> {
        val url = "https://noveo.ir:8443/user/contacts".toHttpUrl()
        val request = Request.Builder()
            .url(url)
            .header("X-User-ID", session.userId)
            .header("X-Auth-Token", session.token)
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Contacts load failed (${response.code})")
            val payload = JSONObject(response.body?.string().orEmpty().ifBlank { "{}" })
            val contactsArray = payload.optJSONArray("contacts") ?: JSONArray()
            return (0 until contactsArray.length()).mapNotNull { index ->
                contactsArray.optJSONObject(index)?.let { user ->
                    val id = user.optString("userId").ifBlank { user.optString("id") }
                    if (id.isBlank()) return@let null
                    UserSummary(
                        id = id,
                        username = user.optString("contactName").ifBlank { user.optString("username").ifBlank { user.optString("name").ifBlank { id } } },
                        avatarUrl = user.optString("avatarUrl").ifBlank { null },
                        handle = user.optString("handle").ifBlank { null },
                        bio = user.optString("bio", ""),
                        isOnline = user.optBoolean("online", false),
                        isVerified = user.optBoolean("isVerified", false)
                    )
                }
            }
        }
    }

    fun getSavedStickers(session: Session): List<SavedSticker> {
        val url = "https://noveo.ir:8443/user/stickers".toHttpUrl()
        val request = Request.Builder()
            .url(url)
            .header("X-User-ID", session.userId)
            .header("X-Auth-Token", session.token)
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Saved stickers load failed (${response.code})")
            return parseStickerList(JSONObject(response.body?.string().orEmpty().ifBlank { "{}" }))
        }
    }

    fun addSavedSticker(session: Session, sticker: SavedSticker): List<SavedSticker> {
        val url = "https://noveo.ir:8443/user/stickers".toHttpUrl()
        val body = JSONObject()
            .put("action", "add")
            .put(
                "sticker",
                JSONObject()
                    .put("url", sticker.url)
                    .put("type", sticker.type)
            )
            .toString()
        val request = Request.Builder()
            .url(url)
            .header("X-User-ID", session.userId)
            .header("X-Auth-Token", session.token)
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Saved sticker update failed (${response.code})")
            return parseStickerList(JSONObject(response.body?.string().orEmpty().ifBlank { "{}" }))
        }
    }

    fun sendMessage(session: Session, chatId: String, text: String, file: MessageFileAttachment? = null, clientTempId: String? = null, replyToId: String? = null) {
        val latch = CountDownLatch(1)
        val failure = AtomicReference<String?>(null)
        val done = AtomicBoolean(false)
        val contentObj = JSONObject().put("text", text.takeIf { it.isNotBlank() })
        if (file != null) {
            contentObj.put("file", JSONObject()
                .put("url", file.url)
                .put("name", file.name)
                .put("type", file.type))
        }
        val content = contentObj.toString()
        val socket = client.newWebSocket(request(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) { webSocket.send(reconnect(session).toString()) }
            override fun onMessage(webSocket: WebSocket, textMsg: String) {
                val msg = JSONObject(textMsg)
                when (msg.optString("type")) {
                    "login_success" -> {
                        val payload = JSONObject()
                            .put("type", "message")
                            .put("chatId", chatId)
                            .put("content", content)
                            .put("replyToId", replyToId)
                            .put("clientTempId", clientTempId)
                        webSocket.send(payload.toString())
                    }
                    "new_message", "message_sent", "chat_history" -> { if (done.compareAndSet(false, true)) latch.countDown(); webSocket.close(1000, null) }
                    "auth_failed", "error" -> { failure.set(msg.optString("message", "Unable to send")); if (done.compareAndSet(false, true)) latch.countDown(); webSocket.close(1000, null) }
                }
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) { failure.set(fail(response, t, "sending message")); if (done.compareAndSet(false, true)) latch.countDown() }
        })
        val finished = latch.await(20, TimeUnit.SECONDS)
        socket.cancel()
        if (!finished) error("Send timeout")
        failure.get()?.let { error(it) }
    }

    fun markAsSeen(session: Session, chatId: String, messageId: String) {
        val socket = client.newWebSocket(request(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(reconnect(session).toString())
            }
            override fun onMessage(webSocket: WebSocket, textMsg: String) {
                val msg = JSONObject(textMsg)
                if (msg.optString("type") == "login_success") {
                    webSocket.send(JSONObject().put("type", "message_seen").put("chatId", chatId).put("messageId", messageId).toString())
                    webSocket.close(1000, null)
                }
            }
        })
    }

    fun sendTyping(session: Session, chatId: String) {
        val socket = client.newWebSocket(request(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(reconnect(session).toString())
            }
            override fun onMessage(webSocket: WebSocket, textMsg: String) {
                val msg = JSONObject(textMsg)
                if (msg.optString("type") == "login_success") {
                    webSocket.send(JSONObject().put("type", "typing").put("chatId", chatId).toString())
                    webSocket.close(1000, null)
                }
            }
        })
    }

    fun searchPublicUsers(session: Session, query: String): Pair<List<UserSummary>, List<ChatSummary>> {
        val normalizedQuery = query.trim()
        if (normalizedQuery.length < 2) return emptyPair()
        val url = "https://noveo.ir:8443/user/public-search".toHttpUrl().newBuilder()
            .addQueryParameter("q", normalizedQuery)
            .build()
        val request = Request.Builder()
            .url(url)
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
            }
            
            val users = mutableListOf<UserSummary>()
            val chats = mutableListOf<ChatSummary>()
            
            for (i in 0 until dataArray.length()) {
                val item = dataArray.optJSONObject(i) ?: continue
                val userId = item.optString("userId").ifBlank { item.optString("id") }
                val chatId = item.optString("chatId")
                
                if (chatId.isNotBlank() || item.optString("chatType").isNotBlank()) {
                    chats.add(parseChat(item, emptyMap(), session.userId))
                } else if (userId.isNotBlank()) {
                    users.add(UserSummary(
                        id = userId,
                        username = item.optString("username").ifBlank { item.optString("name").ifBlank { userId } },
                        avatarUrl = item.optString("avatarUrl").ifBlank { null },
                        handle = item.optString("handle").ifBlank { null },
                        bio = item.optString("bio", ""),
                        isOnline = item.optBoolean("online", false),
                        isVerified = item.optBoolean("isVerified", false)
                    ))
                }
            }
            return users to chats
        }
    }
    
    private fun <A, B> emptyPair(): Pair<List<A>, List<B>> = emptyList<A>() to emptyList<B>()

    fun checkForUpdate(): JSONObject? {
        val url = "https://noveo.ir/update.json".toHttpUrl()
        val request = Request.Builder().url(url).get().build()
        return runCatching {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) JSONObject(response.body?.string().orEmpty()) else null
            }
        }.getOrNull()
    }

    fun updateProfile(session: Session, username: String, bio: String) {
        val url = "https://noveo.ir:8443/user/profile".toHttpUrl()
        val body = JSONObject()
            .put("username", username)
            .put("bio", bio)
            .toString()
        val request = Request.Builder()
            .url(url)
            .header("X-User-ID", session.userId)
            .header("X-Auth-Token", session.token)
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Profile update failed (${response.code})")
        }
    }

    fun getVoiceToken(session: Session, chatId: String, callId: String? = null): JSONObject {
        val url = "https://noveo.ir:8443/voice/token".toHttpUrl()
        val body = JSONObject()
            .put("chatId", chatId)
            .apply { if (callId != null) put("callId", callId) }
            .toString()
        val request = Request.Builder()
            .url(url)
            .header("X-User-ID", session.userId)
            .header("X-Auth-Token", session.token)
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errBody = response.body?.string().orEmpty()
                error("Voice token fetch failed (${response.code}): $errBody")
            }
            val payload = JSONObject(response.body?.string().orEmpty())
            if (!payload.optBoolean("success", false)) error(payload.optString("error", "Unable to start voice chat."))
            return payload
        }
    }

    fun sendBotCallback(session: Session, chatId: String, messageId: String, callbackData: String): JSONObject {
        val url = "https://noveo.ir:8443/bot/callback".toHttpUrl()
        val body = JSONObject()
            .put("chatId", chatId)
            .put("messageId", messageId)
            .put("callbackData", callbackData)
            .toString()
        val request = Request.Builder()
            .url(url)
            .header("X-User-ID", session.userId)
            .header("X-Auth-Token", session.token)
            .header("User-Agent", userAgent)
            .header("X-Noveo-Client", "kotlin")
            .header("X-Noveo-Version", ir.hienob.noveo.BuildConfig.VERSION_NAME)
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Bot callback failed (${response.code})")
            return JSONObject(response.body?.string().orEmpty())
        }
    }

    fun startCaptcha(session: Session?, action: String, extra: Map<String, Any> = emptyMap()): JSONObject {
        val url = "https://web.noveo.ir/puzzle.php?proxy=1&target=/captcha/start".toHttpUrl()
        val body = JSONObject().put("action", action).apply {
            extra.forEach { (k, v) -> put(k, v) }
        }.toString()
        val builder = Request.Builder()
            .url(url)
            .header("Origin", origin)
            .header("User-Agent", userAgent)
            .header("X-Noveo-Client", "kotlin")
            .header("X-Noveo-Version", ir.hienob.noveo.BuildConfig.VERSION_NAME)
        if (session != null) {
            builder.header("X-User-ID", session.userId)
            builder.header("X-Auth-Token", session.token)
        }
        val request = builder.post(body.toRequestBody("application/json".toMediaType())).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Captcha start failed (${response.code})")
            return JSONObject(response.body?.string().orEmpty())
        }
    }

    fun auth(payload: JSONObject): Session {
        val latch = CountDownLatch(1)
        val result = AtomicReference<Session?>(null)
        val failure = AtomicReference<String?>(null)
        val done = AtomicBoolean(false)
        val socket = client.newWebSocket(request(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) { webSocket.send(payload.toString()) }
            override fun onMessage(webSocket: WebSocket, textMsg: String) {
                val msg = JSONObject(textMsg)
                when (msg.optString("type")) {
                    "login_success" -> {
                        val user = msg.optJSONObject("user")
                        val userId = user?.optString("userId").orEmpty()
                        val token = msg.optString("token")
                        if (userId.isBlank() || token.isBlank()) failure.set("Missing session token") else result.set(Session(userId, token, msg.optString("sessionId"), msg.optLong("expiresAt", 0L)))
                        if (done.compareAndSet(false, true)) latch.countDown(); webSocket.close(1000, null)
                    }
                    "auth_failed" -> { failure.set(msg.optString("message", "Authentication failed")); if (done.compareAndSet(false, true)) latch.countDown(); webSocket.close(1000, null) }
                }
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) { failure.set(fail(response, t, "authenticating")); if (done.compareAndSet(false, true)) latch.countDown() }
        })
        val finished = latch.await(20, TimeUnit.SECONDS)
        socket.cancel()
        if (!finished) error("Auth timeout")
        failure.get()?.let { error(it) }
        return result.get() ?: error("Authentication failed")
    }

    private fun sync(session: Session): SyncSnapshot {
        val latch = CountDownLatch(1)
        val history = AtomicReference<JSONObject?>(null)
        val users = AtomicReference<Map<String, UserSummary>>(emptyMap())
        val online = AtomicReference<Set<String>>(emptySet())
        val failure = AtomicReference<String?>(null)
        val done = AtomicBoolean(false)
        val socket = client.newWebSocket(request(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) { webSocket.send(reconnect(session).toString()) }
            override fun onMessage(webSocket: WebSocket, textMsg: String) {
                val msg = JSONObject(textMsg)
                when (msg.optString("type")) {
                    "login_success" -> webSocket.send(JSONObject().put("type", "resync_state").toString())
                    "user_list_update" -> { val parsed = parseUsers(msg); users.set(parsed.first); online.set(parsed.second) }
                    "chat_history" -> { history.set(msg); if (done.compareAndSet(false, true)) latch.countDown(); webSocket.close(1000, null) }
                    "auth_failed" -> { failure.set(msg.optString("message", "Authentication failed")); if (done.compareAndSet(false, true)) latch.countDown(); webSocket.close(1000, null) }
                }
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) { failure.set(fail(response, t, "loading chats")); if (done.compareAndSet(false, true)) latch.countDown() }
        })
        val finished = latch.await(20, TimeUnit.SECONDS)
        socket.cancel()
        if (!finished) error("Sync timeout")
        failure.get()?.let { error(it) }
        return SyncSnapshot(users.get(), online.get(), history.get() ?: JSONObject().put("chats", JSONArray()))
    }

    private fun request(): Request = Request.Builder()
        .url(wsUrl)
        .header("Origin", origin)
        .header("User-Agent", userAgent)
        .header("X-Noveo-Client", "kotlin")
        .header("X-Noveo-Version", ir.hienob.noveo.BuildConfig.VERSION_NAME)
        .build()

    private fun reconnect(session: Session): JSONObject = JSONObject()
        .put("type", "reconnect")
        .put("userId", session.userId)
        .put("token", session.token)
        .put("sessionId", session.sessionId)
        .put("clientInfo", clientInfoJson())

    private fun clientInfoJson(): JSONObject = JSONObject()
        .put("client", "kotlin")
        .put("platform", "android")
        .put("version", ir.hienob.noveo.BuildConfig.VERSION_NAME)
    private fun parseStickerList(payload: JSONObject): List<SavedSticker> {
        val stickersArray = payload.optJSONArray("stickers") ?: JSONArray()
        return (0 until stickersArray.length()).mapNotNull { index ->
            stickersArray.optJSONObject(index)?.let { sticker ->
                val url = sticker.optString("url").trim()
                if (url.isBlank()) {
                    null
                } else {
                    SavedSticker(
                        url = url,
                        type = sticker.optString("type", "image").ifBlank { "image" }
                    )
                }
            }
        }
    }

    private fun fail(response: Response?, t: Throwable, context: String): String {
        val code = response?.code
        return when (code) {
            404 -> "Noveo realtime server was not found while $context (HTTP 404)."
            401, 403 -> "Noveo rejected the realtime connection while $context (HTTP $code)."
            else -> "Socket failure while $context: ${t.message ?: t.javaClass.simpleName}"
        }
    }
}

