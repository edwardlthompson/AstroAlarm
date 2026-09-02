package org.astroalarm.ui.solarterm

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import org.astroalarm.solarterm.SolarTermLayout
import org.astroalarm.solarterm.wrap360
import kotlin.math.cos
import kotlin.math.sin

object SolarTermAxisOverlay {
    const val E_VIS = 0.22
    private const val JUNE_LON = 90.0

    fun polar(a: Float, nuDeg: Double): Float {
        val c = cos(Math.toRadians(nuDeg))
        return (a * (1.0 - E_VIS * E_VIS) / (1.0 + E_VIS * c)).toFloat()
    }

    fun northAxisDeg(): Float = SolarTermLayout.canvasDeg(JUNE_LON) + 180f

    fun northSunwardAlign(nowLon: Double): Double {
        val e = Math.toRadians(SolarTermLayout.canvasDeg(nowLon).toDouble())
        val n = Math.toRadians(northAxisDeg().toDouble())
        return -kotlin.math.cos(n - e)
    }

    fun spiralR(a: Float, nuDeg: Double, perihelionLon: Double, amp: Float): Float {
        val lon = wrap360(perihelionLon + nuDeg)
        return polar(a, nuDeg) - amp * cos(Math.toRadians(lon - JUNE_LON)).toFloat()
    }

    fun drawSpiral(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        inner: Float,
        perihelionLon: Double,
        rot: Float,
        dark: Boolean,
    ) {
        val amp = inner * 0.07f
        val a = inner * 0.58f
        strokeSpiral(canvas, cx, cy, a, perihelionLon, rot, amp, -1.0, if (dark) 0xAA66BB6A.toInt() else 0xCC2E7D32.toInt(), inner)
        strokeSpiral(canvas, cx, cy, a, perihelionLon, rot, amp, 1.0, if (dark) 0xAAE53935.toInt() else 0xCCC62828.toInt(), inner)
    }

    private fun strokeSpiral(
        canvas: Canvas, cx: Float, cy: Float, a: Float, perihelionLon: Double, rot: Float,
        amp: Float, sign: Double, color: Int, inner: Float,
    ) {
        val spiral = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = inner * 0.012f
            this.color = color
        }
        val path = Path()
        for (i in 0..72) {
            val nu = i * 5.0
            val ang = SolarTermLayout.canvasDeg(wrap360(perihelionLon + nu)) + rot
            val r = polar(a, nu) + (amp * sign * cos(Math.toRadians(wrap360(perihelionLon + nu) - JUNE_LON))).toFloat()
            val rad = Math.toRadians(ang.toDouble()).toFloat()
            val x = cx + r * cos(rad)
            val y = cy + r * sin(rad)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, spiral)
    }
}
