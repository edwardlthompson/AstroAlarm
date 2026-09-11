package org.astroalarm.astro.birth

import kotlin.math.cos
import kotlin.math.sin

/** Asc-at-left wheel: ecliptic longitude → canvas radians (y down). */
object NatalWheelLayout {
    fun angleRad(longitudeDeg: Double, ascLonDeg: Double): Double {
        val rel = NatalAspectMath.wrap360(ascLonDeg - longitudeDeg)
        return Math.toRadians(180.0 - rel)
    }

    fun xy(cx: Float, cy: Float, r: Float, longitudeDeg: Double, ascLonDeg: Double): Pair<Float, Float> {
        val a = angleRad(longitudeDeg, ascLonDeg)
        return (cx + r * cos(a).toFloat()) to (cy + r * sin(a).toFloat())
    }

    /** Fallback frame when Asc unknown: ♈ at left (0° at Asc-left position). */
    fun frameAscLon(chart: NatalChart): Double =
        chart.ascendant?.longitudeDeg ?: 0.0
}
