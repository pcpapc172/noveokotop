package ir.hienob.noveo.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.InsertEmoticon
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Reply
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

private val ANDROID_CONTEXT_MENU_REACTIONS = listOf(
    "🙏", "👍", "😭", "😍", "🥰", "🙈", "❤️", "🤔", "🤣", "😘", "😱", "💯", "👎", "🔥", "💩", "🤯",
    "💔", "☃️", "😁", "🎉", "🤷", "😇", "🎃", "🗿", "🥴", "😐", "👏", "🤬", "😢", "🤩", "🤮", "👌",
    "🕊️", "🤡", "🐳", "💘", "🌭", "⚡", "🍌", "🏆", "🤨", "🍓", "🍾", "🖕", "😈", "😴", "🤓", "👻",
    "👨‍💻", "👀", "🙉", "😨", "🤝", "✍️", "🤗", "🫡", "🎅", "🎄", "💅", "🤪", "🆒", "🦄", "💊", "🙊",
    "😎", "👾"
)
private val ANDROID_CONTEXT_MENU_QUICK_REACTIONS = ANDROID_CONTEXT_MENU_REACTIONS.take(7)

private val NoveoAndroidSendPlaneIcon: ImageVector = ImageVector.Builder(
    name = "NoveoAndroidSendPlane",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 72f,
    viewportHeight = 72f
).apply {
    path(
        fill = SolidColor(Color.White),
        pathFillType = PathFillType.EvenOdd
    ) {
        moveTo(6.232f, 35.046f)
        curveTo(22.115f, 28.147f, 32.706f, 23.599f, 38.006f, 21.401f)
        curveTo(53.136f, 15.126f, 56.28f, 14.036f, 58.329f, 14f)
        curveTo(58.78f, 13.992f, 59.788f, 14.104f, 60.441f, 14.632f)
        curveTo(60.992f, 15.078f, 61.143f, 15.68f, 61.216f, 16.103f)
        curveTo(61.289f, 16.526f, 61.379f, 17.489f, 61.307f, 18.241f)
        curveTo(60.487f, 26.831f, 56.939f, 47.675f, 55.134f, 57.295f)
        curveTo(54.371f, 61.366f, 52.867f, 62.731f, 51.411f, 62.865f)
        curveTo(48.247f, 63.155f, 45.844f, 60.78f, 42.78f, 58.777f)
        curveTo(37.985f, 55.643f, 35.276f, 53.692f, 30.621f, 50.634f)
        curveTo(25.242f, 47.1f, 28.729f, 45.157f, 31.795f, 41.983f)
        curveTo(32.597f, 41.152f, 46.537f, 28.51f, 46.807f, 27.363f)
        curveTo(46.841f, 27.22f, 46.872f, 26.685f, 46.554f, 26.403f)
        curveTo(46.235f, 26.12f, 45.765f, 26.217f, 45.426f, 26.294f)
        curveTo(44.945f, 26.402f, 37.284f, 31.451f, 22.444f, 41.438f)
        curveTo(20.27f, 42.927f, 18.3f, 43.652f, 16.536f, 43.614f)
        curveTo(14.591f, 43.572f, 10.849f, 42.518f, 8.067f, 41.616f)
        curveTo(4.655f, 40.51f, 2.448f, 39.938f, 2.684f, 38.06f)
        curveTo(2.807f, 37.082f, 3.99f, 36.077f, 6.232f, 35.046f)
        close()
    }
}.build()

private enum class AndroidHomeModal {
    CONTACTS,
    NEW_CHAT,
    SETTINGS,
    PROFILE,
    ACCOUNT,
    ATTACHMENTS,
    STICKERS,
    CHAT_INFO,
    FORWARD
}

/**
 * Android-home-compatible state consumed by the shared desktop home surface.
 * Keep this state factual: desktop fills it from the server, not from mock/demo data.
 */
data class NoveoHomeFrameState(
    val currentUserId: String? = null,
    val currentUsername: String = "",
    val currentUserBio: String = "",
    val chats: List<NoveoHomeChat> = emptyList(),
    val selectedChatId: String? = null,
    val messages: List<NoveoHomeMessage> = emptyList(),
    val totalUnreadCount: Int = 0,
    val loading: Boolean = false,
    val error: String? = null,
    val isSendingMessage: Boolean = false,
    val canSendMessage: Boolean = true,
    val activeCallTitle: String? = null,
    val pendingAttachment: NoveoPendingAttachment? = null
) {
    val selectedChat: NoveoHomeChat?
        get() = chats.firstOrNull { it.id == selectedChatId }
}

data class NoveoHomeChat(
    val id: String,
    val title: String,
    /** Chat-list preview text. Do not use this in the open-chat header. */
    val subtitle: String = "",
    /** Presence/member status for the open-chat header, matching Android behavior. */
    val headerSubtitle: String = "",
    val time: String = "",
    val unreadCount: Int = 0,
    val avatarInitial: String = title.take(1).ifBlank { "N" },
    val avatarUrl: String? = null,
    val isOnline: Boolean = false,
    val isVerified: Boolean = false,
    val canChat: Boolean = true,
    val chatType: String = "private",
    val memberIds: List<String> = emptyList()
)

data class NoveoPendingAttachment(
    val fileName: String,
    val mimeType: String = "application/octet-stream",
    val sizeLabel: String = "",
    val isUploading: Boolean = false,
    val progress: Float = 0f
)

private fun NoveoHomeChat.openHeaderSubtitle(strings: NoveoStrings): String = when {
    isOnline -> strings.online
    headerSubtitle.isNotBlank() -> headerSubtitle
    chatType != "private" && memberIds.isNotEmpty() -> "${memberIds.size} ${strings.membersCount}"
    else -> strings.lastSeenRecently
}

data class NoveoHomeMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val time: String = "",
    val rawTimestamp: Long = 0L,
    val isOutgoing: Boolean = false,
    val pending: Boolean = false,
    val edited: Boolean = false,
    val forwarded: Boolean = false,
    val seen: Boolean = false,
    val replyAuthor: String? = null,
    val replyPreview: String? = null,
    val attachmentName: String? = null,
    val attachmentUrl: String? = null,
    val attachmentType: String? = null,
    val attachmentSizeLabel: String? = null,
    val reactions: Map<String, Int> = emptyMap(),
    val botButtons: List<List<String>> = emptyList(),
    val dateLabel: String = "",
    val isPinned: Boolean = false,
    val isSystem: Boolean = false,
    val senderAvatarUrl: String? = null
)

private data class TelegramHomeColors(
    val isDark: Boolean,
    val composerBlue: Color,
    val composerPanel: Color,
    val composerField: Color,
    val composerIcon: Color,
    val composerHint: Color,
    val composerDivider: Color,
    val composerText: Color,
    val composerCursor: Color,
    val chatSurface: Color,
    val headerTitle: Color,
    val headerSubtitle: Color,
    val headerIcon: Color,
    val incomingBubble: Color,
    val incomingBubbleSelected: Color,
    val outgoingBubble: Color,
    val outgoingBubbleSelected: Color,
    val incomingText: Color,
    val incomingLink: Color,
    val incomingTime: Color,
    val outgoingText: Color,
    val outgoingTime: Color,
    val replyIncoming: Color,
    val replyOutgoing: Color
)

@Composable
private fun telegramHomeColors(): TelegramHomeColors {
    val scheme = MaterialTheme.colorScheme
    val isDark = scheme.background.red * 0.299f + scheme.background.green * 0.587f + scheme.background.blue * 0.114f < 0.5f
    return if (!isDark) {
        TelegramHomeColors(
            isDark = false,
            composerBlue = scheme.primary,
            composerPanel = scheme.surfaceVariant.copy(alpha = 0.5f),
            composerField = scheme.surface,
            composerIcon = scheme.onSurfaceVariant.copy(alpha = 0.7f),
            composerHint = scheme.onSurfaceVariant.copy(alpha = 0.5f),
            composerDivider = scheme.outlineVariant.copy(alpha = 0.3f),
            composerText = scheme.onSurface,
            composerCursor = scheme.primary,
            chatSurface = scheme.background,
            headerTitle = scheme.onSurface,
            headerSubtitle = scheme.onSurfaceVariant.copy(alpha = 0.7f),
            headerIcon = scheme.onSurfaceVariant,
            incomingBubble = scheme.surface,
            incomingBubbleSelected = scheme.surfaceVariant,
            outgoingBubble = scheme.primaryContainer.copy(alpha = 0.8f),
            outgoingBubbleSelected = scheme.primaryContainer,
            incomingText = scheme.onSurface,
            incomingLink = scheme.primary,
            incomingTime = scheme.onSurfaceVariant.copy(alpha = 0.6f),
            outgoingText = scheme.onPrimaryContainer,
            outgoingTime = scheme.onPrimaryContainer.copy(alpha = 0.7f),
            replyIncoming = scheme.secondaryContainer.copy(alpha = 0.5f),
            replyOutgoing = scheme.onPrimaryContainer.copy(alpha = 0.15f)
        )
    } else {
        TelegramHomeColors(
            isDark = true,
            composerBlue = scheme.primary,
            composerPanel = scheme.surfaceVariant.copy(alpha = 0.3f),
            composerField = scheme.surface,
            composerIcon = scheme.onSurfaceVariant.copy(alpha = 0.7f),
            composerHint = scheme.onSurfaceVariant.copy(alpha = 0.5f),
            composerDivider = scheme.outlineVariant.copy(alpha = 0.3f),
            composerText = scheme.onSurface,
            composerCursor = scheme.primary,
            chatSurface = scheme.background,
            headerTitle = scheme.onSurface,
            headerSubtitle = scheme.onSurfaceVariant.copy(alpha = 0.7f),
            headerIcon = scheme.onSurfaceVariant,
            incomingBubble = scheme.surface,
            incomingBubbleSelected = scheme.surfaceVariant,
            outgoingBubble = scheme.primaryContainer.copy(alpha = 0.7f),
            outgoingBubbleSelected = scheme.primaryContainer,
            incomingText = scheme.onSurface,
            incomingLink = scheme.primary,
            incomingTime = scheme.onSurfaceVariant.copy(alpha = 0.6f),
            outgoingText = scheme.onPrimaryContainer,
            outgoingTime = scheme.onPrimaryContainer.copy(alpha = 0.7f),
            replyIncoming = scheme.secondaryContainer.copy(alpha = 0.4f),
            replyOutgoing = scheme.onPrimaryContainer.copy(alpha = 0.15f)
        )
    }
}

private class TelegramBubbleShape(
    private val isOutgoing: Boolean,
    private val hasTail: Boolean,
    private val cornerRadius: Float = 48f
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
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
        return Outline.Generic(path)
    }
}

