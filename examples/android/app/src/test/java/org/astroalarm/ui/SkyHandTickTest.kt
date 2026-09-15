package org.astroalarm.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SkyHandTickTest {
    @Test
    fun crossesNoonForward() {
        assertTrue(SkyHandTick.crossed(359f, 1f, 0f))
        assertFalse(SkyHandTick.crossed(10f, 12f, 0f))
    }

    @Test
    fun highlightIs150ms() {
        assertEquals(150, YearlyHighlight.MS)
    }
}
