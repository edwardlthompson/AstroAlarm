package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm

enum class AlarmGroupKind {
    Solar,
    Lunar,
    Zodiac,
    Clock,
    Seasonal,
    Planet,
    Natal,
}

object AlarmGroupSections {
    fun kindOf(target: AlarmTarget): AlarmGroupKind = when (target) {
        is AlarmTarget.Solar -> AlarmGroupKind.Solar
        is AlarmTarget.Lunar -> AlarmGroupKind.Lunar
        is AlarmTarget.Zodiac -> AlarmGroupKind.Zodiac
        is AlarmTarget.CustomClock -> AlarmGroupKind.Clock
        is AlarmTarget.SolarTerm -> AlarmGroupKind.Seasonal
        is AlarmTarget.Planet,
        is AlarmTarget.PlanetAlign,
        is AlarmTarget.AllPlanetsAlign,
        -> AlarmGroupKind.Planet
        else -> AlarmGroupKind.Natal
    }

    /** Grouped view skips empty domains so first-run is not seven empty essays. */
    fun nonEmpty(alarms: List<AstroAlarm>): List<Pair<AlarmGroupKind, List<AstroAlarm>>> =
        AlarmGroupKind.entries.map { kind ->
            kind to alarms.filter { kindOf(it.target) == kind }
        }.filter { it.second.isNotEmpty() }
}
