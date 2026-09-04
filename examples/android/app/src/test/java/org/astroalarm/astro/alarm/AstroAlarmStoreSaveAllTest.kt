package org.astroalarm.astro.alarm

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.solarterm.SolarTerm
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class AstroAlarmStoreSaveAllTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun clear() {
        context.getSharedPreferences("os_astro_alarms_prefs", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    fun saveAllWritesEveryPeerStamp() {
        val store = AstroAlarmStore(context)
        val sun = AstroAlarm("b", "June", target = AlarmTarget.Solar(SolarEventType.JuneSolstice))
        val jieqi = AstroAlarm("a", "Xiazhi", target = AlarmTarget.SolarTerm(SolarTerm.XIAZHI))
        store.saveAll(listOf(sun, jieqi))
        val stamped = AlarmFireIdentity.consumeOccurrence(store.getAll(), sun, 1_700_000_000_000L)
        store.saveAll(stamped)
        val again = AstroAlarmStore(context)
        assertEquals(1_700_000_000_000L, again.getAll().first { it.id == "a" }.lastFiredEpochMs)
        assertEquals(1_700_000_000_000L, again.getAll().first { it.id == "b" }.lastFiredEpochMs)
    }
}
