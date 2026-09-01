package org.astroalarm.astro.sun

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/** Apparent ecliptic longitude targets: 0/90/180/270 for the four tropical seasons. */
object SolarSeasons {
    fun instant(year: Int, targetLonDeg: Double): Instant {
        val start = LocalDate.of(year, monthFor(targetLonDeg), 10).atStartOfDay(ZoneOffset.UTC).toInstant()
        val end = start.plusSeconds(20L * 86400L)
        var lo = start.epochSecond
        var hi = end.epochSecond
        repeat(40) {
            val mid = (lo + hi) / 2L
            val err = wrap180(apparentLon(Instant.ofEpochSecond(mid)) - targetLonDeg)
            if (err < 0.0) lo = mid else hi = mid
        }
        return Instant.ofEpochSecond(hi)
    }

    fun apparentLon(instant: Instant): Double {
        val t = SolarMath.julianCentury(SolarMath.julianDay(instant))
        val l0 = SolarMath.geomMeanLongSun(t)
        val m = SolarMath.geomMeanAnomSun(t)
        val c = SolarMath.sunEqOfCenter(t, m)
        var lambda = SolarMath.sunApparentLong(t, l0, c)
        while (lambda >= 360.0) lambda -= 360.0
        while (lambda < 0.0) lambda += 360.0
        return lambda
    }

    private fun monthFor(targetLonDeg: Double): Int = when {
        targetLonDeg < 45.0 -> 3
        targetLonDeg < 135.0 -> 6
        targetLonDeg < 225.0 -> 9
        else -> 12
    }

    private fun wrap180(deg: Double): Double {
        var d = deg
        while (d > 180.0) d -= 360.0
        while (d < -180.0) d += 360.0
        return d
    }
}
