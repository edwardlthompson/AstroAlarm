package org.astroalarm.ui.solarterm

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import org.astroalarm.solarterm.SolarTerm
import kotlin.math.cos
import kotlin.math.sin

object SolarTermRadialLabels {
    fun draw(
        canvas: Canvas, cx: Float, cy: Float, inner: Float, outer: Float,
        req: SolarTermDrawRequest, size: Int,
    ) {
        val band = (outer - inner).coerceAtLeast(1f)
        val emojiR = if (req.compact) (inner + outer) * 0.5f else inner + band * 0.08f
        val glyph = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = org.astroalarm.solarterm.SolarTermPalette.ink(req.dark)
            textAlign = Paint.Align.CENTER
            textSize = size * if (req.compact) 0.030f else 0.028f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        val gAdj = -(glyph.fontMetrics.ascent + glyph.fontMetrics.descent) / 2f
        SolarTerm.entries.forEachIndexed { i, term ->
            val mid = Math.toRadians(SolarTermWheelRenderer.midDeg(i).toDouble())
            val ex = cx + emojiR * cos(mid).toFloat()
            val ey = cy + emojiR * sin(mid).toFloat()
            canvas.drawText(term.glyph, ex, ey + gAdj, glyph)
            if (!req.compact) {
                radialPair(canvas, cx, cy, inner, outer, i, req, size)
            }
        }
    }

    private fun radialPair(
        canvas: Canvas, cx: Float, cy: Float, inner: Float, outer: Float,
        i: Int, req: SolarTermDrawRequest, size: Int,
    ) {
        val name = req.english.getOrElse(i) { "" }
        val stamp = req.whenLocal.getOrElse(i) { "" }
        val band = (outer - inner).coerceAtLeast(1f)
        val emojiKeep = inner + band * 0.08f + size * 0.022f
        val rOut = outer - 2f
        val rIn = emojiKeep.coerceAtMost(rOut - 8f)
        along(canvas, cx, cy, rOut, rIn, nameAng(i), name, size, req.dark, NAME_FRAC)
        along(canvas, cx, cy, rOut, rIn, dateAng(i), stamp, size, req.dark, DATE_FRAC)
    }

    internal fun nameAng(i: Int): Float {
        val start = SolarTermWheelRenderer.startDeg(i)
        val mid = SolarTermWheelRenderer.midDeg(i)
        return start + kotlin.math.sign(mid - start) * INSET
    }

    internal fun dateAng(i: Int): Float {
        val edge = SolarTermWheelRenderer.startDeg(i) + SWEEP
        val mid = SolarTermWheelRenderer.midDeg(i)
        return edge + kotlin.math.sign(mid - edge) * INSET
    }

    internal fun angDist(a: Float, b: Float): Float {
        var d = (a - b) % 360f
        if (d > 180f) d -= 360f
        if (d < -180f) d += 360f
        return kotlin.math.abs(d)
    }

    private fun along(
        canvas: Canvas, cx: Float, cy: Float, rOut: Float, rIn: Float, angDeg: Float,
        text: String, size: Int, dark: Boolean, frac: Float,
    ) {
        if (text.isEmpty()) return
        val rad = Math.toRadians(angDeg.toDouble())
        val x = cx + rOut * cos(rad).toFloat()
        val y = cy + rOut * sin(rad).toFloat()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = org.astroalarm.solarterm.SolarTermPalette.ink(dark)
            textAlign = Paint.Align.LEFT
            textSize = size * frac
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        canvas.save()
        canvas.translate(x, y)
        canvas.rotate(angDeg + 180f)
        val maxW = (rOut - rIn).coerceAtLeast(8f)
        val lines = wrapLines(text, maxW, { paint.measureText(it) }, 3)
        val lh = paint.textSize * 1.12f
        val y0 = -((lines.size - 1) * lh) / 2f
        lines.forEachIndexed { i, line ->
            canvas.drawText(line, 2f, y0 + i * lh + paint.textSize * 0.35f, paint)
        }
        canvas.restore()
    }

    internal fun wrapLines(
        text: String, maxW: Float, measure: (String) -> Float, maxLines: Int = 3,
    ): List<String> {
        val t = text.trim()
        if (t.isEmpty()) return emptyList()
        if (measure(t) <= maxW) return listOf(t)
        val lines = ArrayList<String>(maxLines)
        var rest = t
        while (rest.isNotEmpty() && lines.size < maxLines) {
            if (lines.size == maxLines - 1 || measure(rest) <= maxW) {
                var last = rest
                while (last.length > 1 && measure(last) > maxW) last = last.dropLast(1)
                lines.add(last)
                break
            }
            var cut = rest.length
            while (cut > 1 && measure(rest.take(cut)) > maxW) cut--
            val sp = rest.lastIndexOf(' ', startIndex = (cut - 1).coerceAtLeast(0))
            if (sp >= 1) cut = sp
            val piece = rest.take(cut).trim()
            if (piece.isEmpty()) {
                lines.add(rest.take(1))
                rest = rest.drop(1).trim()
            } else {
                lines.add(piece)
                rest = rest.drop(cut).trim()
            }
        }
        return lines
    }

    private const val SWEEP = -15f
    private const val INSET = 3f
    internal const val NAME_FRAC = 0.022f
    internal const val DATE_FRAC = 0.019f
}
