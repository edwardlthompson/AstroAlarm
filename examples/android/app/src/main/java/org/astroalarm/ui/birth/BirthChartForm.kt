package org.astroalarm.ui.birth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.goldenpath.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.astroalarm.astro.birth.BirthProfile
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.place.AstroPlaceFinder
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Composable
fun BirthChartFormSection(
    initial: BirthProfile?,
    onSave: (BirthProfile) -> Unit,
) {
    val context = LocalContext.current
    var label by remember(initial?.id) { mutableStateOf(initial?.label ?: "") }
    var dateText by remember(initial?.id) { mutableStateOf(initial?.birthDate?.toString() ?: "1990-01-01") }
    var timeText by remember(initial?.id) { mutableStateOf(initial?.birthTime?.toString()?.take(5) ?: "12:00") }
    var timeUnknown by remember(initial?.id) { mutableStateOf(initial?.timeUnknown ?: false) }
    var placeQuery by remember(initial?.id) { mutableStateOf(initial?.cityName ?: "") }
    var selected by remember(initial?.id) {
        mutableStateOf(
            initial?.takeIf { it.hasPlace }?.let {
                AstroPlace(it.cityName, it.lat, it.lon, it.zoneId)
            },
        )
    }
    var suggestions by remember { mutableStateOf<List<AstroPlace>>(emptyList()) }

    LaunchedEffect(placeQuery) {
        suggestions = if (placeQuery.trim().length >= 2) {
            withContext(Dispatchers.IO) { AstroPlaceFinder.searchCities(context, placeQuery) }
        } else {
            emptyList()
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = label,
            onValueChange = { label = it },
            label = { Text(stringResource(R.string.astro_birth_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = dateText,
            onValueChange = { dateText = it },
            label = { Text(stringResource(R.string.astro_birth_date)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(stringResource(R.string.astro_birth_date_hint)) },
        )
        OutlinedTextField(
            value = timeText,
            onValueChange = { timeText = it },
            label = { Text(stringResource(R.string.astro_birth_time)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !timeUnknown,
            placeholder = { Text(stringResource(R.string.astro_birth_time_hint)) },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(R.string.astro_birth_time_unknown))
            Switch(checked = timeUnknown, onCheckedChange = { timeUnknown = it })
        }
        OutlinedTextField(
            value = placeQuery,
            onValueChange = { placeQuery = it },
            label = { Text(stringResource(R.string.astro_birth_place)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(stringResource(R.string.astro_birth_search_hint)) },
        )
        suggestions.take(5).forEach { place ->
            Text(
                text = "${place.cityName} (${place.zoneId})",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selected = place
                        placeQuery = place.cityName
                        suggestions = emptyList()
                    }
                    .padding(vertical = 6.dp),
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Button(
            onClick = {
                val date = runCatching { LocalDate.parse(dateText.trim()) }.getOrNull() ?: return@Button
                val time = if (timeUnknown) {
                    null
                } else {
                    runCatching {
                        val parts = timeText.trim().split(":")
                        LocalTime.of(parts[0].toInt(), parts.getOrNull(1)?.toInt() ?: 0)
                    }.getOrNull() ?: return@Button
                }
                val place = selected ?: return@Button
                onSave(
                    BirthProfile(
                        id = initial?.id ?: UUID.randomUUID().toString(),
                        label = label.ifBlank { place.cityName },
                        birthDate = date,
                        birthTime = time,
                        timeUnknown = timeUnknown,
                        cityName = place.cityName,
                        lat = place.latitude,
                        lon = place.longitude,
                        zoneId = place.zoneId,
                        zoneIsFallback = false,
                        active = true,
                    ),
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selected != null && label.isNotBlank(),
        ) {
            Text(stringResource(R.string.astro_birth_save))
        }
    }
}
