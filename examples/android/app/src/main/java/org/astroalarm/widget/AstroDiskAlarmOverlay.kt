package org.astroalarm.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import org.astroalarm.astro.alarm.AlarmTargetCopy
import org.astroalarm.astro.alarm.AlarmWidgetScope
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.place.AstroPlace
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

object AstroDiskAlarmOverlay {
    fun draw(
        canvas: Canvas,
        alarms: List<AstroAlarm>,
        place: AstroPlace?,
        now: Instant,
        horizon: Instant,
        zone: ZoneId,
        center: Float,
        radius: Float,
        size: Int,
        nowAngle: Float,
        timeFmt: DateTimeFormatter,
    ) {
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.RED; style = Paint.Style.FILL }
        val upcomingItems = mutableListOf<Pair<Instant, String>>()
        AlarmWidgetScope.dailyMarks(alarms, place, now, horizon).forEach { (alarm, next) ->
            val z = ZonedDateTime.ofInstant(next, zone)
            val rad = (((z.hour * 60 + z.minute) / 1440f * 360f - nowAngle - 90f)) * (Math.PI / 180.0)
            canvas.drawCircle(
                center + radius * cos(rad).toFloat(),
                center + radius * sin(rad).toFloat(),
                (size * 0.026f).coerceIn(5f, 12f),
                dotPaint,
            )
            val icon = AlarmTargetCopy.icon(alarm.target)
            upcomingItems.add(next to (icon + z.format(timeFmt)))
        }
        val callouts = upcomingItems.sortedBy { it.first.toEpochMilli() }.distinctBy { it.second }.map { it.second }
        AstroDiskOverlays.drawCallouts(canvas, center, radius, size, callouts)
    }
}
