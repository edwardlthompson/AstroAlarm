package dev.foss.goldenpath.memory

import android.content.ComponentCallbacks2
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MemoryBudgetTest {
    @Test
    fun nullOrBlankDescriptionIsNotLimiterKill() {
        assertFalse(MemoryBudget.isLimiterKill(null))
        assertFalse(MemoryBudget.isLimiterKill(""))
        assertFalse(MemoryBudget.isLimiterKill(" "))
    }

    @Test
    fun limiterMarkersAreDetected() {
        assertTrue(MemoryBudget.isLimiterKill("MemoryLimiter:AnonSwap"))
        assertTrue(MemoryBudget.isLimiterKill("killed by AnonSwap budget"))
        assertFalse(MemoryBudget.isLimiterKill("SIGNALED"))
    }

    @Test
    fun onlyUiHiddenAndBackgroundTrim() {
        assertTrue(MemoryBudget.shouldTrimEphemeral(ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN))
        assertTrue(MemoryBudget.shouldTrimEphemeral(ComponentCallbacks2.TRIM_MEMORY_BACKGROUND))
        assertFalse(MemoryBudget.shouldTrimEphemeral(ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE))
        assertFalse(MemoryBudget.shouldTrimEphemeral(ComponentCallbacks2.TRIM_MEMORY_COMPLETE))
    }
}
