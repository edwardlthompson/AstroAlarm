package org.astroalarm.astro.birth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

class AscendantMathTest {
    @Test
    fun equatorLstZeroGivesCapricornCuspRegion() {
        val instant = Instant.parse("2000-01-01T12:00:00Z")
        val gmst = AscendantMath.greenwichSiderealDeg(instant)
        val lon = -gmst
        val asc = AscendantMath.ascendant(instant, latDeg = 0.0, lonEastDeg = lon)
        assertNotNull(asc)
        assertEquals(270.0, asc!!.longitudeDeg, 2.0)
    }

    @Test
    fun nycReferenceAscWithinTwoDegrees() {
        val profile = BirthProfile(
            id = "nyc",
            label = "NYC",
            birthDate = LocalDate.of(1990, 6, 15),
            birthTime = LocalTime.of(14, 30),
            cityName = "New York",
            lat = 40.7128,
            lon = -74.0060,
            zoneId = "America/New_York",
        )
        val instant = BirthTimeZones.toInstant(profile)!!
        val asc = AscendantMath.ascendant(instant, profile.lat, profile.lon)!!
        assertEquals(13.45, asc.longitudeDeg, 2.0)
    }

    @Test
    fun londonReferenceAscWithinTwoDegrees() {
        val profile = BirthProfile(
            id = "lon",
            label = "London",
            birthDate = LocalDate.of(1985, 3, 20),
            birthTime = LocalTime.of(8, 0),
            cityName = "London",
            lat = 51.5074,
            lon = -0.1278,
            zoneId = "Europe/London",
        )
        val instant = BirthTimeZones.toInstant(profile)!!
        val asc = AscendantMath.ascendant(instant, profile.lat, profile.lon)!!
        assertEquals(235.63, asc.longitudeDeg, 2.5)
    }

    @Test
    fun tokyoMidnightAscFinite() {
        val profile = BirthProfile(
            id = "tyo",
            label = "Tokyo",
            birthDate = LocalDate.of(2000, 1, 1),
            birthTime = LocalTime.of(0, 0),
            cityName = "Tokyo",
            lat = 35.6762,
            lon = 139.6503,
            zoneId = "Asia/Tokyo",
        )
        val instant = BirthTimeZones.toInstant(profile)!!
        val asc = AscendantMath.ascendant(instant, profile.lat, profile.lon)!!
        assertTrue(asc.longitudeDeg in 0.0..360.0)
    }

    @Test
    fun highLatitudeStillReturnsFiniteAsc() {
        val instant = Instant.parse("2020-06-21T12:00:00Z")
        val asc = AscendantMath.ascendant(instant, latDeg = 70.0, lonEastDeg = 25.0)
        assertNotNull(asc)
        assertTrue(asc!!.longitudeDeg.isFinite())
    }

    @Test
    fun timeUnknownSkipsAscendantInChart() {
        val profile = BirthProfile(
            id = "u",
            label = "Unknown",
            birthDate = LocalDate.of(1990, 1, 1),
            timeUnknown = true,
            birthTime = null,
            cityName = "Paris",
            lat = 48.8566,
            lon = 2.3522,
            zoneId = "Europe/Paris",
        )
        assertNull(BirthChartCalculator.compute(profile)?.ascendant)
    }

    @Test
    fun midheavenFinite() {
        val mc = AscendantMath.midheaven(Instant.parse("2000-01-01T12:00:00Z"), 0.0)
        assertTrue(mc.longitudeDeg in 0.0..360.0)
    }
}
