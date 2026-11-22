package org.astroalarm.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
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

/**
 * Widget bitmap size in **device pixels**.
 * Old `minDp * 2.5` capped at 600 looked chunky on xxhdpi+ (`fitCenter` upscaled).
 */
object ClockRenderSize {
    const val MIN_PX = 200
    const val MAX_PX = 1024
    const val PAD_DP = 8

    fun fromWidgetDp(
        widthDp: Int,
        heightDp: Int,
        density: Float,
        paddingDp: Int = PAD_DP,
        maxWidthDp: Int = 0,
        maxHeightDp: Int = 0,
    ): Int {
        val w = maxOf(widthDp, maxWidthDp).coerceAtLeast(1)
        val h = maxOf(heightDp, maxHeightDp).coerceAtLeast(1)
        val innerDp = (minOf(w, h) - 2 * paddingDp).coerceAtLeast(1)
        val px = (innerDp * density.coerceAtLeast(1f)).toInt()
        return px.coerceIn(MIN_PX, MAX_PX)
    }

    fun fromOptions(context: Context, options: Bundle?, fallbackDp: Int = 140): Int {
        val density = context.resources.displayMetrics.density
        val minW = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, fallbackDp) ?: fallbackDp
        val minH = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, fallbackDp) ?: fallbackDp
        val maxW = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 0) ?: 0
        val maxH = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 0) ?: 0
        return fromWidgetDp(minW, minH, density, maxWidthDp = maxW, maxHeightDp = maxH)
    }

    /** Bitmaps are already device pixels; skip an extra mdpi→screen scale in ImageView. */
    fun atDevicePixels(bitmap: Bitmap): Bitmap {
        bitmap.density = Bitmap.DENSITY_NONE
        return bitmap
    }
}
