package org.astroalarm.widget

import android.graphics.*
import kotlin.math.sqrt

object EarthGlobeRenderer {
    private var earthKey: String? = null
    private var earthBmp: Bitmap? = null
    private var moonKey: String? = null
    private var moonBmp: Bitmap? = null

    fun drawGlobe(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        r: Float,
        lat: Double,
        lon: Double,
        texture: Bitmap?,
        highlightUser: Boolean = true,
        sunDecDeg: Double? = null,
        subsolarLonDeg: Double? = null,
        body: String = "earth",
    ) {
        if (r <= 2f) return
        if (texture != null) {
            val globe = rasterize(texture, r, lat, lon, sunDecDeg, subsolarLonDeg, body)
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

    fun poleLat(userLat: Double?): Double = if ((userLat ?: 0.0) < 0.0) -90.0 else 90.0

    fun drawPoleGlobe(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        r: Float,
        lat0: Double,
        lon0: Double,
        texture: Bitmap?,
        userLat: Double?,
        userLon: Double?,
        sunwardDeg: Float,
        sunDecDeg: Double? = null,
        subsolarLonDeg: Double? = null,
        body: String = "earth",
    ) {
        if (r <= 2f) return
        val noonScreen = if (lat0 < 0.0) -90f else 90f
        canvas.save()
        canvas.rotate(sunwardDeg - noonScreen, cx, cy)
        runCatching {
            drawGlobe(canvas, cx, cy, r, lat0, lon0, texture, false, sunDecDeg, subsolarLonDeg, body)
        }.onFailure {
            canvas.drawCircle(cx, cy, r, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(16, 52, 116) })
        }
        if (userLat != null && userLon != null) {
            val (x, y, z) = SphereProjection.latLonToDisk(userLat, userLon, lat0, lon0)
            if (z >= 0.0) {
                val pinR = (r * 0.14f).coerceIn(2.0f, 5.0f)
                canvas.drawCircle(cx + x.toFloat() * r, cy - y.toFloat() * r, pinR, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(200, 255, 68, 68) })
                canvas.drawCircle(cx + x.toFloat() * r, cy - y.toFloat() * r, pinR * 0.42f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(255, 235, 59) })
            }
        }
        canvas.restore()
    }

    private fun rasterize(
        texture: Bitmap, r: Float, lat: Double, lon: Double,
        sunDecDeg: Double?, subsolarLonDeg: Double?, body: String,
    ): Bitmap {
        val d = (r * 2f).toInt().coerceAtLeast(4)
        val qDec = sunDecDeg?.div(2.0)?.toInt()?.toString() ?: "_"
        val qLon = subsolarLonDeg?.div(2.0)?.toInt()?.toString() ?: "_"
        val key = "$body|${(lat * 4).toInt()}|${(lon * 4).toInt()}|$d|$qDec|$qLon"
        cached(body, key)?.let { return it }
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
                val limb = (0.52 + 0.48 * z).toFloat()
                val day = if (sunDecDeg != null && subsolarLonDeg != null) {
                    GlobeIllumination.dayFactor(ll.first, ll.second, sunDecDeg, subsolarLonDeg)
                } else {
                    1f
                }
                out[py * d + px] = shade(tex[ty * tw + tx], GlobeIllumination.shade(limb, day))
            }
        }
        val bmp = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888)
        bmp.setPixels(out, 0, d, 0, 0, d, d)
        store(body, key, bmp)
        return bmp
    }

    private fun cached(body: String, key: String): Bitmap? {
        val slot = if (body == "moon") moonBmp to moonKey else earthBmp to earthKey
        return slot.first?.takeIf { slot.second == key && !it.isRecycled }
    }

    private fun store(body: String, key: String, bmp: Bitmap) {
        if (body == "moon") {
            moonBmp?.takeIf { it != bmp && !it.isRecycled }?.recycle()
            moonBmp = bmp
            moonKey = key
        } else {
            earthBmp?.takeIf { it != bmp && !it.isRecycled }?.recycle()
            earthBmp = bmp
            earthKey = key
        }
    }

    private fun shade(argb: Int, s: Float): Int {
        val r = ((argb shr 16 and 0xFF) * s).toInt().coerceIn(0, 255)
        val g = ((argb shr 8 and 0xFF) * s).toInt().coerceIn(0, 255)
        val b = ((argb and 0xFF) * s).toInt().coerceIn(0, 255)
        return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
    }
}
