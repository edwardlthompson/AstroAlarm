package org.astroalarm.astro.sky

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.math.PI

class BodySkyTest {
    @Test
    fun nhNoonIsSouthMeridian() {
        val coords = BodySky.fromAzAlt(180.0, 70.0, 40.0)
        assertEquals(0.0, coords.haRad, 1e-6)
        assertTrue(coords.decDeg > 15.0)
        assertEquals(PI / 2.0, BodySky.ringAngle(0.0, 40.0), 1e-9)
    }

    @Test
    fun shNoonIsNorthMeridian() {
        assertEquals(-PI / 2.0, BodySky.ringAngle(0.0, -33.9), 1e-9)
        val coords = BodySky.fromAzAlt(0.0, 70.0, -34.0)
        assertEquals(0.0, coords.haRad, 0.05)
    }

    @Test
    fun diskNoonStaysAtNoonMark() {
        assertEquals(-90f, BodySky.diskAngleDeg(0.0, -90f), 1e-4f)
    }

    @Test
    fun subLongitudeAtTransitMatchesObserver() {
        assertEquals(-74.0, BodySky.subLongitude(-74.0, 0.0), 1e-6)
        assertEquals(-90.0, BodySky.subLongitude(0.0, PI / 2.0), 1e-4)
    }
}
