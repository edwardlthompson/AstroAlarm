package org.astroalarm.onboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

object OnboardingIntents {
    fun openSpecialAccess(context: Context, step: OnboardingStep) {
        val pkg = context.packageName
        val intent = when (step) {
            OnboardingStep.ExactAlarms -> exactAlarmIntent(pkg)
            OnboardingStep.FullScreenIntent -> fullScreenIntent(pkg)
            OnboardingStep.Battery -> batteryIntent(pkg)
            else -> null
        } ?: return
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
            .recoverCatching {
                if (step == OnboardingStep.Battery) {
                    context.startActivity(batteryListIntent().also {
                        if (context !is Activity) it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                }
            }
    }

    private fun exactAlarmIntent(pkg: String): Intent? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return null
        return Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).setData(Uri.parse("package:$pkg"))
    }

    private fun fullScreenIntent(pkg: String): Intent? {
        if (Build.VERSION.SDK_INT < 34) return null
        return Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).setData(Uri.parse("package:$pkg"))
    }

    private fun batteryIntent(pkg: String): Intent =
        Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).setData(Uri.parse("package:$pkg"))

    private fun batteryListIntent(): Intent =
        Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
}
