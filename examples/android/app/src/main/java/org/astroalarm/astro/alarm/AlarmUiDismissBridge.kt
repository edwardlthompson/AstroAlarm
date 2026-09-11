package org.astroalarm.astro.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.content.ContextCompat

/** Registers shade-dismiss broadcasts so lockscreen Activity can stop tone and finish. */
object AlarmUiDismissBridge {
    fun register(context: Context, onDismiss: () -> Unit): BroadcastReceiver {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action == AlarmNotificationActions.ACTION_UI_DISMISS) onDismiss()
            }
        }
        val filter = IntentFilter(AlarmNotificationActions.ACTION_UI_DISMISS)
        if (Build.VERSION.SDK_INT >= 33) {
            ContextCompat.registerReceiver(
                context,
                receiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(receiver, filter)
        }
        return receiver
    }

    fun unregister(context: Context, receiver: BroadcastReceiver?) {
        if (receiver == null) return
        runCatching { context.unregisterReceiver(receiver) }
    }
}
