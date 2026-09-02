package org.astroalarm.sol

import java.time.Instant

object PlanetMotion {
    fun geoSpeedDegPerDay(body: PlanetBody, instant: Instant): Double {
        val a = PlanetKepler.geoLon(body, instant)
        val b = PlanetKepler.geoLon(body, instant.plusSeconds(86400L))
        return wrap180(b - a)
    }

    fun isRetrograde(body: PlanetBody, instant: Instant): Boolean =
        body != PlanetBody.EARTH && geoSpeedDegPerDay(body, instant) < 0.0
}
