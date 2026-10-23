package org.astroalarm.wear

import java.time.ZonedDateTime

/** Civil-fraction degrees for the Daily 2D–style now-up dial. */
object WearCivilAngles {
    /** Degrees of civil time from midnight (0..360). */
    fun civilAngle(zdt: ZonedDateTime): Float {
        val minutes = zdt.hour * 60 + zdt.minute + zdt.second / 60f
        return minutes / 1440f * 360f
    }

    /** Screen degrees relative to [anchor] civil angle; 0° = 3 o'clock, -90° = top. */
    fun screenDeg(civilAngle: Float, anchor: Float): Float = civilAngle - anchor - 90f

    /** Now-up red hand stays at screen top. */
    fun nowUpHandDeg(): Float = -90f
}
