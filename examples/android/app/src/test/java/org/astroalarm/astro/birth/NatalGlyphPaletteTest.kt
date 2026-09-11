package org.astroalarm.astro.birth

import org.astroalarm.astro.zodiac.ZodiacSign
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NatalGlyphPaletteTest {
    @Test
    fun bodyColorsAreUniqueRgb() {
        val rgbs = NatalBody.entries.map { NatalGlyphPalette.bodyRgb(it) }.toSet()
        assertEquals(NatalBody.entries.size, rgbs.size)
    }

    @Test
    fun zodiacColorsAreUnique() {
        val rgbs = ZodiacSign.entries.map { NatalGlyphPalette.zodiac(it, dark = true) }.toSet()
        assertEquals(12, rgbs.size)
    }

    @Test
    fun mcIsVioletNotCyanOrAsc() {
        assertTrue(NatalGlyphPalette.MC != NatalGlyphPalette.ASC)
        assertTrue(NatalGlyphPalette.MC != NatalGlyphPalette.rgb(0, 255, 255))
        assertTrue(NatalGlyphPalette.red(NatalGlyphPalette.MC) > 150)
        assertTrue(NatalGlyphPalette.blue(NatalGlyphPalette.MC) > 150)
    }

    @Test
    fun icIsTealDistinctFromMcAsc() {
        assertTrue(NatalGlyphPalette.IC != NatalGlyphPalette.MC)
        assertTrue(NatalGlyphPalette.IC != NatalGlyphPalette.ASC)
        assertTrue(NatalGlyphPalette.green(NatalGlyphPalette.IC) > 100)
        assertTrue(NatalGlyphPalette.blue(NatalGlyphPalette.IC) > 80)
    }

    @Test
    fun dscIsBlueOppositeAscPalette() {
        assertTrue(NatalGlyphPalette.DSC != NatalGlyphPalette.ASC)
        assertTrue(NatalGlyphPalette.DSC != NatalGlyphPalette.MC)
        assertTrue(NatalGlyphPalette.DSC != NatalGlyphPalette.IC)
        assertTrue(NatalGlyphPalette.blue(NatalGlyphPalette.DSC) > 200)
    }
}