@Composable
fun NoveoHomeFrame(
    state: NoveoHomeFrameState,
    strings: NoveoStrings,
    onOpenChat: (String) -> Unit,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    onSendMessage: (String, String?) -> Unit = { text, _ -> onSend(text) },
    onEditMessage: (String, String) -> Unit = { _, _ -> },
    onToggleReaction: (String, String) -> Unit = { _, _ -> },
    onDeleteMessage: (String) -> Unit = {},
    onPinMessage: (String, Boolean) -> Unit = { _, _ -> },
    onForwardMessage: (String, String) -> Unit = { _, _ -> },
    onDownloadFile: (String) -> Unit = {},
    onPickGalleryAttachment: () -> Unit = {},
    onPickFileAttachment: () -> Unit = {},
    onRemoveAttachment: () -> Unit = {},
    onCancelSend: () -> Unit = {},
    onCreateChat: (String, String, String?, String?) -> Unit = { _, _, _, _ -> },
    onUpdateProfile: (String, String) -> Unit = { _, _ -> },
    onChangePassword: (String, String) -> Unit = { _, _ -> },
    onDeleteAccount: (String) -> Unit = {},
    onTyping: () -> Unit,
    onJoinChat: (String) -> Unit = {},
    onLeaveChat: (String) -> Unit = {},
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onStartNewChat: () -> Unit = {},
    onSearchPublic: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showMenu by rememberSaveable { mutableStateOf(false) }
    var activeModal by rememberSaveable { mutableStateOf<AndroidHomeModal?>(null) }
    var showSearch by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val filteredChats = remember(state.chats, searchQuery) {
        if (searchQuery.isBlank()) state.chats
        else state.chats.filter { it.title.contains(searchQuery, true) || it.subtitle.contains(searchQuery, true) }
    }
    LaunchedEffect(showSearch, searchQuery) {
        val query = searchQuery.trim()
        if (showSearch && query.length >= 2) {
            kotlinx.coroutines.delay(350)
            if (query == searchQuery.trim()) {
                onSearchPublic(query)
            }
        }
    }
    val tgColors = telegramHomeColors()

    Surface(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        val selectedChat = state.selectedChat
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val compact = maxWidth < 760.dp

            if (compact) {
                AnimatedContent(
                    targetState = selectedChat != null,
                    label = "android_compact_chat_switch",
                    transitionSpec = {
                        val transition = if (targetState) {
                            (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it / 4 } + fadeOut())
                        } else {
                            (slideInHorizontally { -it / 4 } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                        }
                        transition.using(SizeTransform(clip = false))
                    }
                ) { showingChat ->
                    if (showingChat && selectedChat != null) {
                        AndroidStyleConversationPane(
                            state = state,
                            chat = selectedChat,
                            strings = strings,
                            compact = true,
                            tgColors = tgColors,
                            onBackToChats = onBackToChats,
                            onSend = onSend,
                            onSendMessage = onSendMessage,
                            onEditMessage = onEditMessage,
                            onToggleReaction = onToggleReaction,
                            onDeleteMessage = onDeleteMessage,
                            onPinMessage = onPinMessage,
                            onForwardMessage = onForwardMessage,
                            onDownloadFile = onDownloadFile,
                            onRemoveAttachment = onRemoveAttachment,
                            onCancelSend = onCancelSend,
                            onTyping = onTyping,
                            onJoinChat = onJoinChat,
                            onLeaveChat = onLeaveChat,
                            onRefresh = onRefresh,
                            onOpenAttachments = { activeModal = AndroidHomeModal.ATTACHMENTS },
                            onOpenStickers = { activeModal = AndroidHomeModal.STICKERS },
                            onOpenChatInfo = { activeModal = AndroidHomeModal.CHAT_INFO },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AndroidStyleSidebarPane(
                            state = state,
                            strings = strings,
                            chats = filteredChats,
                            showSearch = showSearch,
                            searchQuery = searchQuery,
                            showMenu = showMenu,
                            onSearchQuery = { searchQuery = it },
                            onMenuClick = { showMenu = !showMenu },
                            onSearchToggle = { showSearch = !showSearch; if (showSearch) searchQuery = "" },
                            onOpenChat = onOpenChat,
                            onRefresh = onRefresh,
                            onLogout = onLogout,
                            onOpenSettings = onOpenSettings,
                            onStartNewChat = onStartNewChat,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            } else {
                Row(Modifier.fillMaxSize()) {
                    AndroidStyleSidebarPane(
                        state = state,
                        strings = strings,
                        chats = filteredChats,
                        showSearch = showSearch,
                        searchQuery = searchQuery,
                        showMenu = showMenu,
                        onSearchQuery = { searchQuery = it },
                        onMenuClick = { showMenu = true },
                        onSearchToggle = { showSearch = !showSearch; if (showSearch) searchQuery = "" },
                        onOpenChat = onOpenChat,
                        onRefresh = onRefresh,
                        onLogout = onLogout,
                        onOpenSettings = onOpenSettings,
                        onStartNewChat = onStartNewChat,
                        modifier = Modifier.width(360.dp).fillMaxHeight()
                    )
                    AnimatedContent(
                        targetState = selectedChat?.id,
                        label = "wide_shell_transition",
                        transitionSpec = {
                            (slideInHorizontally(initialOffsetX = { it / 5 }) + fadeIn())
                                .togetherWith(slideOutHorizontally(targetOffsetX = { -it / 5 }) + fadeOut())
                        },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) { selectedId ->
                        val visibleChat = state.chats.firstOrNull { it.id == selectedId }
                        if (visibleChat != null) {
                            AndroidStyleConversationPane(
                                state = state,
                                chat = visibleChat,
                                strings = strings,
                                compact = false,
                                tgColors = tgColors,
                                onBackToChats = onBackToChats,
                                onSend = onSend,
                                onSendMessage = onSendMessage,
                                onEditMessage = onEditMessage,
                                onToggleReaction = onToggleReaction,
                                onDeleteMessage = onDeleteMessage,
                                onPinMessage = onPinMessage,
                                onForwardMessage = onForwardMessage,
                                onDownloadFile = onDownloadFile,
                                onRemoveAttachment = onRemoveAttachment,
                                onCancelSend = onCancelSend,
                                onTyping = onTyping,
                                onJoinChat = onJoinChat,
                                onLeaveChat = onLeaveChat,
                                onRefresh = onRefresh,
                                onOpenAttachments = { activeModal = AndroidHomeModal.ATTACHMENTS },
                                onOpenStickers = { activeModal = AndroidHomeModal.STICKERS },
                                onOpenChatInfo = { activeModal = AndroidHomeModal.CHAT_INFO },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            AndroidWelcomePane(strings = strings, modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showMenu,
                enter = fadeIn(tween(160)),
                exit = fadeOut(tween(160))
            ) {
                Box(Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.50f)).clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { showMenu = false }
                        )
                    )
                    AnimatedVisibility(
                        visible = showMenu,
                        enter = slideInHorizontally(tween(250, easing = FastOutSlowInEasing)) { -it },
                        exit = slideOutHorizontally(tween(220, easing = FastOutSlowInEasing)) { -it },
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        AndroidStyleSideMenu(
                            strings = strings,
                            onDismiss = { showMenu = false },
                            onContacts = { activeModal = AndroidHomeModal.CONTACTS },
                            onSettings = { activeModal = AndroidHomeModal.SETTINGS },
                            onStartNewChat = { activeModal = AndroidHomeModal.NEW_CHAT },
                            onProfile = { activeModal = AndroidHomeModal.PROFILE },
                            onRefresh = onRefresh,
                            onLogout = onLogout
                        )
                    }
                }
            }

            activeModal?.let { modal ->
                AndroidHomeModalOverlay(
                    modal = modal,
                    state = state,
                    strings = strings,
                    selectedChat = selectedChat,
                    onDismiss = { activeModal = null },
                    onStartNewChat = onStartNewChat,
                    onOpenSettings = onOpenSettings,
                    onRefresh = onRefresh,
                    onLeaveChat = onLeaveChat,
                    onPickGalleryAttachment = onPickGalleryAttachment,
                    onPickFileAttachment = onPickFileAttachment,
                    onCreateChat = onCreateChat,
                    onUpdateProfile = onUpdateProfile,
                    onChangePassword = onChangePassword,
                    onDeleteAccount = onDeleteAccount,
                    onOpenProfile = { activeModal = AndroidHomeModal.PROFILE },
                    onOpenAccount = { activeModal = AndroidHomeModal.ACCOUNT },
                    onLogout = onLogout
                )
            }
        }
    }
}


@Composable
private fun AndroidHomeModalOverlay(
    modal: AndroidHomeModal,
    state: NoveoHomeFrameState,
    strings: NoveoStrings,
    selectedChat: NoveoHomeChat?,
    onDismiss: () -> Unit,
    onStartNewChat: () -> Unit,
    onOpenSettings: () -> Unit,
    onRefresh: () -> Unit,
    onLeaveChat: (String) -> Unit,
    onPickGalleryAttachment: () -> Unit,
    onPickFileAttachment: () -> Unit,
    onCreateChat: (String, String, String?, String?) -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    onChangePassword: (String, String) -> Unit,
    onDeleteAccount: (String) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenAccount: () -> Unit,
    onLogout: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.42f)).clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
        )
        if (modal == AndroidHomeModal.ATTACHMENTS || modal == AndroidHomeModal.STICKERS) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(160)) + slideInVertically(tween(260, easing = FastOutSlowInEasing)) { it },
                exit = fadeOut(tween(120)) + slideOutVertically(tween(180, easing = FastOutSlowInEasing)) { it },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                AndroidBottomSheetSurface {
                    when (modal) {
                        AndroidHomeModal.ATTACHMENTS -> AndroidAttachmentSourceSurface(
                            strings = strings,
                            onGallery = {
                                onDismiss()
                                onPickGalleryAttachment()
                            },
                            onFiles = {
                                onDismiss()
                                onPickFileAttachment()
                            }
                        )
                        AndroidHomeModal.STICKERS -> AndroidStickerSurface(strings = strings)
                        else -> Unit
                    }
                }
            }
        } else {
            AnimatedContent(
                targetState = modal,
                label = "android_home_modal_switch",
                transitionSpec = {
                    (fadeIn(tween(180)) + slideInVertically(tween(260, easing = FastOutSlowInEasing)) { it / 5 })
                        .togetherWith(fadeOut(tween(120)) + slideOutVertically(tween(180, easing = FastOutSlowInEasing)) { it / 6 })
                },
                modifier = Modifier.align(Alignment.Center)
            ) { target ->
                AndroidModalCard(onDismiss = onDismiss) {
                    when (target) {
                        AndroidHomeModal.CONTACTS -> AndroidContactsSurface(state = state, strings = strings)
                        AndroidHomeModal.NEW_CHAT -> AndroidNewChatSurface(
                            strings = strings,
                            onCreateChat = { title, type, handle, bio ->
                                onDismiss()
                                onCreateChat(title, type, handle, bio)
                            }
                        )
                        AndroidHomeModal.SETTINGS -> AndroidSettingsSurface(
                            strings = strings,
                            onOpenProfile = onOpenProfile,
                            onOpenAccount = onOpenAccount,
                            onLogout = onLogout
                        )
                        AndroidHomeModal.PROFILE -> AndroidProfileSurface(
                            strings = strings,
                            state = state,
                            onUpdateProfile = { username, bio ->
                                onDismiss()
                                onUpdateProfile(username, bio)
                            }
                        )
                        AndroidHomeModal.ACCOUNT -> AndroidAccountSurface(
                            strings = strings,
                            onChangePassword = { oldPassword, newPassword ->
                                onDismiss()
                                onChangePassword(oldPassword, newPassword)
                            },
                            onDeleteAccount = { password ->
                                onDismiss()
                                onDeleteAccount(password)
                            }
                        )
                        AndroidHomeModal.CHAT_INFO -> AndroidChatInfoSurface(
                            strings = strings,
                            chat = selectedChat,
                            currentUserId = state.currentUserId,
                            onLeaveChat = { chatId ->
                                onDismiss()
                                onLeaveChat(chatId)
                            }
                        )
                        else -> Unit
                    }
                }
            }
        }
    }
}

