package org.astroalarm.ui.solarterm

import android.content.res.Resources
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.solarterm.SolarTerm
import org.astroalarm.solarterm.SolarTermCache
import org.astroalarm.solarterm.SolarTermLayout
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
        dark: Boolean,
        compact: Boolean,
        alarmOrds: Set<Int> = emptySet(),
    ): Pair<SolarTermSnapshot, SolarTermDrawRequest> {
        val zone = SolarTermFormat.zoneOf(place)
        val south = SolarTermFormat.southern(place)
        val snap = SolarTermCache.snapshot(now, zone)
        val english = SolarTerm.entries.map { SolarTermCopy.name(res, it.localAlias(south)) }
        val whenLocal = snap.year.occurrences.map { SolarTermFormat.localStamp(it.utc, zone) }
        val today = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault()).withZone(zone).format(now)
        val nowLon = SolarTermLayout.nowLongitude(snap)
        val req = SolarTermDrawRequest(
            snapshot = snap,
            english = english,
            whenLocal = whenLocal,
            locationLabel = SolarTermFormat.locationLabel(res, place),
            countdown = SolarTermFormat.countdown(res, snap.hoursUntilNext),
            todayLine = today,
            dark = dark,
            southern = south,
            compact = compact,
            nowLon = nowLon,
            perihelionLon = SolarTermLayout.perihelionLon(now),
            userLat = place?.takeIf { it.isValid }?.latitude,
            userLon = place?.takeIf { it.isValid }?.longitude,
            now = now,
            alarmOrds = alarmOrds,
        )
        return snap to req
    }

    fun zone(place: AstroPlace?): ZoneId = SolarTermFormat.zoneOf(place)
}
