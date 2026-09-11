package org.astroalarm.ui.birth

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.astroalarm.astro.alarm.AstroAlarmStore
import org.astroalarm.astro.birth.BirthChartCalculator
import org.astroalarm.astro.birth.BirthProfileStore

@Composable
fun BirthChartScreen(
    birthStore: BirthProfileStore,
    alarmStore: AstroAlarmStore,
    modifier: Modifier = Modifier,
) {
    val profiles by birthStore.profiles.collectAsState()
    val active = profiles.firstOrNull { it.active } ?: profiles.firstOrNull()
    val chart = active?.let { BirthChartCalculator.compute(it) }
    var showDetails by remember { mutableStateOf(false) }

    NatalWheelSection(
        profile = active,
        chart = chart,
        profiles = profiles,
        onSelectProfile = { birthStore.setActive(it) },
        onOpenDetails = { showDetails = true },
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 6.dp),
    )
    if (showDetails) {
        BirthChartDetailsDialog(
            profile = active,
            chart = chart,
            alarmStore = alarmStore,
            onSaveProfile = { birthStore.save(it.copy(active = true)) },
            onDismiss = { showDetails = false },
        )
    }
}

@Composable
internal fun formatPoint(label: String, point: org.astroalarm.astro.birth.EclipticPoint?): String {
    if (point == null) return "$label —"
    return "$label ${point.sign.englishName} ${"%.1f".format(point.degreeInSign)}°"
}
