package org.astroalarm.astro.sun

import org.astroalarm.astro.model.SolarEventType
import org.shredzone.commons.suncalc.SunTimes
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object SolarCalculator {
    const val ZENITH_OFFICIAL = 90.8333
    const val ZENITH_CIVIL = 96.0
    const val ZENITH_NAUTICAL = 102.0
    const val ZENITH_ASTRONOMICAL = 108.0
    const val ZENITH_GOLDEN_HOUR = 84.0
    const val ZENITH_BLUE_HOUR_BOTTOM = 98.0

    fun calculate(
        event: SolarEventType,
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        zoneId: ZoneId,
    ): Instant? = runCatching {
        when (event) {
            SolarEventType.Sunrise -> inst(times(date, latitude, longitude, zoneId, SunTimes.Twilight.VISUAL)?.rise)
            SolarEventType.Sunset -> inst(times(date, latitude, longitude, zoneId, SunTimes.Twilight.VISUAL)?.set)
            SolarEventType.CivilDawn -> inst(times(date, latitude, longitude, zoneId, SunTimes.Twilight.CIVIL)?.rise)
            SolarEventType.CivilDusk -> inst(times(date, latitude, longitude, zoneId, SunTimes.Twilight.CIVIL)?.set)
            SolarEventType.NauticalDawn -> inst(times(date, latitude, longitude, zoneId, SunTimes.Twilight.NAUTICAL)?.rise)
            SolarEventType.NauticalDusk -> inst(times(date, latitude, longitude, zoneId, SunTimes.Twilight.NAUTICAL)?.set)
            SolarEventType.AstronomicalDawn -> inst(times(date, latitude, longitude, zoneId, SunTimes.Twilight.ASTRONOMICAL)?.rise)
            SolarEventType.AstronomicalDusk -> inst(times(date, latitude, longitude, zoneId, SunTimes.Twilight.ASTRONOMICAL)?.set)
            SolarEventType.SolarNoon -> inst(times(date, latitude, longitude, zoneId, SunTimes.Twilight.VISUAL)?.noon)
            SolarEventType.SolarMidnight -> inst(times(date, latitude, longitude, zoneId, SunTimes.Twilight.VISUAL)?.nadir)
            SolarEventType.GoldenHourMorning -> inst(times(date, latitude, longitude, zoneId, SunTimes.Twilight.GOLDEN_HOUR)?.rise)
            SolarEventType.GoldenHourEvening -> inst(times(date, latitude, longitude, zoneId, SunTimes.Twilight.GOLDEN_HOUR)?.set)
            SolarEventType.BlueHourMorning -> inst(times(date, latitude, longitude, zoneId, SunTimes.Twilight.NIGHT_HOUR)?.rise)
            SolarEventType.BlueHourEvening -> inst(times(date, latitude, longitude, zoneId, SunTimes.Twilight.NIGHT_HOUR)?.set)
            SolarEventType.MarchEquinox -> SolarSeasons.instant(date.year, 0.0)
            SolarEventType.JuneSolstice -> SolarSeasons.instant(date.year, 90.0)
            SolarEventType.SeptemberEquinox -> SolarSeasons.instant(date.year, 180.0)
            SolarEventType.DecemberSolstice -> SolarSeasons.instant(date.year, 270.0)
        }
    }.getOrNull()

    fun solarNoonInstant(date: LocalDate, lat: Double, lon: Double, zone: ZoneId): Instant? =
        calculate(SolarEventType.SolarNoon, date, lat, lon, zone)

    private fun times(
        date: LocalDate,
        lat: Double,
        lon: Double,
        zone: ZoneId,
        twilight: SunTimes.Twilight,
    ): SunTimes? = SunTimes.compute()
        .on(date.atStartOfDay(zone))
        .timezone(zone)
        .at(lat, lon)
        .oneDay()
        .twilight(twilight)
        .execute()

    private fun inst(value: Any?): Instant? = when (value) {
        null -> null
        is Instant -> value
        is java.time.ZonedDateTime -> value.toInstant()
        else -> null
    }
}
