package org.astroalarm.tts

import org.junit.Assert.*
import org.junit.Test

class TtsCatalogTest {

    @Test
    fun testTtsVoiceClamping() {
        val voice = TtsVoice(pitch = 3.5f, minQuality = 900)
        val clamped = voice.clamp()
        assertEquals(TtsVoice.MAX_PITCH, clamped.pitch, 0.01f)
        assertEquals(TtsVoice.QUALITY_VERY_HIGH, clamped.minQuality)
    }

    @Test
    fun testTtsLocaleMenuLanguageCode() {
        assertEquals("en", TtsLocaleMenu.languageCode("en-US"))
        assertEquals("fr", TtsLocaleMenu.languageCode("fr-FR"))
        assertEquals("es", TtsLocaleMenu.languageCode("es"))
    }

    @Test
    fun testTtsLangCatalogCovers() {
        val tags = listOf("en-US", "en-GB", "es-ES")
        assertTrue(TtsLangCatalog.covers(tags, "en"))
        assertTrue(TtsLangCatalog.covers(tags, "en-US"))
        assertFalse(TtsLangCatalog.covers(tags, "de-DE"))
    }

    @Test
    fun testTtsVoicePickBest() {
        val candidates = listOf(
            TtsVoiceCandidate("en-us-x-sfg#female", "en-US", 400, 200, false),
            TtsVoiceCandidate("en-us-x-sfg#male", "en-US", 500, 150, false),
            TtsVoiceCandidate("fr-fr-x-sfg#female", "fr-FR", 500, 150, false)
        )
        val bestEn = TtsVoicePick.best(candidates, "en-US", 400)
        assertNotNull(bestEn)
        assertEquals("en-us-x-sfg#male", bestEn?.name)
    }
}
