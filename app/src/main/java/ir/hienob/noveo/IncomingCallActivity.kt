package ir.hienob.noveo

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ir.hienob.noveo.app.AppViewModel
import ir.hienob.noveo.ui.IncomingCallOverlay
import ir.hienob.noveo.ui.getStrings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import ir.hienob.noveo.background.NoveoNotificationService
import ir.hienob.noveo.data.SocketEvent
import android.app.NotificationManager

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class IncomingCallActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    private fun cancelCallNotification() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(1001) // CALL_NOTIFICATION_ID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Aggressive lock screen bypass
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as android.app.KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        }
        
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )

        handleIntent(intent)

        setContent {
            val state by viewModel.uiState.collectAsState()
            val strings = getStrings(state.languageCode)
            
            val chatId = intent.getStringExtra("chatId") ?: ""
            val callId = intent.getStringExtra("callId") ?: ""
            val callerId = intent.getStringExtra("callerId") ?: ""

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    IncomingCallOverlay(
                        call = SocketEvent.IncomingCall(chatId, callerId, callId),
                        strings = strings,
                        caller = state.usersById[callerId],
                        onAccept = {
                            cancelCallNotification()
                            acceptAndReturn()
                        },
                        onDecline = {
                            cancelCallNotification()
                            viewModel.declineCall()
                            finish()
                        }
                    )
                }
            }
        }
        
        // Listen for call end to finish activity
        lifecycleScope.launch {
            val chatId = intent.getStringExtra("chatId") ?: ""
            NoveoNotificationService.socketEvents.collect { event ->
                if (event is SocketEvent.VoiceCallEnded && event.chatId == chatId) {
                    finish()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val chatId = intent?.getStringExtra("chatId") ?: ""
        val callId = intent?.getStringExtra("callId") ?: ""
        val action = intent?.getStringExtra("action")

        if (chatId.isEmpty() || callId.isEmpty()) return

        if (action == "accept") {
            acceptAndReturn()
        }
    }

    private fun acceptAndReturn() {
        cancelCallNotification()
        val chatId = intent.getStringExtra("chatId") ?: ""
        val callId = intent.getStringExtra("callId") ?: ""
        if (chatId.isNotEmpty() && callId.isNotEmpty()) {
            viewModel.acceptCall(chatId, callId)
        }
        
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        startActivity(mainIntent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Ensure wake lock is released or activity is cleaned up
    }
}
