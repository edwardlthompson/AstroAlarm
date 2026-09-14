package org.astroalarm.astro.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

/** Posts the lock-screen ringing notification used by shade Stop/Snooze. */
object AlarmRingPresenter {
    fun postRinging(context: Context, alarmId: String, snoozeMinutes: Int = 5) {
        AlarmNotificationChannel.ensure(context)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        nm?.notify(
            AlarmNotificationChannel.NOTIFICATION_ID,
            AlarmNotificationChannel.buildRinging(
                context,
                lockscreenPending(context, alarmId, requestCode = 8802),
                alarmId = alarmId,
                snoozeMinutes = snoozeMinutes,
            ),
        )
    }

    fun lockscreenIntent(context: Context, alarmId: String): Intent =
        Intent(context, AstroAlarmActivity::class.java).apply {
            putExtra(AstroAlarmScheduler.EXTRA_ALARM_ID, alarmId)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS,
            )
        }

    fun lockscreenPending(context: Context, alarmId: String, requestCode: Int): PendingIntent {
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        val intent = lockscreenIntent(context, alarmId)
        val creator = AlarmLaunchOptions.creatorBundle()
        return runCatching {
            PendingIntent.getActivity(context, requestCode, intent, flags, creator)
        }.getOrElse {
            PendingIntent.getActivity(context, requestCode, intent, flags)
        }
    }
}
