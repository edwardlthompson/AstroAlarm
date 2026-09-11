package org.astroalarm.astro.alarm

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import dev.foss.goldenpath.R

/** Shade Snooze/Stop actions for the ringing alarm notification. */
object AlarmNotificationActions {
    const val ACTION_SNOOZE = "org.astroalarm.ACTION_NOTIFICATION_SNOOZE"
    const val ACTION_STOP = "org.astroalarm.ACTION_NOTIFICATION_STOP"
    /** Sent so a visible lockscreen Activity stops tone and finishes. */
    const val ACTION_UI_DISMISS = "org.astroalarm.ACTION_ALARM_UI_DISMISS"

    fun stopAction(context: Context, alarmId: String): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            0,
            context.getString(R.string.astro_action_stop),
            broadcast(context, ACTION_STOP, alarmId, requestCode = 8811),
        ).build()
    }

    fun snoozeAction(context: Context, alarmId: String, snoozeMinutes: Int): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            0,
            context.getString(R.string.astro_action_snooze, snoozeMinutes.coerceAtLeast(1)),
            broadcast(context, ACTION_SNOOZE, alarmId, requestCode = 8812),
        ).build()
    }

    fun notifyUiDismiss(context: Context) {
        context.sendBroadcast(
            Intent(ACTION_UI_DISMISS).setPackage(context.packageName),
        )
    }

    private fun broadcast(
        context: Context,
        action: String,
        alarmId: String,
        requestCode: Int,
    ): PendingIntent {
        val intent = Intent(context, AstroAlarmReceiver::class.java).apply {
            this.action = action
            putExtra(AstroAlarmScheduler.EXTRA_ALARM_ID, alarmId)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, requestCode, intent, flags)
    }
}
