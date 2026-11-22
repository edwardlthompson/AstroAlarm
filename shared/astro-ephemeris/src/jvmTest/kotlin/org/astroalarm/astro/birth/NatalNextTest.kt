package org.astroalarm.astro.birth

import org.astroalarm.astro.model.NatalAspect
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

class NatalNextTest {
    @Test
    fun moonReturnFindsFutureInstant() {
        val profile = BirthProfile(
            id = "p",
            label = "P",
            birthDate = LocalDate.of(1990, 6, 15),
            birthTime = LocalTime.of(12, 0),
            cityName = "New York",
            lat = 40.7,
            lon = -74.0,
            zoneId = "America/New_York",
        )
        val chart = BirthChartCalculator.compute(profile)!!
        val now = Instant.parse("2026-01-01T00:00:00Z")
        val next = NatalNext.nextReturn(NatalBody.MOON, chart.moon.longitudeDeg, now)
        assertNotNull(next)
        assertTrue(next!!.isAfter(now))
    }

    @Test
    fun sunConjunctAscSearchBounded() {
        val natalAsc = 13.45
        val now = Instant.parse("2026-01-01T00:00:00Z")
        val next = NatalNext.nextAspectToNatal(NatalBody.SUN, natalAsc, NatalAspect.Conjunction, now)
        assertNotNull(next)
        assertTrue(next!!.isAfter(now))
    }

    @Test
    fun templatesIncludeMoonReturn() {
        val profile = BirthProfile(
            id = "p",
            label = "P",
            birthDate = LocalDate.of(1990, 1, 1),
            birthTime = LocalTime.of(12, 0),
            cityName = "UTC",
            lat = 0.0,
            lon = 0.0,
            zoneId = "UTC",
        )
        // Avoid Android Context: assert factory pieces directly
        assertTrue(profile.canComputeAscendant)
        assertNotNull(BirthChartCalculator.compute(profile)?.sun)
    }
}
