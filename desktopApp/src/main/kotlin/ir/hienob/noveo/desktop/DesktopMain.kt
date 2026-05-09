package ir.hienob.noveo.desktop

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ir.hienob.noveo.core.ui.NoveoHomeFrame
import ir.hienob.noveo.core.ui.NoveoHomeFrameState
import ir.hienob.noveo.core.ui.NoveoHomeMessage
import ir.hienob.noveo.core.ui.NoveoPendingAttachment
import ir.hienob.noveo.core.ui.NoveoRootFrame
import ir.hienob.noveo.core.ui.NoveoRootFrameState
import ir.hienob.noveo.core.ui.NoveoStartupSurface
import ir.hienob.noveo.core.ui.NoveoThemePreset
import ir.hienob.noveo.core.ui.coreNoveoStrings
import ir.hienob.noveo.desktop.data.DesktopHomeSnapshot
import ir.hienob.noveo.desktop.data.DesktopNoveoApi
import ir.hienob.noveo.desktop.data.DesktopSession
import java.awt.Desktop
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.net.URI
import java.util.Properties
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun main() = application {
    val desktopState = DesktopStateHolder()
    Window(
        onCloseRequest = {
            desktopState.close()
            exitApplication()
        },
        title = "Noveo",
        state = rememberWindowState(size = DpSize(1200.dp, 800.dp)),
        resizable = true,
        icon = painterResource("icon.png")
    ) {
        val state by desktopState.state.collectAsState()
        val strings = coreNoveoStrings(state.root.languageCode)
        NoveoRootFrame(
            state = state.root,
            theme = NoveoThemePreset.SKY_LIGHT,
            strings = strings,
            onDismissOnboarding = desktopState::dismissOnboarding,
            onAuthMode = desktopState::setAuthMode,
            onStartRegisterCaptcha = { _, _ -> openNoveoWeb() },
            onAuthSubmit = desktopState::authenticate,
            onOpenRegistrationWeb = ::openNoveoWeb,
            homeContent = {
                NoveoHomeFrame(
                    state = state.home,
                    strings = strings,
                    onOpenChat = desktopState::openChat,
                    onBackToChats = desktopState::backToChats,
                    onSend = { text -> desktopState.sendMessage(text, null) },
                    onSendMessage = desktopState::sendMessage,
                    onEditMessage = desktopState::editMessage,
                    onToggleReaction = desktopState::toggleReaction,
                    onDeleteMessage = desktopState::deleteMessage,
                    onPinMessage = desktopState::pinMessage,
                    onForwardMessage = desktopState::forwardMessage,
                    onDownloadFile = desktopState::downloadFile,
                    onPickGalleryAttachment = { desktopState.pickAttachment(galleryOnly = true) },
                    onPickFileAttachment = { desktopState.pickAttachment(galleryOnly = false) },
                    onRemoveAttachment = desktopState::removeAttachment,
                    onCancelSend = desktopState::cancelSend,
                    onCreateChat = desktopState::createChat,
                    onUpdateProfile = desktopState::updateProfile,
                    onChangePassword = desktopState::changePassword,
                    onDeleteAccount = desktopState::deleteAccount,
                    onTyping = desktopState::sendTyping,
                    onJoinChat = desktopState::joinChat,
                    onLeaveChat = desktopState::leaveChat,
                    onRefresh = desktopState::refreshHome,
                    onLogout = desktopState::logout,
                    onSearchPublic = desktopState::searchPublic
                )
            }
        )
    }
}

private data class DesktopUiState(
    val root: NoveoRootFrameState = NoveoRootFrameState(
        startupSurface = NoveoStartupSurface.Splash,
        connectionTitle = "Noveo"
    ),
    val home: NoveoHomeFrameState = NoveoHomeFrameState()
)

private class DesktopStateHolder {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val api = DesktopNoveoApi()
    private val sessionStore = DesktopSessionStore()
    private var session: DesktopSession? = null
    private var messagesByChat: Map<String, List<NoveoHomeMessage>> = emptyMap()
    private var selectedAttachmentFile: File? = null
    private var selectedAttachmentMimeType: String = "application/octet-stream"
    private var activeSendJob: Job? = null
    private var lastPublicSearchQuery: String = ""

