package org.astroalarm.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GlobeIlluminationTest {
    @Test
    fun subsolarPointIsDay() {
        assertTrue(GlobeIllumination.dayFactor(0.0, 0.0, 0.0, 0.0) > 0.9f)
    }

    @Test
    fun antipodeIsNight() {
        assertTrue(GlobeIllumination.dayFactor(0.0, 180.0, 0.0, 0.0) < 0.1f)
    }

    @Test
    fun juneNorthPoleIsDay() {
        assertTrue(GlobeIllumination.dayFactor(90.0, 0.0, 23.4, 0.0) > 0.8f)
    }

    @Test
    fun twilightIsBetween() {
        val twilight = GlobeIllumination.dayFactor(0.0, 90.0, 0.0, 0.0)
        assertTrue(twilight in 0f..1f)
        assertEquals(0.12f, GlobeIllumination.EARTHSHINE, 0.001f)
        val night = GlobeIllumination.shade(1f, 0f)
        assertEquals(GlobeIllumination.EARTHSHINE, night, 0.001f)
    }
}
