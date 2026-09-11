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
import org.astroalarm.astro.birth.BirthChartCalculator
import org.astroalarm.astro.birth.BirthProfileStore
import org.astroalarm.astro.birth.NatalEventLinks
import org.astroalarm.astro.birth.NatalSkySnapshot
import org.astroalarm.astro.birth.NatalWheelRenderer

/** Bitmap natal chart disk (houses + natal + live luminaries). */
class NatalChartWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val profile = BirthProfileStore(context).active()
        val chart = profile?.let { BirthChartCalculator.compute(it) }
        val dark = isNightUi(context)
        val launch = PendingIntent.getActivity(
            context,
            8849,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val desc = context.getString(R.string.astro_widget_natal_chart_cd)
        for (id in appWidgetIds) {
            val options = appWidgetManager.getAppWidgetOptions(id)
            val minW = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 140) ?: 140
            val minH = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 140) ?: 140
            val sizePx = ClockRenderSize.fromMinDp(minOf(minW, minH))
            val bitmap = if (chart != null) {
                val sky = NatalSkySnapshot.now()
                NatalWheelRenderer.renderBitmap(
                    sizePx, chart, sky, NatalEventLinks.active(chart, sky), dark,
                )
            } else {
                NatalChartWidgetEmpty.bitmap(sizePx, dark, context.getString(R.string.astro_widget_natal_chart_empty))
            }
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
        fun updateAll(context: Context) {
            val mgr = AppWidgetManager.getInstance(context) ?: return
            val ids = mgr.getAppWidgetIds(ComponentName(context, NatalChartWidgetProvider::class.java))
            if (ids.isNotEmpty()) NatalChartWidgetProvider().onUpdate(context, mgr, ids)
        }

        private fun isNightUi(context: Context): Boolean {
            val night = context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
            return night == android.content.res.Configuration.UI_MODE_NIGHT_YES
        }
    }
}
