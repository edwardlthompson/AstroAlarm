package org.astroalarm.astro.sky

import org.astroalarm.astro.model.LunarEventType
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.moon.LunarCalculator
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.sun.SolarCalculator
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** Next solar or lunar rise/set after [now], for the daily “tonight’s sky” card. */
data class TonightSkyEvent(
    val kind: Kind,
    val at: Instant,
    val contextKey: String,
) {
    enum class Kind { Sunrise, Sunset, Moonrise, Moonset }
}

object TonightSky {
    private val kinds = listOf(
        TonightSkyEvent.Kind.Sunrise,
        TonightSkyEvent.Kind.Sunset,
        TonightSkyEvent.Kind.Moonrise,
        TonightSkyEvent.Kind.Moonset,
    )

    fun next(place: AstroPlace, now: Instant = Instant.now()): TonightSkyEvent? {
        if (!place.isValid) return null
        val zone = place.zone
        val start = now.atZone(zone).toLocalDate()
        val candidates = mutableListOf<TonightSkyEvent>()
        for (dayOffset in 0..2) {
            val date = start.plusDays(dayOffset.toLong())
            for (kind in kinds) {
                val at = instant(kind, date, place.latitude, place.longitude, zone) ?: continue
                if (at.isAfter(now)) {
                    candidates += TonightSkyEvent(kind, at, contextKey(kind, date, zone, now))
                }
            }
        }
        return candidates.minByOrNull { it.at }
    }

    private fun instant(
        kind: TonightSkyEvent.Kind,
        date: LocalDate,
        lat: Double,
        lon: Double,
        zone: ZoneId,
    ): Instant? = when (kind) {
        TonightSkyEvent.Kind.Sunrise ->
            SolarCalculator.calculate(SolarEventType.Sunrise, date, lat, lon, zone)
        TonightSkyEvent.Kind.Sunset ->
            SolarCalculator.calculate(SolarEventType.Sunset, date, lat, lon, zone)
        TonightSkyEvent.Kind.Moonrise ->
            LunarCalculator.calculate(LunarEventType.Moonrise, date, lat, lon, zone)
        TonightSkyEvent.Kind.Moonset ->
            LunarCalculator.calculate(LunarEventType.Moonset, date, lat, lon, zone)
    }

    private fun contextKey(
        kind: TonightSkyEvent.Kind,
        date: LocalDate,
        zone: ZoneId,
        now: Instant,
    ): String {
        val today = now.atZone(zone).toLocalDate()
        val whenKey = when {
            date == today -> "today"
            date == today.plusDays(1) -> "tomorrow"
            else -> "later"
        }
        return when (kind) {
            TonightSkyEvent.Kind.Sunrise -> "ctx_sunrise_$whenKey"
            TonightSkyEvent.Kind.Sunset -> "ctx_sunset_$whenKey"
            TonightSkyEvent.Kind.Moonrise -> "ctx_moonrise_$whenKey"
            TonightSkyEvent.Kind.Moonset -> "ctx_moonset_$whenKey"
        }
    }
}
