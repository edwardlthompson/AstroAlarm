package org.astroalarm.wear

import org.junit.Assert.assertEquals
import org.junit.Test

class WearDialLayoutTest {
    @Test
    fun sweepForwardAcrossMidnight() {
        assertEquals(90f, WearDialLayout.sweepDeg(300f, 30f), 0.01f)
    }

    @Test
    fun sweepSameAngleIsZero() {
        assertEquals(0f, WearDialLayout.sweepDeg(45f, 45f), 0.01f)
    }

    @Test
    fun radiusAndHandFractions() {
        val r = WearDialLayout.radius(200f)
        assertEquals(92f, r, 0.01f)
        assertEquals(r * 0.12f, WearDialLayout.handInner(r), 0.01f)
        assertEquals(r * 0.92f, WearDialLayout.handOuter(r), 0.01f)
    }
}
