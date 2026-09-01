package org.astroalarm.widget

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
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.place.AstroPlaceStore
import org.astroalarm.astro.settings.AstroDisplayPreferences
import org.astroalarm.astro.sun.SolarCalculator
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AstroClockWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val placeStore = AstroPlaceStore(context)
        val alarmStore = AstroAlarmStore(context)
        val displayPrefs = AstroDisplayPreferences(context)
        val place = placeStore.get()
        val alarms = alarmStore.getAll()
        val showZodiac = displayPrefs.isShowZodiac2D()
        val showEventTimes = displayPrefs.isShowEventTimes2D()
        val now = Instant.now()
        val zone = place?.zone ?: java.time.ZoneId.systemDefault()
        val today = LocalDate.now(zone)

        val nextRise = place?.let { SolarCalculator.calculate(SolarEventType.Sunrise, today, it.latitude, it.longitude, it.zone) }
        val nextSet = place?.let { SolarCalculator.calculate(SolarEventType.Sunset, today, it.latitude, it.longitude, it.zone) }
        val fmt = DateTimeFormatter.ofPattern("HH:mm").withZone(zone)
        val desc = buildString {
            append(context.getString(R.string.astro_widget_desc))
            if (nextRise != null && nextSet != null) {
                append(" - Sunrise: ").append(fmt.format(nextRise)).append(", Sunset: ").append(fmt.format(nextSet))
            }
        }

        val launchIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = android.app.PendingIntent.getActivity(
            context, 8830, launchIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        for (id in appWidgetIds) {
            val options = appWidgetManager.getAppWidgetOptions(id)
            val minWidthDp = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 140) ?: 140
            val minHeightDp = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 140) ?: 140
            val sizePx = ClockRenderSize.fromMinDp(minOf(minWidthDp, minHeightDp))
            val bitmap = AstroDiskRenderer.renderDisk(
                place, alarms, now, sizePx,
                showZodiac = showZodiac,
                showEventTimes = showEventTimes,
            )

            val views = RemoteViews(context.packageName, R.layout.widget_astro).apply {
                setImageViewBitmap(R.id.widget_astro_disk, bitmap)
                setContentDescription(R.id.widget_astro_disk, desc)
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
        if (intent.action == ACTION_WIDGET_TICK ||
            intent.action == Intent.ACTION_TIME_TICK ||
            intent.action == Intent.ACTION_TIMEZONE_CHANGED ||
            intent.action == Intent.ACTION_TIME_CHANGED
        ) {
            updateAll(context)
        }
    }

    companion object {
        const val ACTION_WIDGET_TICK = "org.astroalarm.ACTION_WIDGET_TICK"

        fun updateAll(context: Context) {
            val mgr = AppWidgetManager.getInstance(context) ?: return
            val ids = mgr.getAppWidgetIds(ComponentName(context, AstroClockWidgetProvider::class.java))
            if (ids.isNotEmpty()) {
                val provider = AstroClockWidgetProvider()
                provider.onUpdate(context, mgr, ids)
            }
        }
    }
}
