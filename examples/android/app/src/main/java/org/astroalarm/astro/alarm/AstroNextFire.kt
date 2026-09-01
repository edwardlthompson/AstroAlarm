package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.moon.LunarCalculator
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.sun.SolarCalculator
import org.astroalarm.astro.zodiac.ZodiacCalculator
import java.time.*

object AstroNextFire {

    fun nextInstant(
        alarm: AstroAlarm,
        place: AstroPlace?,
        now: Instant = Instant.now(),
        zone: ZoneId = place?.zone ?: ZoneId.systemDefault(),
    ): Instant? {
        if (!alarm.enabled) return null
        val nowZdt = ZonedDateTime.ofInstant(now, zone)

        return when (val target = alarm.target) {
            is AlarmTarget.CustomClock -> nextCustomClock(alarm, target, nowZdt)
            is AlarmTarget.Solar -> nextSolar(alarm, target, place, nowZdt, now)
            is AlarmTarget.Lunar -> nextLunar(alarm, target, place, nowZdt, now)
            is AlarmTarget.Zodiac -> nextZodiac(alarm, target, now)
        }
    }

    private fun nextCustomClock(
        alarm: AstroAlarm,
        target: AlarmTarget.CustomClock,
        nowZdt: ZonedDateTime
    ): Instant? {
        val today = nowZdt.toLocalDate()
        for (i in 0..7) {
            val candidateDate = today.plusDays(i.toLong())
            if (alarm.daysOfWeek.isNotEmpty() && !alarm.daysOfWeek.contains(candidateDate.dayOfWeek)) {
                continue
            }
            val candidateZdt = candidateDate.atTime(target.hour, target.minute)
                .atZone(nowZdt.zone)
            val candidateInstant = candidateZdt.toInstant()
            if (candidateInstant.isAfter(nowZdt.toInstant())) {
                return candidateInstant
            }
        }
        return null
    }

    private fun nextSolar(
        alarm: AstroAlarm,
        target: AlarmTarget.Solar,
        place: AstroPlace?,
        nowZdt: ZonedDateTime,
        now: Instant
    ): Instant? {
        if (place == null || !place.isValid) return null
        val today = nowZdt.toLocalDate()
        val isSeasonal = target.event in setOf(
            SolarEventType.MarchEquinox,
            SolarEventType.JuneSolstice,
            SolarEventType.SeptemberEquinox,
            SolarEventType.DecemberSolstice
        )
        if (isSeasonal) {
            val thisYear = today.year
            for (year in thisYear..(thisYear + 3)) {
                val date = LocalDate.of(year, 1, 1)
                val base = SolarCalculator.calculate(target.event, date, place.latitude, place.longitude, place.zone)
                    ?: continue
                val fireInstant = base.plusSeconds(target.offsetMinutes * 60L)
                if (fireInstant.isAfter(now)) {
                    return fireInstant
                }
            }
            return null
        }
        for (i in 0..14) {
            val date = today.plusDays(i.toLong())
            if (alarm.daysOfWeek.isNotEmpty() && !alarm.daysOfWeek.contains(date.dayOfWeek)) {
                continue
            }
            val base = SolarCalculator.calculate(target.event, date, place.latitude, place.longitude, place.zone)
                ?: continue
            val fireInstant = base.plusSeconds(target.offsetMinutes * 60L)
            if (fireInstant.isAfter(now)) {
                return fireInstant
            }
        }
        return null
    }

    private fun nextLunar(
        alarm: AstroAlarm,
        target: AlarmTarget.Lunar,
        place: AstroPlace?,
        nowZdt: ZonedDateTime,
        now: Instant
    ): Instant? {
        if (place == null || !place.isValid) return null
        val today = nowZdt.toLocalDate()
        for (i in 0..35) {
            val date = today.plusDays(i.toLong())
            if (alarm.daysOfWeek.isNotEmpty() && !alarm.daysOfWeek.contains(date.dayOfWeek)) {
                continue
            }
            val base = LunarCalculator.calculate(target.event, date, place.latitude, place.longitude, place.zone)
                ?: continue
            val fireInstant = base.plusSeconds(target.offsetMinutes * 60L)
            if (fireInstant.isAfter(now)) {
                return fireInstant
            }
        }
        return null
    }

    private fun nextZodiac(
        alarm: AstroAlarm,
        target: AlarmTarget.Zodiac,
        now: Instant
    ): Instant {
        val base = ZodiacCalculator.nextInstant(target.sign, target.point, now)
        val fireInstant = base.plusSeconds(target.offsetMinutes * 60L)
        return if (fireInstant.isAfter(now)) {
            fireInstant
        } else {
            val nextBase = ZodiacCalculator.nextInstant(target.sign, target.point, now.plusSeconds(86400L * 2))
            nextBase.plusSeconds(target.offsetMinutes * 60L)
        }
    }
}
