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
import org.astroalarm.astro.alarm.AstroAlarmScheduler
import org.astroalarm.astro.alarm.AstroAlarmStore
import org.astroalarm.astro.alarm.AstroNextFire
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.place.AstroPlaceStore
import org.astroalarm.astro.settings.AstroDisplayPreferences
import org.astroalarm.ui.AlarmViewMode
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AstroUpcomingWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val placeStore = AstroPlaceStore(context)
        val alarmStore = AstroAlarmStore(context)
        val displayPrefs = AstroDisplayPreferences(context)
        val place = placeStore.get()
        val alarms = alarmStore.getAll().filter { it.enabled }
        val viewMode = displayPrefs.getAlarmViewMode()
        val now = Instant.now()
        val zone = place?.zone ?: java.time.ZoneId.systemDefault()
        val horizon = now.plusSeconds(86400L * 2)
        val timeFmt = DateTimeFormatter.ofPattern("EEE HH:mm", Locale.getDefault())

        val formattedItems: List<String> = if (viewMode == AlarmViewMode.Grouped) {
            // Grouped by Category
            val solar = alarms.filter { it.target is AlarmTarget.Solar }.mapNotNull { a -> AstroNextFire.nextInstant(a, place, now)?.let { a to it } }
            val lunar = alarms.filter { it.target is AlarmTarget.Lunar }.mapNotNull { a -> AstroNextFire.nextInstant(a, place, now)?.let { a to it } }
            val zodiac = alarms.filter { it.target is AlarmTarget.Zodiac }.mapNotNull { a -> AstroNextFire.nextInstant(a, place, now)?.let { a to it } }
            val clock = alarms.filter { it.target is AlarmTarget.CustomClock }.mapNotNull { a -> AstroNextFire.nextInstant(a, place, now)?.let { a to it } }

            val list = mutableListOf<String>()
            (solar + lunar + zodiac + clock).forEach { (alarm, next) ->
                if (next.isAfter(now) && !next.isAfter(horizon)) {
                    val zdt = ZonedDateTime.ofInstant(next, zone)
                    val icon = when (val t = alarm.target) {
                        is AlarmTarget.Solar -> "☀️ "
                        is AlarmTarget.Lunar -> "🌙 "
                        is AlarmTarget.Zodiac -> t.sign.symbol + " "
                        is AlarmTarget.CustomClock -> "⏰ "
                    }
                    val label = alarm.label.ifBlank {
                        when (val t = alarm.target) {
                            is AlarmTarget.Solar -> t.event.name
                            is AlarmTarget.Lunar -> t.event.name
                            is AlarmTarget.Zodiac -> t.sign.englishName
                            is AlarmTarget.CustomClock -> String.format(Locale.getDefault(), "%02d:%02d", t.hour, t.minute)
                        }
                    }
                    list.add("$icon${zdt.format(timeFmt)} - $label")
                }
            }
            list
        } else {
            // Sorted Strictly Next Due
            val upcomingItems = mutableListOf<Pair<Instant, String>>()
            alarms.forEach { alarm ->
                AstroNextFire.nextInstant(alarm, place, now)?.let { next ->
                    if (next.isAfter(now) && !next.isAfter(horizon)) {
                        val zdt = ZonedDateTime.ofInstant(next, zone)
                        val icon = when (val t = alarm.target) {
                            is AlarmTarget.Solar -> "☀️ "
                            is AlarmTarget.Lunar -> "🌙 "
                            is AlarmTarget.Zodiac -> t.sign.symbol + " "
                            is AlarmTarget.CustomClock -> "⏰ "
                        }
                        val label = alarm.label.ifBlank {
                            when (val t = alarm.target) {
                                is AlarmTarget.Solar -> t.event.name
                                is AlarmTarget.Lunar -> t.event.name
                                is AlarmTarget.Zodiac -> t.sign.englishName
                                is AlarmTarget.CustomClock -> String.format(Locale.getDefault(), "%02d:%02d", t.hour, t.minute)
                            }
                        }
                        upcomingItems.add(next to "$icon${zdt.format(timeFmt)} - $label")
                    }
                }
            }
            upcomingItems.sortedBy { it.first.toEpochMilli() }.map { it.second }
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
