package org.astroalarm.solarterm

import org.astroalarm.astro.sun.SolarMath
import org.astroalarm.astro.sun.SolarSeasons
import java.time.Instant

/** Year-wheel layout: Lìchūn (315°) at canvas −90° (12 o’clock); longitude increases CCW. */
object SolarTermLayout {
    fun canvasDeg(lonDeg: Double): Float = (-90.0 - wrap360(lonDeg - 315.0)).toFloat()

    fun nowUpRotation(@Suppress("UNUSED_PARAMETER") lonDeg: Double): Float = 0f

    fun nowLongitude(snapshot: SolarTermSnapshot): Double =
        wrap360(snapshot.current.term.longitudeDeg + snapshot.progress * 15.0)

    fun earthAu(now: Instant): Double = SolarMath.sunEarthAu(now)

    fun perihelionLon(now: Instant): Double = SolarMath.longitudeOfPerihelion(now)

    fun apparentLon(now: Instant): Double = SolarSeasons.apparentLon(now)
}
