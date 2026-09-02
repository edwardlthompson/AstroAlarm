package org.astroalarm.ui.sol

import android.graphics.Canvas
import android.graphics.Paint
import org.astroalarm.sol.PlanetBody
import org.astroalarm.sol.PlanetKepler
import org.astroalarm.sol.wrap180
import org.astroalarm.sol.wrap360
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object SolChrome {
    fun drawSpokes(canvas: Canvas, cx: Float, cy: Float, pxPerAu: Float, now: Instant) {
        val spoke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x33FFFFFF.toInt()
            strokeWidth = 1f
        }
        PlanetBody.entries.forEach { body ->
            val st = PlanetKepler.state(body, now)
            canvas.drawLine(
                cx, cy,
                cx + (st.x * pxPerAu).toFloat(),
                cy - (st.y * pxPerAu).toFloat(),
                spoke,
            )
        }
    }

    fun drawOppositionTicks(canvas: Canvas, cx: Float, cy: Float, pxPerAu: Float, now: Instant, size: Int) {
        val tick = (size * 0.010f).coerceIn(3f, 7f)
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x88B3E5FC.toInt()
            strokeWidth = 1.4f
            strokeCap = Paint.Cap.ROUND
        }
        PlanetBody.entries.filter { !it.isInner && it != PlanetBody.EARTH }.forEach { body ->
            val nu = oppositionNu(body, now)
            val (xh, yh) = PlanetKepler.helioXy(body, now, nu)
            val x = cx + (xh * pxPerAu).toFloat()
            val y = cy - (yh * pxPerAu).toFloat()
            val ang = atan2(yh, xh)
            val dx = (cos(ang + Math.PI / 2) * tick).toFloat()
            val dy = (sin(ang + Math.PI / 2) * tick).toFloat()
            canvas.drawLine(x - dx, y + dy, x + dx, y - dy, p)
        }
    }

    fun drawScaleBar(canvas: Canvas, pxPerAu: Float, size: Int, label: String) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xCCFFFFFF.toInt()
            strokeWidth = 2f
            strokeCap = Paint.Cap.SQUARE
        }
        val x0 = size * 0.06f
        val y0 = size * 0.94f
        val w = pxPerAu.coerceAtMost(size * 0.40f)
        canvas.drawLine(x0, y0, x0 + w, y0, paint)
        canvas.drawLine(x0, y0 - 3f, x0, y0 + 3f, paint)
        canvas.drawLine(x0 + w, y0 - 3f, x0 + w, y0 + 3f, paint)
        val txt = Paint(paint).apply {
            textSize = size * 0.028f
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText(label, x0, y0 - 6f, txt)
    }

    fun oppositionNu(body: PlanetBody, now: Instant): Double {
        var bestNu = 0.0
        var best = 180.0
        for (i in 0..96) {
            val nu = i * 360.0 / 96
            val d = abs(wrap180(geoLonAt(body, now, nu) - 180.0))
            if (d < best) {
                best = d
                bestNu = nu
            }
        }
        return bestNu
    }

    fun geoLonAt(body: PlanetBody, now: Instant, nuDeg: Double): Double {
        val (px, py) = PlanetKepler.helioXy(body, now, nuDeg)
        val e = PlanetKepler.state(PlanetBody.EARTH, now)
        return wrap360(Math.toDegrees(atan2(py - e.y, px - e.x)))
    }

    fun lightMin(au: Double): Int = (au * 8.317).toInt().coerceAtLeast(0)

    fun earthApsides(now: Instant): Pair<Instant, Instant> {
        val year = now.atZone(ZoneOffset.UTC).year
        val start = LocalDate.of(year, 1, 1)
        var peri = start.atStartOfDay(ZoneOffset.UTC).toInstant()
        var aph = peri
        var minA = Double.MAX_VALUE
        var maxA = 0.0
        for (i in 0..182) {
            val t = start.plusDays(i * 2L).atStartOfDay(ZoneOffset.UTC).toInstant()
            val au = PlanetKepler.au(PlanetBody.EARTH, t)
            if (au < minA) {
                minA = au
                peri = t
            }
            if (au > maxA) {
                maxA = au
                aph = t
            }
        }
        return peri to aph
    }
}
