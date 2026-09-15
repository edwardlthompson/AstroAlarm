package org.astroalarm.ui

import android.content.Context
import dev.foss.goldenpath.R
import org.astroalarm.astro.alarm.AstroNextFire
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.widget.AppSnackbar
import java.time.Instant

object NextEventA11y {
    fun announce(context: Context, alarms: List<AstroAlarm>, place: AstroPlace?): Boolean {
        val now = Instant.now()
        val next = alarms.mapNotNull { AstroNextFire.nextInstant(it, place, now, all = alarms) }.minOrNull()
        if (next == null) {
            AppSnackbar.emit(context.getString(R.string.a11y_no_next_event))
        } else {
            AppSnackbar.emit(formatInstant(next, place))
        }
        return true
    }
}
