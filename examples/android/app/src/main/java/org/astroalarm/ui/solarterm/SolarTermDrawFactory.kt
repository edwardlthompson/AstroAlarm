package org.astroalarm.ui.solarterm

import android.content.res.Resources
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.solarterm.SolarTerm
import org.astroalarm.solarterm.SolarTermCache
import org.astroalarm.solarterm.SolarTermSnapshot
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object SolarTermDrawFactory {
    fun request(
        res: Resources,
        place: AstroPlace?,
        now: Instant,
        traditional: Boolean,
        localSeasons: Boolean,
        dark: Boolean,
        compact: Boolean,
    ): Pair<SolarTermSnapshot, SolarTermDrawRequest> {
        val zone = SolarTermFormat.zoneOf(place)
        val south = SolarTermFormat.southern(place)
        val snap = SolarTermCache.snapshot(now, zone)
        val labels = SolarTerm.entries.map { it.localAlias(localSeasons, south).hanzi(traditional) }
        val english = SolarTerm.entries.map {
            SolarTermCopy.name(res, it.localAlias(localSeasons, south))
        }
        val whenLocal = snap.year.occurrences.map { SolarTermFormat.localStamp(it.utc, zone) }
        val today = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault()).withZone(zone).format(now)
        val req = SolarTermDrawRequest(
            snapshot = snap,
            labels = labels,
            english = english,
            whenLocal = whenLocal,
            locationLabel = SolarTermFormat.locationLabel(res, place),
            countdown = SolarTermFormat.countdown(res, snap.hoursUntilNext),
            todayLine = today,
            dark = dark,
            localSeasons = localSeasons,
            southern = south,
            compact = compact,
        )
        return snap to req
    }

    fun zone(place: AstroPlace?): ZoneId = SolarTermFormat.zoneOf(place)
}
