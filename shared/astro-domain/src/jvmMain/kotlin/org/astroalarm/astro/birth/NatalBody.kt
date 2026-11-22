package org.astroalarm.astro.birth

import org.astroalarm.sol.PlanetBody

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
