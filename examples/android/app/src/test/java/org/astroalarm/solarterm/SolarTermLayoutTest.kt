package org.astroalarm.solarterm

import org.astroalarm.astro.sun.SolarMath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import kotlin.math.abs

class SolarTermLayoutTest {

    @Test
    fun northEclipticPoleMatchesSolAxes() {
        assertEquals(0f, SolarTermLayout.nowUpRotation(315.0), 0.01f)
        assertEquals(0f, SolarTermLayout.nowUpRotation(90.0), 0.01f)
        assertEquals(-180f, SolarTermLayout.canvasDeg(0.0), 0.01f)
        assertEquals(-270f, SolarTermLayout.canvasDeg(90.0), 0.01f)
        assertEquals(0f, SolarTermLayout.canvasDeg(180.0), 0.01f)
        assertEquals(-90f, SolarTermLayout.canvasDeg(270.0), 0.01f)
        assertEquals(-135f, SolarTermLayout.canvasDeg(315.0), 0.01f)
    }

    @Test
    fun yushuiIsCounterClockwiseOfLichun() {
        assertEquals(-150f, SolarTermLayout.canvasDeg(330.0), 0.01f)
        assertTrue(SolarTermLayout.canvasDeg(330.0) < SolarTermLayout.canvasDeg(315.0))
    }

    @Test
    fun lonFromCanvasDegInvertsCanvasDeg() {
        val lons = doubleArrayOf(0.0, 90.0, 180.0, 270.0, 315.0, 330.0)
        for (lon in lons) {
            val back = SolarTermLayout.lonFromCanvasDeg(SolarTermLayout.canvasDeg(lon).toDouble())
            assertEquals(lon, back, 1e-4)
        }
    }

    @Test
    fun canvasDegMatchesSolEarthScreenAngle() {
        val lons = doubleArrayOf(0.0, 90.0, 180.0, 270.0, 315.0)
        for (lon in lons) {
            val helio = Math.toRadians(lon + 180.0)
            val th = Math.toRadians(SolarTermLayout.canvasDeg(lon).toDouble())
            assertEquals(kotlin.math.cos(helio), kotlin.math.cos(th), 1e-4)
            assertEquals(-kotlin.math.sin(helio), kotlin.math.sin(th), 1e-4)
        }
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
