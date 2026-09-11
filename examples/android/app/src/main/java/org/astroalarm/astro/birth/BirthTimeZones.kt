package org.astroalarm.astro.birth

import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

object BirthTimeZones {
    fun zoneOf(profile: BirthProfile): ZoneId =
        runCatching { ZoneId.of(profile.zoneId) }.getOrDefault(ZoneId.of("UTC"))

    /**
     * Convert birth local civil time to Instant using IANA TZDB (historical DST).
     * When [BirthProfile.timeUnknown], uses noon local so Sun/Moon-day still work.
     */
    fun toInstant(profile: BirthProfile): Instant? {
        if (!profile.hasPlace && profile.zoneId.isBlank()) return null
        val time = when {
            profile.timeUnknown -> LocalTime.NOON
            profile.birthTime != null -> profile.birthTime
            else -> LocalTime.NOON
        }
        return runCatching {
            ZonedDateTime.of(profile.birthDate, time, zoneOf(profile)).toInstant()
        }.getOrNull()
    }

    /** Offset from UTC in seconds at the birth Instant (for tests / UI). */
    fun utcOffsetSeconds(profile: BirthProfile): Int? {
        val instant = toInstant(profile) ?: return null
        return zoneOf(profile).rules.getOffset(instant).totalSeconds
    }
}
