package org.astroalarm.widget

import org.astroalarm.astro.birth.BirthProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

class NatalAlignWidgetCopyTest {
    @Test
    fun emptyWithoutProfile() {
        val lines = NatalAlignWidgetCopy.lines(null, empty = "empty")
        assertEquals("empty", lines.first)
        assertEquals("", lines.second)
        assertEquals("", lines.third)
    }

    @Test
    fun withProfileReturnsTitle() {
        val profile = BirthProfile(
            id = "p",
            label = "Ada",
            birthDate = LocalDate.of(1990, 6, 15),
            birthTime = LocalTime.of(12, 0),
            cityName = "NYC",
            lat = 40.7,
            lon = -74.0,
            zoneId = "America/New_York",
        )
        val lines = NatalAlignWidgetCopy.lines(
            profile,
            now = Instant.parse("2020-01-01T00:00:00Z"),
            zone = ZoneOffset.UTC,
            empty = "empty",
        )
        assertEquals("Ada", lines.first)
        assertTrue(lines.second.isNotBlank())
        assertTrue(lines.third.isNotBlank())
    }
}
