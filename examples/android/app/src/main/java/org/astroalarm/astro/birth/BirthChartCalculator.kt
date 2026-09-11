package org.astroalarm.astro.birth

import org.astroalarm.astro.moon.LunarCalculator
import org.astroalarm.astro.sun.SolarSeasons
import org.astroalarm.sol.PlanetBody
import org.astroalarm.sol.PlanetKepler
import java.time.Instant

enum class NatalBody {
    SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN, URANUS, NEPTUNE;

    fun toPlanetOrNull(): PlanetBody? = when (this) {
        SUN, MOON -> null
        MERCURY -> PlanetBody.MERCURY
        VENUS -> PlanetBody.VENUS
        MARS -> PlanetBody.MARS
        JUPITER -> PlanetBody.JUPITER
        SATURN -> PlanetBody.SATURN
        URANUS -> PlanetBody.URANUS
        NEPTUNE -> PlanetBody.NEPTUNE
    }
}

data class NatalChart(
    val instant: Instant,
    val ascendant: EclipticPoint?,
    val midheaven: EclipticPoint?,
    val sun: EclipticPoint,
    val moon: EclipticPoint,
    val planets: Map<NatalBody, EclipticPoint>,
    val approximate: Boolean,
    val timeUnknown: Boolean,
)

object BirthChartCalculator {
    fun compute(profile: BirthProfile): NatalChart? {
        val instant = BirthTimeZones.toInstant(profile) ?: return null
        val sun = EclipticPoint(SolarSeasons.apparentLon(instant))
        val moon = EclipticPoint(LunarCalculator.eclipticLon(instant))
        val planets = linkedMapOf<NatalBody, EclipticPoint>()
        planets[NatalBody.SUN] = sun
        planets[NatalBody.MOON] = moon
        for (body in NatalBody.entries) {
            val pb = body.toPlanetOrNull() ?: continue
            val lon = PlanetKepler.geoLon(pb, instant)
            if (lon.isFinite()) planets[body] = EclipticPoint(lon)
        }
        val asc = if (profile.canComputeAscendant) {
            AscendantMath.ascendant(instant, profile.lat, profile.lon)
        } else {
            null
        }
        val mc = if (profile.canComputeAscendant) {
            AscendantMath.midheaven(instant, profile.lon)
        } else {
            null
        }
        return NatalChart(
            instant = instant,
            ascendant = asc,
            midheaven = mc,
            sun = sun,
            moon = moon,
            planets = planets,
            approximate = profile.polarWarning || profile.zoneIsFallback,
            timeUnknown = profile.timeUnknown,
        )
    }

    fun longitudeOf(body: NatalBody, instant: Instant): Double? = when (body) {
        NatalBody.SUN -> SolarSeasons.apparentLon(instant)
        NatalBody.MOON -> LunarCalculator.eclipticLon(instant)
        else -> body.toPlanetOrNull()?.let { PlanetKepler.geoLon(it, instant) }?.takeIf { it.isFinite() }
    }
}
