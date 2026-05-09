package ir.hienob.noveo.app


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import android.os.PowerManager
import ir.hienob.noveo.background.NoveoNotificationService
import ir.hienob.noveo.data.CachedHomeState
import ir.hienob.noveo.data.ChatMessage
import ir.hienob.noveo.data.ChatSocket
import ir.hienob.noveo.data.ChatSummary
import ir.hienob.noveo.data.MessageContent
import ir.hienob.noveo.data.MessageFileAttachment
import ir.hienob.noveo.data.NoveoApi
import ir.hienob.noveo.data.parseChat
import ir.hienob.noveo.data.parseUser
import ir.hienob.noveo.data.parseMessageContent
import ir.hienob.noveo.data.parseChatMessage
import ir.hienob.noveo.data.parseMessagesByChat
import ir.hienob.noveo.data.parseRealtimeMessage
import ir.hienob.noveo.data.parseChatMessageList
import ir.hienob.noveo.data.parseReactions
import ir.hienob.noveo.data.NotificationSettings
import ir.hienob.noveo.data.SavedSticker
import ir.hienob.noveo.data.Session
import ir.hienob.noveo.data.SessionStore
import ir.hienob.noveo.data.SocketEvent
import ir.hienob.noveo.data.UserSummary
import ir.hienob.noveo.data.Wallet
import ir.hienob.noveo.ui.getStrings
import java.io.File
import java.io.FileOutputStream
import okhttp3.OkHttpClient
import okhttp3.Request
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max

sealed interface StartupState {
    data object Splash : StartupState
    data object Onboarding : StartupState
    data object Auth : StartupState
    data object Home : StartupState
}

data class AppUiState(
    val startupState: StartupState = StartupState.Splash,
    val loading: Boolean = false,
    val error: String? = null,
    val session: Session? = null,
    val usersById: Map<String, UserSummary> = emptyMap(),
    val onlineUserIds: Set<String> = emptySet(),
    val chats: List<ChatSummary> = emptyList(),
    val totalUnreadCount: Int = 0,
    val selectedChatId: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val authModeSignup: Boolean = false,
    val connectionTitle: String = "",
    val connectionDetail: String? = null,
    val wallet: Wallet? = null,
    val contacts: List<UserSummary> = emptyList(),
    val typingUsers: Map<String, Set<String>> = emptyMap(), // chatId -> set of userIds
    val replyingToMessage: ChatMessage? = null,
    val languageCode: String = java.util.Locale.getDefault().language,
    val updateInfo: UpdateInfo? = null,
    val isCheckingUpdate: Boolean = false,
    val notificationSettings: NotificationSettings = NotificationSettings(),
    val isBatteryOptimized: Boolean = true,
    val captchaInfo: CaptchaInfo? = null,
    val pendingAttachment: PendingAttachment? = null,
    val directRecipientId: String? = null,
    val pendingProfileId: String? = null,
    val pendingGroupInfoId: String? = null,
    val editingMessage: ChatMessage? = null,
    val forwardingMessage: ChatMessage? = null,
    val savedStickers: List<SavedSticker> = emptyList(),
    val currentAudioMessage: ChatMessage? = null,
    val isAudioPlaying: Boolean = false,
    val audioProgress: Float = 0f,
    val attachmentDownloads: Map<String, AttachmentDownloadState> = emptyMap(),
    val voiceChatState: ir.hienob.noveo.data.VoiceChatState = ir.hienob.noveo.data.VoiceChatState(),
    val incomingCall: SocketEvent.IncomingCall? = null,
    val betaUpdatesEnabled: Boolean = false,
    val doubleTapReaction: String = "❤",
    val animatedEmojiTgsEnabled: Boolean = true,
    val isSendingMessage: Boolean = false,
    val messagesByChat: Map<String, List<ChatMessage>> = emptyMap()
)

data class AttachmentDownloadState(
    val localPath: String? = null,
    val isDownloading: Boolean = false,
    val progress: Float = 0f,
    val error: String? = null
)

data class PendingAttachment(
    val uri: android.net.Uri,
    val fileName: String,
    val mimeType: String,
    val fileData: ByteArray,
    val isUploading: Boolean = false,
    val progress: Float = 0f
)

data class CaptchaInfo(
    val sessionId: String,
    val action: String,
    val extra: Map<String, Any> = emptyMap()
)

