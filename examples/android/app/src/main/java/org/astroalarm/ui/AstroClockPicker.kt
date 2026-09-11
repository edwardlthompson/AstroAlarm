package org.astroalarm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.foss.goldenpath.R
import java.util.Locale

@Composable
fun ClockTimePicker(
    hour: Int,
    minute: Int,
    onTimeChange: (Int, Int) -> Unit,
) {
    val presets = listOf(6 to 0, 7 to 0, 8 to 0, 12 to 0, 18 to 0, 22 to 0)
    val presetText = presets.firstOrNull { it.first == hour && it.second == minute }
        ?.let { String.format(Locale.getDefault(), "%02d:%02d", it.first, it.second) }
        ?: stringResource(R.string.astro_action_change)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = String.format(Locale.getDefault(), "%02d:%02d", hour, minute),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AstroNumberWheel(
                    value = hour,
                    range = (0..23).toList(),
                    label = stringResource(R.string.astro_time_hour),
                    onValueChange = { onTimeChange(it, minute) },
                )
                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
                AstroNumberWheel(
                    value = minute,
                    range = (0..59).toList(),
                    label = stringResource(R.string.astro_time_minute),
                    onValueChange = { onTimeChange(hour, it) },
                )
            }
            AstroMenuDropdown(
                label = stringResource(R.string.astro_time_hour),
                selectedText = presetText,
                options = presets.map { (h, m) ->
                    (h to m) to String.format(Locale.getDefault(), "%02d:%02d", h, m)
                },
                onSelect = { (h, m) -> onTimeChange(h, m) },
            )
        }
    }
}
