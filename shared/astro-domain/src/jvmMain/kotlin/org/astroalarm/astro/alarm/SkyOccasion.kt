package org.astroalarm.astro.alarm

import java.time.LocalDate

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
