package org.astroalarm.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class OverlayToggleTest {
    @Test
    fun minHeightIs48() {
        assertEquals(48, OverlayToggle.MIN_DP)
    }
}
