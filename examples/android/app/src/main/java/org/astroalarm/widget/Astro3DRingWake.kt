package org.astroalarm.widget

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import org.astroalarm.astro.sky.BodySky
import kotlin.math.cos
import kotlin.math.sin

/** Sun/moon transit wakes on the Daily 3D ellipses (zodiac stays even). */
object Astro3DRingWake {
    private const val STEPS = 96

    fun draw(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        rx: Float,
        ry: Float,
        bodyAng: Double,
        latDeg: Double,
        isBack: Boolean,
        color: Int,
        wMax: Float,
    ) {
        if (rx < 2f || ry < 2f) return
        val travel = if (latDeg >= 0.0) 1.0 else -1.0
        val xs = ArrayList<Float>(STEPS + 1)
        val ys = ArrayList<Float>(STEPS + 1)
        val ahead = ArrayList<Double>(STEPS + 1)
        for (i in 0..STEPS) {
            val ang = i * 2.0 * Math.PI / STEPS
            if (isBackHalf(ang) != isBack) {
                flush(canvas, xs, ys, ahead, color, wMax)
                continue
            }
            xs.add(cx + rx * cos(ang).toFloat())
            ys.add(cy + ry * sin(ang).toFloat())
            ahead.add(OrbitWake.wrap2pi(travel * (ang - bodyAng)))
        }
        flush(canvas, xs, ys, ahead, color, wMax)
    }

    fun ringAngleOf(haRad: Double, latDeg: Double): Double = BodySky.ringAngle(haRad, latDeg)

    private fun isBackHalf(angRad: Double): Boolean {
        var d = Math.toDegrees(angRad)
        d = ((d % 360.0) + 360.0) % 360.0
        return d >= 180.0
    }

    private fun flush(
        canvas: Canvas, xs: ArrayList<Float>, ys: ArrayList<Float>, ahead: ArrayList<Double>,
        color: Int, wMax: Float,
    ) {
        if (xs.size >= 2) {
            OrbitWake.strokeChain(
                canvas, xs.toFloatArray(), ys.toFloatArray(), ahead.toDoubleArray(), color, wMax,
            )
        }
        xs.clear()
        ys.clear()
        ahead.clear()
    }

    fun drawZodiacHalf(canvas: Canvas, cx: Float, cy: Float, rx: Float, ry: Float, isBack: Boolean, color: Int) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = (rx * 0.016f).coerceIn(1.8f, 3.5f)
            this.color = color
        }
        canvas.drawArc(RectF(cx - rx, cy - ry, cx + rx, cy + ry), if (isBack) 180f else 0f, 180f, false, p)
    }
}
