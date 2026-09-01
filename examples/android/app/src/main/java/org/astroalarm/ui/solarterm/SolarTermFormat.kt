package org.astroalarm.ui.solarterm

import android.content.res.Resources
import dev.foss.goldenpath.R
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.solarterm.SolarTerm
import org.astroalarm.solarterm.SolarTermOccurrence
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object SolarTermFormat {
    private val stamp: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm", Locale.getDefault())

    fun localStamp(instant: Instant, zone: ZoneId): String = stamp.withZone(zone).format(instant)

    fun locationLabel(res: Resources, place: AstroPlace?): String {
        if (place == null || !place.isValid) return res.getString(R.string.solar_term_device_zone)
        return if (place.cityName.isNotBlank()) {
            res.getString(R.string.solar_term_at_location, place.cityName)
        } else {
            res.getString(R.string.solar_term_at_coords, place.latitude, place.longitude)
        }
    }

    fun countdown(res: Resources, hours: Long): String = when {
        hours <= 0L -> res.getString(R.string.solar_term_next_today)
        hours < 24L -> res.getString(R.string.solar_term_next_in_hours, hours.toInt())
        else -> res.getString(R.string.solar_term_next_in_days, (hours / 24L).toInt())
    }

    fun nextGlance(
        res: Resources,
        next: SolarTermOccurrence,
        zone: ZoneId,
        traditional: Boolean,
        southern: Boolean,
        localSeasons: Boolean,
    ): String {
        val shown = next.term.localAlias(localSeasons, southern)
        val name = "${shown.hanzi(traditional)} ${SolarTermCopy.name(res, shown)}"
        return res.getString(R.string.solar_term_next_label, name, localStamp(next.utc, zone))
    }

    fun talkBack(res: Resources, term: SolarTerm, english: String, whenLocal: String): String =
        res.getString(R.string.solar_term_cd_sector, term.hanzi(false) + " " + term.pinyin, english, whenLocal)

    fun zoneOf(place: AstroPlace?): ZoneId = place?.zone ?: ZoneId.systemDefault()

    fun southern(place: AstroPlace?): Boolean = (place?.latitude ?: 0.0) < 0.0
}
