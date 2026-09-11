package dev.foss.goldenpath.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/** Listens for UnifiedPush connector MESSAGE; no-op without a distributor. */
class UnifiedPushMessageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (!UnifiedPushConfig.acceptsConnectorAction(intent?.action)) return
        Log.i(TAG, "UnifiedPush MESSAGE received (payload ignored until product opts in)")
    }

    companion object {
        private const val TAG = "UnifiedPush"
    }
}
