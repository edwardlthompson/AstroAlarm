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
import org.astroalarm.ui.solarterm.SolarTermAlarmDots
import org.astroalarm.ui.solarterm.SolarTermDrawFactory
import org.astroalarm.ui.solarterm.SolarTermFormat
import org.astroalarm.ui.solarterm.SolarTermWheelRenderer
import java.time.Instant

class SolarTermWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val place = AstroPlaceStore(context).get()
        val alarms = AstroAlarmStore(context).getAll()
        val displayPrefs = AstroDisplayPreferences(context)
        val compact = drawCompact(displayPrefs.isSolarTermCompact())
        val now = Instant.now()
        val dark = isNightUi(context)
        val (snap, req) = SolarTermDrawFactory.request(
            context.resources, place, now, dark = dark, compact = compact,
            SolarTermAlarmDots.ordsOf(alarms, displayPrefs.isShowEventTimesYearly()),
        )
        val zone = SolarTermFormat.zoneOf(place)
        val desc = SolarTermFormat.nextGlance(
            context.resources, snap.next, zone, SolarTermFormat.southern(place),
        )
        val launch = PendingIntent.getActivity(
            context, 8870, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        for (id in appWidgetIds) {
            val options = appWidgetManager.getAppWidgetOptions(id)
            val minW = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 140) ?: 140
            val minH = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 140) ?: 140
            val sizePx = ClockRenderSize.fromMinDp(minOf(minW, minH))
            val bitmap = SolarTermWheelRenderer.render(
                req, sizePx, EarthTexture.get(context), MoonTexture.get(context)
            )
            val views = RemoteViews(context.packageName, R.layout.widget_astro).apply {
                setImageViewBitmap(R.id.widget_astro_disk, bitmap)
                setContentDescription(R.id.widget_astro_disk, desc)
                setOnClickPendingIntent(R.id.widget_astro_root, launch)
            }
            appWidgetManager.updateAppWidget(id, views)
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, mgr: AppWidgetManager, id: Int, options: Bundle) {
        super.onAppWidgetOptionsChanged(context, mgr, id, options)
        onUpdate(context, mgr, intArrayOf(id))
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
        /** In-app Compact wins at every tile size, including fullscreen. */
        fun drawCompact(prefCompact: Boolean): Boolean = prefCompact

        fun updateAll(context: Context) {
            val mgr = AppWidgetManager.getInstance(context) ?: return
            val ids = mgr.getAppWidgetIds(ComponentName(context, SolarTermWidgetProvider::class.java))
            if (ids.isNotEmpty()) SolarTermWidgetProvider().onUpdate(context, mgr, ids)
        }

        private fun isNightUi(context: Context): Boolean {
            val night = context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
            return night == android.content.res.Configuration.UI_MODE_NIGHT_YES
        }
    }
}
