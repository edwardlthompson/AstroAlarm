package org.astroalarm.widget

import android.graphics.*
import kotlin.math.sqrt

object EarthGlobeRenderer {
    private var cacheKey: String? = null
    private var cacheBmp: Bitmap? = null

    fun drawGlobe(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        r: Float,
        lat: Double,
        lon: Double,
        texture: Bitmap?,
        highlightUser: Boolean = true,
    ) {
        if (r <= 2f) return
        if (texture != null) {
            val globe = rasterize(texture, r, lat, lon)
            canvas.drawBitmap(globe, cx - r, cy - r, null)
        } else {
            canvas.drawCircle(cx, cy, r, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(16, 52, 116) })
        }
        val atmo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = (r * 0.08f).coerceIn(1.5f, 3.8f)
            color = Color.argb(140, 120, 200, 255)
        }
        canvas.drawCircle(cx, cy, r, atmo)
        if (highlightUser) {
            val pinR = (r * 0.14f).coerceIn(2.5f, 6.0f)
            canvas.drawCircle(cx, cy, pinR, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(200, 255, 68, 68) })
            canvas.drawCircle(cx, cy, pinR * 0.42f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(255, 235, 59) })
        }
    }

    private fun rasterize(texture: Bitmap, r: Float, lat: Double, lon: Double): Bitmap {
        val d = (r * 2f).toInt().coerceAtLeast(4)
        val key = "${(lat * 4).toInt()}|${(lon * 4).toInt()}|$d"
        cacheBmp?.let { if (cacheKey == key && !it.isRecycled) return it }
        val tw = texture.width
        val th = texture.height
        val tex = IntArray(tw * th)
        texture.getPixels(tex, 0, tw, 0, 0, tw, th)
        val out = IntArray(d * d)
        for (py in 0 until d) {
            val y = 1.0 - (py + 0.5) / d * 2.0
            for (px in 0 until d) {
                val x = (px + 0.5) / d * 2.0 - 1.0
                val ll = SphereProjection.diskToLatLon(x, y, lat, lon) ?: continue
                var u = (ll.second + 180.0) / 360.0
                u -= kotlin.math.floor(u)
                val v = ((90.0 - ll.first) / 180.0).coerceIn(0.0, 1.0)
                val tx = (u * tw).toInt().coerceIn(0, tw - 1)
                val ty = (v * th).toInt().coerceIn(0, th - 1)
                val z = sqrt((1.0 - x * x - y * y).coerceAtLeast(0.0))
                out[py * d + px] = shade(tex[ty * tw + tx], (0.52 + 0.48 * z).toFloat())
            }
        }
        val bmp = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888)
        bmp.setPixels(out, 0, d, 0, 0, d, d)
        cacheBmp?.recycle()
        cacheBmp = bmp
        cacheKey = key
        return bmp
    }

    private fun shade(argb: Int, s: Float): Int {
        val r = ((argb shr 16 and 0xFF) * s).toInt().coerceIn(0, 255)
        val g = ((argb shr 8 and 0xFF) * s).toInt().coerceIn(0, 255)
        val b = ((argb and 0xFF) * s).toInt().coerceIn(0, 255)
        return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
    }
}
