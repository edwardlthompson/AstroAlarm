package org.astroalarm.astro.birth

import org.astroalarm.astro.sun.SolarMath
import org.astroalarm.astro.zodiac.ZodiacSign
import java.time.Instant
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

data class EclipticPoint(val longitudeDeg: Double) {
    val sign: ZodiacSign get() = ZodiacSign.fromEclipticLongitude(longitudeDeg)
    val degreeInSign: Double get() {
        var n = longitudeDeg % 360.0
        if (n < 0.0) n += 360.0
        return n % 30.0
    }
}

/**
 * Ascendant / Midheaven from LST, obliquity, and latitude (FOSS NOAA-style helpers).
 * Alarm-grade (±~2° vs Swiss Ephemeris); see ADR-0003.
 */
object AscendantMath {
    private const val DEG = Math.PI / 180.0

    fun greenwichSiderealDeg(instant: Instant): Double {
        val d = (instant.epochSecond - 946728000.0) / 86400.0 + instant.nano / 8.64e13
        var gmst = (280.16 + 360.985647366 * d) % 360.0
        if (gmst < 0.0) gmst += 360.0
        return gmst
    }

    fun localSiderealDeg(instant: Instant, lonEastDeg: Double): Double {
        var lst = greenwichSiderealDeg(instant) + lonEastDeg
        while (lst < 0.0) lst += 360.0
        while (lst >= 360.0) lst -= 360.0
        return lst
    }

    fun obliquityDeg(instant: Instant): Double {
        val t = SolarMath.julianCentury(SolarMath.julianDay(instant))
        return SolarMath.obliqCorrection(t)
    }

    fun ascendant(instant: Instant, latDeg: Double, lonEastDeg: Double): EclipticPoint? {
        if (latDeg !in -90.0..90.0) return null
        val theta = localSiderealDeg(instant, lonEastDeg) * DEG
        val eps = obliquityDeg(instant) * DEG
        val phi = latDeg * DEG
        val y = -cos(theta)
        val x = sin(theta) * cos(eps) + tan(phi) * sin(eps)
        if (!x.isFinite() || !y.isFinite()) return null
        var lam = Math.toDegrees(atan2(y, x))
        while (lam < 0.0) lam += 360.0
        while (lam >= 360.0) lam -= 360.0
        return EclipticPoint(lam)
    }

    fun midheaven(instant: Instant, lonEastDeg: Double): EclipticPoint {
        val ramc = localSiderealDeg(instant, lonEastDeg) * DEG
        val eps = obliquityDeg(instant) * DEG
        var lam = Math.toDegrees(atan2(sin(ramc), cos(ramc) * cos(eps)))
        while (lam < 0.0) lam += 360.0
        while (lam >= 360.0) lam -= 360.0
        return EclipticPoint(lam)
    }
}
