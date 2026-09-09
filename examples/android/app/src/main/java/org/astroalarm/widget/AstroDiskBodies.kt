package org.astroalarm.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import kotlin.math.cos
import kotlin.math.sin

object AstroDiskBodies {
    fun drawHand(canvas: Canvas, center: Float, startR: Float, endR: Float, size: Int) {
        if (endR <= startR) return
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
            strokeWidth = (size * 0.012f).coerceIn(2.5f, 5f)
            style = Paint.Style.FILL_AND_STROKE
        }
        canvas.drawLine(center, center - startR, center, center - endR, p)
    }

    fun drawSun(canvas: Canvas, center: Float, bodyR: Float, size: Int, sunDeg: Float) {
        val emoji = (size * 0.078f).coerceIn(18f, 38f)
        val ep = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = emoji; textAlign = Paint.Align.CENTER }
        val sx = center + bodyR * cos(Math.toRadians(sunDeg.toDouble())).toFloat()
        val sy = center + bodyR * sin(Math.toRadians(sunDeg.toDouble())).toFloat()
        canvas.drawCircle(sx, sy, size * 0.045f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                sx, sy, size * 0.045f,
                intArrayOf(Color.WHITE, Color.rgb(255, 215, 60), Color.TRANSPARENT),
                floatArrayOf(0f, 0.4f, 1f), Shader.TileMode.CLAMP,
            )
        })
        canvas.drawText("☀️", sx, sy + emoji * 0.35f, ep)
    }
}
