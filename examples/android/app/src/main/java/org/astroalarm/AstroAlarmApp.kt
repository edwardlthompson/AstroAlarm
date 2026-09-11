package org.astroalarm

import dagger.hilt.android.HiltAndroidApp
import dev.foss.goldenpath.GoldenPathApplication
import dev.foss.goldenpath.push.UnifiedPushConfig
import org.astroalarm.astro.alarm.AstroAlarmScheduler

@HiltAndroidApp
class AstroAlarmApp : GoldenPathApplication() {
    override fun onCreate() {
        super.onCreate()
        if (!UnifiedPushConfig.usesProprietaryPush() && UnifiedPushConfig.endpointUrl() == null) {
            android.util.Log.i("AstroAlarmApp", "no UnifiedPush distributor")
        }
        AstroAlarmScheduler.rescheduleAll(this)
    }
}
