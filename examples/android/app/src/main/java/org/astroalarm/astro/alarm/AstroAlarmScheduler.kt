package org.astroalarm.astro.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.astroalarm.astro.place.AstroPlaceStore
import org.astroalarm.widget.NatalAlignWidgetProvider
import org.astroalarm.widget.NatalChartWidgetProvider
import org.astroalarm.widget.SolWidgetProvider
import org.astroalarm.widget.SolarTermWidgetProvider
import org.astroalarm.widget.Astro3DClockWidgetProvider
import org.astroalarm.widget.AstroClockWidgetProvider
import org.astroalarm.widget.AstroUpcomingWidgetProvider
import java.time.Instant

object AstroAlarmScheduler {
    const val ACTION_ALARM_FIRE = "org.astroalarm.ACTION_ALARM_FIRE"
    const val EXTRA_ALARM_ID = "extra_astro_alarm_id"
    private const val REQUEST_CODE_ALARM = 8801

    /** Epoch ms for a snooze re-fire (`snoozeMinutes` floored at 1). */
    fun snoozeTriggerEpochMs(nowMs: Long, snoozeMinutes: Int): Long =
        nowMs + snoozeMinutes.coerceAtLeast(1).toLong() * 60_000L

    fun rescheduleAll(context: Context) {
        val alarmStore = AstroAlarmStore(context)
        val placeStore = AstroPlaceStore(context)
        val birthStore = org.astroalarm.astro.birth.BirthProfileStore(context)
        val place = placeStore.get()
        val alarms = alarmStore.getAll().filter { it.enabled }
        val profiles = birthStore.getAll()
        val now = Instant.now()

        refreshWidgets(context)

        val nextPairs = alarms.mapNotNull { alarm ->
            val instant = AstroNextFire.nextInstant(
                alarm,
                place,
                now,
                all = alarms,
                birthProfiles = profiles,
            ) ?: return@mapNotNull null
            alarm to instant
        }
        val earliest = AlarmFireIdentity.armedPair(nextPairs)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        if (earliest == null) {
            cancelAlarm(context, alarmManager)
            return
        }

        val (alarm, instant) = earliest
        armAlarmClock(context, alarmManager, alarm.id, instant.toEpochMilli())
    }

    /**
     * Arms the single AlarmManager slot for a delay re-fire of [alarmId].
     * Replaces any prior natural or snooze arm until the next fire or [rescheduleAll].
     */
    fun scheduleSnooze(context: Context, alarmId: String, triggerAtMs: Long) {
        if (alarmId.isBlank() || alarmId == "unknown") return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        armAlarmClock(context, alarmManager, alarmId, triggerAtMs)
        refreshWidgets(context)
    }

    private fun armAlarmClock(
        context: Context,
        alarmManager: AlarmManager,
        alarmId: String,
        triggerEpochMs: Long,
    ) {
        cancelArmed(context, alarmManager)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        // Broadcast: AlarmManager delivers; receiver posts FSI + lockscreen.
        val fireIntent = Intent(context, AstroAlarmReceiver::class.java).apply {
            action = ACTION_ALARM_FIRE
            putExtra(EXTRA_ALARM_ID, alarmId)
        }
        val operation = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM, fireIntent, flags)
        val showOperation = AlarmRingPresenter.lockscreenPending(
            context,
            alarmId,
            requestCode = REQUEST_CODE_ALARM + 1,
        )
        val clockInfo = AlarmManager.AlarmClockInfo(triggerEpochMs, showOperation)

        runCatching {
            alarmManager.setAlarmClock(clockInfo, operation)
        }
    }

    private fun refreshWidgets(context: Context) {
        runCatching {
            AstroClockWidgetProvider.updateAll(context)
            AstroUpcomingWidgetProvider.updateAll(context)
            Astro3DClockWidgetProvider.updateAll(context)
            SolWidgetProvider.updateAll(context)
            SolarTermWidgetProvider.updateAll(context)
            NatalAlignWidgetProvider.updateAll(context)
            NatalChartWidgetProvider.updateAll(context)
        }
    }

    fun cancelAlarm(context: Context, alarmManager: AlarmManager? = null) {
        val am = alarmManager ?: (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager) ?: return
        cancelArmed(context, am)
        refreshWidgets(context)
    }

    private fun cancelArmed(context: Context, alarmManager: AlarmManager) {
        val flags = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        val broadcast = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_ALARM,
            Intent(context, AstroAlarmReceiver::class.java).setAction(ACTION_ALARM_FIRE),
            flags,
        )
        if (broadcast != null) {
            alarmManager.cancel(broadcast)
        }
        cancelActivityPending(context, alarmManager, PendingIntent.FLAG_IMMUTABLE)
        cancelActivityPending(context, alarmManager, PendingIntent.FLAG_MUTABLE)
    }

    private fun cancelActivityPending(context: Context, alarmManager: AlarmManager, mutability: Int) {
        val activity = PendingIntent.getActivity(
            context,
            REQUEST_CODE_ALARM,
            AlarmRingPresenter.lockscreenIntent(context, ""),
            PendingIntent.FLAG_NO_CREATE or mutability,
        )
        if (activity != null) {
            alarmManager.cancel(activity)
        }
    }
}
