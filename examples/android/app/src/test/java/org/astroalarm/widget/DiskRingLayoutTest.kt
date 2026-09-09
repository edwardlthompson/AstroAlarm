package org.astroalarm.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DiskRingLayoutTest {
    @Test
    fun bothOuterRingsShrinkThePie() {
        val none = DiskRingLayout.of(400, months = false, zodiac = false)
        val both = DiskRingLayout.of(400, months = true, zodiac = true)
        assertTrue(both.innerR < none.innerR - 10f)
        assertTrue(both.zodiacR > both.monthLabelR)
        assertTrue(both.zodiacR + ZodiacGlyph.radius(400) < 200f)
    }

    @Test
    fun hoursOnRimTicksInsideAlarmOnTick() {
        val rings = DiskRingLayout.of(400, months = true, zodiac = true)
        assertTrue(rings.hourTickR < rings.hourLabelR)
        assertEquals(rings.hourTickR, rings.alarmR, 0.01f)
        assertEquals(rings.hourTickR, rings.handEndR, 0.01f)
        assertTrue(rings.handStartR > rings.moonOrbitR)
        assertTrue(rings.earthR < rings.moonOrbitR)
        assertTrue(rings.moonOrbitR < rings.badgeR)
        assertTrue(rings.badgeR < rings.bodyR)
        assertTrue(rings.bodyR < rings.hourTickR)
        assertTrue(rings.handStartR < rings.handEndR)
    }

    @Test
    fun noonPointerHiddenWithoutOuterRings() {
        val none = DiskRingLayout.of(400, months = false, zodiac = false)
        val months = DiskRingLayout.of(400, months = true, zodiac = false)
        val both = DiskRingLayout.of(400, months = true, zodiac = true)
        assertEquals(0f, none.noonPointerEndR, 0.01f)
        assertEquals(months.innerR + months.monthBand, months.noonPointerEndR, 0.01f)
        assertEquals(both.zodiacR, both.noonPointerEndR, 0.01f)
    }
}
