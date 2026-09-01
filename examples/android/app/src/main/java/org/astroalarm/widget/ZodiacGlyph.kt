package org.astroalarm.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import org.astroalarm.astro.zodiac.ZodiacSign
import kotlin.math.cos
import kotlin.math.sin

/** Unicode ♈–♓ as text (VS15), sized to fill a shared bubble. Yellow = sun's sign. */
object ZodiacGlyph {
    fun radius(size: Int): Float = (size * 0.035f).coerceIn(11f, 21f)

    fun diskMargin(size: Int): Float = radius(size) * 2.2f + 2f

    fun ringDistance(center: Float, size: Int): Float =
        center - diskMargin(size) + radius(size) * 1.05f

    fun textSymbol(sign: ZodiacSign): String = sign.symbol + "\uFE0E"

    fun drawRing(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        rx: Float,
        ry: Float,
        sunLon: Double,
        midday: ZodiacSign,
        size: Int,
    ) {
        ZodiacSign.entries.forEach { sign ->
            val ang = Math.toRadians(sign.startLongitudeDeg + 15.0 - sunLon)
            val x = cx + rx * cos(ang).toFloat()
            val y = cy + ry * sin(ang).toFloat()
            val front = sin(ang) >= 0
            val bubble = when {
                sign == midday -> Color.rgb(255, 196, 48)
                front -> Color.rgb(48, 78, 118)
                else -> Color.rgb(22, 36, 58)
            }
            val glyph = if (sign == midday) Color.rgb(40, 24, 0) else Color.rgb(240, 248, 255)
            draw(canvas, x, y, sign, bubble, glyph, size)
        }
    }

    fun draw(canvas: Canvas, x: Float, y: Float, sign: ZodiacSign, bubble: Int, glyph: Int, size: Int) {
        val r = radius(size)
        canvas.drawCircle(x, y, r, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = bubble })
        canvas.drawCircle(
            x, y, r,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(8, 10, 16)
                style = Paint.Style.STROKE
                strokeWidth = (r * 0.10f).coerceIn(0.8f, 1.5f)
            },
        )
        val tp = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = glyph
            textSize = r * 1.78f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            isFakeBoldText = true
        }
        canvas.drawText(textSymbol(sign), x, y - (tp.ascent() + tp.descent()) / 2f, tp)
    }
}
