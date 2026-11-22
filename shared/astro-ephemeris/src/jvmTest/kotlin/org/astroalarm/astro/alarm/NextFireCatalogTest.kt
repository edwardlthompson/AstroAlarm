package org.astroalarm.astro.alarm

import org.astroalarm.astro.birth.BirthProfile
import org.astroalarm.astro.birth.NatalBody
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.LunarEventType
import org.astroalarm.astro.model.NatalAspect
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.sun.SolarCalculator
import org.astroalarm.astro.zodiac.ZodiacPoint
import org.astroalarm.astro.zodiac.ZodiacSign
import org.astroalarm.sol.PlanetBody
import org.astroalarm.sol.PlanetEventType
import org.astroalarm.solarterm.SolarTerm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class NextFireCatalogTest {
    private val nyc = AstroPlace("New York", 40.7128, -74.0060, "America/New_York")
    private val june = Instant.parse("2026-06-15T16:00:00Z")

    @Test
    fun eighteenSolarEventsFireAfterNow() {
        assertEquals(18, SolarEventType.entries.size)
        for (ev in SolarEventType.entries) {
            val alarm = AstroAlarm(id = ev.name, label = ev.name, target = AlarmTarget.Solar(ev))
            val next = AstroNextFire.nextInstant(alarm, nyc, june)
            assertNotNull(ev.name, next)
            assertTrue(ev.name, next!!.isAfter(june))
        }
    }

    @Test
    fun elevenLunarEventsFireAfterNow() {
        assertEquals(11, LunarEventType.entries.size)
        for (ev in LunarEventType.entries) {
            val alarm = AstroAlarm(id = ev.name, label = ev.name, target = AlarmTarget.Lunar(ev))
            val next = AstroNextFire.nextInstant(alarm, nyc, june)
            assertNotNull(ev.name, next)
            assertTrue(ev.name, next!!.isAfter(june))
        }
    }

    @Test
    fun twentyFourJieqiFireAfterNow() {
        assertEquals(24, SolarTerm.entries.size)
        for (term in SolarTerm.entries) {
            val alarm = AstroAlarm(id = term.name, label = term.name, target = AlarmTarget.SolarTerm(term))
            val next = AstroNextFire.nextInstant(alarm, nyc, june)
            assertNotNull(term.name, next)
            assertTrue(term.name, next!!.isAfter(june))
        }
    }

    @Test
    fun otherFamiliesFireAfterNow() {
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
        val cases = listOf(
            "zodiac" to AlarmTarget.Zodiac(ZodiacSign.Aries, ZodiacPoint.Beginning),
            "planet-rise" to AlarmTarget.Planet(PlanetBody.MARS, PlanetEventType.Rise),
            "mercury-retro" to AlarmTarget.Planet(PlanetBody.MERCURY, PlanetEventType.RetrogradeStart),
            "align" to AlarmTarget.PlanetAlign(PlanetBody.VENUS, PlanetBody.MARS),
            "moon-return" to AlarmTarget.MoonReturn("p"),
            "solar-return" to AlarmTarget.SolarReturn("p"),
            "natal-asc" to AlarmTarget.NatalAscAspect(NatalBody.SUN, NatalAspect.Conjunction, "p"),
        )
        for ((name, target) in cases) {
            val alarm = AstroAlarm(id = name, label = name, target = target)
            val next = AstroNextFire.nextInstant(alarm, nyc, june, birthProfiles = listOf(profile))
            assertNotNull(name, next)
            assertTrue(name, next!!.isAfter(june))
        }
    }

    @Test
    fun polarWinterSunriseIsNull() {
        val date = LocalDate.of(2026, 12, 21)
        val zone = ZoneId.of("UTC")
        assertNull(SolarCalculator.calculate(SolarEventType.Sunrise, date, 80.0, 0.0, zone))
        assertNull(SolarCalculator.calculate(SolarEventType.Sunset, date, 80.0, 0.0, zone))
    }
}
