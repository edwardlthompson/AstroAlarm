package org.astroalarm.widget

import org.astroalarm.astro.zodiac.ZodiacCalculator
import java.time.LocalDate
import java.time.ZoneId

/** Calendar month-start ticks; labels sit at the ecliptic midpoint of each month. */
object MonthRimTicks {
    val LABELS = listOf("Ja", "Fe", "Mr", "Ap", "My", "Jn", "Jl", "Au", "Se", "Oc", "No", "De")

    data class Mark(val tickDeg: Float, val labelDeg: Float, val label: String)

    fun marks(year: Int, zone: ZoneId, sunLonNow: Double, aNoonDeg: Float): List<Mark> {
        val lons = (1..12).map { month ->
            ZodiacCalculator.sunLongitudeAt(LocalDate.of(year, month, 1).atStartOfDay(zone).toInstant())
        } + ZodiacCalculator.sunLongitudeAt(LocalDate.of(year + 1, 1, 1).atStartOfDay(zone).toInstant())
        return LABELS.mapIndexed { i, label ->
            val tickLon = lons[i]
            val labelLon = midLon(lons[i], lons[i + 1])
            Mark(
                tickDeg = aNoonDeg + (tickLon - sunLonNow).toFloat(),
                labelDeg = aNoonDeg + (labelLon - sunLonNow).toFloat(),
                label = label,
            )
        }
    }

    fun midLon(fromDeg: Double, toDeg: Double): Double {
        var span = toDeg - fromDeg
        while (span < 0.0) span += 360.0
        while (span >= 360.0) span -= 360.0
        var mid = fromDeg + span / 2.0
        while (mid >= 360.0) mid -= 360.0
        while (mid < 0.0) mid += 360.0
        return mid
    }
}
