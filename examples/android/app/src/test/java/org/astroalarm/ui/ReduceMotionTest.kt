package org.astroalarm.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReduceMotionTest {
    @Test
    fun animatorZeroIsReduced() {
        assertTrue(ReduceMotion.isReduced(0f, 1f))
        assertTrue(ReduceMotion.isReduced(1f, 0f))
        assertFalse(ReduceMotion.isReduced(1f, 1f))
    }
}
