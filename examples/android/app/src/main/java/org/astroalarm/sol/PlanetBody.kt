package org.astroalarm.sol

enum class PlanetBody {
    MERCURY, VENUS, EARTH, MARS, JUPITER, SATURN, URANUS, NEPTUNE;

    val isInner: Boolean get() = this == MERCURY || this == VENUS
}

enum class PlanetEventType {
    Rise,
    Set,
    RetrogradeStart,
    DirectStart,
    Opposition,
    InferiorConjunction,
    SuperiorConjunction,
}

internal fun wrap360(deg: Double): Double {
    var d = deg % 360.0
    if (d < 0.0) d += 360.0
    return d
}

internal fun wrap180(deg: Double): Double {
    var d = wrap360(deg)
    if (d > 180.0) d -= 360.0
    return d
}
