package ir.hienob.noveo.data

import org.json.JSONArray
import org.json.JSONObject

internal data class SyncSnapshot(
    val usersById: Map<String, UserSummary>,
    val onlineUserIds: Set<String>,
    val history: JSONObject
)

fun parseUser(item: JSONObject, onlineIds: Set<String> = emptySet()): UserSummary {
    val userId = item.optString("userId").sanitizeServerString().ifBlank { item.optString("id").sanitizeServerString() }
    return UserSummary(
        id = userId,
        username = item.optString("username").sanitizeServerString().ifBlank { item.optString("name").sanitizeServerString().ifBlank { "Unknown" } },
        avatarUrl = resolveAssetUrl(item, "avatarUrl", "avatar", "photo", "image")?.takeIf { it.isNotBlank() },
        handle = item.optString("handle").sanitizeServerString().takeIf { it.isNotBlank() },
        bio = item.optString("bio").sanitizeServerString(),
        isOnline = onlineIds.contains(userId) || item.optBoolean("online", false),
        isVerified = item.optBoolean("isVerified", false),
        profileSkin = parseProfileSkin(item.optJSONObject("profileSkin")),
        starsBalance = item.optDouble("starsBalance", 0.0),
        languageCode = item.optString("languageCode").sanitizeServerString().ifBlank { "en" },
        lastSeen = item.optLong("lastSeen", item.optLong("last_seen", 0L)).takeIf { it > 0 },
        joinedAt = item.optLong("joinedAt", item.optLong("createdAt", 0L)).takeIf { it > 0 }
    )
}

internal fun parseUsers(payload: JSONObject): Pair<Map<String, UserSummary>, Set<String>> {
    val onlineIds = mutableSetOf<String>()
    val onlineArray = payload.optJSONArray("online") ?: JSONArray()
    for (index in 0 until onlineArray.length()) {
        onlineArray.optString(index).sanitizeServerString().takeIf { it.isNotBlank() }?.let(onlineIds::add)
    }

    val users = mutableMapOf<String, UserSummary>()
    val usersArray = payload.optJSONArray("users") ?: JSONArray()
    for (index in 0 until usersArray.length()) {
        val item = usersArray.optJSONObject(index) ?: continue
        val user = parseUser(item, onlineIds)
        users[user.id] = user
    }
    return users to onlineIds
}

private fun parseProfileSkin(json: JSONObject?): ProfileSkin? {
    if (json == null) return null
    return ProfileSkin(
        mode = json.optString("mode").sanitizeServerString(),
        primaryColor = json.optString("primaryColor").sanitizeServerString(),
        secondaryColor = json.optString("secondaryColor").sanitizeServerString(),
        tertiaryColor = json.optString("tertiaryColor").sanitizeServerString(),
        gradientStops = json.optInt("gradientStops", 2)
    )
}

fun parseChat(item: JSONObject, usersById: Map<String, UserSummary>, selfUserId: String): ChatSummary {
    val chatId = item.optString("chatId").sanitizeServerString()
        .ifBlank { item.optString("chat_id").sanitizeServerString() }
        .ifBlank { item.optString("id").sanitizeServerString() }
    val memberIds = parseStringList(item.optJSONArray("members"))
    val messages = item.optJSONArray("messages") ?: JSONArray()
    
    // Calculate unread count based on seenBy array as per web logic
    val unreadCount = (0 until messages.length()).count { i ->
        val msg = messages.optJSONObject(i) ?: return@count false
        val senderId = msg.optString("senderId").ifBlank { msg.optString("sender") }
        if (senderId == selfUserId) return@count false
        val seenBy = parseStringList(msg.optJSONArray("seenBy"))
        !seenBy.contains(selfUserId)
    }

    val lastMsg = if (messages.length() > 0) messages.optJSONObject(messages.length() - 1) else null
    val preview = if (lastMsg != null) {
        parseMessageContent(lastMsg.opt("content")).previewText()
    } else {
        ""
    }
    val lastTimestamp = lastMsg?.optLong("timestamp", 0L) ?: 0L

    val chatType = item.optString("chatType", item.optString("type", "private")).sanitizeServerString()
    val ownerId = item.optString("ownerId").sanitizeServerString().takeIf { it.isNotBlank() }
    val pinnedMessageObj = item.optJSONObject("pinnedMessage")
    val pinnedMessage = if (pinnedMessageObj != null) parseChatMessage(pinnedMessageObj, chatId, usersById) else null
    
    val permissions = item.optJSONObject("permissions")
    val canChat = when (chatType) {
        "channel" -> ownerId == selfUserId
        "group" -> permissions?.optBoolean("canSendMessages", true) ?: true
        else -> true
    }

    return ChatSummary(
        id = chatId,
        chatType = chatType,
        title = resolveChatTitle(item, usersById, memberIds, selfUserId),
        avatarUrl = resolveChatAvatar(item, usersById, memberIds, selfUserId),
        lastMessagePreview = preview,
        lastMessageTimestamp = lastTimestamp,
        unreadCount = unreadCount,
        memberIds = memberIds,
        handle = item.optString("handle").sanitizeServerString().takeIf { it.isNotBlank() },
        isVerified = item.optBoolean("isVerified", false),
        ownerId = ownerId,
        canChat = canChat,
        hasMoreHistory = item.optBoolean("hasMoreHistory", false),
        pinnedMessage = pinnedMessage
    )
}

