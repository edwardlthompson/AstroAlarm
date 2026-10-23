package org.astroalarm.wear

import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.PI
import kotlin.math.sin

/**
 * Simple day/night rise–set from device [ZoneId] (no lat/lon, no phone sync).
 * Uses local noon ≈ 12:00 and a mild northern-season day-length swing.
 */
object WearDayNightApprox {
    data class RiseSet(
        val riseMinutes: Float,
        val setMinutes: Float,
    )

    fun riseSetMinutes(date: LocalDate, zone: ZoneId): RiseSet {
        // Caller supplies the civil [date] for [zone]; zone id is kept for v1 API + future lon proxy.
        check(zone.id.isNotBlank()) { "zone required" }
        val doy = date.dayOfYear
        val season = sin(2.0 * PI * (doy - 81) / 365.25)
        val halfDayHours = 6.0 + 2.0 * season
        val noonMin = 12f * 60f
        val halfMin = (halfDayHours * 60.0).toFloat()
        return RiseSet(noonMin - halfMin, noonMin + halfMin)
    }

    fun riseSetCivilAngles(date: LocalDate, zone: ZoneId): Pair<Float, Float> {
        val rs = riseSetMinutes(date, zone)
        return minutesToCivil(rs.riseMinutes) to minutesToCivil(rs.setMinutes)
    }

    fun minutesToCivil(minutes: Float): Float = minutes / 1440f * 360f
}
