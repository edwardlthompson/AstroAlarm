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
import org.astroalarm.astro.birth.NatalChart
import org.astroalarm.astro.birth.WholeSignHouses

@Composable
fun BirthChartHousesSection(chart: NatalChart?) {
    val asc = chart?.ascendant
    if (chart == null || asc == null || chart.timeUnknown) return
    val cusps = WholeSignHouses.cusps(asc)
    val placements = WholeSignHouses.placements(chart)
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.astro_birth_houses),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = stringResource(R.string.astro_birth_houses_whole_sign),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        cusps.forEach { c ->
            Text(
                text = stringResource(
                    R.string.astro_birth_house_row,
                    c.house,
                    c.sign.englishName,
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        if (placements.isNotEmpty()) {
            Text(
                text = stringResource(R.string.astro_birth_house_placements),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
            )
            placements.forEach { p ->
                Text(
                    text = stringResource(
                        R.string.astro_birth_house_body,
                        p.body.name.lowercase().replaceFirstChar { it.titlecase() },
                        p.house,
                        p.sign.englishName,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
