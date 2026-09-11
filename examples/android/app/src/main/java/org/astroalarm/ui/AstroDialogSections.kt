package org.astroalarm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.foss.goldenpath.R
import org.astroalarm.astro.model.AlarmTarget
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

private enum class RepeatPreset { Once, Daily, Weekdays, Weekends, Custom }

@Composable
fun RepeatDaysSection(
    target: AlarmTarget,
    selectedDays: Set<DayOfWeek>,
    onDaysChange: (Set<DayOfWeek>) -> Unit,
) {
    val isSeasonal = (target is AlarmTarget.Solar && AstroEventLabels.isSeasonal(target.event)) ||
        target is AlarmTarget.Zodiac || target is AlarmTarget.SolarTerm ||
        target is AlarmTarget.PlanetAlign || target is AlarmTarget.AllPlanetsAlign ||
        (target is AlarmTarget.Planet && target.event != org.astroalarm.sol.PlanetEventType.Rise &&
            target.event != org.astroalarm.sol.PlanetEventType.Set)

    val weekdays = setOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
    )
    val weekends = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
    val preset = when {
        target is AlarmTarget.CustomClock && selectedDays.isEmpty() -> RepeatPreset.Once
        selectedDays.size == 7 || (target !is AlarmTarget.CustomClock && selectedDays.isEmpty()) -> RepeatPreset.Daily
        selectedDays == weekdays -> RepeatPreset.Weekdays
        selectedDays == weekends -> RepeatPreset.Weekends
        else -> RepeatPreset.Custom
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = stringResource(R.string.astro_field_repeat),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            if (isSeasonal) {
                Text(
                    text = stringResource(R.string.astro_repeat_yearly_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                val presets = buildList {
                    if (target is AlarmTarget.CustomClock) {
                        add(RepeatPreset.Once to stringResource(R.string.astro_repeat_once))
                    }
                    add(RepeatPreset.Daily to stringResource(R.string.astro_repeat_daily))
                    add(RepeatPreset.Weekdays to stringResource(R.string.astro_repeat_weekdays))
                    add(RepeatPreset.Weekends to stringResource(R.string.astro_repeat_weekends))
                    add(RepeatPreset.Custom to stringResource(R.string.astro_field_repeat_days))
                }
                AstroMenuDropdown(
                    label = stringResource(R.string.astro_field_repeat_preset),
                    selectedText = presets.first { it.first == preset }.second,
                    options = presets,
                    onSelect = { next ->
                        when (next) {
                            RepeatPreset.Once -> onDaysChange(emptySet())
                            RepeatPreset.Daily -> onDaysChange(DayOfWeek.entries.toSet())
                            RepeatPreset.Weekdays -> onDaysChange(weekdays)
                            RepeatPreset.Weekends -> onDaysChange(weekends)
                            RepeatPreset.Custom -> {
                                if (selectedDays.isEmpty() || selectedDays.size == 7) {
                                    onDaysChange(setOf(DayOfWeek.MONDAY))
                                }
                            }
                        }
                    },
                )
                if (preset == RepeatPreset.Custom) {
                    DayOfWeek.entries.forEach { d ->
                        val on = selectedDays.contains(d)
                        val name = d.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        AstroMenuDropdown(
                            label = name,
                            selectedText = if (on) stringResource(R.string.astro_birth_active) else "—",
                            options = listOf(
                                true to stringResource(R.string.astro_birth_active),
                                false to "—",
                            ),
                            onSelect = { enable ->
                                onDaysChange(if (enable) selectedDays + d else selectedDays - d)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AudioSettingsSection(
    toneEnabled: Boolean,
    onToneChange: (Boolean) -> Unit,
    toneTitle: String,
    onChooseTone: () -> Unit,
    ttsEnabled: Boolean,
    onTtsChange: (Boolean) -> Unit,
    vibrateEnabled: Boolean,
    onVibrateChange: (Boolean) -> Unit,
    mathUnlockEnabled: Boolean,
    onMathUnlockChange: (Boolean) -> Unit,
    snoozeMinutes: Int,
    onSnoozeChange: (Int) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = stringResource(R.string.astro_alarm_actions_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            ToggleRow(
                title = stringResource(R.string.astro_toggle_tone),
                desc = stringResource(R.string.astro_desc_play_tone),
                checked = toneEnabled,
                onCheckedChange = onToneChange,
            )
            if (toneEnabled) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.astro_alarm_sound),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(text = toneTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = onChooseTone,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(stringResource(R.string.astro_action_choose_sound), style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            ToggleRow(
                title = stringResource(R.string.astro_toggle_tts),
                desc = stringResource(R.string.astro_desc_speak_event),
                checked = ttsEnabled,
                onCheckedChange = onTtsChange,
            )
            ToggleRow(
                title = stringResource(R.string.astro_toggle_vibrate),
                desc = stringResource(R.string.astro_desc_vibrate),
                checked = vibrateEnabled,
                onCheckedChange = onVibrateChange,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ToggleRow(
                title = stringResource(R.string.astro_toggle_math_unlock),
                desc = stringResource(R.string.astro_desc_math_unlock),
                checked = mathUnlockEnabled,
                onCheckedChange = onMathUnlockChange,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            AstroMenuDropdown(
                label = stringResource(R.string.astro_snooze_duration_title),
                selectedText = "${snoozeMinutes}m",
                options = listOf(5, 10, 15, 20).map { it to "${it}m" },
                onSelect = onSnoozeChange,
            )
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
