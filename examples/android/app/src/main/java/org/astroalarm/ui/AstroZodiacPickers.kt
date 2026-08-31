package org.astroalarm.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.foss.goldenpath.R
import org.astroalarm.astro.zodiac.ZodiacPoint
import org.astroalarm.astro.zodiac.ZodiacSign

@Composable
fun ZodiacEventPicker(
    selectedSign: ZodiacSign,
    selectedPoint: ZodiacPoint,
    onSelect: (ZodiacSign, ZodiacPoint) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.astro_zodiac_event_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = AstroEventLabels.zodiacLabel(selectedSign, selectedPoint),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = AstroEventLabels.zodiacDescription(selectedSign, selectedPoint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { showDialog = true },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(R.string.astro_action_change),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }

    if (showDialog) {
        ZodiacEventListDialog(
            currentSign = selectedSign,
            currentPoint = selectedPoint,
            onSelect = { sign, point ->
                onSelect(sign, point)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun ZodiacEventListDialog(
    currentSign: ZodiacSign,
    currentPoint: ZodiacPoint,
    onSelect: (ZodiacSign, ZodiacPoint) -> Unit,
    onDismiss: () -> Unit
) {
    var activeSign by remember { mutableStateOf(currentSign) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.astro_dialog_select_zodiac),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ZodiacSign.entries.toTypedArray()) { sign ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (sign == activeSign) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "${sign.symbol} ${sign.englishName} (${sign.startLongitudeDeg.toInt()}° - ${(sign.startLongitudeDeg + 30).toInt() % 360}°)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    ZodiacPoint.entries.forEach { point ->
                                        val isSelected = sign == currentSign && point == currentPoint
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { onSelect(sign, point) },
                                            label = {
                                                val label = when (point) {
                                                    ZodiacPoint.Beginning -> stringResource(R.string.astro_zodiac_point_begin)
                                                    ZodiacPoint.Middle -> stringResource(R.string.astro_zodiac_point_mid)
                                                    ZodiacPoint.End -> stringResource(R.string.astro_zodiac_point_end)
                                                }
                                                Text(label, fontSize = 11.sp)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text(stringResource(R.string.astro_action_cancel))
                }
            }
        }
    }
}
