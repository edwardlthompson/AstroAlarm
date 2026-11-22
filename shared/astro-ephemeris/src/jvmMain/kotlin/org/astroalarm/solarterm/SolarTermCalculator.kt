package org.astroalarm.solarterm

import org.astroalarm.astro.sun.SolarSeasons
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Finds the UTC instant when the Sun’s NOAA apparent geocentric ecliptic
 * longitude equals a jieqi target. Search starts at the term’s typical month
 * (not [SolarSeasons]’s season-month map, which would put Lìchūn in December).
 */
object SolarTermCalculator {
    fun instant(year: Int, term: SolarTerm): Instant {
        val start = LocalDate.of(year, term.typicalMonth, 1).atStartOfDay(ZoneOffset.UTC).toInstant()
        val end = start.plusSeconds(50L * 86400L)
        var lo = start.epochSecond
        var hi = end.epochSecond
        repeat(40) {
            val mid = (lo + hi) / 2L
            val err = wrap180(SolarSeasons.apparentLon(Instant.ofEpochSecond(mid)) - term.longitudeDeg)
            if (err < 0.0) lo = mid else hi = mid
        }
        return Instant.ofEpochSecond(hi)
    }

    /** Lìchūn of [year] through Dàhán in January of [year] + 1. */
    fun tropicalYear(year: Int): List<SolarTermOccurrence> =
        SolarTerm.entries.map { term ->
            val y = if (term.typicalMonth == 1) year + 1 else year
            SolarTermOccurrence(term, instant(y, term))
        }

    /** Xiǎohán/Dàhán in January of [year], then Lìchūn through Dōngzhì. */
    fun gregorianYear(year: Int): List<SolarTermOccurrence> {
        val jan = listOf(SolarTerm.XIAOHAN, SolarTerm.DAHAN).map {
            SolarTermOccurrence(it, instant(year, it))
        }
        val rest = SolarTerm.entries.filter { it.typicalMonth != 1 }.map {
            SolarTermOccurrence(it, instant(year, it))
        }
        return jan + rest
    }

    private fun wrap180(deg: Double): Double {
        var d = deg
        while (d > 180.0) d -= 360.0
        while (d < -180.0) d += 360.0
        return d
    }
}

data class SolarTermOccurrence(
    val term: SolarTerm,
    val utc: Instant,
)
