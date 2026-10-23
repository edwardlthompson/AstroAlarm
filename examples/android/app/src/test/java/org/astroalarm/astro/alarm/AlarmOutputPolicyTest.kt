package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

class AlarmOutputPolicyTest {
    @Test
    fun returnsStoredAlarmWhenPresent() {
        val stored = AstroAlarm(
            id = "a1",
            label = "Sunrise",
            target = AlarmTarget.CustomClock(6, 0),
            toneEnabled = false,
            ttsEnabled = true,
        )
        val resolved = AlarmOutputPolicy.resolveAlarm(stored, "a1", "Alarm", LocalTime.of(7, 0))
        assertEquals(stored, resolved)
        assertFalse(AlarmOutputPolicy.shouldPlayTone(resolved))
        assertTrue(AlarmOutputPolicy.shouldSpeak(resolved))
    }

    @Test
    fun missingAlarmIsMutedWithoutStealingFlags() {
        val resolved = AlarmOutputPolicy.resolveAlarm(null, "missing", "Alarm", LocalTime.of(8, 30))
        assertEquals("missing", resolved.id)
        assertEquals("Alarm", resolved.label)
        assertFalse(resolved.toneEnabled)
        assertFalse(resolved.ttsEnabled)
        assertTrue(resolved.vibrateEnabled)
        assertFalse(AlarmOutputPolicy.shouldPlayTone(resolved))
        assertFalse(AlarmOutputPolicy.shouldSpeak(resolved))
    }

    @Test
    fun blankIdBecomesUnknown() {
        val resolved = AlarmOutputPolicy.resolveAlarm(null, "", "Alarm", LocalTime.of(1, 0))
        assertEquals("unknown", resolved.id)
    }
}