data class UpdateInfo(
    val version: String,
    val url: String,
    val isAvailable: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val isDownloaded: Boolean = false,
    val localPath: String? = null,
    val isDismissed: Boolean = false
)

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionStore = SessionStore(application)
    private val api = NoveoApi()
    private val voiceChatManager = ir.hienob.noveo.data.VoiceChatManager.getInstance(
        application, 
        api
    )
    private val messageCacheByChat = mutableMapOf<String, List<ChatMessage>>()
    private val activeDownloadJobs = mutableMapOf<String, Job>()

    fun cancelDownload(message: ChatMessage) {
        val key = message.content.file?.downloadKey() ?: return
        activeDownloadJobs[key]?.cancel()
        activeDownloadJobs.remove(key)
        updateAttachmentDownload(key, AttachmentDownloadState(isDownloading = false, progress = 0f))
    }

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private var socketResyncJob: Job? = null
    private var selectedChatRefreshJob: Job? = null
    private var pendingChatId: String? = null
    private var activeUploadJob: Job? = null
    private var activeUploadController: NoveoApi.UploadController? = null
    
    private var mediaPlayer: android.media.MediaPlayer? = null
    private var audioProgressJob: Job? = null
    
    private val typingJobs = mutableMapOf<String, Job>()

    init {
        _uiState.value = _uiState.value.copy(
            betaUpdatesEnabled = sessionStore.readBetaUpdatesEnabled(),
            doubleTapReaction = sessionStore.readDoubleTapReaction(),
            animatedEmojiTgsEnabled = sessionStore.readAnimatedEmojiTgsEnabled()
        )
        restoreSession()
        checkForUpdate()
        checkBatteryOptimization()
        
        viewModelScope.launch {
            NoveoNotificationService.socketEvents.collect { event ->
                handleSocketEvent(event)
            }
        }

        viewModelScope.launch {
            voiceChatManager.state.collect { state ->
                _uiState.value = _uiState.value.copy(voiceChatState = state)
                
                // Update background service foreground type based on call state
                val isCallActive = state.connectionState == ir.hienob.noveo.data.VoiceConnectionState.CONNECTED
                NoveoNotificationService.updateCallActive(isCallActive)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAudio()
    }

    fun playAudio(message: ChatMessage) {
        val url = message.content.file?.url ?: return
        val normalizedUrl = normalizeUrl(url) ?: return
        
        if (_uiState.value.currentAudioMessage?.id == message.id) {
            if (_uiState.value.isAudioPlaying) {
                pauseAudio()
            } else {
                resumeAudio()
            }
            return
        }

        stopAudio()

        mediaPlayer = android.media.MediaPlayer().apply {
            setDataSource(normalizedUrl)
            prepareAsync()
            setOnPreparedListener {
                start()
                _uiState.value = _uiState.value.copy(
                    currentAudioMessage = message,
                    isAudioPlaying = true,
                    audioProgress = 0f
                )
                startAudioProgressTracking()
            }
            setOnCompletionListener {
                stopAudio()
            }
            setOnErrorListener { _, _, _ ->
                _uiState.value = _uiState.value.copy(error = "Failed to play audio")
                stopAudio()
                true
            }
        }
    }

    fun pauseAudio() {
        mediaPlayer?.pause()
        _uiState.value = _uiState.value.copy(isAudioPlaying = false)
        audioProgressJob?.cancel()
    }

    fun resumeAudio() {
        mediaPlayer?.start()
        _uiState.value = _uiState.value.copy(isAudioPlaying = true)
        startAudioProgressTracking()
    }

    fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        audioProgressJob?.cancel()
        _uiState.value = _uiState.value.copy(
            currentAudioMessage = null,
            isAudioPlaying = false,
            audioProgress = 0f
        )
    }
    
    fun seekAudio(progress: Float) {
        val player = mediaPlayer ?: return
        val duration = player.duration
        if (duration > 0) {
            player.seekTo((duration * progress).toInt())
            _uiState.value = _uiState.value.copy(audioProgress = progress)
        }
    }

    private fun startAudioProgressTracking() {
        audioProgressJob?.cancel()
        audioProgressJob = viewModelScope.launch {
            while (true) {
                val player = mediaPlayer
                if (player != null && player.isPlaying) {
                    val duration = player.duration
                    if (duration > 0) {
                        val progress = player.currentPosition.toFloat() / duration
                        _uiState.value = _uiState.value.copy(audioProgress = progress)
                    }
                }
                delay(100)
            }
        }
    }

    fun downloadFile(message: ChatMessage) {
        val file = message.content.file ?: return
        val url = normalizeUrl(file.url) ?: return
        val key = file.downloadKey()
        val targetFile = getAttachmentFile(file)
        val shouldOpenWhenDone = !(file.isImage() || file.isVideo())

        if (activeDownloadJobs[key]?.isActive == true) return

        if (targetFile.exists()) {
            updateAttachmentDownload(key, AttachmentDownloadState(localPath = targetFile.absolutePath, progress = 1f))
            if (shouldOpenWhenDone) {
                openDownloadedFile(targetFile, file.type)
            }
            return
        }

        _uiState.value.attachmentDownloads[key]?.let { existing ->
            if (existing.isDownloading) return
            if (!existing.localPath.isNullOrBlank() && File(existing.localPath).exists()) {
                if (shouldOpenWhenDone) {
                    openDownloadedFile(File(existing.localPath), file.type)
                }
                return
            }
        }

        updateAttachmentDownload(
            key = key,
            state = AttachmentDownloadState(isDownloading = true, progress = 0f)
        )

        val job = viewModelScope.launch(Dispatchers.IO) {
            try {
                runCatching {
                    val client = OkHttpClient()
                    val request = Request.Builder().url(url).build()
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) throw Exception("Download failed")
                        val body = response.body ?: throw Exception("Empty body")

                        val total = max(body.contentLength(), 1L)
                        targetFile.parentFile?.mkdirs()
                        body.byteStream().use { input ->
                            FileOutputStream(targetFile).use { output ->
                                val buffer = ByteArray(8192)
                                var current = 0L
                                var read: Int
                                while (input.read(buffer).also { read = it } != -1) {
                                    ensureActive()
                                    output.write(buffer, 0, read)
                                    current += read
                                    withContext(Dispatchers.Main) {
                                        updateAttachmentDownload(
                                            key = key,
                                            state = AttachmentDownloadState(
                                                isDownloading = true,
                                                progress = current.toFloat() / total.toFloat()
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        withContext(Dispatchers.Main) {
                            updateAttachmentDownload(
                                key = key,
                                state = AttachmentDownloadState(localPath = targetFile.absolutePath, progress = 1f)
                            )
                            if (shouldOpenWhenDone) {
                                openDownloadedFile(targetFile, file.type)
                            }
                        }
                    }
                }.onFailure { e ->
                    if (e is CancellationException) return@onFailure
                    withContext(Dispatchers.Main) {
                        updateAttachmentDownload(
                            key = key,
                            state = AttachmentDownloadState(error = e.message)
                        )
                        _uiState.value = _uiState.value.copy(error = "Download failed: ${e.message}")
                    }
                }
            } finally {
                activeDownloadJobs.remove(key)
            }
        }
        activeDownloadJobs[key] = job
    }

    private fun normalizeUrl(url: String): String? {
        // Simple normalization for now, matching HomeUi.kt logic
        val baseUrl = "https://noveo.ir:8443"
        val value = url.trim().replace("\\", "/")
        if (value.isBlank()) return null
        if (value.startsWith("data:")) return value
        if (value.contains("server_no_captcha")) {
             return value.replace(Regex("^(?:(?:https?|wss?)://)?server_no_captcha(?::\\d+)?", RegexOption.IGNORE_CASE), baseUrl)
        }
        if (value.startsWith("//")) return "https:$value"
        if (value.startsWith("http://") || value.startsWith("https://")) return value
        val normalized = if (value.startsWith("/")) value else "/$value"
        return "$baseUrl$normalized"
    }

    fun checkBatteryOptimization() {
        val pm = getApplication<Application>().getSystemService(Context.POWER_SERVICE) as PowerManager
        _uiState.value = _uiState.value.copy(
            isBatteryOptimized = !pm.isIgnoringBatteryOptimizations(getApplication<Application>().packageName)
        )
    }

    fun requestDisableBatteryOptimization() {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${getApplication<Application>().packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        getApplication<Application>().startActivity(intent)
    }

    fun updateNotificationSettings(settings: NotificationSettings) {
        _uiState.value = _uiState.value.copy(notificationSettings = settings)
        sessionStore.writeNotificationSettings(settings)
    }

    fun setBetaUpdatesEnabled(enabled: Boolean) {
        sessionStore.writeBetaUpdatesEnabled(enabled)
        _uiState.value = _uiState.value.copy(
            betaUpdatesEnabled = enabled,
            updateInfo = null,
            isCheckingUpdate = false
        )
        checkForUpdate()
    }

    fun setDoubleTapReaction(reaction: String) {
        sessionStore.writeDoubleTapReaction(reaction)
        _uiState.value = _uiState.value.copy(doubleTapReaction = reaction)
    }

    fun setAnimatedEmojiTgsEnabled(enabled: Boolean) {
        sessionStore.writeAnimatedEmojiTgsEnabled(enabled)
        _uiState.value = _uiState.value.copy(animatedEmojiTgsEnabled = enabled)
    }

    fun cancelPendingUpload(chatId: String) {
        activeUploadController?.cancel()
        activeUploadJob?.cancel()
        val attachment = _uiState.value.pendingAttachment
        _uiState.value = _uiState.value.copy(
            pendingAttachment = attachment?.copy(isUploading = false, progress = 0f),
            isSendingMessage = false
        )
    }

    fun checkForUpdate(manual: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentVersion = ir.hienob.noveo.BuildConfig.VERSION_NAME
            if (manual) _uiState.value = _uiState.value.copy(isCheckingUpdate = true)
            val updateJson = api.checkForUpdate()
            if (manual) delay(500) // Small delay for UX

            if (updateJson == null) {
                if (manual) {
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(
                            isCheckingUpdate = false, 
                            updateInfo = UpdateInfo(currentVersion, "", isAvailable = false)
                        )
                        delay(2000)
                        _uiState.value = _uiState.value.copy(updateInfo = null)
                    }
                }
                return@launch
            }

            val releaseVersion = updateJson.optString("version")
            val releaseUrl = updateJson.optString("url")
            val betaVersion = updateJson.optString("beta_version")
            val betaUrl = updateJson.optString("beta_url")

            val candidates = buildList {
                if (releaseVersion.isNotBlank() && releaseUrl.isNotBlank()) {
                    add(UpdateInfo(version = releaseVersion, url = releaseUrl))
                }
                if (_uiState.value.betaUpdatesEnabled && betaVersion.isNotBlank() && betaUrl.isNotBlank()) {
                    add(UpdateInfo(version = betaVersion, url = betaUrl))
                }
            }
            val nextUpdate = candidates
                .filter { compareVersions(it.version, currentVersion) > 0 }
                .maxWithOrNull(Comparator { left, right -> compareVersions(left.version, right.version) })

            if (nextUpdate != null) {
                val apkFile = File(getApplication<Application>().filesDir, "update-${nextUpdate.version}.apk")
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isCheckingUpdate = false,
                        updateInfo = nextUpdate.copy(
                            isAvailable = true,
                            isDownloaded = apkFile.exists(),
                            localPath = if (apkFile.exists()) apkFile.absolutePath else null,
                            isDismissed = false
                        )
                    )
                }
            } else if (manual) {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isCheckingUpdate = false,
                        updateInfo = UpdateInfo(currentVersion, "", isAvailable = false)
                    )
                    delay(2000)
                    _uiState.value = _uiState.value.copy(updateInfo = null)
                }
            }
        }
    }

    fun downloadUpdate() {
        val info = _uiState.value.updateInfo ?: return
        if (info.isDownloading || info.isDownloaded) return

        viewModelScope.launch(Dispatchers.IO) {
            // Re-check inside to prevent races from rapid clicks
            if (_uiState.value.updateInfo?.isDownloading == true) return@launch
            
            _uiState.value = _uiState.value.copy(
                updateInfo = info.copy(isDownloading = true, downloadProgress = 0f)
            )

            val client = OkHttpClient()
            val request = Request.Builder().url(info.url).build()
            runCatching {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw Exception("Download failed")
                    val body = response.body ?: throw Exception("Empty body")
                    val total = body.contentLength()
                    val apkFile = File(getApplication<Application>().filesDir, "update-${info.version}.apk")

                    body.byteStream().use { input ->
                        FileOutputStream(apkFile).use { output ->
                            val buffer = ByteArray(8192)
                            var read: Int
                            var current = 0L
                            while (input.read(buffer).also { read = it } != -1) {
                                output.write(buffer, 0, read)
                                current += read
                                withContext(Dispatchers.Main) {
                                    _uiState.value = _uiState.value.copy(
                                        updateInfo = _uiState.value.updateInfo?.copy(
                                            downloadProgress = if (total > 0) current.toFloat() / total else 0f
                                        )
                                    )
                                }
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(
                            updateInfo = _uiState.value.updateInfo?.copy(
                                isDownloading = false,
                                isDownloaded = true,
                                localPath = apkFile.absolutePath
                            )
                        )
                    }
                }
            }.onFailure {
                it.printStackTrace()
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        updateInfo = _uiState.value.updateInfo?.copy(isDownloading = false),
                        error = "Download failed: ${it.message}"
                    )
                }
            }
        }
    }

    fun dismissUpdate() {
        _uiState.value = _uiState.value.copy(
            updateInfo = _uiState.value.updateInfo?.copy(isDismissed = true)
        )
    }

    fun installUpdate() {
        val info = _uiState.value.updateInfo ?: return
        val path = info.localPath ?: return
        val apkFile = File(path)
        if (!apkFile.exists()) return

        val context = getApplication<Application>()
        
        runCatching {
            // Android 8.0+ check for unknown apps permission
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    return@runCatching
                }
            }

            val uri = FileProvider.getUriForFile(context, "ir.hienob.noveo.updates.provider", apkFile)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                clipData = ClipData.newRawUri("", uri)
            }
            context.startActivity(intent)
            
            // Clean up old update files except the current one
            viewModelScope.launch(Dispatchers.IO) {
                delay(5000)
                context.filesDir.listFiles { _, name -> 
                    name.startsWith("update-") && name.endsWith(".apk") && !name.contains(info.version)
                }?.forEach { it.delete() }
            }
        }.onFailure {
            _uiState.value = _uiState.value.copy(error = "Installation failed: ${it.message}")
        }
    }

    fun sendBotCallback(chatId: String, messageId: String, callbackData: String) {
        val session = _uiState.value.session ?: return
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { api.sendBotCallback(session, chatId, messageId, callbackData) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun restoreSession() {
        viewModelScope.launch {
            val session = sessionStore.read()
            val notifySettings = sessionStore.readNotificationSettings()
            val cachedHomeState = sessionStore.readCachedHomeState()
            if (session == null) {
                _uiState.value = _uiState.value.copy(
                    startupState = StartupState.Onboarding,
                    loading = false,
                    notificationSettings = notifySettings
                )
                return@launch
            }

            restoreCachedHomeState(cachedHomeState)
            val langCode = sessionStore.readLanguageCode()
            val strings = getStrings(langCode)
            _uiState.value = _uiState.value.copy(
                startupState = StartupState.Home,
                session = session,
                loading = cachedHomeState?.chats.isNullOrEmpty(),
                connectionTitle = if (cachedHomeState == null) strings.brandName else strings.connecting,
                notificationSettings = notifySettings,
                languageCode = langCode
            )
            NoveoNotificationService.start(getApplication())
            loadSavedStickers(session)
            loadHome(session)
        }
    }

    fun dismissOnboarding() {
        _uiState.value = _uiState.value.copy(startupState = StartupState.Auth)
    }

    fun setAuthMode(signup: Boolean) {
        _uiState.value = _uiState.value.copy(authModeSignup = signup, error = null)
    }

    fun authenticate(handle: String, password: String, captchaToken: String? = null) {
        viewModelScope.launch {
            runCatching {
                val strings = getStrings(_uiState.value.languageCode)
                _uiState.value = _uiState.value.copy(startupState = StartupState.Home, loading = true, connectionTitle = strings.brandName)
                val session = withContext(Dispatchers.IO) {
                    if (_uiState.value.authModeSignup) api.signup(handle, password, captchaToken) else api.login(handle, password)
                }
                sessionStore.write(session)
                NoveoNotificationService.start(getApplication())
                loadSavedStickers(session)
                loadHome(session)
            }.onFailure {
                _uiState.value = _uiState.value.copy(startupState = StartupState.Auth, loading = false, error = it.message ?: "Authentication failed")
            }
        }
    }

    fun startRegisterCaptcha(handle: String, password: String) {
        viewModelScope.launch {
            delay(300) // Ensure screen has settled
            runCatching {
                val started = withContext(Dispatchers.IO) {
                    api.startCaptcha(null, "register")
                }
                _uiState.value = _uiState.value.copy(
                    captchaInfo = CaptchaInfo(
                        sessionId = started.getString("sessionId"),
                        action = "register",
                        extra = mapOf("handle" to handle, "password" to password)
                    )
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(error = it.message ?: "Failed to start captcha")
            }
        }
    }

    fun createChat(name: String, type: String, handle: String? = null, bio: String? = null, captchaToken: String? = null) {
        val session = _uiState.value.session ?: return
        viewModelScope.launch {
            runCatching {
                _uiState.value = _uiState.value.copy(loading = true)
                withContext(Dispatchers.IO) {
                    api.createChat(session, name, type, handle, bio, captchaToken)
                }
                loadHome(session)
                _uiState.value = _uiState.value.copy(loading = false)
            }.onFailure {
                _uiState.value = _uiState.value.copy(loading = false, error = it.message ?: "Chat creation failed")
            }
        }
    }

    fun startCreateChatCaptcha(name: String, type: String, handle: String? = null, bio: String? = null) {
        createChat(name, type, handle, bio)
    }

    fun logout() {
        val languageCode = _uiState.value.languageCode
        val notificationSettings = _uiState.value.notificationSettings
        val betaUpdatesEnabled = _uiState.value.betaUpdatesEnabled
        val doubleTapReaction = _uiState.value.doubleTapReaction
        val animatedEmojiTgsEnabled = _uiState.value.animatedEmojiTgsEnabled
        getApplication<Application>().stopService(Intent(getApplication(), NoveoNotificationService::class.java))
        socketResyncJob?.cancel()
        selectedChatRefreshJob?.cancel()
        activeUploadController?.cancel()
        activeUploadJob?.cancel()
        messageCacheByChat.clear()
        sessionStore.clear()
        sessionStore.writeNotificationSettings(notificationSettings)
        sessionStore.writeBetaUpdatesEnabled(betaUpdatesEnabled)
        sessionStore.writeDoubleTapReaction(doubleTapReaction)
        sessionStore.writeAnimatedEmojiTgsEnabled(animatedEmojiTgsEnabled)
        _uiState.value = AppUiState(
            startupState = StartupState.Auth,
            languageCode = languageCode,
            notificationSettings = notificationSettings,
            betaUpdatesEnabled = betaUpdatesEnabled,
            doubleTapReaction = doubleTapReaction,
            animatedEmojiTgsEnabled = animatedEmojiTgsEnabled
        )
    }

    fun backToChatList() {
        selectedChatRefreshJob?.cancel()
        _uiState.value = _uiState.value.copy(
            selectedChatId = null, 
            messages = emptyList(), 
            replyingToMessage = null,
            editingMessage = null,
            forwardingMessage = null
        )
    }

    fun dismissCaptcha() {
        _uiState.value = _uiState.value.copy(captchaInfo = null)
    }

    fun openDirectChat(userId: String) {
        val state = _uiState.value
        val selfId = state.session?.userId ?: return

        if (userId == selfId) {
            val savedChat = state.chats.firstOrNull { chat ->
                chat.id.startsWith("saved_") ||
                    (chat.chatType == "private" && chat.memberIds.size == 1 && chat.memberIds.firstOrNull() == selfId)
            }
            if (savedChat != null) {
                _uiState.value = _uiState.value.copy(directRecipientId = null)
                openChat(savedChat.id)
            } else {
                val syntheticSavedChat = ChatSummary(
                    id = "saved_$selfId",
                    chatType = "private",
                    title = "Saved Messages",
                    avatarUrl = "saved_messages",
                    memberIds = listOf(selfId),
                    canChat = true
                )
                _uiState.value = _uiState.value.copy(
                    chats = state.chats + syntheticSavedChat,
                    selectedChatId = syntheticSavedChat.id,
                    messages = emptyList(),
                    directRecipientId = null
                )
            }
            return
        }

        val chatId = listOf(selfId, userId).sorted().joinToString("_")
        val existingChat = state.chats.firstOrNull { it.id == chatId }

        if (existingChat != null) {
            _uiState.value = _uiState.value.copy(directRecipientId = null)
            openChat(chatId)
        } else {
            // Create a synthetic chat summary to show in the UI
            val user = state.usersById[userId]
            val syntheticChat = ChatSummary(
                id = chatId,
                chatType = "private",
                title = user?.username ?: "Direct Chat",
                avatarUrl = user?.avatarUrl,
                memberIds = listOf(selfId, userId),
                canChat = true
            )

            _uiState.value = _uiState.value.copy(
                chats = state.chats + syntheticChat,
                selectedChatId = chatId,
                messages = emptyList(),
                directRecipientId = userId
            )
        }
    }

    fun openHandle(handle: String) {
        val normalizedHandle = if (handle.startsWith("@")) handle else "@$handle"
        val state = _uiState.value
        val matchedUser = state.usersById.values.firstOrNull {
            "@${it.handle}".equals(normalizedHandle, ignoreCase = true) ||
                "@${it.username}".equals(normalizedHandle, ignoreCase = true)
        }
        if (matchedUser != null) {
            _uiState.value = state.copy(pendingProfileId = matchedUser.id, pendingGroupInfoId = null)
            return
        }
        
        val payload = org.json.JSONObject()
            .put("type", "get_channel_by_handle")
            .put("handle", normalizedHandle)
        
        NoveoNotificationService.send(payload)
    }

    fun clearNavigationSignal() {
        _uiState.value = _uiState.value.copy(
            pendingProfileId = null,
            pendingGroupInfoId = null
        )
    }

    fun joinChat(chatId: String) {
        val session = _uiState.value.session ?: return
        val payload = org.json.JSONObject()
            .put("type", "join_channel")
            .put("chatId", chatId)
        
        NoveoNotificationService.send(payload)
        
        // Optimistically update membership and selection so the UI switches to input mode immediately
        val updatedChats = _uiState.value.chats.map {
            if (it.id == chatId) {
                it.copy(memberIds = it.memberIds + session.userId)
            } else it
        }
        
        _uiState.value = _uiState.value.copy(
            selectedChatId = chatId,
            chats = updatedChats
        )
        refreshHomeSilently()
    }

    fun leaveChat(chatId: String) {
        val session = _uiState.value.session ?: return
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { api.leaveChat(session, chatId) }
                _uiState.value = _uiState.value.let { current ->
                    current.copy(
                        chats = current.chats.map { chat ->
                            if (chat.id == chatId) chat.copy(memberIds = chat.memberIds.filterNot { it == session.userId })
                            else chat
                        },
                        selectedChatId = if (current.selectedChatId == chatId) null else current.selectedChatId,
                        messages = if (current.selectedChatId == chatId) emptyList() else current.messages
                    )
                }
                refreshHomeSilently()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun retryPendingMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            messageCacheByChat.forEach { (chatId, messages) ->
                messages.filter { it.pending }.forEach { msg ->
                    val contentObj = org.json.JSONObject().put("text", msg.content.text)
                    msg.content.file?.let { file ->
                        contentObj.put("file", org.json.JSONObject()
                            .put("url", file.url)
                            .put("name", file.name)
                            .put("type", file.type)
                            .put("size", file.size))
                    }

                    val payload = org.json.JSONObject()
                        .put("type", "message")
                        .put("chatId", chatId)
                        .put("content", contentObj.toString())
                        .put("replyToId", msg.replyToId)
                        .put("clientTempId", msg.clientTempId)
                    
                    NoveoNotificationService.send(payload)
                }
            }
        }
    }

    fun onCaptchaTokenReceived(token: String) {
        val info = _uiState.value.captchaInfo ?: return
        dismissCaptcha()
        
        when (info.action) {
            "register" -> {
                val handle = info.extra["handle"] as? String ?: return
                val password = info.extra["password"] as? String ?: return
                authenticate(handle, password, token)
            }
            "dm_start" -> {
                val targetUserId = info.extra["targetUserId"] as? String ?: return
                createChat(
                    name = "Direct Chat", 
                    type = "private", 
                    handle = null, 
                    bio = null, 
                    captchaToken = token
                )
            }
            "create_chat" -> {
                val name = info.extra["name"] as? String ?: "New Chat"
                val type = info.extra["type"] as? String ?: "group"
                val handle = info.extra["handle"] as? String
                val bio = info.extra["bio"] as? String
                createChat(name, type, handle.takeIf { it?.isNotBlank() == true }, bio.takeIf { it?.isNotBlank() == true }, token)
            }
        }
    }

    fun openChat(chatId: String) {
        val session = _uiState.value.session
        if (session == null) {
            pendingChatId = chatId
            return
        }
        startSelectedChatRefresh(session, chatId)
        viewModelScope.launch {
            val cachedMessages = messageCacheByChat[chatId].orEmpty().sortedBy { it.timestamp }
            val updatedChats = _uiState.value.chats.map {
                if (it.id == chatId) it.copy(unreadCount = 0) else it
            }
            _uiState.value = _uiState.value.copy(
                selectedChatId = chatId,
                chats = updatedChats,
                totalUnreadCount = sumUnread(updatedChats),
                messages = cachedMessages,
                loading = cachedMessages.isEmpty(),
                replyingToMessage = null,
                editingMessage = null,
                forwardingMessage = null
            )
            persistCachedHomeState()
            
            // Mark last message as seen to sync unread status with server
            cachedMessages.lastOrNull { it.senderId != session.userId }?.let { lastMsg ->
                markAsSeen(lastMsg.id)
            }
            
            refreshHomeSilently()
        }
    }

    fun setReplyingTo(message: ChatMessage?) {
        _uiState.value = _uiState.value.copy(replyingToMessage = message)
    }

    fun setEditingMessage(message: ChatMessage?) {
        _uiState.value = _uiState.value.copy(editingMessage = message)
    }

    fun setForwardingMessage(message: ChatMessage?) {
        _uiState.value = _uiState.value.copy(forwardingMessage = message)
    }

    fun attachFile(uri: android.net.Uri) {
        viewModelScope.launch {
            runCatching {
                val context = getApplication<Application>()
                val contentResolver = context.contentResolver
                val fileName = context.getFileName(uri) ?: "file"
                val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
                val fileData = contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: error("Failed to read file")
                
                _uiState.value = _uiState.value.copy(
                    pendingAttachment = PendingAttachment(
                        uri = uri,
                        fileName = fileName,
                        mimeType = mimeType,
                        fileData = fileData
                    )
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(error = "Failed to attach file: ${it.message}")
            }
        }
    }

    fun removeAttachment() {
        _uiState.value = _uiState.value.copy(pendingAttachment = null)
    }

    fun sendSticker(sticker: SavedSticker) {
        val session = _uiState.value.session ?: return
        val chatId = _uiState.value.selectedChatId ?: return
        val directRecipientId = _uiState.value.directRecipientId
        sendPreparedMessage(
            session = session,
            chatId = chatId,
            text = "",
            file = sticker.toMessageAttachment(),
            replyToId = _uiState.value.replyingToMessage?.id,
            directRecipientId = directRecipientId
        )
    }

    fun addSavedStickerFromMessage(message: ChatMessage) {
        val session = _uiState.value.session ?: return
        val file = message.content.file ?: return
        if (!file.canBeSavedAsSticker()) return
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    api.addSavedSticker(
                        session,
                        SavedSticker(
                            url = file.url,
                            type = if (file.isTgsSticker()) "tgs" else "image"
                        )
                    )
                }
            }.onSuccess { stickers ->
                _uiState.value = _uiState.value.copy(savedStickers = stickers)
            }.onFailure {
                _uiState.value = _uiState.value.copy(error = "Failed to save sticker: ${it.message}")
            }
        }
    }

    fun sendMessage(text: String) {
        val session = _uiState.value.session ?: return
        val chatId = _uiState.value.selectedChatId ?: return
        val directRecipientId = _uiState.value.directRecipientId
        val replyingTo = _uiState.value.replyingToMessage
        val editingMessage = _uiState.value.editingMessage
        val attachment = _uiState.value.pendingAttachment
        if (_uiState.value.isSendingMessage) return
        if (text.isBlank() && attachment == null) return

        if (editingMessage != null) {
            editMessage(editingMessage.id, text)
            setEditingMessage(null)
            return
        }

        val tempId = "temp-${System.currentTimeMillis()}"
        
        // If we have an attachment, we need to upload it first
        activeUploadJob = viewModelScope.launch {
            try {
                var uploadedFile: MessageFileAttachment? = null
                _uiState.value = _uiState.value.copy(isSendingMessage = true)
                if (attachment != null) {
                    val uploadController = NoveoApi.UploadController()
                    activeUploadController = uploadController
                    _uiState.value = _uiState.value.copy(
                        pendingAttachment = attachment.copy(isUploading = true, progress = 0f)
                    )
                    uploadedFile = withContext(Dispatchers.IO) {
                        api.uploadFile(session, attachment.fileData, attachment.fileName, attachment.mimeType, uploadController) { progress ->
                            _uiState.value = _uiState.value.copy(
                                pendingAttachment = _uiState.value.pendingAttachment?.copy(progress = progress)
                            )
                        }
                    }
                    ensureActive()
                    uploadedFile?.let { cacheUploadedAttachment(it, attachment) }
                    _uiState.value = _uiState.value.copy(pendingAttachment = null)
                }

                val pendingMsg = ChatMessage(
                    id = tempId,
                    chatId = chatId,
                    senderId = session.userId,
                    senderName = _uiState.value.usersById[session.userId]?.username ?: "Me",
                    content = MessageContent(text = text.takeIf { it.isNotBlank() }, file = uploadedFile, replyToId = replyingTo?.id),
                    timestamp = System.currentTimeMillis() / 1000,
                    pending = true,
                    clientTempId = tempId,
                    replyToId = replyingTo?.id
                )

                messageCacheByChat[chatId] = mergeMessages(messageCacheByChat[chatId].orEmpty(), listOf(pendingMsg))
                val isStillViewingTargetChat = _uiState.value.selectedChatId == chatId
                _uiState.value = _uiState.value.copy(
                    messages = if (isStillViewingTargetChat) {
                        mergeMessages(_uiState.value.messages, listOf(pendingMsg))
                    } else {
                        _uiState.value.messages
                    },
                    replyingToMessage = if (isStillViewingTargetChat) null else _uiState.value.replyingToMessage,
                    messagesByChat = messageCacheByChat.toMap()
                )
                persistCachedHomeState()

                withContext(Dispatchers.IO) {
                    val contentObj = org.json.JSONObject().put("text", text.takeIf { it.isNotBlank() })
                    if (uploadedFile != null) {
                        contentObj.put("file", org.json.JSONObject()
                            .put("url", uploadedFile.url)
                            .put("name", uploadedFile.name)
                            .put("type", uploadedFile.type)
                            .put("size", uploadedFile.size))
                    }

                    val payload = org.json.JSONObject()
                        .put("type", "message")
                        .put("chatId", chatId)
                        .put("content", contentObj.toString())
                        .put("replyToId", replyingTo?.id)
                        .put("clientTempId", tempId)
                    
                    if (directRecipientId != null) {
                        payload.put("recipientId", directRecipientId)
                    }

                    NoveoNotificationService.send(payload)
                }
                _uiState.value = _uiState.value.copy(isSendingMessage = false)
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException || e is java.io.InterruptedIOException) {
                    val currentAttachment = _uiState.value.pendingAttachment ?: attachment
                    _uiState.value = _uiState.value.copy(
                        pendingAttachment = currentAttachment?.copy(isUploading = false, progress = 0f),
                        isSendingMessage = false
                    )
                    return@launch
                }
                _uiState.value = _uiState.value.copy(
                    error = "Failed to send message: ${e.message}",
                    pendingAttachment = attachment?.copy(isUploading = false),
                    isSendingMessage = false
                )
            } finally {
                activeUploadController = null
                activeUploadJob = null
            }
        }
    }

    private fun loadSavedStickers(session: Session) {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    api.getSavedStickers(session)
                }
            }.onSuccess { stickers ->
                _uiState.value = _uiState.value.copy(savedStickers = stickers)
            }.onFailure {
                _uiState.value = _uiState.value.copy(error = "Failed to load stickers: ${it.message}")
            }
        }
    }

    private fun sendPreparedMessage(
        session: Session,
        chatId: String,
        text: String,
        file: MessageFileAttachment?,
        replyToId: String?,
        directRecipientId: String?
    ) {
        val tempId = "temp-${System.currentTimeMillis()}"
        viewModelScope.launch {
            try {
                val pendingMsg = ChatMessage(
                    id = tempId,
                    chatId = chatId,
                    senderId = session.userId,
                    senderName = _uiState.value.usersById[session.userId]?.username ?: "Me",
                    content = MessageContent(
                        text = text.takeIf { it.isNotBlank() },
                        file = file,
                        replyToId = replyToId
                    ),
                    timestamp = System.currentTimeMillis() / 1000,
                    pending = true,
                    clientTempId = tempId,
                    replyToId = replyToId
                )

                messageCacheByChat[chatId] = mergeMessages(messageCacheByChat[chatId].orEmpty(), listOf(pendingMsg))
                val isStillViewingTargetChat = _uiState.value.selectedChatId == chatId
                _uiState.value = _uiState.value.copy(
                    messages = if (isStillViewingTargetChat) {
                        mergeMessages(_uiState.value.messages, listOf(pendingMsg))
                    } else {
                        _uiState.value.messages
                    },
                    replyingToMessage = if (isStillViewingTargetChat) null else _uiState.value.replyingToMessage,
                    messagesByChat = messageCacheByChat.toMap()
                )
                persistCachedHomeState()

                withContext(Dispatchers.IO) {
                    val contentObj = org.json.JSONObject().put("text", text.takeIf { it.isNotBlank() })
                    if (file != null) {
                        contentObj.put("file", org.json.JSONObject()
                            .put("url", file.url)
                            .put("name", file.name)
                            .put("type", file.type)
                            .put("size", file.size))
                    }

                    val payload = org.json.JSONObject()
                        .put("type", "message")
                        .put("chatId", chatId)
                        .put("content", contentObj.toString())
                        .put("replyToId", replyToId)
                        .put("clientTempId", tempId)

                    if (directRecipientId != null) {
                        payload.put("recipientId", directRecipientId)
                    }

                    NoveoNotificationService.send(payload)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to send message: ${e.message}")
            }
        }
    }

    private fun SavedSticker.toMessageAttachment(): MessageFileAttachment {
        val normalizedType = type.ifBlank { "image" }
        val extension = when {
            normalizedType == "tgs" -> "tgs"
            url.contains(".webp", true) -> "webp"
            url.contains(".gif", true) -> "gif"
            url.contains(".jpg", true) -> "jpg"
            url.contains(".jpeg", true) -> "jpeg"
            else -> "png"
        }
        val mimeType = when (extension) {
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "jpg", "jpeg" -> "image/jpeg"
            "tgs" -> "application/x-tgsticker"
            else -> "image/png"
        }
        return MessageFileAttachment(
            url = url,
            name = "sticker.$extension",
            type = mimeType
        )
    }

    private fun MessageFileAttachment.canBeSavedAsSticker(): Boolean {
        val lowerUrl = url.lowercase()
        return isTgsSticker() ||
            isImage() ||
            lowerUrl.contains(".png") ||
            lowerUrl.contains(".webp") ||
            lowerUrl.contains(".gif") ||
            lowerUrl.contains(".jpg") ||
            lowerUrl.contains(".jpeg") ||
            lowerUrl.contains(".tgs")
    }

    private fun android.content.Context.getFileName(uri: android.net.Uri): String? {
        var name: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (index != -1) name = it.getString(index)
                }
            }
        }
        if (name == null) {
            name = uri.path
            val cut = name?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                name = name?.substring(cut + 1)
            }
        }
        return name
    }

    fun sendTyping() {
        val session = _uiState.value.session ?: return
        val chatId = _uiState.value.selectedChatId ?: return
        val payload = org.json.JSONObject()
            .put("type", "typing")
            .put("chatId", chatId)
        NoveoNotificationService.send(payload)
    }

    fun markAsSeen(messageId: String) {
        val session = _uiState.value.session ?: return
        val chatId = _uiState.value.selectedChatId ?: return
        val payload = org.json.JSONObject()
            .put("type", "message_seen")
            .put("chatId", chatId)
            .put("messageId", messageId)
        NoveoNotificationService.send(payload)
    }

    fun loadOlderMessages() {
        val state = _uiState.value
        val chatId = state.selectedChatId ?: return
        val oldestMsg = state.messages.minByOrNull { it.timestamp } ?: return
        
        val payload = org.json.JSONObject()
            .put("type", "load_older_messages")
            .put("chatId", chatId)
            .put("beforeTimestamp", oldestMsg.timestamp)
            .put("beforeMessageId", oldestMsg.id)
        
        NoveoNotificationService.send(payload)
    }

    fun refreshHomeSilently() {
        val session = _uiState.value.session ?: return
        val payload = org.json.JSONObject().put("type", "resync_state")
        NoveoNotificationService.send(payload)
    }

    private fun handleSocketEvent(event: SocketEvent) {
        val session = _uiState.value.session ?: return
        if (event is SocketEvent.UserListUpdate || event is SocketEvent.HistoryUpdate) {
             NoveoNotificationService.updateKnownUsers(_uiState.value.usersById)
        }
        when (event) {
            is SocketEvent.ConnectionState -> {
                val currentState = _uiState.value
                val strings = getStrings(currentState.languageCode)
                _uiState.value = _uiState.value.copy(
                    loading = currentState.chats.isEmpty() && event.connected,
                    connectionTitle = if (event.connected) strings.updating else strings.connecting,
                    connectionDetail = event.detail
                )
                if (event.connected) {
                    retryPendingMessages()
                    // Immediately request resync on connect to speed up UI updates
                    refreshHomeSilently()
                }
            }
            is SocketEvent.NewMessage -> handleIncomingMessage(event.message)
            is SocketEvent.ChannelInfo -> {
                val chat = event.chat
                val updatedChats = (_uiState.value.chats.filter { it.id != chat.id } + chat)
                    .sortedByDescending { it.lastMessageTimestamp }
                
                if (event.messages.isNotEmpty()) {
                    messageCacheByChat[chat.id] = event.messages
                }
                
                val nextState = _uiState.value.copy(
                    chats = updatedChats,
                    messages = if (_uiState.value.selectedChatId == chat.id) event.messages.sortedBy { it.timestamp } else _uiState.value.messages
                )
                _uiState.value = if (chat.chatType == "private") {
                    val otherUserId = chat.memberIds.firstOrNull { it != nextState.session?.userId }
                    nextState.copy(pendingProfileId = otherUserId, pendingGroupInfoId = null)
                } else {
                    nextState.copy(pendingGroupInfoId = chat.id)
                }
            }
            is SocketEvent.NewChatInfo -> {
                val chat = event.chat
                val updatedChats = (_uiState.value.chats.filter { it.id != chat.id } + chat)
                    .sortedByDescending { it.lastMessageTimestamp }
                
                if (event.messages.isNotEmpty()) {
                    messageCacheByChat[chat.id] = event.messages
                }
                
                _uiState.value = _uiState.value.copy(
                    chats = updatedChats,
                    messages = if (_uiState.value.selectedChatId == chat.id) event.messages.sortedBy { it.timestamp } else _uiState.value.messages
                )
            }
            is SocketEvent.MessageSent -> handleIncomingMessage(event.message)
            is SocketEvent.Typing -> handleTyping(event.chatId, event.senderId)
            is SocketEvent.MessageSeenUpdate -> handleSeenUpdate(event.chatId, event.messageId, event.userId)
            is SocketEvent.MessageReactionUpdate -> handleReactionUpdate(event.chatId, event.messageId, event.reactions)
            is SocketEvent.MessageEditUpdate -> handleMessageEditUpdate(event.chatId, event.messageId, event.newContent, event.editedAt)
            is SocketEvent.MessageDeleteUpdate -> handleMessageDelete(event.chatId, event.messageId)
            is SocketEvent.MessagePinUpdate -> handlePinUpdate(event.chatId, event.messageId, event.isPinned)
            is SocketEvent.MessagePinnedUpdate -> handleMessagePinnedUpdate(event.chatId, event.pinnedMessage)
            is SocketEvent.UserListUpdate -> {
                _uiState.value = _uiState.value.copy(
                    usersById = _uiState.value.usersById + event.usersById,
                    onlineUserIds = event.onlineIds
                )
                persistCachedHomeState()
            }
            is SocketEvent.ChatUpdated -> refreshHomeSilently()
            is SocketEvent.HistoryUpdate -> {
                event.messagesByChat.forEach { (chatId, incomingMessages) ->
                    messageCacheByChat[chatId] = mergeMessages(
                        messageCacheByChat[chatId].orEmpty(),
                        incomingMessages
                    )
                }
                val selectedChatMessages = _uiState.value.selectedChatId
                    ?.let { messageCacheByChat[it].orEmpty() }
                    ?: _uiState.value.messages

                val self = event.users[session.userId]
                val lang = self?.languageCode ?: _uiState.value.languageCode
                val strings = getStrings(lang)
                
                val sortedChats = event.chats.sortedByDescending { it.lastMessageTimestamp }
                _uiState.value = _uiState.value.copy(
                    chats = sortedChats,
                    totalUnreadCount = sumUnread(sortedChats),
                    usersById = _uiState.value.usersById + event.users,
                    messages = selectedChatMessages,
                    loading = false,
                    connectionDetail = null,
                    connectionTitle = strings.brandName,
                    languageCode = lang,
                    messagesByChat = messageCacheByChat.toMap()
                )
                persistCachedHomeState()
            }
            is SocketEvent.OlderMessages -> {
                val currentMessages = messageCacheByChat[event.chatId].orEmpty()
                val updatedMessages = mergeMessages(currentMessages, event.messages)
                messageCacheByChat[event.chatId] = updatedMessages
                
                val updatedChats = _uiState.value.chats.map {
                    if (it.id == event.chatId) it.copy(hasMoreHistory = event.hasMoreHistory) else it
                }

                if (event.chatId == _uiState.value.selectedChatId) {
                    _uiState.value = _uiState.value.copy(
                        messages = updatedMessages,
                        chats = updatedChats,
                        messagesByChat = messageCacheByChat.toMap()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        chats = updatedChats,
                        messagesByChat = messageCacheByChat.toMap()
                    )
                }
                persistCachedHomeState()
            }
            is SocketEvent.IncomingCall -> {
                if (_uiState.value.voiceChatState.connectionState == ir.hienob.noveo.data.VoiceConnectionState.IDLE) {
                    _uiState.value = _uiState.value.copy(incomingCall = event)
                }
            }
            is SocketEvent.VoiceChatUpdate -> {
                // Similar to voicechat.js handleServerEvent
                val active = event.activeVoiceChats
                val currentChatId = _uiState.value.voiceChatState.currentChatId
                if (currentChatId != null && !active.has(currentChatId)) {
                    // Call ended on server
                    voiceChatManager.leaveCall()
                }
                refreshHomeSilently()
            }
            is SocketEvent.VoiceCallEnded -> {
                if (event.chatId == _uiState.value.voiceChatState.currentChatId) {
                    voiceChatManager.leaveCall()
                }
            }
            is SocketEvent.VoiceCallError -> {
                _uiState.value = _uiState.value.copy(error = event.message)
            }
        }
    }

    private suspend fun loadHome(session: Session) {
        // startSocketResyncLoop() removed to avoid churn
        
        // Only load non-socket features via HTTP
        runCatching {
            val wallet = withContext(Dispatchers.IO) { runCatching { api.getStarsOverview(session) }.getOrNull() }
            val contacts = withContext(Dispatchers.IO) { runCatching { api.getContacts(session) }.getOrDefault(emptyList()) }
            val strings = getStrings(_uiState.value.languageCode)

            _uiState.value = _uiState.value.copy(
                startupState = StartupState.Home,
                loading = false,
                session = session,
                wallet = wallet,
                contacts = contacts,
                connectionTitle = if (_uiState.value.chats.isEmpty()) strings.connecting else _uiState.value.connectionTitle,
            )
            NoveoNotificationService.updateKnownUsers(_uiState.value.usersById)
            
            pendingChatId?.let { id ->
                pendingChatId = null
                openChat(id)
            }
        }
    }

    private fun startSelectedChatRefresh(session: Session, chatId: String) {
        selectedChatRefreshJob?.cancel()
        selectedChatRefreshJob = viewModelScope.launch {
            refreshHomeSilently()
            while (_uiState.value.session?.userId == session.userId && _uiState.value.selectedChatId == chatId) {
                delay(5000)
                refreshHomeSilently()
            }
        }
    }


    private suspend fun refreshSelectedChat(session: Session, chatId: String, reason: String) {
        // We now rely on WebSocket for real-time updates.
        // If a specific refresh is needed, we could send a targeted message sync request via socket.
    }

    private fun sumUnread(chats: List<ChatSummary>) = chats.sumOf { it.unreadCount }

    private fun handleMessageEditUpdate(chatId: String, messageId: String, newContent: String?, editedAt: Long?) {
        val updateFunc: (ChatMessage) -> ChatMessage = { msg ->
            if (msg.id == messageId) {
                msg.copy(
                    content = if (newContent != null) ir.hienob.noveo.data.parseMessageContent(newContent) else msg.content,
                    editedAt = editedAt ?: msg.editedAt
                )
            } else msg
        }

        messageCacheByChat[chatId] = messageCacheByChat[chatId].orEmpty().map(updateFunc)
        if (chatId == _uiState.value.selectedChatId) {
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages.map(updateFunc)
            )
        }
        persistCachedHomeState()
    }

    private fun handleMessagePinnedUpdate(chatId: String, pinnedMessage: ChatMessage?) {
        val updatedChats = _uiState.value.chats.map {
            if (it.id == chatId) it.copy(pinnedMessage = pinnedMessage) else it
        }
        
        // Also update individual message isPinned status in current list
        val pinId = pinnedMessage?.id
        val updateFunc: (ChatMessage) -> ChatMessage = { msg ->
            msg.copy(isPinned = (msg.id == pinId))
        }

        messageCacheByChat[chatId] = messageCacheByChat[chatId].orEmpty().map(updateFunc)
        
        val newState = _uiState.value.copy(
            chats = updatedChats,
            messages = if (chatId == _uiState.value.selectedChatId) {
                _uiState.value.messages.map(updateFunc)
            } else _uiState.value.messages
        )

        // The UI derives the selected chat object from selectedChatId and chats list.
        // Since we updated updatedChats above, the UI will see the new pinnedMessage automatically.
        _uiState.value = newState
        persistCachedHomeState()
    }

    private fun handleIncomingMessage(msg: ChatMessage) {
        val latestState = _uiState.value
        val session = latestState.session ?: return
        val baseMessages = if (msg.chatId == latestState.selectedChatId) {
            mergeMessages(messageCacheByChat[msg.chatId].orEmpty(), latestState.messages)
        } else {
            messageCacheByChat[msg.chatId].orEmpty()
        }
        val cachedMessages = mergeMessages(baseMessages, listOf(msg))
        messageCacheByChat[msg.chatId] = cachedMessages

        val currentChats = latestState.chats.toMutableList()
        val chatIndex = currentChats.indexOfFirst { it.id == msg.chatId }

        if (chatIndex != -1) {
            val chat = currentChats.removeAt(chatIndex)
            val updatedChat = chat.copy(
                lastMessagePreview = msg.content.previewText(),
                lastMessageTimestamp = msg.timestamp,
                unreadCount = when {
                    msg.chatId == latestState.selectedChatId -> 0
                    msg.senderId == session.userId -> chat.unreadCount
                    else -> chat.unreadCount + 1
                }
            )
            currentChats.add(0, updatedChat)
        } else {
            refreshHomeSilently()
        }

        val isSelectedChat = msg.chatId == latestState.selectedChatId
        val sortedChats = currentChats.sortedByDescending { it.lastMessageTimestamp }
        _uiState.value = latestState.copy(
            messages = if (isSelectedChat) cachedMessages else latestState.messages,
            chats = sortedChats,
            totalUnreadCount = sumUnread(sortedChats),
            messagesByChat = messageCacheByChat.toMap()
        )
        persistCachedHomeState()

        if (isSelectedChat && msg.senderId != session.userId) {
            markAsSeen(msg.id)
        }
    }

    private fun handleReactionUpdate(chatId: String, messageId: String, reactions: Map<String, List<String>>) {
        messageCacheByChat[chatId] = messageCacheByChat[chatId].orEmpty().map {
            if (it.id == messageId) it.copy(reactions = reactions) else it
        }
        if (chatId == _uiState.value.selectedChatId) {
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages.map {
                    if (it.id == messageId) it.copy(reactions = reactions) else it
                }
            )
        }
        persistCachedHomeState()
    }

    private fun handleMessageDelete(chatId: String, messageId: String) {
        messageCacheByChat[chatId] = messageCacheByChat[chatId].orEmpty().filter { it.id != messageId }
        if (chatId == _uiState.value.selectedChatId) {
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages.filter { it.id != messageId }
            )
        }
        persistCachedHomeState()
    }

    private fun handlePinUpdate(chatId: String, messageId: String, isPinned: Boolean) {
        val messages = messageCacheByChat[chatId].orEmpty()
        val pinnedMsg = if (isPinned) messages.find { it.id == messageId } else null
        
        val updateFunc: (ChatMessage) -> ChatMessage = { msg ->
            if (msg.id == messageId) msg.copy(isPinned = isPinned) else msg
        }

        val updatedMessages = messages.map(updateFunc)
        messageCacheByChat[chatId] = updatedMessages
        
        val updatedChats = _uiState.value.chats.map {
            if (it.id == chatId) it.copy(pinnedMessage = pinnedMsg) else it
        }

        // Force uiState update for current messages and chats
        _uiState.value = _uiState.value.copy(
            chats = updatedChats,
            messages = if (chatId == _uiState.value.selectedChatId) {
                updatedMessages
            } else _uiState.value.messages
        )
        persistCachedHomeState()
    }

    fun toggleReaction(messageId: String, emoji: String) {
        val chatId = _uiState.value.selectedChatId ?: return
        val resolvedEmoji = when (emoji) {
            "❤", "❤️", "â¤ï¸" -> _uiState.value.doubleTapReaction
            else -> emoji
        }
        val payload = org.json.JSONObject()
            .put("type", "toggle_reaction")
            .put("chatId", chatId)
            .put("messageId", messageId)
            .put("reaction", resolvedEmoji)
        NoveoNotificationService.send(payload)
    }

    fun editMessage(messageId: String, newText: String) {
        val chatId = _uiState.value.selectedChatId ?: return
        val payload = org.json.JSONObject()
            .put("type", "edit_message")
            .put("chatId", chatId)
            .put("messageId", messageId)
            .put("newContent", newText)
        NoveoNotificationService.send(payload)
    }

    fun deleteMessage(messageId: String) {
        val chatId = _uiState.value.selectedChatId ?: return
        val payload = org.json.JSONObject()
            .put("type", "delete_message")
            .put("chatId", chatId)
            .put("messageId", messageId)
        NoveoNotificationService.send(payload)
    }

    fun pinMessage(messageId: String, isPinned: Boolean) {
        val chatId = _uiState.value.selectedChatId ?: return
        val payload = org.json.JSONObject()
            .put("type", if (isPinned) "pin_message" else "unpin_message")
            .put("chatId", chatId)
        
        if (isPinned) {
            payload.put("messageId", messageId)
        }
        
        NoveoNotificationService.send(payload)
    }

    fun forwardMessage(message: ChatMessage, targetChatId: String) {
        val session = _uiState.value.session ?: return
        
        val content = org.json.JSONObject()
        message.content.text?.let { content.put("text", it) }
        message.content.file?.let { file ->
            val fileObj = org.json.JSONObject()
                .put("url", file.url)
                .put("name", file.name)
                .put("type", file.type)
            content.put("file", fileObj)
        }
        
        val forwardedInfo = org.json.JSONObject()
            .put("from", message.senderName)
            .put("originalTs", message.timestamp)
        content.put("forwardedInfo", forwardedInfo)

        val payload = org.json.JSONObject()
            .put("type", "message")
            .put("chatId", targetChatId)
            .put("content", content)
            .put("replyToId", null)
            
        NoveoNotificationService.send(payload)
        _uiState.value = _uiState.value.copy(forwardingMessage = null)
    }

    private fun handleTyping(chatId: String, userId: String) {
        val currentTyping = _uiState.value.typingUsers[chatId].orEmpty()
        _uiState.value = _uiState.value.copy(
            typingUsers = _uiState.value.typingUsers + (chatId to (currentTyping + userId))
        )
        
        val jobKey = "${chatId}_${userId}"
        typingJobs[jobKey]?.cancel()
        typingJobs[jobKey] = viewModelScope.launch {
            delay(3000)
            val stillTyping = _uiState.value.typingUsers[chatId].orEmpty() - userId
            _uiState.value = _uiState.value.copy(
                typingUsers = _uiState.value.typingUsers + (chatId to stillTyping)
            )
            typingJobs.remove(jobKey)
        }
    }

    private fun handleSeenUpdate(chatId: String, messageId: String, userId: String) {
        val session = _uiState.value.session
        messageCacheByChat[chatId] = messageCacheByChat[chatId].orEmpty().map {
            if (it.id == messageId) {
                it.copy(seenBy = (it.seenBy + userId).distinct())
            } else it
        }
        
        val updatedChats = if (userId == session?.userId) {
            _uiState.value.chats.map {
                if (it.id == chatId) it.copy(unreadCount = (it.unreadCount - 1).coerceAtLeast(0))
                else it
            }
        } else null

        if (chatId != _uiState.value.selectedChatId) {
            if (updatedChats != null) {
                _uiState.value = _uiState.value.copy(
                    chats = updatedChats,
                    totalUnreadCount = sumUnread(updatedChats)
                )
            }
            return
        }
        val messages = _uiState.value.messages.map {
            if (it.id == messageId) {
                it.copy(seenBy = (it.seenBy + userId).distinct())
            } else it
        }
        _uiState.value = _uiState.value.copy(
            messages = messages,
            chats = updatedChats ?: _uiState.value.chats,
            totalUnreadCount = updatedChats?.let { sumUnread(it) } ?: _uiState.value.totalUnreadCount
        )
        persistCachedHomeState()
    }
    
    fun searchPublicDirectory(query: String) {
        val session = _uiState.value.session ?: return
        val normalized = query.trim()
        if (normalized.length < 2) return
        viewModelScope.launch {
            runCatching {
                val (foundUsers, foundChats) = withContext(Dispatchers.IO) { api.searchPublicUsers(session, normalized) }
                _uiState.value = _uiState.value.copy(
                    usersById = _uiState.value.usersById + foundUsers.associateBy { it.id },
                    chats = (_uiState.value.chats + foundChats).distinctBy { it.id }.sortedByDescending { it.lastMessageTimestamp }
                )
            }
        }
    }

    fun updateProfile(username: String, bio: String) {
        val payload = org.json.JSONObject()
            .put("type", "update_profile")
            .put("username", username)
            .put("bio", bio)
        NoveoNotificationService.send(payload)
        // Optimistic update or wait for sync? The server should broadcast a user update.
        // For now, refresh home silently after a short delay
        viewModelScope.launch {
            delay(500)
            refreshHomeSilently()
        }
    }

    fun changePassword(old: String, new: String) {
        val payload = org.json.JSONObject()
            .put("type", "change_password")
            .put("oldPassword", old)
            .put("newPassword", new)
        NoveoNotificationService.send(payload)
    }

    fun deleteAccount(password: String) {
        val payload = org.json.JSONObject()
            .put("type", "delete_account")
            .put("password", password)
        NoveoNotificationService.send(payload)
    }

    fun startOutgoingCall(chatId: String) {
        val session = _uiState.value.session ?: return
        voiceChatManager.joinCall(session, chatId)
        viewModelScope.launch {
            val payload = org.json.JSONObject()
                .put("type", "voice_start")
                .put("chatId", chatId)
            NoveoNotificationService.send(payload)
        }
    }

    fun acceptCall(chatId: String, callId: String) {
        val session = _uiState.value.session ?: return
        _uiState.value = _uiState.value.copy(incomingCall = null)
        voiceChatManager.joinCall(session, chatId, callId)
    }

    fun showIncomingCall(chatId: String, callId: String, callerId: String) {
        _uiState.value = _uiState.value.copy(
            incomingCall = SocketEvent.IncomingCall(chatId, callerId, callId)
        )
    }

    fun declineCall() {
        val call = _uiState.value.incomingCall ?: return
        _uiState.value = _uiState.value.copy(incomingCall = null)
        viewModelScope.launch {
            val payload = org.json.JSONObject()
                .put("type", "voice_leave")
                .put("chatId", call.chatId)
                .put("callId", call.callId)
                .put("reason", "declined")
            NoveoNotificationService.send(payload)
        }
    }

    fun leaveCall() {
        val chatId = _uiState.value.voiceChatState.currentChatId ?: return
        voiceChatManager.leaveCall()
        viewModelScope.launch {
            val payload = org.json.JSONObject()
                .put("type", "voice_leave")
                .put("chatId", chatId)
                .put("reason", "left")
            NoveoNotificationService.send(payload)
        }
    }

    fun toggleMute() {
        voiceChatManager.toggleMute()
    }

    fun toggleDeafen() {
        voiceChatManager.toggleDeafen()
    }

    fun toggleMinimize() {
        voiceChatManager.toggleMinimize()
    }

    fun setLanguage(code: String) {
        val payload = org.json.JSONObject()
            .put("type", "update_profile")
            .put("languageCode", code)
        NoveoNotificationService.send(payload)
        sessionStore.writeLanguageCode(code)
        _uiState.value = _uiState.value.copy(languageCode = code)
        viewModelScope.launch {
            delay(500)
            refreshHomeSilently()
        }
    }

    private fun restoreCachedHomeState(cachedHomeState: CachedHomeState?) {
        messageCacheByChat.clear()
        if (cachedHomeState == null) return
        messageCacheByChat.putAll(cachedHomeState.messagesByChat.mapValues { (_, messages) ->
            messages.sortedBy { it.timestamp }
        })
        _uiState.value = _uiState.value.copy(
            usersById = cachedHomeState.usersById,
            onlineUserIds = cachedHomeState.onlineUserIds,
            chats = cachedHomeState.chats,
            messagesByChat = messageCacheByChat.toMap()
        )
    }

    private fun persistCachedHomeState() {
        val currentState = _uiState.value
        if (currentState.session == null) return
        sessionStore.writeCachedHomeState(
            CachedHomeState(
                usersById = currentState.usersById,
                onlineUserIds = currentState.onlineUserIds,
                chats = currentState.chats,
                messagesByChat = messageCacheByChat.mapValues { (_, messages) ->
                    messages.sortedBy { it.timestamp }
                }
            )
        )
    }

    private fun MessageFileAttachment.cacheExtension(): String {
        val nameExtension = name.substringAfterLast('.', "")
            .takeIf { it.isNotBlank() && it.length <= 8 }
            ?.lowercase()
        if (nameExtension != null) return nameExtension

        return when {
            isVideo() -> "mp4"
            type.equals("image/gif", true) -> "gif"
            type.equals("image/webp", true) -> "webp"
            type.equals("image/jpeg", true) -> "jpg"
            type.equals("image/png", true) -> "png"
            type.startsWith("image/", true) -> type.substringAfter('/').substringBefore(';').ifBlank { "img" }
            isTgsSticker() -> "tgs"
            else -> "bin"
        }
    }

    private fun getAttachmentFile(file: MessageFileAttachment): File {
        val extension = file.cacheExtension()
        val safeName = file.name
            .substringBeforeLast('.', file.name)
            .replace(Regex("[^A-Za-z0-9._-]"), "_")
            .take(40)
            .ifBlank { "attachment" }
        return File(getApplication<Application>().filesDir, "attachments/$safeName-${file.downloadKey()}.$extension")
    }

    private suspend fun cacheUploadedAttachment(file: MessageFileAttachment, attachment: PendingAttachment) {
        val targetFile = getAttachmentFile(file)
        withContext(Dispatchers.IO) {
            targetFile.parentFile?.mkdirs()
            targetFile.writeBytes(attachment.fileData)
        }
        withContext(Dispatchers.Main) {
            updateAttachmentDownload(
                key = file.downloadKey(),
                state = AttachmentDownloadState(localPath = targetFile.absolutePath, progress = 1f)
            )
        }
    }

    private fun openDownloadedFile(file: File, mimeTypeHint: String) {
        val context = getApplication<Application>()
        val uri = FileProvider.getUriForFile(context, "ir.hienob.noveo.updates.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, context.contentResolver.getType(uri) ?: mimeTypeHint.ifBlank { "*/*" })
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Open file").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun updateAttachmentDownload(key: String, state: AttachmentDownloadState) {
        _uiState.value = _uiState.value.copy(
            attachmentDownloads = _uiState.value.attachmentDownloads.toMutableMap().apply {
                this[key] = state
            }
        )
    }
}

private fun mergeMessages(existing: List<ChatMessage>, incoming: List<ChatMessage>): List<ChatMessage> {
    if (incoming.isEmpty()) return existing.sortedBy { it.timestamp }

    val merged = existing.toMutableList()
    for (message in incoming) {
        val existingIndex = if (message.clientTempId != null) {
            merged.indexOfFirst { it.clientTempId == message.clientTempId }
        } else if (message.id.isNotBlank()) {
            merged.indexOfFirst { it.id == message.id }
        } else {
            -1
        }

        if (existingIndex >= 0) {
            val old = merged[existingIndex]
            // Surgically merge fields to avoid corruption from partial server updates
            merged[existingIndex] = old.copy(
                id = if (message.id.isNotBlank()) message.id else old.id,
                content = if (message.content.text != null || message.content.file != null) message.content else old.content,
                senderName = if (message.senderName != "Unknown") message.senderName else old.senderName,
                reactions = if (message.reactions.isNotEmpty() || !message.pending) message.reactions else old.reactions,
                seenBy = if (message.seenBy.isNotEmpty()) message.seenBy else old.seenBy,
                isPinned = if (message.id.isNotBlank()) message.isPinned else old.isPinned,
                editedAt = if (message.editedAt != null) message.editedAt else old.editedAt,
                pending = message.pending,
                clientTempId = if (message.clientTempId != null) message.clientTempId else old.clientTempId
            )
            continue
        }

        val pendingIndex = if (message.pending) {
            -1
        } else {
            findPendingReplacementIndex(merged, message)
        }

        if (pendingIndex >= 0) {
            merged[pendingIndex] = message
        } else {
            merged.add(message)
        }
    }
    return dedupeMergedMessages(merged).sortedBy { it.timestamp }
}

private fun compareVersions(left: String, right: String): Int {
    val leftParts = left.split(Regex("[^0-9]+")).filter { it.isNotBlank() }.map { it.toIntOrNull() ?: 0 }
    val rightParts = right.split(Regex("[^0-9]+")).filter { it.isNotBlank() }.map { it.toIntOrNull() ?: 0 }
    val size = max(leftParts.size, rightParts.size)
    for (index in 0 until size) {
        val leftValue = leftParts.getOrElse(index) { 0 }
        val rightValue = rightParts.getOrElse(index) { 0 }
        if (leftValue != rightValue) return leftValue.compareTo(rightValue)
    }
    return 0
}

private fun findPendingReplacementIndex(messages: List<ChatMessage>, incoming: ChatMessage): Int {
    val pendingIndexes = messages.indices.filter { messages[it].pending }
    if (pendingIndexes.isEmpty()) return -1

    // 1. First try matching by signature (covers chatId, senderId, replyToId, and content)
    val exactSignatureIndex = pendingIndexes.firstOrNull { index ->
        messageMatchSignature(messages[index]) == messageMatchSignature(incoming)
    }
    if (exactSignatureIndex != null) return exactSignatureIndex

    // 2. Fallback to same chatId, senderId, and content with close timestamp
    val sameSenderCandidates = pendingIndexes.filter { index ->
        val current = messages[index]
        current.chatId == incoming.chatId &&
            current.senderId == incoming.senderId &&
            messageMatchSignature(current) == messageMatchSignature(incoming) &&
            isTimestampClose(current.timestamp, incoming.timestamp)
    }
    if (sameSenderCandidates.size == 1) return sameSenderCandidates.first()

    return -1
}

private fun dedupeMergedMessages(messages: List<ChatMessage>): List<ChatMessage> {
    val kept = mutableListOf<ChatMessage>()
    val seenIds = mutableMapOf<String, ChatMessage>()
    for (message in messages) {
        val id = message.id.takeIf { it.isNotBlank() }
        val existingById = id?.let(seenIds::get)
        if (existingById != null) {
            if (messageQualityScore(message) > messageQualityScore(existingById)) {
                val replaceIndex = kept.indexOf(existingById)
                if (replaceIndex >= 0) kept[replaceIndex] = message
                seenIds[id] = message
            }
            continue
        }

        val duplicatePendingIndex = kept.indexOfFirst { current ->
            current.pending != message.pending &&
                current.chatId == message.chatId &&
                current.senderId == message.senderId &&
                current.replyToId == message.replyToId &&
                messageMatchSignature(current) == messageMatchSignature(message) &&
                isTimestampClose(current.timestamp, message.timestamp)
        }
        if (duplicatePendingIndex >= 0) {
            if (messageQualityScore(message) >= messageQualityScore(kept[duplicatePendingIndex])) {
                kept[duplicatePendingIndex] = message
            }
            if (id != null) seenIds[id] = message
            continue
        }

        kept += message
        if (id != null) seenIds[id] = message
    }
    return kept
}

private fun messageMatchSignature(message: ChatMessage): String = buildString {
    append(message.chatId)
    append('|')
    append(message.senderId)
    append('|')
    append(message.replyToId.orEmpty())
    append('|')
    append(message.content.text.orEmpty().trim())
    append('|')
    append(message.content.file?.url.orEmpty())
    append('|')
    append(message.content.file?.name.orEmpty())
    append('|')
    append(message.content.file?.type.orEmpty())
    append('|')
    append(message.content.poll.orEmpty())
    append('|')
    append(message.content.theme.orEmpty())
    append('|')
    append(message.content.callLog.orEmpty())
}

private fun messageQualityScore(message: ChatMessage): Int {
    var score = 0
    if (!message.pending) score += 10
    if (!message.id.startsWith("temp-")) score += 5
    if (!message.clientTempId.isNullOrBlank()) score += 1
    return score
}

private fun isTimestampClose(left: Long, right: Long, maxDeltaSeconds: Long = 120L): Boolean {
    if (left <= 0L || right <= 0L) return false
    return kotlin.math.abs(left - right) <= maxDeltaSeconds
}
