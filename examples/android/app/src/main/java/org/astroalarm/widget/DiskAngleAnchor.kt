package org.astroalarm.widget

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/** Civil-fraction degrees used as the disk subtract-anchor. */
object DiskAngleAnchor {
    fun civilAngle(instant: Instant, zone: ZoneId): Float {
        val z = ZonedDateTime.ofInstant(instant, zone)
        return (z.hour * 60 + z.minute + z.second / 60f) / 1440f * 360f
    }

    /**
     * When [solarNoonUp] and [noon] are set, anchor at solar noon so noon stays at screen top.
     * Otherwise anchor at civil now (legacy now-up dial).
     */
    fun anchor(
        now: Instant,
        noon: Instant?,
        zone: ZoneId,
        solarNoonUp: Boolean,
    ): Float {
        val nowAngle = civilAngle(now, zone)
        if (!solarNoonUp || noon == null) return nowAngle
        return civilAngle(noon, zone)
    }

    fun screenDeg(civilAngle: Float, anchor: Float): Float = civilAngle - anchor - 90f

    fun handDeg(nowAngle: Float, anchor: Float, solarNoonUp: Boolean): Float =
        if (solarNoonUp) screenDeg(nowAngle, anchor) else -90f
}
