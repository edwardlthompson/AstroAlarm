package org.astroalarm.ui.sol

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.sol.PlanetBody
import org.astroalarm.sol.PlanetKepler
import org.astroalarm.widget.EarthGlobeRenderer
import java.time.Instant
import kotlin.math.hypot
import kotlin.math.max

object SolRenderer {
    fun render(
        size: Int,
        now: Instant,
        zoom: Float,
        dark: Boolean,
        textures: Map<PlanetBody, Bitmap?>,
        alarms: List<AstroAlarm> = emptyList(),
        place: AstroPlace? = null,
        scaleLabel: String = "1 AU",
        showEventTimes: Boolean = true,
    ): Bitmap {
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(if (dark) 0xFF070B16.toInt() else 0xFF0B1020.toInt())
        val cx = size / 2f
        val cy = size / 2f
        val pxPerAu = (size * 0.22f) * zoom
        SolOrbitPaths.draw(canvas, cx, cy, pxPerAu, now, size)
        canvas.drawCircle(cx, cy, max(6f, 8f * zoom), Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFD54F.toInt()
        })
        PlanetBody.entries.forEach { body ->
            val st = PlanetKepler.state(body, now)
            val x = cx + (st.x * pxPerAu).toFloat()
            val y = cy - (st.y * pxPerAu).toFloat()
            val r = max(4f, (0.018f * size * zoom / (1f + st.au.toFloat() * 0.15f)))
            EarthGlobeRenderer.drawGlobe(canvas, x, y, r, 0.0, st.helioLon, textures[body], highlightUser = false)
        }
        if (showEventTimes) SolAlarmOverlay.draw(canvas, cx, cy, pxPerAu, now, alarms, place, size)
        SolChrome.drawScaleBar(canvas, pxPerAu, size, scaleLabel)
        return bmp
    }

    fun bodyAt(x: Float, y: Float, size: Int, now: Instant, zoom: Float): PlanetBody? {
        val cx = size / 2f
        val cy = size / 2f
        val pxPerAu = (size * 0.22f) * zoom
        var best: PlanetBody? = null
        var bestD = 24.0
        PlanetBody.entries.forEach { body ->
            val st = PlanetKepler.state(body, now)
            val px = cx + (st.x * pxPerAu).toFloat()
            val py = cy - (st.y * pxPerAu).toFloat()
            val d = hypot((x - px).toDouble(), (y - py).toDouble())
            if (d < bestD) {
                bestD = d
                best = body
            }
        }
        return best
    }
}
