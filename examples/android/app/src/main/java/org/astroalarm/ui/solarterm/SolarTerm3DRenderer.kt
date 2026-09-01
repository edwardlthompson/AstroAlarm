package org.astroalarm.ui.solarterm

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import org.astroalarm.solarterm.SolarTerm
import org.astroalarm.solarterm.SolarTermPalette
import org.astroalarm.widget.EarthGlobeRenderer
import kotlin.math.cos
import kotlin.math.sin

object SolarTerm3DRenderer {
    fun render(
        req: SolarTermDrawRequest,
        size: Int,
        yawDeg: Float,
        earth: Bitmap? = null,
        lat: Double = 40.0,
        lon: Double = 0.0,
    ): Bitmap {
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val cx = size / 2f
        val cy = size / 2f
        canvas.drawColor(SolarTermPalette.wheelBg(req.dark))
        val rx = size * 0.40f
        val ry = size * 0.16f
        val markers = SolarTerm.entries.mapIndexed { i, term ->
            val a = Math.toRadians(i * 15.0 + yawDeg - 90.0)
            val x = cx + rx * cos(a).toFloat()
            val y = cy + ry * sin(a).toFloat()
            val depth = sin(a)
            Marker(term, i, x, y, depth)
        }
        val ring = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = size * 0.012f
            color = 0x88FFD54F.toInt()
        }
        canvas.drawOval(cx - rx, cy - ry, cx + rx, cy + ry, ring)
        markers.filter { it.depth < 0 }.forEach { drawMarker(canvas, it, req, size, false) }
        EarthGlobeRenderer.drawGlobe(canvas, cx, cy, size * 0.11f, lat, lon, earth, true)
        markers.filter { it.depth >= 0 }.forEach { drawMarker(canvas, it, req, size, true) }
        nowMarker(canvas, req, cx, cy, rx, ry, yawDeg, size)
        hubCaption(canvas, cx, cy + ry + size * 0.10f, req, size)
        return bmp
    }

    fun markerAt(x: Float, y: Float, size: Int, yawDeg: Float): Int? {
        val cx = size / 2f
        val cy = size / 2f
        val rx = size * 0.40f
        val ry = size * 0.16f
        var best: Pair<Int, Float>? = null
        SolarTerm.entries.forEachIndexed { i, _ ->
            val a = Math.toRadians(i * 15.0 + yawDeg - 90.0)
            val mx = cx + rx * cos(a).toFloat()
            val my = cy + ry * sin(a).toFloat()
            val d2 = (x - mx) * (x - mx) + (y - my) * (y - my)
            val thresh = size * 0.06f
            if (d2 <= thresh * thresh && (best == null || d2 < best!!.second)) best = i to d2
        }
        return best?.first
    }

    private fun drawMarker(canvas: Canvas, m: Marker, req: SolarTermDrawRequest, size: Int, front: Boolean) {
        val color = SolarTermPalette.sectorColor(m.term, req.localSeasons, req.southern, req.dark)
        val r = size * if (front) 0.022f else 0.016f
        canvas.drawCircle(m.x, m.y, r, Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color })
        if (!front) return
        val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = SolarTermPalette.ink(req.dark)
            textAlign = Paint.Align.CENTER
            textSize = size * 0.028f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        canvas.drawText(req.labels.getOrElse(m.index) { m.term.hans }, m.x, m.y - r * 2.2f, text)
    }

    private fun nowMarker(canvas: Canvas, req: SolarTermDrawRequest, cx: Float, cy: Float, rx: Float, ry: Float, yaw: Float, size: Int) {
        val idx = req.snapshot.current.term.ordinal
        val a = Math.toRadians(idx * 15.0 + req.snapshot.progress * 15.0 + yaw - 90.0)
        val x = cx + rx * cos(a).toFloat()
        val y = cy + ry * sin(a).toFloat()
        canvas.drawCircle(x, y, size * 0.018f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFFFF59D.toInt() })
        canvas.drawLine(cx, cy, x, y, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xAAFFE082.toInt()
            strokeWidth = size * 0.006f
        })
    }

    private fun hubCaption(canvas: Canvas, cx: Float, y: Float, req: SolarTermDrawRequest, size: Int) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = SolarTermPalette.muted(req.dark)
            textAlign = Paint.Align.CENTER
            textSize = size * 0.032f
        }
        canvas.drawText(req.countdown, cx, y, p)
        canvas.drawText(req.locationLabel, cx, y + p.textSize * 1.3f, p)
    }

    private data class Marker(val term: SolarTerm, val index: Int, val x: Float, val y: Float, val depth: Double)
}
