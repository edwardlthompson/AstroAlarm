package org.astroalarm.astro.alarm

import org.junit.Assert.assertEquals
import org.junit.Test

class AlarmTtsSessionTest {
    @Test
    fun utteranceFinishedSchedulesNextSpeakAfterGap() {
        val events = mutableListOf<String>()
        var scheduled: (() -> Unit)? = null
        val repeater = AlarmTtsRepeater(
            gapMs = 10L,
            scheduleAfter = { _, action -> scheduled = action },
            cancelScheduled = { scheduled = null },
            speak = { events += "speak" },
        )
        repeater.start()
        assertEquals(listOf("speak"), events)
        repeater.onUtteranceFinished()
        scheduled!!.invoke()
        assertEquals(listOf("speak", "speak"), events)
        repeater.stop()
        scheduled = null
        repeater.onUtteranceFinished()
        assertEquals(listOf("speak", "speak"), events)
    }
}
