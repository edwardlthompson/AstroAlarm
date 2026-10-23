package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.sun.SolarCalculator
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

enum class SkyOccasionId(val id: String) {
    NAKED_EYE_2040("naked_eye_2040"),
    ALL_PLANETS_2492("all_planets_2492"),
    ;

    companion object {
        fun fromId(raw: String): SkyOccasionId? = entries.firstOrNull { it.id == raw }
    }
}

data class SkyOccasionDef(
    val id: SkyOccasionId,
    val date: LocalDate,
    val titleKey: String,
)

object SkyOccasionCatalog {
    val all: List<SkyOccasionDef> = listOf(
        SkyOccasionDef(SkyOccasionId.NAKED_EYE_2040, LocalDate.of(2040, 9, 8), "astro_occasion_2040"),
        SkyOccasionDef(SkyOccasionId.ALL_PLANETS_2492, LocalDate.of(2492, 5, 6), "astro_occasion_2492"),
    )

    fun def(id: SkyOccasionId): SkyOccasionDef = all.first { it.id == id }

    fun nextInstant(id: SkyOccasionId, place: AstroPlace?, now: Instant): Instant? {
        val zone = place?.zone ?: ZoneId.systemDefault()
        val def = def(id)
        val fire = when (id) {
            SkyOccasionId.NAKED_EYE_2040 -> sunsetOrFallback(def.date, place, zone)
            SkyOccasionId.ALL_PLANETS_2492 -> def.date.atTime(LocalTime.NOON).atZone(zone).toInstant()
        }
        return fire.takeIf { it.isAfter(now) }
    }

    fun nextOpen(
        catalogId: String,
        place: AstroPlace?,
        now: Instant,
        open: (Instant) -> Boolean,
    ): Instant? {
        val id = SkyOccasionId.fromId(catalogId) ?: return null
        return nextInstant(id, place, now)?.takeIf(open)
    }

    private fun sunsetOrFallback(date: LocalDate, place: AstroPlace?, zone: ZoneId): Instant {
        if (place != null && place.isValid) {
            SolarCalculator.calculate(
                SolarEventType.Sunset, date, place.latitude, place.longitude, place.zone,
            )?.let { return it }
        }
        return date.atTime(18, 0).atZone(zone).toInstant()
    }
}
