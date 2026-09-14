package org.astroalarm.astro.alarm

import android.app.AlarmManager
import android.app.Application
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AstroAlarmSchedulerArmTest {
    @Test
    fun scheduleSnoozeArmsBroadcastFire() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val triggerAt = System.currentTimeMillis() + 60_000L
        AstroAlarmScheduler.scheduleSnooze(context, "alarm-1", triggerAt)
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val next = shadowOf(alarmManager).nextScheduledAlarm
        assertNotNull(next)
        val operation = next!!.operation
        assertNotNull(operation)
        val fired = shadowOf(operation).savedIntent
        assertEquals(AstroAlarmScheduler.ACTION_ALARM_FIRE, fired.action)
        assertEquals(AstroAlarmReceiver::class.java.name, fired.component?.className)
        assertEquals("alarm-1", fired.getStringExtra(AstroAlarmScheduler.EXTRA_ALARM_ID))
    }
}
