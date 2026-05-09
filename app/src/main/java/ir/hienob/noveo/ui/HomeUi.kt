package ir.hienob.noveo.ui


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Headset
import androidx.compose.material.icons.outlined.HeadsetOff
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MicOff
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.material.icons.outlined.Collections
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.asPaddingValues
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.lerp as lerpDp
import androidx.compose.ui.text.lerp as lerpTextStyle
import androidx.compose.ui.util.lerp as lerpFloat
import coil3.compose.AsyncImage
import java.io.File
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat
import ir.hienob.noveo.R
import ir.hienob.noveo.app.AppUiState
import ir.hienob.noveo.data.ChatMessage
import ir.hienob.noveo.data.ChatSummary
import ir.hienob.noveo.data.MessageFileAttachment
import ir.hienob.noveo.data.NotificationSettings
import ir.hienob.noveo.data.SavedSticker
import ir.hienob.noveo.data.Session
import ir.hienob.noveo.data.SocketEvent
import ir.hienob.noveo.data.UserSummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope
import kotlin.math.roundToInt
import androidx.compose.ui.res.pluralStringResource

private const val NOVEO_BASE_URL = "https://noveo.ir:8443"
private val CLIENT_VERSION: String
    get() = "v${ir.hienob.noveo.BuildConfig.VERSION_NAME} Kotlin"

private data class SelectedMediaAttachment(
    val attachment: MessageFileAttachment,
    val localPath: String
)

private fun ChatSummary.isSavedMessagesChat(currentUserId: String?): Boolean =
    id.startsWith("saved_") ||
        avatarUrl == "saved_messages" ||
        title == "Saved Messages" ||
        (chatType == "private" && currentUserId != null && memberIds.size == 1 && memberIds.firstOrNull() == currentUserId)

private fun localAttachmentCacheFile(root: File, file: MessageFileAttachment): File {
    val extension = file.name.substringAfterLast('.', "").ifBlank {
        when {
            file.isVideo() -> "mp4"
            file.type.equals("image/gif", true) -> "gif"
            file.type.equals("image/webp", true) -> "webp"
            file.type.equals("image/jpeg", true) -> "jpg"
            file.isTgsSticker() -> "tgs"
            else -> "bin"
        }
    }
    val safeName = file.name
        .substringBeforeLast('.', file.name)
        .replace(Regex("[^A-Za-z0-9._-]"), "_")
        .take(40)
        .ifBlank { "attachment" }
    return File(root, "attachments/$safeName-${file.downloadKey()}.$extension")
}

private fun formatLastSeen(lastSeen: Long?, strings: NoveoStrings): String {
    if (lastSeen == null || lastSeen <= 0) return strings.lastSeenRecently
    
    val now = Calendar.getInstance()
    val date = Calendar.getInstance().apply { timeInMillis = lastSeen * 1000L }
    
    val diffMillis = now.timeInMillis - date.timeInMillis
    val diffSeconds = diffMillis / 1000
    val diffMinutes = diffSeconds / 60
    val diffHours = diffMinutes / 60
    val diffDays = diffHours / 24
    
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeStr = timeFormat.format(date.time)
    
    return when {
        diffSeconds < 60 -> strings.justNow
        diffMinutes < 60 -> strings.minutesAgo.format(diffMinutes)
        now.get(Calendar.YEAR) == date.get(Calendar.YEAR) && now.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) -> "${strings.lastSeenAt} $timeStr"
        else -> {
            val yesterday = Calendar.getInstance().apply { 
                timeInMillis = now.timeInMillis
                add(Calendar.DAY_OF_YEAR, -1)
            }
            if (yesterday.get(Calendar.YEAR) == date.get(Calendar.YEAR) && yesterday.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)) {
                "${strings.lastSeenYesterday} $timeStr"
            } else if (diffDays < 7) {
                strings.lastSeenDaysAgo.format(diffDays)
            } else if (diffDays < 30) {
                val weeks = diffDays / 7
                if (weeks <= 1) strings.lastSeenWeekAgo else strings.lastSeenWeeksAgo.format(weeks)
            } else if (diffDays < 365) {
                val months = diffDays / 30
                if (months <= 1) strings.lastSeenMonthAgo else strings.lastSeenMonthsAgo.format(months)
            } else {
                strings.lastSeenLongTimeAgo
            }
        }
    }
}

@Immutable
class TelegramBubbleShape(
    val isOutgoing: Boolean,
    val hasTail: Boolean,
    val cornerRadius: Float = 48f
) : androidx.compose.ui.graphics.Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): androidx.compose.ui.graphics.Outline {
        val path = androidx.compose.ui.graphics.Path().apply {
            val w = size.width
            val h = size.height
            val r = cornerRadius
            val tr = 6f * density.density
            
            if (isOutgoing) {
                moveTo(r, 0f)
                lineTo(w - r, 0f)
                quadraticTo(w, 0f, w, r)
                
                if (hasTail) {
                    lineTo(w, h - r)
                    lineTo(w, h - 10f * density.density)
                    cubicTo(w, h, w + tr, h, w + tr, h)
                    lineTo(w - r, h)
                } else {
                    lineTo(w, h - r)
                    quadraticTo(w, h, w - r, h)
                }
                
                lineTo(r, h)
                quadraticTo(0f, h, 0f, h - r)
                lineTo(0f, r)
                quadraticTo(0f, 0f, r, 0f)
            } else {
                moveTo(r, 0f)
                lineTo(w - r, 0f)
                quadraticTo(w, 0f, w, r)
                lineTo(w, h - r)
                quadraticTo(w, h, w - r, h)
                lineTo(r, h)
                
                if (hasTail) {
                    lineTo(10f * density.density, h)
                    cubicTo(0f, h, -tr, h, -tr, h)
                    lineTo(0f, h - 10f * density.density)
                } else {
                    quadraticTo(0f, h, 0f, h - r)
                }
                lineTo(0f, r)
                quadraticTo(0f, 0f, r, 0f)
            }
            close()
        }
        return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}

private enum class SettingsSection {
    MENU, SUBSCRIPTION, PROFILE, ACCOUNT, PREFERENCES, THEME, NOTIFICATIONS
}

private data class ThemeSection(
    val title: String,
    val subtitle: String,
    val presets: List<ThemePreset>
)

