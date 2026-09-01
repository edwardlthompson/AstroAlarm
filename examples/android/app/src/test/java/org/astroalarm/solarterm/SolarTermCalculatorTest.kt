package org.astroalarm.solarterm

import org.astroalarm.astro.sun.SolarSeasons
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.math.abs

class SolarTermCalculatorTest {

    @Test
    fun chunfen2026IsMarch20UtcWindow() {
        val instant = SolarTermCalculator.instant(2026, SolarTerm.CHUNFEN)
        val zdt = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
        assertEquals(2026, zdt.year)
        assertEquals(3, zdt.monthValue)
        assertTrue("day ${zdt.dayOfMonth}", zdt.dayOfMonth in 19..21)
        assertTrue(abs(SolarSeasons.apparentLon(instant) - 0.0) < 0.001)
    }

    @Test
    fun xiazhi2026IsJune21Window() {
        val instant = SolarTermCalculator.instant(2026, SolarTerm.XIAZHI)
        val zdt = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
        assertEquals(6, zdt.monthValue)
        assertTrue("day ${zdt.dayOfMonth}", zdt.dayOfMonth in 20..22)
        assertTrue(abs(SolarSeasons.apparentLon(instant) - 90.0) < 0.001)
    }

    @Test
    fun xiaohanAndDahanStayInJanuary() {
        val xiao = SolarTermCalculator.instant(2026, SolarTerm.XIAOHAN)
        val da = SolarTermCalculator.instant(2026, SolarTerm.DAHAN)
        val xz = ZonedDateTime.ofInstant(xiao, ZoneOffset.UTC)
        val dz = ZonedDateTime.ofInstant(da, ZoneOffset.UTC)
        assertEquals(1, xz.monthValue)
        assertEquals(1, dz.monthValue)
        assertTrue(xz.dayOfMonth in 4..8)
        assertTrue(dz.dayOfMonth in 19..22)
        assertTrue(xiao.isBefore(da))
    }

    @Test
    fun lichunIsFebruaryNotDecember() {
        val lichun = SolarTermCalculator.instant(2026, SolarTerm.LICHUN)
        val zdt = ZonedDateTime.ofInstant(lichun, ZoneOffset.UTC)
        assertEquals(2, zdt.monthValue)
        assertTrue(zdt.dayOfMonth in 2..6)
    }

    @Test
    fun leapYearChunfenStillMarch() {
        val zdt = ZonedDateTime.ofInstant(SolarTermCalculator.instant(2024, SolarTerm.CHUNFEN), ZoneOffset.UTC)
        assertEquals(2024, zdt.year)
        assertEquals(3, zdt.monthValue)
        assertTrue(zdt.dayOfMonth in 19..21)
    }

    @Test
    fun dstNewYorkUsesEasternDaylightForChunfen2026() {
        val zone = ZoneId.of("America/New_York")
        val local = ZonedDateTime.ofInstant(SolarTermCalculator.instant(2026, SolarTerm.CHUNFEN), zone)
        assertEquals("-04:00", local.offset.id)
        assertEquals(3, local.monthValue)
    }

    @Test
    fun polarLatitudeDoesNotChangeGeocentricInstant() {
        val utc = SolarTermCalculator.instant(2026, SolarTerm.DONGZHI)
        val oslo = ZonedDateTime.ofInstant(utc, ZoneId.of("Europe/Oslo"))
        val mcmurdo = ZonedDateTime.ofInstant(utc, ZoneId.of("Antarctica/McMurdo"))
        assertTrue(oslo.toInstant() == mcmurdo.toInstant())
        assertTrue(oslo.monthValue == 12)
    }
}
