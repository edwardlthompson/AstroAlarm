package org.astroalarm.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader

/** Earth-view shaded Moon on the Daily 3D transit ring. */
object Astro3DMoon {
    fun draw(canvas: Canvas, x: Float, y: Float, size: Int, elongationDeg: Double, moon: Bitmap?) {
        val r = size * 0.055f
        val halo = size * 0.065f
        canvas.drawCircle(x, y, halo, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                x, y, halo,
                intArrayOf(Color.argb(90, 240, 245, 255), Color.TRANSPARENT),
                floatArrayOf(0.35f, 1f), Shader.TileMode.CLAMP,
            )
        })
        val cam = MoonShade.earthView(elongationDeg)
        EarthGlobeRenderer.drawGlobe(
            canvas, x, y, r, cam.lat0, cam.lon0, moon, false, cam.sunDec, cam.subsolarLon, "moon",
        )
    }
}
