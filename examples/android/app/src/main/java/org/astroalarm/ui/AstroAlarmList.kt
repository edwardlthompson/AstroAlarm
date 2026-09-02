package org.astroalarm.ui

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.stringResource
import dev.foss.goldenpath.R
import org.astroalarm.astro.alarm.AstroNextFire
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.place.AstroPlace
import java.time.Instant

enum class AlarmViewMode {
    NextDue,
    Grouped
}

fun LazyListScope.renderNextDueAlarms(
    alarms: List<AstroAlarm>,
    place: AstroPlace?,
    onToggle: (AstroAlarm, Boolean) -> Unit,
    onEdit: (AstroAlarm) -> Unit,
    onDelete: (AstroAlarm) -> Unit,
) {
    val alarmWithNext = alarms.map { alarm ->
        alarm to AstroNextFire.nextInstant(alarm, place)
    }.sortedWith(
        compareBy<Pair<AstroAlarm, Instant?>> { (alarm, instant) ->
            if (!alarm.enabled) 2 else if (instant == null) 1 else 0
        }.thenBy { it.second?.toEpochMilli() ?: Long.MAX_VALUE }
    )

    items(alarmWithNext, key = { it.first.id }) { (alarm, nextInstant) ->
        val formatted = nextInstant?.let { formatInstant(it, place) }
        AstroAlarmRow(
            alarm = alarm,
            nextFireFormatted = formatted,
            onToggle = { onToggle(alarm, it) },
            onEdit = { onEdit(alarm) },
            onDelete = { onDelete(alarm) }
        )
    }
}

fun LazyListScope.renderGroupedAlarms(
    alarms: List<AstroAlarm>,
    place: AstroPlace?,
    onToggle: (AstroAlarm, Boolean) -> Unit,
    onEdit: (AstroAlarm) -> Unit,
    onDelete: (AstroAlarm) -> Unit,
) {
    item {
        SectionHeader(title = "☀️ " + stringResource(R.string.astro_section_solar))
    }
    val solarAlarms = alarms.filter { it.target is AlarmTarget.Solar }
        .sortedBy { AstroNextFire.nextInstant(it, place)?.toEpochMilli() ?: Long.MAX_VALUE }
    if (solarAlarms.isEmpty()) {
        item { EmptySectionNote(stringResource(R.string.astro_empty_solar)) }
    } else {
        items(solarAlarms, key = { it.id }) { alarm ->
            val nextInstant = AstroNextFire.nextInstant(alarm, place)
            val formatted = nextInstant?.let { formatInstant(it, place) }
            AstroAlarmRow(
                alarm = alarm,
                nextFireFormatted = formatted,
                onToggle = { onToggle(alarm, it) },
                onEdit = { onEdit(alarm) },
                onDelete = { onDelete(alarm) }
            )
        }
    }

    item {
        SectionHeader(title = "🌙 " + stringResource(R.string.astro_section_lunar))
    }
    val lunarAlarms = alarms.filter { it.target is AlarmTarget.Lunar }
        .sortedBy { AstroNextFire.nextInstant(it, place)?.toEpochMilli() ?: Long.MAX_VALUE }
    if (lunarAlarms.isEmpty()) {
        item { EmptySectionNote(stringResource(R.string.astro_empty_lunar)) }
    } else {
        items(lunarAlarms, key = { it.id }) { alarm ->
            val nextInstant = AstroNextFire.nextInstant(alarm, place)
            val formatted = nextInstant?.let { formatInstant(it, place) }
            AstroAlarmRow(
                alarm = alarm,
                nextFireFormatted = formatted,
                onToggle = { onToggle(alarm, it) },
                onEdit = { onEdit(alarm) },
                onDelete = { onDelete(alarm) }
            )
        }
    }

    item {
        SectionHeader(title = "♈ " + stringResource(R.string.astro_section_zodiac))
    }
    val zodiacAlarms = alarms.filter { it.target is AlarmTarget.Zodiac }
        .sortedBy { AstroNextFire.nextInstant(it, place)?.toEpochMilli() ?: Long.MAX_VALUE }
    if (zodiacAlarms.isEmpty()) {
        item { EmptySectionNote(stringResource(R.string.astro_empty_zodiac)) }
    } else {
        items(zodiacAlarms, key = { it.id }) { alarm ->
            val nextInstant = AstroNextFire.nextInstant(alarm, place)
            val formatted = nextInstant?.let { formatInstant(it, place) }
            AstroAlarmRow(
                alarm = alarm,
                nextFireFormatted = formatted,
                onToggle = { onToggle(alarm, it) },
                onEdit = { onEdit(alarm) },
                onDelete = { onDelete(alarm) }
            )
        }
    }

    item {
        SectionHeader(title = "⏰ " + stringResource(R.string.astro_section_clock))
    }
    val clockAlarms = alarms.filter { it.target is AlarmTarget.CustomClock }
        .sortedBy { AstroNextFire.nextInstant(it, place)?.toEpochMilli() ?: Long.MAX_VALUE }
    if (clockAlarms.isEmpty()) {
        item { EmptySectionNote(stringResource(R.string.astro_empty_clock)) }
    } else {
        items(clockAlarms, key = { it.id }) { alarm ->
            val nextInstant = AstroNextFire.nextInstant(alarm, place)
            val formatted = nextInstant?.let { formatInstant(it, place) }
            AstroAlarmRow(
                alarm = alarm,
                nextFireFormatted = formatted,
                onToggle = { onToggle(alarm, it) },
                onEdit = { onEdit(alarm) },
                onDelete = { onDelete(alarm) }
            )
        }
    }

    item {
        SectionHeader(title = "🍃 " + stringResource(R.string.astro_section_seasonal))
    }
    val seasonalAlarms = alarms.filter { it.target is AlarmTarget.SolarTerm }
        .sortedBy { AstroNextFire.nextInstant(it, place)?.toEpochMilli() ?: Long.MAX_VALUE }
    if (seasonalAlarms.isEmpty()) {
        item { EmptySectionNote(stringResource(R.string.astro_empty_seasonal)) }
    } else {
        items(seasonalAlarms, key = { it.id }) { alarm ->
            val nextInstant = AstroNextFire.nextInstant(alarm, place)
            val formatted = nextInstant?.let { formatInstant(it, place) }
            AstroAlarmRow(
                alarm = alarm,
                nextFireFormatted = formatted,
                onToggle = { onToggle(alarm, it) },
                onEdit = { onEdit(alarm) },
                onDelete = { onDelete(alarm) }
            )
        }
    }

    item {
        SectionHeader(title = "🪐 " + stringResource(R.string.astro_section_planet))
    }
    val planetAlarms = alarms.filter {
        it.target is AlarmTarget.Planet || it.target is AlarmTarget.PlanetAlign || it.target is AlarmTarget.AllPlanetsAlign
    }.sortedBy { AstroNextFire.nextInstant(it, place)?.toEpochMilli() ?: Long.MAX_VALUE }
    if (planetAlarms.isEmpty()) {
        item { EmptySectionNote(stringResource(R.string.astro_empty_planet)) }
    } else {
        items(planetAlarms, key = { it.id }) { alarm ->
            val nextInstant = AstroNextFire.nextInstant(alarm, place)
            val formatted = nextInstant?.let { formatInstant(it, place) }
            AstroAlarmRow(
                alarm = alarm,
                nextFireFormatted = formatted,
                onToggle = { onToggle(alarm, it) },
                onEdit = { onEdit(alarm) },
                onDelete = { onDelete(alarm) }
            )
        }
    }
}
