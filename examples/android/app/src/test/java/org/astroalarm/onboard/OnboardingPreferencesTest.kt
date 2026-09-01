package org.astroalarm.onboard

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class OnboardingPreferencesTest {
    @Test
    fun completeFlagPersists() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("astro_onboarding_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        val prefs = OnboardingPreferences(context)
        assertFalse(prefs.isComplete())
        prefs.markComplete()
        assertTrue(OnboardingPreferences(context).isComplete())
    }

    @Test
    fun locationIsDeniedUntilGranted() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertFalse(OnboardingChecker.isGranted(context, OnboardingStep.Location, sdk = 26))
    }
}
