package org.astroalarm.widget

import android.graphics.*
import org.astroalarm.astro.alarm.AstroNextFire
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.moon.LunarCalculator
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.sun.SolarMath
import org.astroalarm.astro.zodiac.ZodiacCalculator
import org.astroalarm.astro.zodiac.ZodiacSign
import java.time.Instant
import java.time.ZonedDateTime
import kotlin.math.*

object Astro3DRenderer {

    fun render3D(
        place: AstroPlace?,
        alarms: List<AstroAlarm>,
        now: Instant = Instant.now(),
        size: Int = 400,
        showZodiac: Boolean = true,
        parallaxX: Float = 0f,
        parallaxY: Float = 0f,
        earth: Bitmap? = null,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val globeCx = size / 2f
        val globeCy = size / 2f
        val cx = globeCx + parallaxX
        val cy = globeCy + parallaxY
        val zone = place?.zone ?: java.time.ZoneId.systemDefault()
        val zdt = ZonedDateTime.ofInstant(now, zone)
        val lat = place?.latitude ?: 40.0
        val lon = place?.longitude ?: -74.0
        val date = zdt.toLocalDate()

        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), Paint().apply {
            shader = RadialGradient(globeCx, globeCy, size * 0.75f, intArrayOf(Color.rgb(14, 20, 42), Color.rgb(5, 8, 22), Color.rgb(1, 3, 8)), floatArrayOf(0f, 0.6f, 1f), Shader.TileMode.CLAMP)
        })
        drawStars(canvas, size, parallaxX * 0.4f, parallaxY * 0.4f)

        val tiltRad = Math.toRadians((90.0 - lat).coerceIn(15.0, 75.0))
        val sinT = sin(tiltRad).toFloat()
        val jd = SolarMath.julianDay(date)
        val t = SolarMath.julianCentury(jd)
        val sunDec = SolarMath.sunDeclination(t, SolarMath.geomMeanLongSun(t), SolarMath.sunEqOfCenter(t, SolarMath.geomMeanAnomSun(t)))
        val moonAge = LunarCalculator.moonAgeDays(date)
        val moonDec = 23.44 * sin((moonAge / 29.53059) * 2.0 * Math.PI)

        val ringR = size * 0.38f
        val sunRx = ringR * cos(Math.toRadians(sunDec)).toFloat()
        val sunRy = sunRx * 0.42f
        val sunCy = cy - ringR * sin(Math.toRadians(sunDec)).toFloat() * sinT * 0.5f
        val moonRx = ringR * cos(Math.toRadians(moonDec)).toFloat()
        val moonRy = moonRx * 0.42f
        val moonCy = cy - ringR * sin(Math.toRadians(moonDec)).toFloat() * sinT * 0.5f

        drawTiltedRingHalf(canvas, cx, moonCy, moonRx, moonRy, true, Color.argb(80, 240, 245, 255))
        drawTiltedRingHalf(canvas, cx, sunCy, sunRx, sunRy, true, Color.argb(120, 255, 215, 0))
        val zodiacRx = ringR * 1.08f
        val zodiacRy = ringR * 0.46f * cos(tiltRad - Math.toRadians(23.44)).toFloat()
        if (showZodiac) drawTiltedRingHalf(canvas, cx, cy, zodiacRx, zodiacRy, true, Color.argb(60, 140, 190, 255))

        EarthGlobeRenderer.drawGlobe(canvas, globeCx, globeCy, size * 0.16f, lat, lon, earth, true)

        drawTiltedRingHalf(canvas, cx, moonCy, moonRx, moonRy, false, Color.rgb(240, 245, 255))
        drawTiltedRingHalf(canvas, cx, sunCy, sunRx, sunRy, false, Color.rgb(255, 215, 0))
        if (showZodiac) {
            drawTiltedRingHalf(canvas, cx, cy, zodiacRx, zodiacRy, false, Color.argb(175, 140, 190, 255))
            val sunLon = ZodiacCalculator.sunLongitudeAt(now)
            drawOnRing(canvas, cx, cy, zodiacRx, zodiacRy, sunLon, ZodiacSign.fromEclipticLongitude(sunLon), size)
        }

        val sunFraction = (zdt.hour * 60 + zdt.minute + zdt.second / 60f) / 1440f
        val sunAng = (sunFraction * 2.0 * Math.PI) - Math.PI / 2.0
        val (sx, sy) = ellipsePoint(cx, sunCy, sunRx, sunRy, sunAng)
        drawBody(canvas, sx, sy, size * 0.08f, Color.rgb(255, 220, 64), "☀️", size)

        val moonAng = ((sunFraction + moonAge / 29.53059) % 1.0 * 2.0 * Math.PI) - Math.PI / 2.0
        val (mx, my) = ellipsePoint(cx, moonCy, moonRx, moonRy, moonAng)
        drawBody(canvas, mx, my, size * 0.065f, Color.rgb(200, 220, 255), moonPhaseEmoji(moonAge), size)

        drawAlarmNodes(canvas, alarms, place, now, cx, sunCy, sunRx, sunRy, size)
        return bitmap
    }

    private fun ellipsePoint(cx: Float, cy: Float, rx: Float, ry: Float, ang: Double): Pair<Float, Float> =
        (cx + rx * cos(ang).toFloat()) to (cy + ry * sin(ang).toFloat())

    private fun drawBody(canvas: Canvas, x: Float, y: Float, halo: Float, tint: Int, emoji: String, size: Int) {
        canvas.drawCircle(x, y, halo, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(x, y, halo, intArrayOf(Color.WHITE, tint, Color.TRANSPARENT), floatArrayOf(0f, 0.45f, 1f), Shader.TileMode.CLAMP)
        })
        val ep = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = (size * 0.075f).coerceIn(18f, 38f); textAlign = Paint.Align.CENTER }
        canvas.drawText(emoji, x, y + ep.textSize * 0.35f, ep)
    }

    private fun moonPhaseEmoji(age: Double): String = when (age) {
        in 0.0..1.84, in 27.69..29.53 -> "🌑"; in 1.84..5.53 -> "🌒"; in 5.53..9.22 -> "🌓"; in 9.22..12.91 -> "🌔"
        in 12.91..16.61 -> "🌕"; in 16.61..20.30 -> "🌖"; in 20.30..23.99 -> "🌗"; else -> "🌘"
    }

    private fun drawStars(canvas: Canvas, size: Int, px: Float, py: Float) {
        val sp = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
        listOf(0.12f to 0.18f, 0.85f to 0.14f, 0.22f to 0.78f, 0.88f to 0.82f, 0.08f to 0.45f, 0.92f to 0.48f, 0.35f to 0.12f, 0.65f to 0.88f).forEach { (rx, ry) ->
            sp.alpha = (120 + (rx * 135).toInt()); canvas.drawCircle(rx * size + px, ry * size + py, 1.3f, sp)
        }
    }

    private fun drawTiltedRingHalf(canvas: Canvas, cx: Float, cy: Float, rx: Float, ry: Float, isBack: Boolean, color: Int) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = (rx * 0.016f).coerceIn(1.8f, 3.5f); this.color = color }
        canvas.drawArc(RectF(cx - rx, cy - ry, cx + rx, cy + ry), if (isBack) 180f else 0f, 180f, false, p)
    }

    private fun drawOnRing(canvas: Canvas, cx: Float, cy: Float, rx: Float, ry: Float, sunLon: Double, midday: ZodiacSign, size: Int) {
        val ep = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER; textSize = (size * 0.068f).coerceIn(18f, 34f) }
        ZodiacSign.entries.forEach { sign ->
            val ang = Math.toRadians(sign.startLongitudeDeg + 15.0 - sunLon)
            val (x, y) = ellipsePoint(cx, cy, rx, ry, ang)
            val front = sin(ang) >= 0
            ep.color = if (sign == midday) Color.rgb(255, 215, 60) else if (front) Color.rgb(220, 238, 255) else Color.argb(130, 150, 180, 220)
            canvas.drawText(sign.symbol, x, y + ep.textSize * 0.35f, ep)
        }
    }

    private fun drawAlarmNodes(canvas: Canvas, alarms: List<AstroAlarm>, place: AstroPlace?, now: Instant, cx: Float, cy: Float, rx: Float, ry: Float, size: Int) {
        val nodePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(255, 60, 60) }
        val horizon = now.plusSeconds(86400L)
        val zone = place?.zone ?: java.time.ZoneId.systemDefault()
        alarms.filter { it.enabled }.forEach { alarm ->
            AstroNextFire.nextInstant(alarm, place, now)?.let { next ->
                if (next.isAfter(now) && !next.isAfter(horizon)) {
                    val zdt = ZonedDateTime.ofInstant(next, zone)
                    val ang = ((zdt.hour * 60 + zdt.minute) / 1440.0 * 2.0 * Math.PI) - Math.PI / 2.0
                    val (x, y) = ellipsePoint(cx, cy, rx, ry, ang)
                    canvas.drawCircle(x, y, (size * 0.024f).coerceIn(4f, 9f), nodePaint)
                }
            }
        }
    }
}
