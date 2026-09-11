package dev.foss.goldenpath.memory

import android.app.ActivityManager
import android.app.Application
import android.content.ComponentCallbacks2
import android.os.Build
import android.util.Log

/**
 * Android 17 memory-limiter helpers. No largeHeap; release builds shrink with R8.
 */
object MemoryBudget {
    private const val TAG = "MemoryBudget"
    private const val LIMITER_MARKER = "MemoryLimiter"
    private const val ANON_SWAP = "AnonSwap"

    /** Null/blank descriptions are never limiter kills. */
    fun isLimiterKill(description: String?): Boolean {
        val text = description?.trim().orEmpty()
        if (text.isEmpty()) return false
        return text.contains(LIMITER_MARKER, ignoreCase = true) ||
            text.contains(ANON_SWAP, ignoreCase = true)
    }

    fun shouldTrimEphemeral(level: Int): Boolean =
        level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN ||
            level == ComponentCallbacks2.TRIM_MEMORY_BACKGROUND

    fun logRecentLimiterKills(app: Application) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return
        runCatching {
            val am = app.getSystemService(ActivityManager::class.java) ?: return
            am.getHistoricalProcessExitReasons(null, 0, 8).forEach { info ->
                if (isLimiterKill(info.description)) {
                    Log.w(TAG, "prior limiter kill: ${info.description}")
                }
            }
        }
    }

    fun onTrimMemory(level: Int, clearEphemeral: () -> Unit = {}) {
        if (!shouldTrimEphemeral(level)) return
        clearEphemeral()
        runCatching { Log.i(TAG, "onTrimMemory level=$level") }
    }
}
