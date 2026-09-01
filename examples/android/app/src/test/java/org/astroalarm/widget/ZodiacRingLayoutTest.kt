package org.astroalarm.widget

import org.astroalarm.astro.zodiac.ZodiacSign
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ZodiacRingLayoutTest {
    @Test
    fun wikipediaUrlUsesAstrologyPage() {
        ZodiacSign.entries.forEach { sign ->
            val url = ZodiacRingLayout.wikipediaUrl(sign)
            assertTrue(url, url.startsWith("https://en.wikipedia.org/wiki/"))
            assertTrue(url, url.contains(sign.englishName))
            assertTrue(url, url.endsWith("_(astrology)"))
        }
        assertEquals(
            "https://en.wikipedia.org/wiki/Virgo_(astrology)",
            ZodiacRingLayout.wikipediaUrl(ZodiacSign.Virgo),
        )
    }

    @Test
    fun cuspTicksSitAtSignBoundaries() {
        val cusps = ZodiacRingLayout.cuspAngles(0.0, 0f)
        assertEquals(12, cusps.size)
        assertEquals(0f, cusps[0], 0.01f)
        assertEquals(30f, cusps[1], 0.01f)
        assertEquals(150f, cusps[ZodiacSign.Virgo.ordinal], 0.01f)
        assertEquals(-90f, ZodiacRingLayout.cuspAngles(0.0, -90f)[0], 0.01f)
    }

    @Test
    fun tapOnBubbleSelectsThatSign() {
        val hits = ZodiacRingLayout.positions(100f, 40f, 0.0, -90f, 200)
        assertEquals(12, hits.size)
        val aries = hits.first { it.sign == ZodiacSign.Aries }
        assertEquals(ZodiacSign.Aries, ZodiacRingLayout.at(hits, aries.x, aries.y))
        assertNull(ZodiacRingLayout.at(hits, 100f, 100f))
    }
}
