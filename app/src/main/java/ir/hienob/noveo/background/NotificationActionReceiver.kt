package ir.hienob.noveo.background

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import ir.hienob.noveo.data.NoveoApi
import ir.hienob.noveo.data.SessionStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject

class NotificationActionReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val api = NoveoApi()

    override fun onReceive(context: Context, intent: Intent) {
        val chatId = intent.getStringExtra("chatId") ?: return
        val messageId = intent.getStringExtra("messageId")
        val sessionStore = SessionStore(context)

        when (intent.action) {
            "ir.hienob.noveo.ACTION_REPLY" -> {
                val remoteInput = RemoteInput.getResultsFromIntent(intent)
                val replyText = remoteInput?.getCharSequence("key_text_reply")?.toString()
                if (!replyText.isNullOrBlank()) {
                    val contentObj = JSONObject().put("text", replyText)
                    if (messageId != null) {
                        contentObj.put("replyToId", messageId)
                    }
                    val payload = JSONObject()
                        .put("type", "message")
                        .put("chatId", chatId)
                        .put("content", contentObj.toString())
                        .put("replyToId", messageId)
                    NoveoNotificationService.send(payload)
                    
                    // Clear notification
                    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    nm.cancel(chatId.hashCode())
                }
            }
            "ir.hienob.noveo.ACTION_SEEN" -> {
                if (messageId != null) {
                    val payload = JSONObject()
                        .put("type", "message_seen")
                        .put("chatId", chatId)
                        .put("messageId", messageId)
                    NoveoNotificationService.send(payload)
                    
                    // Clear notification
                    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    nm.cancel(chatId.hashCode())
                }
            }
            "ir.hienob.noveo.ACTION_DECLINE_CALL" -> {
                val callId = intent.getStringExtra("callId")
                val payload = JSONObject()
                    .put("type", "voice_leave")
                    .put("chatId", chatId)
                    .put("callId", callId)
                    .put("reason", "declined")
                NoveoNotificationService.send(payload)
                
                // Clear notification
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.cancel(1001) // CALL_NOTIFICATION_ID
            }
        }
    }
}
