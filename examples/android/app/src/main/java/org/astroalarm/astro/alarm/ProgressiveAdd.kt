package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget

enum class ProgressiveKind { Solar, Lunar, Zodiac, Clock, Seasonal, Planet, Natal }

/** Full alarm-type catalog for the editor dropdown. */
object ProgressiveAdd {
    fun kindOf(target: AlarmTarget): ProgressiveKind = when (target) {
        is AlarmTarget.Solar -> ProgressiveKind.Solar
        is AlarmTarget.Lunar -> ProgressiveKind.Lunar
        is AlarmTarget.Zodiac -> ProgressiveKind.Zodiac
        is AlarmTarget.CustomClock -> ProgressiveKind.Clock
        is AlarmTarget.SolarTerm -> ProgressiveKind.Seasonal
        is AlarmTarget.Planet, is AlarmTarget.PlanetAlign, is AlarmTarget.AllPlanetsAlign ->
            ProgressiveKind.Planet
        else -> ProgressiveKind.Natal
    }

    fun kinds(hasNatal: Boolean): List<ProgressiveKind> = buildList {
        add(ProgressiveKind.Solar)
        add(ProgressiveKind.Lunar)
        add(ProgressiveKind.Zodiac)
        add(ProgressiveKind.Clock)
        add(ProgressiveKind.Seasonal)
        add(ProgressiveKind.Planet)
        if (hasNatal) add(ProgressiveKind.Natal)
    }
}