@Composable
internal fun HomeScreen(
    state: AppUiState,
    onOpenChat: (String) -> Unit,
    onStartDirectChat: (String) -> Unit,
    onStartCreateChat: (String, String, String?, String?) -> Unit,
    onSearchPublic: (String) -> Unit,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    onTyping: () -> Unit,
    onLogout: () -> Unit,
    onAttachFile: (android.net.Uri) -> Unit,
    onRemoveAttachment: () -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    onLoadOlder: () -> Unit,
    onReply: (ChatMessage?) -> Unit,
    onEditMessage: (ChatMessage?) -> Unit,
    onForwardMessage: (ChatMessage?) -> Unit,
    onToggleReaction: (String, String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onPinMessage: (String, Boolean) -> Unit,
    onChangePassword: (String, String) -> Unit,
    onDeleteAccount: (String) -> Unit,
    onSetLanguage: (String) -> Unit,
    onDismissUpdate: () -> Unit,
    onDownloadUpdate: () -> Unit,
    onInstallUpdate: () -> Unit,
    onCheckUpdate: () -> Unit,
    onSetBetaUpdatesEnabled: (Boolean) -> Unit,
    onSetDoubleTapReaction: (String) -> Unit,
    onUpdateNotificationSettings: (NotificationSettings) -> Unit,
    onRequestBatteryOptimization: () -> Unit,
    onPlayAudio: (ChatMessage) -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    onStopAudio: () -> Unit,
    onSeekAudio: (Float) -> Unit,
    onDownloadFile: (ChatMessage) -> Unit,
    onCancelDownload: (ChatMessage) -> Unit,
    onCall: (String) -> Unit,
    onAcceptCall: (String, String) -> Unit,
    onDeclineCall: () -> Unit,
    onLeaveCall: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleDeafen: () -> Unit,
    onToggleMinimize: () -> Unit,
    onCancelUpload: (String) -> Unit,
    onSendSticker: (SavedSticker) -> Unit,
    onAddSavedSticker: (ChatMessage) -> Unit,
    onHandleClick: (String) -> Unit,
    onJoinChat: (String) -> Unit,
    onLeaveChat: (String) -> Unit,
    onClearNavigationSignal: () -> Unit,
    onBotCallback: (String, String, String) -> Unit,
    currentTheme: ThemePreset,
    onThemeChange: (ThemePreset) -> Unit,
    onForwardConfirm: (ChatMessage, String) -> Unit = { _, _ -> }
) {
    val strings = getStrings(state.languageCode)
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        initializeTgsSupport(context)
    }

    var showMenu by rememberSaveable { mutableStateOf(false) }
    var showSearch by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showContactsModal by rememberSaveable { mutableStateOf(false) }
    var showCreateModal by rememberSaveable { mutableStateOf(false) }
    var showSettingsModal by rememberSaveable { mutableStateOf(false) }
    var settingsSection by rememberSaveable { mutableStateOf(SettingsSection.MENU) }
    var profileUserId by rememberSaveable { mutableStateOf<String?>(null) }
    var infoChatId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedMediaAttachment by remember { mutableStateOf<SelectedMediaAttachment?>(null) }
    var animateModalEntrance by remember { mutableStateOf(false) }

    val onOpenProfile = { userId: String ->
        profileUserId = userId
        animateModalEntrance = true
    }

    val keyboardHeight = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    var lastKeyboardHeight by remember { mutableStateOf(300.dp) }
    LaunchedEffect(keyboardHeight) {
        if (keyboardHeight > 0.dp) {
            lastKeyboardHeight = keyboardHeight
        }
    }

    val onMediaClick = { message: ChatMessage, attachment: MessageFileAttachment ->
        val localPath = state.attachmentDownloads[attachment.downloadKey()]?.localPath
            ?: localAttachmentCacheFile(context.filesDir, attachment).takeIf { it.exists() }?.absolutePath
        if (!localPath.isNullOrBlank() && (attachment.isImage() || attachment.isVideo())) {
            selectedMediaAttachment = SelectedMediaAttachment(attachment = attachment, localPath = localPath)
        } else if (attachment.isImage() || attachment.isVideo()) {
            onDownloadFile(message)
        } else {
            val url = attachment.url.normalizeNoveoUrl()
            if (url != null) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
                    context.startActivity(intent)
                } catch (_: Exception) {
                }
            }
        }
    }

    var showForwardPicker by remember { mutableStateOf(false) }
    
    LaunchedEffect(state.forwardingMessage) {
        showForwardPicker = state.forwardingMessage != null
    }

    LaunchedEffect(state.pendingProfileId) {
        state.pendingProfileId?.let {
            profileUserId = it
            animateModalEntrance = true
            onClearNavigationSignal()
        }
    }

    LaunchedEffect(state.pendingGroupInfoId) {
        state.pendingGroupInfoId?.let {
            val chat = state.chats.firstOrNull { c -> c.id == it }
            if (chat?.chatType == "private") {
                val otherUserId = chat.memberIds.firstOrNull { memberId -> memberId != state.session?.userId }
                infoChatId = null
                otherUserId?.let { userId ->
                    profileUserId = userId
                    animateModalEntrance = true
                }
            } else {
                infoChatId = it
                animateModalEntrance = true
            }
            onClearNavigationSignal()
        }
    }

    val isAnyModalVisible = showContactsModal || showCreateModal || showSettingsModal ||
                          infoChatId != null ||
                          profileUserId != null || selectedMediaAttachment != null || showSearch || showForwardPicker

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onUpdateNotificationSettings(state.notificationSettings.copy(enabled = true))
        }
    }

    val density = LocalDensity.current
    val menuWidth = 296.dp
    val menuWidthPx = with(density) { menuWidth.toPx() }
    val backSwipeEdgePx = with(density) { 32.dp.toPx() }
    
    // Core offsets
    val sidebarOffset = remember { androidx.compose.animation.core.Animatable(-menuWidthPx) }
    val chatBackOffset = remember { androidx.compose.animation.core.Animatable(0f) }
    
    val scope = rememberCoroutineScope()
    val rootView = LocalView.current
    var chatSnapshot by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    var chatSnapshotBounds by remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }
    var chatSnapshotCapturedForGesture by remember { mutableStateOf(false) }
    var lockedSlidingChatId by remember { mutableStateOf<String?>(null) }
    var lockedSlidingChat by remember { mutableStateOf<ChatSummary?>(null) }
    var lockedSlidingMessages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var isBackSwipeDragging by remember { mutableStateOf(false) }
    var isCompletingBackSwipe by remember { mutableStateOf(false) }
    var lastCompactSelectedChatId by remember { mutableStateOf<String?>(null) }
    var openingChatId by remember { mutableStateOf<String?>(null) }
    var directChatDragOffset by remember { mutableStateOf<Float?>(null) }

    // Sync menu state with animation
    LaunchedEffect(showMenu) {
        if (showMenu) {
            sidebarOffset.animateTo(0f, tween(250, easing = FastOutSlowInEasing))
        } else {
            sidebarOffset.animateTo(-menuWidthPx, tween(250, easing = FastOutSlowInEasing))
        }
    }

    LaunchedEffect(state.selectedChatId) {
        if (isBackSwipeDragging || isCompletingBackSwipe || openingChatId != null) return@LaunchedEffect
        chatSnapshot = null
        chatSnapshotCapturedForGesture = false
        if (state.selectedChatId == null) {
            chatBackOffset.snapTo(0f)
            directChatDragOffset = null
            lockedSlidingChatId = null
            lockedSlidingChat = null
            lockedSlidingMessages = emptyList()
        }
    }

    val filteredChats = remember(state.chats, searchQuery) {
        state.chats.filter {
            searchQuery.isBlank() ||
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.lastMessagePreview.contains(searchQuery, ignoreCase = true) ||
                (it.handle?.contains(searchQuery, ignoreCase = true) == true)
        }
    }
    val filteredUsers = remember(state.usersById, searchQuery, state.session?.userId) {
        state.usersById.values
            .filter { it.id != state.session?.userId }
            .sortedBy { it.username.lowercase() }
            .filter {
                searchQuery.isBlank() ||
                    it.username.contains(searchQuery, ignoreCase = true) ||
                    (it.handle?.contains(searchQuery, ignoreCase = true) == true) ||
                    it.bio.contains(searchQuery, ignoreCase = true)
            }
    }

    val effectiveSelectedChatId = lockedSlidingChatId ?: state.selectedChatId
    val selectedChat = state.chats.firstOrNull { it.id == effectiveSelectedChatId }
    val effectiveSelectedChat = lockedSlidingChat ?: selectedChat
    val effectiveChatState = if (lockedSlidingChat != null) state.copy(selectedChatId = lockedSlidingChatId, messages = lockedSlidingMessages) else state
    val latestState by rememberUpdatedState(state)
    val latestSelectedChat by rememberUpdatedState(selectedChat)
    val latestOnBackToChats by rememberUpdatedState(onBackToChats)
    val selectedProfile = remember(profileUserId, state.usersById) { profileUserId?.let(state.usersById::get) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        val compact = maxWidth < 760.dp
        val screenWidthPx = with(density) { maxWidth.toPx() }

        fun clearBackSwipeVisualState() {
            chatSnapshotCapturedForGesture = false
            chatSnapshot = null
            lockedSlidingChatId = null
            lockedSlidingChat = null
            lockedSlidingMessages = emptyList()
            isBackSwipeDragging = false
            isCompletingBackSwipe = false
            directChatDragOffset = null
        }

        fun lockCurrentChatForBack(currentState: AppUiState = latestState): Boolean {
            val currentSelectedId = currentState.selectedChatId ?: return false
            val currentSelectedChat = latestSelectedChat?.takeIf { it.id == currentSelectedId }
                ?: currentState.chats.firstOrNull { it.id == currentSelectedId }
                ?: return false
            lockedSlidingChatId = currentSelectedId
            lockedSlidingChat = currentSelectedChat
            lockedSlidingMessages = currentState.messages.toList()
            return true
        }

        suspend fun finishCompactBackNavigation(targetWidth: Float) {
            if (isCompletingBackSwipe) return
            isBackSwipeDragging = false
            isCompletingBackSwipe = true
            openingChatId = null
            sidebarOffset.stop()
            chatBackOffset.stop()
            val startingOffset = directChatDragOffset ?: chatBackOffset.value
            directChatDragOffset = null
            chatBackOffset.snapTo(startingOffset.coerceAtLeast(0f))
            chatBackOffset.animateTo(
                targetWidth.coerceAtLeast(chatBackOffset.value),
                tween(260, easing = FastOutSlowInEasing)
            )
            latestOnBackToChats()
            lastCompactSelectedChatId = null
            chatBackOffset.snapTo(0f)
            clearBackSwipeVisualState()
        }

        fun startCompactBackNavigation() {
            if (compact) {
                if (lockedSlidingChat != null || lockCurrentChatForBack(latestState)) {
                    scope.launch { finishCompactBackNavigation(screenWidthPx) }
                } else {
                    latestOnBackToChats()
                }
            } else {
                latestOnBackToChats()
            }
        }

        LaunchedEffect(compact, state.selectedChatId, screenWidthPx) {
            val currentChatId = state.selectedChatId
            if (!compact) {
                openingChatId = null
                directChatDragOffset = null
                lastCompactSelectedChatId = currentChatId
                return@LaunchedEffect
            }
            if (isBackSwipeDragging || isCompletingBackSwipe || lockedSlidingChat != null) {
                return@LaunchedEffect
            }
            val previousChatId = lastCompactSelectedChatId
            if (currentChatId != null && currentChatId != previousChatId) {
                openingChatId = currentChatId
                directChatDragOffset = null
                chatBackOffset.stop()
                chatBackOffset.snapTo(screenWidthPx)
                try {
                    chatBackOffset.animateTo(0f, tween(260, easing = FastOutSlowInEasing))
                } finally {
                    if (state.selectedChatId == currentChatId) {
                        lastCompactSelectedChatId = currentChatId
                    }
                    openingChatId = null
                }
            } else if (currentChatId == null) {
                openingChatId = null
                directChatDragOffset = null
                chatBackOffset.snapTo(0f)
                lastCompactSelectedChatId = null
            } else {
                lastCompactSelectedChatId = currentChatId
            }
        }

        androidx.activity.compose.BackHandler(enabled = isAnyModalVisible || showMenu || state.selectedChatId != null) {
            when {
                selectedMediaAttachment != null -> selectedMediaAttachment = null
                profileUserId != null -> {
                    profileUserId = null
                    animateModalEntrance = false
                }
                infoChatId != null -> {
                    infoChatId = null
                    animateModalEntrance = false
                }
                showSettingsModal -> showSettingsModal = false
                showCreateModal -> showCreateModal = false
                showContactsModal -> showContactsModal = false
                showForwardPicker -> onForwardMessage(null)
                showSearch -> { showSearch = false; searchQuery = "" }
                showMenu -> showMenu = false
                state.selectedChatId != null -> startCompactBackNavigation()
            }
        }
        
        // Allow sidebar swiping even when a chat is open in landscape, but keep it constrained
        val allowSidebarSwipe = !compact || state.selectedChatId == null

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(showMenu, isAnyModalVisible, compact) {
                    if (isAnyModalVisible) return@pointerInput
                    var allowChatBackDrag = false
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            val currentState = latestState
                            val currentSelectedId = currentState.selectedChatId
                            val currentSelectedChat = currentSelectedId?.let { id ->
                                latestSelectedChat?.takeIf { it.id == id }
                                    ?: currentState.chats.firstOrNull { it.id == id }
                            }
                            allowChatBackDrag = compact && currentSelectedId != null && currentSelectedChat != null && offset.x <= backSwipeEdgePx
                            if (allowChatBackDrag) {
                                lockedSlidingChatId = currentSelectedId
                                lockedSlidingChat = currentSelectedChat
                                lockedSlidingMessages = currentState.messages.toList()
                                openingChatId = null
                                directChatDragOffset = chatBackOffset.value.coerceAtLeast(0f)
                                isBackSwipeDragging = true
                            }
                            scope.launch {
                                sidebarOffset.stop()
                                chatBackOffset.stop()
                            }
                            if (allowChatBackDrag) {
                                chatSnapshotCapturedForGesture = false
                                chatSnapshot = null
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            if (compact && (lockedSlidingChat != null || latestState.selectedChatId != null)) {
                                if (!allowChatBackDrag) return@detectHorizontalDragGestures
                                // Chat back: only positive drag
                                val currentOffset = directChatDragOffset ?: chatBackOffset.value
                                val target = currentOffset + dragAmount
                                if (target >= 0) {
                                    directChatDragOffset = target
                                }
                            } else if (allowSidebarSwipe) {
                                // Sidebar: constrain to valid range
                                val target = sidebarOffset.value + dragAmount
                                if (target <= 0f && target >= -menuWidthPx) {
                                    scope.launch { sidebarOffset.snapTo(target) }
                                }
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                if (compact && (lockedSlidingChat != null || latestState.selectedChatId != null)) {
                                    val total = directChatDragOffset ?: chatBackOffset.value
                                    if (total > 150) {
                                        if (lockedSlidingChat == null) {
                                            lockCurrentChatForBack(latestState)
                                        }
                                        finishCompactBackNavigation(size.width.toFloat().coerceAtLeast(total))
                                    } else {
                                        chatBackOffset.snapTo(total.coerceAtLeast(0f))
                                        directChatDragOffset = null
                                        chatBackOffset.animateTo(0f, tween(220, easing = FastOutSlowInEasing))
                                        clearBackSwipeVisualState()
                                    }
                                } else if (allowSidebarSwipe) {
                                    if (sidebarOffset.value > -menuWidthPx * 0.6f) {
                                        showMenu = true
                                        sidebarOffset.animateTo(0f)
                                    } else {
                                        showMenu = false
                                        sidebarOffset.animateTo(-menuWidthPx)
                                    }
                                }
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                if (compact && (lockedSlidingChat != null || latestState.selectedChatId != null)) {
                                    val cancelOffset = directChatDragOffset ?: chatBackOffset.value
                                    chatBackOffset.snapTo(cancelOffset.coerceAtLeast(0f))
                                    directChatDragOffset = null
                                    chatBackOffset.animateTo(0f, tween(220, easing = FastOutSlowInEasing))
                                    clearBackSwipeVisualState()
                                }
                            }
                        }
                    )
                }
        ) {
            if (compact) {
                val compactOpeningPrime = compact &&
                    state.selectedChatId != null &&
                    state.selectedChatId != lastCompactSelectedChatId &&
                    openingChatId == null &&
                    lockedSlidingChat == null &&
                    !isBackSwipeDragging &&
                    !isCompletingBackSwipe
                val showingChatSurface = lockedSlidingChat != null || state.selectedChatId != null || openingChatId != null || compactOpeningPrime
                fun chatSurfaceOffset(): IntOffset {
                    val offset = when {
                        compactOpeningPrime -> screenWidthPx
                        directChatDragOffset != null -> directChatDragOffset ?: 0f
                        showingChatSurface -> chatBackOffset.value
                        else -> 0f
                    }
                    return IntOffset(offset.roundToInt(), 0)
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    SidebarPane(
                        state = state,
                        strings = strings,
                        chats = filteredChats,
                        users = filteredUsers,
                        showSearch = showSearch,
                        searchQuery = searchQuery,
                        onMenuClick = { showMenu = true },
                        onSearchToggle = {
                            showSearch = !showSearch
                            if (!showSearch) searchQuery = ""
                        },
                        onSearchQueryChange = {
                            searchQuery = it
                            onSearchPublic(it)
                        },
                        onOpenChat = onOpenChat,
                        onOpenContacts = { showContactsModal = true },
                        onOpenCreate = { showCreateModal = true },
                        onOpenSettings = {
                            settingsSection = SettingsSection.MENU
                            showSettingsModal = true
                        },
                        onOpenStars = {
                            settingsSection = SettingsSection.SUBSCRIPTION
                            showSettingsModal = true
                        },
                        onOpenProfile = onOpenProfile,
                        onOpenGroupInfo = { chatId ->
                            infoChatId = chatId
                            animateModalEntrance = true
                        },
                        onDismissUpdate = onDismissUpdate,
                        onDownloadUpdate = onDownloadUpdate,
                        onInstallUpdate = onInstallUpdate,
                        onPauseAudio = onPauseAudio,
                        onResumeAudio = onResumeAudio,
                        onStopAudio = onStopAudio,
                        onSeekAudio = onSeekAudio,
                        onToggleMute = onToggleMute,
                        onToggleMinimize = onToggleMinimize,
                        onLeaveCall = onLeaveCall,
                        modifier = Modifier.fillMaxSize()
                    )

                    val visibleChat = effectiveSelectedChat
                    if (showingChatSurface && visibleChat != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .onGloballyPositioned { chatSnapshotBounds = it.boundsInRoot() }
                                .offset { chatSurfaceOffset() }
                        ) {
                            ChatPane(
                                state = effectiveChatState,
                                compact = true,
                                strings = strings,
                                selectedChat = visibleChat,
                                currentUserId = state.session?.userId,
                                onBackToChats = { startCompactBackNavigation() },
                                onSend = onSend,
                                onTyping = onTyping,
                                onLoadOlder = onLoadOlder,
                                onMediaClick = onMediaClick,
                                onAttachFile = onAttachFile,
                                onRemoveAttachment = onRemoveAttachment,
                                onOpenProfile = onOpenProfile,
                                onOpenGroupInfo = {
                                    infoChatId = visibleChat.id
                                    animateModalEntrance = true
                                },
                                onReply = { onReply(it) },
                                onEditMessage = onEditMessage,
                                onForwardMessage = onForwardMessage,
                                onToggleReaction = onToggleReaction,
                                onDeleteMessage = onDeleteMessage,
                                onPinMessage = onPinMessage,
                                onCancelEdit = { onEditMessage(null) },
                                onPlayAudio = onPlayAudio,
                                onPauseAudio = onPauseAudio,
                                onResumeAudio = onResumeAudio,
                                onStopAudio = onStopAudio,
                                onSeekAudio = onSeekAudio,
                                onDownloadFile = onDownloadFile,
                                onCancelDownload = onCancelDownload,
                                onCall = { onCall(visibleChat.id) },
                                onCancelUpload = { onCancelUpload(visibleChat.id) },
                                onSendSticker = onSendSticker,
                                onAddSavedSticker = onAddSavedSticker,
                                onHandleClick = onHandleClick,
                                onJoinChat = onJoinChat,
                                onLeaveChat = onLeaveChat,
                                onBotCallback = onBotCallback,
                                onToggleMute = onToggleMute,
                                onToggleMinimize = onToggleMinimize,
                                onLeaveCall = onLeaveCall,
                                lastKeyboardHeight = lastKeyboardHeight
                            )
                        }
                    }
                }
            } else {
                Row(modifier = Modifier.fillMaxSize()) {
                    SidebarPane(
                        state = state,
                        strings = strings,
                        chats = filteredChats,
                        users = filteredUsers,
                        showSearch = showSearch,
                        searchQuery = searchQuery,
                        onMenuClick = { showMenu = true },
                        onSearchToggle = {
                            showSearch = !showSearch
                            if (!showSearch) searchQuery = ""
                        },
                        onSearchQueryChange = {
                            searchQuery = it
                            onSearchPublic(it)
                        },
                        onOpenChat = onOpenChat,
                        onOpenContacts = { showContactsModal = true },
                        onOpenCreate = { showCreateModal = true },
                        onOpenSettings = {
                            settingsSection = SettingsSection.MENU
                            showSettingsModal = true
                        },
                        onOpenStars = { 
                            settingsSection = SettingsSection.SUBSCRIPTION
                            showSettingsModal = true 
                        },
                        onOpenProfile = onOpenProfile,
                        onOpenGroupInfo = { chatId ->
                            infoChatId = chatId
                            animateModalEntrance = true
                        },
                        onDismissUpdate = onDismissUpdate,
                        onDownloadUpdate = onDownloadUpdate,
                        onInstallUpdate = onInstallUpdate,
                        onPauseAudio = onPauseAudio,
                        onResumeAudio = onResumeAudio,
                        onStopAudio = onStopAudio,
                        onSeekAudio = onSeekAudio,
                        onToggleMute = onToggleMute,
                        onToggleMinimize = onToggleMinimize,
                        onLeaveCall = onLeaveCall,
                        modifier = Modifier.width(360.dp).fillMaxHeight()
                    )
                    AnimatedContent(
                        targetState = state.selectedChatId,
                        label = "wide_shell_transition",
                        transitionSpec = {
                            slideInHorizontally(initialOffsetX = { it / 5 }) + fadeIn() togetherWith
                                    slideOutHorizontally(targetOffsetX = { -it / 5 }) + fadeOut()
                        }
                    ) { selectedId ->
                        if (selectedId == null) {
                            WelcomePane(strings = strings, modifier = Modifier.weight(1f))
                        } else {
                            ChatPane(
                                state = state,
                                compact = false,
                                strings = strings,
                                selectedChat = selectedChat,
                                currentUserId = state.session?.userId,
                                onBackToChats = onBackToChats,
                                onSend = onSend,
                                onTyping = onTyping,
                                onLoadOlder = onLoadOlder,
                                onMediaClick = onMediaClick,
                                onAttachFile = onAttachFile,
                                onRemoveAttachment = onRemoveAttachment,
                                onOpenProfile = onOpenProfile,
                                onOpenGroupInfo = { 
                                    selectedChat?.id?.let {
                                        infoChatId = it
                                        animateModalEntrance = true
                                    }
                                },
                                onReply = { onReply(it) },
                                onEditMessage = onEditMessage,
                                onForwardMessage = onForwardMessage,
                                onToggleReaction = onToggleReaction,
                                onDeleteMessage = onDeleteMessage,
                                onPinMessage = onPinMessage,
                                onCancelEdit = { onEditMessage(null) },
                                onPlayAudio = onPlayAudio,
                                onPauseAudio = onPauseAudio,
                                onResumeAudio = onResumeAudio,
                                onStopAudio = onStopAudio,
                                onSeekAudio = onSeekAudio,
                                onDownloadFile = onDownloadFile,
                                onCancelDownload = onCancelDownload,
                                onCall = { selectedChat?.id?.let { onCall(it) } },
                                onCancelUpload = { selectedChat?.id?.let { onCancelUpload(it) } },
                                onSendSticker = onSendSticker,
                                onAddSavedSticker = onAddSavedSticker,
                                onHandleClick = onHandleClick,
                                onJoinChat = onJoinChat,
                                onLeaveChat = onLeaveChat,
                                onBotCallback = onBotCallback,
                                onToggleMute = onToggleMute,
                                onToggleMinimize = onToggleMinimize,
                                onLeaveCall = onLeaveCall,
                                lastKeyboardHeight = lastKeyboardHeight,
                                modifier = Modifier.weight(1f)
                                )                        }
                    }
                }
            }
        }

        // Sidebar Menu Overlay & Sheet
        if (!compact || state.selectedChatId == null) {
            val currentMenuOffset = sidebarOffset.value
            val progress = (currentMenuOffset + menuWidthPx) / menuWidthPx
            if (progress > 0.01f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = (0.5f * progress).coerceIn(0f, 0.5f)))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { showMenu = false }
                        )
                )

                Box(
                    modifier = Modifier
                        .width(menuWidth)
                        .fillMaxHeight()
                        .graphicsLayer { translationX = currentMenuOffset.coerceIn(-menuWidthPx, 0f) }
                ) {
                    MenuSheet(
                        state = state,
                        strings = strings,
                        onOpenContacts = {
                            showMenu = false
                            showContactsModal = true
                        },
                        onOpenCreate = {
                            showMenu = false
                            showCreateModal = true
                        },
                        onOpenStars = {
                            showMenu = false
                            settingsSection = SettingsSection.SUBSCRIPTION
                            showSettingsModal = true
                        },
                        onOpenSettings = {
                            showMenu = false
                            settingsSection = SettingsSection.MENU
                            showSettingsModal = true
                        }
                    )
                }
            }
        }

        ModalHost(visible = showContactsModal, onDismiss = { showContactsModal = false }) {
            ContactsModal(
                strings = strings,
                users = state.contacts,
                chats = state.chats,
                selfUserId = state.session?.userId,
                onClose = { showContactsModal = false },
                onMessage = { userId ->
                    showContactsModal = false
                    onStartDirectChat(userId)
                },
                onOpenProfile = { userId -> profileUserId = userId }
            )
        }
ModalHost(visible = showCreateModal, onDismiss = { showCreateModal = false }) {
    CreateChannelModal(
        strings = strings,
        onCreate = onStartCreateChat,
        onClose = { showCreateModal = false }
    )
}

        if (selectedMediaAttachment != null) {
            FullscreenMediaModal(
                attachment = selectedMediaAttachment!!.attachment,
                localPath = selectedMediaAttachment!!.localPath,
                onDismiss = { selectedMediaAttachment = null }
            )
        }

        ModalHost(visible = showSettingsModal, onDismiss = { showSettingsModal = false }) {
            SettingsModal(
                state = state,
                strings = strings,
                section = settingsSection,
                onSectionChange = { settingsSection = it },
                onClose = { showSettingsModal = false },
                onLogout = onLogout,
                currentTheme = currentTheme,
                onThemeChange = onThemeChange,
                onUpdateProfile = onUpdateProfile,
                onChangePassword = onChangePassword,
                onDeleteAccount = onDeleteAccount,
                onSetLanguage = onSetLanguage,
                onCheckUpdate = onCheckUpdate,
                onSetBetaUpdatesEnabled = onSetBetaUpdatesEnabled,
                onSetDoubleTapReaction = onSetDoubleTapReaction,
                onUpdateNotificationSettings = onUpdateNotificationSettings,
                onRequestBatteryOptimization = onRequestBatteryOptimization,
                onRequestPermission = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
            )
        }

        ModalHost(visible = infoChatId != null, onDismiss = { infoChatId = null; animateModalEntrance = false }, fullscreen = true) {
            val infoChat = remember(infoChatId, state.chats) { state.chats.firstOrNull { it.id == infoChatId } }
            infoChat?.let { chat ->
                GroupInfoModal(
                    chat = chat,
                    strings = strings,
                    state = state,
                    onOpenProfile = onOpenProfile,
                    onClose = { 
                        infoChatId = null
                        animateModalEntrance = false
                    },
                    onJoinChat = onJoinChat,
                    onLeaveChat = { chatId ->
                        onLeaveChat(chatId)
                        if (infoChatId == chatId) {
                            infoChatId = null
                            animateModalEntrance = false
                        }
                    },
                    onOpenChat = { chatId ->
                        onOpenChat(chatId)
                        infoChatId = null
                        animateModalEntrance = false
                    },
                    animateEntrance = animateModalEntrance
                )
            }
        }

        ModalHost(visible = selectedProfile != null, onDismiss = { profileUserId = null; animateModalEntrance = false }, fullscreen = true) {
            selectedProfile?.let { user ->
                ProfileModal(
                    strings = strings,
                    user = user,
                    chats = state.chats,
                    selfUserId = state.session?.userId,
                    onClose = { 
                        profileUserId = null
                        animateModalEntrance = false
                    },
                    onMessage = {
                        profileUserId = null
                        infoChatId = null
                        animateModalEntrance = false
                        onStartDirectChat(user.id)
                    },
                    onLeaveChat = onLeaveChat,
                    animateEntrance = animateModalEntrance
                )
            }
        }

        ModalHost(
            visible = showForwardPicker && state.forwardingMessage != null,
            onDismiss = { onForwardMessage(null) }
        ) {
            state.forwardingMessage?.let { msg ->
                ForwardChatPicker(
                    strings = strings,
                    chats = state.chats,
                    onClose = { onForwardMessage(null) },
                    onForward = { targetChatId ->
                        onForwardConfirm(msg, targetChatId)
                    }
                )
            }
        }

        // Voice Call Overlay
        if (state.voiceChatState.connectionState != ir.hienob.noveo.data.VoiceConnectionState.IDLE && !state.voiceChatState.isMinimized) {
            VoiceCallOverlay(
                state = state.voiceChatState,
                strings = strings,
                usersById = state.usersById,
                onLeave = onLeaveCall,
                onToggleMute = onToggleMute,
                onToggleDeafen = onToggleDeafen,
                onMinimize = onToggleMinimize
            )
        }
    }
}

