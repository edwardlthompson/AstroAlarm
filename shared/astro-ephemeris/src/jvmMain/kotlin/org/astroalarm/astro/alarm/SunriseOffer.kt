package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.place.AstroPlace
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

object SunriseOffer {
    val target: AlarmTarget.Solar = AlarmTarget.Solar(SolarEventType.Sunrise, 0)

    fun hasSunriseAlarm(alarms: List<AstroAlarm>): Boolean =
        alarms.any { a ->
            val t = a.target
            t is AlarmTarget.Solar && t.event == SolarEventType.Sunrise
        }

    fun shouldShow(
        place: AstroPlace?,
        alarms: List<AstroAlarm>,
        dismissed: Boolean,
        pending: Boolean,
    ): Boolean {
        if (dismissed || !pending) return false
        if (place == null || !place.isValid) return false
        if (hasSunriseAlarm(alarms)) return false
        return nextFire(place) != null
    }

    fun nextFire(place: AstroPlace, now: Instant = Instant.now()): Instant? {
        val probe = AstroAlarm(id = "sunrise-offer", label = "Sunrise", target = target)
        return AstroNextFire.nextInstant(probe, place, now)
    }

    fun formatHm(instant: Instant, zone: ZoneId): String =
        DateTimeFormatter.ofPattern("H:mm").withZone(zone).format(instant)

    fun alarm(label: String, id: String = UUID.randomUUID().toString()): AstroAlarm =
        AstroAlarm(id = id, label = label, enabled = true, target = target)
}
