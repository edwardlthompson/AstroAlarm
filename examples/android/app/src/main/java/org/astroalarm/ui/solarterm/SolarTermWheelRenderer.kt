package org.astroalarm.ui.solarterm

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import org.astroalarm.solarterm.SolarTerm
import org.astroalarm.solarterm.SolarTermLayout
import org.astroalarm.solarterm.SolarTermPalette
import org.astroalarm.solarterm.SolarTermSnapshot
import org.astroalarm.solarterm.wrap360
import java.time.Instant
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class SolarTermDrawRequest(
    val snapshot: SolarTermSnapshot,
    val english: List<String>,
    val whenLocal: List<String>,
    val locationLabel: String,
    val countdown: String,
    val todayLine: String,
    val dark: Boolean,
    val southern: Boolean,
    val compact: Boolean,
    val nowLon: Double,
    val perihelionLon: Double,
    val userLat: Double?,
    val userLon: Double?,
    val now: Instant,
    val alarmOrds: Set<Int> = emptySet(),
)

object SolarTermWheelRenderer {
    fun outerFrac(): Float = 0.48f

    fun innerFrac(compact: Boolean): Float {
        if (!compact) return 0.26f
        val band = maxOf(0.042f, 0.030f + 2f * 0.006f)
        return outerFrac() - band
    }

    fun render(req: SolarTermDrawRequest, size: Int, earth: Bitmap? = null, moon: Bitmap? = null): Bitmap {
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val cx = size / 2f
        val cy = size / 2f
        val outer = size * outerFrac()
        val inner = size * innerFrac(req.compact)
        val hubFill = if (req.compact) inner - size * 0.02f else inner
        canvas.drawColor(SolarTermPalette.wheelBg(req.dark))
        val rot = 0f
        val oval = RectF(cx - outer, cy - outer, cx + outer, cy + outer)
        val sector = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
        SolarTerm.entries.forEachIndexed { i, term ->
            sector.color = SolarTermPalette.sectorColor(term, req.southern, req.dark)
            canvas.drawArc(oval, startDeg(i), SWEEP, true, sector)
        }
        canvas.drawCircle(cx, cy, hubFill, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = SolarTermPalette.hub(req.dark)
        })
        SolarTermCrosshairs.draw(canvas, cx, cy, hubFill, req.dark)
        highlight(canvas, oval, req.snapshot.current.term.ordinal, req.dark)
        SolarTermRadialLabels.draw(canvas, cx, cy, inner, outer, req, size)
        SolarTermHubRenderer.draw(
            canvas, cx, cy, inner, req.nowLon, req.perihelionLon, req.dark, rot,
            req.now, earth, moon, req.userLat, req.userLon,
        )
        SolarTermAlarmDots.draw(canvas, cx, cy, hubFill, rot, req.alarmOrds, size)
        needleAt(canvas, cx, cy, inner, outer, req.dark, SolarTermLayout.canvasDeg(req.nowLon))
        return bmp
    }

    fun sectorAt(x: Float, y: Float, size: Int, @Suppress("UNUSED_PARAMETER") nowLon: Double, compact: Boolean = false): Int? {
        val cx = size / 2f
        val cy = size / 2f
        val dx = x - cx
        val dy = y - cy
        val r = kotlin.math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
        val outer = size * outerFrac()
        val inner = size * innerFrac(compact)
        if (r < inner || r > outer) return null
        val deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble()))
        return (wrap360(-90.0 - deg) / 15.0).toInt().coerceIn(0, 23)
    }

    internal fun startDeg(index: Int): Float = -90f - index * 15f

    internal fun midDeg(index: Int): Float = startDeg(index) + SWEEP / 2f

    private fun highlight(canvas: Canvas, oval: RectF, index: Int, dark: Boolean) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = oval.width() * 0.012f
            color = if (dark) 0xFFFFF4C2.toInt() else 0xFF1A237E.toInt()
        }
        canvas.drawArc(oval, startDeg(index), SWEEP, false, p)
    }

    private fun needleAt(canvas: Canvas, cx: Float, cy: Float, inner: Float, outer: Float, dark: Boolean, angDeg: Float) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (dark) 0xFFFFE082.toInt() else 0xFFC62828.toInt()
            strokeWidth = outer * 0.016f
            strokeCap = Paint.Cap.ROUND
        }
        val rad = Math.toRadians(angDeg.toDouble()).toFloat()
        val c = cos(rad)
        val s = sin(rad)
        val r0 = inner * 0.88f
        val r1 = inner + (outer - inner) * 0.16f
        canvas.drawLine(cx + r0 * c, cy + r0 * s, cx + r1 * c, cy + r1 * s, p)
    }

    private const val SWEEP = -15f
}