@Composable
private fun SidebarPane(
    state: AppUiState,
    strings: NoveoStrings,
    chats: List<ChatSummary>,
    users: List<UserSummary>,
    showSearch: Boolean,
    searchQuery: String,
    onMenuClick: () -> Unit,
    onSearchToggle: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenChat: (String) -> Unit,
    onOpenContacts: () -> Unit,
    onOpenCreate: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStars: () -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenGroupInfo: (String) -> Unit,
    onDismissUpdate: () -> Unit,
    onDownloadUpdate: () -> Unit,
    onInstallUpdate: () -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    onStopAudio: () -> Unit,
    onSeekAudio: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onToggleMinimize: () -> Unit,
    onLeaveCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tgColors = telegramColors()
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.fillMaxSize()) {
            SidebarHeader(
                state = state,
                strings = strings,
                showSearch = showSearch,
                searchQuery = searchQuery,
                connectionTitle = state.connectionTitle,
                onMenuClick = onMenuClick,
                onSearchToggle = onSearchToggle,
                onSearchQueryChange = onSearchQueryChange
            )
            
            if (state.currentAudioMessage != null) {
                GlobalAudioMiniPlayer(
                    state = state,
                    strings = strings,
                    onPause = onPauseAudio,
                    onResume = onResumeAudio,
                    onStop = onStopAudio,
                    onSeek = onSeekAudio,
                    tgColors = tgColors
                )
            }

            if (state.voiceChatState.connectionState != ir.hienob.noveo.data.VoiceConnectionState.IDLE && state.voiceChatState.isMinimized) {
                VoiceChatTray(
                    state = state.voiceChatState,
                    strings = strings,
                    onExpand = onToggleMinimize,
                    onLeave = onLeaveCall,
                    onToggleMute = onToggleMute,
                    tgColors = tgColors
                )
            }
            
            state.updateInfo?.let { info ->
                UpdateBubble(
                    strings = strings,
                    updateInfo = info,
                    onDismiss = onDismissUpdate,
                    onUpdate = onDownloadUpdate,
                    onInstall = onInstallUpdate
                )
            }

            if (showSearch) {
                SearchResultsList(
                    strings = strings,
                    chats = chats,
                    users = users,
                    onOpenChat = onOpenChat,
                    onOpenContacts = onOpenContacts,
                    onOpenProfile = onOpenProfile,
                    onOpenGroupInfo = onOpenGroupInfo
                )
            } else {
                ChatListContent(state = state, strings = strings, chats = chats, onOpenChat = onOpenChat)
            }
        }
    }
}

@Composable
private fun SearchResultsList(
    strings: NoveoStrings,
    chats: List<ChatSummary>,
    users: List<UserSummary>,
    onOpenChat: (String) -> Unit,
    onOpenContacts: () -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenGroupInfo: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (chats.isNotEmpty()) {
            item { Text(strings.newChat, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.primary) }
            items(chats) { chat ->
                ChatRow(
                    chat = chat, 
                    strings = strings, 
                    usersById = emptyMap(),
                    currentUserId = null,
                    selected = false, 
                    onClick = { 
                        if (chat.chatType == "private") {
                            onOpenChat(chat.id)
                        } else {
                            onOpenGroupInfo(chat.id)
                        }
                    }
                )
            }
        }
        if (users.isNotEmpty()) {
            item { Text(strings.allContacts, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.primary) }
            items(users) { user ->
                ContactRow(
                    user = user,
                    strings = strings,
                    existingChat = null,
                    onMessage = { onOpenProfile(user.id) },
                    onOpenProfile = { onOpenProfile(user.id) }
                )
            }
        }
    }
}

@Composable
private fun ChatListContent(
    state: AppUiState,
    strings: NoveoStrings,
    chats: List<ChatSummary>,
    onOpenChat: (String) -> Unit
) {
    if (state.loading && chats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
        }
    } else if (chats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = strings.noMessagesYet,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = state.error ?: strings.selectChatHint,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chats, key = { it.id }) { chat ->
                ChatRow(
                    chat = chat,
                    strings = strings,
                    usersById = state.usersById,
                    currentUserId = state.session?.userId,
                    selected = chat.id == state.selectedChatId,
                    onClick = { onOpenChat(chat.id) }
                )
            }
        }
    }
}

@Composable
private fun SidebarHeader(
    state: AppUiState,
    strings: NoveoStrings,
    showSearch: Boolean,
    searchQuery: String,
    connectionTitle: String,
    onMenuClick: () -> Unit,
    onSearchToggle: () -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    val titleAlpha = 1f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderIconButton(icon = Icons.Outlined.Menu, onClick = onMenuClick)
        Spacer(Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            AnimatedContent(
                targetState = showSearch,
                label = "sidebar_header_swap",
                contentAlignment = Alignment.Center,
                transitionSpec = {
                    (slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn())
                        .togetherWith(slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut())
                }
            ) { searching ->
                if (searching) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth(0.88f).height(46.dp),
                        placeholder = { Text(strings.searchPlaceholder, style = MaterialTheme.typography.bodyMedium) },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        singleLine = true,
                        shape = RoundedCornerShape(23.dp)
                    )
                } else {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AnimatedContent(
                            targetState = connectionTitle,
                            label = "title_animation",
                            transitionSpec = {
                                (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                                    slideOutVertically { height -> height } + fadeOut())
                            }
                        ) { title ->
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                lineHeight = 22.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                                    .wrapContentHeight(Alignment.CenterVertically)
                                    .alpha(titleAlpha),
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        HeaderIconButton(
            icon = if (showSearch) Icons.Outlined.Close else Icons.Outlined.Search,
            onClick = onSearchToggle
        )
    }
}

private fun formatDuration(seconds: Int, strings: NoveoStrings): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return if (mins > 0) {
        "${localizeDigits(mins.toString(), strings.languageCode)}m ${localizeDigits(secs.toString(), strings.languageCode)}s"
    } else {
        "${localizeDigits(secs.toString(), strings.languageCode)}s"
    }
}

