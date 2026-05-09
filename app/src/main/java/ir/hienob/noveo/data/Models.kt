package ir.hienob.noveo.data

data class Session(
    val userId: String,
    val token: String,
    val sessionId: String = "",
    val expiresAt: Long = 0L
)

data class ProfileSkin(
    val mode: String = "",
    val primaryColor: String = "",
    val secondaryColor: String = "",
    val tertiaryColor: String = "",
    val gradientStops: Int = 2
)

data class Transaction(
    val id: String,
    val amountTenths: Int,
    val balanceAfterTenths: Int,
    val type: String,
    val description: String,
    val createdAt: Long,
    val relatedUserId: String? = null
)

data class Wallet(
    val balanceTenths: Int,
    val balanceLabel: String,
    val transactions: List<Transaction> = emptyList()
)

data class UserSummary(
    val id: String,
    val username: String,
    val avatarUrl: String? = null,
    val handle: String? = null,
    val bio: String = "",
    val isOnline: Boolean = false,
    val isVerified: Boolean = false,
    val profileSkin: ProfileSkin? = null,
    val starsBalance: Double = 0.0,
    val languageCode: String = "en",
    val lastSeen: Long? = null,
    val joinedAt: Long? = null
)

data class MessageFileAttachment(
    val url: String = "",
    val name: String = "",
    val type: String = "",
    val size: Long = 0L
) {
    private fun lowerName(): String = name.trim().lowercase()
    private fun lowerUrl(): String = url.trim().lowercase()
    private fun lowerType(): String = type.trim().lowercase()

    fun isImage(): Boolean =
        type.startsWith("image/", true) ||
            name.endsWith(".jpg", true) ||
            name.endsWith(".jpeg", true) ||
            name.endsWith(".png", true) ||
            name.endsWith(".gif", true) ||
            name.endsWith(".webp", true) ||
            url.contains(".jpg", true) ||
            url.contains(".jpeg", true) ||
            url.contains(".png", true) ||
            url.contains(".webp", true) ||
            url.contains(".gif", true)

    fun isVideo(): Boolean =
        type.startsWith("video/", true) ||
            name.endsWith(".mp4", true) ||
            name.endsWith(".webm", true) ||
            name.endsWith(".mov", true) ||
            name.endsWith(".ogg", true) ||
            url.contains(".mp4", true) ||
            url.contains(".webm", true) ||
            url.contains(".mov", true) ||
            url.contains(".ogg", true)

    fun isAudio(): Boolean =
        type.startsWith("audio/") ||
            name.endsWith(".mp3", true) ||
            name.endsWith(".m4a", true) ||
            name.endsWith(".wav", true) ||
            name.endsWith(".ogg", true)

    fun isTgsSticker(): Boolean {
        val n = lowerName()
        val u = lowerUrl()
        val t = lowerType()
        val namedSticker = n == "sticker" ||
            n.startsWith("sticker.") ||
            n.contains("_sticker") ||
            n.contains("-sticker") ||
            n.contains(" sticker")
        return t.contains("tgsticker") ||
            t == "application/x-tgs" ||
            t == "application/x-tgsticker" ||
            t.contains("lottie") ||
            n.endsWith(".tgs") ||
            u.endsWith(".tgs") ||
            u.contains(".tgs?") ||
            (namedSticker && (t.contains("gzip") || t.contains("tgz") || t == "application/octet-stream" || t == "binary/octet-stream"))
    }

    fun isSticker(): Boolean {
        val n = lowerName()
        val u = lowerUrl()
        val namedSticker = n == "sticker" ||
            n.startsWith("sticker.") ||
            n.contains("_sticker") ||
            n.contains("-sticker") ||
            n.contains(" sticker") ||
            u.contains("/stickers/") ||
            u.contains("/sticker") ||
            u.contains("sticker.")
        val supportedStickerExtension = n.endsWith(".gif") ||
            n.endsWith(".png") ||
            n.endsWith(".jpg") ||
            n.endsWith(".jpeg") ||
            n.endsWith(".webp") ||
            n.endsWith(".tgs") ||
            u.endsWith(".gif") ||
            u.endsWith(".png") ||
            u.endsWith(".jpg") ||
            u.endsWith(".jpeg") ||
            u.endsWith(".webp") ||
            u.endsWith(".tgs") ||
            u.contains(".tgs?")
        return isTgsSticker() || (namedSticker && (isImage() || supportedStickerExtension || type.isBlank() || lowerType() == "application/octet-stream"))
    }

    fun downloadKey(): String {
        val source = "${url.trim().lowercase()}|${name.trim().lowercase()}|${type.trim().lowercase()}"
        return source.hashCode().toUInt().toString(16)
    }
}

