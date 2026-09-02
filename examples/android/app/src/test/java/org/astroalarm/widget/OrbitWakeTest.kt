package org.astroalarm.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI

class OrbitWakeTest {
    @Test
    fun gapAheadHasZeroWidth() {
        assertEquals(0f, OrbitWake.width(0.01, 4f), 1e-4f)
        assertEquals(0f, OrbitWake.width(OrbitWake.GAP, 4f), 1e-4f)
    }

    @Test
    fun behindIsThickerThanOpposite() {
        val wMax = 4f
        val behind = OrbitWake.width(2.0 * PI - 0.05, wMax)
        val far = OrbitWake.width(PI, wMax)
        assertTrue("behind $behind vs far $far", behind > far)
        assertTrue(behind > wMax * 0.8f)
    }
}
