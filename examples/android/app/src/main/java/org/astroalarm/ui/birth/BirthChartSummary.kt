package org.astroalarm.ui.birth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.foss.goldenpath.R
import org.astroalarm.astro.birth.BirthProfile
import org.astroalarm.astro.birth.NatalChart

@Composable
fun BirthChartSummarySection(profile: BirthProfile?, chart: NatalChart?) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.astro_birth_summary),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        if (profile == null || chart == null) {
            Text(stringResource(R.string.astro_birth_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
            return
        }
        Text(formatPoint(stringResource(R.string.astro_birth_sun), chart.sun))
        Text(formatPoint(stringResource(R.string.astro_birth_moon), chart.moon))
        Text(formatPoint(stringResource(R.string.astro_birth_rising), chart.ascendant))
        Text(formatPoint(stringResource(R.string.astro_birth_mc), chart.midheaven))
        if (chart.timeUnknown) {
            Text(stringResource(R.string.astro_birth_warn_time), color = MaterialTheme.colorScheme.error)
        }
        if (profile.polarWarning) {
            Text(stringResource(R.string.astro_birth_warn_polar), color = MaterialTheme.colorScheme.tertiary)
        }
        if (profile.zoneIsFallback) {
            Text(stringResource(R.string.astro_birth_warn_zone), color = MaterialTheme.colorScheme.tertiary)
        }
        BirthChartHousesSection(chart = chart)
    }
}
