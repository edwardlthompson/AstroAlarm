package org.astroalarm.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class GpsLocateSkeletonTest {
    @Test
    fun threeMutedBars() {
        assertEquals(3, GpsLocateSkeleton.BAR_COUNT)
    }
}
