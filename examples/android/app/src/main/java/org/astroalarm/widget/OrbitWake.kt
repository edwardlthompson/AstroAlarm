package org.astroalarm.widget

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.PI

/** Comet-style orbit wake: gap ahead of the body, thickest immediately behind. */
object OrbitWake {
    val GAP: Double = Math.toRadians(28.0)

    fun wrap2pi(rad: Double): Double {
        var a = rad % (2.0 * PI)
        if (a < 0.0) a += 2.0 * PI
        return a
    }

    fun width(aheadRad: Double, wMax: Float, wMin: Float = wMax * 0.12f): Float {
        val a = wrap2pi(aheadRad)
        if (a <= GAP) return 0f
        val span = 2.0 * PI - GAP
        val t = ((a - GAP) / span).coerceIn(0.0, 1.0)
        return (wMin + (wMax - wMin) * t).toFloat()
    }

    fun strokeChain(
        canvas: Canvas,
        xs: FloatArray,
        ys: FloatArray,
        ahead: DoubleArray,
        color: Int,
        wMax: Float,
    ) {
        if (xs.size < 2) return
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            this.color = color
        }
        val last = xs.size - 1
        for (i in 0 until last) {
            val w = if (width(ahead[i], wMax) <= 0f || width(ahead[i + 1], wMax) <= 0f) {
                0f
            } else {
                width(ahead[i], wMax)
            }
            if (w <= 0f) continue
            paint.strokeWidth = w
            canvas.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1], paint)
        }
    }
}
