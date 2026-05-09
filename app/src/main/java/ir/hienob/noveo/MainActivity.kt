package ir.hienob.noveo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import android.content.Intent
import ir.hienob.noveo.app.AppViewModel
import ir.hienob.noveo.ui.NoveoRoot

import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.os.Build
import android.content.Context
import android.provider.Settings

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val currentSettings = viewModel.uiState.value.notificationSettings
            viewModel.updateNotificationSettings(currentSettings.copy(enabled = true))
        }
    }

    private val micPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleMute()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request notification permission on first launch
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val sharedPrefs = getSharedPreferences("noveo_prefs", Context.MODE_PRIVATE)
            val asked = sharedPrefs.getBoolean("notif_permission_asked", false)
            if (!asked) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                sharedPrefs.edit().putBoolean("notif_permission_asked", true).apply()
            }
        }

        setContent {
            val state by viewModel.uiState.collectAsState()
            NoveoRoot(
                state = state,
                onDismissOnboarding = viewModel::dismissOnboarding,
                onAuthMode = viewModel::setAuthMode,
                onStartRegisterCaptcha = viewModel::startRegisterCaptcha,
                onAuthSubmit = { h, p -> viewModel.authenticate(h, p) },
                onOpenChat = viewModel::openChat,
                onStartDirectChat = viewModel::openDirectChat,
                onStartCreateChat = viewModel::startCreateChatCaptcha,
                onSearchPublic = viewModel::searchPublicDirectory,
                onBackToChats = viewModel::backToChatList,
                onSend = viewModel::sendMessage,
                onTyping = viewModel::sendTyping,
                onLogout = viewModel::logout,
                onAttachFile = viewModel::attachFile,
                onRemoveAttachment = viewModel::removeAttachment,
                onCaptchaTokenReceived = viewModel::onCaptchaTokenReceived,
                onCaptchaDismiss = viewModel::dismissCaptcha,
                onUpdateProfile = { u, b -> viewModel.updateProfile(u, b) },
                onLoadOlder = viewModel::loadOlderMessages,
                onReply = viewModel::setReplyingTo,
                onEditMessage = viewModel::setEditingMessage,
                onForwardMessage = viewModel::setForwardingMessage,
                onForwardConfirm = viewModel::forwardMessage,
                onToggleReaction = viewModel::toggleReaction,
                onDeleteMessage = viewModel::deleteMessage,
                onPinMessage = viewModel::pinMessage,
                onChangePassword = { o, n -> viewModel.changePassword(o, n) },
                onDeleteAccount = { p -> viewModel.deleteAccount(p) },
                onSetLanguage = { c -> viewModel.setLanguage(c) },
                onDismissUpdate = viewModel::dismissUpdate,
                onDownloadUpdate = viewModel::downloadUpdate,
                onInstallUpdate = viewModel::installUpdate,
                onCheckUpdate = { viewModel.checkForUpdate(manual = true) },
                onSetBetaUpdatesEnabled = viewModel::setBetaUpdatesEnabled,
                onSetDoubleTapReaction = viewModel::setDoubleTapReaction,
                onUpdateNotificationSettings = { viewModel.updateNotificationSettings(it) },
                onRequestBatteryOptimization = { viewModel.requestDisableBatteryOptimization() },
                onRequestPermission = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                            openNotificationSettings()
                        } else {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        openNotificationSettings()
                    }
                },
                onPlayAudio = { viewModel.playAudio(it) },
                onPauseAudio = { viewModel.pauseAudio() },
                onResumeAudio = { viewModel.resumeAudio() },
                onStopAudio = { viewModel.stopAudio() },
                onSeekAudio = { viewModel.seekAudio(it) },
                onDownloadFile = { viewModel.downloadFile(it) },
                onCancelDownload = { viewModel.cancelDownload(it) },
                onCall = { chatId ->
                    if (androidx.core.content.ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        viewModel.startOutgoingCall(chatId)
                    } else {
                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onAcceptCall = { c, i ->
                    if (androidx.core.content.ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        viewModel.acceptCall(c, i)
                    } else {
                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onDeclineCall = viewModel::declineCall,
                onLeaveCall = viewModel::leaveCall,
                onToggleMute = {
                    if (androidx.core.content.ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        viewModel.toggleMute()
                    } else {
                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onToggleDeafen = viewModel::toggleDeafen,
                onToggleMinimize = viewModel::toggleMinimize,
                onCancelUpload = viewModel::cancelPendingUpload,
                onSendSticker = { sticker -> viewModel.sendSticker(sticker) },
                onAddSavedSticker = { message -> viewModel.addSavedStickerFromMessage(message) },
                onHandleClick = viewModel::openHandle,
                onJoinChat = viewModel::joinChat,
                onLeaveChat = viewModel::leaveChat,
                onClearNavigationSignal = viewModel::clearNavigationSignal,
                onBotCallback = viewModel::sendBotCallback
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkBatteryOptimization()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val chatId = intent?.getStringExtra("chatId")
        val callId = intent?.getStringExtra("callId")
        val callerId = intent?.getStringExtra("callerId")
        val action = intent?.getStringExtra("action")

        if (chatId != null) {
            viewModel.openChat(chatId)
            
            // Only launch IncomingCallActivity if we aren't already in a call 
            // and this is a fresh incoming call intent
            val currentCall = viewModel.uiState.value.voiceChatState
            val isAlreadyInThisCall = currentCall.currentCallId == callId && 
                                     currentCall.connectionState != ir.hienob.noveo.data.VoiceConnectionState.IDLE

            if (callId != null && callerId != null && !isAlreadyInThisCall) {
                val callIntent = Intent(this, IncomingCallActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    putExtra("chatId", chatId)
                    putExtra("callId", callId)
                    putExtra("callerId", callerId)
                    if (action == "accept_call") {
                        putExtra("action", "accept")
                    }
                }
                startActivity(callIntent)
            }
            
            // Clear intent extras to prevent re-handling on configuration changes or re-entry
            intent.removeExtra("callId")
            intent.removeExtra("callerId")
            intent.removeExtra("action")
        }
    }

    private fun openNotificationSettings() {
        val intent = Intent().apply {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
                else -> {
                    action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    putExtra("app_package", packageName)
                    putExtra("app_uid", applicationInfo.uid)
                }
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }
}
