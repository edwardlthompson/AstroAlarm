package org.astroalarm.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.math.abs

class SphereProjectionTest {

    @Test
    fun diskCenterIsObserverLocation() {
        val ll = SphereProjection.diskToLatLon(0.0, 0.0, 40.7, -74.0)
        assertNotNull(ll)
        assertEquals(40.7, ll!!.first, 0.01)
        assertEquals(-74.0, ll.second, 0.01)
    }

    @Test
    fun equatorEastLimbIsNinetyDegreesEast() {
        val ll = SphereProjection.diskToLatLon(1.0, 0.0, 0.0, 0.0)
        assertNotNull(ll)
        assertEquals(0.0, ll!!.first, 0.5)
        assertEquals(90.0, ll.second, 0.5)
    }

    @Test
    fun northPoleObserverPlacesEquatorOnRim() {
        val ll = SphereProjection.diskToLatLon(0.0, -1.0, 90.0, 0.0)
        assertNotNull(ll)
        assertEquals(0.0, ll!!.first, 1.0)
    }

    @Test
    fun outsideDiskReturnsNull() {
        assertNull(SphereProjection.diskToLatLon(1.1, 0.0, 0.0, 0.0))
    }

    @Test
    fun longitudeWrapsWestOfDateline() {
        val ll = SphereProjection.diskToLatLon(-0.5, 0.0, 0.0, 170.0)
        assertNotNull(ll)
        assertEquals(0.0, ll!!.first, 1.0)
        assertEquals(true, abs(ll.second - 170.0) > 10.0)
    }
}
