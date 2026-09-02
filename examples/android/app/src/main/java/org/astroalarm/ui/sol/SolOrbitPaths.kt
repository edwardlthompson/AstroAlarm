package org.astroalarm.ui.sol

import android.graphics.Canvas
import android.graphics.Paint
import org.astroalarm.sol.PlanetBody
import org.astroalarm.sol.PlanetKepler
import org.astroalarm.sol.wrap360
import org.astroalarm.widget.OrbitWake
import kotlin.math.hypot

object SolOrbitPaths {
    private const val STEPS = 96

    fun draw(canvas: Canvas, cx: Float, cy: Float, pxPerAu: Float, now: java.time.Instant, size: Int) {
        SolChrome.drawSpokes(canvas, cx, cy, pxPerAu, now)
        val wMax = (size * 0.012f).coerceIn(1.6f, 4.5f)
        val peri = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x88FFE082.toInt()
            strokeWidth = 2f
            strokeCap = Paint.Cap.ROUND
        }
        PlanetBody.entries.forEach { body ->
            val st = PlanetKepler.state(body, now)
            val xs = FloatArray(STEPS + 1)
            val ys = FloatArray(STEPS + 1)
            val ahead = DoubleArray(STEPS + 1)
            var rMin = Double.MAX_VALUE
            var rMax = 0.0
            var periX = cx
            var periY = cy
            for (i in 0..STEPS) {
                val nu = i * 360.0 / STEPS
                val (xh, yh) = PlanetKepler.helioXy(body, now, nu)
                val r = hypot(xh, yh)
                if (r < rMin) {
                    rMin = r
                    periX = cx + (xh * pxPerAu).toFloat()
                    periY = cy - (yh * pxPerAu).toFloat()
                }
                if (r > rMax) rMax = r
                xs[i] = cx + (xh * pxPerAu).toFloat()
                ys[i] = cy - (yh * pxPerAu).toFloat()
                ahead[i] = OrbitWake.wrap2pi(Math.toRadians(wrap360(nu - st.nuDeg)))
            }
            OrbitWake.strokeChain(canvas, xs, ys, ahead, 0x55FFFFFF.toInt(), wMax)
            val tick = (size * 0.012f).coerceIn(3f, 8f)
            canvas.drawLine(periX - tick, periY, periX + tick, periY, peri)
            canvas.drawLine(periX, periY - tick, periX, periY + tick, peri)
        }
        val r0 = 0.42f * size
        canvas.drawLine(cx + r0 * 0.88f, cy, cx + r0, cy, peri)
        val tickTxt = Paint(peri).apply {
            textSize = size * 0.028f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("♈", cx + r0 + size * 0.028f, cy + tickTxt.textSize * 0.35f, tickTxt)
        SolChrome.drawOppositionTicks(canvas, cx, cy, pxPerAu, now, size)
    }

    fun mercurySpan(now: java.time.Instant): Double {
        var rMin = Double.MAX_VALUE
        var rMax = 0.0
        for (i in 0..STEPS) {
            val (xh, yh) = PlanetKepler.helioXy(PlanetBody.MERCURY, now, i * 360.0 / STEPS)
            val r = hypot(xh, yh)
            rMin = minOf(rMin, r)
            rMax = maxOf(rMax, r)
        }
        return rMax - rMin
    }

    fun earthSpan(now: java.time.Instant): Double {
        var rMin = Double.MAX_VALUE
        var rMax = 0.0
        for (i in 0..STEPS) {
            val (xh, yh) = PlanetKepler.helioXy(PlanetBody.EARTH, now, i * 360.0 / STEPS)
            val r = hypot(xh, yh)
            rMin = minOf(rMin, r)
            rMax = maxOf(rMax, r)
        }
        return rMax - rMin
    }
}
