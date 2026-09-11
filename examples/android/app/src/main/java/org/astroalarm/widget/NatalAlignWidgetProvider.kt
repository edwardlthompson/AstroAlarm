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
import org.astroalarm.astro.birth.BirthProfileStore

class NatalAlignWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val store = BirthProfileStore(context)
        val profile = store.active()
        val empty = context.getString(R.string.astro_widget_natal_align_empty)
        val (title, doubleLine, tripleLine) = NatalAlignWidgetCopy.lines(profile, empty = empty)
        val launch = PendingIntent.getActivity(
            context,
            8848,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_natal_align).apply {
                setOnClickPendingIntent(R.id.widget_natal_align_root, launch)
                setTextViewText(R.id.widget_natal_align_title, context.getString(R.string.astro_widget_natal_align_name))
                if (profile == null) {
                    setViewVisibility(R.id.widget_natal_align_empty, View.VISIBLE)
                    setViewVisibility(R.id.widget_natal_align_profile, View.GONE)
                    setViewVisibility(R.id.widget_natal_align_double, View.GONE)
                    setViewVisibility(R.id.widget_natal_align_triple, View.GONE)
                    setTextViewText(R.id.widget_natal_align_empty, empty)
                } else {
                    setViewVisibility(R.id.widget_natal_align_empty, View.GONE)
                    setViewVisibility(R.id.widget_natal_align_profile, View.VISIBLE)
                    setViewVisibility(R.id.widget_natal_align_double, View.VISIBLE)
                    setViewVisibility(R.id.widget_natal_align_triple, View.VISIBLE)
                    setTextViewText(R.id.widget_natal_align_profile, title)
                    setTextViewText(R.id.widget_natal_align_double, doubleLine)
                    setTextViewText(R.id.widget_natal_align_triple, tripleLine)
                }
            }
            appWidgetManager.updateAppWidget(id, views)
        }
    }

    companion object {
        fun updateAll(context: Context) {
            val mgr = AppWidgetManager.getInstance(context) ?: return
            val ids = mgr.getAppWidgetIds(ComponentName(context, NatalAlignWidgetProvider::class.java))
            if (ids.isNotEmpty()) {
                NatalAlignWidgetProvider().onUpdate(context, mgr, ids)
            }
        }
    }
}
