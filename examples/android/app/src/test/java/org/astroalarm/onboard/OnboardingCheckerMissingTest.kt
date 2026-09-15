package org.astroalarm.onboard

import android.Manifest
import android.app.Application
import android.app.NotificationManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class OnboardingCheckerMissingTest {
    @Test
    fun missingStepsIncludesNotificationsWhenBlocked() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        shadowOf(context).denyPermissions(Manifest.permission.POST_NOTIFICATIONS)
        shadowOf(context.getSystemService(NotificationManager::class.java))
            .setNotificationsEnabled(false)
        val missing = OnboardingChecker.missingSteps(context, sdk = 33)
        assertTrue(missing.contains(OnboardingStep.Notifications))
        assertTrue(OnboardingChecker.hasMissing(context, sdk = 33))
    }

    @Test
    fun notificationsNotMissingWhenAllowed() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        shadowOf(context).grantPermissions(Manifest.permission.POST_NOTIFICATIONS)
        shadowOf(context.getSystemService(NotificationManager::class.java))
            .setNotificationsEnabled(true)
        assertFalse(
            OnboardingChecker.missingSteps(context, sdk = 33)
                .contains(OnboardingStep.Notifications),
        )
    }

    @Test
    fun missingRingStepsIgnoresLocationDeny() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        shadowOf(context).denyPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        shadowOf(context).grantPermissions(Manifest.permission.POST_NOTIFICATIONS)
        shadowOf(context.getSystemService(NotificationManager::class.java))
            .setNotificationsEnabled(true)
        val missing = OnboardingChecker.missingRingSteps(context, sdk = 26)
        assertFalse(missing.contains(OnboardingStep.Location))
        assertTrue(OnboardingChecker.missingSteps(context, sdk = 26).contains(OnboardingStep.Location))
    }
}
