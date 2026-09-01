package org.astroalarm.ui.solarterm

import org.astroalarm.astro.place.AstroPlace
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.time.Instant

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class SolarTermWheelRenderTest {

    @Test
    fun wheelAndRingRenderSquareBitmaps() {
        val ctx = RuntimeEnvironment.getApplication()
        val place = AstroPlace("Wellington", -41.2865, 174.7762, "Pacific/Auckland")
        val now = Instant.parse("2026-06-21T00:00:00Z")
        val req = SolarTermDrawFactory.request(
            ctx.resources, place, now,
            traditional = true, localSeasons = true, dark = true, compact = true,
        ).second
        val wheel = SolarTermWheelRenderer.render(req, 96)
        val ring = SolarTerm3DRenderer.render(req, 96, yawDeg = 25f, earth = null, lat = place.latitude, lon = place.longitude)
        assertEquals(96, wheel.width)
        assertEquals(96, ring.height)
        assertNotNull(SolarTermWheelRenderer.sectorAt(48f, 12f, 96))
    }
}
