package org.astroalarm.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SolarNoonPointerTest {
    @Test
    fun hiddenWhenBothRingsOff() {
        val rings = DiskRingLayout.of(400, months = false, zodiac = false)
        assertEquals(0f, SolarNoonPointer.endR(false, false, rings), 0.01f)
    }

    @Test
    fun monthsOnlyShorterThanBoth() {
        val months = DiskRingLayout.of(400, months = true, zodiac = false)
        val both = DiskRingLayout.of(400, months = true, zodiac = true)
        val monthEnd = SolarNoonPointer.endR(true, false, months)
        val bothEnd = SolarNoonPointer.endR(true, true, both)
        assertEquals(months.innerR + months.monthBand, monthEnd, 0.01f)
        assertEquals(both.zodiacR, bothEnd, 0.01f)
        assertTrue(bothEnd > both.innerR + both.monthBand)
    }
}
