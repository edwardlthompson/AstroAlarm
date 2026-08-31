package org.astroalarm.astro.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import dev.foss.goldenpath.R

/** Alarm-grade channel so ringing can cover the lock screen without unlocking. */
object AlarmNotificationChannel {
    const val ID = "astroalarm_alarm"
    const val LEGACY_ID = "astroalarm_channel"
    const val NOTIFICATION_ID = 8800
    const val IMPORTANCE = NotificationManager.IMPORTANCE_MAX
    const val AUDIO_USAGE = AudioAttributes.USAGE_ALARM

    fun alarmAudioAttributes(): AudioAttributes =
        AudioAttributes.Builder()
            .setUsage(AUDIO_USAGE)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
            .build()

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val channel = NotificationChannel(
            ID,
            context.getString(R.string.astro_channel_name),
            IMPORTANCE,
        ).apply {
            description = context.getString(R.string.astro_channel_desc)
            setBypassDnd(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            enableVibration(true)
            enableLights(true)
            setSound(alarmUri, alarmAudioAttributes())
        }
        nm.createNotificationChannel(channel)
        nm.deleteNotificationChannel(LEGACY_ID)
    }

    fun buildRinging(context: Context, fullScreen: PendingIntent): Notification {
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        return NotificationCompat.Builder(context, ID)
            .setSmallIcon(R.drawable.ic_brand_mark)
            .setContentTitle(context.getString(R.string.astro_alarm_ringing))
            .setContentText(context.getString(R.string.astro_alarm_tap_to_open))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setSound(alarmUri, alarmAudioAttributes())
            .setContentIntent(fullScreen)
            .setFullScreenIntent(fullScreen, true)
            .build()
    }

    fun cancel(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        nm?.cancel(NOTIFICATION_ID)
    }
}
