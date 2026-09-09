package org.astroalarm.widget

import android.graphics.*
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.sky.SkyBodies
import org.astroalarm.astro.sun.SolarCalculator
import org.astroalarm.astro.zodiac.ZodiacCalculator
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object AstroDiskRenderer {
    fun renderDisk(
        place: AstroPlace?,
        alarms: List<AstroAlarm>,
        now: Instant = Instant.now(),
        size: Int = 300,
        showZodiac: Boolean = true,
        showEventTimes: Boolean = true,
        showMonthTicks: Boolean = false,
        showHourMarks: Boolean = false,
        earth: Bitmap? = null,
        moon: Bitmap? = null,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        draw(Canvas(bitmap), place, alarms, now, size, showZodiac, showEventTimes, showMonthTicks, showHourMarks, earth, moon)
        return bitmap
    }

    fun draw(
        canvas: Canvas,
        place: AstroPlace?,
        alarms: List<AstroAlarm>,
        now: Instant,
        size: Int,
        showZodiac: Boolean,
        showEventTimes: Boolean,
        showMonthTicks: Boolean,
        showHourMarks: Boolean,
        earth: Bitmap? = null,
        moon: Bitmap? = null,
    ) {
        val center = size / 2f
        val rings = DiskRingLayout.of(size, showMonthTicks, showZodiac)
        val radius = rings.innerR
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
        val eventTimes = DiskEventTimeLayers.fromToggle(showEventTimes)
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
        val timeFmt = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
        val tSize = DiskLabelFit.textSize(size, rings.badgeR, 4, "00:00")
        if (eventTimes.sunriseSunsetBadges) {
            if (rise != null && aRise != null) AstroDiskOverlays.drawTransitionBadge(canvas, center, rings.badgeR, aRise, ZonedDateTime.ofInstant(rise, zone).format(timeFmt), tSize)
            if (set != null && aSet != null) AstroDiskOverlays.drawTransitionBadge(canvas, center, rings.badgeR, aSet, ZonedDateTime.ofInstant(set, zone).format(timeFmt), tSize)
        }
        if (eventTimes.noonMidnightBadges) {
            if (noon != null && aNoon != null) AstroDiskOverlays.drawTransitionBadge(canvas, center, rings.badgeR, aNoon, ZonedDateTime.ofInstant(noon, zone).format(timeFmt), tSize)
            if (mid != null) AstroDiskOverlays.drawTransitionBadge(canvas, center, rings.badgeR, aMid, ZonedDateTime.ofInstant(mid, zone).format(timeFmt), tSize)
        }
        val sunEq = place?.let { SkyBodies.sun(now, it.latitude, it.longitude) }
        val aNoonDeg = aNoon ?: -90f
        val sunDeg = sunEq?.let { TransitTicks.diskAngleDeg(it.haRad, aNoonDeg) } ?: -90f
        val sunLon = ZodiacCalculator.sunLongitudeAt(noon ?: now)
        if (showHourMarks) CivilHourMarks.draw(canvas, center, rings.hourTickR, rings.hourLabelR, size, CivilHourMarks.marks(nowAngle, aRise, aSet))
        SolarNoonPointer.draw(canvas, center, rings.innerR, SolarNoonPointer.endR(showMonthTicks, showZodiac, rings), aNoonDeg, size)
        if (showMonthTicks) {
            AstroDiskOverlays.drawMonthRim(canvas, center, radius, size, MonthRimTicks.marks(date.year, zone, sunLon, aNoonDeg))
        }
        if (showZodiac) {
            AstroDiskOverlays.drawZodiacRing(canvas, center, rings.zodiacR, sunLon, aNoonDeg, size)
            AstroDiskOverlays.drawZodiacCusps(canvas, center, rings.zodiacR, size, sunLon, aNoonDeg)
        }
        AstroDiskBodies.drawHand(canvas, center, rings.handStartR, rings.handEndR, size)
        DailyHubRenderer.draw(canvas, center, rings, now, place, sunDeg, earth, moon)
        AstroDiskBodies.drawSun(canvas, center, rings.bodyR, size, sunDeg)
        if (eventTimes.alarmMarkers) {
            AstroDiskAlarmOverlay.draw(canvas, alarms, place, now, horizon, zone, center, radius, rings.alarmR, size, nowAngle, timeFmt)
        }
    }
}
