package org.astroalarm.sol

import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.sun.SolarMath
import java.time.Instant
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/** Topocentric rise/set from Kepler ecliptic xyz (FOSS, no extra deps). */
object PlanetHorizon {
    fun eclipticToEquatorial(x: Double, y: Double, z: Double, instant: Instant): Pair<Double, Double> {
        val t = PlanetKepler.centuries(instant)
        val eps = Math.toRadians(SolarMath.obliqCorrection(t))
        val ye = y * cos(eps) - z * sin(eps)
        val ze = y * sin(eps) + z * cos(eps)
        val ra = atan2(ye, x)
        val dec = atan2(ze, hypot(x, ye))
        return ra to dec
    }

    fun altitude(body: PlanetBody, instant: Instant, place: AstroPlace): Double? {
        if (body == PlanetBody.EARTH) return null
        val p = PlanetKepler.state(body, instant)
        val e = PlanetKepler.state(PlanetBody.EARTH, instant)
        val x = p.x - e.x
        val y = p.y - e.y
        val z = p.z - e.z
        val (ra, dec) = eclipticToEquatorial(x, y, z, instant)
        val lst = localSiderealRad(instant, place.longitude)
        val ha = lst - ra
        val lat = Math.toRadians(place.latitude)
        val sinAlt = sin(lat) * sin(dec) + cos(lat) * cos(dec) * cos(ha)
        return Math.toDegrees(asin(sinAlt.coerceIn(-1.0, 1.0)))
    }

    fun nextCrossing(body: PlanetBody, place: AstroPlace, now: Instant, rising: Boolean): Instant? {
        var prev = altitude(body, now, place) ?: return null
        var t = now
        for (i in 1..336) {
            val nextT = now.plusSeconds(i * 1800L)
            val alt = altitude(body, nextT, place) ?: continue
            val crossed = if (rising) prev < 0.0 && alt >= 0.0 else prev > 0.0 && alt <= 0.0
            if (crossed) {
                val span = alt - prev
                val frac = if (span == 0.0) 0.0 else (-prev / span)
                return t.plusSeconds(((nextT.epochSecond - t.epochSecond) * frac).toLong())
            }
            prev = alt
            t = nextT
        }
        return null
    }

    private fun hypot(a: Double, b: Double) = kotlin.math.hypot(a, b)

    private fun localSiderealRad(instant: Instant, lonDeg: Double): Double {
        val d = (instant.epochSecond - 946728000.0) / 86400.0
        val gmst = wrap360(280.16 + 360.985647366 * d)
        return Math.toRadians(wrap360(gmst + lonDeg))
    }
}
