package org.astroalarm.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dev.foss.goldenpath.R

object MoonTexture {
    @Volatile
    private var cached: Bitmap? = null

    fun get(context: Context): Bitmap? {
        cached?.let { if (!it.isRecycled) return it }
        val decoded = runCatching {
            BitmapFactory.decodeResource(context.resources, R.drawable.moon_map)
        }.getOrNull() ?: return null
        cached = decoded
        return decoded
    }
}