    private val _state = MutableStateFlow(DesktopUiState())
    val state = _state.asStateFlow()

    init { restoreSession() }

    fun close() {}

    fun dismissOnboarding() {
        _state.value = _state.value.copy(root = _state.value.root.copy(startupSurface = NoveoStartupSurface.Auth, error = null))
    }

    fun setAuthMode(signup: Boolean) {
        _state.value = _state.value.copy(root = _state.value.root.copy(authModeSignup = signup, error = null))
    }

    fun authenticate(handle: String, password: String) {
        if (handle.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(root = _state.value.root.copy(error = "Enter your username and password."))
            return
        }
        _state.value = _state.value.copy(root = _state.value.root.copy(loading = true, error = null))
        scope.launch {
            runCatching {
                val signedIn = withContext(Dispatchers.IO) { api.login(handle.trim(), password) }
                session = signedIn
                sessionStore.write(signedIn)
                val snapshot = withContext(Dispatchers.IO) { api.loadHome(signedIn) }
                applyHomeSnapshot(snapshot, selectedChatId = null)
            }.onFailure { error ->
                _state.value = _state.value.copy(root = _state.value.root.copy(
                    startupSurface = NoveoStartupSurface.Auth,
                    loading = false,
                    error = error.message ?: "Authentication failed"
                ))
            }
        }
    }

    fun openChat(chatId: String) {
        _state.value = _state.value.copy(home = _state.value.home.copy(
            selectedChatId = chatId,
            messages = messagesByChat[chatId].orEmpty(),
            error = null
        ))
    }

    fun backToChats() {
        _state.value = _state.value.copy(home = _state.value.home.copy(selectedChatId = null, messages = emptyList()))
    }

    fun refreshHome() {
        val currentSession = session ?: return
        val selectedChatId = _state.value.home.selectedChatId
        _state.value = _state.value.copy(home = _state.value.home.copy(loading = true, error = null))
        scope.launch {
            runCatching {
                val snapshot = withContext(Dispatchers.IO) { api.loadHome(currentSession) }
                applyHomeSnapshot(snapshot, selectedChatId = selectedChatId)
            }.onFailure { error ->
                _state.value = _state.value.copy(home = _state.value.home.copy(loading = false, error = error.message ?: "Refresh failed"))
            }
        }
    }

    fun searchPublic(query: String) {
        val currentSession = session ?: return
        val normalized = query.trim()
        if (normalized.length < 2 || normalized == lastPublicSearchQuery) return
        lastPublicSearchQuery = normalized
        scope.launch {
            runCatching {
                val foundChats = withContext(Dispatchers.IO) { api.searchPublic(currentSession, normalized) }
                if (foundChats.isNotEmpty()) {
                    val currentHome = _state.value.home
                    val mergedChats = (currentHome.chats + foundChats).distinctBy { it.id }
                    _state.value = _state.value.copy(home = currentHome.copy(chats = mergedChats, error = null))
                }
            }.onFailure { error ->
                _state.value = _state.value.copy(home = _state.value.home.copy(error = error.message ?: "Public search failed"))
            }
        }
    }

    fun pickAttachment(galleryOnly: Boolean) {
        val selected = runCatching {
            val dialog = FileDialog(null as Frame?, if (galleryOnly) "Select image or video" else "Select file", FileDialog.LOAD)
            if (galleryOnly) {
                dialog.filenameFilter = java.io.FilenameFilter { _, name ->
                    val lower = name.lowercase()
                    lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                        lower.endsWith(".webp") || lower.endsWith(".gif") || lower.endsWith(".mp4") ||
                        lower.endsWith(".webm") || lower.endsWith(".mov")
                }
            }
            dialog.isVisible = true
            val fileName = dialog.file ?: return
            File(dialog.directory, fileName).takeIf { it.isFile }
        }.getOrNull() ?: return

        selectedAttachmentFile = selected
        selectedAttachmentMimeType = inferMimeType(selected)
        _state.value = _state.value.copy(home = _state.value.home.copy(
            pendingAttachment = NoveoPendingAttachment(
                fileName = selected.name,
                mimeType = selectedAttachmentMimeType,
                sizeLabel = desktopFormatBytes(selected.length())
            ),
            error = null
        ))
    }

