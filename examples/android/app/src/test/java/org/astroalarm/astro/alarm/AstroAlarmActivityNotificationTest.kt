package org.astroalarm.astro.alarm

import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class AstroAlarmActivityNotificationTest {
    @Test
    fun destroyWithoutFinishKeepsRingingNotification() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pending = android.app.PendingIntent.getActivity(
            context,
            1,
            android.content.Intent(context, AstroAlarmActivity::class.java),
            android.app.PendingIntent.FLAG_IMMUTABLE,
        )
        nm.notify(
            AlarmNotificationChannel.NOTIFICATION_ID,
            AlarmNotificationChannel.buildRinging(context, pending),
        )
        val controller = Robolectric.buildActivity(AstroAlarmActivity::class.java).setup()
        controller.destroy()
        assertTrue(
            nm.activeNotifications.any { it.id == AlarmNotificationChannel.NOTIFICATION_ID },
        )
    }
}
