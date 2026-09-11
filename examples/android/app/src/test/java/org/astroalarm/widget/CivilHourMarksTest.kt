package org.astroalarm.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CivilHourMarksTest {
    @Test
    fun twentyFourHourLabels() {
        val marks = CivilHourMarks.marks(0f)
        assertEquals(24, marks.size)
        assertEquals((0..23).map { it.toString() }, marks.map { it.label })
    }

    @Test
    fun civilNoonPuts12AtTopNotBottom() {
        val atCivilNoon = CivilHourMarks.marks(nowAngle = 180f)
        assertEquals(-90f, atCivilNoon[12].deg, 1e-4f)
        assertEquals(90f, wrap(atCivilNoon[0].deg), 1e-4f)
    }

    @Test
    fun currentHourStaysAtTop() {
        assertEquals(-90f, CivilHourMarks.marks(nowAngle = 0f)[0].deg, 1e-4f)
        assertEquals(-90f, CivilHourMarks.marks(nowAngle = 270f)[18].deg, 1e-4f)
    }

    @Test
    fun clockNoonIgnoresSolarNoonAnchor() {
        val civil = CivilHourMarks.marks(nowAngle = 180f)[12].deg
        val solarNoonDeg = -88f
        assertTrue(kotlin.math.abs(civil - solarNoonDeg) > 0.5f)
        assertEquals(-90f, civil, 1e-4f)
    }

    @Test
    fun daylightHoursUseSunriseSpan() {
        assertTrue(CivilHourMarks.isDay(-90f, -90f, 90f, 12))
        assertTrue(!CivilHourMarks.isDay(-255f, -90f, 90f, 1))
        assertTrue(CivilHourMarks.marks(nowAngle = 180f, aRise = -90f, aSet = 90f)[12].day)
    }

    private fun wrap(deg: Float): Float = ((deg % 360f) + 360f) % 360f
}
