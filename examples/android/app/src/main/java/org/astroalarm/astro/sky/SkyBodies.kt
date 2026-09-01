package org.astroalarm.astro.sky

import org.shredzone.commons.suncalc.MoonPosition
import org.shredzone.commons.suncalc.SunPosition
import java.time.Instant

/** Topocentric sun/moon from commons-suncalc. Geometric HA/dec uses true altitude. */
object SkyBodies {
    fun sun(instant: Instant, lat: Double, lon: Double): SkyCoords? = runCatching {
        val pos = SunPosition.compute().on(instant).at(lat, lon).execute()
        BodySky.fromAzAlt(pos.azimuth, pos.trueAltitude, lat)
    }.getOrNull()

    fun moon(instant: Instant, lat: Double, lon: Double): SkyCoords? = runCatching {
        val pos = MoonPosition.compute().on(instant).at(lat, lon).execute()
        BodySky.fromAzAlt(pos.azimuth, pos.trueAltitude, lat)
    }.getOrNull()
}
