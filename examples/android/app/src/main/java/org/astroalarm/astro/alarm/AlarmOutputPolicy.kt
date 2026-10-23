package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import java.time.LocalTime

/** Pure rules for lockscreen tone / TTS when the stored alarm is missing. */
object AlarmOutputPolicy {
    fun resolveAlarm(
        stored: AstroAlarm?,
        alarmId: String,
        fallbackLabel: String,
        now: LocalTime = LocalTime.now(),
    ): AstroAlarm {
        if (stored != null) return stored
        return AstroAlarm(
            id = alarmId.ifBlank { "unknown" },
            label = fallbackLabel,
            target = AlarmTarget.CustomClock(now.hour, now.minute),
            toneEnabled = false,
            ttsEnabled = false,
            vibrateEnabled = true,
        )
    }

    fun shouldPlayTone(alarm: AstroAlarm): Boolean = alarm.toneEnabled

    fun shouldSpeak(alarm: AstroAlarm): Boolean = alarm.ttsEnabled
}
