package org.astroalarm.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import dev.foss.goldenpath.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object WidgetPinEvents {
    private val _messages = MutableSharedFlow<Int>(extraBufferCapacity = 4)
    val messages: SharedFlow<Int> = _messages.asSharedFlow()

    fun emit(resId: Int) {
        _messages.tryEmit(resId)
    }
}

object AppSnackbar {
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    fun emit(text: String) {
        _messages.tryEmit(text)
    }
}

object WidgetPin {
    const val ACTION = "org.astroalarm.WIDGET_PINNED"

    fun request(context: Context, provider: Class<*>) {
        val mgr = context.getSystemService(AppWidgetManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            mgr != null &&
            mgr.isRequestPinAppWidgetSupported
        ) {
            val intent = Intent(ACTION).setPackage(context.packageName)
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            val success = PendingIntent.getBroadcast(
                context,
                provider.name.hashCode(),
                intent,
                flags,
            )
            mgr.requestPinAppWidget(ComponentName(context, provider), null, success)
        } else {
            WidgetPinEvents.emit(R.string.astro_widget_pin_manual_guide)
        }
    }
}

class WidgetPinReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != WidgetPin.ACTION) return
        WidgetPinEvents.emit(R.string.astro_widget_pinned_success)
    }
}
