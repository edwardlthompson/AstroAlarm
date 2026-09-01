package org.astroalarm.ui.solarterm

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import org.astroalarm.solarterm.SolarTerm
import org.astroalarm.solarterm.SolarTermPalette
import org.astroalarm.solarterm.SolarTermSnapshot
import org.astroalarm.solarterm.wrap360
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

data class SolarTermDrawRequest(
    val snapshot: SolarTermSnapshot,
    val labels: List<String>,
    val english: List<String>,
    val whenLocal: List<String>,
    val locationLabel: String,
    val countdown: String,
    val todayLine: String,
    val dark: Boolean,
    val localSeasons: Boolean,
    val southern: Boolean,
    val compact: Boolean,
)

object SolarTermWheelRenderer {
    fun render(req: SolarTermDrawRequest, size: Int): Bitmap {
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val cx = size / 2f
        val cy = size / 2f
        val outer = size * 0.48f
        val inner = size * 0.22f
        canvas.drawColor(SolarTermPalette.wheelBg(req.dark))
        val oval = RectF(cx - outer, cy - outer, cx + outer, cy + outer)
        val sector = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
        SolarTerm.entries.forEachIndexed { i, term ->
            sector.color = SolarTermPalette.sectorColor(term, req.localSeasons, req.southern, req.dark)
            canvas.drawArc(oval, startDeg(i), 15f, true, sector)
        }
        canvas.drawCircle(cx, cy, inner, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = SolarTermPalette.hub(req.dark)
        })
        val current = req.snapshot.current.term
        highlight(canvas, oval, current.ordinal, req.dark)
        needle(canvas, cx, cy, outer, req.snapshot.progress, current.ordinal, req.dark)
        drawLabels(canvas, cx, cy, (inner + outer) * 0.52f, req, size)
        drawHubText(canvas, cx, cy, inner, req, size)
        return bmp
    }

    fun sectorAt(x: Float, y: Float, size: Int): Int? {
        val cx = size / 2f
        val cy = size / 2f
        val dx = x - cx
        val dy = y - cy
        val r = kotlin.math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
        val outer = size * 0.48f
        val inner = size * 0.18f
        if (r < inner || r > outer) return null
        val deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
        val idx = (wrap360((deg + 90.0).toDouble()) / 15.0).toInt().coerceIn(0, 23)
        return idx
    }

    private fun startDeg(index: Int): Float = -90f + index * 15f

    private fun highlight(canvas: Canvas, oval: RectF, index: Int, dark: Boolean) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = oval.width() * 0.012f
            color = if (dark) 0xFFFFF4C2.toInt() else 0xFF1A237E.toInt()
        }
        canvas.drawArc(oval, startDeg(index), 15f, false, p)
    }

    private fun needle(canvas: Canvas, cx: Float, cy: Float, outer: Float, progress: Float, index: Int, dark: Boolean) {
        val ang = Math.toRadians((startDeg(index) + progress * 15f).toDouble())
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (dark) 0xFFFFE082.toInt() else 0xFFC62828.toInt()
            strokeWidth = outer * 0.018f
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawLine(cx, cy, cx + (outer * 0.92f * cos(ang)).toFloat(), cy + (outer * 0.92f * sin(ang)).toFloat(), p)
    }

    private fun drawLabels(canvas: Canvas, cx: Float, cy: Float, r: Float, req: SolarTermDrawRequest, size: Int) {
        val han = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = SolarTermPalette.ink(req.dark)
            textAlign = Paint.Align.CENTER
            textSize = size * if (req.compact) 0.028f else 0.032f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        val en = Paint(han).apply {
            textSize = size * if (req.compact) 0.018f else 0.022f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            color = SolarTermPalette.muted(req.dark)
        }
        SolarTerm.entries.forEachIndexed { i, term ->
            val mid = Math.toRadians((startDeg(i) + 7.5f).toDouble())
            val x = cx + r * cos(mid).toFloat()
            val y = cy + r * sin(mid).toFloat()
            canvas.drawText(term.glyph, x, y - han.textSize * 0.85f, han)
            canvas.drawText(req.labels.getOrElse(i) { term.hans }, x, y, han)
            if (!req.compact) {
                canvas.drawText(req.english.getOrElse(i) { "" }, x, y + en.textSize * 1.15f, en)
                val stamp = req.whenLocal.getOrElse(i) { "" }
                if (stamp.isNotEmpty()) canvas.drawText(stamp, x, y + en.textSize * 2.25f, en)
            }
        }
    }

    private fun drawHubText(canvas: Canvas, cx: Float, cy: Float, inner: Float, req: SolarTermDrawRequest, size: Int) {
        val title = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = SolarTermPalette.ink(req.dark)
            textAlign = Paint.Align.CENTER
            textSize = min(inner * 0.22f, size * 0.045f)
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        val sub = Paint(title).apply {
            textSize = title.textSize * 0.48f
            typeface = Typeface.SANS_SERIF
            color = SolarTermPalette.muted(req.dark)
        }
        val cur = req.snapshot.current.term
        val shownIdx = cur.localAlias(req.localSeasons, req.southern).ordinal
        canvas.drawText(req.labels.getOrElse(shownIdx) { cur.hans }, cx, cy - title.textSize * 0.2f, title)
        canvas.drawText(req.todayLine, cx, cy + sub.textSize * 1.1f, sub)
        canvas.drawText(req.countdown, cx, cy + sub.textSize * 2.4f, sub)
        if (!req.compact) canvas.drawText(req.locationLabel, cx, cy + sub.textSize * 3.7f, sub)
    }
}
