package org.astroalarm.astro.sky

import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/** Horizontal to equatorial mapping for the Earth-centered transit clock. */
data class SkyCoords(val haRad: Double, val decDeg: Double, val azDeg: Double, val altDeg: Double)

object BodySky {
    /** [azDeg] north-based clockwise; [trueAltDeg] unrefracted altitude. */
    fun fromAzAlt(azDeg: Double, trueAltDeg: Double, latDeg: Double): SkyCoords {
        val phi = Math.toRadians(latDeg)
        val alt = Math.toRadians(trueAltDeg)
        val az = Math.toRadians(azDeg)
        val sinDec = (sin(phi) * sin(alt) + cos(phi) * cos(alt) * cos(az)).coerceIn(-1.0, 1.0)
        val dec = asin(sinDec)
        val y = -cos(alt) * sin(az)
        val x = sin(alt) * cos(phi) - cos(alt) * cos(az) * sin(phi)
        val ha = atan2(y, x)
        return SkyCoords(ha, Math.toDegrees(dec), azDeg, trueAltDeg)
    }

    /** HA=0 at the geographic meridian: south/bottom in NH, north/top in SH. */
    fun ringAngle(haRad: Double, latDeg: Double): Double =
        if (latDeg >= 0.0) Math.PI / 2.0 + haRad else -Math.PI / 2.0 - haRad

    fun diskAngleDeg(haRad: Double, aNoonDeg: Float): Float =
        aNoonDeg + Math.toDegrees(haRad).toFloat()

    fun subLongitude(observerLon: Double, haRad: Double): Double {
        var lon = observerLon - Math.toDegrees(haRad)
        lon = ((lon + 180.0) % 360.0 + 360.0) % 360.0 - 180.0
        return lon
    }
}
