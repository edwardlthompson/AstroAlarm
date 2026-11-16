package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.LunarEventType
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.place.CityCatalog
import org.astroalarm.solarterm.SolarTerm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class NextFireGoldenTest {
    private fun city(name: String): AstroPlace = CityCatalog.search(name).first { it.cityName == name }

    private fun fire(target: AlarmTarget, place: AstroPlace?, now: Instant, zone: ZoneId? = null): String {
        val alarm = AstroAlarm(id = "g", label = "g", target = target)
        val next = if (zone == null) {
            AstroNextFire.nextInstant(alarm, place, now)
        } else {
            AstroNextFire.nextInstant(alarm, place, now, zone)
        }
        return next?.toString() ?: "null"
    }

    @Test
    fun citiesDstAndEdgesMatchLockedInstants() {
        val ny = city("New York")
        val london = city("London")
        val auckland = city("Auckland")
        val reykjavik = city("Reykjavik")
        val june = Instant.parse("2026-06-15T16:00:00Z")
        val nyZone = ZoneId.of("America/New_York")
        val dstSpring = ZonedDateTime.of(LocalDate.of(2026, 3, 8), LocalTime.of(1, 30), nyZone).toInstant()
        val dstFall = ZonedDateTime.of(LocalDate.of(2026, 11, 1), LocalTime.of(0, 30), nyZone).toInstant()
        val rows = listOf(
            "nyc-sunrise-june" to fire(AlarmTarget.Solar(SolarEventType.Sunrise), ny, june),
            "london-sunset-june" to fire(AlarmTarget.Solar(SolarEventType.Sunset), london, june),
            "auckland-sunrise-june" to fire(AlarmTarget.Solar(SolarEventType.Sunrise), auckland, june),
            "reykjavik-sunrise-june" to fire(AlarmTarget.Solar(SolarEventType.Sunrise), reykjavik, june),
            "nyc-fullmoon-june" to fire(AlarmTarget.Lunar(LunarEventType.FullMoon), ny, june),
            "lichun-after-june" to fire(AlarmTarget.SolarTerm(SolarTerm.LICHUN), ny, june),
            "occasion-2040" to fire(AlarmTarget.Occasion(SkyOccasionId.NAKED_EYE_2040.id), ny, june),
            "clock-dst-spring-0230" to fire(AlarmTarget.CustomClock(2, 30), ny, dstSpring, nyZone),
            "clock-dst-fall-0130" to fire(AlarmTarget.CustomClock(1, 30), ny, dstFall, nyZone),
        )
        val actual = rows.joinToString("\n") { "${it.first},${it.second}" }
        val expected = javaClass.classLoader!!.getResourceAsStream("next_fire_golden.csv")!!
            .bufferedReader().readText().trim()
        if (expected != actual) {
            System.err.println("GOLDEN_ACTUAL\n$actual")
        }
        assertEquals(expected, actual)
    }

    @Test
    fun customClockSameDayMatchesAndroidFixture() {
        val zone = ZoneId.of("America/New_York")
        val now = ZonedDateTime.of(LocalDate.of(2026, 8, 30), LocalTime.of(6, 0), zone).toInstant()
        val alarm = AstroAlarm("1", "Morning", target = AlarmTarget.CustomClock(7, 30))
        val next = AstroNextFire.nextInstant(alarm, null, now, zone)
        assertNotNull(next)
        val zdt = ZonedDateTime.ofInstant(next, zone)
        assertEquals(7, zdt.hour)
        assertEquals(30, zdt.minute)
        assertEquals(LocalDate.of(2026, 8, 30), zdt.toLocalDate())
    }

    @Test
    fun weekdayFilterSkipsSunday() {
        val zone = ZoneId.of("America/New_York")
        val now = ZonedDateTime.of(LocalDate.of(2026, 8, 30), LocalTime.of(6, 0), zone).toInstant()
        val alarm = AstroAlarm(
            id = "3",
            label = "Weekday",
            target = AlarmTarget.CustomClock(7, 0),
            daysOfWeek = setOf(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
            ),
        )
        val next = ZonedDateTime.ofInstant(AstroNextFire.nextInstant(alarm, null, now, zone)!!, zone)
        assertEquals(DayOfWeek.MONDAY, next.dayOfWeek)
        assertTrue(next.toLocalDate() == LocalDate.of(2026, 8, 31))
    }
}
