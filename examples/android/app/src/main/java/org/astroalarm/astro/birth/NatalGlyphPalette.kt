package org.astroalarm.astro.birth

import android.graphics.Canvas
import android.graphics.Paint
import org.astroalarm.astro.zodiac.ZodiacSign

/** Distinct hues for natal-wheel glyphs (Chart + homescreen disk). JVM-safe packed ARGB. */
object NatalGlyphPalette {
    val ASC: Int = rgb(255, 193, 7)
    val MC: Int = rgb(186, 104, 200)
    /** Imum Coeli — opposite MC; deep teal (not Uranus cyan). */
    val IC: Int = rgb(0, 121, 107)
    /** Descendant — opposite Asc; clear blue. */
    val DSC: Int = rgb(33, 150, 243)
    fun house(dark: Boolean): Int =
        if (dark) rgb(176, 160, 192) else rgb(90, 70, 110)

    fun body(b: NatalBody, dark: Boolean = true): Int {
        val base = when (b) {
            NatalBody.SUN -> rgb(255, 196, 48)
            NatalBody.MOON -> rgb(220, 228, 240)
            NatalBody.MERCURY -> rgb(158, 158, 158)
            NatalBody.VENUS -> rgb(255, 193, 7)
            NatalBody.MARS -> rgb(239, 83, 80)
            NatalBody.JUPITER -> rgb(255, 152, 0)
            NatalBody.SATURN -> rgb(161, 136, 127)
            NatalBody.URANUS -> rgb(0, 188, 212)
            NatalBody.NEPTUNE -> rgb(92, 107, 192)
        }
        val outer = b == NatalBody.JUPITER || b == NatalBody.SATURN ||
            b == NatalBody.URANUS || b == NatalBody.NEPTUNE
        return if (outer) argb(200, red(base), green(base), blue(base)) else base
    }

    /** Opaque RGB for uniqueness / live strokes. */
    fun bodyRgb(b: NatalBody): Int = rgb(red(body(b)), green(body(b)), blue(body(b)))

    fun zodiac(sign: ZodiacSign, dark: Boolean = true): Int {
        val base = when (sign) {
            ZodiacSign.Aries -> rgb(244, 81, 30)
            ZodiacSign.Leo -> rgb(255, 143, 0)
            ZodiacSign.Sagittarius -> rgb(255, 87, 34)
            ZodiacSign.Taurus -> rgb(67, 160, 71)
            ZodiacSign.Virgo -> rgb(102, 187, 106)
            ZodiacSign.Capricorn -> rgb(46, 125, 50)
            ZodiacSign.Gemini -> rgb(255, 214, 0)
            ZodiacSign.Libra -> rgb(171, 71, 188)
            ZodiacSign.Aquarius -> rgb(126, 87, 194)
            ZodiacSign.Cancer -> rgb(41, 182, 246)
            ZodiacSign.Scorpio -> rgb(3, 155, 229)
            ZodiacSign.Pisces -> rgb(0, 151, 167)
        }
        if (dark) return base
        return rgb(
            (red(base) * 0.75f).toInt().coerceIn(0, 255),
            (green(base) * 0.75f).toInt().coerceIn(0, 255),
            (blue(base) * 0.75f).toInt().coerceIn(0, 255),
        )
    }

    fun halo(dark: Boolean): Int =
        if (dark) argb(220, 18, 18, 18) else argb(200, 255, 255, 255)

    fun drawGlyphHalo(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        paint: Paint,
        dark: Boolean,
    ) {
        val fill = paint.color
        val style = paint.style
        paint.style = Paint.Style.FILL
        paint.color = halo(dark)
        val dx = paint.textSize * 0.06f
        canvas.drawText(text, x - dx, y, paint)
        canvas.drawText(text, x + dx, y, paint)
        canvas.drawText(text, x, y - dx, paint)
        canvas.drawText(text, x, y + dx, paint)
        paint.color = fill
        canvas.drawText(text, x, y, paint)
        paint.style = style
    }

    fun rgb(r: Int, g: Int, b: Int): Int =
        (0xFF shl 24) or (r shl 16) or (g shl 8) or b

    fun argb(a: Int, r: Int, g: Int, b: Int): Int =
        (a shl 24) or (r shl 16) or (g shl 8) or b

    fun red(c: Int): Int = (c shr 16) and 0xFF
    fun green(c: Int): Int = (c shr 8) and 0xFF
    fun blue(c: Int): Int = c and 0xFF
}
