package org.astroalarm.astro.sky

import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.sun.SolarCalculator
import org.astroalarm.astro.model.SolarEventType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class TonightSkyTest {
    private val nyc = AstroPlace("New York", 40.7128, -74.0060, "America/New_York")

    @Test
    fun nextIsAfterNow() {
        val zone = ZoneId.of("America/New_York")
        val date = LocalDate.of(2025, 6, 21)
        val sunrise = SolarCalculator.calculate(
            SolarEventType.Sunrise, date, nyc.latitude, nyc.longitude, zone,
        )!!
        val before = sunrise.minusSeconds(60)
        val event = TonightSky.next(nyc, before)
        assertNotNull(event)
        assertTrue(event!!.at.isAfter(before))
        assertEquals(TonightSkyEvent.Kind.Sunrise, event.kind)
    }

    @Test
    fun invalidPlaceReturnsNull() {
        val bad = AstroPlace("", 0.0, 0.0, "UTC")
        assertNull(TonightSky.next(bad))
    }
}
