package org.astroalarm.astro.alarm

import org.junit.Assert.assertEquals
import org.junit.Test

class AstroAlarmSchedulerSnoozeTest {
    @Test
    fun snoozeTriggerUsesConfiguredMinutes() {
        val now = 1_700_000_000_000L
        assertEquals(now + 10 * 60_000L, AstroAlarmScheduler.snoozeTriggerEpochMs(now, 10))
        assertEquals(now + 5 * 60_000L, AstroAlarmScheduler.snoozeTriggerEpochMs(now, 5))
    }

    @Test
    fun snoozeTriggerFloorsZeroOrNegativeToOneMinute() {
        val now = 1_700_000_000_000L
        assertEquals(now + 60_000L, AstroAlarmScheduler.snoozeTriggerEpochMs(now, 0))
        assertEquals(now + 60_000L, AstroAlarmScheduler.snoozeTriggerEpochMs(now, -3))
    }
}
