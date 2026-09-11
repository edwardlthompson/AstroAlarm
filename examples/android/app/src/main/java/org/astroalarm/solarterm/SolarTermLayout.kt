package org.astroalarm.solarterm

import org.astroalarm.astro.sun.SolarMath
import org.astroalarm.astro.sun.SolarSeasons
import java.time.Instant

/**
 * North-ecliptic-pole frame, same as Sol (`sx = cx + x`, `sy = cy − y`).
 * Jieqi longitudes are geocentric solar λ; Earth is heliocentric at λ+180°,
 * so `canvasDeg(λ) = −(λ+180)` puts March Earth at 9 o’clock, June at 6,
 * September (toward ♈) at 3, and December at 12. Prograde is CCW.
 */
object SolarTermLayout {
    fun canvasDeg(lonDeg: Double): Float = (-wrap360(lonDeg + 180.0)).toFloat()

    fun lonFromCanvasDeg(canvasDeg: Double): Double = wrap360(-canvasDeg - 180.0)

    fun nowUpRotation(@Suppress("UNUSED_PARAMETER") lonDeg: Double): Float = 0f

    fun nowLongitude(snapshot: SolarTermSnapshot): Double =
        wrap360(snapshot.current.term.longitudeDeg + snapshot.progress * 15.0)

    fun earthAu(now: Instant): Double = SolarMath.sunEarthAu(now)

    fun perihelionLon(now: Instant): Double = SolarMath.longitudeOfPerihelion(now)

    fun apparentLon(now: Instant): Double = SolarSeasons.apparentLon(now)
}
