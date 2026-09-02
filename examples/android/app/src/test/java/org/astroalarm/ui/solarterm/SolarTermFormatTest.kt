package org.astroalarm.ui.solarterm

import org.astroalarm.astro.place.AstroPlace
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.time.Instant
import java.time.ZoneOffset

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class SolarTermFormatTest {

    @Test
    fun localStampOmitsYear() {
        val stamp = SolarTermFormat.localStamp(Instant.parse("2026-03-15T12:34:00Z"), ZoneOffset.UTC)
        assertFalse(stamp.contains("2026"))
        assertFalse(Regex("""\d{4}""").containsMatchIn(stamp))
        assertTrue(stamp.contains("15"))
        assertTrue(stamp.contains("12:34"))
    }

    @Test
    fun locationLabelIsCityNameOnly() {
        val ctx = RuntimeEnvironment.getApplication()
        val place = AstroPlace("Austin", 30.27, -97.74, "America/Chicago")
        val label = SolarTermFormat.locationLabel(ctx.resources, place)
        assertEquals("Austin", label)
        assertFalse(label.contains("location", ignoreCase = true))
    }
}
