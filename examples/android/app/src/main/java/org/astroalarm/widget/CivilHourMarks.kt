package org.astroalarm.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import kotlin.math.cos
import kotlin.math.sin

/**
 * Civil clock hours on the 24h disk: `deg = hour * 15 - nowAngle - 90`.
 * Independent of solar noon (do not pass `aNoonDeg`).
 */
object CivilHourMarks {
    data class Mark(val deg: Float, val label: String, val day: Boolean)

    fun marks(nowAngle: Float, aRise: Float? = null, aSet: Float? = null): List<Mark> =
        (0..23).map { hour ->
            val deg = hour * 15f - nowAngle - 90f
            Mark(deg = deg, label = hour.toString(), day = isDay(deg, aRise, aSet, hour))
        }

    fun isDay(deg: Float, aRise: Float?, aSet: Float?, hour: Int): Boolean {
        if (aRise == null || aSet == null) return hour in 6..17
        var fromRise = deg - aRise
        while (fromRise < 0f) fromRise += 360f
        var daySpan = aSet - aRise
        while (daySpan < 0f) daySpan += 360f
        return fromRise <= daySpan
    }

    fun draw(canvas: Canvas, center: Float, tickR: Float, labelR: Float, size: Int, marks: List<Mark>) {
        val tickLen = (size * 0.028f).coerceIn(6f, 12f)
        val evenOnly = DiskLabelFit.evenHoursOnly(size, labelR)
        val ts = DiskLabelFit.textSize(size, labelR, if (evenOnly) 12 else 24, "23")
        val tick = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = (size * 0.008f).coerceIn(1.6f, 3.2f)
            style = Paint.Style.STROKE
        }
        val tp = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = ts
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
        marks.forEach { mark ->
            val color = if (mark.day) Color.BLACK else Color.WHITE
            tick.color = color
            tp.color = color
            val rad = mark.deg * (Math.PI / 180.0)
            val cosR = cos(rad).toFloat()
            val sinR = sin(rad).toFloat()
            canvas.drawLine(
                center + (tickR - tickLen) * cosR, center + (tickR - tickLen) * sinR,
                center + tickR * cosR, center + tickR * sinR, tick,
            )
            val hour = mark.label.toInt()
            if (evenOnly && hour % 2 != 0) return@forEach
            val lx = center + labelR * cosR
            val ly = center + labelR * sinR - (tp.ascent() + tp.descent()) / 2f
            canvas.drawText(mark.label, lx, ly, tp)
        }
    }
}
