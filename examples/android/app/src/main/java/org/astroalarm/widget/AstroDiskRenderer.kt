package org.astroalarm.widget

import android.graphics.*
import org.astroalarm.astro.alarm.AstroNextFire
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.moon.LunarCalculator
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.sun.SolarCalculator
import org.astroalarm.astro.zodiac.ZodiacCalculator
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.*

object AstroDiskRenderer {
    fun renderDisk(
        place: AstroPlace?,
        alarms: List<AstroAlarm>,
        now: Instant = Instant.now(),
        size: Int = 300,
        showZodiac: Boolean = true,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val center = size / 2f
        val emojiSize = (size * 0.078f).coerceIn(18f, 38f)
        val radius = center - emojiSize * 1.50f - 4f
        val zone = place?.zone ?: java.time.ZoneId.systemDefault()
        val nowZdt = ZonedDateTime.ofInstant(now, zone)
        val nowAngle = (nowZdt.hour * 60 + nowZdt.minute + (nowZdt.second / 60f)) / 1440f * 360f
        val date = nowZdt.toLocalDate()
        val horizon = now.plusSeconds(86400L)
        val oval = RectF(center - radius, center - radius, center + radius, center + radius)

        val rise = place?.let { SolarCalculator.calculate(SolarEventType.Sunrise, date, it.latitude, it.longitude, it.zone) }
        val set = place?.let { SolarCalculator.calculate(SolarEventType.Sunset, date, it.latitude, it.longitude, it.zone) }
        val noon = place?.let { SolarCalculator.calculate(SolarEventType.SolarNoon, date, it.latitude, it.longitude, it.zone) }
        val mid = place?.let { SolarCalculator.calculate(SolarEventType.SolarMidnight, date, it.latitude, it.longitude, it.zone) }
        val dawnA = place?.let { SolarCalculator.calculate(SolarEventType.AstronomicalDawn, date, it.latitude, it.longitude, it.zone) }
        val duskA = place?.let { SolarCalculator.calculate(SolarEventType.AstronomicalDusk, date, it.latitude, it.longitude, it.zone) }
        val bm = place?.let { SolarCalculator.calculate(SolarEventType.BlueHourMorning, date, it.latitude, it.longitude, it.zone) }
        val be = place?.let { SolarCalculator.calculate(SolarEventType.BlueHourEvening, date, it.latitude, it.longitude, it.zone) }
        val gm = place?.let { SolarCalculator.calculate(SolarEventType.GoldenHourMorning, date, it.latitude, it.longitude, it.zone) }
        val ge = place?.let { SolarCalculator.calculate(SolarEventType.GoldenHourEvening, date, it.latitude, it.longitude, it.zone) }

        fun ang(i: Instant?): Float? = i?.let {
            val z = ZonedDateTime.ofInstant(it, zone)
            ((z.hour * 60 + z.minute + z.second / 60f) / 1440f) * 360f - nowAngle - 90f
        }
        fun sw(f: Float, t: Float): Float { var d = t - f; while (d < 0) d += 360f; return d }

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(14, 22, 38) }
        canvas.drawCircle(center, center, radius, paint)

        val aRise = ang(rise); val aSet = ang(set); val aNoon = ang(noon); val aMid = ang(mid) ?: ((aSet ?: 0f) + 90f)
        val dividers = mutableListOf<Pair<Float, Boolean>>()

        if (aRise != null && aSet != null) {
            val aDawn = ang(dawnA) ?: (aRise - 30f); val aDusk = ang(duskA) ?: (aSet + 30f)
            val aBm = ang(bm) ?: (aRise - 15f); val aBe = ang(be) ?: (aSet + 15f)
            val aGm = ang(gm) ?: (aRise + 12f); val aGe = ang(ge) ?: (aSet - 12f)
            val nMid = aNoon ?: (aRise + sw(aRise, aSet) / 2f)

            paint.color = Color.rgb(10, 18, 32); canvas.drawArc(oval, aDusk, sw(aDusk, aDawn), true, paint)
            paint.color = Color.rgb(0, 0, 0); canvas.drawArc(oval, aMid - 8f, 16f, true, paint)
            paint.color = Color.rgb(65, 75, 90); canvas.drawArc(oval, aDawn, sw(aDawn, aBm), true, paint); canvas.drawArc(oval, aBe, sw(aBe, aDusk), true, paint)
            paint.color = Color.rgb(25, 75, 155); canvas.drawArc(oval, aBm, sw(aBm, aRise), true, paint); canvas.drawArc(oval, aSet, sw(aSet, aBe), true, paint)
            paint.color = Color.rgb(235, 175, 45); canvas.drawArc(oval, aRise, sw(aRise, aGm), true, paint); canvas.drawArc(oval, aGe, sw(aGe, aSet), true, paint)
            paint.color = Color.rgb(215, 232, 248); canvas.drawArc(oval, aGm, sw(aGm, nMid - 8f), true, paint); canvas.drawArc(oval, nMid + 8f, sw(nMid + 8f, aGe), true, paint)
            paint.color = Color.rgb(255, 255, 255); canvas.drawArc(oval, nMid - 8f, 16f, true, paint)

            val totalDay = sw(aRise, aSet)
            listOf(aDawn, aBm, aRise, aGm, nMid - 8f, nMid, nMid + 8f, aGe, aSet, aBe, aDusk, aMid - 8f, aMid, aMid + 8f).forEach { deg ->
                dividers.add(deg to (sw(aRise, deg) <= totalDay))
            }
        }

