package ir.hienob.noveo.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class SessionStore(context: Context) {
    private val prefs = context.getSharedPreferences("noveo_session", Context.MODE_PRIVATE)

    fun read(): Session? {
        val userId = prefs.getString("user_id", null)
        val token = prefs.getString("token", null)
        if (userId.isNullOrBlank() || token.isNullOrBlank()) return null
        return Session(
            userId = userId,
            token = token,
            sessionId = prefs.getString("session_id", "") ?: "",
            expiresAt = prefs.getLong("expires_at", 0L)
        )
    }

    fun write(session: Session) {
        prefs.edit()
            .putString("user_id", session.userId)
            .putString("token", session.token)
            .putString("session_id", session.sessionId)
            .putLong("expires_at", session.expiresAt)
            .apply()
    }

    fun readNotificationSettings(): NotificationSettings {
        return NotificationSettings(
            enabled = prefs.getBoolean("notify_enabled", true),
            groups = prefs.getBoolean("notify_groups", true),
            channels = prefs.getBoolean("notify_channels", true),
            dms = prefs.getBoolean("notify_dms", true)
        )
    }

    fun writeNotificationSettings(settings: NotificationSettings) {
        prefs.edit()
            .putBoolean("notify_enabled", settings.enabled)
            .putBoolean("notify_groups", settings.groups)
            .putBoolean("notify_channels", settings.channels)
            .putBoolean("notify_dms", settings.dms)
            .apply()
    }

    fun readBetaUpdatesEnabled(): Boolean = prefs.getBoolean("beta_updates_enabled", false)

    fun writeBetaUpdatesEnabled(enabled: Boolean) {
        prefs.edit()
            .putBoolean("beta_updates_enabled", enabled)
            .apply()
    }

    private fun normalizeDoubleTapReaction(reaction: String?): String {
        val value = reaction?.trim().orEmpty()
        return when (value) {
            "", "❤" -> "❤️"
            else -> value
        }
    }

    fun readDoubleTapReaction(): String =
        normalizeDoubleTapReaction(prefs.getString("double_tap_reaction", "❤️"))

    fun writeDoubleTapReaction(reaction: String) {
        prefs.edit()
            .putString("double_tap_reaction", normalizeDoubleTapReaction(reaction))
            .apply()
    }

    fun readAnimatedEmojiTgsEnabled(): Boolean = prefs.getBoolean("animated_emoji_tgs_enabled", true)

    fun writeAnimatedEmojiTgsEnabled(enabled: Boolean) {
        prefs.edit()
            .putBoolean("animated_emoji_tgs_enabled", enabled)
            .apply()
    }

    fun readLanguageCode(): String = prefs.getString("language_code", "en") ?: "en"

    fun writeLanguageCode(code: String) {
        prefs.edit()
            .putString("language_code", code)
            .apply()
    }

    fun readCachedHomeState(): CachedHomeState? {
        val raw = prefs.getString("cached_home_state", null) ?: return null
        return runCatching {
            val root = JSONObject(raw)
            val users = root.optJSONArray("users") ?: JSONArray()
            val chats = root.optJSONArray("chats") ?: JSONArray()
            val online = root.optJSONArray("onlineUserIds") ?: JSONArray()
            val messagesByChat = root.optJSONObject("messagesByChat") ?: JSONObject()

            CachedHomeState(
                usersById = buildMap {
                    for (index in 0 until users.length()) {
                        val item = users.optJSONObject(index) ?: continue
                        val user = item.toUserSummary()
                        put(user.id, user)
                    }
                },
                onlineUserIds = buildSet {
                    for (index in 0 until online.length()) {
                        online.optString(index)?.takeIf { it.isNotBlank() }?.let(::add)
                    }
                },
                chats = buildList {
                    for (index in 0 until chats.length()) {
                        val item = chats.optJSONObject(index) ?: continue
                        add(item.toChatSummary())
                    }
                },
                messagesByChat = buildMap {
                    val keys = messagesByChat.keys()
                    while (keys.hasNext()) {
                        val chatId = keys.next()
                        val array = messagesByChat.optJSONArray(chatId) ?: JSONArray()
                        put(
                            chatId,
                            buildList {
                                for (index in 0 until array.length()) {
                                    val item = array.optJSONObject(index) ?: continue
                                    add(item.toChatMessage())
                                }
                            }
                        )
                    }
                }
            )
        }.getOrNull()
    }

    fun writeCachedHomeState(state: CachedHomeState) {
        val root = JSONObject()
            .put(
                "users",
                JSONArray().apply {
                    state.usersById.values.forEach { put(it.toJson()) }
                }
            )
            .put(
                "onlineUserIds",
                JSONArray().apply {
                    state.onlineUserIds.forEach(::put)
                }
            )
            .put(
                "chats",
                JSONArray().apply {
                    state.chats.forEach { put(it.toJson()) }
                }
            )
            .put(
                "messagesByChat",
                JSONObject().apply {
                    state.messagesByChat.forEach { (chatId, messages) ->
                        put(
                            chatId,
                            JSONArray().apply {
                                messages.forEach { put(it.toJson()) }
                            }
                        )
                    }
                }
            )

        prefs.edit()
            .putString("cached_home_state", root.toString())
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}

private fun UserSummary.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("username", username)
    .put("avatarUrl", avatarUrl)
    .put("handle", handle)
    .put("bio", bio)
    .put("isOnline", isOnline)
    .put("isVerified", isVerified)
    .put("starsBalance", starsBalance)
    .put("languageCode", languageCode)
    .put("lastSeen", lastSeen)
    .put("profileSkin", profileSkin?.toJson())

private fun ProfileSkin.toJson(): JSONObject = JSONObject()
    .put("mode", mode)
    .put("primaryColor", primaryColor)
    .put("secondaryColor", secondaryColor)
    .put("tertiaryColor", tertiaryColor)
    .put("gradientStops", gradientStops)

private fun ChatSummary.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("chatType", chatType)
    .put("title", title)
    .put("avatarUrl", avatarUrl)
    .put("lastMessagePreview", lastMessagePreview)
    .put("unreadCount", unreadCount)
    .put("memberIds", JSONArray().apply { memberIds.forEach(::put) })
    .put("handle", handle)
    .put("isVerified", isVerified)
    .put("ownerId", ownerId)
    .put("canChat", canChat)
    .put("hasMoreHistory", hasMoreHistory)

private fun ChatMessage.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("chatId", chatId)
    .put("senderId", senderId)
    .put("senderName", senderName)
    .put("chatType", chatType)
    .put("content", content.toJson())
    .put("timestamp", timestamp)
    .put("seenBy", JSONArray().apply { seenBy.forEach(::put) })
    .put("pending", pending)
    .put("clientTempId", clientTempId)
    .put("replyToId", replyToId)
    .put("editedAt", editedAt)

private fun MessageContent.toJson(): JSONObject = JSONObject()
    .put("text", text)
    .put("poll", poll)
    .put("theme", theme)
    .put("callLog", callLog)
    .put("forwardedInfo", forwardedInfo?.toJson())
    .put("replyToId", replyToId)
    .put("file", file?.toJson())

private fun ForwardedInfo.toJson(): JSONObject = JSONObject()
    .put("from", from)
    .put("originalTs", originalTs)

private fun MessageFileAttachment.toJson(): JSONObject = JSONObject()
    .put("url", url)
    .put("name", name)
    .put("type", type)

private fun JSONObject.toUserSummary(): UserSummary = UserSummary(
    id = optString("id"),
    username = optString("username"),
    avatarUrl = optString("avatarUrl").takeIf { it.isNotBlank() },
    handle = optString("handle").takeIf { it.isNotBlank() },
    bio = optString("bio"),
    isOnline = optBoolean("isOnline", false),
    isVerified = optBoolean("isVerified", false),
    profileSkin = optJSONObject("profileSkin")?.toProfileSkin(),
    starsBalance = optDouble("starsBalance", 0.0),
    languageCode = optString("languageCode", "en"),
    lastSeen = optLong("lastSeen").takeIf { it > 0L }
)

private fun JSONObject.toProfileSkin(): ProfileSkin = ProfileSkin(
    mode = optString("mode"),
    primaryColor = optString("primaryColor"),
    secondaryColor = optString("secondaryColor"),
    tertiaryColor = optString("tertiaryColor"),
    gradientStops = optInt("gradientStops", 2)
)

private fun JSONObject.toChatSummary(): ChatSummary = ChatSummary(
    id = optString("id"),
    chatType = optString("chatType"),
    title = optString("title"),
    avatarUrl = optString("avatarUrl").takeIf { it.isNotBlank() },
    lastMessagePreview = optString("lastMessagePreview"),
    unreadCount = optInt("unreadCount", 0),
    memberIds = buildList {
        val array = optJSONArray("memberIds") ?: JSONArray()
        for (index in 0 until array.length()) {
            array.optString(index)?.takeIf { it.isNotBlank() }?.let(::add)
        }
    },
    handle = optString("handle").takeIf { it.isNotBlank() },
    isVerified = optBoolean("isVerified", false),
    ownerId = optString("ownerId").takeIf { it.isNotBlank() },
    canChat = optBoolean("canChat", true),
    hasMoreHistory = optBoolean("hasMoreHistory", false)
)

private fun JSONObject.toChatMessage(): ChatMessage = ChatMessage(
    id = optString("id"),
    chatId = optString("chatId"),
    senderId = optString("senderId"),
    senderName = optString("senderName"),
    chatType = optString("chatType", "private"),
    content = (optJSONObject("content") ?: JSONObject()).toMessageContent(),
    timestamp = optLong("timestamp", 0L),
    seenBy = buildList {
        val array = optJSONArray("seenBy") ?: JSONArray()
        for (index in 0 until array.length()) {
            array.optString(index)?.takeIf { it.isNotBlank() }?.let(::add)
        }
    },
    pending = optBoolean("pending", false),
    clientTempId = optString("clientTempId").takeIf { it.isNotBlank() },
    replyToId = optString("replyToId").takeIf { it.isNotBlank() },
    editedAt = optLong("editedAt").takeIf { it > 0L }
)

private fun JSONObject.toMessageContent(): MessageContent = MessageContent(
    text = optString("text").takeIf { it.isNotBlank() },
    file = optJSONObject("file")?.toMessageFileAttachment(),
    poll = optString("poll").takeIf { it.isNotBlank() },
    theme = optString("theme").takeIf { it.isNotBlank() },
    callLog = optString("callLog").takeIf { it.isNotBlank() },
    forwardedInfo = optJSONObject("forwardedInfo")?.let {
        ForwardedInfo(
            from = it.optString("from"),
            originalTs = it.optLong("originalTs", 0L)
        )
    },
    replyToId = optString("replyToId").takeIf { it.isNotBlank() }
)

private fun JSONObject.toMessageFileAttachment(): MessageFileAttachment = MessageFileAttachment(
    url = optString("url"),
    name = optString("name"),
    type = optString("type")
)
