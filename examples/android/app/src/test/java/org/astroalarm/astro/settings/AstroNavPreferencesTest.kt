package org.astroalarm.astro.settings

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class AstroNavPreferencesTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun clearPrefs() {
        context.getSharedPreferences("astro_display_prefs", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    fun defaultsAreDaily2dAndYearly() = runBlocking {
        val prefs = AstroNavPreferences(context)
        assertEquals(DailyChip.TwoD, prefs.dailyChip.first())
        assertEquals(SkyChip.Yearly, prefs.skyChip.first())
    }

    @Test
    fun persistsDailyAndSkyChips() = runBlocking {
        AstroNavPreferences(context).apply {
            setDailyChip(DailyChip.ThreeD)
            setSkyChip(SkyChip.Chart)
        }
        val reader = AstroNavPreferences(context)
        assertEquals(DailyChip.ThreeD, reader.dailyChip.first())
        assertEquals(SkyChip.Chart, reader.skyChip.first())
    }
}
