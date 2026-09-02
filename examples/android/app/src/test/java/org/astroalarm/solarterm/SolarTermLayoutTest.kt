package org.astroalarm.solarterm

import org.astroalarm.astro.sun.SolarMath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import kotlin.math.abs

class SolarTermLayoutTest {

    @Test
    fun lichunStaysAtTwelveAndNowUpIsFrozen() {
        assertEquals(0f, SolarTermLayout.nowUpRotation(315.0), 0.01f)
        assertEquals(0f, SolarTermLayout.nowUpRotation(90.0), 0.01f)
        assertEquals(-90f, SolarTermLayout.canvasDeg(315.0), 0.01f)
    }

    @Test
    fun yushuiIsCounterClockwiseOfLichun() {
        assertEquals(-105f, SolarTermLayout.canvasDeg(330.0), 0.01f)
        assertTrue(SolarTermLayout.canvasDeg(330.0) < SolarTermLayout.canvasDeg(315.0))
    }

    @Test
    fun juneEarthIsNotForcedToTwelve() {
        val lon = 90.0
        assertEquals(0f, SolarTermLayout.nowUpRotation(lon), 0.01f)
        assertTrue(abs(SolarTermLayout.canvasDeg(lon) + 90f) > 1f)
    }

    @Test
    fun januaryIsCloserToTheSunThanJuly() {
        val peri = SolarMath.sunEarthAu(Instant.parse("2026-01-04T00:00:00Z"))
        val aph = SolarMath.sunEarthAu(Instant.parse("2026-07-04T00:00:00Z"))
        assertTrue("perihelion $peri vs aphelion $aph", peri < aph)
        assertTrue(abs(peri - 0.983) < 0.01)
        assertTrue(abs(aph - 1.017) < 0.01)
    }
}