internal fun parseChats(payload: JSONObject, usersById: Map<String, UserSummary>, selfUserId: String): List<ChatSummary> {
    val chatsArray = payload.optJSONArray("chats") ?: JSONArray()
    return buildList {
        for (index in 0 until chatsArray.length()) {
            val item = chatsArray.optJSONObject(index) ?: continue
            add(parseChat(item, usersById, selfUserId))
        }
    }
}

internal fun parseMessagesForChat(payload: JSONObject, usersById: Map<String, UserSummary>, chatId: String): List<ChatMessage> {
    val chatsArray = payload.optJSONArray("chats") ?: JSONArray()
    for (index in 0 until chatsArray.length()) {
        val item = chatsArray.optJSONObject(index) ?: continue
        val itemChatId = item.optString("chatId").sanitizeServerString().ifBlank { item.optString("id").sanitizeServerString() }
        if (itemChatId != chatId) continue
        val messagesArray = item.optJSONArray("messages") ?: JSONArray()
        return buildList {
            for (messageIndex in 0 until messagesArray.length()) {
                val message = messagesArray.optJSONObject(messageIndex) ?: continue
                add(parseChatMessage(message, itemChatId, usersById))
            }
        }
    }
    return emptyList()
}

internal fun parseMessagesByChat(payload: JSONObject, usersById: Map<String, UserSummary>): Map<String, List<ChatMessage>> {
    val chatsArray = payload.optJSONArray("chats") ?: JSONArray()
    return buildMap {
        for (index in 0 until chatsArray.length()) {
            val item = chatsArray.optJSONObject(index) ?: continue
            val chatId = item.optString("chatId").sanitizeServerString()
                .ifBlank { item.optString("chat_id").sanitizeServerString() }
                .ifBlank { item.optString("id").sanitizeServerString() }
            if (chatId.isBlank()) continue
            val messagesArray = item.optJSONArray("messages") ?: continue
            val messages = buildList {
                for (messageIndex in 0 until messagesArray.length()) {
                    val message = messagesArray.optJSONObject(messageIndex) ?: continue
                    add(parseChatMessage(message, chatId, usersById))
                }
            }
            put(chatId, messages)
        }
    }
}

internal fun parseRealtimeMessage(payload: JSONObject, usersById: Map<String, UserSummary>): ChatMessage {
    val message = payload.unwrapRealtimePayload()
    val chatId = message.optString("chatId").sanitizeServerString()
        .ifBlank { message.optString("chat_id").sanitizeServerString() }
        .ifBlank { payload.optString("chatId").sanitizeServerString() }
        .ifBlank { payload.optString("chat_id").sanitizeServerString() }
    return parseChatMessage(message, chatId, usersById)
}

internal fun parseChatMessageList(array: JSONArray?, chatId: String, usersById: Map<String, UserSummary>): List<ChatMessage> {
    if (array == null) return emptyList()
    return buildList {
        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue
            add(parseChatMessage(item, chatId, usersById))
        }
    }
}

