package ir.hienob.noveo.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import ir.hienob.noveo.R
import ir.hienob.noveo.data.ChatMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import coil3.compose.SubcomposeAsyncImage

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
internal fun ChatInput(
    draft: String,
    onDraftChange: (String) -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    sendScale: Float = 1f,
    replyingTo: ChatMessage? = null,
    editingMessage: ChatMessage? = null,
    onCancelReply: () -> Unit = {},
    onCancelEdit: () -> Unit = {},
    placeholder: String = "Message",
    onAttachClick: () -> Unit = {},
    onLongAttachClick: () -> Unit = {},
    onEmojiClick: () -> Unit = {},
    onPasteUri: (android.net.Uri) -> Unit = {},
    onTextFieldFocused: () -> Unit = {},
    showStickers: Boolean = false,
    hasAttachment: Boolean = false,
    isSendingMessage: Boolean = false,
    onCancelSend: () -> Unit = {},
    tgColors: TelegramThemeColors = telegramColors(),
    strings: NoveoStrings
) {
    val buttonInteraction = remember { MutableInteractionSource() }
    val inputFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }
    var isRecording by remember { mutableStateOf(false) }
    var recordTimeMillis by remember { mutableStateOf(0L) }
    
    var dragOffsetX by remember { mutableStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "dot")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            audioRecorder.start()
            isRecording = true
            recordTimeMillis = 0L
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                delay(50)
                recordTimeMillis += 50
            }
        } else {
            dragOffsetX = 0f
        }
    }

    LaunchedEffect(replyingTo?.id, editingMessage?.id) {
        if ((replyingTo != null || editingMessage != null) && !isRecording) {
            inputFocusRequester.requestFocus()
            keyboardController?.show()
        }
        if (editingMessage != null) {
            onDraftChange(editingMessage.content.text ?: "")
        }
    }

    fun finishRecording(send: Boolean) {
        if (!isRecording) return
        isRecording = false
        dragOffsetX = 0f
        if (send) {
            audioRecorder.stop()
            audioRecorder.outputFile?.let { file ->
                val uri = android.net.Uri.fromFile(file)
                onPasteUri(uri)
            }
        } else {
            audioRecorder.cancel()
        }
    }

    val showSendButton = (draft.isNotBlank() || hasAttachment || isSendingMessage)
    val targetButtonColor = if (isRecording || isSendingMessage) Color.Red else if (!showSendButton) tgColors.composerField else tgColors.composerBlue
    val buttonColor by animateColorAsState(targetValue = targetButtonColor, label = "buttonColor")
    val iconColor = if (!showSendButton && !isRecording) tgColors.composerIcon else Color.White

    val baseTargetScale = if (isRecording) {
        val swipeProgress = (Math.abs(dragOffsetX) / 150f).coerceIn(0f, 1f)
        val scaleReduction = swipeProgress * 0.5f
        (1.0f - scaleReduction).coerceAtLeast(0.5f)
    } else {
        sendScale
    }

    val micScale by animateFloatAsState(
        targetValue = baseTargetScale,
        animationSpec = tween(if (isRecording) 50 else 150),
        label = "micScale"
    )

    val animDragOffsetX by animateFloatAsState(
        targetValue = dragOffsetX,
        animationSpec = tween(150),
        label = "animDragOffsetX"
    )

    Box(modifier = modifier.fillMaxWidth().padding(horizontal = 6.dp).padding(bottom = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = tgColors.composerField,
                shadowElevation = 1.dp
            ) {
                Box(modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp), contentAlignment = Alignment.CenterStart) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (isRecording) 0f else 1f)
                    ) {
                        if (replyingTo != null) {
                            Row(modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.width(2.dp).height(30.dp).background(tgColors.composerBlue, RoundedCornerShape(1.dp)))
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(replyingTo.senderName, style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp), color = tgColors.composerBlue, fontWeight = FontWeight.Bold, maxLines = 1)
                                    Text(localizeMessagePreview(replyingTo.content.previewText(), strings), style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp), color = tgColors.composerHint, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                IconButton(onClick = onCancelReply, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Outlined.Close, contentDescription = null, modifier = Modifier.size(17.dp), tint = tgColors.composerHint)
                                }
                            }
                        }

                        if (editingMessage != null) {
                            Row(modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.width(2.dp).height(30.dp).background(tgColors.composerBlue, RoundedCornerShape(1.dp)))
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Edit Message", style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp), color = tgColors.composerBlue, fontWeight = FontWeight.Bold, maxLines = 1)
                                    Text(editingMessage.content.text ?: "", style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp), color = tgColors.composerHint, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                IconButton(onClick = onCancelEdit, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Outlined.Close, contentDescription = null, modifier = Modifier.size(17.dp), tint = tgColors.composerHint)
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            GlassIconButton(
                                resId = if (showStickers) R.drawable.tg_input_smile else R.drawable.tg_input_smile, // Should use keyboard icon if showStickers but I don't have one in drawable list
                                contentDescription = "Emoji",
                                tint = tgColors.composerIcon,
                                selectorTint = Color.Transparent,
                                onClick = onEmojiClick,
                                modifier = Modifier.padding(start = 4.dp)
                            )

                            Box(modifier = Modifier.weight(1f).padding(vertical = 10.dp, horizontal = 4.dp), contentAlignment = Alignment.CenterStart) {
                                if (draft.isBlank()) {
                                    Text(placeholder, color = tgColors.composerHint, fontSize = 17.sp)
                                }
                                BasicTextField(
                                    value = draft,
                                    onValueChange = onDraftChange,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(inputFocusRequester)
                                        .onFocusChanged { if (it.isFocused) onTextFieldFocused() },
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp, color = tgColors.composerText),
                                    cursorBrush = SolidColor(tgColors.composerCursor),
                                    maxLines = 6
                                )
                            }

                            GlassIconButton(
                                resId = R.drawable.tg_msg_input_attach2,
                                contentDescription = "Attach",
                                tint = tgColors.composerIcon,
                                selectorTint = Color.Transparent,
                                onClick = onAttachClick,
                                onLongClick = onLongAttachClick,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }

                    if (isRecording) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color.Red.copy(alpha = dotAlpha)))
                            Spacer(Modifier.width(12.dp))
                            val seconds = (recordTimeMillis / 1000) % 60
                            val minutes = (recordTimeMillis / 1000) / 60
                            Text(String.format("%02d:%02d", minutes, seconds), fontSize = 17.sp, fontWeight = FontWeight.Medium, color = tgColors.composerText)
                            
                            Spacer(Modifier.weight(1f))
                            
                            val slideOffsetAbs = Math.abs(dragOffsetX)
                            val slideAlpha = (1f - (slideOffsetAbs / 150f)).coerceIn(0.2f, 1f)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.graphicsLayer { 
                                    alpha = slideAlpha 
                                    translationX = dragOffsetX / 2f
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, contentDescription = null, tint = tgColors.composerHint, modifier = Modifier.size(20.dp))
                                Text(strings.dropToAttach, fontSize = 15.sp, color = tgColors.composerHint)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.width(8.dp))
            Box(Modifier.size(48.dp))
        }

        val density = LocalDensity.current
        
        val buttonModifier = if (!showSendButton) {
            Modifier.pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    var isLocalRecording = false
                    
                    try {
                        withTimeout(200L) {
                            waitForUpOrCancellation()
                        }
                        down.consume()
                        onActionClick()
                    } catch (e: PointerEventTimeoutCancellationException) {
                        down.consume()
                        val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        
                        if (hasPermission) {
                            audioRecorder.start()
                            isRecording = true
                            recordTimeMillis = 0L
                            isLocalRecording = true
                            
                            dragOffsetX = 0f
                            var lockAxis = 0 
                            
                            while (isLocalRecording) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull()
                                
                                if (change == null || !change.pressed) {
                                    if (isRecording) {
                                        if (dragOffsetX <= -150f) finishRecording(false)
                                        else finishRecording(true)
                                    }
                                    isLocalRecording = false
                                    dragOffsetX = 0f
                                    break
                                }
                                
                                change.consume()
                                val posChange = change.positionChange()
                                
                                if (lockAxis == 0 && Math.abs(posChange.x) > 2f) {
                                    if (posChange.x < 0) {
                                        lockAxis = 1 
                                    }
                                }
                                
                                if (lockAxis == 1) {
                                    dragOffsetX = (dragOffsetX + posChange.x).coerceAtMost(0f)
                                    if (dragOffsetX <= -150f) {
                                        finishRecording(false)
                                        isLocalRecording = false
                                        dragOffsetX = 0f
                                        break
                                    }
                                }
                            }
                        } else {
                            permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                        }
                    }
                }
            }
        } else {
            Modifier.clickable(interactionSource = buttonInteraction, indication = null) {
                if (isSendingMessage) onCancelSend() else onActionClick()
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(48.dp)
                .scale(micScale)
                .offset(x = animDragOffsetX.dp / 2f, y = 0.dp),
            shape = CircleShape,
            color = buttonColor,
            shadowElevation = if (isRecording) 4.dp else 1.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .then(buttonModifier),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = !showSendButton,
                    transitionSpec = {
                        if (!targetState) {
                            (fadeIn(tween(200)) + slideIn(
                                animationSpec = tween(250, easing = LinearOutSlowInEasing),
                                initialOffset = { IntOffset(with(density) { -50.dp.roundToPx() }, with(density) { 50.dp.roundToPx() }) }
                            )).togetherWith(fadeOut(tween(150)))
                        } else {
                            fadeIn(tween(150)).togetherWith(fadeOut(tween(150)))
                        }
                    },
                    label = "send_icon"
                ) { targetIsMic ->
                    val animRotation by transition.animateFloat(
                        label = "rot",
                        transitionSpec = { tween(250) }
                    ) { state -> if (state == EnterExitState.PreEnter && !targetIsMic) -25f else 0f }

                    if (isSendingMessage) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = iconColor
                        )
                    } else {
                        Image(
                            painter = painterResource(if (targetIsMic) R.drawable.tg_input_mic else R.drawable.tg_send_plane_24),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp).graphicsLayer { rotationZ = animRotation },
                            colorFilter = ColorFilter.tint(iconColor)
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun AttachmentPreview(
    attachment: ir.hienob.noveo.app.PendingAttachment,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                if (attachment.mimeType.startsWith("image/") || attachment.mimeType.startsWith("video/")) {
                    SubcomposeAsyncImage(
                        model = attachment.uri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = { CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp) },
                        error = { Icon(Icons.Outlined.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                    )
                } else {
                    Icon(Icons.Outlined.Close, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                
                if (attachment.isUploading) {
                    CircularProgressIndicator(progress = { attachment.progress }, modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
                }
            }
            
            Spacer(Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = attachment.fileName, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = if (attachment.isUploading) "Uploading ${ (attachment.progress * 100).toInt() }%" else "Ready to send", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
            
            IconButton(onClick = onRemove, enabled = !attachment.isUploading) {
                Icon(imageVector = Icons.Outlined.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GlassIconButton(
    resId: Int,
    contentDescription: String,
    tint: Color,
    selectorTint: Color,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(modifier = modifier.size(38.dp).clip(CircleShape).background(selectorTint, CircleShape).combinedClickable(interactionSource = interactionSource, indication = null, onClick = onClick, onLongClick = onLongClick), contentAlignment = Alignment.Center) {
        Image(painter = painterResource(resId), contentDescription = contentDescription, modifier = Modifier.size(22.dp), colorFilter = ColorFilter.tint(tint))
    }
}
