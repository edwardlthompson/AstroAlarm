package org.astroalarm.ui.solarterm

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.cos
import kotlin.math.sin

object SolarTermAlarmDots {
    fun draw(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        r: Float,
        rot: Float,
        alarmOrds: Set<Int>,
        size: Int,
    ) {
        if (alarmOrds.isEmpty()) return
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFE53935.toInt() }
        val rotR = Math.toRadians(rot.toDouble())
        val rad = (size * 0.018f).coerceIn(4f, 10f)
        alarmOrds.forEach { ord ->
            val mid = Math.toRadians(SolarTermWheelRenderer.midDeg(ord).toDouble())
            val lx = r * cos(mid)
            val ly = r * sin(mid)
            val x = cx + (lx * cos(rotR) - ly * sin(rotR)).toFloat()
            val y = cy + (lx * sin(rotR) + ly * cos(rotR)).toFloat()
            canvas.drawCircle(x, y, rad, p)
        }
    }
}
