package org.astroalarm.ui.solarterm

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import org.astroalarm.astro.moon.LunarCalculator
import org.astroalarm.astro.sun.SolarMath
import org.astroalarm.solarterm.SolarTermLayout
import org.astroalarm.solarterm.wrap360
import org.astroalarm.widget.EarthGlobeRenderer
import org.astroalarm.widget.LunarHub
import org.astroalarm.widget.OrbitWake
import java.time.Instant
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object SolarTermHubRenderer {
    fun draw(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        inner: Float,
        nowLon: Double,
        perihelionLon: Double,
        dark: Boolean,
        rot: Float,
        now: Instant,
        earth: Bitmap?,
        moon: Bitmap?,
        userLat: Double?,
        userLon: Double?,
    ) {
        val a = inner * 0.58f
        drawEarthWake(canvas, cx, cy, a, nowLon, perihelionLon, rot, dark, inner)
        SolarTermAxisOverlay.drawSpiral(canvas, cx, cy, inner, perihelionLon, rot, dark)
        canvas.drawCircle(cx, cy, inner * 0.12f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (dark) 0xFFFFD54F.toInt() else 0xFFFFB300.toInt()
        })
        val nuNow = wrap360(nowLon - perihelionLon)
        val earthAng = earthCanvasDeg(nowLon, rot)
        val rPx = SolarTermAxisOverlay.polar(a, nuNow)
        val rad = Math.toRadians(earthAng.toDouble()).toFloat()
        val ex = cx + rPx * cos(rad)
        val ey = cy + rPx * sin(rad)
        val er = inner * 0.10f
        val lat0 = EarthGlobeRenderer.poleLat(userLat)
        val lon0 = runCatching { SolarMath.subsolarLongitude(now) }.getOrDefault(0.0)
        val sunDec = runCatching { SolarMath.sunDeclination(now) }.getOrDefault(0.0)
        val sunward = Math.toDegrees(atan2((cy - ey).toDouble(), (cx - ex).toDouble())).toFloat()
        EarthGlobeRenderer.drawPoleGlobe(canvas, ex, ey, er, lat0, lon0, earth, userLat, userLon, sunward, sunDec, lon0, "earth")
        SolarTermPoleLabels.draw(canvas, cx, cy, inner, perihelionLon, rot, dark, ex, ey, er)
        drawMoon(canvas, inner, ex, ey, now, moon, rot, sunward)
    }

    fun earthCanvasDeg(nowLon: Double, rot: Float): Float = SolarTermLayout.canvasDeg(nowLon) + rot

    /** Offset from Earth on the heliocentric hub. Full moon is anti-sunward (same bearing as Earth from the Sun). */
    fun moonAroundEarthDeg(moonLon: Double, rot: Float): Float =
        SolarTermLayout.canvasDeg(moonLon) + 180f + rot

    private fun drawEarthWake(
        canvas: Canvas, cx: Float, cy: Float, a: Float, nowLon: Double, perihelionLon: Double,
        rot: Float, dark: Boolean, inner: Float,
    ) {
        val n = 96
        val xs = FloatArray(n + 1)
        val ys = FloatArray(n + 1)
        val ahead = DoubleArray(n + 1)
        for (i in 0..n) {
            val nu = i * 360.0 / n
            val lon = wrap360(perihelionLon + nu)
            val ang = SolarTermLayout.canvasDeg(lon) + rot
            val r = SolarTermAxisOverlay.polar(a, nu)
            val rad = Math.toRadians(ang.toDouble()).toFloat()
            xs[i] = cx + r * cos(rad)
            ys[i] = cy + r * sin(rad)
            ahead[i] = OrbitWake.wrap2pi(Math.toRadians(wrap360(lon - nowLon)))
        }
        val color = if (dark) 0xAAFFE082.toInt() else 0xAAEF6C00.toInt()
        OrbitWake.strokeChain(canvas, xs, ys, ahead, color, inner * 0.028f)
    }

    private fun drawMoon(
        canvas: Canvas, inner: Float, ex: Float, ey: Float,
        now: Instant, moon: Bitmap?, rot: Float, sunward: Float,
    ) {
        val moonLon = LunarCalculator.eclipticLon(now)
        val moonAng = moonAroundEarthDeg(moonLon, rot)
        val mr = inner * 0.20f
        LunarHub.drawCircularWake(canvas, ex, ey, mr, moonAng, 0xAAB0BEC5.toInt(), inner * 0.018f)
        LunarHub.drawMoonTopDown(canvas, ex, ey, mr, inner * 0.035f, moonAng, sunward, moon)
    }
}
