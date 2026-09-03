package org.astroalarm.astro.settings

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.astroalarm.widget.SolarTermWidgetProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class AstroDisplayPreferencesTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun clearPrefs() {
        context.getSharedPreferences("astro_display_prefs", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    fun eventTimesDefaultOn() = runBlocking {
        val prefs = AstroDisplayPreferences(context)
        assertTrue(prefs.isShowEventTimes2D())
        assertTrue(prefs.showEventTimes2D.first())
    }

    @Test
    fun persistsEventTimesToggle() = runBlocking {
        val writer = AstroDisplayPreferences(context)
        writer.setShowEventTimes2D(false)
        assertFalse(writer.isShowEventTimes2D())

        val reader = AstroDisplayPreferences(context)
        assertFalse(reader.isShowEventTimes2D())
        assertFalse(reader.showEventTimes2D.first())
    }

    @Test
    fun monthTicksDefaultOffAndPersist() = runBlocking {
        val prefs = AstroDisplayPreferences(context)
        assertFalse(prefs.isShowMonthTicks2D())
        prefs.setShowMonthTicks2D(true)
        val reader = AstroDisplayPreferences(context)
        assertTrue(reader.isShowMonthTicks2D())
        assertTrue(reader.showMonthTicks2D.first())
    }

    @Test
    fun solarTermCompactPersists() = runBlocking {
        val prefs = AstroDisplayPreferences(context)
        assertFalse(prefs.isSolarTermCompact())
        prefs.setSolarTermCompact(true)
        val reader = AstroDisplayPreferences(context)
        assertTrue(reader.isSolarTermCompact())
        assertTrue(reader.solarTermCompact.first())
        assertTrue(SolarTermWidgetProvider.drawCompact(reader.isSolarTermCompact()))
        prefs.setSolarTermCompact(false)
        assertFalse(SolarTermWidgetProvider.drawCompact(prefs.isSolarTermCompact()))
    }

    @Test
    fun yearlyWidgetKeepsCompactOnFullscreenTile() {
        assertTrue(SolarTermWidgetProvider.drawCompact(true))
        assertFalse(SolarTermWidgetProvider.drawCompact(false))
    }
}
