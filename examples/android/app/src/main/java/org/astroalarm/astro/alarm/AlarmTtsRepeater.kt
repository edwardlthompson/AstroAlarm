package org.astroalarm.astro.alarm

/** Speaks once, then again [gapMs] after each utterance until [stop]. */
class AlarmTtsRepeater(
    private val gapMs: Long = GAP_MS,
    private val scheduleAfter: (Long, () -> Unit) -> Unit,
    private val cancelScheduled: () -> Unit,
    private val speak: () -> Unit,
) {
    @Volatile
    var running: Boolean = false
        private set

    fun start() {
        running = true
        cancelScheduled()
        speak()
    }

    fun onUtteranceFinished() {
        if (!running) return
        cancelScheduled()
        scheduleAfter(gapMs) {
            if (running) speak()
        }
    }

    fun stop() {
        running = false
        cancelScheduled()
    }

    companion object {
        const val GAP_MS = 2000L
        const val UTTERANCE_ID = "astro_alarm_shout"
    }
}