    fun removeAttachment() {
        selectedAttachmentFile = null
        selectedAttachmentMimeType = "application/octet-stream"
        _state.value = _state.value.copy(home = _state.value.home.copy(pendingAttachment = null))
    }

    fun cancelSend() {
        activeSendJob?.cancel()
        activeSendJob = null
        _state.value = _state.value.copy(home = _state.value.home.copy(
            messages = _state.value.home.messages.filterNot { it.pending },
            isSendingMessage = false,
            pendingAttachment = _state.value.home.pendingAttachment?.copy(isUploading = false, progress = 0f),
            error = null
        ))
    }

    fun sendTyping() {}

    fun joinChat(chatId: String) {
        val currentSession = session ?: return
        val currentHome = _state.value.home
        val optimisticChats = currentHome.chats.map { chat ->
            if (chat.id == chatId) chat.copy(memberIds = (chat.memberIds + currentSession.userId).distinct(), canChat = true)
            else chat
        }
        _state.value = _state.value.copy(home = currentHome.copy(chats = optimisticChats, selectedChatId = chatId, loading = true, error = null))
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) { api.joinChat(currentSession, chatId) }
                val snapshot = withContext(Dispatchers.IO) { api.loadHome(currentSession) }
                applyHomeSnapshot(snapshot, selectedChatId = chatId)
            }.onFailure { error ->
                _state.value = _state.value.copy(home = _state.value.home.copy(loading = false, error = error.message ?: "Unable to join chat"))
            }
        }
    }

    fun leaveChat(chatId: String) {
        val currentSession = session ?: return
        val currentHome = _state.value.home
        val optimisticChats = currentHome.chats.map { chat ->
            if (chat.id == chatId) chat.copy(memberIds = chat.memberIds.filterNot { it == currentSession.userId }) else chat
        }
        _state.value = _state.value.copy(home = currentHome.copy(
            chats = optimisticChats,
            selectedChatId = if (currentHome.selectedChatId == chatId) null else currentHome.selectedChatId,
            messages = if (currentHome.selectedChatId == chatId) emptyList() else currentHome.messages,
            loading = true, error = null
        ))
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) { api.leaveChat(currentSession, chatId) }
                val snapshot = withContext(Dispatchers.IO) { api.loadHome(currentSession) }
                applyHomeSnapshot(snapshot, selectedChatId = null)
            }.onFailure { error ->
                _state.value = _state.value.copy(home = _state.value.home.copy(loading = false, error = error.message ?: "Unable to leave chat"))
            }
        }
    }

    fun sendMessage(text: String, replyToId: String?) {
        val currentSession = session ?: return
        val chatId = _state.value.home.selectedChatId ?: return
        val selectedChat = _state.value.home.chats.firstOrNull { it.id == chatId } ?: return
        val isMember = selectedChat.memberIds.contains(currentSession.userId)
        val canWrite = _state.value.home.canSendMessage && selectedChat.canChat &&
            (selectedChat.chatType == "private" || isMember)
        if (!canWrite) return
        val attachmentFile = selectedAttachmentFile
        val attachmentMimeType = selectedAttachmentMimeType
        if (text.isBlank() && attachmentFile == null) return
        val pendingAttachment = _state.value.home.pendingAttachment
        val replySource = replyToId?.let { id -> _state.value.home.messages.firstOrNull { it.id == id } }
        val pendingMessage = NoveoHomeMessage(
            id = "pending-${System.currentTimeMillis()}",
            senderId = currentSession.userId,
            senderName = "You",
            text = text,
            isOutgoing = true,
            pending = true,
            attachmentName = pendingAttachment?.fileName,
            attachmentType = pendingAttachment?.mimeType,
            attachmentSizeLabel = pendingAttachment?.sizeLabel,
            replyAuthor = replySource?.senderName,
            replyPreview = replySource?.text?.ifBlank { replySource.attachmentName.orEmpty() }
        )
        _state.value = _state.value.copy(home = _state.value.home.copy(
            messages = _state.value.home.messages + pendingMessage,
            isSendingMessage = true,
            pendingAttachment = pendingAttachment?.copy(isUploading = attachmentFile != null, progress = 0f),
            error = null
        ))
        activeSendJob?.cancel()
        activeSendJob = scope.launch {
            runCatching {
                val uploadedFile = if (attachmentFile != null) {
                    withContext(Dispatchers.IO) {
                        api.uploadFile(currentSession, attachmentFile, attachmentMimeType) { progress ->
                            _state.value = _state.value.copy(home = _state.value.home.copy(
                                pendingAttachment = _state.value.home.pendingAttachment?.copy(progress = progress.coerceIn(0f, 1f), isUploading = true)
                            ))
                        }
                    }
                } else null
                withContext(Dispatchers.IO) { api.sendMessage(currentSession, chatId, text, replyToId, uploadedFile) }
                selectedAttachmentFile = null
                selectedAttachmentMimeType = "application/octet-stream"
                val snapshot = withContext(Dispatchers.IO) { api.loadHome(currentSession) }
                applyHomeSnapshot(snapshot, selectedChatId = chatId)
            }.onFailure { error ->
                if (error is CancellationException) return@onFailure
                _state.value = _state.value.copy(home = _state.value.home.copy(
                    messages = _state.value.home.messages.filterNot { it.id == pendingMessage.id },
                    isSendingMessage = false,
                    pendingAttachment = _state.value.home.pendingAttachment?.copy(isUploading = false, progress = 0f),
                    error = error.message ?: "Send failed"
                ))
            }
            activeSendJob = null
        }
    }

    fun editMessage(messageId: String, newText: String) {
        val currentSession = session ?: return
        val chatId = _state.value.home.selectedChatId ?: return
        _state.value = _state.value.copy(home = _state.value.home.copy(
            messages = _state.value.home.messages.map { if (it.id == messageId) it.copy(text = newText, edited = true) else it },
            error = null
        ))
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) { api.editMessage(currentSession, chatId, messageId, newText) }
                val snapshot = withContext(Dispatchers.IO) { api.loadHome(currentSession) }
                applyHomeSnapshot(snapshot, selectedChatId = chatId)
            }.onFailure { error ->
                _state.value = _state.value.copy(home = _state.value.home.copy(error = error.message ?: "Edit failed"))
            }
        }
    }

    fun toggleReaction(messageId: String, emoji: String) {
        val currentSession = session ?: return
        val chatId = _state.value.home.selectedChatId ?: return
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) { api.toggleReaction(currentSession, chatId, messageId, emoji) }
                val snapshot = withContext(Dispatchers.IO) { api.loadHome(currentSession) }
                applyHomeSnapshot(snapshot, selectedChatId = chatId)
            }.onFailure { error ->
                _state.value = _state.value.copy(home = _state.value.home.copy(error = error.message ?: "Reaction failed"))
            }
        }
    }

    fun deleteMessage(messageId: String) {
        val currentSession = session ?: return
        val chatId = _state.value.home.selectedChatId ?: return
        _state.value = _state.value.copy(home = _state.value.home.copy(
            messages = _state.value.home.messages.filterNot { it.id == messageId },
            error = null
        ))
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) { api.deleteMessage(currentSession, chatId, messageId) }
                val snapshot = withContext(Dispatchers.IO) { api.loadHome(currentSession) }
                applyHomeSnapshot(snapshot, selectedChatId = chatId)
            }.onFailure { error ->
                _state.value = _state.value.copy(home = _state.value.home.copy(error = error.message ?: "Delete failed"))
            }
        }
    }

    fun pinMessage(messageId: String, pin: Boolean) {
        val currentSession = session ?: return
        val chatId = _state.value.home.selectedChatId ?: return
        _state.value = _state.value.copy(home = _state.value.home.copy(
            messages = _state.value.home.messages.map { msg ->
                if (msg.id == messageId) msg.copy(isPinned = pin) else if (pin) msg.copy(isPinned = false) else msg
            },
            error = null
        ))
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) { api.pinMessage(currentSession, chatId, messageId, pin) }
                val snapshot = withContext(Dispatchers.IO) { api.loadHome(currentSession) }
                applyHomeSnapshot(snapshot, selectedChatId = chatId)
            }.onFailure { error ->
                _state.value = _state.value.copy(home = _state.value.home.copy(error = error.message ?: "Pin failed"))
            }
        }
    }

    fun forwardMessage(messageId: String, targetChatId: String) {
        val currentSession = session ?: return
        val sourceChatId = _state.value.home.selectedChatId ?: return
        val sourceMessage = _state.value.home.messages.firstOrNull { it.id == messageId } ?: return
        _state.value = _state.value.copy(home = _state.value.home.copy(loading = true, error = null))
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) { api.forwardMessage(currentSession, targetChatId, sourceMessage) }
                val snapshot = withContext(Dispatchers.IO) { api.loadHome(currentSession) }
                applyHomeSnapshot(snapshot, selectedChatId = sourceChatId)
            }.onFailure { error ->
                _state.value = _state.value.copy(home = _state.value.home.copy(loading = false, error = error.message ?: "Forward failed"))
            }
        }
    }

    fun downloadFile(messageId: String) {
        val message = _state.value.home.messages.firstOrNull { it.id == messageId } ?: return
        val attachmentUrl = message.attachmentUrl.orEmpty()
        val attachmentName = message.attachmentName?.ifBlank { null }
        if (attachmentUrl.isBlank()) return
        scope.launch(Dispatchers.IO) {
            runCatching {
                val downloadsDir = File(System.getProperty("user.home"), "Downloads/Noveo").also { it.mkdirs() }
                val fileName = attachmentName ?: attachmentUrl.substringAfterLast('/').ifBlank { "noveo_file_${System.currentTimeMillis()}" }
                val destFile = File(downloadsDir, fileName)
                val url = java.net.URL(attachmentUrl)
                url.openStream().use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(home = _state.value.home.copy(
                        error = "Saved to ${destFile.absolutePath}"
                    ))
                }
                // Also open the folder so user can see it
                if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(downloadsDir)
            }.onFailure { error ->
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(home = _state.value.home.copy(
                        error = error.message ?: "Download failed"
                    ))
                }
            }
        }
    }

    fun createChat(name: String, type: String, handle: String?, bio: String?) {
        val currentSession = session ?: return
        _state.value = _state.value.copy(home = _state.value.home.copy(loading = true, error = null))
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) { api.createChat(currentSession, name, type, handle, bio) }
                val snapshot = withContext(Dispatchers.IO) { api.loadHome(currentSession) }
                val createdChat = snapshot.chats.firstOrNull { it.title.equals(name, ignoreCase = true) }
                applyHomeSnapshot(snapshot, selectedChatId = createdChat?.id ?: _state.value.home.selectedChatId)
            }.onFailure { error ->
                _state.value = _state.value.copy(home = _state.value.home.copy(loading = false, error = error.message ?: "Chat creation failed"))
            }
        }
    }

    fun updateProfile(username: String, bio: String) {
        val currentSession = session ?: return
        _state.value = _state.value.copy(home = _state.value.home.copy(loading = true, error = null))
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) { api.updateProfile(currentSession, username, bio) }
                val snapshot = withContext(Dispatchers.IO) { api.loadHome(currentSession) }
                applyHomeSnapshot(snapshot, selectedChatId = _state.value.home.selectedChatId)
            }.onFailure { error ->
                _state.value = _state.value.copy(home = _state.value.home.copy(loading = false, error = error.message ?: "Profile update failed"))
            }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        val currentSession = session ?: return
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) { api.changePassword(currentSession, oldPassword, newPassword) }
            }.onFailure { error ->
                _state.value = _state.value.copy(home = _state.value.home.copy(error = error.message ?: "Password change failed"))
            }
        }
    }

    fun deleteAccount(password: String) {
        val currentSession = session ?: return
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) { api.deleteAccount(currentSession, password) }
                logout()
            }.onFailure { error ->
                _state.value = _state.value.copy(home = _state.value.home.copy(error = error.message ?: "Account deletion failed"))
            }
        }
    }

    fun logout() {
        session = null
        messagesByChat = emptyMap()
        sessionStore.clear()
        _state.value = DesktopUiState(root = NoveoRootFrameState(startupSurface = NoveoStartupSurface.Auth, connectionTitle = "Noveo"))
    }

    private fun restoreSession() {
        scope.launch {
            val stored = sessionStore.read()
            if (stored == null) {
                _state.value = _state.value.copy(root = _state.value.root.copy(startupSurface = NoveoStartupSurface.Onboarding, loading = false))
                return@launch
            }
            session = stored
            _state.value = _state.value.copy(root = _state.value.root.copy(startupSurface = NoveoStartupSurface.Splash, loading = true))
            runCatching {
                val snapshot = withContext(Dispatchers.IO) { api.loadHome(stored) }
                applyHomeSnapshot(snapshot, selectedChatId = null)
            }.onFailure {
                sessionStore.clear()
                session = null
                _state.value = _state.value.copy(root = _state.value.root.copy(
                    startupSurface = NoveoStartupSurface.Auth,
                    loading = false,
                    error = it.message ?: "Session restore failed"
                ))
            }
        }
    }

    private fun applyHomeSnapshot(snapshot: DesktopHomeSnapshot, selectedChatId: String?) {
        session = snapshot.session
        messagesByChat = snapshot.messagesByChat
        val actualSelectedId = selectedChatId?.takeIf { id -> snapshot.chats.any { it.id == id } }
        _state.value = _state.value.copy(
            root = _state.value.root.copy(startupSurface = NoveoStartupSurface.Home, loading = false, error = null),
            home = NoveoHomeFrameState(
                currentUserId = snapshot.session.userId,
                currentUsername = snapshot.currentUsername,
                currentUserBio = snapshot.currentUserBio,
                chats = snapshot.chats,
                selectedChatId = actualSelectedId,
                messages = actualSelectedId?.let { messagesByChat[it] }.orEmpty(),
                totalUnreadCount = snapshot.totalUnreadCount,
                loading = false,
                isSendingMessage = false,
                pendingAttachment = null
            )
        )
    }
}

