package org.astroalarm.onboard

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import androidx.core.content.ContextCompat

object OnboardingChecker {
    fun isGranted(context: Context, step: OnboardingStep, sdk: Int = Build.VERSION.SDK_INT): Boolean {
        return when (step) {
            OnboardingStep.Notifications ->
                sdk < OnboardingPolicy.NOTIFICATIONS_SDK || has(context, Manifest.permission.POST_NOTIFICATIONS)
            OnboardingStep.Location ->
                has(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    has(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            OnboardingStep.ExactAlarms ->
                sdk < OnboardingPolicy.EXACT_ALARM_SDK || canScheduleExact(context)
            OnboardingStep.FullScreenIntent ->
                sdk < OnboardingPolicy.FULL_SCREEN_SDK || canUseFullScreen(context)
            OnboardingStep.Battery -> isIgnoringBattery(context)
        }
    }

    fun snapshot(context: Context, sdk: Int = Build.VERSION.SDK_INT): Map<OnboardingStep, Boolean> =
        OnboardingPolicy.steps(sdk).associateWith { isGranted(context, it, sdk) }

    private fun has(context: Context, permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    private fun canScheduleExact(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val am = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return false
        return am.canScheduleExactAlarms()
    }

    private fun canUseFullScreen(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 34) return true
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return false
        return nm.canUseFullScreenIntent()
    }

    private fun isIgnoringBattery(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager ?: return false
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }
}
