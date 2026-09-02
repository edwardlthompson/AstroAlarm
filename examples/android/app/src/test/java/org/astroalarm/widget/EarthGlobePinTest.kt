package org.astroalarm.widget

import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.sun.SolarCalculator
import org.astroalarm.astro.sun.SolarMath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class EarthGlobePinTest {

    @Test
    fun nhNoonPinSitsOnSunwardHalf() {
        val lat = 40.7128
        val lon = -74.0060
        val noon = SolarCalculator.calculate(
            SolarEventType.SolarNoon, LocalDate.of(2026, 6, 21), lat, lon, ZoneId.of("America/New_York"),
        )!!
        val lon0 = SolarMath.subsolarLongitude(noon)
        assertEquals(lon, lon0, 20.0)
        val lat0 = EarthGlobeRenderer.poleLat(lat)
        assertEquals(90.0, lat0, 0.0)
        val (_, y, z) = SphereProjection.latLonToDisk(lat, lon, lat0, lon0)
        assertTrue(z >= 0.0)
        assertTrue("NH noon should project down toward the hub Sun", y < 0.0)
    }

    @Test
    fun shPoleCameraAndNoonUpBeforeRotate() {
        val lat = -33.8688
        val lon = 151.2093
        assertEquals(-90.0, EarthGlobeRenderer.poleLat(lat), 0.0)
        val noon = SolarCalculator.calculate(
            SolarEventType.SolarNoon, LocalDate.of(2026, 12, 21), lat, lon, ZoneId.of("Australia/Sydney"),
        )!!
        val lon0 = SolarMath.subsolarLongitude(noon)
        val (_, y, z) = SphereProjection.latLonToDisk(lat, lon, -90.0, lon0)
        assertTrue(z >= 0.0)
        assertTrue("SH noon is up before the 180° canvas rotate", y > 0.0)
    }
}
