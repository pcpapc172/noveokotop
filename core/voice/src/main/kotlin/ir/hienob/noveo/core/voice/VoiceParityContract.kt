package ir.hienob.noveo.core.voice

data class VoiceTokenRequest(
    val chatId: Long,
    val callId: String? = null
)

enum class VoiceConnectionState {
    IDLE,
    RINGING,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    ENDED
}
