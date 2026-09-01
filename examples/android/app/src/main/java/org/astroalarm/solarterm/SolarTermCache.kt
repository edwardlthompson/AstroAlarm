package org.astroalarm.solarterm

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/** Caches tropical years for the current and next Lìchūn year; invalidates on year or zone change. */
object SolarTermCache {
    private val lock = Any()
    private val years = LinkedHashMap<CacheKey, SolarTermYear>(8, 0.75f, true)

    fun yearFor(now: Instant, zone: ZoneId): SolarTermYear {
        val keyYear = SolarTermYear.covering(now, zone).lichunYear
        return get(keyYear)
    }

    fun nextYear(now: Instant, zone: ZoneId): SolarTermYear = get(yearFor(now, zone).lichunYear + 1)

    fun snapshot(now: Instant, zone: ZoneId): SolarTermSnapshot {
        val year = yearFor(now, zone)
        val nxt = year.nextAfter(now)
        val resolved = if (!year.occurrences.any { it.utc == nxt.utc && it.term == nxt.term }) {
            nextYear(now, zone).occurrences.firstOrNull { it.term == nxt.term } ?: nxt
        } else {
            nxt
        }
        return SolarTermSnapshot(
            year = year,
            current = year.current(now),
            next = resolved,
            hoursUntilNext = Duration.between(now, resolved.utc).toHours().coerceAtLeast(0L),
            progress = year.progressInSector(now),
            localYear = ZonedDateTime.ofInstant(now, zone).year,
        )
    }

    fun clear() {
        synchronized(lock) { years.clear() }
    }

    private fun get(lichunYear: Int): SolarTermYear = synchronized(lock) {
        years.getOrPut(CacheKey(lichunYear)) { SolarTermYear.of(lichunYear) }.also {
            while (years.size > 4) {
                val oldest = years.keys.first()
                years.remove(oldest)
            }
        }
    }

    private data class CacheKey(val lichunYear: Int)
}

data class SolarTermSnapshot(
    val year: SolarTermYear,
    val current: SolarTermOccurrence,
    val next: SolarTermOccurrence,
    val hoursUntilNext: Long,
    val progress: Float,
    val localYear: Int,
)
