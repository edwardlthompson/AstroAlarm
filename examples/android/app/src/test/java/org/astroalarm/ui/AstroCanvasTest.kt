package org.astroalarm.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AstroCanvasTest {
    @Test
    fun packedMatchesBrandRgb() {
        assertEquals(AstroCanvas.argb(0x1A1A2E), AstroCanvas.night)
        assertEquals(AstroCanvas.argb(0xC9A227), AstroCanvas.gold)
    }

    @Test
    fun tokensJsonHasCanvasNightGold() {
        val json = tokensFile().readText()
        assertTrue(json.contains("\"night\""))
        assertTrue(json.contains("\"gold\""))
        assertTrue(json.contains("#1a1a2e"))
        assertTrue(json.contains("#C9A227"))
    }

    private fun tokensFile(): File {
        val candidates = listOf(
            File("design-tokens/design-tokens.json"),
            File("../../design-tokens/design-tokens.json"),
            File("../../../design-tokens/design-tokens.json"),
        )
        return candidates.first { it.isFile }
    }
}
