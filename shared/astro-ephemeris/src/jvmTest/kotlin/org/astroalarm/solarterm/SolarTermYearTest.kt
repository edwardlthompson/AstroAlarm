package org.astroalarm.solarterm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime

class SolarTermYearTest {

    @Before
    fun clearCache() {
        SolarTermCache.clear()
    }

    @Test
    fun tropicalYearHas24TermsStartingAtLichun() {
        val year = SolarTermYear.of(2026)
        assertEquals(24, year.occurrences.size)
        assertEquals(SolarTerm.LICHUN, year.occurrences.first().term)
        assertEquals(SolarTerm.DAHAN, year.occurrences.last().term)
        val last = ZonedDateTime.ofInstant(year.occurrences.last().utc, ZoneOffset.UTC)
        assertEquals(2027, last.year)
        assertEquals(1, last.monthValue)
    }

    @Test
    fun gregorianYearPutsXiaohanFirstInJanuary() {
        val list = SolarTermCalculator.gregorianYear(2026)
        assertEquals(SolarTerm.XIAOHAN, list.first().term)
        assertEquals(1, ZonedDateTime.ofInstant(list.first().utc, ZoneOffset.UTC).monthValue)
        assertEquals(24, list.size)
    }

    @Test
    fun coveringBeforeLichunUsesPreviousTropicalYear() {
        val before = LocalDate.of(2026, 1, 15).atStartOfDay(ZoneOffset.UTC).toInstant()
        val year = SolarTermYear.covering(before, ZoneOffset.UTC)
        assertEquals(2025, year.lichunYear)
    }

    @Test
    fun nextAfterDongzhiIsXiaohan() {
        val year = SolarTermYear.of(2026)
        val dongzhi = year.occurrences.first { it.term == SolarTerm.DONGZHI }
        val next = year.nextAfter(dongzhi.utc.plusSeconds(1))
        assertEquals(SolarTerm.XIAOHAN, next.term)
    }

    @Test
    fun cacheReturnsSameInstanceUntilCleared() {
        val now = Instant.parse("2026-06-21T12:00:00Z")
        val a = SolarTermCache.yearFor(now, ZoneOffset.UTC)
        val b = SolarTermCache.yearFor(now, ZoneOffset.UTC)
        assertTrue(a === b)
        SolarTermCache.clear()
        val c = SolarTermCache.yearFor(now, ZoneOffset.UTC)
        assertTrue(a !== c)
        assertEquals(a.lichunYear, c.lichunYear)
    }

    @Test
    fun snapshotProgressIsWithinSector() {
        val now = Instant.parse("2026-03-25T00:00:00Z")
        val snap = SolarTermCache.snapshot(now, ZoneOffset.UTC)
        assertTrue(snap.progress in 0f..1f)
        assertEquals(SolarTerm.CHUNFEN, snap.current.term)
    }
}
