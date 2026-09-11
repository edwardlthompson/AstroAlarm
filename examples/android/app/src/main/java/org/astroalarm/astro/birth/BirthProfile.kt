package org.astroalarm.astro.birth

import java.time.LocalDate
import java.time.LocalTime

/**
 * Natal profile (multi-profile list). Birth place is separate from Settings observer place.
 * @param zoneId IANA id used with TZDB for historical UTC conversion
 * @param zoneIsFallback true when zone fell back to device default (warn in UI)
 */
data class BirthProfile(
    val id: String,
    val label: String,
    val birthDate: LocalDate,
    val birthTime: LocalTime? = LocalTime.of(12, 0),
    val timeUnknown: Boolean = false,
    val cityName: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val zoneId: String = "UTC",
    val zoneIsFallback: Boolean = false,
    val active: Boolean = false,
) {
    val hasPlace: Boolean get() = cityName.isNotBlank() && lat in -90.0..90.0 && lon in -180.0..180.0
    val polarWarning: Boolean get() = kotlin.math.abs(lat) > 66.0
    val canComputeAscendant: Boolean get() = hasPlace && !timeUnknown && birthTime != null
}
