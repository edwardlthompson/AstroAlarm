package org.astroalarm.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/** Geographic rotation axis in the same orthographic camera as ground tracks. */
object GlobeAxis {
    fun poleDisk(lat0: Double, lon0: Double, north: Boolean): Triple<Double, Double, Double> =
        SphereProjection.latLonToDisk(if (north) 90.0 else -90.0, lon0, lat0, lon0)

    fun draw(canvas: Canvas, cx: Float, cy: Float, r: Float, lat0: Double, lon0: Double) {
        if (r <= 2f) return
        val (nx, ny, nz) = poleDisk(lat0, lon0, north = true)
        val (sx, sy, sz) = poleDisk(lat0, lon0, north = false)
        val nPx = cx + (nx * r).toFloat()
        val nPy = cy - (ny * r).toFloat()
        val sPx = cx + (sx * r).toFloat()
        val sPy = cy - (sy * r).toFloat()
        val dx = nPx - sPx
        val dy = nPy - sPy
        val len = kotlin.math.hypot(dx, dy).coerceAtLeast(1f)
        val ux = dx / len
        val uy = dy / len
        val stub = r * 0.18f
        val far = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(90, 230, 230, 240)
            strokeWidth = (r * 0.045f).coerceIn(1.2f, 2.4f)
            strokeCap = Paint.Cap.ROUND
        }
        val near = Paint(far).apply {
            color = Color.argb(220, 245, 245, 250)
            strokeWidth = far.strokeWidth * 1.15f
        }
        canvas.drawLine(sPx - ux * stub, sPy - uy * stub, nPx + ux * stub, nPy + uy * stub, far)
        if (nz >= 0.0 && sz >= 0.0) {
            canvas.drawLine(sPx, sPy, nPx, nPy, near)
        } else if (nz >= 0.0) {
            canvas.drawLine(cx, cy, nPx + ux * stub, nPy + uy * stub, near)
        } else if (sz >= 0.0) {
            canvas.drawLine(sPx - ux * stub, sPy - uy * stub, cx, cy, near)
        }
        val tick = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = (r * 0.16f).coerceIn(8f, 14f)
        }
        if (nz >= 0.0) canvas.drawText("N", nPx + ux * stub * 0.6f, nPy + uy * stub * 0.6f + tick.textSize * 0.35f, tick)
        if (sz >= 0.0) canvas.drawText("S", sPx - ux * stub * 0.6f, sPy - uy * stub * 0.6f + tick.textSize * 0.35f, tick)
    }
}
