package ir.hienob.noveo.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Shared, platform-neutral Noveo root frame.
 *
 * Android and desktop should both enter the product through this root. Platform-specific
 * behavior such as WebView captcha, file pickers, permissions, and URL opening is passed
 * in through callbacks/slots instead of being imported here.
 */
enum class NoveoStartupSurface {
    Splash,
    Onboarding,
    Auth,
    Home
}

data class NoveoRootFrameState(
    val startupSurface: NoveoStartupSurface,
    val languageCode: String = "en",
    val authModeSignup: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null,
    val connectionTitle: String = "Noveo"
)

@Composable
fun NoveoRootFrame(
    state: NoveoRootFrameState,
    theme: NoveoThemePreset,
    strings: NoveoStrings,
    onDismissOnboarding: () -> Unit,
    onAuthMode: (Boolean) -> Unit,
    onStartRegisterCaptcha: (String, String) -> Unit,
    onAuthSubmit: (String, String) -> Unit,
    onOpenRegistrationWeb: () -> Unit,
    homeContent: @Composable () -> Unit,
    captchaContent: @Composable () -> Unit = {}
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        NoveoTheme(theme = theme) {
            Surface(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (state.startupSurface) {
                        NoveoStartupSurface.Splash -> ConnectingShell(state.connectionTitle.ifBlank { strings.brandName })
                        NoveoStartupSurface.Onboarding -> SharedOnboardingScreen(strings, onDismissOnboarding)
                        NoveoStartupSurface.Auth -> SharedAuthScreen(
                            strings = strings,
                            authModeSignup = state.authModeSignup,
                            loading = state.loading,
                            error = state.error,
                            onAuthMode = onAuthMode,
                            onStartRegisterCaptcha = onStartRegisterCaptcha,
                            onAuthSubmit = onAuthSubmit,
                            onOpenRegistrationWeb = onOpenRegistrationWeb
                        )
                        NoveoStartupSurface.Home -> homeContent()
                    }
                    captchaContent()
                }
            }
        }
    }
}

@Composable
private fun ConnectingShell(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = title,
            label = "connection_title",
            transitionSpec = {
                (slideInVertically(initialOffsetY = { it / 2 }) + fadeIn())
                    .togetherWith(slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut())
                    .using(SizeTransform(clip = false))
            }
        ) { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SharedOnboardingScreen(strings: NoveoStrings, onDismissOnboarding: () -> Unit) {
    var page by remember { mutableStateOf(0) }
    val pages = strings.onboardingPages

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(strings.brandName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Text(pages[page], style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(pages.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == page) 12.dp else 8.dp)
                        .background(
                            if (index == page) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }
        Spacer(Modifier.height(32.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (page < pages.lastIndex) {
                Text(
                    strings.skip,
                    modifier = Modifier.clickable { onDismissOnboarding() }.padding(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Button(onClick = { page += 1 }) { Text(strings.next) }
            } else {
                Spacer(Modifier.width(1.dp))
                Button(onClick = onDismissOnboarding) { Text(strings.onboardingAction) }
            }
        }
    }
}

@Composable
private fun SharedAuthScreen(
    strings: NoveoStrings,
    authModeSignup: Boolean,
    loading: Boolean,
    error: String?,
    onAuthMode: (Boolean) -> Unit,
    onStartRegisterCaptcha: (String, String) -> Unit,
    onAuthSubmit: (String, String) -> Unit,
    onOpenRegistrationWeb: () -> Unit
) {
    var handle by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(if (authModeSignup) strings.signupTitle else strings.loginTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(if (authModeSignup) strings.switchSignup else strings.switchLogin)
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(strings.loginButton, modifier = Modifier.clickable { onAuthMode(false) }.padding(8.dp), fontWeight = if (!authModeSignup) FontWeight.Bold else FontWeight.Normal)
            Text(strings.signupButton, modifier = Modifier.clickable { onAuthMode(true) }.padding(8.dp), fontWeight = if (authModeSignup) FontWeight.Bold else FontWeight.Normal)
        }
        Spacer(Modifier.height(12.dp))

        if (authModeSignup) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(strings.registerOnWebTitle, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(strings.registerOnWebBody)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onOpenRegistrationWeb,
                        enabled = !loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(strings.openNoveoWeb)
                    }
                }
            }
        } else {
            OutlinedTextField(value = handle, onValueChange = { handle = it }, label = { Text(strings.handlePlaceholder) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text(strings.passwordPlaceholder) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { onAuthSubmit(handle, password) },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(strings.loginButton)
            }
        }
        error?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
