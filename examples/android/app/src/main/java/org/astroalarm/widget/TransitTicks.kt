package org.astroalarm.widget

import org.astroalarm.astro.model.LunarEventType
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.moon.LunarCalculator
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.sky.BodySky
import org.astroalarm.astro.sky.SkyBodies
import org.astroalarm.astro.sun.SolarCalculator
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/** Angles on the 3D sun/moon ellipses from real hour angle. */
object TransitTicks {
    data class Marks(
        val sun: List<Double>,
        val moonHorizon: List<Double>,
        val moonMeridian: List<Double>,
    )

    fun clockAngle(instant: Instant, zone: ZoneId): Double {
        val z = ZonedDateTime.ofInstant(instant, zone)
        val fraction = (z.hour * 60 + z.minute + z.second / 60.0) / 1440.0
        return fraction * 2.0 * Math.PI - Math.PI / 2.0
    }

    fun diskAngleDeg(haRad: Double, aNoonDeg: Float): Float = BodySky.diskAngleDeg(haRad, aNoonDeg)

    fun marks(place: AstroPlace?, now: Instant): Marks {
        if (place == null) return Marks(emptyList(), emptyList(), emptyList())
        val date = now.atZone(place.zone).toLocalDate()
        val lat = place.latitude
        val lon = place.longitude
        val zone = place.zone
        val sun = listOf(
            SolarEventType.SolarMidnight,
            SolarEventType.Sunrise,
            SolarEventType.SolarNoon,
            SolarEventType.Sunset,
        ).mapNotNull { event ->
            SolarCalculator.calculate(event, date, lat, lon, zone)?.let { at ->
                SkyBodies.sun(at, lat, lon)?.let { BodySky.ringAngle(it.haRad, lat) }
            }
        }
        val moonHorizon = listOf(LunarEventType.Moonrise, LunarEventType.Moonset).mapNotNull { event ->
            LunarCalculator.calculate(event, date, lat, lon, zone)?.let { at ->
                SkyBodies.moon(at, lat, lon)?.let { BodySky.ringAngle(it.haRad, lat) }
            }
        }
        val moonMeridian = LunarCalculator.calculate(LunarEventType.MoonTransit, date, lat, lon, zone)?.let { at ->
            SkyBodies.moon(at, lat, lon)?.let { listOf(BodySky.ringAngle(it.haRad, lat)) }
        } ?: emptyList()
        return Marks(sun, moonHorizon, moonMeridian)
    }
}
