package org.astroalarm.ui

import androidx.compose.runtime.Composable
import org.astroalarm.astro.alarm.AlarmTargetCopy
import org.astroalarm.astro.model.AlarmTarget

@Composable
fun AstroEditTargetSection(
    target: AlarmTarget,
    onTargetChange: (AlarmTarget) -> Unit
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
    }
}
