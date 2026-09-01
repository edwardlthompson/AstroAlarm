package org.astroalarm.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import org.astroalarm.astro.zodiac.ZodiacSign
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ZodiacGlyphTest {
    @Test
    fun radiusScalesAndStaysReadable() {
        assertEquals(11f, ZodiacGlyph.radius(80), 0.01f)
        assertEquals(21f, ZodiacGlyph.radius(800), 0.01f)
        assertEquals(14f, ZodiacGlyph.radius(400), 0.01f)
    }

    @Test
    fun textSymbolsAreUnicodeWithTextPresentation() {
        val expected = listOf("♈", "♉", "♊", "♋", "♌", "♍", "♎", "♏", "♐", "♑", "♒", "♓")
        assertEquals(expected, ZodiacSign.entries.map { it.symbol })
        ZodiacSign.entries.forEach { sign ->
            val text = ZodiacGlyph.textSymbol(sign)
            assertTrue(sign.name, text.startsWith(sign.symbol))
            assertTrue(sign.name, text.endsWith("\uFE0E"))
        }
    }

    @Test
    fun bubblesStayInsideTheBitmap() {
        listOf(200, 400, 800, 1080).forEach { size ->
            val center = size / 2f
            val edge = ZodiacGlyph.ringDistance(center, size) + ZodiacGlyph.radius(size)
            assertTrue("size=$size edge=$edge", edge <= center)
        }
    }

    @Test
    fun drawAcceptsEveryUnicodeSign() {
        val bmp = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        ZodiacSign.entries.forEach { sign ->
            ZodiacGlyph.draw(canvas, 40f, 40f, sign, Color.rgb(255, 196, 48), Color.BLACK, 400)
        }
    }
}
