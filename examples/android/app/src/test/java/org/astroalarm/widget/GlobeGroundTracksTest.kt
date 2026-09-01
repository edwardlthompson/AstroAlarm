package org.astroalarm.widget

import org.astroalarm.astro.sky.BodySky
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GlobeGroundTracksTest {
    @Test
    fun solarNoonDotSitsOnObserverMeridian() {
        assertEquals(-74.0, GlobeGroundTracks.subLongitude(-74.0, 0.0), 1e-6)
    }

    @Test
    fun sixHoursAfterNoonMovesWest() {
        val lon = GlobeGroundTracks.subLongitude(0.0, Math.PI / 2.0)
        assertEquals(-90.0, lon, 1e-4)
        assertEquals(-90.0, BodySky.subLongitude(0.0, Math.PI / 2.0), 1e-4)
    }

    @Test
    fun backHemisphereHasNegativeZ() {
        val (_, _, z) = SphereProjection.latLonToDisk(0.0, 180.0, 40.0, -74.0)
        assertTrue(z < 0.0)
    }
}
