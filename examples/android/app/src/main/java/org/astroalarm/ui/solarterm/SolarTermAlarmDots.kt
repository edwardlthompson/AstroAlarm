package org.astroalarm.ui.solarterm

import android.graphics.Canvas
import android.graphics.Paint
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.solarterm.SolarTerm
import kotlin.math.cos
import kotlin.math.sin

/** Red armed-jieqi marks on the term ray (solstice/equinox spokes), inside the hub. */
object SolarTermAlarmDots {
    fun ordsOf(alarms: List<AstroAlarm>, show: Boolean = true): Set<Int> {
        if (!show) return emptySet()
        return alarms.asSequence().filter { it.enabled }.mapNotNull { ordOf(it.target) }.toSet()
    }

    fun ordOf(target: AlarmTarget): Int? = when (target) {
        is AlarmTarget.SolarTerm -> target.term.ordinal
        is AlarmTarget.Solar -> seasonOrd(target.event)
        else -> null
    }

    fun lineDeg(ord: Int): Float = SolarTermWheelRenderer.startDeg(ord)

    fun ringR(hubFill: Float): Float = hubFill * 0.92f

    fun dotRad(size: Int): Float = (size * 0.018f).coerceIn(4f, 10f)

    fun xy(cx: Float, cy: Float, hubFill: Float, rot: Float, ord: Int): Pair<Float, Float> {
        val ang = Math.toRadians(lineDeg(ord).toDouble())
        val rotR = Math.toRadians(rot.toDouble())
        val r = ringR(hubFill)
        val lx = r * cos(ang)
        val ly = r * sin(ang)
        val x = cx + (lx * cos(rotR) - ly * sin(rotR)).toFloat()
        val y = cy + (lx * sin(rotR) + ly * cos(rotR)).toFloat()
        return x to y
    }

    fun draw(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        hubFill: Float,
        rot: Float,
        alarmOrds: Set<Int>,
        size: Int,
    ) {
        if (alarmOrds.isEmpty()) return
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFE53935.toInt() }
        val rad = dotRad(size)
        alarmOrds.forEach { ord ->
            val (x, y) = xy(cx, cy, hubFill, rot, ord)
            canvas.drawCircle(x, y, rad, p)
        }
    }

    private fun seasonOrd(event: SolarEventType): Int? = when (event) {
        SolarEventType.JuneSolstice -> SolarTerm.XIAZHI.ordinal
        SolarEventType.DecemberSolstice -> SolarTerm.DONGZHI.ordinal
        SolarEventType.MarchEquinox -> SolarTerm.CHUNFEN.ordinal
        SolarEventType.SeptemberEquinox -> SolarTerm.QIUFEN.ordinal
        else -> null
    }
}
