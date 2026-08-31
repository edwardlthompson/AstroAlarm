package org.astroalarm.astro.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import dev.foss.goldenpath.R
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import java.util.UUID

class AstroAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AstroAlarmScheduler.ACTION_ALARM_FIRE -> handleAlarmFire(context, intent)
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_TIME_CHANGED -> AstroAlarmScheduler.rescheduleAll(context)
            AlarmClock.ACTION_SET_ALARM -> handleActionSetAlarm(context, intent)
            AlarmClock.ACTION_DISMISS_ALARM -> handleActionDismissAlarm(context)
        }
    }

    private fun handleAlarmFire(context: Context, intent: Intent) {
        val alarmId = intent.getStringExtra(AstroAlarmScheduler.EXTRA_ALARM_ID) ?: ""
        val activityIntent = Intent(context, AstroAlarmActivity::class.java).apply {
            putExtra(AstroAlarmScheduler.EXTRA_ALARM_ID, alarmId)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS,
            )
        }

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val fullScreenPending = PendingIntent.getActivity(context, 8802, activityIntent, flags)

        AlarmNotificationChannel.ensure(context)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        nm?.notify(
            AlarmNotificationChannel.NOTIFICATION_ID,
            AlarmNotificationChannel.buildRinging(context, fullScreenPending),
        )

        runCatching { context.startActivity(activityIntent) }
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
