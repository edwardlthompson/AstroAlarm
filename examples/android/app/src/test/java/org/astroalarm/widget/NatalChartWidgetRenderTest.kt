package org.astroalarm.widget

import org.astroalarm.astro.birth.BirthChartCalculator
import org.astroalarm.astro.birth.BirthProfile
import org.astroalarm.astro.birth.NatalWheelRenderer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate
import java.time.LocalTime

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class NatalChartWidgetRenderTest {
    @Test
    fun renderBitmapForProfile() {
        val profile = BirthProfile(
            id = "w",
            label = "Widget",
            birthDate = LocalDate.of(1990, 6, 15),
            birthTime = LocalTime.of(12, 0),
            cityName = "NYC",
            lat = 40.7,
            lon = -74.0,
            zoneId = "America/New_York",
        )
        val chart = BirthChartCalculator.compute(profile)!!
        val bmp = NatalWheelRenderer.renderBitmap(200, chart, sky = null, dark = true)
        assertNotNull(bmp)
        assertEquals(200, bmp.width)
        assertEquals(200, bmp.height)
    }

    @Test
    fun emptyBitmapHasSize() {
        val bmp = NatalChartWidgetEmpty.bitmap(160, dark = true, message = "No birth profile")
        assertEquals(160, bmp.width)
        assertTrue(bmp.height == 160)
    }
}
