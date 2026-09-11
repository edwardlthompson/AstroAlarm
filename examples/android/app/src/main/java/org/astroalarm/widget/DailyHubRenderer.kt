package org.astroalarm.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import org.astroalarm.astro.moon.LunarCalculator
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.sun.SolarMath
import java.time.Instant
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/** Pole Earth + orbiting Moon at the Daily 2D center. */
object DailyHubRenderer {
    fun draw(
        canvas: Canvas,
        center: Float,
        rings: DiskRingLayout.Rings,
        now: Instant,
        place: AstroPlace?,
        sunDeg: Float,
        earth: Bitmap?,
        moon: Bitmap?,
    ) {
        val lat = place?.latitude
        val lon = place?.longitude
        val lat0 = EarthGlobeRenderer.poleLat(lat)
        val lon0 = runCatching { SolarMath.subsolarLongitude(now) }.getOrDefault(0.0)
        val sunDec = runCatching { SolarMath.sunDeclination(now) }.getOrDefault(0.0)
        val rad = Math.toRadians(sunDeg.toDouble())
        val sx = center + rings.bodyR * cos(rad).toFloat()
        val sy = center + rings.bodyR * sin(rad).toFloat()
        val sunward = Math.toDegrees(atan2((sy - center).toDouble(), (sx - center).toDouble())).toFloat()
        EarthGlobeRenderer.drawPoleGlobe(
            canvas, center, center, rings.earthR, lat0, lon0, earth, lat, lon, sunward, sunDec, lon0, "earth",
        )
        val elong = LunarCalculator.elongationDeg(now)
        val moonDeg = LunarHub.moonDeg(sunDeg, elong)
        LunarHub.drawCircularWake(
            canvas, center, center, rings.moonOrbitR, moonDeg, 0xAAB0BEC5.toInt(), rings.moonR * 0.55f,
        )
        LunarHub.drawMoonTopDown(
            canvas, center, center, rings.moonOrbitR, rings.moonR, moonDeg, sunward, moon,
        )
    }
}
