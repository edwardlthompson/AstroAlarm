package org.astroalarm.share

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class StoreCopyTest {
    @Test
    fun listingMatchesProductTagline() {
        val tagline = "Wake with the sun, the moon, and your own clock."
        for (rel in listOf(
            "fastlane/metadata/android/en-US/short_description.txt",
            "metadata/en-US/short_description.txt",
            "fastlane/metadata/android/en-US/full_description.txt",
            "metadata/en-US/full_description.txt",
        )) {
            val text = meta(rel).readText()
            assertTrue(rel, text.contains("AstroAlarm") || text.contains(tagline))
            assertFalse(rel, text.contains("Golden Path Android"))
        }
        assertTrue(meta("fastlane/metadata/android/en-US/short_description.txt").readText().trim() == tagline)
        assertTrue(meta("metadata/en-US/short_description.txt").readText().trim() == tagline)
        val voice = branding("voice.md").readText()
        assertTrue(voice.contains("AstroAlarm"))
        assertFalse(voice.contains("Placeholder voice for Golden Path"))
    }

    private fun meta(rel: String): File {
        val candidates = listOf(File(rel), File("../$rel"), File("../../examples/android/$rel"))
        return candidates.first { it.isFile }
    }

    private fun branding(name: String): File {
        val candidates = listOf(
            File("branding/$name"),
            File("../../branding/$name"),
            File("../../../branding/$name"),
        )
        return candidates.first { it.isFile }
    }
}
