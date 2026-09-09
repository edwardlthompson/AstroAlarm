package org.astroalarm.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.cos
import kotlin.math.sin

/** Red ray from the pie rim through enabled month/zodiac rings. */
object SolarNoonPointer {
    fun endR(months: Boolean, zodiac: Boolean, rings: DiskRingLayout.Rings): Float {
        if (!months && !zodiac) return 0f
        return rings.noonPointerEndR
    }

    fun draw(
        canvas: Canvas,
        center: Float,
        startR: Float,
        endR: Float,
        angleDeg: Float,
        size: Int,
    ) {
        if (endR <= startR) return
        val rad = Math.toRadians(angleDeg.toDouble())
        val cosR = cos(rad).toFloat()
        val sinR = sin(rad).toFloat()
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
            strokeWidth = (size * 0.012f).coerceIn(2.5f, 5f)
            style = Paint.Style.STROKE
        }
        canvas.drawLine(
            center + startR * cosR, center + startR * sinR,
            center + endR * cosR, center + endR * sinR, p,
        )
    }
}
