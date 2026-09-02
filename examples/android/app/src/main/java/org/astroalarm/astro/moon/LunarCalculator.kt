package org.astroalarm.astro.moon

import org.astroalarm.astro.model.LunarEventType
import org.astroalarm.astro.sky.SkyBodies
import org.astroalarm.astro.sun.SolarSeasons
import org.shredzone.commons.suncalc.MoonIllumination
import org.shredzone.commons.suncalc.MoonPhase
import org.shredzone.commons.suncalc.MoonTimes
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

object LunarCalculator {
    const val SYNODIC_MONTH = 29.53058867

    fun moonAgeDays(date: LocalDate): Double = runCatching {
        // suncalc phase: -180 new (waxing) → 0 full → +180 new (waning)
        val phase = MoonIllumination.compute().on(date.atStartOfDay(ZoneOffset.UTC)).execute().phase
        var age = ((phase + 180.0) / 360.0) * SYNODIC_MONTH
        if (age < 0.0) age += SYNODIC_MONTH
        if (age >= SYNODIC_MONTH) age -= SYNODIC_MONTH
        age
    }.getOrDefault(0.0)

    fun calculate(
        event: LunarEventType,
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        zoneId: ZoneId,
    ): Instant? = runCatching {
        when (event) {
            LunarEventType.NewMoon -> phase(date, zoneId, MoonPhase.Phase.NEW_MOON)
            LunarEventType.WaxingCrescent -> phase(date, zoneId, MoonPhase.Phase.WAXING_CRESCENT)
            LunarEventType.FirstQuarter -> phase(date, zoneId, MoonPhase.Phase.FIRST_QUARTER)
            LunarEventType.WaxingGibbous -> phase(date, zoneId, MoonPhase.Phase.WAXING_GIBBOUS)
            LunarEventType.FullMoon -> phase(date, zoneId, MoonPhase.Phase.FULL_MOON)
            LunarEventType.WaningGibbous -> phase(date, zoneId, MoonPhase.Phase.WANING_GIBBOUS)
            LunarEventType.LastQuarter -> phase(date, zoneId, MoonPhase.Phase.LAST_QUARTER)
            LunarEventType.WaningCrescent -> phase(date, zoneId, MoonPhase.Phase.WANING_CRESCENT)
            LunarEventType.Moonrise -> times(date, latitude, longitude, zoneId).rise?.toInstant()
            LunarEventType.Moonset -> times(date, latitude, longitude, zoneId).set?.toInstant()
            LunarEventType.MoonTransit -> transit(date, latitude, longitude, zoneId)
        }
    }.getOrNull()

    private fun phase(date: LocalDate, zone: ZoneId, kind: MoonPhase.Phase): Instant? =
        MoonPhase.compute().on(date.atStartOfDay(zone)).timezone(zone).phase(kind).execute().time?.toInstant()

    private fun times(date: LocalDate, lat: Double, lon: Double, zone: ZoneId): MoonTimes =
        MoonTimes.compute().on(date.atStartOfDay(zone)).timezone(zone).at(lat, lon).oneDay().execute()

    private fun transit(date: LocalDate, lat: Double, lon: Double, zone: ZoneId): Instant? {
        val start = date.atStartOfDay(zone).toInstant()
        var prevT = start
        var prevHa = SkyBodies.moon(start, lat, lon)?.haRad ?: return null
        for (i in 1..48) {
            val t = start.plusSeconds(i * 1800L)
            val ha = SkyBodies.moon(t, lat, lon)?.haRad ?: continue
            if (prevHa < 0.0 && ha >= 0.0) {
                val span = ha - prevHa
                val frac = if (span == 0.0) 0.0 else (-prevHa / span)
                val dt = (t.epochSecond - prevT.epochSecond) * frac
                return prevT.plusSeconds(dt.toLong())
            }
            prevT = t
            prevHa = ha
        }
        return null
    }

    fun eclipticLon(instant: Instant): Double = runCatching {
        wrap360(SolarSeasons.apparentLon(instant) + elongationDeg(instant))
    }.getOrElse {
        val d = (instant.epochSecond - 946728000.0) / 86400.0
        wrap360(218.316 + 13.176396 * d)
    }

    fun elongationDeg(instant: Instant): Double = runCatching {
        wrap360(180.0 + MoonIllumination.compute().on(instant).execute().phase)
    }.getOrDefault(0.0)

    private fun wrap360(deg: Double): Double {
        var d = deg % 360.0
        if (d < 0.0) d += 360.0
        return d
    }
}
