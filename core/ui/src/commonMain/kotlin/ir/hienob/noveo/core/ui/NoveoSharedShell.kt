package ir.hienob.noveo.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Cross-platform Noveo UI primitives shared by Android and desktop.
 *
 * The Android app still owns Android-only behavior such as permissions, file pickers,
 * notifications, and voice services. Desktop should compose these same shell primitives
 * instead of keeping a separate placeholder UI.
 */
enum class NoveoThemePreset {
    SKY_LIGHT,
    LIGHT,
    SUNSET_LIGHT,
    DARK,
    OCEAN_DARK,
    PLUM_DARK,
    OLED_DARK,
    SUNSET_SHIMMER,
    CHERRY_RED,
    SNOWY_DAYDREAM,
    RAINBOW_RAGEBAIT
}


@Composable
fun NoveoTheme(
    theme: NoveoThemePreset = NoveoThemePreset.SKY_LIGHT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        NoveoThemePreset.SKY_LIGHT -> lightColorScheme(
            primary = Color(0xFF2563EB),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFDBEAFE),
            onPrimaryContainer = Color(0xFF1E40AF),
            secondary = Color(0xFF60A5FA),
            secondaryContainer = Color(0xFFEAF2FF),
            onSecondaryContainer = Color(0xFF123A6F),
            background = Color(0xFFE0F2FE),
            surface = Color(0xFFF8FCFF),
            surfaceVariant = Color(0xFFECFEFF),
            onSurface = Color(0xFF082F49),
            onSurfaceVariant = Color(0xFF0F4C68),
            outline = Color(0xFFBAE6FD),
            error = Color(0xFFDC2626)
        )
        NoveoThemePreset.LIGHT -> lightColorScheme(
            primary = Color(0xFF2563EB),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFEFF6FF),
            onPrimaryContainer = Color(0xFF1E40AF),
            secondary = Color(0xFF60A5FA),
            secondaryContainer = Color(0xFFEAF2FF),
            onSecondaryContainer = Color(0xFF123A6F),
            background = Color(0xFFEEF4FF),
            surface = Color.White,
            surfaceVariant = Color(0xFFF8FAFC),
            onSurface = Color(0xFF0F172A),
            onSurfaceVariant = Color(0xFF475569),
            outline = Color(0xFFDBEAFE),
            error = Color(0xFFDC2626)
        )
        NoveoThemePreset.SUNSET_LIGHT -> lightColorScheme(
            primary = Color(0xFFEA580C),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFF7ED),
            onPrimaryContainer = Color(0xFF431407),
            secondary = Color(0xFFFB923C),
            secondaryContainer = Color(0xFFFFEDD5),
            onSecondaryContainer = Color(0xFF7C2D12),
            background = Color(0xFFFFF7ED),
            surface = Color(0xFFFFFAF5),
            surfaceVariant = Color(0xFFFDE6D7),
            onSurface = Color(0xFF431407),
            onSurfaceVariant = Color(0xFF7C2D12),
            outline = Color(0xFFFDBA74)
        )
        NoveoThemePreset.OCEAN_DARK -> darkColorScheme(
            primary = Color(0xFF1D4ED8),
            onPrimary = Color.White,
            primaryContainer = Color(0xFF0F2742),
            onPrimaryContainer = Color(0xFFEFF6FF),
            secondary = Color(0xFF3B82F6),
            background = Color(0xFF071423),
            surface = Color(0xFF081A2F),
            surfaceVariant = Color(0xFF0C233D),
            onSurface = Color(0xFFEFF6FF),
            onSurfaceVariant = Color(0xFFDBEAFE),
            outline = Color(0xFF12324F),
            error = Color(0xFFEF4444)
        )
        NoveoThemePreset.DARK -> darkColorScheme(
            primary = Color(0xFF3B82F6),
            onPrimary = Color.White,
            primaryContainer = Color(0xFF1E293B),
            onPrimaryContainer = Color(0xFFF8FAF4),
            secondary = Color(0xFF60A5FA),
            background = Color(0xFF020617),
            surface = Color(0xFF0F172A),
            surfaceVariant = Color(0xFF111827),
            onSurface = Color(0xFFF8FAF4),
            onSurfaceVariant = Color(0xFFCBD5E1),
            outline = Color(0xFF1F2937),
            error = Color(0xFFEF4444)
        )
        NoveoThemePreset.PLUM_DARK -> darkColorScheme(
            primary = Color(0xFFA855F7),
            onPrimary = Color.White,
            primaryContainer = Color(0xFF2E1065),
            onPrimaryContainer = Color(0xFFFAF5FF),
            secondary = Color(0xFFD8B4FE),
            background = Color(0xFF14051F),
            surface = Color(0xFF1B0F2A),
            surfaceVariant = Color(0xFF2A1741),
            onSurface = Color(0xFFFAF5FF),
            onSurfaceVariant = Color(0xFFF3E8FF),
            outline = Color(0xFF4C1D95)
        )
        NoveoThemePreset.OLED_DARK -> darkColorScheme(
            primary = Color(0xFF3B82F6),
            onPrimary = Color.White,
            primaryContainer = Color(0xFF0B0B0B),
            onPrimaryContainer = Color.White,
            secondary = Color(0xFF60A5FA),
            background = Color.Black,
            surface = Color.Black,
            surfaceVariant = Color(0xFF050505),
            onSurface = Color.White,
            onSurfaceVariant = Color(0xFFE5E5E5),
            outline = Color(0xFF1F1F1F)
        )
        NoveoThemePreset.SUNSET_SHIMMER -> darkColorScheme(
            primary = Color(0xFFF97316),
            onPrimary = Color.White,
            primaryContainer = Color(0xFF4A2311),
            onPrimaryContainer = Color(0xFFFFF7ED),
            secondary = Color(0xFFFB923C),
            background = Color(0xFF180B07),
            surface = Color(0xFF21110B),
            surfaceVariant = Color(0xFF2F170D),
            onSurface = Color(0xFFFFF7ED),
            onSurfaceVariant = Color(0xFFFED7AA),
            outline = Color(0xFF7C2D12)
        )
        NoveoThemePreset.CHERRY_RED -> darkColorScheme(
            primary = Color(0xFFD2042D),
            onPrimary = Color.White,
            primaryContainer = Color(0xFF450A0A),
            onPrimaryContainer = Color(0xFFFECACA),
            secondary = Color(0xFFFF2C55),
            onSecondary = Color.White,
            background = Color(0xFF0C0A09),
            surface = Color(0xFF1C1917),
            surfaceVariant = Color(0xFF292524),
            onSurface = Color(0xFFF5F5F4),
            onSurfaceVariant = Color(0xFFD6D3D1),
            outline = Color(0xFF44403C)
        )
        NoveoThemePreset.SNOWY_DAYDREAM -> lightColorScheme(
            primary = Color(0xFF3B82F6),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFDDE6FF),
            onPrimaryContainer = Color(0xFF102041),
            inversePrimary = Color(0xFF9CC4FF),
            secondary = Color(0xFF5E7FA8),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFEAF2FF),
            onSecondaryContainer = Color(0xFF123A6F),
            tertiary = Color(0xFF6B8FB8),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFE7F0FF),
            onTertiaryContainer = Color(0xFF15365F),
            background = Color(0xFFF1F7FF),
            onBackground = Color(0xFF102041),
            surface = Color.White.copy(alpha = 0.92f),
            onSurface = Color(0xFF102041),
            surfaceVariant = Color(0xFFEAF2FF),
            onSurfaceVariant = Color(0xFF375575),
            surfaceTint = Color(0xFF3B82F6),
            inverseSurface = Color(0xFF213A5C),
            inverseOnSurface = Color(0xFFF1F7FF),
            outline = Color(0xFFBFDBFE),
            outlineVariant = Color(0xFFD7E8FF),
            error = Color(0xFFDC2626),
            errorContainer = Color(0xFFFFE1E1),
            onError = Color.White,
            onErrorContainer = Color(0xFF7F1D1D)
        )
        NoveoThemePreset.RAINBOW_RAGEBAIT -> darkColorScheme(
            primary = Color(0xFFFF4FD8),
            onPrimary = Color.Black,
            primaryContainer = Color(0xFF331D52),
            onPrimaryContainer = Color(0xFFFDF4FF),
            secondary = Color(0xFFD8B4FE),
            onSecondary = Color(0xFF3B0764),
            background = Color(0xFF020617),
            surface = Color(0xFF0F172A),
            surfaceVariant = Color(0xFF1E293B),
            onSurface = Color(0xFFF8FAFC),
            onSurfaceVariant = Color(0xFFCBD5E1),
            outline = Color(0xFF334155)
        )
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}
