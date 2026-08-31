package org.astroalarm.astro.alarm

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class AlarmNotificationChannelTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var nm: NotificationManager

    @Before
    fun resetChannels() {
        nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.deleteNotificationChannel(AlarmNotificationChannel.ID)
        nm.deleteNotificationChannel(AlarmNotificationChannel.LEGACY_ID)
    }

    @Test
    fun createsMaxImportanceAlarmChannel() {
        AlarmNotificationChannel.ensure(context)
        val channel = nm.getNotificationChannel(AlarmNotificationChannel.ID)
        assertNotNull(channel)
        assertEquals(AlarmNotificationChannel.IMPORTANCE, channel.importance)
        assertTrue(channel.canBypassDnd())
        assertEquals(Notification.VISIBILITY_PUBLIC, channel.lockscreenVisibility)
        assertEquals(AudioAttributes.USAGE_ALARM, channel.audioAttributes.usage)
    }

    @Test
    fun deletesLegacyGenericChannel() {
        nm.createNotificationChannel(
            android.app.NotificationChannel(
                AlarmNotificationChannel.LEGACY_ID,
                "legacy",
                NotificationManager.IMPORTANCE_HIGH,
            ),
        )
        AlarmNotificationChannel.ensure(context)
        assertNull(nm.getNotificationChannel(AlarmNotificationChannel.LEGACY_ID))
        assertNotNull(nm.getNotificationChannel(AlarmNotificationChannel.ID))
    }

    @Test
    fun ringingNotificationUsesAlarmCategoryAndFullScreen() {
        val pending = android.app.PendingIntent.getActivity(
            context,
            1,
            android.content.Intent(context, AstroAlarmActivity::class.java),
            android.app.PendingIntent.FLAG_IMMUTABLE,
        )
        val notif = AlarmNotificationChannel.buildRinging(context, pending)
        assertEquals(NotificationCompat.CATEGORY_ALARM, notif.category)
        assertEquals(Notification.VISIBILITY_PUBLIC, notif.visibility)
        assertTrue(notif.flags and Notification.FLAG_ONGOING_EVENT != 0)
        assertNotNull(notif.fullScreenIntent)
        assertEquals(AlarmNotificationChannel.ID, notif.channelId)
    }
}
