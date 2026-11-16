package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AlarmFireIdentityOnceTest {
    @Test
    fun stopDisablesOnceCustomClock() {
        val once = AstroAlarm(
            id = "once",
            label = "once",
            target = AlarmTarget.CustomClock(7, 0),
            daysOfWeek = emptySet(),
        )
        assertTrue(once.isOnce)
        val out = AlarmFireIdentity.consumeOccurrence(listOf(once), once, 1_700_000_000_000L, disableOnce = true)
        assertFalse(out.single().enabled)
        assertEquals(1_700_000_000_000L, out.single().lastFiredEpochMs)
    }

    @Test
    fun snoozeKeepsOnceCustomClockEnabled() {
        val once = AstroAlarm(
            id = "once",
            label = "once",
            target = AlarmTarget.CustomClock(7, 0),
            daysOfWeek = emptySet(),
        )
        val out = AlarmFireIdentity.consumeOccurrence(listOf(once), once, 1_700_000_000_000L, disableOnce = false)
        assertTrue(out.single().enabled)
        assertEquals(1_700_000_000_000L, out.single().lastFiredEpochMs)
    }
}
