package org.astroalarm.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.foss.goldenpath.R
import org.astroalarm.astro.alarm.AlarmTargetCopy
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.LunarEventType
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.zodiac.ZodiacPoint
import org.astroalarm.astro.zodiac.ZodiacSign
import org.astroalarm.sol.PlanetBody
import org.astroalarm.sol.PlanetEventType
import org.astroalarm.solarterm.SolarTerm
import org.astroalarm.ui.solarterm.SolarTermCopy

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TargetTypeSelector(currentTarget: AlarmTarget, onTargetChange: (AlarmTarget) -> Unit) {
    val planetCat = currentTarget is AlarmTarget.Planet ||
        currentTarget is AlarmTarget.PlanetAlign || currentTarget is AlarmTarget.AllPlanetsAlign
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = currentTarget is AlarmTarget.Solar,
            onClick = { if (currentTarget !is AlarmTarget.Solar) onTargetChange(AlarmTarget.Solar(SolarEventType.Sunrise, 0)) },
            label = { Text(stringResource(R.string.astro_tab_sun)) }
        )
        FilterChip(
            selected = currentTarget is AlarmTarget.Lunar,
            onClick = { if (currentTarget !is AlarmTarget.Lunar) onTargetChange(AlarmTarget.Lunar(LunarEventType.FullMoon, 0)) },
            label = { Text(stringResource(R.string.astro_tab_moon)) }
        )
        FilterChip(
            selected = currentTarget is AlarmTarget.Zodiac,
            onClick = { if (currentTarget !is AlarmTarget.Zodiac) onTargetChange(AlarmTarget.Zodiac(ZodiacSign.Aries, ZodiacPoint.Beginning, 0)) },
            label = { Text(stringResource(R.string.astro_tab_zodiac)) }
        )
        FilterChip(
            selected = currentTarget is AlarmTarget.CustomClock,
            onClick = { if (currentTarget !is AlarmTarget.CustomClock) onTargetChange(AlarmTarget.CustomClock(7, 0)) },
            label = { Text(stringResource(R.string.astro_tab_clock)) }
        )
        FilterChip(
            selected = currentTarget is AlarmTarget.SolarTerm,
            onClick = { if (currentTarget !is AlarmTarget.SolarTerm) onTargetChange(AlarmTarget.SolarTerm(SolarTerm.LICHUN, 0)) },
            label = { Text(stringResource(R.string.astro_tab_seasonal)) }
        )
        FilterChip(
            selected = planetCat,
            onClick = { if (!planetCat) onTargetChange(AlarmTarget.Planet(PlanetBody.MARS, PlanetEventType.Rise, 0)) },
            label = { Text(stringResource(R.string.astro_tab_planet)) }
        )
    }
}

@Composable
fun SeasonalTermPicker(selected: SolarTerm, onSelect: (SolarTerm) -> Unit) {
    var show by remember { mutableStateOf(false) }
    val res = LocalContext.current.resources
    OutlinedEventCard(
        title = stringResource(R.string.astro_seasonal_event_title),
        value = "${selected.glyph} ${selected.pinyin} · ${SolarTermCopy.name(res, selected)}",
        onClick = { show = true },
    )
    if (show) {
        Dialog(onDismissRequest = { show = false }) {
            Card(Modifier.fillMaxWidth().fillMaxHeight(0.85f), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.astro_dialog_select_seasonal), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    LazyColumn(Modifier.weight(1f).padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(SolarTerm.entries) { term ->
                            val on = term == selected
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onSelect(term); show = false },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (on) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "${term.glyph}  ${term.pinyin}  ${term.hans}  ·  ${SolarTermCopy.name(res, term)}",
                                    modifier = Modifier.padding(12.dp),
                                    fontWeight = if (on) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlanetTargetPicker(target: AlarmTarget, onTargetChange: (AlarmTarget) -> Unit) {
    val offset = when (target) {
        is AlarmTarget.Planet -> target.offsetMinutes
        is AlarmTarget.PlanetAlign -> target.offsetMinutes
        is AlarmTarget.AllPlanetsAlign -> target.offsetMinutes
        else -> 0
    }
    val body = when (target) {
        is AlarmTarget.Planet -> target.body
        is AlarmTarget.PlanetAlign -> target.bodyA
        else -> PlanetBody.MARS
    }
    val bodies = PlanetBody.entries.filter { it != PlanetBody.EARTH }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.astro_planet_body_title), style = MaterialTheme.typography.labelMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            bodies.forEach { b ->
                FilterChip(
                    selected = body == b && target !is AlarmTarget.AllPlanetsAlign,
                    onClick = { onTargetChange(retargetBody(target, b, offset)) },
                    label = { Text(b.name.lowercase().replaceFirstChar { it.titlecase() }) }
                )
            }
        }
        Text(stringResource(R.string.astro_planet_event_title), style = MaterialTheme.typography.labelMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            eventChips(body).forEach { ev ->
                FilterChip(
                    selected = target is AlarmTarget.Planet && target.event == ev,
                    onClick = { onTargetChange(AlarmTarget.Planet(body, ev, offset)) },
                    label = { Text(AlarmTargetCopy.planetEventLabel(ev)) }
                )
            }
            FilterChip(
                selected = target is AlarmTarget.PlanetAlign,
                onClick = {
                    val other = bodies.first { it != body }
                    onTargetChange(AlarmTarget.PlanetAlign(body, other, offset))
                },
                label = { Text(stringResource(R.string.astro_planet_align_with)) }
            )
            FilterChip(
                selected = target is AlarmTarget.AllPlanetsAlign,
                onClick = { onTargetChange(AlarmTarget.AllPlanetsAlign(offset)) },
                label = { Text(stringResource(R.string.astro_planet_all_align)) }
            )
        }
        if (target is AlarmTarget.PlanetAlign) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                bodies.filter { it != target.bodyA }.forEach { b ->
                    FilterChip(
                        selected = target.bodyB == b,
                        onClick = { onTargetChange(target.copy(bodyB = b)) },
                        label = { Text(b.name.lowercase().replaceFirstChar { it.titlecase() }) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OutlinedEventCard(title: String, value: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

private fun eventChips(body: PlanetBody): List<PlanetEventType> {
    val core = listOf(PlanetEventType.Rise, PlanetEventType.Set, PlanetEventType.RetrogradeStart, PlanetEventType.DirectStart)
    return core + if (body.isInner) {
        listOf(PlanetEventType.InferiorConjunction, PlanetEventType.SuperiorConjunction)
    } else {
        listOf(PlanetEventType.Opposition)
    }
}

private fun retargetBody(target: AlarmTarget, body: PlanetBody, offset: Int): AlarmTarget = when (target) {
    is AlarmTarget.PlanetAlign -> {
        val other = if (target.bodyB == body) PlanetBody.entries.first { it != PlanetBody.EARTH && it != body } else target.bodyB
        target.copy(bodyA = body, bodyB = other)
    }
    is AlarmTarget.Planet -> {
        val ev = when {
            body.isInner && target.event == PlanetEventType.Opposition -> PlanetEventType.InferiorConjunction
            !body.isInner && (target.event == PlanetEventType.InferiorConjunction || target.event == PlanetEventType.SuperiorConjunction) ->
                PlanetEventType.Opposition
            else -> target.event
        }
        AlarmTarget.Planet(body, ev, offset)
    }
    else -> AlarmTarget.Planet(body, PlanetEventType.Rise, offset)
}
