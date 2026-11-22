package org.astroalarm.astro.zodiac

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.math.abs

class ZodiacCalculatorTest {

    @Test
    fun sunLongitudeProducesExpectedRanges() {
        // March 20, 2026 ~12:00 UTC (near March Equinox -> Aries 0°)
        val marchEquinox = LocalDate.of(2026, 3, 20).atTime(12, 0).toInstant(ZoneOffset.UTC)
        val marchLon = ZodiacCalculator.sunLongitudeAt(marchEquinox)
        assertTrue("March lon $marchLon should be near 0° or 360°", marchLon < 2.0 || marchLon > 358.0)

        // June 21, 2026 ~12:00 UTC (near June Solstice -> Cancer 90°)
        val juneSolstice = LocalDate.of(2026, 6, 21).atTime(12, 0).toInstant(ZoneOffset.UTC)
        val juneLon = ZodiacCalculator.sunLongitudeAt(juneSolstice)
        assertTrue("June lon $juneLon should be near 90°", abs(juneLon - 90.0) < 2.0)

        // Sept 22, 2026 ~12:00 UTC (near Sept Equinox -> Libra 180°)
        val septEquinox = LocalDate.of(2026, 9, 22).atTime(12, 0).toInstant(ZoneOffset.UTC)
        val septLon = ZodiacCalculator.sunLongitudeAt(septEquinox)
        assertTrue("Sept lon $septLon should be near 180°", abs(septLon - 180.0) < 2.0)

        // Dec 21, 2026 ~12:00 UTC (near Dec Solstice -> Capricorn 270°)
        val decSolstice = LocalDate.of(2026, 12, 21).atTime(12, 0).toInstant(ZoneOffset.UTC)
        val decLon = ZodiacCalculator.sunLongitudeAt(decSolstice)
        assertTrue("Dec lon $decLon should be near 270°", abs(decLon - 270.0) < 2.0)
    }

    @Test
    fun overheadMidnightZodiacReturnsOppositeSign() {
        // In late August (Sun in Virgo ~155°), midnight overhead should be Pisces (~335°)
        val aug = LocalDate.of(2026, 8, 30).atTime(12, 0).toInstant(ZoneOffset.UTC)
        val zodiac = ZodiacCalculator.overheadMidnightZodiac(aug)
        assertEquals(ZodiacSign.Pisces, zodiac)
    }

    @Test
    fun nextInstantCalculatesFutureDatesAccurately() {
        val now = LocalDate.of(2026, 8, 31).atTime(12, 0).toInstant(ZoneOffset.UTC)
        for (sign in ZodiacSign.entries) {
            for (point in ZodiacPoint.entries) {
                val next = ZodiacCalculator.nextInstant(sign, point, now)
                assertTrue("Next occurrence of $sign $point must be in future", next.isAfter(now))
                val targetDeg = (sign.startLongitudeDeg + point.degreeOffset) % 360.0
                val calculatedLon = ZodiacCalculator.sunLongitudeAt(next)
                var diff = abs(calculatedLon - targetDeg)
                if (diff > 180.0) diff = 360.0 - diff
                assertTrue("Longitude error for $sign $point was $diff deg", diff < 0.001)
            }
        }
    }
}
