package ir.hienob.noveo.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.io.File
import java.net.URI
import java.util.zip.GZIPInputStream

private const val NOVEO_BASE_URL = "https://noveo.ir:8443"
private var tgsClient: OkHttpClient? = null

private fun resolveTgsUrl(value: String): String {
    val trimmed = value.trim()
    if (trimmed.startsWith("http://", true) || trimmed.startsWith("https://", true)) return trimmed
    if (trimmed.startsWith("//")) return "https:$trimmed"
    if (trimmed.startsWith("/")) return "$NOVEO_BASE_URL$trimmed"
    return "$NOVEO_BASE_URL/$trimmed"
}

private fun loadTgsJson(url: String): String {
    val trimmed = url.trim()
    val localFile = when {
        trimmed.startsWith("file:", true) -> runCatching { File(URI(trimmed)) }.getOrNull()
        trimmed.startsWith("/") -> File(trimmed).takeIf { it.exists() && it.isFile }
        else -> File(trimmed).takeIf { it.exists() && it.isFile }
    }

    val bytes = if (localFile != null) {
        localFile.readBytes()
    } else {
        val client = tgsClient ?: OkHttpClient()
        val request = Request.Builder().url(resolveTgsUrl(trimmed)).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("TGS download failed")
            val body = response.body ?: error("Empty TGS response")
            body.bytes()
        }
    }

    return if (bytes.size >= 2 && bytes[0] == 0x1f.toByte() && bytes[1] == 0x8b.toByte()) {
        GZIPInputStream(ByteArrayInputStream(bytes)).bufferedReader().use { it.readText() }
    } else {
        bytes.toString(Charsets.UTF_8)
    }
}

internal fun initializeTgsSupport(context: android.content.Context) {
    if (tgsClient == null) {
        tgsClient = OkHttpClient.Builder()
            .cache(Cache(File(context.cacheDir, "tgs_cache"), 100 * 1024 * 1024))
            .build()
    }
    EmojiTgsManager.initialize(context)
}

@Composable
internal fun TgsSticker(
    url: String?,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    iterations: Int = LottieConstants.IterateForever,
    restartOnPlay: Boolean = false,
    autoPlay: Boolean = iterations != 1,
    playOnClick: Boolean = iterations == 1
) {
    if (iterations == LottieConstants.IterateForever && autoPlay && !playOnClick) {
        LoopingTgsSticker(url = url, modifier = modifier, tint = tint)
    } else {
        ControlledTgsSticker(
            url = url,
            modifier = modifier,
            tint = tint,
            iterations = iterations,
            restartOnPlay = restartOnPlay,
            autoPlay = autoPlay,
            playOnClick = playOnClick
        )
    }
}

@Composable
private fun LoopingTgsSticker(
    url: String?,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    var json by remember(url) { mutableStateOf<String?>(null) }
    var failed by remember(url) { mutableStateOf(false) }

    LaunchedEffect(url) {
        json = null
        failed = false
        if (url.isNullOrBlank()) {
            failed = true
            return@LaunchedEffect
        }
        runCatching { withContext(Dispatchers.IO) { loadTgsJson(url) } }
            .onSuccess { json = it }
            .onFailure { failed = true }
    }

    val composition by rememberLottieComposition(
        spec = json?.let(LottieCompositionSpec::JsonString) ?: LottieCompositionSpec.JsonString("{}")
    )
    val currentComposition = composition

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when {
            currentComposition != null -> {
                LottieAnimation(
                    composition = currentComposition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.fillMaxSize()
                )
            }
            failed -> Text(text = "TGS", color = tint, fontWeight = FontWeight.Bold)
            else -> CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = tint)
        }
    }
}

@Composable
private fun ControlledTgsSticker(
    url: String?,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    iterations: Int = 1,
    restartOnPlay: Boolean = false,
    autoPlay: Boolean = false,
    playOnClick: Boolean = true
) {
    val shouldAutoPlay = iterations != 1 || autoPlay
    var json by remember(url) { mutableStateOf<String?>(null) }
    var failed by remember(url) { mutableStateOf(false) }
    var isPlaying by remember(url) { mutableStateOf(shouldAutoPlay) }

    LaunchedEffect(url) {
        json = null
        failed = false
        if (url.isNullOrBlank()) {
            failed = true
            return@LaunchedEffect
        }
        runCatching { withContext(Dispatchers.IO) { loadTgsJson(url) } }
            .onSuccess { json = it }
            .onFailure { failed = true }
    }

    val composition by rememberLottieComposition(
        spec = json?.let(LottieCompositionSpec::JsonString) ?: LottieCompositionSpec.JsonString("{}")
    )
    val lottieProgress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = iterations,
        restartOnPlay = restartOnPlay || iterations == 1
    )
    val currentComposition = composition

    LaunchedEffect(url, shouldAutoPlay) { isPlaying = shouldAutoPlay }
    LaunchedEffect(lottieProgress, isPlaying, iterations) {
        if (isPlaying && iterations == 1 && lottieProgress >= 0.99f) isPlaying = false
    }

    val playbackModifier = if (playOnClick) {
        modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
            isPlaying = true
        }
    } else {
        modifier
    }

    Box(modifier = playbackModifier, contentAlignment = Alignment.Center) {
        when {
            currentComposition != null -> {
                LottieAnimation(
                    composition = currentComposition,
                    progress = { lottieProgress },
                    modifier = Modifier.fillMaxSize()
                )
            }
            failed -> Text(text = "TGS", color = tint, fontWeight = FontWeight.Bold)
            else -> CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = tint)
        }
    }
}
