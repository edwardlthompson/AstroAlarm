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

    fun drawZodiacRing(
        canvas: Canvas,
        center: Float,
        dist: Float,
        sunLon: Double,
        aNoonDeg: Float,
        size: Int,
    ) {
        val middaySign = ZodiacSign.fromEclipticLongitude(sunLon)
        ZodiacRingLayout.positions(center, dist, sunLon, aNoonDeg, size).forEach { hit ->
            val bubble = if (hit.sign == middaySign) Color.rgb(255, 196, 48) else Color.rgb(48, 78, 118)
            val glyph = if (hit.sign == middaySign) Color.rgb(40, 24, 0) else Color.rgb(240, 248, 255)
            ZodiacGlyph.draw(canvas, hit.x, hit.y, hit.sign, bubble, glyph, size)
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

    fun drawCenterHub(canvas: Canvas, center: Float, size: Int) {
        val hub = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = DiskCenterHub.COLOR
            style = Paint.Style.FILL
        }
        canvas.drawCircle(center, center, DiskCenterHub.radius(size), hub)
    }

    fun drawZodiacCusps(
        canvas: Canvas,
        center: Float,
        radius: Float,
        size: Int,
        sunLon: Double,
        aNoonDeg: Float,
    ) {
        drawRimTicks(canvas, center, radius, size, ZodiacRingLayout.cuspAngles(sunLon, aNoonDeg), Color.rgb(255, 196, 48))
    }

    fun drawMonthRim(canvas: Canvas, center: Float, radius: Float, size: Int, marks: List<MonthRimTicks.Mark>) {
        val tickLen = (size * 0.028f).coerceIn(6f, 12f)
        drawRimTicks(canvas, center, radius, size, marks.map { it.tickDeg }, Color.rgb(220, 228, 240))
        val tp = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(230, 236, 245)
            textSize = (size * 0.038f).coerceIn(9f, 14f)
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
        marks.forEach { mark ->
            val rad = mark.labelDeg * (Math.PI / 180.0)
            val cosR = cos(rad).toFloat()
            val sinR = sin(rad).toFloat()
            val lx = center + (radius + tickLen + tp.textSize * 0.70f) * cosR
            val ly = center + (radius + tickLen + tp.textSize * 0.70f) * sinR - (tp.ascent() + tp.descent()) / 2f
            canvas.drawText(mark.label, lx, ly, tp)
        }
    }

    private fun drawRimTicks(
        canvas: Canvas,
        center: Float,
        radius: Float,
        size: Int,
        angles: List<Float>,
        color: Int,
    ) {
        val tickLen = (size * 0.028f).coerceIn(6f, 12f)
        val tick = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            strokeWidth = (size * 0.008f).coerceIn(1.6f, 3.2f)
            style = Paint.Style.STROKE
        }
        angles.forEach { deg ->
            val rad = deg * (Math.PI / 180.0)
            val cosR = cos(rad).toFloat()
            val sinR = sin(rad).toFloat()
            canvas.drawLine(
                center + radius * cosR, center + radius * sinR,
                center + (radius + tickLen) * cosR, center + (radius + tickLen) * sinR, tick,
            )
        }
    }
}