        AstroDiskOverlays.drawTicksAndDividers(canvas, center, radius, size, dividers)
        paint.style = Paint.Style.STROKE; paint.strokeWidth = (size * 0.012f).coerceIn(2f, 5f); paint.color = Color.GRAY
        canvas.drawCircle(center, center, radius, paint)

        val dist = radius + emojiSize * 0.95f
        val timeFmt = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
        val tSize = (size * 0.052f).coerceIn(13f, 26f)
        val bodyEmojiSize = (size * 0.078f).coerceIn(18f, 38f)

        if (rise != null && set != null && aRise != null && aSet != null) {
            AstroDiskOverlays.drawTransitionBadge(canvas, center, radius, aRise, ZonedDateTime.ofInstant(rise, zone).format(timeFmt), tSize)
            AstroDiskOverlays.drawTransitionBadge(canvas, center, radius, aSet, ZonedDateTime.ofInstant(set, zone).format(timeFmt), tSize)
        }

        // Real-time sun and moon (phase) on the inner ring
        val aSunRad = -Math.PI / 2.0
        val sx = center + (radius * 0.74f) * cos(aSunRad).toFloat()
        val sy = center + (radius * 0.74f) * sin(aSunRad).toFloat()
        canvas.drawCircle(sx, sy, size * 0.045f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(sx, sy, size * 0.045f, intArrayOf(Color.WHITE, Color.rgb(255, 215, 60), Color.TRANSPARENT), floatArrayOf(0f, 0.4f, 1f), Shader.TileMode.CLAMP)
        })
        val ep = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = bodyEmojiSize; textAlign = Paint.Align.CENTER }
        canvas.drawText("☀️", sx, sy + bodyEmojiSize * 0.35f, ep)

        val moonAge = LunarCalculator.moonAgeDays(date)
        val moonAngleRad = (-Math.PI / 2.0) + ((moonAge / 29.53059) * 2.0 * Math.PI)
        val mx = center + (radius * 0.74f) * cos(moonAngleRad).toFloat()
        val my = center + (radius * 0.74f) * sin(moonAngleRad).toFloat()
        canvas.drawCircle(mx, my, size * 0.040f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(mx, my, size * 0.040f, intArrayOf(Color.WHITE, Color.rgb(180, 210, 255), Color.TRANSPARENT), floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        })
        val moonEmoji = when (moonAge) {
            in 0.0..1.84, in 27.69..29.53 -> "🌑"; in 1.84..5.53 -> "🌒"; in 5.53..9.22 -> "🌓"; in 9.22..12.91 -> "🌔"
            in 12.91..16.61 -> "🌕"; in 16.61..20.30 -> "🌖"; in 20.30..23.99 -> "🌗"; else -> "🌘"
        }
        canvas.drawText(moonEmoji, mx, my + bodyEmojiSize * 0.35f, ep)

        if (showZodiac) {
            val sunLon = ZodiacCalculator.sunLongitudeAt(noon ?: now)
            val aNoonDeg = aNoon ?: (aRise?.let { r -> aSet?.let { s -> r + sw(r, s) / 2f } } ?: 0f)
            AstroDiskOverlays.drawZodiacRing(canvas, center, dist, emojiSize, sunLon, aNoonDeg)
        }

        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.RED; style = Paint.Style.FILL }
        val upcomingItems = mutableListOf<Pair<Instant, String>>()
        alarms.filter { it.enabled }.forEach { alarm ->
            AstroNextFire.nextInstant(alarm, place, now)?.let { next ->
                if (next.isAfter(now) && !next.isAfter(horizon)) {
                    val z = ZonedDateTime.ofInstant(next, zone)
                    val rad = (((z.hour * 60 + z.minute) / 1440f * 360f - nowAngle - 90f)) * (Math.PI / 180.0)
                    canvas.drawCircle(center + radius * cos(rad).toFloat(), center + radius * sin(rad).toFloat(), (size * 0.026f).coerceIn(5f, 12f), dotPaint)
                    val icon = when (val t = alarm.target) {
                        is AlarmTarget.Solar -> "☀️ "; is AlarmTarget.Lunar -> "🌙 "; is AlarmTarget.Zodiac -> t.sign.symbol + " "; is AlarmTarget.CustomClock -> "⏰ "
                    }
                    upcomingItems.add(next to (icon + z.format(timeFmt)))
                }
            }
        }
        AstroDiskOverlays.drawCallouts(canvas, center, radius, size, upcomingItems.sortedBy { it.first.toEpochMilli() }.distinctBy { it.second }.map { it.second })

        val handPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.RED; strokeWidth = (size * 0.012f).coerceIn(2.5f, 5f); style = Paint.Style.FILL_AND_STROKE }
        canvas.drawLine(center, center, center, center - radius + 10f, handPaint)
        AstroDiskOverlays.drawCenterHub(canvas, center, size)
        return bitmap
    }
}