private class DesktopSessionStore {
    private val file: File = File(System.getProperty("user.home"), ".noveo/session.properties")

    fun read(): DesktopSession? = runCatching {
        if (!file.exists()) return null
        val properties = Properties()
        file.inputStream().use(properties::load)
        val userId = properties.getProperty("userId").orEmpty()
        val token = properties.getProperty("token").orEmpty()
        if (userId.isBlank() || token.isBlank()) return null
        DesktopSession(userId = userId, token = token,
            sessionId = properties.getProperty("sessionId").orEmpty(),
            expiresAt = properties.getProperty("expiresAt")?.toLongOrNull() ?: 0L)
    }.getOrNull()

    fun write(session: DesktopSession) {
        file.parentFile?.mkdirs()
        val properties = Properties().apply {
            setProperty("userId", session.userId)
            setProperty("token", session.token)
            setProperty("sessionId", session.sessionId)
            setProperty("expiresAt", session.expiresAt.toString())
        }
        file.outputStream().use { properties.store(it, "Noveo desktop session") }
    }

    fun clear() { runCatching { file.delete() } }
}

private fun inferMimeType(file: File): String = when (file.extension.lowercase()) {
    "jpg", "jpeg" -> "image/jpeg"
    "png" -> "image/png"
    "gif" -> "image/gif"
    "webp" -> "image/webp"
    "mp4" -> "video/mp4"
    "webm" -> "video/webm"
    "mov" -> "video/quicktime"
    "mp3" -> "audio/mpeg"
    "m4a" -> "audio/mp4"
    "wav" -> "audio/wav"
    "ogg" -> "audio/ogg"
    "pdf" -> "application/pdf"
    "txt" -> "text/plain"
    else -> "application/octet-stream"
}

private fun desktopFormatBytes(size: Long): String {
    if (size <= 0L) return ""
    val units = arrayOf("B", "KB", "MB", "GB")
    var value = size.toDouble()
    var unit = 0
    while (value >= 1024.0 && unit < units.lastIndex) { value /= 1024.0; unit++ }
    return if (unit == 0) "$size ${units[unit]}" else String.format(java.util.Locale.US, "%.1f %s", value, units[unit])
}

private fun openNoveoWeb() {
    runCatching { if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(URI("https://web.noveo.ir")) }
}
