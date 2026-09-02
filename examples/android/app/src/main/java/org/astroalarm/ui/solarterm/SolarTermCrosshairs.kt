package org.astroalarm.ui.solarterm

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import org.astroalarm.solarterm.SolarTermLayout
import kotlin.math.cos
import kotlin.math.sin

/** Faint hub diameters: screen axes, solstices, equinoxes. Clipped to the hub fill. */
object SolarTermCrosshairs {
    fun draw(canvas: Canvas, cx: Float, cy: Float, hubFill: Float, dark: Boolean) {
        if (hubFill < 4f) return
        canvas.save()
        canvas.clipPath(Path().apply { addCircle(cx, cy, hubFill, Path.Direction.CW) })
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = (hubFill * 0.012f).coerceIn(0.8f, 1.6f)
            color = if (dark) 0x44FFFFFF.toInt() else 0x33212121.toInt()
        }
        canvas.drawLine(cx, cy - hubFill, cx, cy + hubFill, p)
        canvas.drawLine(cx - hubFill, cy, cx + hubFill, cy, p)
        spoke(canvas, cx, cy, hubFill, SolarTermLayout.canvasDeg(90.0), p)
        spoke(canvas, cx, cy, hubFill, SolarTermLayout.canvasDeg(0.0), p)
        canvas.restore()
    }

    private fun spoke(canvas: Canvas, cx: Float, cy: Float, r: Float, angDeg: Float, p: Paint) {
        val rad = Math.toRadians(angDeg.toDouble())
        val dx = (r * cos(rad)).toFloat()
        val dy = (r * sin(rad)).toFloat()
        canvas.drawLine(cx - dx, cy - dy, cx + dx, cy + dy, p)
    }
}
