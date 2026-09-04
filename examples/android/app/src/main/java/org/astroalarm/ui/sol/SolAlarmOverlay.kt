package org.astroalarm.ui.sol

import android.graphics.Canvas
import android.graphics.Paint
import org.astroalarm.astro.alarm.AlarmTargetCopy
import org.astroalarm.astro.alarm.AlarmWidgetScope
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.sol.PlanetKepler
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.hypot

object SolAlarmOverlay {
    private val stamp: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MMM d HH:mm", Locale.getDefault())

    fun labelOf(instant: Instant, zone: ZoneId, icon: String): String =
        icon + instant.atZone(zone).format(stamp)

    fun draw(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        pxPerAu: Float,
        now: Instant,
        alarms: List<AstroAlarm>,
        place: AstroPlace?,
        size: Int,
    ) {
        val zone = place?.zone ?: ZoneId.systemDefault()
        val dot = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFE53935.toInt() }
        val txt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xEEFFFFFF.toInt()
            textSize = (size * 0.028f).coerceIn(8f, 14f)
            textAlign = Paint.Align.CENTER
        }
        val rad = (size * 0.016f).coerceIn(3.5f, 8f)
        AlarmWidgetScope.solMarks(alarms, place, now).forEach { (alarm, next) ->
            val label = labelOf(next, zone, AlarmTargetCopy.icon(alarm.target))
            AlarmWidgetScope.solBodies(alarm.target).forEach { body ->
                val st = PlanetKepler.state(body, next)
                val x = cx + (st.x * pxPerAu).toFloat()
                val y = cy - (st.y * pxPerAu).toFloat()
                canvas.drawCircle(x, y, rad, dot)
                val dist = hypot(st.x, st.y)
                if (dist < 1e-4) return@forEach
                val pad = rad + txt.textSize
                val lx = x + ((st.x / dist) * pad).toFloat()
                val ly = y - ((st.y / dist) * pad).toFloat() + txt.textSize * 0.35f
                canvas.drawText(label, lx, ly, txt)
            }
        }
    }
}
