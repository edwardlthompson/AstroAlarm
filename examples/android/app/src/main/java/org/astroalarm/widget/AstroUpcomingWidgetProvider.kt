package org.astroalarm.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import dev.foss.goldenpath.MainActivity
import dev.foss.goldenpath.R
import org.astroalarm.astro.alarm.AlarmTargetCopy
import org.astroalarm.astro.alarm.AlarmWidgetScope
import org.astroalarm.astro.alarm.AstroAlarmStore
import org.astroalarm.astro.place.AstroPlaceStore
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AstroUpcomingWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val placeStore = AstroPlaceStore(context)
        val alarmStore = AstroAlarmStore(context)
        val place = placeStore.get()
        val alarms = alarmStore.getAll().filter { it.enabled }
        val now = Instant.now()
        val zone = place?.zone ?: java.time.ZoneId.systemDefault()
        val horizon = now.plusSeconds(86400L * 2)
        val timeFmt = DateTimeFormatter.ofPattern("EEE HH:mm", Locale.getDefault())

        val formattedItems: List<String> = AlarmWidgetScope.upcomingLines(alarms, place, now, horizon).map { (alarm, next) ->
            val zdt = ZonedDateTime.ofInstant(next, zone)
            val icon = AlarmTargetCopy.icon(alarm.target)
            val label = alarm.label.ifBlank { AlarmTargetCopy.fallback(alarm.target) }
            "$icon${zdt.format(timeFmt)} - $label"
        }

        val launchIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 8840, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_upcoming).apply {
                setOnClickPendingIntent(R.id.widget_upcoming_root, pendingIntent)
                if (formattedItems.isEmpty()) {
                    setViewVisibility(R.id.widget_upcoming_empty, View.VISIBLE)
                    setViewVisibility(R.id.widget_upcoming_line1, View.GONE)
                    setViewVisibility(R.id.widget_upcoming_line2, View.GONE)
                    setViewVisibility(R.id.widget_upcoming_line3, View.GONE)
                    setTextViewText(R.id.widget_upcoming_count, "0 active")
                } else {
                    setViewVisibility(R.id.widget_upcoming_empty, View.GONE)
                    setTextViewText(R.id.widget_upcoming_count, "${formattedItems.size} active")

                    setTextViewText(R.id.widget_upcoming_line1, formattedItems.getOrNull(0) ?: "")
                    setViewVisibility(R.id.widget_upcoming_line1, if (formattedItems.isNotEmpty()) View.VISIBLE else View.GONE)

                    setTextViewText(R.id.widget_upcoming_line2, formattedItems.getOrNull(1) ?: "")
                    setViewVisibility(R.id.widget_upcoming_line2, if (formattedItems.size > 1) View.VISIBLE else View.GONE)

                    setTextViewText(R.id.widget_upcoming_line3, formattedItems.getOrNull(2) ?: "")
                    setViewVisibility(R.id.widget_upcoming_line3, if (formattedItems.size > 2) View.VISIBLE else View.GONE)
                }
            }
            appWidgetManager.updateAppWidget(id, views)
        }
    }

    companion object {
        fun updateAll(context: Context) {
            val mgr = AppWidgetManager.getInstance(context) ?: return
            val ids = mgr.getAppWidgetIds(ComponentName(context, AstroUpcomingWidgetProvider::class.java))
            if (ids.isNotEmpty()) {
                val provider = AstroUpcomingWidgetProvider()
                provider.onUpdate(context, mgr, ids)
            }
        }
    }
}
