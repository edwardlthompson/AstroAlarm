package org.astroalarm.widget

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

/** Solar zenith day/night factor on a lat/lon sphere. */
object GlobeIllumination {
    const val EARTHSHINE = 0.12f

    fun dayFactor(latDeg: Double, lonDeg: Double, sunDecDeg: Double, subsolarLonDeg: Double): Float {
        val lat = Math.toRadians(latDeg)
        val dec = Math.toRadians(sunDecDeg)
        val dlon = Math.toRadians(lonDeg - subsolarLonDeg)
        val cosZ = sin(lat) * sin(dec) + cos(lat) * cos(dec) * cos(dlon)
        val zDeg = Math.toDegrees(acos(cosZ.coerceIn(-1.0, 1.0)))
        return smoothstep(96.0, 84.0, zDeg).toFloat()
    }

    fun shade(limb: Float, day: Float): Float = limb * (EARTHSHINE + (1f - EARTHSHINE) * day)

    private fun smoothstep(edge0: Double, edge1: Double, x: Double): Double {
        val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0.0, 1.0)
        return t * t * (3.0 - 2.0 * t)
    }
}
