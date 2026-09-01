package org.astroalarm.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import org.astroalarm.astro.alarm.AstroNextFire
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.sky.BodySky
import org.astroalarm.astro.sky.SkyBodies
import java.time.Instant
import kotlin.math.cos
import kotlin.math.sin

object Astro3DTransitOverlay {
    fun draw(
        canvas: Canvas,
        marks: TransitTicks.Marks,
        cx: Float,
        sunCy: Float,
        sunRx: Float,
        sunRy: Float,
        moonCy: Float,
        moonRx: Float,
        moonRy: Float,
        size: Int,
    ) {
        val len = (size * 0.038f).coerceIn(8f, 16f)
        val sunPaint = tickPaint(Color.rgb(255, 196, 64), (size * 0.009f).coerceIn(2.4f, 4.2f))
        val moonFill = tickPaint(Color.rgb(255, 255, 255), (size * 0.010f).coerceIn(2.6f, 4.6f))
        val moonEdge = tickPaint(Color.rgb(12, 16, 28), (size * 0.016f).coerceIn(4.0f, 7.0f))
        val meridianFill = tickPaint(Color.rgb(48, 220, 210), (size * 0.010f).coerceIn(2.6f, 4.6f))
        marks.sun.forEach { ang -> tick(canvas, cx, sunCy, sunRx, sunRy, ang, len, sunPaint) }
        marks.moonHorizon.forEach { ang ->
            tick(canvas, cx, moonCy, moonRx, moonRy, ang, len, moonEdge)
            tick(canvas, cx, moonCy, moonRx, moonRy, ang, len * 0.72f, moonFill)
            dot(canvas, cx, moonCy, moonRx, moonRy, ang, (size * 0.018f).coerceIn(3.5f, 7f))
        }
        marks.moonMeridian.forEach { ang ->
            tick(canvas, cx, moonCy, moonRx, moonRy, ang, len, moonEdge)
            tick(canvas, cx, moonCy, moonRx, moonRy, ang, len * 0.72f, meridianFill)
            dot(canvas, cx, moonCy, moonRx, moonRy, ang, (size * 0.018f).coerceIn(3.5f, 7f), Color.rgb(48, 220, 210))
        }
    }

    fun drawAlarms(
        canvas: Canvas,
        alarms: List<AstroAlarm>,
        place: AstroPlace?,
        now: Instant,
        cx: Float,
        cy: Float,
        rx: Float,
        ry: Float,
        size: Int,
    ) {
        val nodePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(255, 60, 60) }
        val horizon = now.plusSeconds(86400L)
        val lat = place?.latitude ?: 40.0
        val lon = place?.longitude ?: -74.0
        alarms.filter { it.enabled }.forEach { alarm ->
            AstroNextFire.nextInstant(alarm, place, now)?.let { next ->
                if (next.isAfter(now) && !next.isAfter(horizon)) {
                    val ang = SkyBodies.sun(next, lat, lon)?.let {
                        BodySky.ringAngle(it.haRad, lat)
                    } ?: return@let
                    canvas.drawCircle(
                        cx + rx * cos(ang).toFloat(),
                        cy + ry * sin(ang).toFloat(),
                        (size * 0.024f).coerceIn(4f, 9f),
                        nodePaint,
                    )
                }
            }
        }
    }

    private fun tickPaint(color: Int, width: Float) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        strokeWidth = width
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private fun tick(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        rx: Float,
        ry: Float,
        ang: Double,
        len: Float,
        paint: Paint,
    ) {
        val cosA = cos(ang).toFloat()
        val sinA = sin(ang).toFloat()
        val x = cx + rx * cosA
        val y = cy + ry * sinA
        val nx = if (rx == 0f) 0f else cosA / rx
        val ny = if (ry == 0f) 0f else sinA / ry
        val mag = kotlin.math.sqrt(nx * nx + ny * ny).coerceAtLeast(1e-4f)
        canvas.drawLine(x - nx / mag * len, y - ny / mag * len, x + nx / mag * len, y + ny / mag * len, paint)
    }

    private fun dot(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        rx: Float,
        ry: Float,
        ang: Double,
        r: Float,
        fill: Int = Color.WHITE,
    ) {
        val x = cx + rx * cos(ang).toFloat()
        val y = cy + ry * sin(ang).toFloat()
        canvas.drawCircle(x, y, r, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(12, 16, 28) })
        canvas.drawCircle(x, y, r * 0.55f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = fill })
    }
}
