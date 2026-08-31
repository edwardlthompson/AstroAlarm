package org.astroalarm.widget

import android.graphics.*
import org.astroalarm.astro.zodiac.ZodiacSign
import kotlin.math.*

object AstroDiskOverlays {

    fun drawTicksAndDividers(canvas: Canvas, center: Float, radius: Float, size: Int, dividers: List<Pair<Float, Boolean>>) {
        val darkDiv = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(175, 10, 14, 22); strokeWidth = (size * 0.006f).coerceIn(1.5f, 3.2f); style = Paint.Style.STROKE }
        val lightDiv = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(195, 240, 245, 255); strokeWidth = (size * 0.006f).coerceIn(1.5f, 3.2f); style = Paint.Style.STROKE }
        val darkTick = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(10, 14, 22); strokeWidth = (size * 0.010f).coerceIn(2.2f, 4.5f); style = Paint.Style.STROKE }
        val lightTick = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(240, 245, 255); strokeWidth = (size * 0.010f).coerceIn(2.2f, 4.5f); style = Paint.Style.STROKE }
        val tickLen = (size * 0.042f).coerceIn(9f, 20f)

        dividers.forEach { (deg, isDay) ->
            val rad = deg * (Math.PI / 180.0)
            val cosR = cos(rad).toFloat(); val sinR = sin(rad).toFloat()
            canvas.drawLine(center, center, center + radius * cosR, center + radius * sinR, if (isDay) darkDiv else lightDiv)
            canvas.drawLine(center + (radius - tickLen) * cosR, center + (radius - tickLen) * sinR, center + radius * cosR, center + radius * sinR, if (isDay) darkTick else lightTick)
        }
    }

    fun drawTransitionBadge(canvas: Canvas, center: Float, radius: Float, angleDeg: Float, timeStr: String, textSize: Float) {
        val rad = angleDeg * (Math.PI / 180.0)
        val cosR = cos(rad).toFloat(); val sinR = sin(rad).toFloat()

        val tp = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.textSize = textSize; typeface = Typeface.DEFAULT_BOLD; textAlign = Paint.Align.CENTER; color = Color.WHITE }
        val tDist = radius * 0.68f
        val tx = center + tDist * cosR
        val ty = center + tDist * sinR + textSize * 0.35f
        val pillW = tp.measureText(timeStr) + 16f
        val pillRect = RectF(tx - pillW / 2f, ty - textSize * 0.88f, tx + pillW / 2f, ty + textSize * 0.32f)
        canvas.drawRoundRect(pillRect, 6f, 6f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(215, 10, 14, 24); style = Paint.Style.FILL })
        canvas.drawText(timeStr, tx, ty, tp)
    }

    fun drawZodiacRing(canvas: Canvas, center: Float, dist: Float, emojiSize: Float, sunLon: Double, aNoonDeg: Float) {
        val defaultPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = emojiSize; textAlign = Paint.Align.CENTER; color = Color.rgb(225, 235, 250) }
        val middayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = emojiSize * 1.25f; typeface = Typeface.DEFAULT_BOLD; textAlign = Paint.Align.CENTER; color = Color.rgb(255, 212, 34) }
        val middaySign = ZodiacSign.fromEclipticLongitude(sunLon)

        ZodiacSign.entries.forEach { sign ->
            val signCenter = sign.startLongitudeDeg + 15.0
            val deg = aNoonDeg + (signCenter - sunLon).toFloat()
            val rad = deg * (Math.PI / 180.0)
            val x = center + dist * cos(rad).toFloat()
            val y = center + dist * sin(rad).toFloat() + emojiSize * 0.35f
            canvas.drawText(sign.symbol, x, y, if (sign == middaySign) middayPaint else defaultPaint)
        }
    }

    fun drawCallouts(canvas: Canvas, center: Float, radius: Float, size: Int, callouts: List<String>) {
        val textSize = (size * 0.038f).coerceIn(9f, 16f)
        val lineHeight = textSize * 1.22f
        val maxLines = (((radius * 0.55f) - 4f) / lineHeight).toInt().coerceAtLeast(1)
        val displayLines = callouts.take(maxLines)
        if (displayLines.isEmpty()) return

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; this.textSize = textSize; typeface = Typeface.DEFAULT_BOLD; textAlign = Paint.Align.CENTER }
        val maxTextW = displayLines.maxOfOrNull { textPaint.measureText(it) } ?: 0f
        val pillHeight = displayLines.size * lineHeight + 6f
        val yCenter = center + radius * 0.48f
        val maxDy = (yCenter + pillHeight / 2f) - center
        val safeChordHalf = if (maxDy < radius) sqrt(radius * radius - maxDy * maxDy) - 8f else radius * 0.45f
        val pillWidth = minOf(maxTextW + 14f, safeChordHalf * 2f).coerceAtLeast(36f)
        val pillRect = RectF(center - pillWidth / 2f, yCenter - pillHeight / 2f, center + pillWidth / 2f, yCenter + pillHeight / 2f)
        canvas.drawRoundRect(pillRect, 6f, 6f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(215, 14, 20, 30); style = Paint.Style.FILL })

        var textY = yCenter - (displayLines.size - 1) * (lineHeight / 2f) + (textSize * 0.35f)
        displayLines.forEach { line ->
            canvas.drawText(line, center, textY, textPaint)
            textY += lineHeight
        }
    }
}
