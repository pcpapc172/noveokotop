package ir.hienob.noveo.ui

import android.content.Context
import org.json.JSONObject

internal object EmojiTgsManager {
    private var emojiToFilename = mutableMapOf<String, String>()
    private var initialized = false

    fun initialize(context: Context) {
        if (initialized) return
        try {
            val jsonString = context.assets.open("manifest.json").bufferedReader().use { it.readText() }
            val root = JSONObject(jsonString)
            val items = root.optJSONArray("items") ?: return
            for (i in 0 until items.length()) {
                val item = items.optJSONObject(i) ?: continue
                val emoji = item.optString("emoji")
                val filename = item.optString("filename")
                if (emoji.isNotBlank() && filename.isNotBlank()) {
                    emojiToFilename[emoji] = filename
                }
            }
            initialized = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getTgsUrlForEmoji(text: String): String? {
        if (!initialized) return null
        val trimmed = text.trim()
        val filename = emojiToFilename[trimmed] ?: return null
        return "https://noveo.ir/emoji_tgs/$filename"
    }
}
