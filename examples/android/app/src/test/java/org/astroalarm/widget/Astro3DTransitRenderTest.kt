package org.astroalarm.widget

import org.astroalarm.astro.place.AstroPlace
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate
import java.time.ZoneId

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class Astro3DTransitRenderTest {
    @Test
    fun threeDDiskRendersWithTransitTicks() {
        val zone = ZoneId.of("America/New_York")
        val bmp = Astro3DRenderer.render3D(
            place = AstroPlace("NYC", 40.7128, -74.0060, zone.id),
            alarms = emptyList(),
            now = LocalDate.of(2026, 6, 21).atStartOfDay(zone).toInstant(),
            size = 96,
            showZodiac = false,
        )
        assertNotNull(bmp)
        assertEquals(96, bmp.width)
        assertEquals(96, bmp.height)
    }
}
