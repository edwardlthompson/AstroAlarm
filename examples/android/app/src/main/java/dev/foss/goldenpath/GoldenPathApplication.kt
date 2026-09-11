package dev.foss.goldenpath

import android.app.Application
import dev.foss.goldenpath.crashcapture.CrashCapture
import dev.foss.goldenpath.memory.MemoryBudget

/** Golden Path Application base: crash capture + memory-budget hooks. */
open class GoldenPathApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashCapture.install(this)
        MemoryBudget.logRecentLimiterKills(this)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        MemoryBudget.onTrimMemory(level)
    }
}
