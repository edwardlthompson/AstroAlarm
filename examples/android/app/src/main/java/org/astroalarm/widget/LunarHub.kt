package org.astroalarm.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import kotlin.math.cos
import kotlin.math.sin

/** Moon orbit, wake, and top-down globe around an Earth hub. */
object LunarHub {
    fun moonDeg(sunDeg: Float, elongationDeg: Double): Float = sunDeg + elongationDeg.toFloat()

    fun drawCircularWake(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        orbitR: Float,
        moonDeg: Float,
        color: Int,
        wMax: Float,
    ) {
        val n = 64
        val xs = FloatArray(n + 1)
        val ys = FloatArray(n + 1)
        val ahead = DoubleArray(n + 1)
        val moonRad = Math.toRadians(moonDeg.toDouble())
        for (i in 0..n) {
            val ang = i * 360.0 / n
            val rad = Math.toRadians(ang)
            xs[i] = cx + orbitR * cos(rad).toFloat()
            ys[i] = cy + orbitR * sin(rad).toFloat()
            ahead[i] = OrbitWake.wrap2pi(rad - moonRad)
        }
        OrbitWake.strokeChain(canvas, xs, ys, ahead, color, wMax)
    }

    fun drawMoonTopDown(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        orbitR: Float,
        moonR: Float,
        moonDeg: Float,
        sunwardDeg: Float,
        moon: Bitmap?,
    ) {
        val rad = Math.toRadians(moonDeg.toDouble())
        val mx = cx + orbitR * cos(rad).toFloat()
        val my = cy + orbitR * sin(rad).toFloat()
        val cam = MoonShade.topDown(sunwardDeg)
        EarthGlobeRenderer.drawPoleGlobe(
            canvas, mx, my, moonR, cam.lat0, cam.lon0, moon,
            null, null, cam.sunwardDeg, cam.sunDec, cam.subsolarLon, "moon",
        )
    }
}
