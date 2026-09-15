package org.astroalarm.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LockscreenStopTest {
    @Test
    fun spokenIncludesLabel() {
        assertEquals("Stop sunrise", LockscreenStop.spoken("Stop", "sunrise"))
        assertEquals("Stop", LockscreenStop.spoken("Stop", "  "))
    }

    @Test
    fun reducedMotionSkipsScale() {
        assertFalse(LockscreenStop.animateScale(true))
        assertTrue(LockscreenStop.animateScale(false))
        assertEquals(80, LockscreenStop.SCALE_MS)
    }
}
