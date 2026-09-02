package org.astroalarm.math

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
class MathPreferencesTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun clearPrefs() {
        context.getSharedPreferences("astro_math_prefs", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    fun persistsElementaryAndSurvivesNewInstance() = runBlocking {
        val writer = MathPreferences(context)
        writer.setDifficulty(MathDifficulty.ELEMENTARY)
        writer.setProblemCount(3)
        assertEquals(MathDifficulty.ELEMENTARY, writer.getDifficulty())

        val reader = MathPreferences(context)
        assertEquals(MathDifficulty.ELEMENTARY, reader.getDifficulty())
        assertEquals(MathDifficulty.ELEMENTARY, reader.difficulty.first())
        assertEquals(3, reader.getProblemCount())
    }

    @Test
    fun mapsLegacyGeniusToHard() {
        context.getSharedPreferences("astro_math_prefs", Context.MODE_PRIVATE)
            .edit().putString("math_difficulty", "GENIUS").commit()
        val prefs = MathPreferences(context)
        assertEquals(MathDifficulty.HARD, prefs.getDifficulty())
    }
}
