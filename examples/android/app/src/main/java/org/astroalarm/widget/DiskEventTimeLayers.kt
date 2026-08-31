package org.astroalarm.widget

/** Which 2D dial labels to draw when the event-times toggle is on or off. */
data class DiskEventTimeLayers(
    val sunriseSunsetBadges: Boolean,
    val alarmMarkers: Boolean,
) {
    companion object {
        fun fromToggle(showEventTimes: Boolean) = DiskEventTimeLayers(
            sunriseSunsetBadges = showEventTimes,
            alarmMarkers = showEventTimes,
        )
    }
}
