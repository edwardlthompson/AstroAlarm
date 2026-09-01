package org.astroalarm.ui.solarterm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.foss.goldenpath.R
import org.astroalarm.astro.settings.AstroDisplayPreferences

@Composable
fun SolarTermSettingsSection(
    prefs: AstroDisplayPreferences,
    modifier: Modifier = Modifier,
) {
    val show by prefs.showSolarTermsYear.collectAsState()
    val traditional by prefs.solarTermTraditional.collectAsState()
    val localSeasons by prefs.solarTermLocalSeasons.collectAsState()
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.solar_term_settings_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(stringResource(R.string.solar_term_section_desc), style = MaterialTheme.typography.bodySmall)
            ToggleLine(stringResource(R.string.solar_term_toggle_year), show, prefs::setShowSolarTermsYear)
            ToggleLine(stringResource(R.string.solar_term_toggle_traditional), traditional, prefs::setSolarTermTraditional)
            ToggleLine(stringResource(R.string.solar_term_toggle_local_seasons), localSeasons, prefs::setSolarTermLocalSeasons)
        }
    }
}

@Composable
private fun ToggleLine(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
