package org.astroalarm.astro.zodiac

import org.astroalarm.astro.sun.SolarMath
import java.time.Instant
import kotlin.math.abs

object ZodiacCalculator {

    fun sunLongitudeAt(instant: Instant): Double {
        val epochSec = instant.epochSecond.toDouble()
        val jd = 2451545.0 + (epochSec - 946728000.0) / 86400.0
        val t = (jd - 2451545.0) / 36525.0
        val l0 = SolarMath.geomMeanLongSun(t)
        val m = SolarMath.geomMeanAnomSun(t)
        val c = SolarMath.sunEqOfCenter(t, m)
        var lambda = SolarMath.sunApparentLong(t, l0, c)
        while (lambda >= 360.0) lambda -= 360.0
        while (lambda < 0.0) lambda += 360.0
        return lambda
    }

    fun overheadMiddayZodiac(now: Instant): ZodiacSign {
        val sunLon = sunLongitudeAt(now)
        return ZodiacSign.fromEclipticLongitude(sunLon)
    }

    fun overheadMidnightZodiac(now: Instant): ZodiacSign {
        val sunLon = sunLongitudeAt(now)
        val midnightLon = (sunLon + 180.0) % 360.0
        return ZodiacSign.fromEclipticLongitude(midnightLon)
    }

    fun nextInstant(sign: ZodiacSign, point: ZodiacPoint, now: Instant = Instant.now()): Instant {
        var targetDeg = (sign.startLongitudeDeg + point.degreeOffset) % 360.0
        if (targetDeg < 0.0) targetDeg += 360.0

        val currentLon = sunLongitudeAt(now)
        var diff = targetDeg - currentLon
        while (diff <= 0.0001) {
            diff += 360.0
        }

        val approxSec = (diff / 0.9856473) * 86400.0
        var estimate = now.plusSeconds(approxSec.toLong())

        for (iter in 0..6) {
            val lon = sunLongitudeAt(estimate)
            var err = targetDeg - lon
            while (err > 180.0) err -= 360.0
            while (err < -180.0) err += 360.0

            if (abs(err) < 0.00001) break
            val deltaSec = (err / 0.9856473) * 86400.0
            estimate = estimate.plusSeconds(deltaSec.toLong())
        }

        return estimate
    }
}
