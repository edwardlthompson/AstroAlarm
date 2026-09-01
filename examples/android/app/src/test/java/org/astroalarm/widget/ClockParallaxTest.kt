package org.astroalarm.widget

import org.junit.Assert.assertEquals
import org.junit.Test

class ClockParallaxTest {
    @Test
    fun uprightRestHasNoVerticalShift() {
        val (x, y) = ClockParallax.fromAccelerometer(0f, 0f)
        assertEquals(0f, x, 0.01f)
        assertEquals(0f, y, 0.01f)
    }

    @Test
    fun pitchMovesRingsVertically() {
        val (_, y) = ClockParallax.fromAccelerometer(0f, -4f)
        assertEquals(8.8f, y, 0.05f)
    }
}
