package org.astroalarm.ui

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.foss.goldenpath.R
import org.astroalarm.astro.alarm.AlarmTargetCopy
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.ui.birth.NatalCompoundPicker

@Composable
fun AstroEditTargetSection(
    target: AlarmTarget,
    onTargetChange: (AlarmTarget) -> Unit,
    natalProfileId: String? = null,
    timeUnknown: Boolean = false,
) {
    when (target) {
        is AlarmTarget.Solar -> {
            SolarEventPicker(
                selectedEvent = target.event,
                onSelectEvent = { onTargetChange(target.copy(event = it)) }
            )
            OffsetSelector(
                offsetMinutes = target.offsetMinutes,
                eventName = AstroEventLabels.solarLabel(target.event),
                onOffsetChange = { onTargetChange(target.copy(offsetMinutes = it)) }
            )
        }
        is AlarmTarget.Lunar -> {
            LunarEventPicker(
                selectedEvent = target.event,
                onSelectEvent = { onTargetChange(target.copy(event = it)) }
            )
            OffsetSelector(
                offsetMinutes = target.offsetMinutes,
                eventName = AstroEventLabels.lunarLabel(target.event),
                onOffsetChange = { onTargetChange(target.copy(offsetMinutes = it)) }
            )
        }
        is AlarmTarget.Zodiac -> {
            ZodiacEventPicker(
                selectedSign = target.sign,
                selectedPoint = target.point,
                onSelect = { s, p -> onTargetChange(target.copy(sign = s, point = p)) }
            )
            OffsetSelector(
                offsetMinutes = target.offsetMinutes,
                eventName = AstroEventLabels.zodiacLabel(target.sign, target.point),
                onOffsetChange = { onTargetChange(target.copy(offsetMinutes = it)) }
            )
        }
        is AlarmTarget.CustomClock -> {
            ClockTimePicker(
                hour = target.hour,
                minute = target.minute,
                onTimeChange = { h, m -> onTargetChange(AlarmTarget.CustomClock(h, m)) }
            )
        }
        is AlarmTarget.SolarTerm -> {
            SeasonalTermPicker(
                selected = target.term,
                onSelect = { onTargetChange(target.copy(term = it)) }
            )
            OffsetSelector(
                offsetMinutes = target.offsetMinutes,
                eventName = target.term.pinyin,
                onOffsetChange = { onTargetChange(target.copy(offsetMinutes = it)) }
            )
        }
        is AlarmTarget.Planet, is AlarmTarget.PlanetAlign, is AlarmTarget.AllPlanetsAlign -> {
            PlanetTargetPicker(target = target, onTargetChange = onTargetChange)
            val name = AlarmTargetCopy.fallback(target)
            val offset = when (target) {
                is AlarmTarget.Planet -> target.offsetMinutes
                is AlarmTarget.PlanetAlign -> target.offsetMinutes
                is AlarmTarget.AllPlanetsAlign -> target.offsetMinutes
            }
            OffsetSelector(
                offsetMinutes = offset,
                eventName = name,
                onOffsetChange = { m ->
                    onTargetChange(
                        when (target) {
                            is AlarmTarget.Planet -> target.copy(offsetMinutes = m)
                            is AlarmTarget.PlanetAlign -> target.copy(offsetMinutes = m)
                            is AlarmTarget.AllPlanetsAlign -> target.copy(offsetMinutes = m)
                        }
                    )
                }
            )
        }
        is AlarmTarget.NatalCompoundSpecific,
        is AlarmTarget.NatalCompoundAny -> {
            val pid = when (target) {
                is AlarmTarget.NatalCompoundSpecific -> target.profileId
                is AlarmTarget.NatalCompoundAny -> target.profileId
            }
            if (pid.isNotBlank()) {
                NatalCompoundPicker(
                    target = target,
                    profileId = pid,
                    timeUnknown = timeUnknown,
                    onTargetChange = onTargetChange,
                )
            }
            val offset = when (target) {
                is AlarmTarget.NatalCompoundSpecific -> target.offsetMinutes
                is AlarmTarget.NatalCompoundAny -> target.offsetMinutes
            }
            OffsetSelector(
                offsetMinutes = offset,
                eventName = AlarmTargetCopy.fallback(target),
                onOffsetChange = { m ->
                    onTargetChange(
                        when (target) {
                            is AlarmTarget.NatalCompoundSpecific -> target.copy(offsetMinutes = m)
                            is AlarmTarget.NatalCompoundAny -> target.copy(offsetMinutes = m)
                        },
                    )
                },
            )
        }
        is AlarmTarget.NatalAscAspect,
        is AlarmTarget.NatalMcAspect,
        is AlarmTarget.MoonReturn,
        is AlarmTarget.SolarReturn,
        is AlarmTarget.MercuryStation,
        is AlarmTarget.MoonSignIngress -> {
            val pid = natalProfileId ?: when (target) {
                is AlarmTarget.NatalAscAspect -> target.profileId
                is AlarmTarget.NatalMcAspect -> target.profileId
                is AlarmTarget.MoonReturn -> target.profileId
                is AlarmTarget.SolarReturn -> target.profileId
                else -> null
            }
            if (pid != null) {
                TextButton(
                    onClick = {
                        onTargetChange(
                            AlarmTarget.NatalCompoundAny(2, pid),
                        )
                    },
                ) {
                    Text(stringResource(R.string.astro_natal_compound_combine))
                }
            }
            val offset = when (target) {
                is AlarmTarget.NatalAscAspect -> target.offsetMinutes
                is AlarmTarget.NatalMcAspect -> target.offsetMinutes
                is AlarmTarget.MoonReturn -> target.offsetMinutes
                is AlarmTarget.SolarReturn -> target.offsetMinutes
                is AlarmTarget.MercuryStation -> target.offsetMinutes
                is AlarmTarget.MoonSignIngress -> target.offsetMinutes
            }
            OffsetSelector(
                offsetMinutes = offset,
                eventName = AlarmTargetCopy.fallback(target),
                onOffsetChange = { m ->
                    onTargetChange(
                        when (target) {
                            is AlarmTarget.NatalAscAspect -> target.copy(offsetMinutes = m)
                            is AlarmTarget.NatalMcAspect -> target.copy(offsetMinutes = m)
                            is AlarmTarget.MoonReturn -> target.copy(offsetMinutes = m)
                            is AlarmTarget.SolarReturn -> target.copy(offsetMinutes = m)
                            is AlarmTarget.MercuryStation -> target.copy(offsetMinutes = m)
                            is AlarmTarget.MoonSignIngress -> target.copy(offsetMinutes = m)
                        },
                    )
                },
            )
        }
    }
}
