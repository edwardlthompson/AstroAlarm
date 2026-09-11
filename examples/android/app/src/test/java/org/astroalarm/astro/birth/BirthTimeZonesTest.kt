package org.astroalarm.astro.birth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate
import java.time.LocalTime

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class BirthTimeZonesTest {
    @Test
    fun usEdtJuneOffsetMinusFourHours() {
        val p = BirthProfile(
            id = "1",
            label = "NY",
            birthDate = LocalDate.of(2020, 6, 15),
            birthTime = LocalTime.of(12, 0),
            cityName = "New York",
            lat = 40.7,
            lon = -74.0,
            zoneId = "America/New_York",
        )
        assertEquals(-4 * 3600, BirthTimeZones.utcOffsetSeconds(p))
    }

    @Test
    fun usEstJanuaryOffsetMinusFiveHours() {
        val p = BirthProfile(
            id = "2",
            label = "NY",
            birthDate = LocalDate.of(2020, 1, 15),
            birthTime = LocalTime.of(12, 0),
            cityName = "New York",
            lat = 40.7,
            lon = -74.0,
            zoneId = "America/New_York",
        )
        assertEquals(-5 * 3600, BirthTimeZones.utcOffsetSeconds(p))
    }

    @Test
    fun europeParis1955SummerOffsetPlusOne() {
        val p = BirthProfile(
            id = "3",
            label = "Paris",
            birthDate = LocalDate.of(1955, 7, 1),
            birthTime = LocalTime.of(12, 0),
            cityName = "Paris",
            lat = 48.85,
            lon = 2.35,
            zoneId = "Europe/Paris",
        )
        assertEquals(3600, BirthTimeZones.utcOffsetSeconds(p))
    }

    @Test
    fun asiaTokyoNoDstPlusNine() {
        val p = BirthProfile(
            id = "4",
            label = "Tokyo",
            birthDate = LocalDate.of(1980, 8, 1),
            birthTime = LocalTime.of(12, 0),
            cityName = "Tokyo",
            lat = 35.6,
            lon = 139.7,
            zoneId = "Asia/Tokyo",
        )
        assertEquals(9 * 3600, BirthTimeZones.utcOffsetSeconds(p))
    }

    @Test
    fun midnightBirthResolves() {
        val p = BirthProfile(
            id = "5",
            label = "Midnight",
            birthDate = LocalDate.of(2001, 1, 1),
            birthTime = LocalTime.MIDNIGHT,
            cityName = "UTC",
            lat = 0.0,
            lon = 0.0,
            zoneId = "UTC",
        )
        assertNotNull(BirthTimeZones.toInstant(p))
    }

    @Test
    fun jsonRoundTrip() {
        val p = BirthProfile(
            id = "abc",
            label = "Test",
            birthDate = LocalDate.of(1999, 12, 31),
            birthTime = LocalTime.of(23, 59),
            cityName = "Oslo",
            lat = 59.9,
            lon = 10.7,
            zoneId = "Europe/Oslo",
            active = true,
        )
        val raw = BirthProfileJson.listToJson(listOf(p))
        val back = BirthProfileJson.listFromJson(raw).single()
        assertEquals(p, back)
    }
}
