package org.astroalarm.astro.birth

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/** Angle spokes, aspects, natal glyphs, live overlay for [NatalWheelRenderer]. */
internal object NatalWheelLayers {
    fun drawAngles(
        canvas: Canvas, cx: Float, cy: Float, rOuter: Float, rPlanet: Float,
        size: Int, chart: NatalChart, asc: Double,
    ) {
        if (chart.ascendant != null) {
            val spoke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = NatalGlyphPalette.ASC
                strokeWidth = size * 0.012f
            }
            val (ax, ay) = NatalWheelLayout.xy(cx, cy, rPlanet * 0.2f, asc, asc)
            val (bx, by) = NatalWheelLayout.xy(cx, cy, rOuter, asc, asc)
            canvas.drawLine(ax, ay, bx, by, spoke)
            val dsc = NatalAspectMath.wrap360(asc + 180.0)
            val dscPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = NatalGlyphPalette.DSC
                strokeWidth = size * 0.012f
            }
            val (dx0, dy0) = NatalWheelLayout.xy(cx, cy, rPlanet * 0.2f, dsc, asc)
            val (dx1, dy1) = NatalWheelLayout.xy(cx, cy, rOuter, dsc, asc)
            canvas.drawLine(dx0, dy0, dx1, dy1, dscPaint)
        }
        val mc = chart.midheaven?.longitudeDeg ?: return
        val mcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = NatalGlyphPalette.MC
            strokeWidth = size * 0.008f
        }
        val (ax, ay) = NatalWheelLayout.xy(cx, cy, rPlanet * 0.2f, mc, asc)
        val (bx, by) = NatalWheelLayout.xy(cx, cy, rOuter, mc, asc)
        canvas.drawLine(ax, ay, bx, by, mcPaint)
        val ic = NatalAspectMath.wrap360(mc + 180.0)
        val icPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = NatalGlyphPalette.IC
            strokeWidth = size * 0.008f
        }
        val (ix0, iy0) = NatalWheelLayout.xy(cx, cy, rPlanet * 0.2f, ic, asc)
        val (ix1, iy1) = NatalWheelLayout.xy(cx, cy, rOuter, ic, asc)
        canvas.drawLine(ix0, iy0, ix1, iy1, icPaint)
    }

    fun drawAspects(
        canvas: Canvas, chart: NatalChart, focusBody: NatalBody?, eventLinks: List<NatalEventLink>,
        cx: Float, cy: Float, rPlanet: Float, asc: Double, size: Int,
    ) {
        val aspectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = size * 0.004f
            alpha = if (eventLinks.isEmpty()) 120 else 60
        }
        for (link in NatalAspectMath.links(chart)) {
            if (focusBody != null && link.a != focusBody && link.b != focusBody) continue
            aspectPaint.color = aspectColor(link.aspect)
            val pa = chart.planets[link.a] ?: continue
            val pb = chart.planets[link.b] ?: continue
            val (x1, y1) = NatalWheelLayout.xy(cx, cy, rPlanet, pa.longitudeDeg, asc)
            val (x2, y2) = NatalWheelLayout.xy(cx, cy, rPlanet, pb.longitudeDeg, asc)
            canvas.drawLine(x1, y1, x2, y2, aspectPaint)
        }
    }

    fun drawNatalPlanets(
        canvas: Canvas, chart: NatalChart, cx: Float, cy: Float, rPlanet: Float,
        asc: Double, size: Int, dark: Boolean,
    ) {
        val bodies = chart.planets.keys.toList()
        val lons = bodies.map { chart.planets[it]!!.longitudeDeg }
        val factors = NatalWheelStellium.radiusFactors(lons)
        val glyph = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = size * 0.045f
            textAlign = Paint.Align.CENTER
        }
        bodies.forEachIndexed { i, body ->
            glyph.color = NatalGlyphPalette.body(body, dark)
            val r = rPlanet * factors[i]
            val (x, y) = NatalWheelLayout.xy(cx, cy, r, chart.planets[body]!!.longitudeDeg, asc)
            NatalGlyphPalette.drawGlyphHalo(
                canvas, bodyGlyph(body), x, y + glyph.textSize / 3f, glyph, dark,
            )
        }
    }

    fun drawLive(
        canvas: Canvas, sky: NatalSkySnapshot, eventLinks: List<NatalEventLink>,
        showSun: Boolean, showMoon: Boolean, showMercury: Boolean,
        cx: Float, cy: Float, rLive: Float, rPlanet: Float, asc: Double, size: Int,
        dark: Boolean = true,
    ) {
        val live = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = size * 0.006f
            textSize = size * 0.04f
            textAlign = Paint.Align.CENTER
        }
        fun liveAt(on: Boolean, lon: Double, body: NatalBody, g: String) {
            if (!on) return
            live.color = NatalGlyphPalette.bodyRgb(body)
            val (x, y) = NatalWheelLayout.xy(cx, cy, rLive, lon, asc)
            canvas.drawCircle(x, y, size * 0.022f, live)
            NatalGlyphPalette.drawGlyphHalo(canvas, g, x, y + live.textSize / 3f, live, dark)
        }
        liveAt(showSun, sky.sun, NatalBody.SUN, "☉")
        liveAt(showMoon, sky.moon, NatalBody.MOON, "☽")
        liveAt(showMercury, sky.mercury, NatalBody.MERCURY, "☿")
        val bright = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(255, 64, 129)
            strokeWidth = size * 0.01f
            alpha = 200
        }
        for (ev in eventLinks) {
            val (x1, y1) = NatalWheelLayout.xy(cx, cy, rLive, ev.liveLon, asc)
            val (x2, y2) = NatalWheelLayout.xy(cx, cy, rPlanet, ev.targetLon, asc)
            canvas.drawLine(x1, y1, x2, y2, bright)
        }
    }

    private fun aspectColor(a: WheelAspect): Int = when (a) {
        WheelAspect.Conjunction -> Color.rgb(255, 235, 59)
        WheelAspect.Sextile -> Color.rgb(129, 199, 132)
        WheelAspect.Square -> Color.rgb(239, 83, 80)
        WheelAspect.Trine -> Color.rgb(66, 165, 245)
        WheelAspect.Opposition -> Color.rgb(171, 71, 188)
    }

    private fun bodyGlyph(b: NatalBody): String = when (b) {
        NatalBody.SUN -> "☉"
        NatalBody.MOON -> "☽"
        NatalBody.MERCURY -> "☿"
        NatalBody.VENUS -> "♀"
        NatalBody.MARS -> "♂"
        NatalBody.JUPITER -> "♃"
        NatalBody.SATURN -> "♄"
        NatalBody.URANUS -> "♅"
        NatalBody.NEPTUNE -> "♆"
    }
}
