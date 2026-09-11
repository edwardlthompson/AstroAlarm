package org.astroalarm.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

private enum class TargetKind { Solar, Lunar, Zodiac, Clock, Seasonal, Planet, Natal }

@Composable
fun TargetTypeSelector(
    currentTarget: AlarmTarget,
    onTargetChange: (AlarmTarget) -> Unit,
    natalProfileId: String? = null,
    natalAscOk: Boolean = false,
) {
    val kind = when (currentTarget) {
        is AlarmTarget.Solar -> TargetKind.Solar
        is AlarmTarget.Lunar -> TargetKind.Lunar
        is AlarmTarget.Zodiac -> TargetKind.Zodiac
        is AlarmTarget.CustomClock -> TargetKind.Clock
        is AlarmTarget.SolarTerm -> TargetKind.Seasonal
        is AlarmTarget.Planet, is AlarmTarget.PlanetAlign, is AlarmTarget.AllPlanetsAlign -> TargetKind.Planet
        else -> TargetKind.Natal
    }
    val options = buildList {
        add(TargetKind.Solar to stringResource(R.string.astro_tab_sun))
        add(TargetKind.Lunar to stringResource(R.string.astro_tab_moon))
        add(TargetKind.Zodiac to stringResource(R.string.astro_tab_zodiac))
        add(TargetKind.Clock to stringResource(R.string.astro_tab_clock))
        add(TargetKind.Seasonal to stringResource(R.string.astro_tab_seasonal))
        add(TargetKind.Planet to stringResource(R.string.astro_tab_planet))
        if (natalProfileId != null) add(TargetKind.Natal to stringResource(R.string.astro_tab_natal))
    }
    AstroMenuDropdown(
        label = stringResource(R.string.astro_field_target_type),
        selectedText = options.first { it.first == kind }.second,
        options = options,
        onSelect = { next ->
            if (next == kind) return@AstroMenuDropdown
            onTargetChange(
                when (next) {
                    TargetKind.Solar -> AlarmTarget.Solar(SolarEventType.Sunrise, 0)
                    TargetKind.Lunar -> AlarmTarget.Lunar(LunarEventType.FullMoon, 0)
                    TargetKind.Zodiac -> AlarmTarget.Zodiac(ZodiacSign.Aries, ZodiacPoint.Beginning, 0)
                    TargetKind.Clock -> AlarmTarget.CustomClock(7, 0)
                    TargetKind.Seasonal -> AlarmTarget.SolarTerm(SolarTerm.LICHUN, 0)
                    TargetKind.Planet -> AlarmTarget.Planet(PlanetBody.MARS, PlanetEventType.Rise, 0)
                    TargetKind.Natal -> if (natalAscOk && natalProfileId != null) {
                        AlarmTarget.NatalAscAspect(
                            org.astroalarm.astro.birth.NatalBody.SUN,
                            org.astroalarm.astro.model.NatalAspect.Conjunction,
                            natalProfileId,
                        )
                    } else {
                        AlarmTarget.MoonReturn(natalProfileId ?: "")
                    }
                },
            )
        },
    )
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
                    Text(
                        stringResource(R.string.astro_dialog_select_seasonal),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f).padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(SolarTerm.entries) { term ->
                            val on = term == selected
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    onSelect(term); show = false
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (on) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    },
                                ),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Text(
                                    "${term.glyph}  ${term.pinyin}  ${term.hans}  ·  ${SolarTermCopy.name(res, term)}",
                                    modifier = Modifier.padding(12.dp),
                                    fontWeight = if (on) FontWeight.Bold else FontWeight.Normal,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

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
    val modeKey = when (target) {
        is AlarmTarget.PlanetAlign -> "align"
        is AlarmTarget.AllPlanetsAlign -> "all"
        else -> "event:${(target as? AlarmTarget.Planet)?.event?.name ?: PlanetEventType.Rise.name}"
    }
    val modeOptions = buildList {
        eventChips(body).forEach { ev ->
            add("event:${ev.name}" to AlarmTargetCopy.planetEventLabel(ev))
        }
        add("align" to stringResource(R.string.astro_planet_align_with))
        add("all" to stringResource(R.string.astro_planet_all_align))
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AstroMenuDropdown(
            label = stringResource(R.string.astro_planet_body_title),
            selectedText = body.name.lowercase().replaceFirstChar { it.titlecase() },
            options = bodies.map { b ->
                b to b.name.lowercase().replaceFirstChar { it.titlecase() }
            },
            onSelect = { onTargetChange(retargetBody(target, it, offset)) },
            enabled = target !is AlarmTarget.AllPlanetsAlign,
        )
        AstroMenuDropdown(
            label = stringResource(R.string.astro_planet_event_title),
            selectedText = modeOptions.first { it.first == modeKey }.second,
            options = modeOptions,
            onSelect = { key ->
                when {
                    key == "align" -> {
                        val other = bodies.first { it != body }
                        onTargetChange(AlarmTarget.PlanetAlign(body, other, offset))
                    }
                    key == "all" -> onTargetChange(AlarmTarget.AllPlanetsAlign(offset))
                    key.startsWith("event:") -> {
                        val ev = PlanetEventType.valueOf(key.removePrefix("event:"))
                        onTargetChange(AlarmTarget.Planet(body, ev, offset))
                    }
                }
            },
        )
        if (target is AlarmTarget.PlanetAlign) {
            AstroMenuDropdown(
                label = stringResource(R.string.astro_planet_align_with),
                selectedText = target.bodyB.name.lowercase().replaceFirstChar { it.titlecase() },
                options = bodies.filter { it != target.bodyA }.map { b ->
                    b to b.name.lowercase().replaceFirstChar { it.titlecase() }
                },
                onSelect = { onTargetChange(target.copy(bodyB = it)) },
            )
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
    val core = listOf(
        PlanetEventType.Rise, PlanetEventType.Set,
        PlanetEventType.RetrogradeStart, PlanetEventType.DirectStart,
    )
    return core + if (body.isInner) {
        listOf(PlanetEventType.InferiorConjunction, PlanetEventType.SuperiorConjunction)
    } else {
        listOf(PlanetEventType.Opposition)
    }
}

private fun retargetBody(target: AlarmTarget, body: PlanetBody, offset: Int): AlarmTarget = when (target) {
    is AlarmTarget.PlanetAlign -> {
        val other = if (target.bodyB == body) {
            PlanetBody.entries.first { it != PlanetBody.EARTH && it != body }
        } else {
            target.bodyB
        }
        target.copy(bodyA = body, bodyB = other)
    }
    is AlarmTarget.Planet -> {
        val ev = when {
            body.isInner && target.event == PlanetEventType.Opposition ->
                PlanetEventType.InferiorConjunction
            !body.isInner && (
                target.event == PlanetEventType.InferiorConjunction ||
                    target.event == PlanetEventType.SuperiorConjunction
                ) -> PlanetEventType.Opposition
            else -> target.event
        }
        AlarmTarget.Planet(body, ev, offset)
    }
    else -> AlarmTarget.Planet(body, PlanetEventType.Rise, offset)
}