@Composable
private fun AndroidBottomSheetSurface(content: @Composable () -> Unit) {
    val tgColors = telegramHomeColors()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = tgColors.incomingBubble,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        shadowElevation = 16.dp,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
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
            content()
        }
    }
}

@Composable
private fun AndroidModalCard(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.widthIn(min = 320.dp, max = 420.dp).padding(18.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp,
        tonalElevation = 2.dp
    ) {
        Box(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                content()
            }
            HeaderIconButton(
                icon = Icons.Outlined.Close,
                onClick = onDismiss,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
            )
        }
    }
}

@Composable
private fun AndroidModalHeader(title: String, subtitle: String? = null, icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Outlined.Info) {
    Row(modifier = Modifier.fillMaxWidth().padding(end = 42.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(46.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (!subtitle.isNullOrBlank()) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun AndroidContactsSurface(state: NoveoHomeFrameState, strings: NoveoStrings) {
    AndroidModalHeader(strings.allContacts, strings.searchGlobal, Icons.Outlined.AccountCircle)
    val privateChats = state.chats.filter { it.chatType == "private" }.take(12)
    if (privateChats.isEmpty()) {
        AndroidEmptyModalText(strings.noContacts)
    } else {
        LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(privateChats, key = { it.id }) { chat ->
                AndroidContactRow(chat = chat, strings = strings)
            }
        }
    }
}

@Composable
private fun AndroidContactRow(chat: NoveoHomeChat, strings: NoveoStrings) {
    Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ProfileCircle(name = chat.title, isSavedMessages = chat.title == "Saved Messages" || chat.title == strings.savedMessages, size = 42.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(chat.title.ifBlank { strings.unknown }, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(chat.openHeaderSubtitle(strings), style = MaterialTheme.typography.bodySmall, color = if (chat.isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun AndroidNewChatSurface(
    strings: NoveoStrings,
    onCreateChat: (String, String, String?, String?) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var handle by rememberSaveable { mutableStateOf("") }
    var bio by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("group") }

    AndroidModalHeader(strings.newChat, strings.handleOptional, Icons.Outlined.Add)
    AndroidChatTypeSelector(
        selected = type,
        strings = strings,
        onSelected = { type = it }
    )
    Spacer(Modifier.height(12.dp))
    AndroidFormField(
        value = title,
        onValueChange = { title = it },
        label = strings.title,
        placeholder = strings.newChat
    )
    Spacer(Modifier.height(10.dp))
    AndroidFormField(
        value = handle,
        onValueChange = { handle = it },
        label = strings.handleOptional,
        placeholder = "@handle"
    )
    Spacer(Modifier.height(10.dp))
    AndroidFormField(
        value = bio,
        onValueChange = { bio = it },
        label = strings.bioOptional,
        placeholder = strings.about,
        minHeight = 76
    )
    Spacer(Modifier.height(16.dp))
    AndroidPrimaryActionRow(
        label = strings.create,
        enabled = title.trim().isNotBlank(),
        onClick = {
            onCreateChat(
                title.trim(),
                type,
                handle.trim().removePrefix("@").takeIf { it.isNotBlank() },
                bio.trim().takeIf { it.isNotBlank() }
            )
        }
    )
}

@Composable
private fun AndroidChatTypeSelector(
    selected: String,
    strings: NoveoStrings,
    onSelected: (String) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            "group" to strings.group,
            "channel" to strings.channel,
            "private" to strings.privateChatType
        ).forEach { (value, label) ->
            val active = selected == value
            Surface(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(18.dp)).clickable { onSelected(value) },
                shape = RoundedCornerShape(18.dp),
                color = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                border = BorderStroke(1.dp, if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                    color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AndroidSettingsSurface(
    strings: NoveoStrings,
    onOpenProfile: () -> Unit,
    onOpenAccount: () -> Unit,
    onLogout: () -> Unit
) {
    AndroidModalHeader(strings.settings, strings.preferences, Icons.Outlined.Settings)
    AndroidSettingsRow(strings.profile, strings.displayName, Icons.Outlined.AccountCircle, onClick = onOpenProfile)
    AndroidSettingsRow(strings.account, strings.changePassword, Icons.Outlined.Lock, onClick = onOpenAccount)
    AndroidSettingsRow(strings.themes, strings.themeLightDesc, Icons.Outlined.Star, onClick = {})
    AndroidSettingsRow(strings.notificationSettings, strings.enableNotifications, Icons.Outlined.Info, onClick = {})
    Spacer(Modifier.height(6.dp))
    TextButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text(strings.logout, color = MaterialTheme.colorScheme.error) }
}

@Composable
private fun AndroidProfileSurface(
    strings: NoveoStrings,
    state: NoveoHomeFrameState,
    onUpdateProfile: (String, String) -> Unit
) {
    var displayName by rememberSaveable(state.currentUsername) {
        mutableStateOf(state.currentUsername.ifBlank { strings.brandName })
    }
    var bio by rememberSaveable(state.currentUserBio) { mutableStateOf(state.currentUserBio) }

    AndroidModalHeader(strings.profile, strings.about, Icons.Outlined.AccountCircle)
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        ProfileCircle(name = displayName.ifBlank { strings.brandName }, size = 70.dp)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(displayName.ifBlank { strings.brandName }, fontWeight = FontWeight.Bold, fontSize = 20.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(state.currentUserId ?: strings.userId, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(strings.online, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
        }
    }
    Spacer(Modifier.height(12.dp))
    AndroidFormField(
        value = displayName,
        onValueChange = { displayName = it },
        label = strings.displayName,
        placeholder = strings.displayName
    )
    Spacer(Modifier.height(10.dp))
    AndroidFormField(
        value = bio,
        onValueChange = { bio = it },
        label = strings.bioOptional,
        placeholder = strings.about,
        minHeight = 76
    )
    Spacer(Modifier.height(16.dp))
    AndroidPrimaryActionRow(
        label = strings.saveChanges,
        enabled = displayName.trim().isNotBlank(),
        onClick = { onUpdateProfile(displayName.trim(), bio.trim()) }
    )
}

@Composable
private fun AndroidAccountSurface(
    strings: NoveoStrings,
    onChangePassword: (String, String) -> Unit,
    onDeleteAccount: (String) -> Unit
) {
    var oldPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var deletePassword by rememberSaveable { mutableStateOf("") }

    AndroidModalHeader(strings.account, strings.changePassword, Icons.Outlined.Lock)
    AndroidFormField(
        value = oldPassword,
        onValueChange = { oldPassword = it },
        label = strings.oldPassword,
        placeholder = strings.oldPassword
    )
    Spacer(Modifier.height(10.dp))
    AndroidFormField(
        value = newPassword,
        onValueChange = { newPassword = it },
        label = strings.newPassword,
        placeholder = strings.newPassword
    )
    Spacer(Modifier.height(12.dp))
    AndroidPrimaryActionRow(
        label = strings.changePassword,
        enabled = oldPassword.isNotBlank() && newPassword.isNotBlank(),
        onClick = { onChangePassword(oldPassword, newPassword) }
    )
    Spacer(Modifier.height(18.dp))
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
    Spacer(Modifier.height(12.dp))
    Text(strings.deleteAccount, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    AndroidFormField(
        value = deletePassword,
        onValueChange = { deletePassword = it },
        label = strings.passwordPlaceholder,
        placeholder = strings.passwordPlaceholder
    )
    Spacer(Modifier.height(10.dp))
    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).clickable(enabled = deletePassword.isNotBlank()) { onDeleteAccount(deletePassword) },
        shape = RoundedCornerShape(20.dp),
        color = if (deletePassword.isNotBlank()) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    ) {
        Text(
            text = strings.deleteAccount,
            modifier = Modifier.padding(vertical = 12.dp),
            color = if (deletePassword.isNotBlank()) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AndroidFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    minHeight: Int = 48
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(5.dp))
        Surface(
            modifier = Modifier.fillMaxWidth().heightIn(min = minHeight.dp),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), contentAlignment = Alignment.CenterStart) {
                if (value.isBlank()) {
                    Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.58f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    maxLines = if (minHeight > 60) 3 else 1
                )
            }
        }
    }
}

@Composable
private fun AndroidPrimaryActionRow(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(vertical = 12.dp),
            color = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AndroidAttachmentSourceSurface(
    strings: NoveoStrings,
    onGallery: () -> Unit,
    onFiles: () -> Unit
) {
    val tgColors = telegramHomeColors()
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
        AndroidAttachmentChoice(
            label = strings.gallery,
            icon = Icons.Outlined.Collections,
            color = Color(0xFF2EA6FF),
            modifier = Modifier.weight(1f),
            onClick = onGallery
        )
        AndroidAttachmentChoice(
            label = strings.files,
            icon = Icons.Outlined.Description,
            color = Color(0xFF34C759),
            modifier = Modifier.weight(1f),
            onClick = onFiles
        )
    }
}

@Composable
private fun AndroidAttachmentChoice(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
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
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(27.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun AndroidStickerSurface(strings: NoveoStrings) {
    val tgColors = telegramHomeColors()
    Text(
        text = strings.stickers,
        color = tgColors.headerTitle,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
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

@Composable
private fun AndroidChatInfoSurface(
    strings: NoveoStrings,
    chat: NoveoHomeChat?,
    currentUserId: String?,
    onLeaveChat: (String) -> Unit
) {
    val isMember = currentUserId != null && chat?.memberIds?.contains(currentUserId) == true
    val canLeave = chat != null && chat.chatType != "private" && isMember
    val chatTypeLabel = when (chat?.chatType) {
        "channel" -> strings.channel
        "group" -> strings.group
        else -> strings.privateChatType
    }
    AndroidModalHeader(strings.chatInfo, chat?.openHeaderSubtitle(strings) ?: strings.members, Icons.Outlined.Info)
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        ProfileCircle(name = chat?.title ?: strings.chatInfo, isSavedMessages = chat?.title == strings.savedMessages || chat?.title == "Saved Messages", size = 68.dp)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(chat?.title ?: strings.chatInfo, fontWeight = FontWeight.Bold, fontSize = 20.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(chat?.openHeaderSubtitle(strings) ?: chatTypeLabel, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
    Spacer(Modifier.height(10.dp))
    AndroidSettingsRow(strings.members, chat?.memberIds?.size?.let { "$it ${strings.membersCount}" } ?: strings.membersCount, Icons.Outlined.AccountCircle, onClick = {})
    AndroidSettingsRow(strings.searchMessages, strings.searchPlaceholder, Icons.Outlined.Search, onClick = {})
    AndroidSettingsRow(strings.notificationSettings, strings.enableNotifications, Icons.Outlined.Info, onClick = {})
    if (canLeave && chat != null) {
        AndroidDangerSettingsRow(
            title = strings.leave,
            subtitle = chatTypeLabel,
            icon = Icons.Outlined.Close,
            onClick = { onLeaveChat(chat.id) }
        )
    }
}

@Composable
private fun AndroidDangerSettingsRow(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.65f),
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.error.copy(alpha = 0.14f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.error, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.82f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun AndroidSettingsRow(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable(onClick = onClick)) {
        Row(modifier = Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun AndroidEmptyModalText(text: String) {
    Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
private fun AndroidStyleSidebarPane(
    state: NoveoHomeFrameState,
    strings: NoveoStrings,
    chats: List<NoveoHomeChat>,
    showSearch: Boolean,
    searchQuery: String,
    showMenu: Boolean,
    onSearchQuery: (String) -> Unit,
    onMenuClick: () -> Unit,
    onSearchToggle: () -> Unit,
    onOpenChat: (String) -> Unit,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartNewChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                AndroidStyleSidebarHeader(
                    strings = strings,
                    showSearch = showSearch,
                    searchQuery = searchQuery,
                    connectionTitle = strings.brandName,
                    onMenuClick = onMenuClick,
                    onSearchToggle = onSearchToggle,
                    onSearchQueryChange = onSearchQuery
                )
                state.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                if (showSearch) {
                    AndroidStyleSearchResults(strings, chats, onOpenChat)
                } else {
                    AndroidStyleChatListContent(state, strings, chats, onOpenChat)
                }
            }
        }
    }
}

@Composable
private fun AndroidStyleSidebarHeader(
    strings: NoveoStrings,
    showSearch: Boolean,
    searchQuery: String,
    connectionTitle: String,
    onMenuClick: () -> Unit,
    onSearchToggle: () -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
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
                    (slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn()).togetherWith(slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut())
                }
            ) { searching ->
                if (searching) {
                    SearchField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = strings.searchPlaceholder,
                        modifier = Modifier.fillMaxWidth(0.88f).height(46.dp)
                    )
                } else {
                    AnimatedContent(
                        targetState = connectionTitle,
                        label = "title_animation",
                        transitionSpec = {
                            (slideInVertically { height -> -height } + fadeIn()).togetherWith(slideOutVertically { height -> height } + fadeOut())
                        }
                    ) { title ->
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier.fillMaxWidth().height(28.dp).alpha(1f),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        HeaderIconButton(icon = if (showSearch) Icons.Outlined.Close else Icons.Outlined.Search, onClick = onSearchToggle)
    }
}

@Composable
private fun SearchField(value: String, onValueChange: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(23.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)) {
        Row(Modifier.fillMaxSize().padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    if (value.isBlank()) Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.68f), fontSize = 14.sp)
                    inner()
                }
            )
        }
    }
}

@Composable
private fun AndroidStyleChatListContent(state: NoveoHomeFrameState, strings: NoveoStrings, chats: List<NoveoHomeChat>, onOpenChat: (String) -> Unit) {
    if (state.loading && chats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
        }
    } else if (chats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(strings.noMessagesYet, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
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
                AndroidStyleChatRow(chat = chat, strings = strings, selected = chat.id == state.selectedChatId, onClick = { onOpenChat(chat.id) })
            }
        }
    }
}

@Composable
private fun AndroidStyleSearchResults(strings: NoveoStrings, chats: List<NoveoHomeChat>, onOpenChat: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (chats.isNotEmpty()) {
            item { Text(strings.newChat, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.primary) }
            items(chats, key = { it.id }) { chat ->
                AndroidStyleChatRow(chat = chat, strings = strings, selected = false, onClick = { onOpenChat(chat.id) })
            }
        }
    }
}

@Composable
private fun AndroidStyleChatRow(chat: NoveoHomeChat, strings: NoveoStrings, selected: Boolean, onClick: () -> Unit) {
    val chatTitle = remember(chat, strings) {
        if (chat.title == "Saved Messages") strings.savedMessages else chat.title.ifBlank { strings.chatInfo }
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ProfileCircle(name = chatTitle, isSavedMessages = chatTitle == strings.savedMessages || chat.title == "Saved Messages", imageUrl = chat.avatarUrl)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(chatTitle, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (chat.isVerified) {
                        Spacer(Modifier.width(4.dp))
                        VerifiedIcon()
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    chat.subtitle.ifBlank { strings.noMessagesYet },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (chat.unreadCount > 0) {
                Spacer(Modifier.width(8.dp))
                UnreadBadge(chat.unreadCount)
            }
        }
    }
}

@Composable
private fun AndroidStyleSideMenu(
    strings: NoveoStrings,
    onDismiss: () -> Unit,
    onContacts: () -> Unit,
    onSettings: () -> Unit,
    onStartNewChat: () -> Unit,
    onProfile: () -> Unit,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(296.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(strings.menu, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(14.dp))
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onProfile(); onDismiss() },
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.62f))
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                ProfileCircle(name = strings.brandName, size = 48.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(strings.profile, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(strings.online, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        AndroidMenuRow(strings.allContacts, Icons.Outlined.Info) { onContacts(); onDismiss() }
        AndroidMenuRow(strings.newChat, Icons.Outlined.Menu) { onStartNewChat(); onDismiss() }
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onDismiss() },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFFFD700))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(strings.stars, fontWeight = FontWeight.SemiBold)
                    Text("0.00 ${strings.stars}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        AndroidMenuRow(strings.settings, Icons.Outlined.Settings) { onSettings(); onDismiss() }
        AndroidMenuRow(strings.refresh, Icons.Outlined.Refresh) { onRefresh(); onDismiss() }
        Spacer(Modifier.weight(1f))
        TextButton(onClick = { onLogout(); onDismiss() }, modifier = Modifier.fillMaxWidth()) { Text(strings.logout) }
        Spacer(Modifier.height(8.dp))
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(strings.brandName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text("vDesktop Kotlin", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AndroidMenuRow(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
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
private fun AndroidStyleConversationPane(
    state: NoveoHomeFrameState,
    chat: NoveoHomeChat,
    strings: NoveoStrings,
    compact: Boolean,
    tgColors: TelegramHomeColors,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    onSendMessage: (String, String?) -> Unit,
    onEditMessage: (String, String) -> Unit,
    onToggleReaction: (String, String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onPinMessage: (String, Boolean) -> Unit,
    onForwardMessage: (String, String) -> Unit,
    onDownloadFile: (String) -> Unit,
    onRemoveAttachment: () -> Unit,
    onCancelSend: () -> Unit,
    onTyping: () -> Unit,
    onJoinChat: (String) -> Unit,
    onLeaveChat: (String) -> Unit,
    onRefresh: () -> Unit,
    onOpenAttachments: () -> Unit,
    onOpenStickers: () -> Unit,
    onOpenChatInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var draft by rememberSaveable(chat.id) { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var contextMenuState by remember { mutableStateOf<AndroidContextMenuState?>(null) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showMessageSearch by rememberSaveable(chat.id) { mutableStateOf(false) }
    var messageSearchQuery by rememberSaveable(chat.id) { mutableStateOf("") }
    var replyingToMessage by remember { mutableStateOf<NoveoHomeMessage?>(null) }
    var editingMessage by remember { mutableStateOf<NoveoHomeMessage?>(null) }
    var forwardingMessage by remember { mutableStateOf<NoveoHomeMessage?>(null) }
    val clipboard = LocalClipboardManager.current
    val pinnedMessage = remember(state.messages) { state.messages.lastOrNull { it.isPinned } }
    val visibleMessages = remember(state.messages, showMessageSearch, messageSearchQuery) {
        if (!showMessageSearch || messageSearchQuery.isBlank()) {
            state.messages
        } else {
            val query = messageSearchQuery.trim()
            state.messages.filter { message ->
                message.text.contains(query, ignoreCase = true) ||
                    message.senderName.contains(query, ignoreCase = true) ||
                    message.replyPreview?.contains(query, ignoreCase = true) == true ||
                    message.attachmentName?.contains(query, ignoreCase = true) == true
            }
        }
    }
    val isMember = state.currentUserId?.let { chat.memberIds.contains(it) } == true
    val canWriteToChat = state.canSendMessage && chat.canChat &&
        (chat.chatType == "private" || isMember)
    val canJoinChat = chat.chatType != "private" && !isMember
    val showCannotSendBar = !canWriteToChat && !canJoinChat
    val showBottomInputSurface = canWriteToChat || canJoinChat || showCannotSendBar

    LaunchedEffect(canWriteToChat, chat.id) {
        if (!canWriteToChat) {
            draft = ""
            replyingToMessage = null
            editingMessage = null
        }
    }

    LaunchedEffect(editingMessage?.id) {
        editingMessage?.let { draft = it.text }
    }

    LaunchedEffect(chat.id, visibleMessages.size, showMessageSearch) {
        if (visibleMessages.isNotEmpty()) {
            val lastIndex = visibleMessages.lastIndex
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: lastIndex
            val nearBottom = lastIndex - lastVisible <= 2
            if (nearBottom) listState.animateScrollToItem(lastIndex)
            else if (listState.layoutInfo.totalItemsCount == 0) listState.scrollToItem(lastIndex)
        }
    }

    val showScrollToBottom = remember(visibleMessages.size, listState.firstVisibleItemIndex) {
        visibleMessages.isNotEmpty() && listState.firstVisibleItemIndex < visibleMessages.lastIndex - 8
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(tgColors.chatSurface)) {
        val maxBubbleWidth = maxWidth * 0.78f
        val topChromePadding = 64.dp +
            (if (pinnedMessage != null) 48.dp else 0.dp) +
            (if (showMessageSearch) 56.dp else 0.dp)
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 8.dp, top = topChromePadding, end = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (state.messages.isEmpty()) item { EmptyMessagesSurface(strings, tgColors) }
            else if (visibleMessages.isEmpty()) item { EmptyMessageSearchSurface(strings, tgColors, messageSearchQuery) }
            itemsIndexed(visibleMessages, key = { _, message -> message.id }) { index, message ->
                val prev = visibleMessages.getOrNull(index - 1)
                val next = visibleMessages.getOrNull(index + 1)
                if (message.dateLabel.isNotBlank() && prev?.dateLabel != message.dateLabel) {
                    AndroidDateSeparator(message.dateLabel, tgColors)
                }
                val sameAuthorAsPrevious = prev != null && prev.senderId == message.senderId && prev.dateLabel == message.dateLabel
                val sameAuthorAsNext = next != null && next.senderId == message.senderId && next.dateLabel == message.dateLabel
                val showSenderInfo = !message.isOutgoing && !sameAuthorAsPrevious
                val hasTail = !sameAuthorAsNext
                AndroidStyleMessageRow(
                    message = message,
                    strings = strings,
                    showSenderInfo = showSenderInfo,
                    hasTail = hasTail,
                    isGroupChat = chat.chatType != "private",
                    maxBubbleWidth = maxBubbleWidth,
                    tgColors = tgColors,
                    onOpenMenu = { bounds -> contextMenuState = AndroidContextMenuState(message = message, bubbleBounds = bounds) }
                )
            }
        }

        if (showBottomInputSurface) {
            Box(
                modifier = Modifier.fillMaxWidth().height(150.dp).align(Alignment.BottomCenter).offset(y = 42.dp).background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, tgColors.chatSurface),
                        startY = 0f,
                        endY = 110f
                    )
                )
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth().height(56.dp).align(Alignment.TopCenter),
            color = tgColors.incomingBubble,
            tonalElevation = 1.dp,
            shadowElevation = 1.dp
        ) {
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                HeaderIconButton(
                    icon = Icons.AutoMirrored.Outlined.ArrowBack,
                    onClick = onBackToChats,
                    tint = tgColors.headerIcon,
                    modifier = Modifier.padding(start = 4.dp).alpha(if (compact) 1f else 0f)
                )
                Row(
                    modifier = Modifier.weight(1f).padding(vertical = 4.dp, horizontal = 4.dp).clip(RoundedCornerShape(18.dp)).clickable(onClick = onOpenChatInfo),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileCircle(name = chat.title, isSavedMessages = chat.title == strings.savedMessages || chat.title == "Saved Messages", size = 40.dp, imageUrl = chat.avatarUrl)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                chat.title,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = tgColors.headerTitle,
                                fontSize = 15.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (chat.isVerified) {
                                Spacer(Modifier.width(4.dp))
                                VerifiedIcon(modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(Modifier.height(3.dp))
                        Text(
                            chat.openHeaderSubtitle(strings),
                            color = tgColors.headerSubtitle,
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                HeaderIconButton(icon = Icons.Outlined.Call, onClick = {}, tint = tgColors.headerIcon)
                Box {
                    HeaderIconButton(icon = Icons.Outlined.MoreVert, onClick = { showMoreMenu = true }, tint = tgColors.headerIcon, modifier = Modifier.padding(end = 4.dp))
                    DropdownMenu(expanded = showMoreMenu, onDismissRequest = { showMoreMenu = false }) {
                        DropdownMenuItem(text = { Text(strings.chatInfo) }, onClick = { showMoreMenu = false; onOpenChatInfo() })
                        DropdownMenuItem(
                            text = { Text(strings.searchMessages) },
                            onClick = {
                                showMoreMenu = false
                                showMessageSearch = true
                                messageSearchQuery = ""
                            }
                        )
                        if (chat.chatType != "private" && isMember) {
                            DropdownMenuItem(
                                text = { Text(strings.leave, color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showMoreMenu = false
                                    onLeaveChat(chat.id)
                                }
                            )
                        }
                        DropdownMenuItem(text = { Text(strings.refresh) }, onClick = { showMoreMenu = false; onRefresh() })
                    }
                }
            }
        }


        pinnedMessage?.let { pinned ->
            AndroidPinnedMessageBar(
                message = pinned,
                strings = strings,
                tgColors = tgColors,
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).offset(y = 56.dp)
            )
        }

        AnimatedVisibility(
            visible = showMessageSearch,
            enter = fadeIn() + slideInVertically { -it / 2 },
            exit = fadeOut() + slideOutVertically { -it / 2 },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .offset(y = if (pinnedMessage != null) 104.dp else 56.dp)
        ) {
            AndroidMessageSearchBar(
                query = messageSearchQuery,
                strings = strings,
                tgColors = tgColors,
                resultCount = visibleMessages.size,
                onQueryChange = { messageSearchQuery = it },
                onClose = {
                    showMessageSearch = false
                    messageSearchQuery = ""
                }
            )
        }

        AnimatedVisibility(
            visible = showScrollToBottom,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 76.dp)
        ) {
            Surface(
                modifier = Modifier.size(42.dp).clickable {
                    scope.launch {
                        val target = visibleMessages.lastIndex
                        if (target >= 0) {
                            if (listState.firstVisibleItemIndex < target - 20) listState.scrollToItem((target - 10).coerceAtLeast(0))
                            listState.animateScrollToItem(target)
                        }
                    }
                },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
                }
            }
        }

        state.activeCallTitle?.let { callTitle ->
            Surface(color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).offset(y = 56.dp)) {
                Text("${strings.activeCall}: $callTitle", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

        if (canWriteToChat) {
            Box(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 8.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                AndroidStyleComposer(
                    modifier = Modifier.widthIn(max = 780.dp).fillMaxWidth(),
                    draft = draft,
                    onDraftChange = { draft = it; onTyping() },
                    placeholder = strings.messagePlaceholder,
                    enabled = true,
                    sending = state.isSendingMessage,
                    tgColors = tgColors,
                    replyingTo = replyingToMessage,
                    editingMessage = editingMessage,
                    pendingAttachment = state.pendingAttachment,
                    onCancelReply = { replyingToMessage = null },
                    onCancelEdit = { editingMessage = null; draft = "" },
                    onRemoveAttachment = onRemoveAttachment,
                    onCancelSend = onCancelSend,
                    onOpenAttachments = onOpenAttachments,
                    onOpenStickers = onOpenStickers,
                    onSend = {
                        val text = draft.trim()
                        if ((text.isNotBlank() || state.pendingAttachment != null) && canWriteToChat) {
                            val replyId = replyingToMessage?.id
                            val editId = editingMessage?.id
                            draft = ""
                            replyingToMessage = null
                            editingMessage = null
                            if (editId != null) {
                                onEditMessage(editId, text)
                            } else {
                                onSendMessage(text, replyId)
                            }
                        }
                    }
                )
            }
        } else if (canJoinChat) {
            AndroidJoinChatBar(
                strings = strings,
                tgColors = tgColors,
                onJoin = { onJoinChat(chat.id) },
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
            )
        } else {
            AndroidCannotSendBar(
                strings = strings,
                tgColors = tgColors,
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
            )
        }

        forwardingMessage?.let { message ->
            AndroidForwardPickerOverlay(
                message = message,
                chats = state.chats,
                currentChatId = chat.id,
                strings = strings,
                onDismiss = { forwardingMessage = null },
                onForward = { targetChatId ->
                    onForwardMessage(message.id, targetChatId)
                    forwardingMessage = null
                }
            )
        }

        contextMenuState?.let { menuState ->
            MessageContextMenuOverlay(
                state = menuState,
                strings = strings,
                tgColors = tgColors,
                onReply = { replyingToMessage = menuState.message; editingMessage = null; contextMenuState = null },
                onEdit = { editingMessage = menuState.message; replyingToMessage = null; contextMenuState = null },
                onToggleReaction = { emoji -> onToggleReaction(menuState.message.id, emoji); contextMenuState = null },
                onPin = { onPinMessage(menuState.message.id, !menuState.message.isPinned); contextMenuState = null },
                onCopyText = {
                    if (menuState.message.text.isNotBlank()) clipboard.setText(AnnotatedString(menuState.message.text))
                    contextMenuState = null
                },
                onForward = { forwardingMessage = menuState.message; contextMenuState = null },
                onSeenBy = { contextMenuState = null },
                onAddAsSticker = { contextMenuState = null },
                onDownload = { onDownloadFile(menuState.message.id); contextMenuState = null },
                onDelete = { onDeleteMessage(menuState.message.id); contextMenuState = null },
                onDismiss = { contextMenuState = null }
            )
        }
    }
}


@Composable
private fun AndroidForwardPickerOverlay(
    message: NoveoHomeMessage,
    chats: List<NoveoHomeChat>,
    currentChatId: String,
    strings: NoveoStrings,
    onDismiss: () -> Unit,
    onForward: (String) -> Unit
) {
    val targets = remember(chats, currentChatId) {
        chats.filter { it.id != currentChatId && it.canChat }
    }
    Box(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.42f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        )
        AnimatedContent(
            targetState = message.id,
            label = "android_forward_picker",
            transitionSpec = {
                (fadeIn(tween(180)) + slideInVertically(tween(260, easing = FastOutSlowInEasing)) { it / 5 })
                    .togetherWith(fadeOut(tween(120)) + slideOutVertically(tween(180, easing = FastOutSlowInEasing)) { it / 6 })
            },
            modifier = Modifier.align(Alignment.Center)
        ) {
            AndroidModalCard(onDismiss = onDismiss) {
                AndroidModalHeader(strings.forward, message.text.ifBlank { message.attachmentName ?: strings.messagePlaceholder }, Icons.AutoMirrored.Outlined.ArrowForward)
                if (targets.isEmpty()) {
                    AndroidEmptyModalText(strings.noContacts)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(targets, key = { it.id }) { chat ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onForward(chat.id) }
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    ProfileCircle(name = chat.title, isSavedMessages = chat.title == strings.savedMessages || chat.title == "Saved Messages", size = 42.dp)
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(chat.title.ifBlank { strings.unknown }, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                            if (chat.unreadCount > 0) AndroidUnreadBadge(chat.unreadCount)
                                        }
                                        Text(chat.openHeaderSubtitle(strings), style = MaterialTheme.typography.bodySmall, color = if (chat.isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
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

@Composable
private fun AndroidMessageSearchBar(
    query: String,
    strings: NoveoStrings,
    tgColors: TelegramHomeColors,
    resultCount: Int,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(56.dp),
        color = tgColors.incomingBubble,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderIconButton(icon = Icons.AutoMirrored.Outlined.ArrowBack, onClick = onClose, tint = tgColors.headerIcon)
            SearchField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = strings.searchMessages,
                modifier = Modifier.weight(1f).height(42.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (query.isBlank()) strings.searchMessages else "$resultCount ${strings.messages}",
                color = tgColors.headerSubtitle,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(min = 54.dp, max = 96.dp)
            )
        }
    }
}

@Composable
private fun EmptyMessageSearchSurface(strings: NoveoStrings, tgColors: TelegramHomeColors, query: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 80.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = tgColors.headerSubtitle.copy(alpha = 0.65f), modifier = Modifier.size(42.dp))
            Spacer(Modifier.height(12.dp))
            Text(
                text = strings.noMessagesYet,
                color = tgColors.headerTitle,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            if (query.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = query,
                    color = tgColors.headerSubtitle,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AndroidJoinChatBar(
    strings: NoveoStrings,
    tgColors: TelegramHomeColors,
    onJoin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 6.dp)
            .padding(bottom = 4.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clickable(onClick = onJoin),
                shape = RoundedCornerShape(24.dp),
                color = tgColors.composerField,
                shadowElevation = 1.dp
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = strings.join,
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
                    .clickable(onClick = onJoin),
                shape = CircleShape,
                color = tgColors.composerField,
                shadowElevation = 1.dp
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = tgColors.composerBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AndroidCannotSendBar(
    strings: NoveoStrings,
    tgColors: TelegramHomeColors,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 6.dp)
            .padding(bottom = 4.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            color = tgColors.composerField.copy(alpha = 0.96f),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = tgColors.composerIcon,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = strings.cannotSendMessage,
                    color = tgColors.composerIcon,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AndroidPinnedMessageBar(
    message: NoveoHomeMessage,
    strings: NoveoStrings,
    tgColors: TelegramHomeColors,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = tgColors.incomingBubble.copy(alpha = 0.98f),
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(3.dp).height(34.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp)))
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(strings.pinnedMessage, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(
                    text = message.text.ifBlank { message.attachmentName ?: strings.messagePlaceholder },
                    color = tgColors.headerSubtitle,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = tgColors.headerIcon, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun AndroidDateSeparator(label: String, tgColors: TelegramHomeColors) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
        Surface(
            color = tgColors.incomingBubble.copy(alpha = if (tgColors.isDark) 0.55f else 0.80f),
            shape = RoundedCornerShape(14.dp),
            shadowElevation = 0.5.dp
        ) {
            Text(
                label,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                color = tgColors.headerSubtitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AndroidWelcomePane(strings: NoveoStrings, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Text(strings.brandName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(strings.selectChatHint, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun EmptyMessagesSurface(strings: NoveoStrings, tgColors: TelegramHomeColors) {
    Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
        Surface(shape = RoundedCornerShape(20.dp), color = tgColors.incomingBubble.copy(alpha = 0.88f)) {
            Text(strings.noMessagesYet, modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp), color = tgColors.headerSubtitle)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AndroidStyleMessageRow(
    message: NoveoHomeMessage,
    strings: NoveoStrings,
    showSenderInfo: Boolean,
    hasTail: Boolean,
    isGroupChat: Boolean,
    maxBubbleWidth: androidx.compose.ui.unit.Dp,
    tgColors: TelegramHomeColors,
    onOpenMenu: (Rect) -> Unit
) {
    if (message.isSystem) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
            Surface(color = tgColors.chatSurface.copy(alpha = 0.45f), shape = CircleShape) {
                Text(
                    text = message.text.ifBlank { strings.noMessagesYet },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        return
    }

    val ownMessage = message.isOutgoing
    var bubbleBounds by remember(message.id) { mutableStateOf(Rect.Zero) }
    var pendingIntroStarted by remember(message.id) { mutableStateOf(false) }
    LaunchedEffect(message.id) { pendingIntroStarted = true }
    val pendingIntroFraction by animateFloatAsState(
        targetValue = if (pendingIntroStarted) 1f else 0f,
        animationSpec = tween(140, easing = FastOutSlowInEasing),
        label = "message_pending_intro"
    )

    Column(
        horizontalAlignment = if (ownMessage) Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (showSenderInfo) 10.dp else 0.dp)
            .padding(bottom = if (hasTail) 6.dp else 0.dp)
            .pointerInput(message.id) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.button == PointerButton.Secondary && event.changes.any { it.pressed }) {
                            val pos = event.changes.first().position
                            onOpenMenu(bubbleBounds)
                            event.changes.forEach { it.consume() }
                        }
                    }
                }
            }
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { /* tap does nothing */ },
                onLongClick = { onOpenMenu(bubbleBounds) }
            )
            .graphicsLayer {
                if (message.pending) {
                    translationY = (1f - pendingIntroFraction) * 18f
                    translationX = (1f - pendingIntroFraction) * if (ownMessage) 26f else -26f
                    alpha = 0.35f + (0.65f * pendingIntroFraction)
                    scaleX = 0.92f + (0.08f * pendingIntroFraction)
                    scaleY = 0.92f + (0.08f * pendingIntroFraction)
                    transformOrigin = TransformOrigin(if (ownMessage) 1f else 0f, 1f)
                }
            }
    ) {
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
            if (!ownMessage && isGroupChat) {
                if (hasTail) {
                    ProfileCircle(name = message.senderName, size = 36.dp, imageUrl = message.senderAvatarUrl)
                } else {
                    Spacer(Modifier.width(36.dp))
                }
                Spacer(Modifier.width(8.dp))
            } else if (ownMessage) {
                Spacer(Modifier.weight(1f))
            }

            Column(
                horizontalAlignment = if (ownMessage) Alignment.End else Alignment.Start,
                modifier = if (ownMessage) Modifier else Modifier.weight(1f, false)
            ) {
                Surface(
                    modifier = Modifier.widthIn(max = maxBubbleWidth).onGloballyPositioned { bubbleBounds = it.boundsInRoot() },
                    shape = TelegramBubbleShape(
                        isOutgoing = ownMessage,
                        hasTail = hasTail,
                        cornerRadius = with(androidx.compose.ui.platform.LocalDensity.current) { 16.dp.toPx() }
                    ),
                    color = if (ownMessage) tgColors.outgoingBubble else tgColors.incomingBubble,
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(6.dp).padding(horizontal = 4.dp)) {
                        if (!ownMessage && isGroupChat && showSenderInfo) {
                            Text(
                                text = message.senderName,
                                color = tgColors.incomingLink,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                            )
                        }

                        if (message.forwarded) {
                            Row(modifier = Modifier.padding(start = 4.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                    contentDescription = null,
                                    tint = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink,
                                    modifier = Modifier.size(14.dp).scale(-1f, 1f)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = strings.forwarded,
                                    fontSize = 12.sp,
                                    color = (if (ownMessage) tgColors.outgoingText else tgColors.incomingLink).copy(alpha = 0.78f),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        if (!message.replyAuthor.isNullOrBlank() || !message.replyPreview.isNullOrBlank()) {
                            Surface(
                                modifier = Modifier.padding(bottom = 4.dp),
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
                                            text = message.replyAuthor ?: strings.reply,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = message.replyPreview.orEmpty(),
                                            fontSize = 13.sp,
                                            color = if (ownMessage) tgColors.outgoingTime else tgColors.incomingTime,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        if (!message.attachmentName.isNullOrBlank()) {
                            AndroidAttachmentChip(
                                message = message,
                                ownMessage = ownMessage,
                                tgColors = tgColors
                            )
                            if (message.text.isNotBlank()) Spacer(Modifier.height(4.dp))
                        }

                        if (message.text.isNotBlank()) {
                            NoveoMarkdownText(
                                text = message.text,
                                color = if (ownMessage) tgColors.outgoingText else tgColors.incomingText,
                                linkColor = if (ownMessage) tgColors.outgoingText.copy(alpha = 0.9f) else tgColors.incomingLink,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }

                        if (message.reactions.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            ReactionChipRow(message = message, ownMessage = ownMessage, tgColors = tgColors)
                        }

                        if (message.botButtons.isNotEmpty()) {
                            Spacer(Modifier.height(6.dp))
                            AndroidInlineKeyboard(buttonRows = message.botButtons, ownMessage = ownMessage, tgColors = tgColors)
                        }

                        Row(modifier = Modifier.align(Alignment.End).padding(top = 1.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (message.edited) {
                                Text(
                                    strings.edited,
                                    fontSize = 11.sp,
                                    color = (if (ownMessage) tgColors.outgoingTime else tgColors.incomingTime).copy(alpha = 0.7f),
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                            Text(
                                message.time,
                                fontSize = 11.sp,
                                color = if (ownMessage) tgColors.outgoingTime else tgColors.incomingTime
                            )
                            if (ownMessage) {
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    imageVector = when {
                                        message.pending -> Icons.Outlined.Schedule
                                        message.seen -> Icons.Outlined.DoneAll
                                        else -> Icons.Outlined.Check
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(if (message.seen) 15.dp else 13.dp),
                                    tint = tgColors.outgoingTime
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AndroidInlineKeyboard(buttonRows: List<List<String>>, ownMessage: Boolean, tgColors: TelegramHomeColors) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
        buttonRows.take(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                row.take(3).forEach { label ->
                    Surface(
                        modifier = Modifier.weight(1f).height(34.dp).clickable { },
                        shape = RoundedCornerShape(9.dp),
                        color = (if (ownMessage) tgColors.outgoingText else tgColors.incomingLink).copy(alpha = 0.10f),
                        border = BorderStroke(1.dp, (if (ownMessage) tgColors.outgoingText else tgColors.incomingLink).copy(alpha = 0.14f))
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp)) {
                            Text(
                                label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AndroidAttachmentChip(message: NoveoHomeMessage, ownMessage: Boolean, tgColors: TelegramHomeColors) {
    val attachmentName = message.attachmentName ?: return
    val isImage = message.attachmentType?.startsWith("image/", ignoreCase = true) == true
    val isVideo = message.attachmentType?.startsWith("video/", ignoreCase = true) == true
    val isAudio = message.attachmentType?.startsWith("audio/", ignoreCase = true) == true
    val accent = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink
    val primaryText = if (ownMessage) tgColors.outgoingText else tgColors.incomingText
    val secondaryText = if (ownMessage) tgColors.outgoingTime else tgColors.incomingTime

    if (isImage || isVideo) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(bottom = 3.dp),
            shape = RoundedCornerShape(14.dp),
            color = accent.copy(alpha = 0.10f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 154.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                accent.copy(alpha = 0.20f),
                                accent.copy(alpha = 0.07f)
                            )
                        )
                    )
            ) {
                Icon(
                    imageVector = if (isVideo) Icons.Outlined.PlayArrow else Icons.Outlined.Collections,
                    contentDescription = null,
                    tint = accent.copy(alpha = 0.70f),
                    modifier = Modifier.align(Alignment.Center).size(if (isVideo) 54.dp else 46.dp)
                )
                Surface(
                    modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth(),
                    color = Color.Black.copy(alpha = if (tgColors.isDark) 0.28f else 0.16f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            attachmentName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (tgColors.isDark) Color.White else primaryText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        message.attachmentSizeLabel?.let { size ->
                            Text(size, color = if (tgColors.isDark) Color.White.copy(alpha = 0.78f) else secondaryText, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
        return
    }

    if (isAudio) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
            shape = RoundedCornerShape(13.dp),
            color = accent.copy(alpha = 0.10f)
        ) {
            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(42.dp).clip(CircleShape).background(accent.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.PlayArrow, contentDescription = null, tint = accent, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(22.dp)) {
                        repeat(22) { index ->
                            val height = when (index % 6) {
                                0 -> 8.dp
                                1 -> 14.dp
                                2 -> 18.dp
                                3 -> 12.dp
                                4 -> 20.dp
                                else -> 10.dp
                            }
                            Box(
                                modifier = Modifier
                                    .padding(end = 2.dp)
                                    .width(2.dp)
                                    .height(height)
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(accent.copy(alpha = 0.42f))
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(attachmentName, maxLines = 1, overflow = TextOverflow.Ellipsis, color = primaryText, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Text(message.attachmentSizeLabel ?: stringsFileLabel(message.attachmentType), color = secondaryText, fontSize = 11.sp)
                    }
                }
            }
        }
        return
    }

    Surface(
        modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
        shape = RoundedCornerShape(12.dp),
        color = accent.copy(alpha = 0.10f)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(42.dp).clip(RoundedCornerShape(9.dp)).background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(attachmentName, maxLines = 1, overflow = TextOverflow.Ellipsis, color = primaryText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(message.attachmentSizeLabel ?: message.attachmentType ?: stringsFileLabel(message.attachmentType), maxLines = 1, overflow = TextOverflow.Ellipsis, color = secondaryText, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun NoveoMarkdownText(
    text: String,
    color: Color,
    linkColor: Color,
    modifier: Modifier = Modifier
) {
    val hasBold = text.contains("**")
    val hasCode = text.contains("`")
    val hasHandle = text.contains('@')

    if (!hasBold && !hasCode && !hasHandle) {
        Text(text = text, color = color, fontSize = 16.sp, lineHeight = 20.sp, modifier = modifier)
        return
    }

    val annotated = remember(text, color, linkColor) {
        buildAnnotatedString {
            var i = 0
            while (i < text.length) {
                when {
                    text.startsWith("**", i) -> {
                        val end = text.indexOf("**", i + 2)
                        if (end != -1) {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = color)) {
                                append(text.substring(i + 2, end))
                            }
                            i = end + 2
                        } else { append(text[i]); i++ }
                    }
                    text.startsWith("`", i) -> {
                        val end = text.indexOf("`", i + 1)
                        if (end != -1) {
                            withStyle(SpanStyle(
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                background = color.copy(alpha = 0.10f),
                                color = color
                            )) {
                                append(text.substring(i + 1, end))
                            }
                            i = end + 1
                        } else { append(text[i]); i++ }
                    }
                    text[i] == '@' -> {
                        var end = i + 1
                        while (end < text.length && (text[end].isLetterOrDigit() || text[end] == '_')) end++
                        if (end > i + 1) {
                            pushStringAnnotation("handle", text.substring(i, end))
                            withStyle(SpanStyle(color = linkColor, fontWeight = FontWeight.SemiBold)) {
                                append(text.substring(i, end))
                            }
                            pop()
                            i = end
                        } else { append(text[i]); i++ }
                    }
                    else -> { append(text[i]); i++ }
                }
            }
        }
    }
    Text(text = annotated, fontSize = 16.sp, lineHeight = 20.sp, modifier = modifier)
}

private fun stringsFileLabel(type: String?): String = when {
    type == null -> "File"
    type.startsWith("image/", ignoreCase = true) -> "Photo"
    type.startsWith("video/", ignoreCase = true) -> "Video"
    type.startsWith("audio/", ignoreCase = true) -> "Audio"
    else -> "File"
}

@Composable
private fun ReactionChipRow(message: NoveoHomeMessage, ownMessage: Boolean, tgColors: TelegramHomeColors) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(horizontal = 4.dp).wrapContentHeight()) {
        message.reactions.entries.take(4).forEach { (emoji, count) ->
            if (count > 0) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = (if (ownMessage) tgColors.outgoingText else tgColors.incomingLink).copy(alpha = 0.10f)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(emoji, fontSize = 12.sp)
                        Spacer(Modifier.width(2.dp))
                        Text(count.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink)
                    }
                }
            }
        }
    }
}

@Composable
private fun AndroidStyleComposer(
    modifier: Modifier = Modifier,
    draft: String,
    onDraftChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    sending: Boolean,
    tgColors: TelegramHomeColors,
    replyingTo: NoveoHomeMessage? = null,
    editingMessage: NoveoHomeMessage? = null,
    pendingAttachment: NoveoPendingAttachment? = null,
    onCancelReply: () -> Unit = {},
    onCancelEdit: () -> Unit = {},
    onRemoveAttachment: () -> Unit = {},
    onCancelSend: () -> Unit = {},
    onOpenAttachments: () -> Unit = {},
    onOpenStickers: () -> Unit = {},
    onSend: () -> Unit
) {
    val showSendButton = draft.isNotBlank() || pendingAttachment != null || sending
    val buttonColor by animateColorAsState(
        targetValue = if (sending) Color.Red else if (!showSendButton) tgColors.composerField else tgColors.composerBlue,
        label = "buttonColor"
    )
    val iconColor = if (!showSendButton) tgColors.composerIcon else Color.White
    val micScale by animateFloatAsState(targetValue = if (showSendButton) 1f else 0.96f, animationSpec = tween(150), label = "micScale")
    fun sendFromComposer() {
        if (enabled && !sending && (draft.trim().isNotBlank() || pendingAttachment != null)) {
            onSend()
        }
    }

    Box(modifier = modifier.fillMaxWidth().padding(horizontal = 6.dp).padding(bottom = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = tgColors.composerField,
                shadowElevation = 1.dp
            ) {
                Box(modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp), contentAlignment = Alignment.CenterStart) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (replyingTo != null) {
                            AndroidComposerContextBar(
                                title = replyingTo.senderName,
                                preview = replyingTo.text.ifBlank { replyingTo.attachmentName ?: stringsFileLabel(replyingTo.attachmentType) },
                                tgColors = tgColors,
                                onCancel = onCancelReply
                            )
                        }
                        if (editingMessage != null) {
                            AndroidComposerContextBar(
                                title = "Edit Message",
                                preview = editingMessage.text,
                                tgColors = tgColors,
                                onCancel = onCancelEdit
                            )
                        }
                        if (pendingAttachment != null) {
                            AndroidComposerAttachmentBar(
                                attachment = pendingAttachment,
                                tgColors = tgColors,
                                onCancel = onRemoveAttachment
                            )
                        }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        ComposerGlassIconButton(
                            icon = Icons.Outlined.InsertEmoticon,
                            contentDescription = "Emoji",
                            tint = tgColors.composerIcon,
                            onClick = onOpenStickers,
                            modifier = Modifier.padding(start = 4.dp)
                        )

                        Box(modifier = Modifier.weight(1f).padding(vertical = 10.dp, horizontal = 4.dp), contentAlignment = Alignment.CenterStart) {
                            if (draft.isBlank()) {
                                Text(placeholder, color = tgColors.composerHint, fontSize = 17.sp)
                            }
                            BasicTextField(
                                value = draft,
                                onValueChange = onDraftChange,
                                enabled = enabled && !sending,
                                cursorBrush = SolidColor(tgColors.composerCursor),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp, color = tgColors.composerText, lineHeight = 22.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onPreviewKeyEvent { event ->
                                        val plainEnter = event.key == Key.Enter &&
                                            event.type == KeyEventType.KeyDown &&
                                            !event.isShiftPressed &&
                                            !event.isCtrlPressed &&
                                            !event.isAltPressed
                                        if (plainEnter) {
                                            sendFromComposer()
                                            true
                                        } else {
                                            false
                                        }
                                    },
                                maxLines = 6
                            )
                        }

                        ComposerGlassIconButton(
                            icon = Icons.Outlined.AttachFile,
                            contentDescription = "Attach",
                            tint = tgColors.composerIcon,
                            onClick = onOpenAttachments,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Box(Modifier.size(48.dp))
        }

        Surface(
            modifier = Modifier.align(Alignment.BottomEnd).size(48.dp).scale(micScale),
            shape = CircleShape,
            color = buttonColor,
            shadowElevation = 1.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize().clip(CircleShape).clickable(enabled = enabled && showSendButton) {
                    if (sending) onCancelSend() else sendFromComposer()
                },
                contentAlignment = Alignment.Center
            ) {
                if (sending) {
                    Icon(Icons.Outlined.Close, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
                } else {
                    AnimatedContent(
                        targetState = !showSendButton,
                        transitionSpec = {
                            if (!targetState) {
                                (fadeIn(tween(200)) + slideIn(
                                    animationSpec = tween(250, easing = LinearOutSlowInEasing),
                                    initialOffset = { IntOffset(-50, 50) }
                                )).togetherWith(fadeOut(tween(150)))
                            } else {
                                fadeIn(tween(150)).togetherWith(fadeOut(tween(150)))
                            }
                        },
                        label = "send_icon"
                    ) { targetIsMic ->
                        Icon(
                            imageVector = if (targetIsMic) Icons.Outlined.Mic else NoveoAndroidSendPlaneIcon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = iconColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AndroidComposerContextBar(
    title: String,
    preview: String,
    tgColors: TelegramHomeColors,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(2.dp).height(30.dp).background(tgColors.composerBlue, RoundedCornerShape(1.dp)))
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp), color = tgColors.composerBlue, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(preview, style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp), color = tgColors.composerHint, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        IconButton(onClick = onCancel, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Outlined.Close, contentDescription = null, modifier = Modifier.size(17.dp), tint = tgColors.composerHint)
        }
    }
}


@Composable
private fun AndroidComposerAttachmentBar(
    attachment: NoveoPendingAttachment,
    tgColors: TelegramHomeColors,
    onCancel: () -> Unit
) {
    val clampedProgress = attachment.progress.coerceIn(0f, 1f)
    Surface(
        modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 10.dp, top = 8.dp, bottom = 2.dp),
        shape = RoundedCornerShape(12.dp),
        color = tgColors.composerPanel.copy(alpha = 0.85f)
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(tgColors.composerBlue.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Description, contentDescription = null, tint = tgColors.composerBlue, modifier = Modifier.size(21.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(attachment.fileName, maxLines = 1, overflow = TextOverflow.Ellipsis, color = tgColors.composerText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(
                        text = if (attachment.isUploading) "${(clampedProgress * 100).roundToInt()}%" else attachment.sizeLabel.ifBlank { stringsFileLabel(attachment.mimeType) },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = tgColors.composerHint,
                        fontSize = 12.sp
                    )
                }
                IconButton(onClick = onCancel, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Outlined.Close, contentDescription = null, tint = tgColors.composerIcon, modifier = Modifier.size(18.dp))
                }
            }
            if (attachment.isUploading) {
                Box(Modifier.fillMaxWidth().height(2.dp).background(tgColors.composerDivider)) {
                    Box(Modifier.fillMaxWidth(clampedProgress).height(2.dp).background(tgColors.composerBlue))
                }
            }
        }
    }
}

@Composable
private fun ComposerGlassIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(38.dp).clip(CircleShape).background(Color.Transparent, CircleShape).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(22.dp), tint = tint)
    }
}

private data class AndroidContextMenuState(
    val message: NoveoHomeMessage,
    val bubbleBounds: Rect
)

@Composable
private fun MessageContextMenuOverlay(
    state: AndroidContextMenuState,
    strings: NoveoStrings,
    tgColors: TelegramHomeColors,
    onReply: () -> Unit,
    onEdit: () -> Unit,
    onToggleReaction: (String) -> Unit,
    onPin: () -> Unit,
    onCopyText: () -> Unit,
    onForward: () -> Unit,
    onSeenBy: () -> Unit,
    onAddAsSticker: () -> Unit,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val message = state.message
    var expanded by remember { mutableStateOf(false) }
    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animateIn = true }
    val wrapperScale by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0.85f,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "contextMenuScale"
    )
    val wrapperAlpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(200),
        label = "contextMenuAlpha"
    )

    BoxWithConstraints(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.42f)).clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
        )

        val density = androidx.compose.ui.platform.LocalDensity.current
        val menuWidthPx = with(density) { 320.dp.toPx() }
        val reactionHeight = if (expanded) 320.dp else 52.dp
        val actionRows = androidMessageActionCount(message)
        val actionHeightPx = with(density) { reactionHeight.toPx() + 6.dp.toPx() + (8.dp + (actionRows * 40).dp).toPx() }
        val safePx = with(density) { 8.dp.toPx() }
        val safeBottomPx = with(density) { 16.dp.toPx() }
        val gapPx = with(density) { 10.dp.toPx() }
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }
        val preferredLeft = if (message.isOutgoing) {
            state.bubbleBounds.right - menuWidthPx + with(density) { 32.dp.toPx() }
        } else {
            state.bubbleBounds.left - with(density) { 32.dp.toPx() }
        }
        val left = preferredLeft.coerceIn(safePx, (screenWidthPx - menuWidthPx - safePx).coerceAtLeast(safePx))
        val spaceAbove = state.bubbleBounds.top - safePx - gapPx
        val spaceBelow = screenHeightPx - state.bubbleBounds.bottom - safeBottomPx - gapPx
        val renderBelow = spaceAbove < actionHeightPx && spaceBelow > with(density) { 64.dp.toPx() }
        val preferredTop = if (renderBelow) state.bubbleBounds.bottom + gapPx else state.bubbleBounds.top - actionHeightPx - gapPx
        val top = preferredTop.coerceIn(safePx, (screenHeightPx - actionHeightPx - safeBottomPx).coerceAtLeast(safePx))

        Column(
            modifier = Modifier
                .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                .graphicsLayer {
                    alpha = wrapperAlpha
                    scaleX = wrapperScale
                    scaleY = wrapperScale
                    transformOrigin = TransformOrigin(0.5f, if (renderBelow) 0f else 1f)
                },
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (renderBelow && !expanded) {
                AndroidMessageActionMenu(message, strings, tgColors, onReply, onEdit, onPin, onCopyText, onForward, onSeenBy, onAddAsSticker, onDownload, onDelete)
            }

            Surface(
                color = tgColors.incomingBubble,
                shape = RoundedCornerShape(if (expanded) 18.dp else 26.dp),
                shadowElevation = 8.dp
            ) {
                if (expanded) {
                    Column(modifier = Modifier.width(320.dp).height(320.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(strings.reactions, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = tgColors.incomingTime)
                            Box(
                                modifier = Modifier.size(28.dp).clip(CircleShape).background(if (tgColors.isDark) Color(0xFF2C353F) else Color(0xFFF0F2F5)).clickable { expanded = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = tgColors.incomingTime, modifier = Modifier.size(18.dp))
                            }
                        }
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(6),
                            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(ANDROID_CONTEXT_MENU_REACTIONS) { emoji ->
                                ReactionButton(emoji = emoji, expanded = true, tgColors = tgColors, onClick = { onToggleReaction(emoji) })
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.width(320.dp).height(52.dp).padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ANDROID_CONTEXT_MENU_QUICK_REACTIONS.forEach { emoji ->
                            ReactionButton(emoji = emoji, expanded = false, tgColors = tgColors, onClick = { onToggleReaction(emoji) })
                        }
                        Box(
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(if (tgColors.isDark) Color(0xFF2C353F) else Color(0xFFF0F2F5)).clickable { expanded = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.ExpandMore, contentDescription = null, tint = tgColors.incomingTime, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            if (!renderBelow && !expanded) {
                AndroidMessageActionMenu(message, strings, tgColors, onReply, onEdit, onPin, onCopyText, onForward, onSeenBy, onAddAsSticker, onDownload, onDelete)
            }
        }
    }
}

@Composable
private fun ReactionButton(emoji: String, expanded: Boolean, tgColors: TelegramHomeColors, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (expanded) (if (tgColors.isDark) Color(0xFF2C353F) else Color(0xFFF0F2F5)) else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 20.sp)
    }
}

private fun androidMessageActionCount(message: NoveoHomeMessage): Int {
    var count = 4 // reply, pin/unpin, forward, delete
    if (message.isOutgoing && message.text.isNotBlank()) count += 1
    if (message.text.isNotBlank()) count += 1
    if (message.seen && message.isOutgoing) count += 1
    if (!message.attachmentName.isNullOrBlank()) count += 1
    if (message.attachmentType?.startsWith("image/", ignoreCase = true) == true) count += 1
    return count
}

@Composable
private fun AndroidMessageActionMenu(
    message: NoveoHomeMessage,
    strings: NoveoStrings,
    tgColors: TelegramHomeColors,
    onReply: () -> Unit,
    onEdit: () -> Unit,
    onPin: () -> Unit,
    onCopyText: () -> Unit,
    onForward: () -> Unit,
    onSeenBy: () -> Unit,
    onAddAsSticker: () -> Unit,
    onDownload: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = tgColors.incomingBubble,
        shadowElevation = 8.dp
    ) {
        Column(Modifier.width(220.dp).padding(vertical = 4.dp)) {
            MenuItem(strings.reply, Icons.AutoMirrored.Outlined.ArrowBack, tgColors.headerIcon, tgColors.incomingText, onReply)
            if (message.isOutgoing && message.text.isNotBlank()) {
                MenuItem(strings.edit, Icons.Outlined.Edit, tgColors.headerIcon, tgColors.incomingText, onEdit)
            }
            MenuItem(if (message.isPinned) strings.unpin else strings.pin, Icons.Outlined.Bookmark, tgColors.headerIcon, tgColors.incomingText, onPin)
            if (message.text.isNotBlank()) {
                MenuItem(strings.copyText, Icons.Outlined.Description, tgColors.headerIcon, tgColors.incomingText, onCopyText)
            }
            MenuItem(strings.forward, Icons.AutoMirrored.Outlined.ArrowForward, tgColors.headerIcon, tgColors.incomingText, onForward)
            if (message.seen && message.isOutgoing) {
                MenuItem(strings.seenBy, Icons.Outlined.Check, tgColors.headerIcon, tgColors.incomingText, onSeenBy)
            }
            if (message.attachmentType?.startsWith("image/", ignoreCase = true) == true) {
                MenuItem(strings.addAsSticker, Icons.Outlined.Star, tgColors.headerIcon, tgColors.incomingText, onAddAsSticker)
            }
            if (!message.attachmentName.isNullOrBlank()) {
                MenuItem(strings.download, Icons.Outlined.KeyboardArrowDown, tgColors.headerIcon, tgColors.incomingText, onDownload)
            }
            MenuItem(strings.delete, Icons.Outlined.Delete, Color(0xFFE53935), Color(0xFFE53935), onDelete)
        }
    }
}

@Composable
private fun MenuItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color, textColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(14.dp))
        Text(label, fontSize = 15.sp, color = textColor)
    }
}

@Composable
private fun HeaderIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
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
private fun ProfileCircle(
    name: String,
    isSavedMessages: Boolean = false,
    size: androidx.compose.ui.unit.Dp = 40.dp,
    imageUrl: String? = null,
    modifier: Modifier = Modifier
) {
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

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Fallback gradient + initial always rendered underneath
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f)
                    )
                )
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(name.firstOrNull()?.uppercase() ?: "N", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
        // Real photo on top if available
        if (!imageUrl.isNullOrBlank()) {
            val ctx = LocalPlatformContext.current
            AsyncImage(
                model = ImageRequest.Builder(ctx)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = name,
                modifier = Modifier.fillMaxSize().clip(CircleShape)
            )
        }
    }
}

@Composable
private fun VerifiedIcon(modifier: Modifier = Modifier.size(14.dp)) {
    Box(modifier = modifier.background(Color(0xFF2EA6FF), CircleShape), contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = "Verified",
            tint = Color.White,
            modifier = Modifier.fillMaxSize().padding(2.dp)
        )
    }
}

@Composable
private fun UnreadBadge(count: Int) {
    Box(modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primary).padding(horizontal = 8.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
        Text(count.coerceAtMost(99).toString(), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun AndroidUnreadBadge(count: Int) {
    UnreadBadge(count)
}
