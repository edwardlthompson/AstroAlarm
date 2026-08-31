package org.astroalarm.ui

import androidx.compose.runtime.Composable
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.zodiac.ZodiacPoint
import org.astroalarm.astro.zodiac.ZodiacSign

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
    }
}
