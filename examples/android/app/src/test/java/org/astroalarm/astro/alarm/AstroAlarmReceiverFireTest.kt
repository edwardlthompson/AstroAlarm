package org.astroalarm.astro.alarm

import android.app.Application
import android.app.NotificationManager
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class AstroAlarmReceiverFireTest {
    @Test
    fun firePostsFullScreenNotificationAndStartsActivity() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val intent = Intent(AstroAlarmScheduler.ACTION_ALARM_FIRE).putExtra(
            AstroAlarmScheduler.EXTRA_ALARM_ID,
            "alarm-1",
        )
        AstroAlarmReceiver().onReceive(context, intent)
        val posted = context.getSystemService(NotificationManager::class.java)
            .activeNotifications
            .firstOrNull { it.id == AlarmNotificationChannel.NOTIFICATION_ID }
        assertNotNull(posted)
        assertNotNull(posted!!.notification.fullScreenIntent)
        val started = shadowOf(context).nextStartedActivity
        assertNotNull(started)
        assertEquals(AstroAlarmActivity::class.java.name, started!!.component?.className)
        assertEquals("alarm-1", started.getStringExtra(AstroAlarmScheduler.EXTRA_ALARM_ID))
    }
}
