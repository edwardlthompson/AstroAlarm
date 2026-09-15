package org.astroalarm.ui

import androidx.compose.animation.EnterTransition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UxMotionTest {
    @Test
    fun reducedMotionSkipsFabEnter() {
        assertEquals(EnterTransition.None, UxMotion.fabEnter(true))
        assertTrue(UxMotion.fabEnter(false) != EnterTransition.None)
        assertEquals(200, UxMotion.FAB_MS)
        assertEquals(180, UxMotion.ROW_MS)
        assertEquals(120, UxMotion.EMPTY_MS)
    }
}
