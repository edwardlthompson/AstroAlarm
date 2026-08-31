package org.astroalarm

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.astroalarm.astro.alarm.AstroAlarmScheduler

@HiltAndroidApp
class AstroAlarmApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AstroAlarmScheduler.rescheduleAll(this)
    }
}
