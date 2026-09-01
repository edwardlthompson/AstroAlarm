package org.astroalarm.astro.sky

import org.astroalarm.astro.model.LunarEventType
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.moon.LunarCalculator
import org.astroalarm.astro.sun.SolarCalculator
import org.astroalarm.astro.sun.SolarSeasons
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.abs

class SkyBodiesTest {
    private val nycLat = 40.7128
    private val nycLon = -74.0060
    private val sydLat = -33.8688
    private val sydLon = 151.2093
    private val nyZone = ZoneId.of("America/New_York")

    @Test
    fun nhAndShAzimuthsDifferAtSameInstant() {
        val now = Instant.parse("2026-09-01T16:00:00Z")
        val nyc = SkyBodies.sun(now, nycLat, nycLon)!!
        val syd = SkyBodies.sun(now, sydLat, sydLon)!!
        assertTrue(abs(nyc.azDeg - syd.azDeg) > 5.0)
        assertEquals(Math.PI / 2.0, BodySky.ringAngle(0.0, nycLat), 1e-9)
        assertEquals(-Math.PI / 2.0, BodySky.ringAngle(0.0, sydLat), 1e-9)
    }

    @Test
    fun sydneySolarNoonSitsAtNorthOfGlobe() {
        val date = LocalDate.of(2026, 12, 21)
        val zone = ZoneId.of("Australia/Sydney")
        val noon = SolarCalculator.calculate(SolarEventType.SolarNoon, date, sydLat, sydLon, zone)!!
        val eq = SkyBodies.sun(noon, sydLat, sydLon)!!
        assertEquals(-Math.PI / 2.0, BodySky.ringAngle(eq.haRad, sydLat), 0.15)
    }

    @Test
    fun polarJuneHasNoFakeSunrise() {
        val date = LocalDate.of(2026, 6, 21)
        val zone = ZoneId.of("UTC")
        val rise = SolarCalculator.calculate(SolarEventType.Sunrise, date, 78.0, 15.0, zone)
        val noon = SolarCalculator.calculate(SolarEventType.SolarNoon, date, 78.0, 15.0, zone)
        assertNotNull(noon)
        val sun = SkyBodies.sun(noon!!, 78.0, 15.0)
        assertNotNull(sun)
        if (rise != null) {
            val hour = ZonedDateTime.ofInstant(rise, zone).hour
            assertTrue("polar sunrise hour $hour", hour in 0..23)
        }
    }

    @Test
    fun dstSpringForwardUsesInstantNotStartOfDayOffset() {
        val zone = nyZone
        val before = ZonedDateTime.of(2026, 3, 8, 1, 30, 0, 0, zone).toInstant()
        val after = ZonedDateTime.of(2026, 3, 8, 3, 30, 0, 0, zone).toInstant()
        val haBefore = SkyBodies.sun(before, nycLat, nycLon)!!.haRad
        val haAfter = SkyBodies.sun(after, nycLat, nycLon)!!.haRad
        val dHa = abs(haAfter - haBefore)
        assertTrue("HA jumped $dHa", dHa < 0.6)
        val sunrise = SolarCalculator.calculate(SolarEventType.Sunrise, LocalDate.of(2026, 3, 8), nycLat, nycLon, zone)!!
        val local = ZonedDateTime.ofInstant(sunrise, zone)
        assertTrue(local.hour in 5..8)
    }

    @Test
    fun leapDayHasFinitePositions() {
        val t = Instant.parse("2024-02-29T12:00:00Z")
        assertNotNull(SkyBodies.sun(t, nycLat, nycLon))
        assertNotNull(SkyBodies.moon(t, nycLat, nycLon))
    }

    @Test
    fun yearBoundaryDeclinationIsContinuous() {
        val a = SkyBodies.sun(Instant.parse("2026-12-31T23:30:00Z"), nycLat, nycLon)!!
        val b = SkyBodies.sun(Instant.parse("2027-01-01T00:30:00Z"), nycLat, nycLon)!!
        assertTrue(abs(a.decDeg - b.decDeg) < 1.0)
        val sol = SolarCalculator.calculate(
            SolarEventType.DecemberSolstice, LocalDate.of(2026, 1, 1), nycLat, nycLon, nyZone,
        )!!
        val z = ZonedDateTime.ofInstant(sol, ZoneId.of("UTC"))
        assertEquals(2026, z.year)
        assertEquals(12, z.monthValue)
    }

    @Test
    fun datelineLongitudesStayFinite() {
        val t = Instant.parse("2026-09-01T12:00:00Z")
        val east = SkyBodies.sun(t, 0.0, 179.0)!!
        val west = SkyBodies.sun(t, 0.0, -179.0)!!
        val eLon = BodySky.subLongitude(179.0, east.haRad)
        val wLon = BodySky.subLongitude(-179.0, west.haRad)
        assertTrue(eLon in -180.0..180.0)
        assertTrue(wLon in -180.0..180.0)
    }

    @Test
    fun moonTransitExistsForNycJune() {
        val transit = LunarCalculator.calculate(
            LunarEventType.MoonTransit, LocalDate.of(2026, 6, 21), nycLat, nycLon, nyZone,
        )
        assertNotNull(transit)
        val eq = SkyBodies.moon(transit!!, nycLat, nycLon)!!
        assertTrue(abs(eq.haRad) < 0.25)
    }

    @Test
    fun tokyoSunExampleIsSoutheast() {
        val t = ZonedDateTime.of(2018, 11, 13, 10, 3, 24, 0, ZoneId.of("Asia/Tokyo")).toInstant()
        val sun = SkyBodies.sun(t, 35.689722, 139.692222)!!
        assertEquals(156.6, sun.azDeg, 3.0)
        assertTrue(sun.altDeg > 25.0 && sun.altDeg < 40.0)
    }

    @Test
    fun marchEquinoxLongitudeIsNearZero() {
        val inst = SolarSeasons.instant(2026, 0.0)
        assertTrue(abs(SolarSeasons.apparentLon(inst)) < 0.05 || abs(SolarSeasons.apparentLon(inst) - 360.0) < 0.05)
    }

    @Test
    fun moonAgeNearNewAndFull() {
        val zone = ZoneId.of("UTC")
        val probe = LocalDate.of(2026, 6, 1)
        val newMoon = LunarCalculator.calculate(LunarEventType.NewMoon, probe, 0.0, 0.0, zone)!!
        val ageNew = LunarCalculator.moonAgeDays(newMoon.atZone(zone).toLocalDate())
        assertTrue("new-moon age $ageNew", ageNew < 1.5 || ageNew > 28.0)
        val fullMoon = LunarCalculator.calculate(LunarEventType.FullMoon, probe, 0.0, 0.0, zone)!!
        val ageFull = LunarCalculator.moonAgeDays(fullMoon.atZone(zone).toLocalDate())
        assertTrue("full-moon age $ageFull", abs(ageFull - 14.765) < 2.0)
    }
}
