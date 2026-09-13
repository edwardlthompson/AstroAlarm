package org.astroalarm.astro.alarm

import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.AlarmClock
import dev.foss.goldenpath.R
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import java.util.UUID

class AstroAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AstroAlarmScheduler.ACTION_ALARM_FIRE -> handleAlarmFire(context, intent)
            AlarmNotificationActions.ACTION_SNOOZE -> handleNotificationSnooze(context, intent)
            AlarmNotificationActions.ACTION_STOP -> handleNotificationStop(context, intent)
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_TIME_CHANGED -> AstroAlarmScheduler.rescheduleAll(context)
            AlarmClock.ACTION_SET_ALARM -> handleActionSetAlarm(context, intent)
            AlarmClock.ACTION_DISMISS_ALARM -> handleActionDismissAlarm(context)
        }
    }

    private fun handleAlarmFire(context: Context, intent: Intent) {
        val alarmId = intent.getStringExtra(AstroAlarmScheduler.EXTRA_ALARM_ID) ?: ""
        val ringing = AstroAlarmStore(context).getById(alarmId)
        val snoozeMinutes = ringing?.snoozeMinutes ?: 5
        AlarmRingPresenter.postRinging(context, alarmId, snoozeMinutes)
        launchLockscreen(context, alarmId)
    }

    private fun launchLockscreen(context: Context, alarmId: String) {
        val activityIntent = AlarmRingPresenter.lockscreenIntent(context, alarmId)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pending = PendingIntent.getActivity(context, 8802, activityIntent, flags)
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                @Suppress("DEPRECATION")
                val opts = ActivityOptions.makeBasic().setPendingIntentBackgroundActivityStartMode(
                    ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED,
                )
                pending.send(context, 0, null, null, null, null, opts.toBundle())
            } else {
                pending.send()
            }
        }.recoverCatching {
            context.startActivity(activityIntent)
        }
    }

    private fun handleNotificationSnooze(context: Context, intent: Intent) {
        val ringing = resolveRinging(context, intent) ?: run {
            AlarmNotificationChannel.cancel(context)
            AlarmNotificationActions.notifyUiDismiss(context)
            return
        }
        AlarmDismissActions.snooze(context, ringing)
        AlarmNotificationChannel.cancel(context)
        AlarmNotificationActions.notifyUiDismiss(context)
    }

    private fun handleNotificationStop(context: Context, intent: Intent) {
        val ringing = resolveRinging(context, intent) ?: run {
            AlarmNotificationChannel.cancel(context)
            AlarmNotificationActions.notifyUiDismiss(context)
            return
        }
        AlarmDismissActions.stop(context, ringing)
        AlarmNotificationChannel.cancel(context)
        AlarmNotificationActions.notifyUiDismiss(context)
    }

    private fun resolveRinging(context: Context, intent: Intent): AstroAlarm? {
        val alarmId = intent.getStringExtra(AstroAlarmScheduler.EXTRA_ALARM_ID).orEmpty()
        if (alarmId.isBlank() || alarmId == "unknown") return null
        return AstroAlarmStore(context).getById(alarmId)
    }

    private fun handleActionSetAlarm(context: Context, intent: Intent) {
        val hour = intent.getIntExtra(AlarmClock.EXTRA_HOUR, 7)
        val minute = intent.getIntExtra(AlarmClock.EXTRA_MINUTES, 0)
        val message = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE)
            ?: context.getString(R.string.astro_custom_alarm_title)
        val alarm = AstroAlarm(
            id = UUID.randomUUID().toString(),
            label = message,
            target = AlarmTarget.CustomClock(hour, minute),
            enabled = true,
        )
        AstroAlarmStore(context).save(alarm)
        AstroAlarmScheduler.rescheduleAll(context)
    }

    private fun handleActionDismissAlarm(context: Context) {
        AlarmNotificationChannel.cancel(context)
        AstroAlarmScheduler.rescheduleAll(context)
    }
}
