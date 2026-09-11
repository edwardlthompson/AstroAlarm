package org.astroalarm.astro.alarm

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class AlarmNotificationActionsTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var store: AstroAlarmStore
    private lateinit var nm: NotificationManager

    @Before
    fun setUp() {
        store = AstroAlarmStore(context)
        store.saveAll(emptyList())
        nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()
    }

    @Test
    fun stopActionCancelsNotificationAndKeepsAlarmEnabledForRecurring() {
        val alarm = AstroAlarm(
            id = "stop-1",
            label = "Stop me",
            target = AlarmTarget.CustomClock(7, 0),
            enabled = true,
            snoozeMinutes = 5,
        )
        store.save(alarm)
        nm.notify(
            AlarmNotificationChannel.NOTIFICATION_ID,
            AlarmNotificationChannel.buildRinging(
                context,
                android.app.PendingIntent.getActivity(
                    context,
                    1,
                    Intent(context, AstroAlarmActivity::class.java),
                    android.app.PendingIntent.FLAG_IMMUTABLE,
                ),
                alarmId = alarm.id,
            ),
        )
        AstroAlarmReceiver().onReceive(
            context,
            Intent(AlarmNotificationActions.ACTION_STOP).putExtra(
                AstroAlarmScheduler.EXTRA_ALARM_ID,
                alarm.id,
            ),
        )
        assertNull(shadowOf(nm).getNotification(AlarmNotificationChannel.NOTIFICATION_ID))
        assertEquals(true, store.getById(alarm.id)?.enabled)
    }

    @Test
    fun blankAlarmIdStillCancelsNotification() {
        nm.notify(
            AlarmNotificationChannel.NOTIFICATION_ID,
            AlarmNotificationChannel.buildRinging(
                context,
                android.app.PendingIntent.getActivity(
                    context,
                    1,
                    Intent(context, AstroAlarmActivity::class.java),
                    android.app.PendingIntent.FLAG_IMMUTABLE,
                ),
            ),
        )
        AstroAlarmReceiver().onReceive(context, Intent(AlarmNotificationActions.ACTION_SNOOZE))
        assertNull(shadowOf(nm).getNotification(AlarmNotificationChannel.NOTIFICATION_ID))
    }
}
