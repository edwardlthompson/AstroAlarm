package org.astroalarm.ui.sol

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import org.astroalarm.astro.birth.BirthProfile
import org.astroalarm.astro.birth.BirthTimeZones
import org.astroalarm.astro.birth.NatalAspectMath
import org.astroalarm.astro.birth.WheelAspect
import org.astroalarm.sol.PlanetBody
import org.astroalarm.sol.PlanetKepler
import org.astroalarm.sol.PlanetState
import kotlin.math.abs
import kotlin.math.max

/** Heliocentric birth-epoch ghost planets (incl. Earth) + muted aspect chords. */
object SolNatalOverlay {
    private const val ORB = 7.0

    fun draw(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        pxPerAu: Float,
        profile: BirthProfile?,
        size: Int,
    ) {
        val instant = profile?.let { BirthTimeZones.toInstant(it) } ?: return
        val pts = PlanetBody.entries.map { body ->
            body to PlanetKepler.state(body, instant)
        }
        drawAspects(canvas, cx, cy, pxPerAu, pts, size)
        val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(2f, size * 0.004f)
        }
        val fill = Paint(Paint.ANTI_ALIAS_FLAG)
        for ((body, st) in pts) {
            val x = cx + (st.x * pxPerAu).toFloat()
            val y = cy - (st.y * pxPerAu).toFloat()
            val r = max(3f, 0.012f * size) * if (body == PlanetBody.EARTH) 1.35f else 1f
            val base = ghostColor(body)
            fill.color = Color.argb(0x66, Color.red(base), Color.green(base), Color.blue(base))
            stroke.color = Color.argb(0xAA, Color.red(base), Color.green(base), Color.blue(base))
            canvas.drawCircle(x, y, r, fill)
            canvas.drawCircle(x, y, r, stroke)
        }
    }

    private fun drawAspects(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        pxPerAu: Float,
        pts: List<Pair<PlanetBody, PlanetState>>,
        size: Int,
    ) {
        val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(1.5f, size * 0.003f)
        }
        for (i in pts.indices) {
            for (j in i + 1 until pts.size) {
                val asp = aspectOf(pts[i].second.helioLon, pts[j].second.helioLon) ?: continue
                val rgb = aspectRgb(asp)
                line.color = Color.argb(0x58, Color.red(rgb), Color.green(rgb), Color.blue(rgb))
                val a = pts[i].second
                val b = pts[j].second
                canvas.drawLine(
                    cx + (a.x * pxPerAu).toFloat(),
                    cy - (a.y * pxPerAu).toFloat(),
                    cx + (b.x * pxPerAu).toFloat(),
                    cy - (b.y * pxPerAu).toFloat(),
                    line,
                )
            }
        }
    }

    private fun aspectOf(lonA: Double, lonB: Double): WheelAspect? {
        val diff = abs(NatalAspectMath.wrap180(lonA - lonB))
        for (asp in WheelAspect.entries) {
            if (abs(diff - asp.degrees) <= ORB) return asp
        }
        return null
    }

    private fun aspectRgb(a: WheelAspect): Int = when (a) {
        WheelAspect.Conjunction -> Color.rgb(255, 235, 59)
        WheelAspect.Sextile -> Color.rgb(129, 199, 132)
        WheelAspect.Square -> Color.rgb(239, 83, 80)
        WheelAspect.Trine -> Color.rgb(66, 165, 245)
        WheelAspect.Opposition -> Color.rgb(171, 71, 188)
    }

    private fun ghostColor(body: PlanetBody): Int = when (body) {
        PlanetBody.MERCURY -> Color.rgb(180, 180, 180)
        PlanetBody.VENUS -> Color.rgb(255, 193, 7)
        PlanetBody.EARTH -> Color.rgb(66, 165, 245)
        PlanetBody.MARS -> Color.rgb(239, 83, 80)
        PlanetBody.JUPITER -> Color.rgb(255, 167, 38)
        PlanetBody.SATURN -> Color.rgb(161, 136, 127)
        PlanetBody.URANUS -> Color.rgb(77, 208, 225)
        PlanetBody.NEPTUNE -> Color.rgb(92, 107, 192)
    }
}
