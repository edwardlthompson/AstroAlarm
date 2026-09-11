package org.astroalarm.astro.alarm

import android.content.Context
import org.astroalarm.astro.model.AstroAlarm

/** Persist + AlarmManager side effects after lockscreen Snooze or Stop. */
object AlarmDismissActions {
    fun snooze(context: Context, ringing: AstroAlarm, nowMs: Long = System.currentTimeMillis()) {
        val store = AstroAlarmStore(context)
        store.saveAll(
            AlarmFireIdentity.consumeOccurrence(
                store.getAll(),
                ringing,
                nowMs,
                disableOnce = false,
            ),
        )
        if (ringing.id.isNotBlank() && ringing.id != "unknown") {
            AstroAlarmScheduler.scheduleSnooze(
                context,
                ringing.id,
                AstroAlarmScheduler.snoozeTriggerEpochMs(nowMs, ringing.snoozeMinutes),
            )
        }
    }

    fun stop(context: Context, ringing: AstroAlarm, nowMs: Long = System.currentTimeMillis()) {
        val store = AstroAlarmStore(context)
        store.saveAll(
            AlarmFireIdentity.consumeOccurrence(store.getAll(), ringing, nowMs, disableOnce = true),
        )
        AstroAlarmScheduler.rescheduleAll(context)
    }
}
