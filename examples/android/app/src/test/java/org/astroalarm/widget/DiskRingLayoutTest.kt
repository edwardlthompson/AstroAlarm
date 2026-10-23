package org.astroalarm.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiskRingLayoutTest {
    @Test
    fun bothOuterRingsShrinkThePie() {
        val none = DiskRingLayout.of(400, months = false, zodiac = false)
        val both = DiskRingLayout.of(400, months = true, zodiac = true)
        assertTrue(both.innerR < none.innerR - 10f)
        assertTrue(both.zodiacR > both.monthLabelR)
        assertTrue(both.zodiacR + ZodiacGlyph.radius(400) < 200f - DiskRingLayout.EDGE)
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

    @Test
    fun outerContentStaysInsideClipAcrossSizes() {
        val sizes = intArrayOf(160, 200, 400, 600)
        for (size in sizes) {
            val center = size / 2f
            val clipSafe = center - DiskRingLayout.EDGE
            val glyph = ZodiacGlyph.radius(size)
            val stroke = (glyph * 0.10f).coerceIn(0.8f, 1.5f)
            val textEst = (size * 0.048f).coerceIn(DiskLabelFit.MIN_READABLE, DiskLabelFit.MAX_SIZE)
            val halfW = DiskLabelFit.glyphWidth(textEst, "Sep") / 2f
            for (months in booleanArrayOf(false, true)) {
                for (zodiac in booleanArrayOf(false, true)) {
                    if (!months && !zodiac) continue
                    val rings = DiskRingLayout.of(size, months, zodiac)
                    if (zodiac) {
                        assertTrue(
                            "zodiac size=$size",
                            rings.zodiacR + glyph + stroke <= clipSafe + 0.01f,
                        )
                    }
                    if (months) {
                        assertTrue(
                            "month size=$size",
                            rings.monthLabelR + halfW <= clipSafe + 0.01f,
                        )
                    }
                }
            }
        }
    }
}
