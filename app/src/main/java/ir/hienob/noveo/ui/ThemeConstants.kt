package ir.hienob.noveo.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Composable
fun telegramColors(): TelegramThemeColors {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val colorScheme = MaterialTheme.colorScheme
    
    return if (!isDark) {
        // Light Theme - Dynamic colors based on colorScheme
        TelegramThemeColors(
            isDark = false,
            composerBlue = colorScheme.primary,
            composerPanel = colorScheme.surfaceVariant.copy(alpha = 0.5f),
            composerField = colorScheme.surface,
            composerIcon = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            composerHint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            composerDivider = colorScheme.outlineVariant.copy(alpha = 0.3f),
            composerText = colorScheme.onSurface,
            composerCursor = colorScheme.primary,
            chatSurface = colorScheme.background,
            headerTitle = colorScheme.onSurface,
            headerSubtitle = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            headerIcon = colorScheme.onSurfaceVariant,
            incomingBubble = colorScheme.surface,
            incomingBubbleSelected = colorScheme.surfaceVariant,
            outgoingBubble = colorScheme.primaryContainer.copy(alpha = 0.8f), 
            outgoingBubbleSelected = colorScheme.primaryContainer,
            incomingText = colorScheme.onSurface,
            incomingLink = colorScheme.primary,
            incomingTime = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            outgoingText = colorScheme.onPrimaryContainer,
            outgoingTime = colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            replyIncoming = colorScheme.secondaryContainer.copy(alpha = 0.5f),
            replyOutgoing = colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
        )
    } else {
        // Dark Theme - Dynamic colors based on colorScheme
        TelegramThemeColors(
            isDark = true,
            composerBlue = colorScheme.primary,
            composerPanel = colorScheme.surfaceVariant.copy(alpha = 0.3f),
            composerField = colorScheme.surface,
            composerIcon = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            composerHint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            composerDivider = colorScheme.outlineVariant.copy(alpha = 0.3f),
            composerText = colorScheme.onSurface,
            composerCursor = colorScheme.primary,
            chatSurface = colorScheme.background,
            headerTitle = colorScheme.onSurface,
            headerSubtitle = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            headerIcon = colorScheme.onSurfaceVariant,
            incomingBubble = colorScheme.surface,
            incomingBubbleSelected = colorScheme.surfaceVariant,
            outgoingBubble = colorScheme.primaryContainer.copy(alpha = 0.7f),
            outgoingBubbleSelected = colorScheme.primaryContainer,
            incomingText = colorScheme.onSurface,
            incomingLink = colorScheme.primary,
            incomingTime = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            outgoingText = colorScheme.onPrimaryContainer,
            outgoingTime = colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            replyIncoming = colorScheme.secondaryContainer.copy(alpha = 0.4f),
            replyOutgoing = colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
        )
    }
}

data class TelegramThemeColors(
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

// Legacy constants for backward compatibility if needed during refactoring
val TelegramComposerBlue = Color(0xFF229AF0)
val TelegramComposerPanel = Color(0xFFF6F7F8)
val TelegramComposerField = Color.White
val TelegramComposerIcon = Color(0xFF7A8591)
val TelegramComposerHint = Color(0xFF7A8591)
val TelegramComposerDivider = Color(0x14000000)
val TelegramComposerText = Color(0xFF000000)
val TelegramComposerCursor = Color(0xFF459DE1)
val TelegramChatSurface = Color(0xFF91A8C0)
val TelegramHeaderTitle = Color(0xFF333333)
val TelegramHeaderSubtitle = Color(0xFF797979)
val TelegramHeaderIcon = Color(0xFF6B7A8C)
val TelegramIncomingBubble = Color(0xFFFFFFFF)
val TelegramIncomingBubbleSelected = Color(0xFFF2F2F2)
val TelegramOutgoingBubble = Color(0xFFEFFDDE)
val TelegramOutgoingBubbleSelected = Color(0xFFD9F7C5)
val TelegramIncomingText = Color(0xFF222222)
val TelegramIncomingLink = Color(0xFF127ACA)
val TelegramIncomingTime = Color(0xFF939599)
val TelegramOutgoingText = Color(0xFF222222)
val TelegramOutgoingTime = Color(0xFF66A060)
val TelegramReplyIncoming = Color(0xFFD8E8F7)
val TelegramReplyOutgoing = Color(0x80FFFFFF)
