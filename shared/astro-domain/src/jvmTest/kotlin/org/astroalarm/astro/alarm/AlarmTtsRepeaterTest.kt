package org.astroalarm.astro.alarm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AlarmTtsRepeaterTest {
    private class FakeClock {
        var speakCount = 0
        var pending: (() -> Unit)? = null
        var lastDelayMs: Long? = null

        fun schedule(delayMs: Long, action: () -> Unit) {
            lastDelayMs = delayMs
            pending = action
        }

        fun cancel() {
            pending = null
            lastDelayMs = null
        }

        fun firePending() {
            val next = pending
            pending = null
            lastDelayMs = null
            next?.invoke()
        }
    }

    private fun repeater(clock: FakeClock) = AlarmTtsRepeater(
        scheduleAfter = clock::schedule,
        cancelScheduled = clock::cancel,
        speak = { clock.speakCount += 1 },
    )

    @Test
    fun startSpeaksImmediatelyWithoutScheduling() {
        val clock = FakeClock()
        val tts = repeater(clock)
        tts.start()
        assertTrue(tts.running)
        assertEquals(1, clock.speakCount)
        assertNull(clock.pending)
    }

    @Test
    fun utteranceDoneWaitsTwoSecondsThenSpeaksAgain() {
        val clock = FakeClock()
        val tts = repeater(clock)
        tts.start()
        tts.onUtteranceFinished()
        assertEquals(1, clock.speakCount)
        assertEquals(AlarmTtsRepeater.GAP_MS, clock.lastDelayMs)
        clock.firePending()
        assertEquals(2, clock.speakCount)
    }

    @Test
    fun stopCancelsPendingRepeat() {
        val clock = FakeClock()
        val tts = repeater(clock)
        tts.start()
        tts.onUtteranceFinished()
        tts.stop()
        assertFalse(tts.running)
        assertNull(clock.pending)
        clock.firePending()
        assertEquals(1, clock.speakCount)
    }

    @Test
    fun finishedAfterStopDoesNotReschedule() {
        val clock = FakeClock()
        val tts = repeater(clock)
        tts.start()
        tts.stop()
        tts.onUtteranceFinished()
        assertNull(clock.pending)
        assertEquals(1, clock.speakCount)
    }
}
