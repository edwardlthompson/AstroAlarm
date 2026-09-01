package org.astroalarm.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import org.astroalarm.astro.sky.BodySky

/** Front-hemisphere solar/lunar ground tracks on the 3D globe. */
object GlobeGroundTracks {
    fun subLongitude(observerLon: Double, haRad: Double): Double = BodySky.subLongitude(observerLon, haRad)

    fun draw(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        r: Float,
        lat0: Double,
        lon0: Double,
        sunDec: Double,
        moonDec: Double,
        sunLon: Double,
        moonLon: Double,
    ) {
        if (r <= 2f) return
        canvas.save()
        canvas.clipPath(Path().apply { addCircle(cx, cy, r, Path.Direction.CW) })
        strokeParallel(canvas, cx, cy, r, lat0, lon0, sunDec, Color.rgb(255, 210, 48), (r * 0.045f).coerceIn(1.6f, 3.2f))
        strokeParallel(canvas, cx, cy, r, lat0, lon0, moonDec, Color.rgb(245, 250, 255), (r * 0.040f).coerceIn(1.4f, 2.8f))
        dot(canvas, cx, cy, r, lat0, lon0, sunDec, sunLon, Color.rgb(255, 220, 64))
        dot(canvas, cx, cy, r, lat0, lon0, moonDec, moonLon, Color.rgb(245, 250, 255))
        canvas.restore()
    }

    private fun strokeParallel(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        r: Float,
        lat0: Double,
        lon0: Double,
        dec: Double,
        color: Int,
        width: Float,
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = width
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        val path = Path()
        var drawing = false
        for (i in 0..72) {
            val lon = -180.0 + i * 5.0
            val (x, y, z) = SphereProjection.latLonToDisk(dec, lon, lat0, lon0)
            if (z < 0.0) {
                drawing = false
                continue
            }
            val px = cx + (x * r).toFloat()
            val py = cy - (y * r).toFloat()
            if (!drawing) {
                path.moveTo(px, py)
                drawing = true
            } else {
                path.lineTo(px, py)
            }
        }
        canvas.drawPath(path, paint)
    }

    private fun dot(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        r: Float,
        lat0: Double,
        lon0: Double,
        dec: Double,
        lon: Double,
        fill: Int,
    ) {
        val (x, y, z) = SphereProjection.latLonToDisk(dec, lon, lat0, lon0)
        if (z < 0.0) return
        val px = cx + (x * r).toFloat()
        val py = cy - (y * r).toFloat()
        val rad = (r * 0.09f).coerceIn(2.2f, 4.5f)
        canvas.drawCircle(px, py, rad, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(8, 10, 16) })
        canvas.drawCircle(px, py, rad * 0.62f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = fill })
    }
}