@Composable
private fun CallLogView(
    callLogJson: String,
    strings: NoveoStrings,
    ownMessage: Boolean,
    tgColors: TelegramThemeColors
) {
    val log = remember(callLogJson) { runCatching { org.json.JSONObject(callLogJson) }.getOrNull() } ?: return
    val type = log.optString("type").ifBlank { log.optString("status") }
    val duration = if (log.has("duration")) log.optInt("duration", 0) else log.optInt("durationSeconds", 0)
    
    val icon = when (type) {
        "outgoing" -> Icons.Outlined.Call
        "incoming" -> Icons.Outlined.Call
        "missed" -> Icons.Outlined.ErrorOutline
        "cancelled", "canceled" -> Icons.Outlined.Close
        "declined", "rejected" -> Icons.Outlined.Close
        else -> Icons.Outlined.Call
    }
    
    val label = when (type) {
        "outgoing" -> strings.outgoingCall
        "incoming" -> strings.incomingCall
        "missed" -> strings.missedCall
        "cancelled", "canceled" -> strings.cancelledCall
        "declined", "rejected" -> strings.declinedCall
        else -> strings.incomingCall
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(4.dp)) {
        Icon(
            imageVector = icon, 
            contentDescription = null, 
            tint = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink, 
            modifier = Modifier.size(20.dp).graphicsLayer {
                if (type == "outgoing") {
                    rotationZ = 45f
                } else if (type == "incoming") {
                    rotationZ = 225f
                }
            }
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                label, 
                fontWeight = FontWeight.Bold, 
                color = if (ownMessage) tgColors.outgoingText else tgColors.incomingText,
                fontSize = 15.sp
            )
            if (duration > 0) {
                Text(
                    formatDuration(duration, strings), 
                    style = MaterialTheme.typography.labelSmall, 
                    color = (if (ownMessage) tgColors.outgoingText else tgColors.incomingText).copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun VerifiedIcon(modifier: Modifier = Modifier.size(14.dp)) {
    Box(
        modifier = modifier.background(Color(0xFF2EA6FF), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = "Verified",
            tint = Color.White,
            modifier = Modifier.matchParentSize().padding(2.dp)
        )
    }
}

@Composable
private fun PinnedMessageBanner(
    pinnedMessage: ChatMessage,
    tgColors: TelegramThemeColors,
    strings: NoveoStrings,
    onClick: () -> Unit,
    onUnpin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { onClick() },
        color = tgColors.incomingBubble,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.Bookmark,
                contentDescription = null,
                tint = tgColors.headerIcon,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    strings.pinnedMessage,
                    color = tgColors.headerIcon,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    pinnedMessage.content.previewText(),
                    color = tgColors.incomingText,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onUnpin) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "Unpin",
                    tint = tgColors.headerSubtitle,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChatPane(
    state: AppUiState,
    compact: Boolean,
    strings: NoveoStrings,
    selectedChat: ChatSummary?,
    currentUserId: String?,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    onTyping: () -> Unit,
    onLoadOlder: () -> Unit,
    onMediaClick: (ChatMessage, MessageFileAttachment) -> Unit,
    onAttachFile: (android.net.Uri) -> Unit,
    onRemoveAttachment: () -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenGroupInfo: () -> Unit,
    onReply: (ChatMessage?) -> Unit,
    onEditMessage: (ChatMessage?) -> Unit,
    onForwardMessage: (ChatMessage?) -> Unit,
    onToggleReaction: (String, String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onPinMessage: (String, Boolean) -> Unit,
    onCancelEdit: () -> Unit,
    onPlayAudio: (ChatMessage) -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    onStopAudio: () -> Unit,
    onSeekAudio: (Float) -> Unit,
    onDownloadFile: (ChatMessage) -> Unit,
    onCancelDownload: (ChatMessage) -> Unit,
    onCall: () -> Unit,
    onCancelUpload: () -> Unit,
    onSendSticker: (SavedSticker) -> Unit,
    onAddSavedSticker: (ChatMessage) -> Unit,
    onHandleClick: (String) -> Unit,
    onJoinChat: (String) -> Unit,
    onLeaveChat: (String) -> Unit,
    onBotCallback: (String, String, String) -> Unit,
    onToggleMute: () -> Unit,
    onToggleMinimize: () -> Unit,
    onLeaveCall: () -> Unit,
    lastKeyboardHeight: Dp = 300.dp,
    modifier: Modifier = Modifier
) {
    var draft by rememberSaveable(state.selectedChatId) { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(state.editingMessage) {
        state.editingMessage?.content?.text?.let {
            draft = it
        }
    }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onAttachFile(it) }
    }

    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { onAttachFile(it) }
    }

    val selectedTitle = remember(selectedChat, strings, state.session?.userId) {
        if (selectedChat?.isSavedMessagesChat(state.session?.userId) == true) strings.savedMessages
        else selectedChat?.title?.ifBlank { strings.chatInfo } ?: strings.chatInfo
    }
    
    val profileUserId = remember(selectedChat, state.session?.userId) {
        resolveProfileUserId(selectedChat, state.session?.userId)
    }
    val profileUser = remember(profileUserId, state.usersById) { state.usersById[profileUserId] }
    val isOnline = remember(profileUserId, state.onlineUserIds) { state.onlineUserIds.contains(profileUserId) }
    
    val onlineCount = remember(selectedChat, state.onlineUserIds) {
        selectedChat?.memberIds?.count { state.onlineUserIds.contains(it) } ?: 0
    }
    
    val messages = state.messages
    val usersById = state.usersById
    val sessionUserId = state.session?.userId
    val attachmentDownloads = state.attachmentDownloads
    val currentAudioMessageId = state.currentAudioMessage?.id
    val currentAudioPlaying = state.isAudioPlaying
    val currentAudioProgress = state.audioProgress
    val doubleTapEmoji = state.doubleTapReaction.ifBlank { "❤" }

    val typingUsers = state.typingUsers[selectedChat?.id].orEmpty()
    
    val showScrollToBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            totalItems > 0 && lastVisibleItem >= 0 && (totalItems - 1 - lastVisibleItem) >= 10
        }
    }

    val typingText = remember(selectedChat?.chatType, typingUsers, state.usersById, strings) {
        if (typingUsers.isEmpty()) null
        else if (selectedChat?.chatType == "private") {
            strings.typingPrivate
        } else {
            val names = typingUsers.mapNotNull { state.usersById[it]?.username?.split(" ")?.firstOrNull() }
            when {
                names.isEmpty() -> strings.typingSomeone
                names.size == 1 -> "${names.first()} ${strings.typingSingle}"
                names.size == 2 -> "${names[0]} ${strings.typingDouble} ${names[1]}"
                else -> "${localizeDigits(names.size.toString(), strings.languageCode)} ${strings.typingMulti}"
            }
        }
    }

    val subtitle = remember(selectedChat, profileUser, isOnline, onlineCount, typingText, strings, messages.size, sessionUserId) {
        if (selectedChat == null) return@remember ""
        if (typingText != null) return@remember typingText
        val isSavedMessages = selectedChat.isSavedMessagesChat(sessionUserId)
        if (isSavedMessages) {
            formatMessagesCount(messages.size, strings)
        } else if (selectedChat.chatType == "private") {
            if (isOnline) strings.membersOnline
            else formatLastSeen(profileUser?.lastSeen, strings)
        } else {
            val onlineStr = localizeDigits(onlineCount.toString(), strings.languageCode)
            val totalMembersText = formatMembersCount(selectedChat.memberIds.size, strings)
            val rawSubtitle = if (onlineCount > 0) "$totalMembersText${strings.comma} $onlineStr ${strings.membersOnline}" else totalMembersText
            if (strings.languageCode == "fa" || strings.languageCode == "ar") "\u200F$rawSubtitle" else rawSubtitle
        }
    }

    var highlightedMessageId by remember { mutableStateOf<String?>(null) }
    var contextMenuState by remember { mutableStateOf<MessageContextMenuState?>(null) }
    var contextMenuExpanded by remember { mutableStateOf(false) }
    var showSeenByMessage by remember { mutableStateOf<ChatMessage?>(null) }
    var showAttachPopup by remember { mutableStateOf(false) }
    var showStickers by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    val imeVisible = WindowInsets.isImeVisible


    val canLoadOlder = selectedChat?.hasMoreHistory == true && !state.loading
    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    
    LaunchedEffect(firstVisibleItemIndex) {
        if (firstVisibleItemIndex <= 2 && canLoadOlder && messages.isNotEmpty()) {
            onLoadOlder()
        }
    }

    LaunchedEffect(imeVisible) {
        if (imeVisible && showStickers) {
            showStickers = false
        }
    }

    // Force scroll to bottom when a chat is first opened
    LaunchedEffect(state.selectedChatId) {
        if (state.selectedChatId != null && messages.isNotEmpty()) {
            listState.scrollToItem(messages.lastIndex)
        }
    }

    // Handle history loading vs new messages
    val lastMessageId = remember { mutableStateOf<String?>(null) }
    LaunchedEffect(messages.size) {
        if (messages.isEmpty()) return@LaunchedEffect
        val newLastId = messages.last().id
        if (lastMessageId.value != null && newLastId != lastMessageId.value) {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: messages.lastIndex
            val itemsFromBottom = messages.lastIndex - lastVisibleItem
            if (itemsFromBottom <= 2) {
                listState.scrollToItem(messages.lastIndex)
            } else if (itemsFromBottom < 10) {
                listState.animateScrollToItem(messages.lastIndex)
            }
        }
        lastMessageId.value = newLastId
    }

    val tgColors = telegramColors()
    val onScrollToMessage = { messageId: String ->
        val index = messages.indexOfFirst { it.id == messageId }
        if (index >= 0) {
            scope.launch {
                highlightedMessageId = messageId
                listState.animateScrollToItem(index)
                delay(2000)
                if (highlightedMessageId == messageId) {
                    highlightedMessageId = null
                }
            }
        }
    }

    val density = LocalDensity.current

    val hasAudio = state.currentAudioMessage != null
    val hasVoice = state.voiceChatState.connectionState != ir.hienob.noveo.data.VoiceConnectionState.IDLE && state.voiceChatState.isMinimized
    val hasPinned = selectedChat?.pinnedMessage != null
    
    val topPadding = 56.dp + (if (hasAudio) 48.dp else 0.dp) + (if (hasVoice) 48.dp else 0.dp) + (if (hasPinned) 48.dp else 0.dp) + 8.dp

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(tgColors.chatSurface)) {
        val maxBubbleWidth = maxWidth * 0.78f

        // 1. Messages Layer
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 8.dp, 
                top = topPadding, 
                end = 8.dp, 
                bottom = 90.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(
                items = messages,
                key = { _, message -> message.id },
                contentType = { _, msg ->
                    when {
                        msg.senderId == "system" -> "system"
                        msg.content.file?.isSticker() == true -> "sticker"
                        msg.content.file?.isAudio() == true -> "audio"
                        msg.content.file != null -> "attachment"
                        msg.content.inlineKeyboard.isNotEmpty() -> "bot_keyboard"
                        else -> "message"
                    }
                }
            ) { index, message ->
                val prevMessage = messages.getOrNull(index - 1)
                val nextMessage = messages.getOrNull(index + 1)
                val ownMessage = message.senderId == sessionUserId
                val showSenderInfo = prevMessage == null ||
                    prevMessage.senderId != message.senderId ||
                    (message.timestamp - prevMessage.timestamp) > 300 ||
                    prevMessage.senderId == "system"
                val hasTail = nextMessage == null ||
                    nextMessage.senderId != message.senderId ||
                    (nextMessage.timestamp - message.timestamp) > 300 ||
                    nextMessage.senderId == "system"
                val senderAvatarUrl = usersById[message.senderId]?.avatarUrl
                val repliedMessage = message.replyToId?.let { replyId ->
                    messages.firstOrNull { it.id == replyId }
                }

                MessageRow(
                    strings = strings,
                    message = message,
                    ownMessage = ownMessage,
                    senderAvatarUrl = senderAvatarUrl,
                    showSenderInfo = showSenderInfo,
                    hasTail = hasTail,
                    isGroupChat = selectedChat?.chatType != "private",
                    currentUserId = currentUserId,
                    onMediaClick = onMediaClick,
                    onOpenProfile = onOpenProfile,
                    repliedMessage = repliedMessage,
                    maxBubbleWidth = maxBubbleWidth,
                    onReply = { onReply(message) },
                    onToggleReaction = onToggleReaction,
                    onOpenContextMenu = { bubbleBounds ->
                        contextMenuState = MessageContextMenuState(
                            message = message,
                            ownMessage = ownMessage,
                            bubbleBounds = bubbleBounds
                        )
                        contextMenuExpanded = false
                    },
                    onScrollToMessage = onScrollToMessage,
                    onPlayAudio = onPlayAudio,
                    onPauseAudio = onPauseAudio,
                    onResumeAudio = onResumeAudio,
                    onStopAudio = onStopAudio,
                    onSeekAudio = onSeekAudio,
                    doubleTapReaction = doubleTapEmoji,
                    onDownloadFile = onDownloadFile,
                    onCancelDownload = onCancelDownload,
                    onHandleClick = onHandleClick,
                    onBotCallback = onBotCallback,
                    currentAudioMessageId = currentAudioMessageId,
                    isAudioPlaying = currentAudioMessageId == message.id && currentAudioPlaying,
                    audioProgress = if (currentAudioMessageId == message.id) currentAudioProgress else 0f,
                    attachmentDownloadState = message.content.file?.let { attachmentDownloads[it.downloadKey()] },
                    isHighlighted = highlightedMessageId == message.id,
                    tgColors = tgColors
                )
            }
        }

        // 1.5 Chat Input Gradient Layer
        if (selectedChat?.canChat != false) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 42.dp) // Centered on typical input height + padding
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, tgColors.chatSurface),
                            startY = 0f,
                            endY = with(density) { 110.dp.toPx() }
                        )
                    )
            )
        }

        // 2. Headbar Layer (ActionBar)
        Surface(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            color = tgColors.incomingBubble,
            tonalElevation = 1.dp,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderIconButton(
                    icon = Icons.AutoMirrored.Outlined.ArrowBack,
                    onClick = onBackToChats,
                    tint = tgColors.headerIcon,
                    modifier = Modifier.padding(start = 4.dp)
                )
                
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (profileUserId != null) onOpenProfile(profileUserId)
                            else if (selectedChat?.chatType == "private") profileUserId?.let(onOpenProfile)
                            else if (selectedChat != null) onOpenGroupInfo()
                        }
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileCircle(
                        name = selectedTitle,
                        imageUrl = selectedChat?.avatarUrl,
                        isSavedMessages = selectedChat?.id?.startsWith("saved_") == true,
                        size = 40.dp,
                        modifier = Modifier.clickable {
                            profileUserId?.let { onOpenProfile(it) }
                                ?: if (selectedChat?.chatType != "private") onOpenGroupInfo() else Unit
                        }
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                selectedTitle,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = tgColors.headerTitle,
                                fontSize = 15.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (selectedChat?.isVerified == true || profileUser?.isVerified == true) {
                                Spacer(Modifier.width(4.dp))
                                VerifiedIcon(modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(Modifier.height(3.dp))
                        Text(
                            subtitle,
                            color = tgColors.headerSubtitle,
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                HeaderIconButton(icon = Icons.Outlined.Call, onClick = onCall, tint = tgColors.headerIcon)
                HeaderIconButton(icon = Icons.Outlined.Search, onClick = {}, tint = tgColors.headerIcon, modifier = Modifier.padding(end = 4.dp))
            }
        }

        if (state.currentAudioMessage != null) {
            Box(modifier = Modifier.padding(top = 56.dp)) {
                GlobalAudioMiniPlayer(
                    state = state,
                    strings = strings,
                    onPause = onPauseAudio,
                    onResume = onResumeAudio,
                    onStop = onStopAudio,
                    onSeek = onSeekAudio,
                    tgColors = tgColors
                )
            }
        }

        if (hasVoice) {
            val voiceOffset = 56.dp + (if (hasAudio) 48.dp else 0.dp)
            Box(modifier = Modifier.padding(top = voiceOffset)) {
                VoiceChatTray(
                    state = state.voiceChatState,
                    strings = strings,
                    onExpand = onToggleMinimize,
                    onLeave = onLeaveCall,
                    onToggleMute = onToggleMute,
                    tgColors = tgColors
                )
            }
        }

        // 2.1 Pinned Message Bar
        selectedChat?.pinnedMessage?.let { pinned ->
            val pinnedOffset = 56.dp + (if (hasAudio) 48.dp else 0.dp) + (if (hasVoice) 48.dp else 0.dp)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = pinnedOffset)
                    .height(48.dp)
                    .clickable { onScrollToMessage(pinned.id) },
                color = tgColors.incomingBubble.copy(alpha = 0.98f),
                tonalElevation = 1.dp,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Bookmark,
                        contentDescription = null,
                        tint = tgColors.headerIcon,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            strings.pinnedMessage,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = tgColors.headerIcon,
                            fontSize = 12.sp
                        )
                        Text(
                            pinned.content.previewText(),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 13.sp,
                            color = tgColors.incomingTime
                        )
                    }
                    IconButton(onClick = { onPinMessage(pinned.id, false) }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Unpin",
                            tint = tgColors.headerSubtitle,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 2.5 Scroll to Bottom Button
        AnimatedVisibility(
            visible = showScrollToBottom,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 76.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clickable { 
                        scope.launch { 
                            if (messages.isNotEmpty()) {
                                val targetIndex = messages.lastIndex
                                if (listState.firstVisibleItemIndex < targetIndex - 20) {
                                    listState.scrollToItem(targetIndex - 10)
                                }
                                listState.animateScrollToItem(targetIndex)
                            }
                        }
                    },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Scroll to bottom",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // 3. Floating Input Layer
        val isMember = selectedChat?.memberIds?.contains(currentUserId) == true
        if (selectedChat?.canChat != false && (selectedChat?.chatType == "private" || isMember)) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (state.pendingAttachment != null) {                        Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                            AttachmentPreview(
                                attachment = state.pendingAttachment,
                                onRemove = onRemoveAttachment
                            )
                        }
                    }
                    ChatInput(
                        draft = draft,
                        onDraftChange = {
                            draft = it
                            onTyping()
                        },
                        sendScale = 1f,
                        replyingTo = state.replyingToMessage,
                        editingMessage = state.editingMessage,
                        onCancelReply = { onReply(null) },
                        onCancelEdit = {
                            onCancelEdit()
                            draft = ""
                        },
                        placeholder = strings.messagePlaceholder,
                        strings = strings,
                        onAttachClick = { 
                            showAttachPopup = true
                        },
                        onLongAttachClick = {
                            filePicker.launch(arrayOf("*/*"))
                        },
                        onEmojiClick = {
                            showStickers = !showStickers
                            if (showStickers) {
                                keyboardController?.hide()
                            } else {
                                keyboardController?.show()
                            }
                        },
                        onTextFieldFocused = {
                            showStickers = false
                        },
                        showStickers = showStickers,
                        onPasteUri = { onAttachFile(it) },
                        hasAttachment = state.pendingAttachment != null,
                        isSendingMessage = state.isSendingMessage,
                        onCancelSend = onCancelUpload,
                        tgColors = tgColors,
                        onActionClick = {
                            val text = draft.trim()
                            if (text.isNotBlank() || state.pendingAttachment != null) {
                                onSend(text)
                                draft = ""
                                showStickers = false
                            }
                        }
                    )
                    
                    if (showStickers) {
                        StickerPicker(
                            strings = strings,
                            stickers = state.savedStickers,
                            onStickerSelected = { sticker ->
                                onSendSticker(sticker)
                                showStickers = false
                                if (state.replyingToMessage != null) {
                                    onReply(null)
                                }
                            },
                            tgColors = tgColors
                        )
                    }
                }
            }
        } else if (selectedChat != null && selectedChat.chatType != "private" && !isMember) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
                    .padding(bottom = 4.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clickable { onJoinChat(selectedChat!!.id) },
                        shape = RoundedCornerShape(24.dp),
                        color = tgColors.composerField,
                        shadowElevation = 1.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                strings.join,
                                color = tgColors.composerBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        }
                    }
                    
                    Spacer(Modifier.width(8.dp))
                    
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { onJoinChat(selectedChat!!.id) },
                        shape = CircleShape,
                        color = tgColors.composerField,
                        shadowElevation = 1.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowForward,
                                contentDescription = null,
                                tint = tgColors.composerBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
        
        if (showAttachPopup) {
            AttachmentPicker(
                strings = strings,
                onGalleryClick = {
                    showAttachPopup = false
                    photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                },
                onFilesClick = {
                    showAttachPopup = false
                    filePicker.launch(arrayOf("*/*"))
                },
                onDismiss = { showAttachPopup = false },
                tgColors = tgColors
            )
        }

        // Context Menu Layer
        val currentContextMenuState = contextMenuState
        val displayedContextMenuState = remember(currentContextMenuState) {
            if (currentContextMenuState != null) currentContextMenuState else null
        }
        // Use a derived state or a separate remember to hold the state during exit animation
        var lastNonNullContextMenuState by remember { mutableStateOf<MessageContextMenuState?>(null) }
        LaunchedEffect(currentContextMenuState) {
            if (currentContextMenuState != null) {
                lastNonNullContextMenuState = currentContextMenuState
            }
        }

        AnimatedVisibility(
            visible = currentContextMenuState != null,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            lastNonNullContextMenuState?.let { menuState ->
                MessageContextMenuOverlay(
                    state = menuState,
                    expanded = contextMenuExpanded,
                    tgColors = tgColors,
                    onDismiss = {
                        contextMenuState = null
                        contextMenuExpanded = false
                    },
                    onExpandedChange = { contextMenuExpanded = it },
                    onReply = {
                        contextMenuState = null
                        contextMenuExpanded = false
                        onReply(menuState.message)
                    },
                    onCopyText = {
                        menuState.message.content.text?.let { clipboard.setText(AnnotatedString(it)) }
                        contextMenuState = null
                        contextMenuExpanded = false
                    },
                    onReaction = { emoji ->
                        contextMenuState = null
                        contextMenuExpanded = false
                        onToggleReaction(menuState.message.id, emoji)
                    },
                    onEdit = {
                        contextMenuState = null
                        contextMenuExpanded = false
                        onEditMessage(menuState.message)
                    },
                    onDelete = {
                        contextMenuState = null
                        contextMenuExpanded = false
                        onDeleteMessage(menuState.message.id)
                    },
                    onPin = {
                        contextMenuState = null
                        contextMenuExpanded = false
                        onPinMessage(menuState.message.id, !menuState.message.isPinned)
                    },
                    onForward = {
                        contextMenuState = null
                        contextMenuExpanded = false
                        onForwardMessage(menuState.message)
                    },
                    onDownload = {
                        contextMenuState = null
                        contextMenuExpanded = false
                        onDownloadFile(menuState.message)
                    },
                    onSeenBy = {
                        contextMenuState = null
                        contextMenuExpanded = false
                        showSeenByMessage = menuState.message
                    },
                    onAddAsSticker = {
                        contextMenuState = null
                        contextMenuExpanded = false
                        onAddSavedSticker(menuState.message)
                    },
                    strings = strings,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        ModalHost(visible = showSeenByMessage != null, onDismiss = { showSeenByMessage = null }) {
            showSeenByMessage?.let { msg ->
                SeenByModal(
                    strings = strings,
                    message = msg,
                    usersById = state.usersById,
                    onClose = { showSeenByMessage = null },
                    onOpenProfile = onOpenProfile
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
private fun MessageRow(
    strings: NoveoStrings,
    message: ChatMessage,
    ownMessage: Boolean,
    senderAvatarUrl: String?,
    showSenderInfo: Boolean,
    hasTail: Boolean,
    isGroupChat: Boolean,
    currentUserId: String?,
    onMediaClick: (ChatMessage, MessageFileAttachment) -> Unit,
    onOpenProfile: (String) -> Unit,
    repliedMessage: ChatMessage? = null,
    maxBubbleWidth: Dp,
    onReply: () -> Unit,
    onToggleReaction: (String, String) -> Unit,
    onOpenContextMenu: (Rect) -> Unit = {},
    onScrollToMessage: (String) -> Unit,
    onPlayAudio: (ChatMessage) -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    onStopAudio: () -> Unit,
    onSeekAudio: (Float) -> Unit,
    doubleTapReaction: String,
    onDownloadFile: (ChatMessage) -> Unit,
    onCancelDownload: (ChatMessage) -> Unit,
    onHandleClick: (String) -> Unit,
    onBotCallback: (String, String, String) -> Unit,
    currentAudioMessageId: String?,
    isAudioPlaying: Boolean,
    audioProgress: Float,
    attachmentDownloadState: ir.hienob.noveo.app.AttachmentDownloadState?,
    isHighlighted: Boolean = false,
    tgColors: TelegramThemeColors = telegramColors()
) {
    val haptic = LocalHapticFeedback.current
    val isSystem = message.senderId == "system"
    val isCallLog = !message.content.callLog.isNullOrBlank()
    if (isSystem && !isCallLog) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
            Surface(
                color = tgColors.chatSurface.copy(alpha = 0.45f),
                shape = CircleShape
            ) {
                Text(
                    message.content.text ?: strings.noMessagesYet,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }
        return
    }

    val timeStr = remember(message.timestamp, strings.languageCode) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        localizeDigits(sdf.format(Date(message.timestamp * 1000)), strings.languageCode)
    }
    val bubbleBoundsRef = remember(message.id) { arrayOfNulls<Rect>(1) }

    val animatePendingIntro = ownMessage && message.pending
    val pendingIntroFraction = if (animatePendingIntro) {
        var pendingIntroStarted by remember(message.id) { mutableStateOf(false) }
        LaunchedEffect(message.id) { pendingIntroStarted = true }
        val pendingIntroProgress by animateFloatAsState(
            targetValue = if (pendingIntroStarted) 1f else 0f,
            animationSpec = tween(140, easing = FastOutSlowInEasing),
            label = "message_pending_intro"
        )
        pendingIntroProgress
    } else {
        1f
    }

    // Swipe state
    var swipeOffset by remember(message.id) { mutableStateOf(0f) }
    val rowTransformModifier = if (animatePendingIntro || swipeOffset != 0f) {
        Modifier.graphicsLayer {
            translationY = (1f - pendingIntroFraction) * 18f
            translationX = ((1f - pendingIntroFraction) * 26f) + swipeOffset
            alpha = 0.35f + (0.65f * pendingIntroFraction)
            scaleX = 0.92f + (0.08f * pendingIntroFraction)
            scaleY = 0.92f + (0.08f * pendingIntroFraction)
            transformOrigin = TransformOrigin(if (ownMessage) 1f else 0f, 1f)
        }
    } else {
        Modifier
    }

    Column(
        horizontalAlignment = if (ownMessage) Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (showSenderInfo) 10.dp else 0.dp)
            .padding(bottom = if (hasTail) 6.dp else 0.dp)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { bubbleBoundsRef[0]?.let(onOpenContextMenu) },
                onDoubleClick = { onToggleReaction(message.id, doubleTapReaction) },
                onLongClick = { bubbleBoundsRef[0]?.let(onOpenContextMenu) }
            )
            .then(rowTransformModifier)
    ) {
        val bubbleModifier = Modifier
            .onGloballyPositioned { bubbleBoundsRef[0] = it.boundsInRoot() }
            .pointerInput(message.id) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        if (dragAmount < 0) { // Only swipe left
                            val current = swipeOffset
                            val target = (current + dragAmount).coerceIn(-100f, 0f)
                            if (current > -60f && target <= -60f) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            swipeOffset = target
                        }
                    },
                    onDragEnd = {
                        if (swipeOffset < -60f) {
                            onReply()
                        }
                        swipeOffset = 0f
                    },
                    onDragCancel = {
                        swipeOffset = 0f
                    }
                )
            }

        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        ) {
            if (!ownMessage) {
                // Telegram only shows avatar in group chats, and only for the last message in a group
                if (isGroupChat) {
                    if (hasTail) {
                        ProfileCircle(
                            name = message.senderName,
                            imageUrl = senderAvatarUrl,
                            size = 36.dp,
                            modifier = Modifier.clickable { onOpenProfile(message.senderId) }
                        )
                    } else {
                        Spacer(Modifier.width(36.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                }
            } else {
                Spacer(Modifier.weight(1f))
            }
            Column(
                horizontalAlignment = if (ownMessage) Alignment.End else Alignment.Start,
                modifier = if (ownMessage) Modifier else Modifier.weight(1f, false)
            ) {
                val emojiTgsUrl = remember(message.content.text) {
                    message.content.text?.let { EmojiTgsManager.getTgsUrlForEmoji(it) }
                }
                val isSticker = (message.content.file?.isSticker() == true) || (emojiTgsUrl != null)
                
                if (isSticker) {
                    val file = message.content.file
                    val emojiTgsUrlState = remember(message.content.text) {
                        message.content.text?.let { EmojiTgsManager.getTgsUrlForEmoji(it) }
                    }
                    val normalizedUrl = remember(file?.url, emojiTgsUrlState) { 
                        emojiTgsUrlState ?: file?.url.normalizeNoveoUrl() ?: ""
                    }
                    Box(
                        modifier = bubbleModifier.padding(vertical = 4.dp)
                    ) {
                        Column(horizontalAlignment = if (ownMessage) Alignment.End else Alignment.Start) {
                            if (repliedMessage != null) {
                                Surface(
                                    modifier = Modifier
                                        .padding(bottom = 4.dp)
                                        .clickable { onScrollToMessage(repliedMessage.id) },
                                    color = tgColors.chatSurface.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(28.dp)
                                                .background(if (ownMessage) tgColors.outgoingText.copy(alpha = 0.6f) else tgColors.incomingLink, RoundedCornerShape(1.dp))
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = repliedMessage.senderName,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                                fontWeight = FontWeight.Bold,
                                                color = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = repliedMessage.content.previewText(),
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                color = Color.White.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                }
                            }

                            val isTgs = emojiTgsUrlState != null || file?.isTgsSticker() == true
                            if (isTgs) {
                                TgsSticker(
                                    url = normalizedUrl,
                                    modifier = Modifier.size(if (emojiTgsUrlState != null) 80.dp else 160.dp),
                                    tint = Color.White,
                                    iterations = if (emojiTgsUrlState != null) 1 else com.airbnb.lottie.compose.LottieConstants.IterateForever
                                )
                            } else {
                                AsyncImage(
                                    model = normalizedUrl,
                                    contentDescription = "sticker",
                                    modifier = Modifier.size(160.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            if (message.reactions.isNotEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                FlowRow(
                                    modifier = Modifier.padding(horizontal = 4.dp).wrapContentWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    message.reactions.forEach { (emoji, userIds) ->
                                        if (userIds.isNotEmpty()) {
                                            Surface(
                                                modifier = Modifier.clickable { onToggleReaction(message.id, emoji) },
                                                shape = RoundedCornerShape(10.dp),
                                                color = Color.Black.copy(alpha = 0.25f),
                                                border = if (userIds.contains(currentUserId)) BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)) else null
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(emoji, fontSize = 12.sp)
                                                    Spacer(Modifier.width(2.dp))
                                                    Text(
                                                        localizeDigits(userIds.size.toString(), strings.languageCode),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Surface(
                                modifier = Modifier.padding(top = 4.dp),
                                color = Color.Black.copy(alpha = 0.35f),
                                shape = CircleShape
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        timeStr,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = Color.White
                                    )
                                    if (ownMessage) {
                                        Spacer(Modifier.width(4.dp))
                                        val seen = message.seenBy.isNotEmpty()
                                        Icon(
                                            imageVector = if (seen) Icons.Outlined.DoneAll else Icons.Outlined.Check,
                                            contentDescription = if (seen) "Seen" else "Sent",
                                            modifier = Modifier.size(13.dp),
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Surface(
                        modifier = bubbleModifier
                            .widthIn(max = maxBubbleWidth),
                        shape = TelegramBubbleShape(
                            isOutgoing = ownMessage,
                            hasTail = hasTail,
                            cornerRadius = with(LocalDensity.current) { 16.dp.toPx() }
                        ),
                        color = when {
                            ownMessage && isHighlighted -> tgColors.outgoingBubbleSelected
                            ownMessage -> tgColors.outgoingBubble
                            isHighlighted -> tgColors.incomingBubbleSelected
                            else -> tgColors.incomingBubble
                        },
                        shadowElevation = 0.5.dp
                    ) {
                        val hasVisualMedia = message.content.file?.let { it.isImage() || it.isVideo() } == true
                        Column(modifier = Modifier.padding(if (hasVisualMedia) 3.dp else 6.dp).padding(horizontal = 4.dp)) {
                            if (!ownMessage && isGroupChat && showSenderInfo) {
                                Text(
                                    message.senderName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                                    color = tgColors.incomingLink,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                )
                            }
                            if (message.content.forwardedInfo != null) {
                                Row(
                                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ArrowForward,
                                        contentDescription = null,
                                        tint = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink,
                                        modifier = Modifier.size(14.dp).scale(-1f, 1f) // Mirror for "from"
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Column {
                                        Text(
                                            text = strings.forwardedFrom,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                            color = (if (ownMessage) tgColors.outgoingText else tgColors.incomingLink).copy(alpha = 0.7f)
                                        )
                                        Text(
                                            text = message.content.forwardedInfo.from,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                            color = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink
                                        )
                                    }
                                }
                            }
                            if (repliedMessage != null) {
                                Surface(
                                    modifier = Modifier
                                        .padding(bottom = 4.dp)
                                        .clickable { onScrollToMessage(repliedMessage.id) },
                                    color = if (ownMessage) tgColors.replyOutgoing else tgColors.replyIncoming,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(28.dp)
                                                .background(if (ownMessage) tgColors.outgoingText.copy(alpha = 0.6f) else tgColors.incomingLink, RoundedCornerShape(1.dp))
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = repliedMessage.senderName,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                                fontWeight = FontWeight.Bold,
                                                color = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = repliedMessage.content.previewText(),
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                color = if (ownMessage) tgColors.outgoingTime else tgColors.incomingTime
                                            )
                                        }
                                    }
                                }
                            }

                            val file = message.content.file
                            if (file != null) {
                                if (file.isAudio()) {
                                    AudioPlayer(
                                        message = message,
                                        isCurrent = currentAudioMessageId == message.id,
                                        isPlaying = isAudioPlaying,
                                        progress = audioProgress,
                                        onPlayToggle = { onPlayAudio(message) },
                                        onSeek = onSeekAudio,
                                        tgColors = tgColors
                                    )
                                } else {
                                    MessageAttachment(
                                        file = file,
                                        downloadState = attachmentDownloadState,
                                        ownMessage = ownMessage,
                                        onClick = { onMediaClick(message, file) },
                                        onDownloadClick = { onDownloadFile(message) },
                                        onCancelClick = { onCancelDownload(message) },
                                        tgColors = tgColors
                                    )
                                }
                            }
                            
                            val callLog = message.content.callLog
                            if (!callLog.isNullOrBlank()) {
                                CallLogView(callLog, strings, ownMessage, tgColors)
                            }

                            val caption = message.content.text

                            if (!caption.isNullOrBlank() && !(callLog != null && caption.equals("Call", ignoreCase = true))) {
                                if (message.content.file != null) Spacer(Modifier.height(4.dp))
                                Box(modifier = Modifier.padding(horizontal = if (hasVisualMedia) 6.dp else 4.dp)) {
                                    MarkdownText(
                                        text = caption,
                                        color = if (ownMessage) tgColors.outgoingText else tgColors.incomingText,
                                        onHandleClick = onHandleClick,
                                        strings = strings
                                    )
                                }
                            }

                            if (message.reactions.isNotEmpty() || (isSticker && message.reactions.isNotEmpty())) {
                                Spacer(Modifier.height(4.dp))
                                FlowRow(
                                    modifier = Modifier.padding(horizontal = 4.dp).wrapContentWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    message.reactions.forEach { (emoji, userIds) ->
                                        if (userIds.isNotEmpty()) {
                                            Surface(
                                                modifier = Modifier.clickable { onToggleReaction(message.id, emoji) },
                                                shape = RoundedCornerShape(10.dp),
                                                color = (if (ownMessage) tgColors.outgoingText else tgColors.incomingLink).copy(alpha = 0.1f),
                                                border = if (userIds.contains(currentUserId)) BorderStroke(1.dp, if (ownMessage) tgColors.outgoingText.copy(alpha = 0.3f) else tgColors.incomingLink.copy(alpha = 0.3f)) else null
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(emoji, fontSize = 12.sp)
                                                    Spacer(Modifier.width(2.dp))
                                                    Text(
                                                        localizeDigits(userIds.size.toString(), strings.languageCode),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            
                            Row(
                                modifier = Modifier.align(Alignment.End).padding(top = 1.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (message.editedAt != null) {
                                    Text(
                                        strings.edited,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = (if (ownMessage) tgColors.outgoingTime else tgColors.incomingTime).copy(alpha = 0.7f),
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                }
                                Text(
                                    timeStr,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = if (ownMessage) tgColors.outgoingTime else tgColors.incomingTime
                                )
                                if (ownMessage) {
                                    Spacer(Modifier.width(4.dp))
                                    if (message.pending) {
                                        Icon(
                                            imageVector = Icons.Outlined.Schedule,
                                            contentDescription = strings.sending,
                                            modifier = Modifier.size(13.dp),
                                            tint = tgColors.outgoingTime
                                        )
                                    } else {
                                        val seen = message.seenBy.isNotEmpty()
                                        Icon(
                                            imageVector = if (seen) Icons.Outlined.DoneAll else Icons.Outlined.Check,
                                            contentDescription = if (seen) "Seen" else "Sent",
                                            modifier = Modifier.size(15.dp),
                                            tint = tgColors.outgoingTime
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (message.content.inlineKeyboard.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .widthIn(max = maxBubbleWidth),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        message.content.inlineKeyboard.forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                row.forEach { button ->
                                    Button(
                                        onClick = { 
                                            button.callbackData?.let { data ->
                                                onBotCallback(message.chatId, message.id, data)
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (ownMessage) tgColors.outgoingBubble.copy(alpha = 0.8f) else tgColors.incomingBubble.copy(alpha = 0.8f),
                                            contentColor = if (ownMessage) tgColors.outgoingText else tgColors.incomingText
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                                    ) {
                                        Text(
                                            text = button.text,
                                            style = MaterialTheme.typography.labelMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private const val LONG_MESSAGE_COLLAPSE_CHAR_LIMIT = 900
private const val LONG_MESSAGE_COLLAPSE_MAX_LINES = 28

private fun showMoreText(strings: NoveoStrings): String = when (strings.languageCode) {
    "fa" -> "نمایش بیشتر"
    "fr" -> "Afficher plus"
    "de" -> "Mehr anzeigen"
    "ru" -> "Показать больше"
    "zh" -> "显示更多"
    else -> "Show more"
}

private fun showLessText(strings: NoveoStrings): String = when (strings.languageCode) {
    "fa" -> "نمایش کمتر"
    "fr" -> "Afficher moins"
    "de" -> "Weniger anzeigen"
    "ru" -> "Скрыть"
    "zh" -> "收起"
    else -> "Show less"
}

@Composable
private fun MarkdownText(
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onHandleClick: ((String) -> Unit)? = null,
    strings: NoveoStrings? = null,
    collapseLongText: Boolean = true
) {
    val handleColor = if (color == MaterialTheme.colorScheme.onSurface) MaterialTheme.colorScheme.primary else color.copy(alpha = 0.95f)
    val shouldCollapse = collapseLongText && text.length > LONG_MESSAGE_COLLAPSE_CHAR_LIMIT
    var expanded by remember(text) { mutableStateOf(false) }
    val visibleText = remember(text, shouldCollapse, expanded) {
        if (shouldCollapse && !expanded) {
            text.take(LONG_MESSAGE_COLLAPSE_CHAR_LIMIT).trimEnd() + "…"
        } else {
            text
        }
    }
    val maxLines = if (shouldCollapse && !expanded) LONG_MESSAGE_COLLAPSE_MAX_LINES else Int.MAX_VALUE
    val hasClickableHandle = onHandleClick != null && visibleText.indexOf('@') >= 0
    val hasBoldMarkup = visibleText.indexOf("**") >= 0

    Column {
        if (!hasClickableHandle && !hasBoldMarkup) {
            Text(
                text = visibleText,
                style = TextStyle(color = color, fontSize = 16.sp, lineHeight = 20.sp),
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis
            )
        } else {
            val annotated = remember(visibleText, handleColor, hasClickableHandle) {
                buildAnnotatedString {
                    var index = 0
                    while (index < visibleText.length) {
                        val nextMarker = visibleText.indexOf("**", index)
                        val nextHandle = if (hasClickableHandle) visibleText.indexOf("@", index) else -1

                        val markers = mutableListOf<Pair<Int, String>>()
                        if (nextMarker != -1) markers.add(nextMarker to "**")
                        if (nextHandle != -1) markers.add(nextHandle to "@")

                        val nearest = markers.minByOrNull { it.first }

                        if (nearest == null) {
                            append(visibleText.substring(index))
                            break
                        }

                        if (nearest.first > index) {
                            append(visibleText.substring(index, nearest.first))
                        }

                        if (nearest.second == "**") {
                            val endBold = visibleText.indexOf("**", nearest.first + 2)
                            if (endBold != -1) {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(visibleText.substring(nearest.first + 2, endBold))
                                }
                                index = endBold + 2
                            } else {
                                append("**")
                                index = nearest.first + 2
                            }
                        } else if (nearest.second == "@") {
                            var endHandle = nearest.first + 1
                            while (endHandle < visibleText.length && (visibleText[endHandle].isLetterOrDigit() || visibleText[endHandle] == '_')) {
                                endHandle++
                            }
                            if (endHandle > nearest.first + 1) {
                                val handle = visibleText.substring(nearest.first, endHandle)
                                pushStringAnnotation("handle", handle)
                                withStyle(SpanStyle(color = handleColor, fontWeight = FontWeight.SemiBold)) {
                                    append(handle)
                                }
                                pop()
                                index = endHandle
                            } else {
                                append("@")
                                index = nearest.first + 1
                            }
                        }
                    }
                }
            }

            if (hasClickableHandle && onHandleClick != null) {
                val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
                Text(
                    text = annotated,
                    style = TextStyle(color = color, fontSize = 16.sp, lineHeight = 20.sp),
                    maxLines = maxLines,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { layoutResult.value = it },
                    modifier = Modifier.pointerInput(annotated, onHandleClick) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: continue
                                if (event.type == PointerEventType.Press) {
                                    val layout = layoutResult.value ?: continue
                                    val position = layout.getOffsetForPosition(change.position)
                                    val annotation = annotated.getStringAnnotations("handle", position, position).firstOrNull()

                                    if (annotation != null) {
                                        change.consume()
                                        val up = waitForUpOrCancellation()
                                        if (up != null) {
                                            up.consume()
                                            onHandleClick(annotation.item)
                                        }
                                    }
                                }
                            }
                        }
                    }
                )
            } else {
                Text(
                    text = annotated,
                    style = TextStyle(color = color, fontSize = 16.sp, lineHeight = 20.sp),
                    maxLines = maxLines,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (shouldCollapse && strings != null) {
            Text(
                text = if (expanded) showLessText(strings) else showMoreText(strings),
                style = TextStyle(
                    color = handleColor,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { expanded = !expanded }
            )
        }
    }
}

@Composable
private fun MessageAttachment(
    file: MessageFileAttachment,
    downloadState: ir.hienob.noveo.app.AttachmentDownloadState?,
    ownMessage: Boolean,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onCancelClick: () -> Unit,
    tgColors: TelegramThemeColors = telegramColors()
) {
    val context = LocalContext.current
    val localPath = downloadState?.localPath
    val cacheFile = remember(file.url, file.name, file.type) { localAttachmentCacheFile(context.filesDir, file) }
    val localFile = remember(localPath, cacheFile.absolutePath) {
        localPath?.let(::File)?.takeIf { it.exists() } ?: cacheFile.takeIf { it.exists() }
    }
    val isDownloaded = localFile != null
    val isDownloading = downloadState?.isDownloading == true
    val progress = downloadState?.progress ?: 0f
    val overlayTint = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink

    if (file.isImage()) {
        var imageLoaded by remember(localFile?.absolutePath) { mutableStateOf(false) }
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(bottom = 2.dp)
                .fillMaxWidth()
                .heightIn(max = 340.dp)
                .clickable(enabled = isDownloaded) { onClick() },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp)
                    .background((if (ownMessage) tgColors.outgoingBubble else tgColors.incomingBubble).copy(alpha = 0.68f))
            ) {
                if (localFile != null) {
                    AsyncImage(
                        model = localFile,
                        contentDescription = file.name,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth,
                        onSuccess = { imageLoaded = true }
                    )
                }

                if (!imageLoaded || isDownloading || localFile == null) {
                    AttachmentDownloadOverlay(
                        isVideo = false,
                        isDownloaded = isDownloaded,
                        isDownloading = isDownloading,
                        progress = progress,
                        tint = overlayTint,
                        onDownloadClick = onDownloadClick,
                        onCancelClick = onCancelClick,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    } else if (file.isVideo()) {
        Surface(
            modifier = Modifier
                .padding(bottom = 2.dp)
                .fillMaxWidth()
                .heightIn(min = 180.dp)
                .clickable(enabled = isDownloaded) { onClick() },
            color = (if (ownMessage) tgColors.outgoingText else tgColors.incomingLink).copy(alpha = 0.08f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                tgColors.incomingBubble.copy(alpha = 0.85f),
                                tgColors.chatSurface.copy(alpha = 0.95f)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = null,
                        tint = overlayTint.copy(alpha = 0.8f),
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (ownMessage) tgColors.outgoingText else tgColors.incomingText
                    )
                    Text(
                        text = file.type.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = (if (ownMessage) tgColors.outgoingText else tgColors.incomingText).copy(alpha = 0.6f)
                    )
                }
                AttachmentDownloadOverlay(
                    isVideo = true,
                    isDownloaded = isDownloaded,
                    isDownloading = isDownloading,
                    progress = progress,
                    tint = overlayTint,
                    onDownloadClick = onDownloadClick,
                    onCancelClick = onCancelClick,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    } else {
        val normalizedUrl = remember(file.url) { file.url.normalizeNoveoUrl() }
        Surface(
            modifier = Modifier
                .padding(bottom = 2.dp)
                .fillMaxWidth()
                .clickable { normalizedUrl?.let { onClick() } },
            color = (if (ownMessage) tgColors.outgoingText else tgColors.incomingLink).copy(alpha = 0.08f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background((if (ownMessage) tgColors.outgoingText else tgColors.incomingLink).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        tint = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (ownMessage) tgColors.outgoingText else tgColors.incomingText
                    )
                    Text(
                        text = file.type.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = (if (ownMessage) tgColors.outgoingText else tgColors.incomingText).copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachmentDownloadOverlay(
    isVideo: Boolean,
    isDownloaded: Boolean,
    isDownloading: Boolean,
    progress: Float,
    tint: Color,
    onDownloadClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isDownloaded && !isVideo) return

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.42f)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clickable(enabled = !isDownloading && !isDownloaded) { onDownloadClick() },
                contentAlignment = Alignment.Center
            ) {
                when {
                    isDownloading -> {
                        Box(contentAlignment = Alignment.Center) {
                            DownloadProgressGlyph(progress = progress, tint = tint)
                            IconButton(onClick = onCancelClick) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Cancel",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    isDownloaded && isVideo -> Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                    else -> Icon(
                        imageVector = Icons.Outlined.Download,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        if (isDownloading) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = "${(progress.coerceIn(0f, 1f) * 100).roundToInt()}%",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun DownloadProgressGlyph(progress: Float, tint: Color) {
    val clamped = progress.coerceIn(0f, 1f)
    Canvas(modifier = Modifier.size(34.dp)) {
        val stroke = 5.dp.toPx()
        val gap = (1f - clamped) * 260f
        drawArc(
            color = tint,
            startAngle = -90f + (gap / 2f),
            sweepAngle = 360f - gap,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
    }
}

@Composable
private fun ContactsModal(
    strings: NoveoStrings,
    users: List<UserSummary>,
    chats: List<ChatSummary>,
    selfUserId: String?,
    onClose: () -> Unit,
    onMessage: (String) -> Unit,
    onOpenProfile: (String) -> Unit
) {
    Surface(shape = RoundedCornerShape(28.dp), tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth().height(560.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ModalHeader(title = strings.allContacts, onClose = onClose)
            if (users.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strings.noContacts,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(users, key = { it.id }) { user ->
                        ContactRow(
                            user = user,
                            strings = strings,
                            existingChat = findDirectChatForUser(chats, selfUserId, user.id),
                            onMessage = { onMessage(user.id) },
                            onOpenProfile = { onOpenProfile(user.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactRow(
    user: UserSummary,
    strings: NoveoStrings,
    existingChat: ChatSummary?,
    onMessage: () -> Unit,
    onOpenProfile: () -> Unit
) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f).clickable(onClick = onOpenProfile),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileCircle(name = user.username, imageUrl = user.avatarUrl, size = 46.dp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(user.username, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (user.isVerified) {
                            Spacer(Modifier.width(4.dp))
                            VerifiedIcon(modifier = Modifier.size(14.dp))
                        }
                    }
                    Text(
                        user.handle ?: user.bio.ifBlank { if (user.isOnline) strings.online else strings.offline },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onMessage) {
                Text(if (existingChat != null) strings.messageButton else strings.open)
            }
        }
    }
}

@Composable
private fun CreateChannelModal(
    strings: NoveoStrings,
    onCreate: (String, String, String?, String?) -> Unit,
    onClose: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var handle by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("group") }

    Surface(shape = RoundedCornerShape(28.dp), tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ModalHeader(title = strings.newChat, onClose = onClose)
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(strings.newChat) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = handle, onValueChange = { handle = it }, label = { Text(strings.handleOptional) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text(strings.bioOptional) }, modifier = Modifier.fillMaxWidth())
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { type = "group" },
                        modifier = Modifier.weight(1f),
                        border = if (type == "group") BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonBorder
                    ) { Text(strings.group) }
                    OutlinedButton(
                        onClick = { type = "channel" },
                        modifier = Modifier.weight(1f),
                        border = if (type == "channel") BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonBorder
                    ) { Text(strings.channel) }
                }
                
                Button(
                    onClick = { 
                        if (name.isNotBlank()) {
                            onCreate(name, type, handle.takeIf { it.isNotBlank() }, bio.takeIf { it.isNotBlank() })
                            onClose()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = name.isNotBlank()
                ) { Text(strings.create) }
            }
        }
    }
}

@Composable
private fun SettingsModal(
    state: AppUiState,
    strings: NoveoStrings,
    section: SettingsSection,
    onSectionChange: (SettingsSection) -> Unit,
    onClose: () -> Unit,
    onLogout: () -> Unit,
    currentTheme: ThemePreset,
    onThemeChange: (ThemePreset) -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    onChangePassword: (String, String) -> Unit,
    onDeleteAccount: (String) -> Unit,
    onSetLanguage: (String) -> Unit,
    onCheckUpdate: () -> Unit,
    onSetBetaUpdatesEnabled: (Boolean) -> Unit,
    onSetDoubleTapReaction: (String) -> Unit,
    onUpdateNotificationSettings: (NotificationSettings) -> Unit,
    onRequestBatteryOptimization: () -> Unit,
    onRequestPermission: () -> Unit
) {
    val me = state.session?.userId?.let { state.usersById[it] }
    Surface(shape = RoundedCornerShape(28.dp), tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth().height(620.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ModalHeader(
                title = when (section) {
                    SettingsSection.MENU -> strings.settings
                    SettingsSection.SUBSCRIPTION -> strings.subscription
                    SettingsSection.PROFILE -> strings.profile
                    SettingsSection.ACCOUNT -> strings.account
                    SettingsSection.PREFERENCES -> strings.preferences
                    SettingsSection.THEME -> strings.themes
                    SettingsSection.NOTIFICATIONS -> strings.notificationSettings
                },
                onClose = onClose,
                onBack = when (section) {
                    SettingsSection.MENU -> null
                    SettingsSection.THEME -> ({ onSectionChange(SettingsSection.PREFERENCES) })
                    SettingsSection.NOTIFICATIONS -> ({ onSectionChange(SettingsSection.PREFERENCES) })
                    else -> ({ onSectionChange(SettingsSection.MENU) })
                }
            )
            Crossfade(targetState = section, label = "settings_section") { current ->
                when (current) {
                    SettingsSection.MENU -> SettingsMenu(strings, onSectionChange)
                    SettingsSection.SUBSCRIPTION -> SettingsSubscriptionSection(strings)
                    SettingsSection.PROFILE -> SettingsProfileSection(strings, me, onUpdateProfile)
                    SettingsSection.ACCOUNT -> SettingsAccountSection(strings, state, onLogout, onChangePassword, onDeleteAccount)
                    SettingsSection.PREFERENCES -> SettingsPreferencesSection(state, strings, onSectionChange, onSetLanguage, onCheckUpdate, onSetBetaUpdatesEnabled, onSetDoubleTapReaction, currentTheme, onThemeChange, onRequestBatteryOptimization)
                    SettingsSection.THEME -> SettingsThemeSection(strings, currentTheme, onThemeChange)
                    SettingsSection.NOTIFICATIONS -> SettingsNotificationSection(state, strings, onUpdateNotificationSettings, onRequestPermission)
                }
            } // Build fix pass 2
        }
    }
}

@Composable
private fun SettingsMenu(strings: NoveoStrings, onSectionChange: (SettingsSection) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SettingsRow(strings.subscription, Icons.Outlined.Star) { onSectionChange(SettingsSection.SUBSCRIPTION) }
        SettingsRow(strings.profile, Icons.Outlined.Person) { onSectionChange(SettingsSection.PROFILE) }
        SettingsRow(strings.account, Icons.Outlined.AccountCircle) { onSectionChange(SettingsSection.ACCOUNT) }
        SettingsRow(strings.preferences, Icons.Outlined.Settings) { onSectionChange(SettingsSection.PREFERENCES) }
    }
}

@Composable
private fun SettingsSubscriptionSection(strings: NoveoStrings) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DetailCard(title = strings.premiumTitle, body = strings.premiumBody)
        DetailCard(title = strings.walletTitle, body = strings.walletBody)
    }
}

@Composable
private fun SettingsProfileSection(strings: NoveoStrings, me: UserSummary?, onUpdateProfile: (String, String) -> Unit) {
    var username by remember(me) { mutableStateOf(me?.username ?: "") }
    var bio by remember(me) { mutableStateOf(me?.bio ?: "") }

    Column(
        modifier = Modifier.fillMaxSize().padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileCircle(name = me?.username ?: "Me", imageUrl = me?.avatarUrl, size = 90.dp)
        Spacer(Modifier.height(16.dp))
        
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(strings.displayName) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text(strings.bio) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { onUpdateProfile(username, bio) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(strings.saveChanges)
        }
    }
}

@Composable
private fun SettingsAccountSection(strings: NoveoStrings, state: AppUiState, onLogout: () -> Unit, onChangePassword: (String, String) -> Unit, onDeleteAccount: (String) -> Unit) {
    var showChangePassword by rememberSaveable { mutableStateOf(false) }
    var showDeleteAccount by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DetailRow(strings.userId, state.session?.userId ?: strings.unknown)
        DetailRow(strings.sessionId, state.session?.sessionId?.ifBlank { "Connected" } ?: "Unavailable")
        DetailRow(strings.expiry, formatExpiry(state.session, strings))
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

        SettingsRow(strings.changePassword, Icons.Outlined.Lock) { showChangePassword = true }
        SettingsRow(strings.deleteAccount, Icons.Outlined.Delete) { showDeleteAccount = true }
        
        Spacer(Modifier.weight(1f))
        
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(strings.logout)
        }
    }

    if (showChangePassword) {
        var oldPw by remember { mutableStateOf("") }
        var newPw by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showChangePassword = false },
            title = { Text(strings.changePassword) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = oldPw, onValueChange = { oldPw = it }, label = { Text(strings.oldPassword) }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
                    OutlinedTextField(value = newPw, onValueChange = { newPw = it }, label = { Text(strings.newPassword) }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (oldPw.isNotBlank() && newPw.isNotBlank()) {
                        onChangePassword(oldPw, newPw)
                        showChangePassword = false
                    }
                }) { Text(strings.update) }
            },
            dismissButton = { OutlinedButton(onClick = { showChangePassword = false }) { Text(strings.cancel) } }
        )
    }

    if (showDeleteAccount) {
        var pw by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDeleteAccount = false },
            title = { Text(strings.deleteAccount) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(strings.deleteConfirmText)
                    OutlinedTextField(value = pw, onValueChange = { pw = it }, label = { Text(strings.passwordPlaceholder) }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pw.isNotBlank()) {
                            onDeleteAccount(pw)
                            showDeleteAccount = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(strings.delete) }
            },
            dismissButton = { OutlinedButton(onClick = { showDeleteAccount = false }) { Text(strings.cancel) } }
        )
    }
}

@Composable
private fun SettingsPreferencesSection(
    state: AppUiState,
    strings: NoveoStrings,
    onSectionChange: (SettingsSection) -> Unit,
    onSetLanguage: (String) -> Unit,
    onCheckUpdate: () -> Unit,
    onSetBetaUpdatesEnabled: (Boolean) -> Unit,
    onSetDoubleTapReaction: (String) -> Unit,
    currentTheme: ThemePreset,
    onThemeChange: (ThemePreset) -> Unit,
    onRequestBatteryOptimization: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var showReactionDialog by rememberSaveable { mutableStateOf(false) }
    val reactionOptions = CONTEXT_MENU_REACTIONS
    val languages = listOf(
        "English" to "en",
        "Persian (فارسی)" to "fa",
        "Russian (Русский)" to "ru",
        "Chinese (中文)" to "zh",
        "German (Deutsch)" to "de",
        "French (Français)" to "fr",
        "Spanish (Español)" to "es",
        "Arabic (العربية)" to "ar",
        "Turkish (Türkçe)" to "tr"
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SettingsRow(strings.themes, Icons.Outlined.Palette) { onSectionChange(SettingsSection.THEME) }
        SettingsRow(strings.language, Icons.Outlined.Language) { showLanguageDialog = true }
        SettingsRow(strings.notificationSettings, Icons.Outlined.Notifications) { onSectionChange(SettingsSection.NOTIFICATIONS) }

        val updateText = when {
            state.isCheckingUpdate -> strings.checkingForUpdates
            state.updateInfo != null && state.updateInfo.isAvailable && !state.updateInfo.isDismissed -> strings.updateAvailable.format(state.updateInfo.version)
            state.updateInfo != null && !state.updateInfo.isAvailable -> strings.youAreUpdated
            else -> strings.checkForUpdates
        }
        SettingsRow(updateText, Icons.Outlined.History) { onCheckUpdate() }
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSetBetaUpdatesEnabled(!state.betaUpdatesEnabled) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(strings.betaUpdates, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(strings.betaUpdatesBody, style = MaterialTheme.typography.bodySmall)
                }
                androidx.compose.material3.Switch(
                    checked = state.betaUpdatesEnabled,
                    onCheckedChange = onSetBetaUpdatesEnabled
                )
            }
        }
        SettingsRow("${strings.doubleTapReaction}: ${state.doubleTapReaction.ifBlank { "❤" }}", Icons.Outlined.Star) {
            showReactionDialog = true
        }

        if (state.isBatteryOptimized) {
            Spacer(Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(strings.batteryOptimization, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(strings.batteryOptimizationBody, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onRequestBatteryOptimization) {
                        Text(strings.requestPermission)
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

        DetailCard(title = strings.privacy, body = strings.privacyBody)
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(strings.selectLanguage) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(languages) { (name, code) ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                onSetLanguage(code)
                                showLanguageDialog = false
                            },
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Text(name, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showLanguageDialog = false }) { Text(strings.cancel) } }
        )
    }

    if (showReactionDialog) {
        AlertDialog(
            onDismissRequest = { showReactionDialog = false },
            title = { Text(strings.doubleTapReaction) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(strings.doubleTapReactionBody)
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(48.dp),
                        modifier = Modifier.heightIn(max = 320.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(reactionOptions) { reaction ->
                            Surface(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable {
                                        onSetDoubleTapReaction(reaction)
                                        showReactionDialog = false
                                    },
                                color = if (reaction == state.doubleTapReaction.ifBlank { "❤" }) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    Color.Transparent
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = reaction, fontSize = 22.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showReactionDialog = false }) { Text(strings.cancel) } }
        )
    }
}

@Composable
private fun SettingsNotificationSection(
    state: AppUiState,
    strings: NoveoStrings,
    onUpdateNotificationSettings: (NotificationSettings) -> Unit,
    onRequestPermission: () -> Unit
) {
    val settings = state.notificationSettings
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NotificationToggle(strings.enableNotifications, settings.enabled) { enabled ->
            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                onRequestPermission()
            } else {
                onUpdateNotificationSettings(settings.copy(enabled = enabled))
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        NotificationToggle(strings.notifyDms, settings.dms) {
            onUpdateNotificationSettings(settings.copy(dms = it))
        }
        NotificationToggle(strings.notifyGroups, settings.groups) {
            onUpdateNotificationSettings(settings.copy(groups = it))
        }
        NotificationToggle(strings.notifyChannels, settings.channels) {
            onUpdateNotificationSettings(settings.copy(channels = it))
        }
    }
}

@Composable
private fun NotificationToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) }.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        androidx.compose.material3.Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
@Composable
private fun SettingsThemeSection(strings: NoveoStrings, currentTheme: ThemePreset, onThemeChange: (ThemePreset) -> Unit) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val themeSections = listOf(
            ThemeSection(
                title = strings.themeLight,
                subtitle = strings.themeLightDesc,
                presets = listOf(ThemePreset.LIGHT, ThemePreset.SKY_LIGHT, ThemePreset.SUNSET_LIGHT, ThemePreset.SNOWY_DAYDREAM)
            ),
            ThemeSection(
                title = strings.themeDark,
                subtitle = strings.themeDarkDesc,
                presets = listOf(ThemePreset.DARK, ThemePreset.OCEAN_DARK, ThemePreset.PLUM_DARK, ThemePreset.OLED_DARK)
            ),
            ThemeSection(
                title = strings.themePremium,
                subtitle = strings.themePremiumDesc,
                presets = listOf(ThemePreset.SUNSET_SHIMMER, ThemePreset.CHERRY_RED, ThemePreset.RAINBOW_RAGEBAIT)
            )
        )

        themeSections.forEach { section ->
            ThemeSectionBlock(
                strings = strings,
                section = section,
                currentTheme = currentTheme,
                onThemeChange = onThemeChange
            )
        }
    }
}

@Composable
private fun ThemeSectionBlock(
    strings: NoveoStrings,
    section: ThemeSection,
    currentTheme: ThemePreset,
    onThemeChange: (ThemePreset) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(section.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(section.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        section.presets.forEach { preset ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onThemeChange(preset) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (preset == currentTheme) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(preset.label, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    if (preset == currentTheme) {
                        Text(strings.themeSelected, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsChangelogSection(strings: NoveoStrings) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DetailCard(title = strings.version, body = localizeDigits(CLIENT_VERSION, strings.languageCode))
        DetailCard(title = strings.whatNew, body = strings.changelogBody)
    }
}

@Composable
private fun ProfileModal(
    strings: NoveoStrings,
    user: UserSummary,
    chats: List<ChatSummary>,
    selfUserId: String?,
    onClose: () -> Unit,
    onMessage: () -> Unit,
    onLeaveChat: ((String) -> Unit)? = null,
    animateEntrance: Boolean = false
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    
    val expandedHeight = 320.dp
    val collapsedHeight = 56.dp
    val expandedHeightPx = with(density) { expandedHeight.toPx() }
    val collapsedHeightPx = with(density) { collapsedHeight.toPx() }
    
    val fraction = remember { derivedStateOf { 
        if (listState.firstVisibleItemIndex > 0) 1f 
        else (listState.firstVisibleItemScrollOffset.toFloat() / (expandedHeightPx - collapsedHeightPx)).coerceIn(0f, 1f)
    } }.value
    
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenWidth = maxWidth
            
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = expandedHeight, bottom = 100.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Info Section (Telegram Style)
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                InfoItem(label = strings.displayName, value = user.username)
                                if (!user.handle.isNullOrBlank()) {
                                    InfoItem(label = strings.handle, value = user.handle, onClick = {
                                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(user.handle))
                                    })
                                }
                                if (user.bio.isNotBlank()) {
                                    InfoItem(label = strings.about, value = user.bio)
                                }
                                val joinedDateText = remember(user.joinedAt) {
                                    val joinedAt = user.joinedAt
                                    if (joinedAt != null && joinedAt > 0) {
                                        val date = Date(joinedAt * 1000L)
                                        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(date)
                                    } else {
                                        "April 2026"
                                    }
                                }
                                InfoItem(label = strings.joinDate, value = joinedDateText)
                            }
                        }

                        Button(
                            onClick = onMessage, 
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) { 
                            Text(strings.sendMessage) 
                        }
                        
                        Spacer(Modifier.height(300.dp))
                    }
                }
            }
            
            // Collapsing Header
            val currentHeaderHeight = lerpDp(expandedHeight, collapsedHeight, fraction)
            Surface(
                modifier = Modifier.fillMaxWidth().height(currentHeaderHeight),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = lerpDp(0.dp, 4.dp, fraction)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Back Button
                    HeaderIconButton(
                        icon = Icons.AutoMirrored.Outlined.ArrowBack,
                        onClick = onClose,
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                    )
                    
                    val avatarSize = lerpDp(120.dp, 38.dp, fraction)
                    
                    // Avatar position calculation
                    val expandedAvatarX = (screenWidth / 2) - (avatarSize / 2)
                    val collapsedAvatarX = 52.dp // Next to back button
                    val avatarX = lerpDp(expandedAvatarX, collapsedAvatarX, fraction)
                    
                    val expandedAvatarY = (expandedHeight / 2) - (avatarSize / 2) - 20.dp
                    val collapsedAvatarY = (collapsedHeight / 2) - (avatarSize / 2)
                    val avatarY = lerpDp(expandedAvatarY, collapsedAvatarY, fraction)
                    
                    Box(modifier = Modifier.offset(x = avatarX, y = avatarY)) {
                        ProfileCircle(name = user.username, imageUrl = user.avatarUrl, size = avatarSize)
                    }
                    
                    // Expanded Name/Status
                    if (fraction < 0.5f) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = expandedAvatarY + avatarSize + 16.dp)
                                .alpha((1f - fraction * 2f).coerceIn(0f, 1f)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    user.username, 
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                if (user.isVerified) {
                                    Spacer(Modifier.width(6.dp))
                                    VerifiedIcon(modifier = Modifier.size(18.dp))
                                }
                            }
                            val lastSeenText = remember(user, strings) {
                                if (user.isOnline) strings.online
                                else formatLastSeen(user.lastSeen, strings)
                            }
                            Text(
                                lastSeenText, 
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (user.isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Collapsed Name/Status
                    if (fraction > 0.5f) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 100.dp) // Offset by back button + avatar
                                .alpha(((fraction - 0.5f) * 2f).coerceIn(0f, 1f))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    user.username,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (user.isVerified) {
                                    Spacer(Modifier.width(4.dp))
                                    VerifiedIcon(modifier = Modifier.size(14.dp))
                                }
                            }
                            val lastSeenText = remember(user, strings) {
                                if (user.isOnline) strings.online
                                else formatLastSeen(user.lastSeen, strings)
                            }
                            Text(
                                lastSeenText,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (user.isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String, onClick: (() -> Unit)? = null) {
    Column(
        modifier = if (onClick != null) Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp) else Modifier
    ) {
        Text(value, style = MaterialTheme.typography.bodyLarge)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun GroupInfoModal(
    chat: ChatSummary, 
    strings: NoveoStrings, 
    state: AppUiState,
    onOpenProfile: (String) -> Unit,
    onClose: () -> Unit,
    onJoinChat: (String) -> Unit,
    onLeaveChat: (String) -> Unit,
    onOpenChat: (String) -> Unit,
    animateEntrance: Boolean = false
) {
    val usersById = state.usersById
    val sessionUserId = state.session?.userId
    val isSavedMessages = remember(chat, sessionUserId) { chat.isSavedMessagesChat(sessionUserId) }
    val savedMessagesCount = state.messagesByChat[chat.id]?.size ?: state.messages.size
    val chatTitle = remember(chat.title, strings) {
        if (isSavedMessages) strings.savedMessages
        else chat.title.ifBlank { strings.chatInfo }
    }
    val profileUserId = remember(chat, sessionUserId) { resolveProfileUserId(chat, sessionUserId) }
    val isVerified = chat.isVerified || (profileUserId?.let { usersById[it]?.isVerified } == true)
    
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    
    val expandedHeight = 320.dp
    val collapsedHeight = 56.dp
    val expandedHeightPx = with(density) { expandedHeight.toPx() }
    val collapsedHeightPx = with(density) { collapsedHeight.toPx() }
    
    val fraction = remember { derivedStateOf { 
        if (listState.firstVisibleItemIndex > 0) 1f 
        else (listState.firstVisibleItemScrollOffset.toFloat() / (expandedHeightPx - collapsedHeightPx)).coerceIn(0f, 1f)
    } }.value
    
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenWidth = maxWidth
            
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = expandedHeight, bottom = 100.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Info Section
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                InfoItem(label = strings.title, value = chatTitle)
                                if (!chat.handle.isNullOrBlank()) {
                                    val normalizedHandle = chat.handle.removePrefix("@")
                                    InfoItem(label = strings.link, value = "@$normalizedHandle", onClick = {
                                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString("@$normalizedHandle"))
                                    })
                                }
                                if (!isSavedMessages) {
                                    InfoItem(label = strings.type, value = formatChatType(chat.chatType, strings))
                                }
                                if (isSavedMessages) {
                                    InfoItem(
                                        label = strings.messages,
                                        value = formatMessagesCount(savedMessagesCount, strings)
                                    )
                                }
                                
                                val isMember = chat.memberIds.contains(state.session?.userId)
                                if (chat.chatType != "private") {
                                    Spacer(Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { onOpenChat(chat.id) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(strings.open)
                                        }
                                        
                                        if (!isMember) {
                                            Button(
                                                onClick = { onJoinChat(chat.id) },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            ) {
                                                Text(strings.join)
                                            }
                                        } else if (!isSavedMessages) {
                                            Button(
                                                onClick = { onLeaveChat(chat.id) },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                                )
                                            ) {
                                                Text(strings.leave)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Text(
                            if (isSavedMessages) strings.messages else strings.members,
                            style = MaterialTheme.typography.titleMedium, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                items(if (isSavedMessages) emptyList() else chat.memberIds, key = { it }) { memberId ->
                    val user = usersById[memberId]
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Card(
                            shape = RoundedCornerShape(12.dp), 
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            onClick = { onOpenProfile(memberId) }
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                ProfileCircle(name = user?.username ?: memberId, imageUrl = user?.avatarUrl, size = 40.dp)
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(user?.username ?: memberId, fontWeight = FontWeight.SemiBold)
                                        if (user?.isVerified == true) {
                                            Spacer(Modifier.width(4.dp))
                                            VerifiedIcon(modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    val lastSeenText = remember(user, strings) {
                                        if (user?.isOnline == true) strings.online
                                        else formatLastSeen(user?.lastSeen, strings)
                                    }
                                    Text(
                                        lastSeenText, 
                                        style = MaterialTheme.typography.bodySmall, 
                                        color = if (user?.isOnline == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                
                item {
                    Spacer(Modifier.height(300.dp))
                }
            }
            
            // Collapsing Header
            val currentHeaderHeight = lerpDp(expandedHeight, collapsedHeight, fraction)
            Surface(
                modifier = Modifier.fillMaxWidth().height(currentHeaderHeight),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = lerpDp(0.dp, 4.dp, fraction)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Back Button
                    HeaderIconButton(
                        icon = Icons.AutoMirrored.Outlined.ArrowBack,
                        onClick = onClose,
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                    )
                    
                    val avatarSize = lerpDp(120.dp, 38.dp, fraction)
                    
                    // Avatar position calculation
                    val expandedAvatarX = (screenWidth / 2) - (avatarSize / 2)
                    val collapsedAvatarX = 52.dp 
                    val avatarX = lerpDp(expandedAvatarX, collapsedAvatarX, fraction)
                    
                    val expandedAvatarY = (expandedHeight / 2) - (avatarSize / 2) - 20.dp
                    val collapsedAvatarY = (collapsedHeight / 2) - (avatarSize / 2)
                    val avatarY = lerpDp(expandedAvatarY, collapsedAvatarY, fraction)
                    
                    Box(modifier = Modifier.offset(x = avatarX, y = avatarY)) {
                        ProfileCircle(name = chatTitle, imageUrl = chat.avatarUrl, size = avatarSize, isSavedMessages = isSavedMessages)
                    }
                    
                    // Expanded Title/Subtitle
                    if (fraction < 0.5f) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = expandedAvatarY + avatarSize + 16.dp)
                                .alpha((1f - fraction * 2f).coerceIn(0f, 1f)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    chatTitle, 
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isVerified) {
                                    Spacer(Modifier.width(6.dp))
                                    VerifiedIcon(modifier = Modifier.size(18.dp))
                                }
                            }
                            Text(
                                if (isSavedMessages) formatMessagesCount(savedMessagesCount, strings) else formatMembersCount(chat.memberIds.size, strings),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Collapsed Title/Subtitle
                    if (fraction > 0.5f) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 100.dp) 
                                .alpha(((fraction - 0.5f) * 2f).coerceIn(0f, 1f))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    chatTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (isVerified) {
                                    Spacer(Modifier.width(4.dp))
                                    VerifiedIcon(modifier = Modifier.size(14.dp))
                                }
                            }
                            Text(
                                if (isSavedMessages) formatMessagesCount(savedMessagesCount, strings) else formatMembersCount(chat.memberIds.size, strings),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuSheet(
    state: AppUiState,
    strings: NoveoStrings,
    onOpenContacts: () -> Unit,
    onOpenCreate: () -> Unit,
    onOpenStars: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val me = state.session?.userId?.let { state.usersById[it] }
    Column(
        modifier = Modifier
            .width(296.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(strings.menu, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        MenuRow(strings.allContacts, Icons.Outlined.Info, onOpenContacts)
        MenuRow(strings.newChat, Icons.Outlined.Menu, onOpenCreate)
        Card(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenStars),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFFFD700))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(strings.stars, fontWeight = FontWeight.SemiBold)
                    Text("${localizeDigits(state.wallet?.balanceLabel ?: "0.00", strings.languageCode)} ${strings.stars}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        MenuRow(strings.settings, Icons.Outlined.Settings, onOpenSettings)
        Spacer(Modifier.weight(1f))
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(strings.brandName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(localizeDigits(CLIENT_VERSION, strings.languageCode), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MenuRow(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(text, fontWeight = FontWeight.SemiBold)
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun SettingsRow(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(text, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ChatRow(
    chat: ChatSummary,
    strings: NoveoStrings,
    usersById: Map<String, UserSummary>,
    currentUserId: String?,
    selected: Boolean,
    onClick: () -> Unit
) {
    val chatTitle = remember(chat, strings, currentUserId) {
        if (chat.isSavedMessagesChat(currentUserId)) strings.savedMessages
        else chat.title.ifBlank { strings.chatInfo }
    }
    val profileUserId = remember(chat, currentUserId) { resolveProfileUserId(chat, currentUserId) }
    val isVerified = chat.isVerified || (profileUserId?.let { usersById[it]?.isVerified } == true)
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ProfileCircle(name = chatTitle, imageUrl = chat.avatarUrl, isSavedMessages = chat.isSavedMessagesChat(currentUserId))
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(chatTitle, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (isVerified) {
                        Spacer(Modifier.width(4.dp))
                        VerifiedIcon()
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(localizeMessagePreview(chat.lastMessagePreview, strings).ifBlank { strings.noMessagesYet }, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall)
            }
            if (chat.unreadCount > 0) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primary).padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(localizeDigits(chat.unreadCount.toString(), strings.languageCode), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}


@Composable
private fun WelcomePane(strings: NoveoStrings, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Text(strings.brandName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(strings.selectChatHint, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ModalHost(visible: Boolean, onDismiss: () -> Unit, fullscreen: Boolean = false, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(250)),
        exit = fadeOut(animationSpec = tween(250))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (fullscreen) MaterialTheme.colorScheme.background else Color.Black.copy(alpha = 0.5f))
                .then(
                    if (fullscreen) Modifier // No close-on-click for fullscreen
                    else Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = if (fullscreen) Modifier.fillMaxSize() else Modifier
                    .padding(18.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
            ) {
                content()
            }
        }
    }
}

@Composable
private fun ModalHeader(title: String, onClose: () -> Unit, onBack: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            HeaderIconButton(icon = Icons.AutoMirrored.Outlined.ArrowBack, onClick = onBack)
            Spacer(Modifier.width(8.dp))
        }
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        HeaderIconButton(icon = Icons.Outlined.Close, onClick = onClose)
    }
    HorizontalDivider()
}

@Composable
private fun DetailCard(title: String, body: String) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(body)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontWeight = FontWeight.Medium)
            Spacer(Modifier.width(16.dp))
            Text(value, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.End)
        }
    }
}

@Composable
private fun ProfileCircle(name: String, imageUrl: String?, size: Dp = 40.dp, modifier: Modifier = Modifier, isSavedMessages: Boolean = false) {
    if (isSavedMessages) {
        Box(
            modifier = modifier
                .size(size)

                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Bookmark, contentDescription = null, tint = Color.White, modifier = Modifier.size(size * 0.6f))
        }
        return
    }

    val resolvedImageUrl = remember(imageUrl) { imageUrl.normalizeNoveoUrl() }
    val isDefaultAvatar = remember(resolvedImageUrl) { resolvedImageUrl?.endsWith("default.png") == true }
    
    val fallback = @Composable {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(name.firstOrNull()?.uppercase() ?: "N", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }

    if (!resolvedImageUrl.isNullOrBlank() && !isDefaultAvatar) {
        AsyncImage(
            model = resolvedImageUrl,
            contentDescription = name,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentScale = ContentScale.Crop
        )
    } else {
        fallback()
    }
}

@Composable
private fun HeaderIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint)
    }
}

@Composable
private fun SeenByModal(
    strings: NoveoStrings,
    message: ChatMessage,
    usersById: Map<String, UserSummary>,
    onClose: () -> Unit,
    onOpenProfile: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = strings.seenBy,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(Icons.Outlined.Close, contentDescription = null)
                }
            }
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            
            val seenUsers = remember(message.seenBy, usersById) {
                message.seenBy.mapNotNull { usersById[it] }
            }
            
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(seenUsers) { user ->
                    ContactRow(
                        user = user,
                        strings = strings,
                        existingChat = null,
                        onMessage = {
                            onClose()
                            onOpenProfile(user.id)
                        },
                        onOpenProfile = {
                            onClose()
                            onOpenProfile(user.id)
                        }
                    )
                }
            }
        }
    }
}

private fun resolveProfileUserId(chat: ChatSummary?, selfUserId: String?): String? {
    if (chat == null || selfUserId.isNullOrBlank()) return null
    if (chat.chatType != "private") return null
    return chat.memberIds.firstOrNull { it != selfUserId }
}

private fun findDirectChatForUser(chats: List<ChatSummary>, selfUserId: String?, userId: String): ChatSummary? {
    if (selfUserId.isNullOrBlank()) return null
    return chats.firstOrNull { chat ->
        chat.chatType == "private" && chat.memberIds.contains(selfUserId) && chat.memberIds.contains(userId)
    }
}

private fun formatExpiry(session: Session?, strings: NoveoStrings): String {
    val value = session?.expiresAt ?: 0L
    if (value <= 0L) return strings.unknown
    return runCatching {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        localizeDigits(sdf.format(Date(value)), strings.languageCode)
    }.getOrElse { strings.unknown }
}

private fun String?.normalizeNoveoUrl(): String? {
    val value = this?.trim().orEmpty().replace("\\", "/")
    if (value.isBlank()) return null
    if (value.startsWith("data:")) return value
    val noCaptchaMatch = Regex(
        pattern = "^(?:(?:https?|wss?)://)?server_no_captcha(?::\\d+)?(?:(/.*)?)$",
        option = RegexOption.IGNORE_CASE
    ).matchEntire(value)
    if (noCaptchaMatch != null) {
        val path = noCaptchaMatch.groupValues.getOrNull(1).orEmpty()
        return if (path.isBlank()) NOVEO_BASE_URL else "$NOVEO_BASE_URL$path"
    }
    if (value.startsWith("//")) return "https:$value"
    if (value.startsWith("http://") || value.startsWith("https://")) return value
    if (value.startsWith("ws://")) return value.replaceFirst("ws://", "http://")
    if (value.startsWith("wss://")) return value.replaceFirst("wss://", "https://")
    
    val normalized = if (value.startsWith("/")) value else "/$value"
    return "$NOVEO_BASE_URL$normalized"
}

@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun FullscreenMediaModal(attachment: MessageFileAttachment, localPath: String, onDismiss: () -> Unit) {
    val normalizedUrl = remember(attachment.url) { attachment.url.normalizeNoveoUrl() }
    val context = LocalContext.current
    val isVideo = remember(attachment) { attachment.isVideo() }
    val mediaUri = remember(localPath, normalizedUrl) {
        Uri.fromFile(File(localPath)).takeIf { localPath.isNotBlank() } ?: normalizedUrl?.let(Uri::parse)
    }

    Surface(
        color = Color.Black,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isVideo && mediaUri != null) {
                val exoPlayer = remember {
                    androidx.media3.exoplayer.ExoPlayer.Builder(context).build().apply {
                        setMediaItem(androidx.media3.common.MediaItem.fromUri(mediaUri))
                        prepare()
                        playWhenReady = true
                    }
                }
                androidx.compose.runtime.DisposableEffect(Unit) {
                    onDispose { exoPlayer.release() }
                }
                androidx.compose.ui.viewinterop.AndroidView(
                    factory = { ctx ->
                        androidx.media3.ui.PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = true
                            setBackgroundColor(android.graphics.Color.BLACK)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AsyncImage(
                    model = File(localPath),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            
            HeaderIconButton(
                icon = Icons.Outlined.Close,
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(16.dp),
                tint = Color.White
            )
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun AttachmentPicker(
    strings: NoveoStrings,
    onGalleryClick: () -> Unit,
    onFilesClick: () -> Unit,
    onDismiss: () -> Unit,
    tgColors: TelegramThemeColors
) {
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = tgColors.incomingBubble,
        dragHandle = { androidx.compose.material3.BottomSheetDefaults.DragHandle(color = tgColors.headerSubtitle.copy(alpha = 0.4f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = strings.selectSource, 
                style = MaterialTheme.typography.titleMedium,
                color = tgColors.headerTitle,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AttachmentOption(
                    label = strings.gallery,
                    icon = Icons.Outlined.Collections, // Use Collections icon
                    color = Color(0xFF2EA6FF),
                    onClick = onGalleryClick,
                    modifier = Modifier.weight(1f)
                )
                AttachmentOption(
                    label = strings.files,
                    icon = Icons.Outlined.Description,
                    color = Color(0xFF34C759),
                    onClick = onFilesClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AttachmentOption(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun StickerPicker(
    strings: NoveoStrings,
    stickers: List<SavedSticker>,
    onStickerSelected: (SavedSticker) -> Unit,
    tgColors: TelegramThemeColors,
    displayHeight: Dp = 300.dp
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(displayHeight)
            .navigationBarsPadding(),
        color = tgColors.incomingBubble,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.width(44.dp).height(4.dp),
                    color = tgColors.headerSubtitle.copy(alpha = 0.35f),
                    shape = CircleShape
                ) {}
            }
            Text(
                text = strings.stickers,
                color = tgColors.headerTitle,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(stickers.size, key = { index -> stickers[index].url }) { index ->
                    val sticker = stickers[index]
                    val normalizedUrl = remember(sticker.url) { sticker.url.normalizeNoveoUrl() }
                    Surface(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onStickerSelected(sticker) },
                        color = tgColors.chatSurface.copy(alpha = 0.72f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (sticker.type == "tgs") {
                                TgsSticker(
                                    url = normalizedUrl,
                                    modifier = Modifier.fillMaxSize(),
                                    tint = tgColors.headerIcon
                                )
                            } else {
                                AsyncImage(
                                    model = normalizedUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }
                if (stickers.isEmpty()) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(4) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = strings.noSavedStickers,
                                color = tgColors.headerSubtitle,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdateBubble(
    strings: NoveoStrings,
    updateInfo: ir.hienob.noveo.app.UpdateInfo,
    onDismiss: () -> Unit,
    onUpdate: () -> Unit,
    onInstall: () -> Unit
) {
    if (updateInfo.isDismissed || !updateInfo.isAvailable) return

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE8F5E9),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = strings.updateAvailable.format(updateInfo.version),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )
                
                if (updateInfo.isDownloaded) {
                    TextButton(onClick = onInstall, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(strings.install, color = Color(0xFF2E7D32), style = MaterialTheme.typography.labelLarge)
                    }
                } else if (!updateInfo.isDownloading) {
                    TextButton(onClick = onDismiss, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(strings.dismiss, color = Color(0xFF757575), style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(Modifier.width(4.dp))
                    TextButton(onClick = onUpdate, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(strings.update, color = Color(0xFF2E7D32), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            
            if (updateInfo.isDownloading) {
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = updateInfo.downloadProgress,
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = Color(0xFF2E7D32),
                    trackColor = Color(0xFFC8E6C9)
                )
            }
        }
    }
}

@Composable
private fun ForwardChatPicker(
    strings: NoveoStrings,
    chats: List<ChatSummary>,
    onClose: () -> Unit,
    onForward: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth().height(480.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ModalHeader(title = strings.forwarded, onClose = onClose)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chats) { chat ->
                    ChatRow(
                        chat = chat,
                        strings = strings,
                        usersById = emptyMap(),
                        currentUserId = null,
                        selected = false,
                        onClick = { onForward(chat.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AudioPlayer(
    message: ChatMessage,
    isCurrent: Boolean,
    isPlaying: Boolean,
    progress: Float,
    onPlayToggle: () -> Unit,
    onSeek: (Float) -> Unit,
    tgColors: TelegramThemeColors
) {
    val durationText = remember(message.content.file?.size) {
        "Audio"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPlayToggle,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (isCurrent && isPlaying) Icons.Filled.Pause else Icons.Outlined.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = if (isCurrent) tgColors.headerIcon else tgColors.incomingText
            )
        }
        
        Spacer(Modifier.width(8.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            androidx.compose.material3.Slider(
                value = if (isCurrent) progress else 0f,
                onValueChange = onSeek,
                modifier = Modifier.height(24.dp),
                colors = androidx.compose.material3.SliderDefaults.colors(
                    thumbColor = if (isCurrent) tgColors.headerIcon else tgColors.incomingText,
                    activeTrackColor = if (isCurrent) tgColors.headerIcon else tgColors.incomingText
                )
            )
            Text(
                text = durationText,
                style = MaterialTheme.typography.labelSmall,
                color = if (isCurrent) tgColors.headerSubtitle else tgColors.incomingTime
            )
        }
    }
}

@Composable
private fun GlobalAudioMiniPlayer(
    state: AppUiState,
    strings: NoveoStrings,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onSeek: (Float) -> Unit,
    tgColors: TelegramThemeColors
) {
    val audio = state.currentAudioMessage ?: return
    
    Surface(
        color = tgColors.chatSurface,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (state.isAudioPlaying) onPause() else onResume() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (state.isAudioPlaying) Icons.Filled.Pause else Icons.Outlined.PlayArrow,
                    contentDescription = null,
                    tint = tgColors.headerIcon
                )
            }
            
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = audio.content.file?.name ?: strings.brandName,
                    style = MaterialTheme.typography.labelMedium,
                    color = tgColors.headerTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                androidx.compose.material3.LinearProgressIndicator(
                    progress = { state.audioProgress },
                    modifier = Modifier.fillMaxWidth().height(2.dp),
                    color = tgColors.headerIcon,
                    trackColor = tgColors.headerIcon.copy(alpha = 0.2f)
                )
            }
            
            IconButton(onClick = onStop, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Outlined.Close, contentDescription = "Close", tint = tgColors.headerIcon, modifier = Modifier.size(18.dp))
            }
        }
    }
}


@Composable
fun VoiceCallOverlay(
    state: ir.hienob.noveo.data.VoiceChatState,
    strings: NoveoStrings,
    usersById: Map<String, UserSummary>,
    onLeave: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleDeafen: () -> Unit,
    onMinimize: () -> Unit
) {
    var sheetDragOffsetY by remember { mutableStateOf(0f) }
    val hideThresholdPx = with(LocalDensity.current) { 96.dp.toPx() }
    val joinedCount = remember(state.participantIds) { state.participantIds.distinct().size }
    val joinedCountLabel = remember(joinedCount, strings) { formatVoiceJoinedCount(joinedCount, strings) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(0, sheetDragOffsetY.roundToInt()) }
                .pointerInput(onMinimize) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { _, dragAmount ->
                            sheetDragOffsetY = (sheetDragOffsetY + dragAmount).coerceAtLeast(0f)
                        },
                        onDragEnd = {
                            if (sheetDragOffsetY > hideThresholdPx) {
                                onMinimize()
                            }
                            sheetDragOffsetY = 0f
                        },
                        onDragCancel = {
                            sheetDragOffsetY = 0f
                        }
                    )
                },
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(38.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.28f))
                )

                Spacer(Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    IconButton(
                        onClick = onMinimize,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            Icons.Outlined.KeyboardArrowDown,
                            contentDescription = strings.minimize,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = strings.voiceChat,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Text(
                    text = when (state.connectionState) {
                        ir.hienob.noveo.data.VoiceConnectionState.CONNECTING -> strings.connecting
                        ir.hienob.noveo.data.VoiceConnectionState.RECONNECTING -> strings.connecting
                        ir.hienob.noveo.data.VoiceConnectionState.CONNECTED -> joinedCountLabel
                        else -> joinedCountLabel
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (state.participantIds.isEmpty()) {
                        VoiceParticipantListItem(
                            user = null,
                            isSpeaking = false,
                            statusLabel = strings.connecting
                        )
                    } else {
                        state.participantIds.forEach { id ->
                            val user = usersById[id]
                            val isSpeaking = state.activeSpeakers.contains(id)
                            VoiceParticipantListItem(
                                user = user,
                                isSpeaking = isSpeaking,
                                statusLabel = if (isSpeaking) "Speaking" else strings.online
                            )
                        }
                    }
                }

                Spacer(Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = onToggleMute,
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    if (state.isMuted) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = if (state.isMuted) Icons.Outlined.MicOff else Icons.Outlined.Mic,
                                contentDescription = if (state.isMuted) strings.micOn else strings.muted,
                                tint = if (state.isMuted) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(
                            text = if (state.isMuted) strings.muted else strings.micOn,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Button(
                        onClick = onLeave,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(Icons.Outlined.Close, contentDescription = strings.leave, tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = onToggleDeafen,
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    if (state.isDeafened) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = if (state.isDeafened) Icons.Outlined.HeadsetOff else Icons.Outlined.Headset,
                                contentDescription = if (state.isDeafened) strings.audioOn else strings.deafened,
                                tint = if (state.isDeafened) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(
                            text = if (state.isDeafened) strings.deafened else strings.audioOn,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}


private fun formatVoiceJoinedCount(count: Int, strings: NoveoStrings): String {
    val localizedCount = localizeDigits(count.toString(), strings.languageCode)
    val rawText = when (strings.languageCode) {
        "fa" -> "$localizedCount نفر پیوسته‌اند"
        "ar" -> "$localizedCount مشارك"
        "tr" -> "$localizedCount katildi"
        "de" -> if (count == 1) "$localizedCount Person beigetreten" else "$localizedCount Personen beigetreten"
        "ru" -> "$localizedCount участников"
        "zh" -> "$localizedCount 人已加入"
        "es" -> if (count == 1) "$localizedCount usuario unido" else "$localizedCount usuarios unidos"
        "fr" -> if (count == 1) "$localizedCount utilisateur a rejoint" else "$localizedCount utilisateurs ont rejoint"
        else -> if (count == 1) "$localizedCount user joined" else "$localizedCount users joined"
    }
    return if (strings.languageCode == "fa" || strings.languageCode == "ar") "\u200F$rawText" else rawText
}

@Composable
private fun VoiceParticipantListItem(
    user: UserSummary?,
    isSpeaking: Boolean,
    statusLabel: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.58f),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(52.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isSpeaking) {
                    Surface(
                        modifier = Modifier.size(52.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
                    ) {}
                }

                ProfileCircle(
                    name = user?.username ?: "User",
                    imageUrl = user?.avatarUrl,
                    size = 44.dp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = user?.username?.split(" ")?.firstOrNull() ?: "User",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = statusLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isSpeaking) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "LIVE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun IncomingCallOverlay(
    call: SocketEvent.IncomingCall,
    strings: NoveoStrings,
    caller: UserSummary?,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Blurred-like background (using a dark semi-transparent overlay over the avatar)
            if (caller?.avatarUrl != null) {
                AsyncImage(
                    model = caller.avatarUrl.normalizeNoveoUrl(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().alpha(0.3f),
                    contentScale = ContentScale.Crop
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(vertical = 64.dp, horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ProfileCircle(name = caller?.username ?: "User", imageUrl = caller?.avatarUrl, size = 120.dp)
                    Spacer(Modifier.height(32.dp))
                    Text(
                        text = caller?.username ?: "Unknown Caller",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = strings.incomingCall,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = onDecline,
                            modifier = Modifier
                                .size(72.dp)
                                .background(Color(0xFFF44336), CircleShape)
                        ) {
                            Icon(Icons.Outlined.Close, contentDescription = strings.decline, tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(text = strings.decline, style = MaterialTheme.typography.bodyLarge, color = Color.White)
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = onAccept,
                            modifier = Modifier
                                .size(72.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                        ) {
                            Icon(Icons.Outlined.Call, contentDescription = strings.accept, tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(text = strings.accept, style = MaterialTheme.typography.bodyLarge, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceChatTray(
    state: ir.hienob.noveo.data.VoiceChatState,
    strings: NoveoStrings,
    onExpand: () -> Unit,
    onLeave: () -> Unit,
    onToggleMute: () -> Unit,
    tgColors: TelegramThemeColors = telegramColors()
) {
    Surface(
        color = tgColors.chatSurface,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onExpand() },
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleMute,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (state.isMuted) Icons.Outlined.MicOff else Icons.Outlined.Mic,
                    contentDescription = null,
                    tint = if (state.isMuted) MaterialTheme.colorScheme.error else tgColors.headerIcon
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = strings.activeCall,
                    style = MaterialTheme.typography.labelMedium,
                    color = tgColors.headerTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (state.isMuted) strings.muted else strings.micOn,
                    style = MaterialTheme.typography.labelSmall,
                    color = tgColors.headerSubtitle,
                    maxLines = 1
                )
            }

            IconButton(onClick = onLeave, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = null,
                    tint = tgColors.headerSubtitle
                )
            }
        }
    }
}
