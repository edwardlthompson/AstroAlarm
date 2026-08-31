package org.astroalarm.widget

import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/** Inverse orthographic projection for an Earth-centered camera. */
object SphereProjection {
    /**
     * Maps a unit-disk pixel (x right, y up, both in [-1, 1]) to geographic
     * latitude/longitude in degrees. The observer at ([lat0Deg], [lon0Deg]) is
     * the disk center. Returns null outside the sphere.
     */
    fun diskToLatLon(x: Double, y: Double, lat0Deg: Double, lon0Deg: Double): Pair<Double, Double>? {
        val rho2 = x * x + y * y
        if (rho2 > 1.0) return null
        val z = sqrt(1.0 - rho2)
        val lat0 = Math.toRadians(lat0Deg)
        val lon0 = Math.toRadians(lon0Deg)
        if (rho2 < 1e-12) return lat0Deg to lon0Deg
        val lat = asin((z * sin(lat0) + y * cos(lat0)).coerceIn(-1.0, 1.0))
        val lon = lon0 + atan2(x, z * cos(lat0) - y * sin(lat0))
        return Math.toDegrees(lat) to Math.toDegrees(lon)
    }
}
