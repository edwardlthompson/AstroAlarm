package org.astroalarm.widget

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DiskLabelFitTest {
    @Test
    fun largerCanvasUsesBiggerType() {
        val small = DiskLabelFit.textSize(200, 80f, 24, "23")
        val large = DiskLabelFit.textSize(1080, 400f, 24, "23")
        assertTrue(large > small)
        assertTrue(large >= DiskLabelFit.MIN_READABLE)
        assertTrue(large <= DiskLabelFit.MAX_SIZE)
    }

    @Test
    fun evenHourFallbackWhenTwentyFourWouldOverlap() {
        assertTrue(DiskLabelFit.evenHoursOnly(80, 12f, "23"))
        assertFalse(DiskLabelFit.evenHoursOnly(1080, 400f, "23"))
    }

    @Test
    fun twelveMonthLabelsStayReadable() {
        val ts = DiskLabelFit.textSize(400, 180f, 12, "Sep")
        assertTrue(ts >= DiskLabelFit.MIN_READABLE)
    }
}