internal fun parseReactions(json: JSONObject, key: String = "reactions"): Map<String, List<String>> {
    val reactions = mutableMapOf<String, List<String>>()
    val reactionsObj = json.optJSONObject(key)
    if (reactionsObj != null) {
        val keys = reactionsObj.keys()
        while (keys.hasNext()) {
            val emoji = keys.next()
            reactions[emoji] = parseStringList(reactionsObj.optJSONArray(emoji))
        }
    } else {
        val reactionsArray = json.optJSONArray(key)
        if (reactionsArray != null) {
            for (i in 0 until reactionsArray.length()) {
                val item = reactionsArray.optJSONObject(i) ?: continue
                val emoji = item.optString("emoji").takeIf { it.isNotBlank() } ?: continue
                val userIds = parseStringList(item.optJSONArray("userIds"))
                    .ifEmpty { parseStringList(item.optJSONArray("users")) }
                reactions[emoji] = userIds
            }
        }
    }
    return reactions
}

internal fun parseChatMessage(message: JSONObject, chatId: String, usersById: Map<String, UserSummary>): ChatMessage {
    val messageId = message.optString("messageId").sanitizeServerString().ifBlank { message.optString("id").sanitizeServerString() }
    val senderId = message.optString("senderId").sanitizeServerString().ifBlank { message.optString("sender").sanitizeServerString() }
    val timestamp = message.optLong("timestamp", message.optLong("createdAt", 0L))
    val seenByArray = message.optJSONArray("seenBy") ?: JSONArray()
    val seenBy = (0 until seenByArray.length()).map { seenByArray.optString(it) }

    val reactions = parseReactions(message)

    return ChatMessage(
        id = messageId,
        chatId = message.optString("chatId").sanitizeServerString()
            .ifBlank { message.optString("chat_id").sanitizeServerString() }
            .ifBlank { chatId },
        senderId = senderId,
        senderName = resolveSenderName(senderId, message, usersById),
        chatType = message.optString("chatType", "private").sanitizeServerString(),
        content = parseMessageContent(message.opt("content")),
        timestamp = timestamp,
        seenBy = seenBy,
        reactions = reactions,
        isPinned = message.optBoolean("isPinned", message.optBoolean("pinned", false)),
        pending = message.optBoolean("pending", false),
        clientTempId = message.optString("clientTempId").takeIf { it.isNotBlank() },
        replyToId = message.optString("replyToId").sanitizeServerString().takeIf { it.isNotBlank() },
        editedAt = message.optLong("editedAt").takeIf { it > 0 }
    )
}

internal fun parseMessageContent(raw: Any?): MessageContent {
    val payload = when (raw) {
        is JSONObject -> raw
        is String -> {
            val text = raw.sanitizeServerString()
            if (text.isBlank()) return MessageContent()
            if (text.startsWith("{") || text.startsWith("[")) {
                runCatching { JSONObject(text) }.getOrNull() ?: return MessageContent(text = text)
            } else {
                return MessageContent(text = text)
            }
        }
        else -> return MessageContent(text = raw?.toString().sanitizeServerString())
    }

    val fileObject = payload.optJSONObject("file")
        ?: payload.optJSONObject("attachment")
        ?: payload.optJSONObject("document")
        ?: payload.optJSONObject("media")
    
    val file = fileObject?.let {
        MessageFileAttachment(
            url = resolveAssetUrl(it, "url", "src", "path", "downloadUrl", "fileUrl").orEmpty(),
            name = it.optString("name").sanitizeServerString().ifBlank {
                it.optString("filename").sanitizeServerString().ifBlank { it.optString("title").sanitizeServerString() }
            },
            type = it.optString("type").sanitizeServerString().ifBlank {
                it.optString("mimeType").sanitizeServerString().ifBlank { it.optString("contentType").sanitizeServerString() }
            }
        )
    }
    
    val forwardedInfoObj = payload.optJSONObject("forwardedInfo")
    val forwardedInfo = forwardedInfoObj?.let {
        ForwardedInfo(
            from = it.optString("from").sanitizeServerString().ifBlank { "Unknown" },
            originalTs = it.optLong("originalTs", 0L)
        )
    }

    val inlineKeyboard = mutableListOf<List<InlineKeyboardButton>>()
    payload.optJSONArray("inlineKeyboard")?.let { rows ->
        for (i in 0 until rows.length()) {
            val rowArray = rows.optJSONArray(i) ?: continue
            val row = mutableListOf<InlineKeyboardButton>()
            for (j in 0 until rowArray.length()) {
                val btn = rowArray.optJSONObject(j) ?: continue
                row.add(
                    InlineKeyboardButton(
                        text = btn.optString("text"),
                        callbackData = btn.optString("callbackData").takeIf { it.isNotBlank() },
                        url = btn.optString("url").takeIf { it.isNotBlank() }
                    )
                )
            }
            if (row.isNotEmpty()) inlineKeyboard.add(row)
        }
    }
    
    return MessageContent(
        text = payload.optString("text").sanitizeServerString().takeIf { it.isNotBlank() },
        file = file,
        poll = payload.optJSONObject("poll")?.toString(),
        theme = payload.optJSONObject("theme")?.toString(),
        callLog = payload.optJSONObject("callLog")?.toString() ?: payload.optString("callLog").takeIf { it.isNotBlank() },
        forwardedInfo = forwardedInfo,
        replyToId = payload.optString("replyToId").sanitizeServerString().takeIf { it.isNotBlank() },
        inlineKeyboard = inlineKeyboard
    )
}

