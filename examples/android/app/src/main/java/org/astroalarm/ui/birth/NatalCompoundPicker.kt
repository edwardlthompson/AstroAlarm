package org.astroalarm.ui.birth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.goldenpath.R
import org.astroalarm.astro.birth.NatalEventLinks
import org.astroalarm.astro.birth.NatalStoryKind
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.ui.AstroMenuDropdown

private enum class CompoundMode { Specific2, Specific3, Any2, Any3 }

/** Specific / any double-triple natal compound target editor (dropdowns). */
@Composable
fun NatalCompoundPicker(
    target: AlarmTarget,
    profileId: String,
    timeUnknown: Boolean,
    onTargetChange: (AlarmTarget) -> Unit,
) {
    val specific = target as? AlarmTarget.NatalCompoundSpecific
    val any = target as? AlarmTarget.NatalCompoundAny
    val mode = when {
        specific != null && specific.kinds.size >= 3 -> CompoundMode.Specific3
        specific != null -> CompoundMode.Specific2
        any?.arity == 3 -> CompoundMode.Any3
        else -> CompoundMode.Any2
    }
    val selected = specific?.kinds?.mapNotNull {
        runCatching { NatalStoryKind.valueOf(it) }.getOrNull()
    }?.toList() ?: emptyList()
    val modeOptions = listOf(
        CompoundMode.Specific2 to stringResource(R.string.astro_natal_compound_specific_double),
        CompoundMode.Specific3 to stringResource(R.string.astro_natal_compound_specific_triple),
        CompoundMode.Any2 to stringResource(R.string.astro_natal_compound_any_double),
        CompoundMode.Any3 to stringResource(R.string.astro_natal_compound_any_triple),
    )
    val kindOptions = NatalStoryKind.entries
        .filter { !(timeUnknown && NatalEventLinks.needsAscendant(it)) }
        .map { it to it.name }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AstroMenuDropdown(
            label = stringResource(R.string.astro_natal_compound_combine),
            selectedText = modeOptions.first { it.first == mode }.second,
            options = modeOptions,
            onSelect = { next ->
                when (next) {
                    CompoundMode.Specific2 -> onTargetChange(
                        AlarmTarget.NatalCompoundSpecific(
                            listOf(NatalStoryKind.SolarReturn.name, NatalStoryKind.MoonReturn.name),
                            profileId,
                        ),
                    )
                    CompoundMode.Specific3 -> onTargetChange(
                        AlarmTarget.NatalCompoundSpecific(
                            listOf(
                                NatalStoryKind.SolarReturn.name,
                                NatalStoryKind.MoonReturn.name,
                                NatalStoryKind.MercuryStation.name,
                            ),
                            profileId,
                        ),
                    )
                    CompoundMode.Any2 -> onTargetChange(AlarmTarget.NatalCompoundAny(2, profileId))
                    CompoundMode.Any3 -> onTargetChange(AlarmTarget.NatalCompoundAny(3, profileId))
                }
            },
        )
        if (specific != null) {
            val need = if (mode == CompoundMode.Specific3) 3 else 2
            for (slot in 0 until need) {
                val current = selected.getOrNull(slot) ?: NatalStoryKind.SolarReturn
                AstroMenuDropdown(
                    label = "Event ${slot + 1}",
                    selectedText = current.name,
                    options = kindOptions,
                    onSelect = { picked ->
                        val next = selected.toMutableList()
                        while (next.size < need) next.add(NatalStoryKind.MoonReturn)
                        next[slot] = picked
                        onTargetChange(
                            AlarmTarget.NatalCompoundSpecific(
                                next.take(need).map { it.name }.distinct().let { kinds ->
                                    if (kinds.size < need) {
                                        (kinds + NatalStoryKind.entries.map { it.name })
                                            .distinct()
                                            .take(need)
                                    } else {
                                        kinds
                                    }
                                },
                                profileId,
                                specific.offsetMinutes,
                            ),
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun NatalCombineButton(profileId: String, onTargetChange: (AlarmTarget) -> Unit) {
    TextButton(onClick = { onTargetChange(AlarmTarget.NatalCompoundAny(2, profileId)) }) {
        Text(stringResource(R.string.astro_natal_compound_combine))
    }
}
