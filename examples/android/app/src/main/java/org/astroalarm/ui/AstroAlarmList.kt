package org.astroalarm.ui

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.foss.goldenpath.R
import org.astroalarm.astro.alarm.AlarmFireIdentity
import org.astroalarm.astro.alarm.AlarmGroupKind
import org.astroalarm.astro.alarm.AlarmGroupSections
import org.astroalarm.astro.alarm.AlarmTargetCopy
import org.astroalarm.astro.alarm.AstroNextFire
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
        alarm to AstroNextFire.nextInstant(alarm, place, all = alarms)
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
            onDelete = { onDelete(alarm) },
            modifier = Modifier.animateItem(),
            peerNote = alarmPeerNote(alarm, alarms),
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
    AlarmGroupSections.nonEmpty(alarms).forEach { (kind, rows) ->
        item(key = "hdr-${kind.name}") {
            SectionHeader(title = groupedTitle(kind))
        }
        val sorted = rows.sortedBy {
            AstroNextFire.nextInstant(it, place)?.toEpochMilli() ?: Long.MAX_VALUE
        }
        items(sorted, key = { it.id }) { alarm ->
            val nextInstant = AstroNextFire.nextInstant(alarm, place, all = alarms)
            val formatted = nextInstant?.let { formatInstant(it, place) }
            AstroAlarmRow(
                alarm = alarm,
                nextFireFormatted = formatted,
                onToggle = { onToggle(alarm, it) },
                onEdit = { onEdit(alarm) },
                onDelete = { onDelete(alarm) },
                modifier = Modifier.animateItem(),
                peerNote = alarmPeerNote(alarm, alarms),
            )
        }
    }
}

@Composable
private fun groupedTitle(kind: AlarmGroupKind): String = when (kind) {
    AlarmGroupKind.Solar -> stringResource(R.string.astro_section_solar)
    AlarmGroupKind.Lunar -> stringResource(R.string.astro_section_lunar)
    AlarmGroupKind.Zodiac -> stringResource(R.string.astro_section_zodiac)
    AlarmGroupKind.Clock -> stringResource(R.string.astro_section_clock)
    AlarmGroupKind.Seasonal -> stringResource(R.string.astro_section_seasonal)
    AlarmGroupKind.Planet -> stringResource(R.string.astro_section_planet)
    AlarmGroupKind.Natal -> stringResource(R.string.astro_tab_natal)
}

@Composable
internal fun alarmPeerNote(alarm: AstroAlarm, alarms: List<AstroAlarm>): String? {
    val peer = AlarmFireIdentity.otherPeer(alarm.target, alarm.id, alarms) ?: return null
    val name = peer.label.ifBlank { AlarmTargetCopy.fallback(LocalContext.current.resources, peer.target) }
    return stringResource(R.string.astro_alarm_also_listed, name)
}