private fun resolveChatTitle(chat: JSONObject, usersById: Map<String, UserSummary>, memberIds: List<String>, selfUserId: String): String {
    val explicit = chat.optString("chatName").sanitizeServerString()
    if (explicit.isNotBlank()) return explicit
    val chatType = chat.optString("chatType", chat.optString("type", "private")).sanitizeServerString()
    if (chatType == "private") {
        if (memberIds.size == 1 && memberIds[0] == selfUserId) {
            return "Saved Messages"
        }
        return memberIds.firstOrNull { it != selfUserId }
            ?.let(usersById::get)
            ?.username
            ?.sanitizeServerString()
            ?.ifBlank { "Direct Message" }
            ?: "Direct Message"
    }
    return "Chat"
}

private fun resolveChatAvatar(chat: JSONObject, usersById: Map<String, UserSummary>, memberIds: List<String>, selfUserId: String): String? {
    val explicit = resolveAssetUrl(chat, "avatarUrl", "avatar", "photo", "image")
    if (!explicit.isNullOrBlank()) return explicit
    val chatType = chat.optString("chatType", chat.optString("type", "private")).sanitizeServerString()
    if (chatType == "private") {
        if (memberIds.size == 1 && memberIds[0] == selfUserId) {
            return "saved_messages"
        }
        return memberIds.firstOrNull { it != selfUserId }
            ?.let(usersById::get)
            ?.avatarUrl
    }
    return null
}

private fun resolveSenderName(senderId: String, payload: JSONObject, usersById: Map<String, UserSummary>): String {
    if (senderId == "system") return "System"
    if (senderId == "anonymous") return "Anonymous"
    return usersById[senderId]?.username?.sanitizeServerString()?.takeIf { it.isNotBlank() }
        ?: payload.optString("senderName").sanitizeServerString().takeIf { it.isNotBlank() }
        ?: payload.optString("sender").sanitizeServerString().takeIf { it.isNotBlank() }
        ?: "Unknown"
}

private fun resolveAssetUrl(source: JSONObject, vararg keys: String): String? {
    for (key in keys) {
        val direct = source.optString(key).sanitizeServerString()
        if (direct.isNotBlank()) return direct
        val nested = source.optJSONObject(key) ?: continue
        val candidates = listOf(
            nested.optString("url").sanitizeServerString(),
            nested.optString("src").sanitizeServerString(),
            nested.optString("path").sanitizeServerString(),
            nested.optString("downloadUrl").sanitizeServerString(),
            nested.optString("fileUrl").sanitizeServerString(),
            nested.optString("thumbUrl").sanitizeServerString(),
            nested.optString("thumbnailUrl").sanitizeServerString()
        )
        val resolved = candidates.firstOrNull { it.isNotBlank() }
        if (!resolved.isNullOrBlank()) return resolved
    }
    return null
}

private fun parseStringList(array: JSONArray?): List<String> {
    if (array == null) return emptyList()
    return buildList {
        for (index in 0 until array.length()) {
            array.optString(index).sanitizeServerString().takeIf { it.isNotBlank() }?.let(::add)
        }
    }
}

internal fun JSONObject.unwrapRealtimePayload(): JSONObject {
    return optJSONObject("message")
        ?: optJSONObject("payload")
        ?: optJSONObject("data")
        ?: this
}

private fun String?.sanitizeServerString(): String {
    val value = this?.trim().orEmpty()
    val lower = value.lowercase()
    if (lower == "null" || lower == "undefined" || lower == "none") return ""
    return value
}
