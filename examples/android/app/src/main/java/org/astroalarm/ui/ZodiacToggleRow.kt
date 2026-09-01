package org.astroalarm.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ZodiacToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
    ) {
        OverlayToggleLine(
            title = title,
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
        )
    }
}

@Composable
fun ClockOverlayToggles(
    showZodiac: Boolean,
    onShowZodiacChange: (Boolean) -> Unit,
    showEventTimes: Boolean,
    onShowEventTimesChange: (Boolean) -> Unit,
    showMonthTicks: Boolean,
    onShowMonthTicksChange: (Boolean) -> Unit,
    zodiacTitle: String,
    eventTimesTitle: String,
    monthTicksTitle: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)) {
            OverlayToggleLine(zodiacTitle, showZodiac, onShowZodiacChange)
            OverlayToggleLine(eventTimesTitle, showEventTimes, onShowEventTimesChange)
            OverlayToggleLine(monthTicksTitle, showMonthTicks, onShowMonthTicksChange)
        }
    }
}

@Composable
private fun OverlayToggleLine(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.semantics { contentDescription = title },
        )
    }
}
