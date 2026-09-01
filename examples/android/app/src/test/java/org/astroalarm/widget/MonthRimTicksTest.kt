package org.astroalarm.widget

import org.astroalarm.astro.zodiac.ZodiacSign
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneOffset
import kotlin.math.abs

class MonthRimTicksTest {
    @Test
    fun twelveTwoLetterLabels() {
        assertEquals(12, MonthRimTicks.LABELS.size)
        MonthRimTicks.LABELS.forEach { assertTrue(it.length in 1..2) }
    }

    @Test
    fun januaryTickSitsInCapricorn() {
        val marks = MonthRimTicks.marks(2026, ZoneOffset.UTC, 0.0, 0f)
        assertEquals(12, marks.size)
        assertEquals("Ja", marks[0].label)
        assertEquals(ZodiacSign.Capricorn, ZodiacSign.fromEclipticLongitude(marks[0].tickDeg.toDouble()))
    }

    @Test
    fun labelsSitAtMidMonthNotOnTheTick() {
        val marks = MonthRimTicks.marks(2026, ZoneOffset.UTC, 0.0, 0f)
        marks.forEachIndexed { i, mark ->
            val nextTick = marks[(i + 1) % 12].tickDeg
            val span = ((nextTick - mark.tickDeg + 360f) % 360f)
            val fromTick = ((mark.labelDeg - mark.tickDeg + 360f) % 360f)
            assertTrue("${mark.label} fromTick=$fromTick span=$span", fromTick > 8f && fromTick < span - 8f)
            assertTrue("${mark.label}", abs(mark.labelDeg - mark.tickDeg) > 5f)
        }
    }

    @Test
    fun marchLabelWrapsAcrossAries() {
        assertEquals(355.5, MonthRimTicks.midLon(340.0, 11.0), 0.01)
        val mar = MonthRimTicks.marks(2026, ZoneOffset.UTC, 0.0, 0f).first { it.label == "Mr" }
        assertEquals(ZodiacSign.Pisces, ZodiacSign.fromEclipticLongitude(mar.labelDeg.toDouble()))
    }
}
