package org.astroalarm.astro.birth

import org.astroalarm.astro.zodiac.ZodiacSign

data class EclipticPoint(val longitudeDeg: Double) {
    val sign: ZodiacSign get() = ZodiacSign.fromEclipticLongitude(longitudeDeg)
    val degreeInSign: Double get() {
        var n = longitudeDeg % 360.0
        if (n < 0.0) n += 360.0
        return n % 30.0
    }
}
