package org.astroalarm.solarterm

import org.astroalarm.astro.sun.SolarSeasons
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/** Precomputed 24-term tropical year plus “now / next” helpers. Do not recompute per frame. */
data class SolarTermYear(
    val lichunYear: Int,
    val occurrences: List<SolarTermOccurrence>,
) {
    fun current(now: Instant): SolarTermOccurrence {
        val lon = SolarSeasons.apparentLon(now)
        val term = SolarTerm.containing(lon)
        return occurrences.firstOrNull { it.term == term }
            ?: SolarTermOccurrence(term, now)
    }

    fun nextAfter(now: Instant): SolarTermOccurrence {
        val upcoming = occurrences.filter { it.utc.isAfter(now) }.minByOrNull { it.utc }
        if (upcoming != null) return upcoming
        val last = occurrences.last()
        return SolarTermOccurrence(last.term.next(), last.utc.plusSeconds(15L * 86400L))
    }

    fun hoursUntilNext(now: Instant): Long =
        Duration.between(now, nextAfter(now).utc).toHours().coerceAtLeast(0L)

    fun progressInSector(now: Instant): Float {
        val lon = SolarSeasons.apparentLon(now)
        val term = SolarTerm.containing(lon)
        return (wrap360(lon - term.longitudeDeg) / 15.0).toFloat().coerceIn(0f, 1f)
    }

    companion object {
        fun of(lichunYear: Int): SolarTermYear =
            SolarTermYear(lichunYear, SolarTermCalculator.tropicalYear(lichunYear))

        fun covering(now: Instant, zone: ZoneId): SolarTermYear {
            val y = ZonedDateTime.ofInstant(now, zone).year
            val candidate = of(y)
            val lichun = candidate.occurrences.first().utc
            return if (now.isBefore(lichun)) of(y - 1) else candidate
        }
    }
}
