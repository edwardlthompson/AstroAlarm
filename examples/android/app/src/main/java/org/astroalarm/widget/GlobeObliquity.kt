package org.astroalarm.widget

import kotlin.math.atan2
import kotlin.math.hypot

/** Extra canvas roll that nods geographic north toward/away from the Sun by |sunDec|. */
object GlobeObliquity {
    fun rollDeg(
        sunDecDeg: Double,
        pole: Triple<Double, Double, Double>,
        subsolar: Triple<Double, Double, Double>,
    ): Float {
        if (kotlin.math.abs(sunDecDeg) < 0.5) return 0f
        val poleH = hypot(pole.first, pole.second)
        val subH = hypot(subsolar.first, subsolar.second)
        if (poleH < 1e-3 || subH < 1e-3) return 0f
        val poleAz = atan2(pole.first, pole.second)
        val subAz = atan2(subsolar.first, subsolar.second)
        var delta = Math.toDegrees(subAz - poleAz)
        while (delta > 180.0) delta -= 360.0
        while (delta < -180.0) delta += 360.0
        if (kotlin.math.abs(delta) < 1e-6) return 0f
        val mag = kotlin.math.abs(sunDecDeg).coerceAtMost(26.0)
        val toward = if (delta >= 0.0) 1.0 else -1.0
        val signed = if (sunDecDeg >= 0.0) toward else -toward
        return (signed * mag).toFloat()
    }
}