data class SavedSticker(
    val url: String,
    val type: String = "image"
)

data class ForwardedInfo(
    val from: String,
    val originalTs: Long
)

data class InlineKeyboardButton(
    val text: String,
    val callbackData: String? = null,
    val url: String? = null
)

data class MessageContent(
    val text: String? = null,
    val file: MessageFileAttachment? = null,
    val poll: String? = null, // Simplified for now
    val theme: String? = null,
    val callLog: String? = null,
    val forwardedInfo: ForwardedInfo? = null,
    val replyToId: String? = null,
    val inlineKeyboard: List<List<InlineKeyboardButton>> = emptyList()
) {
    fun previewText(): String {
        return when {
            !callLog.isNullOrBlank() -> {
                try {
                    val log = org.json.JSONObject(callLog)
                    val type = log.optString("type").ifBlank { log.optString("status") }
                    when (type) {
                        "outgoing" -> "Outgoing Call"
                        "incoming" -> "Incoming Call"
                        "missed" -> "Missed Call"
                        "cancelled", "canceled" -> "Cancelled Call"
                        "declined", "rejected" -> "Declined Call"
                        else -> "Voice Call"
                    }
                } catch (e: Exception) {
                    "Voice Call"
                }
            }
            !text.isNullOrBlank() -> text
            file?.isSticker() == true -> "Sticker"
            file != null -> if (file.isImage()) "Photo" else if (file.isVideo()) "Video" else "File"
            !poll.isNullOrBlank() -> "Poll"
            !theme.isNullOrBlank() -> "Theme"
            forwardedInfo != null -> "Forwarded message"
            else -> ""
        }
    }
}

data class ChatSummary(
    val id: String,
    val chatType: String,
    val title: String,
    val avatarUrl: String? = null,
    val lastMessagePreview: String = "",
    val lastMessageTimestamp: Long = 0L,
    val unreadCount: Int = 0,
    val memberIds: List<String> = emptyList(),
    val handle: String? = null,
    val isVerified: Boolean = false,
    val ownerId: String? = null,
    val canChat: Boolean = true,
    val hasMoreHistory: Boolean = false,
    val pinnedMessage: ChatMessage? = null
)

data class ChatMessage(
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String = "User",
    val chatType: String = "private",
    val content: MessageContent,
    val timestamp: Long = 0L,
    val seenBy: List<String> = emptyList(),
    val reactions: Map<String, List<String>> = emptyMap(), // emoji -> list of userIds
    val isPinned: Boolean = false,
    val pending: Boolean = false,
    val clientTempId: String? = null,
    val replyToId: String? = null,
    val editedAt: Long? = null
)

data class HomeData(
    val usersById: Map<String, UserSummary>,
    val onlineUserIds: Set<String>,
    val chats: List<ChatSummary>
)

data class CachedHomeState(
    val usersById: Map<String, UserSummary>,
    val onlineUserIds: Set<String>,
    val chats: List<ChatSummary>,
    val messagesByChat: Map<String, List<ChatMessage>>
)

data class MessageLoadResult(
    val usersById: Map<String, UserSummary>,
    val messages: List<ChatMessage>
)

data class NotificationSettings(
    val enabled: Boolean = true,
    val groups: Boolean = true,
    val channels: Boolean = true,
    val dms: Boolean = true
)

inline fun <R : Comparable<R>> BooleanArray.sortedBy(crossinline selector: (Boolean) -> R?): List<Boolean> {
    return this.toTypedArray().sortedWith(compareBy(selector))
}

inline fun <R : Comparable<R>> CharArray.sortedBy(crossinline selector: (Char) -> R?): List<Char> {
    return this.toTypedArray().sortedWith(compareBy(selector))
}

inline fun <T, R : Comparable<R>> Iterable<T>.sortedBy(crossinline selector: (T) -> R?): List<T> {
    return this.sortedWith(compareBy(selector))
}

inline fun <T, R : Comparable<R>> Sequence<T>.sortedBy(crossinline selector: (T) -> R?): Sequence<T> {
    return this.sortedWith(compareBy(selector))
}
