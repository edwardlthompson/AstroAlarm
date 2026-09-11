package org.astroalarm.astro.birth

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import org.astroalarm.astro.zodiac.ZodiacSign

/** Native Canvas natal wheel (Asc-left). Keep ≤150 lines. */
object NatalWheelRenderer {
    const val ASPECT_MIN_SIZE = 280

    fun renderBitmap(
        size: Int,
        chart: NatalChart,
        sky: NatalSkySnapshot? = NatalSkySnapshot.now(),
        eventLinks: List<NatalEventLink> = emptyList(),
        dark: Boolean = true,
    ): Bitmap {
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(if (dark) 0xFF121212.toInt() else 0xFFF5F5F5.toInt())
        val links = if (sky != null) {
            eventLinks.ifEmpty { NatalEventLinks.active(chart, sky) }
        } else {
            eventLinks
        }
        draw(canvas, size, chart, sky, links, dark = dark)
        return bmp
    }

    fun draw(
        canvas: Canvas,
        size: Int,
        chart: NatalChart,
        sky: NatalSkySnapshot? = null,
        eventLinks: List<NatalEventLink> = emptyList(),
        showSun: Boolean = true,
        showMoon: Boolean = true,
        showMercury: Boolean = true,
        focusBody: NatalBody? = null,
        dark: Boolean = true,
    ) {
        val cx = size / 2f
        val cy = size / 2f
        val rOuter = size * 0.46f
        val rHouse = size * 0.38f
        val rPlanet = size * 0.30f
        val rLive = size * 0.24f
        val asc = NatalWheelLayout.frameAscLon(chart)
        val ring = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = size * 0.01f
            color = if (dark) Color.WHITE else Color.DKGRAY
        }
        canvas.drawCircle(cx, cy, rOuter, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (dark) Color.argb(40, 255, 255, 255) else Color.argb(30, 0, 0, 0)
        })
        canvas.drawCircle(cx, cy, rOuter, ring)
        canvas.drawCircle(cx, cy, rHouse, ring)
        val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = size * 0.028f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        for (sign in ZodiacSign.entries) {
            val mid = sign.startLongitudeDeg + 15.0
            val (x, y) = NatalWheelLayout.xy(cx, cy, rOuter * 0.92f, mid, asc)
            text.color = NatalGlyphPalette.zodiac(sign, dark)
            NatalGlyphPalette.drawGlyphHalo(
                canvas, sign.symbol, x, y + text.textSize / 3f, text, dark,
            )
            val (x0, y0) = NatalWheelLayout.xy(cx, cy, rHouse, sign.startLongitudeDeg, asc)
            val (x1, y1) = NatalWheelLayout.xy(cx, cy, rOuter, sign.startLongitudeDeg, asc)
            canvas.drawLine(x0, y0, x1, y1, ring)
        }
        val housePaint = Paint(text).apply {
            textSize = size * 0.032f
            color = NatalGlyphPalette.house(dark)
        }
        NatalWheelHousePaint.drawNumbers(canvas, cx, cy, rHouse, chart, housePaint)
        NatalWheelLayers.drawAngles(canvas, cx, cy, rOuter, rPlanet, size, chart, asc)
        if (size >= ASPECT_MIN_SIZE) {
            NatalWheelLayers.drawAspects(canvas, chart, focusBody, eventLinks, cx, cy, rPlanet, asc, size)
        }
        NatalWheelLayers.drawNatalPlanets(canvas, chart, cx, cy, rPlanet, asc, size, dark)
        if (sky != null) {
            NatalWheelLayers.drawLive(
                canvas, sky, eventLinks, showSun, showMoon, showMercury,
                cx, cy, rLive, rPlanet, asc, size, dark,
            )
        }
    }
}
