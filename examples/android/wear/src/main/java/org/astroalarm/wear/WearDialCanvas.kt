package org.astroalarm.wear

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import java.time.ZonedDateTime

/** Draws the Daily 2D–styled analog dial (now-up hand + simple day/night pie). */
object WearDialCanvas {
    private val night = Color.rgb(10, 18, 32)
    private val day = Color.rgb(215, 232, 248)
    private val rim = Color.rgb(120, 144, 156)
    private val hand = Color.rgb(239, 83, 80)
    private val hub = Color.rgb(25, 118, 210)

    fun draw(canvas: Canvas, bounds: Rect, zdt: ZonedDateTime, ambient: Boolean) {
        val cx = bounds.exactCenterX()
        val cy = bounds.exactCenterY()
        val size = minOf(bounds.width(), bounds.height()).toFloat()
        val r = WearDialLayout.radius(size)
        val oval = RectF(cx - r, cy - r, cx + r, cy + r)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.style = Paint.Style.FILL
        paint.color = if (ambient) Color.BLACK else Color.rgb(14, 22, 38)
        canvas.drawCircle(cx, cy, r, paint)

        if (!ambient) {
            val nowAngle = WearCivilAngles.civilAngle(zdt)
            val (riseCivil, setCivil) = WearDayNightApprox.riseSetCivilAngles(zdt.toLocalDate(), zdt.zone)
            val aRise = WearCivilAngles.screenDeg(riseCivil, nowAngle)
            val aSet = WearCivilAngles.screenDeg(setCivil, nowAngle)
            paint.color = night
            canvas.drawArc(oval, aSet, WearDialLayout.sweepDeg(aSet, aRise), true, paint)
            paint.color = day
            canvas.drawArc(oval, aRise, WearDialLayout.sweepDeg(aRise, aSet), true, paint)
        }

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = (size * 0.012f).coerceIn(1.5f, 4f)
        paint.color = if (ambient) Color.GRAY else rim
        canvas.drawCircle(cx, cy, r, paint)

        val handDeg = WearCivilAngles.nowUpHandDeg()
        val rad = Math.toRadians(handDeg.toDouble())
        val x0 = cx + WearDialLayout.handInner(r) * kotlin.math.cos(rad).toFloat()
        val y0 = cy + WearDialLayout.handInner(r) * kotlin.math.sin(rad).toFloat()
        val x1 = cx + WearDialLayout.handOuter(r) * kotlin.math.cos(rad).toFloat()
        val y1 = cy + WearDialLayout.handOuter(r) * kotlin.math.sin(rad).toFloat()
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = (size * 0.028f).coerceIn(3f, 8f)
        paint.color = if (ambient) Color.WHITE else hand
        canvas.drawLine(x0, y0, x1, y1, paint)

        paint.style = Paint.Style.FILL
        paint.color = if (ambient) Color.LTGRAY else hub
        canvas.drawCircle(cx, cy, r * 0.08f, paint)
    }
}
