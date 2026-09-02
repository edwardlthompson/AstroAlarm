package org.astroalarm.ui.solarterm

import android.graphics.Canvas
import android.graphics.Paint
import org.astroalarm.solarterm.SolarTermLayout
import org.astroalarm.solarterm.wrap360
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

object SolarTermPoleLabels {
    private const val JUNE = 90.0
    private const val DEC = 270.0

    fun draw(
        canvas: Canvas, cx: Float, cy: Float, inner: Float, perihelionLon: Double,
        rot: Float, dark: Boolean, ex: Float, ey: Float, er: Float,
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            textSize = letterSize(inner)
        }
        letter(canvas, cx, cy, inner, perihelionLon, rot, DEC, north = true, dark, paint, ex, ey, er)
        letter(canvas, cx, cy, inner, perihelionLon, rot, JUNE, north = true, dark, paint, ex, ey, er)
        letter(canvas, cx, cy, inner, perihelionLon, rot, DEC, north = false, dark, paint, ex, ey, er)
        letter(canvas, cx, cy, inner, perihelionLon, rot, JUNE, north = false, dark, paint, ex, ey, er)
    }

    internal fun letterSize(inner: Float): Float = (inner * 0.09f).coerceIn(18f, 32f)

    private fun letter(
        canvas: Canvas, cx: Float, cy: Float, inner: Float, peri: Double, rot: Float,
        lon: Double, north: Boolean, dark: Boolean, paint: Paint, ex: Float, ey: Float, er: Float,
    ) {
        var useLon = lon
        var (x, y) = xy(cx, cy, inner, peri, rot, useLon, north)
        if (hypot((x - ex).toDouble(), (y - ey).toDouble()) < 1.4 * er) {
            useLon = wrap360(lon + 8.0)
            val n = xy(cx, cy, inner, peri, rot, useLon, north)
            x = n.first
            y = n.second
        }
        paint.color = if (north) {
            if (dark) 0xFF66BB6A.toInt() else 0xFF2E7D32.toInt()
        } else {
            if (dark) 0xFFE53935.toInt() else 0xFFC62828.toInt()
        }
        canvas.drawText(if (north) "N" else "S", x, y + paint.textSize * 0.35f, paint)
    }

    internal fun xy(
        cx: Float, cy: Float, inner: Float, peri: Double, rot: Float, lon: Double, north: Boolean,
    ): Pair<Float, Float> {
        val r = letterR(inner, peri, lon, north)
        val ang = SolarTermLayout.canvasDeg(lon) + rot
        val rad = Math.toRadians(ang.toDouble())
        return (cx + r * cos(rad).toFloat()) to (cy + r * sin(rad).toFloat())
    }

    internal fun letterR(inner: Float, peri: Double, lon: Double, north: Boolean): Float {
        val a = inner * 0.58f
        val amp = inner * 0.07f
        val textSize = letterSize(inner)
        val pad = textSize * 0.6f
        val stroke = inner * 0.012f
        val nu = wrap360(lon - peri)
        val polar = SolarTermAxisOverlay.polar(a, nu)
        val green = polar - amp * cos(Math.toRadians(lon - JUNE)).toFloat()
        val red = polar + amp * cos(Math.toRadians(lon - JUNE)).toFloat()
        val winter = wrap360(lon) > 180.0
        val trace = if (north) green else red
        // N: outside green in winter, inside in summer. S: inside red in winter, outside in summer.
        val outside = if (north) winter else !winter
        var r = if (outside) trace + pad else trace - pad
        val minOff = stroke / 2f + textSize * 0.35f
        if (abs(r - trace) < minOff) {
            r += if (r >= trace) 0.4f * textSize else -0.4f * textSize
        }
        return r
    }

    internal fun traceR(inner: Float, peri: Double, lon: Double, north: Boolean): Float {
        val a = inner * 0.58f
        val amp = inner * 0.07f
        val nu = wrap360(lon - peri)
        val polar = SolarTermAxisOverlay.polar(a, nu)
        val green = polar - amp * cos(Math.toRadians(lon - JUNE)).toFloat()
        val red = polar + amp * cos(Math.toRadians(lon - JUNE)).toFloat()
        return if (north) green else red
    }
}
