package org.astroalarm.astro.alarm

import android.app.ActivityOptions
import android.os.Build
import android.os.Bundle

/**
 * Android 16: creator BAL only when creating a PendingIntent; sender BAL only when sending.
 * Mixing them throws or is reset.
 */
object AlarmLaunchOptions {
    fun senderBundle(): Bundle? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return null
        val opts = ActivityOptions.makeBasic()
        @Suppress("DEPRECATION")
        opts.setPendingIntentBackgroundActivityStartMode(
            ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED,
        )
        return opts.toBundle()
    }

    fun creatorBundle(): Bundle? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) return null
        val opts = ActivityOptions.makeBasic()
        opts.setPendingIntentCreatorBackgroundActivityStartMode(
            ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED,
        )
        return opts.toBundle()
    }
}
