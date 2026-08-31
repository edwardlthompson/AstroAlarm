package org.astroalarm.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dev.foss.goldenpath.R

/** Cached NASA Blue Marble equirectangular map (public domain). */
object EarthTexture {
    @Volatile
    private var cached: Bitmap? = null

    fun get(context: Context): Bitmap? {
        cached?.let { if (!it.isRecycled) return it }
        val decoded = runCatching {
            BitmapFactory.decodeResource(context.resources, R.drawable.earth_daymap)
        }.getOrNull() ?: return null
        cached = decoded
        return decoded
    }
}

/** Same pixel budget the home-screen widgets use, so in-app clocks match. */
object ClockRenderSize {
    fun fromMinDp(minSideDp: Int): Int = (minSideDp * 2.5f).toInt().coerceIn(200, 600)
}
