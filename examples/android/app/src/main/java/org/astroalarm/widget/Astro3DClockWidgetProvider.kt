package org.astroalarm.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import dev.foss.goldenpath.MainActivity
import dev.foss.goldenpath.R
import org.astroalarm.astro.alarm.AstroAlarmStore
import org.astroalarm.astro.place.AstroPlaceStore
import org.astroalarm.astro.settings.AstroDisplayPreferences
import java.time.Instant

class Astro3DClockWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val placeStore = AstroPlaceStore(context)
        val alarmStore = AstroAlarmStore(context)
        val displayPrefs = AstroDisplayPreferences(context)
        val place = placeStore.get()
        val alarms = alarmStore.getAll()
        val showZodiac = displayPrefs.isShowZodiac3D()
        val showEventTimes = displayPrefs.isShowEventTimes3D()
        val now = Instant.now()

        val launchIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 8850, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        for (id in appWidgetIds) {
            val options = appWidgetManager.getAppWidgetOptions(id)
            val minWidthDp = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 250) ?: 250
            val minHeightDp = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 250) ?: 250
            val sizePx = ClockRenderSize.fromMinDp(minOf(minWidthDp, minHeightDp))
            val bitmap = Astro3DRenderer.render3D(
                place, alarms, now, sizePx,
                showZodiac = showZodiac,
                showEventTimes = showEventTimes,
                earth = EarthTexture.get(context),
            )

            val views = RemoteViews(context.packageName, R.layout.widget_astro).apply {
                setImageViewBitmap(R.id.widget_astro_disk, bitmap)
                setContentDescription(R.id.widget_astro_disk, context.getString(R.string.astro_widget_3d_desc))
                setOnClickPendingIntent(R.id.widget_astro_root, pendingIntent)
            }
            appWidgetManager.updateAppWidget(id, views)
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        onUpdate(context, appWidgetManager, intArrayOf(appWidgetId))
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AstroClockWidgetProvider.ACTION_WIDGET_TICK ||
            intent.action == Intent.ACTION_TIME_TICK ||
            intent.action == Intent.ACTION_TIMEZONE_CHANGED ||
            intent.action == Intent.ACTION_TIME_CHANGED
        ) {
            updateAll(context)
        }
    }

    companion object {
        fun updateAll(context: Context) {
            val mgr = AppWidgetManager.getInstance(context) ?: return
            val ids = mgr.getAppWidgetIds(ComponentName(context, Astro3DClockWidgetProvider::class.java))
            if (ids.isNotEmpty()) {
                val provider = Astro3DClockWidgetProvider()
                provider.onUpdate(context, mgr, ids)
            }
        }
    }
}
