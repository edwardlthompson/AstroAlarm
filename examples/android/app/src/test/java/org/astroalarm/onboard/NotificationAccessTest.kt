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
class NotificationAccessTest {
    @Test
    fun reportsDisabledWhenNotificationsBlocked() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        shadowOf(context).grantPermissions(Manifest.permission.POST_NOTIFICATIONS)
        val nm = context.getSystemService(NotificationManager::class.java)
        shadowOf(nm).setNotificationsEnabled(false)
        assertFalse(NotificationAccess.areEnabled(context))
    }

    @Test
    fun reportsDisabledWhenRuntimePermissionMissing() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val nm = context.getSystemService(NotificationManager::class.java)
        shadowOf(nm).setNotificationsEnabled(true)
        shadowOf(context).denyPermissions(Manifest.permission.POST_NOTIFICATIONS)
        assertFalse(NotificationAccess.areEnabled(context))
    }

    @Test
    fun reportsEnabledWhenNotificationsAndPermissionAllowed() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        shadowOf(context).grantPermissions(Manifest.permission.POST_NOTIFICATIONS)
        val nm = context.getSystemService(NotificationManager::class.java)
        shadowOf(nm).setNotificationsEnabled(true)
        assertTrue(NotificationAccess.areEnabled(context))
    }
}
