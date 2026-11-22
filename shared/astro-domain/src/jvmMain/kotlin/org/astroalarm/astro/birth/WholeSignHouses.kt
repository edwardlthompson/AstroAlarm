package org.astroalarm.astro.birth

import org.astroalarm.astro.zodiac.ZodiacSign

/** Whole-sign house cusp: house 1 = rising sign, then +1 sign each. */
data class WholeSignCusp(val house: Int, val sign: ZodiacSign)

data class WholeSignPlacement(val body: NatalBody, val house: Int, val sign: ZodiacSign)

/**
 * Whole-sign houses from Ascendant sign (FOSS alarm-grade; not Placidus).
 */
object WholeSignHouses {
    fun cusps(ascendant: EclipticPoint?): List<WholeSignCusp> {
        if (ascendant == null) return emptyList()
        val rising = ascendant.sign
        val start = ZodiacSign.entries.indexOf(rising)
        return (0 until 12).map { i ->
            WholeSignCusp(house = i + 1, sign = ZodiacSign.entries[(start + i) % 12])
        }
    }

    fun houseOf(longitudeDeg: Double, ascendant: EclipticPoint?): Int? {
        if (ascendant == null) return null
        val bodySign = ZodiacSign.fromEclipticLongitude(longitudeDeg)
        val rising = ZodiacSign.entries.indexOf(ascendant.sign)
        val body = ZodiacSign.entries.indexOf(bodySign)
        return ((body - rising + 12) % 12) + 1
    }

    fun placements(chart: NatalChart): List<WholeSignPlacement> {
        val asc = chart.ascendant ?: return emptyList()
        val out = ArrayList<WholeSignPlacement>(chart.planets.size)
        for ((body, point) in chart.planets) {
            val h = houseOf(point.longitudeDeg, asc) ?: continue
            out.add(WholeSignPlacement(body, h, point.sign))
        }
        return out.sortedBy { it.house }
    }
}
