package org.astroalarm.astro.birth

import org.astroalarm.astro.moon.LunarCalculator
import org.astroalarm.astro.sun.SolarSeasons
import org.astroalarm.sol.PlanetBody
import org.astroalarm.sol.PlanetKepler
import java.time.Instant
import kotlin.math.abs

data class NatalSkySnapshot(
    val sun: Double,
    val moon: Double,
    val mercury: Double,
) {
    companion object {
        fun now(instant: Instant = Instant.now()): NatalSkySnapshot = NatalSkySnapshot(
            sun = SolarSeasons.apparentLon(instant),
            moon = LunarCalculator.eclipticLon(instant),
            mercury = PlanetKepler.geoLon(PlanetBody.MERCURY, instant),
        )
    }
}

enum class NatalStoryKind {
    AscSun,
    AscMoon,
    AscMercury,
    SolarReturn,
    MoonReturn,
    MoonRisingSign,
    MercuryStation,
}

data class NatalEventLink(
    val kind: NatalStoryKind,
    val liveLon: Double,
    val targetLon: Double,
    val deltaDeg: Double,
)

object NatalEventLinks {
    const val HIGHLIGHT_ORB = 3.0

    fun active(chart: NatalChart, sky: NatalSkySnapshot, now: Instant = Instant.now()): List<NatalEventLink> {
        val out = ArrayList<NatalEventLink>()
        fun add(kind: NatalStoryKind, live: Double, target: Double) {
            val d = abs(NatalAspectMath.wrap180(live - target))
            if (d <= HIGHLIGHT_ORB) out.add(NatalEventLink(kind, live, target, d))
        }
        val ascPt = chart.ascendant
        if (ascPt != null && !chart.timeUnknown) {
            add(NatalStoryKind.AscSun, sky.sun, ascPt.longitudeDeg)
            add(NatalStoryKind.AscMoon, sky.moon, ascPt.longitudeDeg)
            add(NatalStoryKind.AscMercury, sky.mercury, ascPt.longitudeDeg)
            add(NatalStoryKind.MoonRisingSign, sky.moon, ascPt.sign.startLongitudeDeg)
        }
        add(NatalStoryKind.SolarReturn, sky.sun, chart.sun.longitudeDeg)
        add(NatalStoryKind.MoonReturn, sky.moon, chart.moon.longitudeDeg)
        if (nearMercuryStation(now)) {
            add(NatalStoryKind.MercuryStation, sky.mercury, sky.mercury)
        }
        return out
    }

    fun nearMercuryStation(now: Instant): Boolean {
        val next = NatalNext.nextMercuryStation(retrogradeStart = true, now = now.minusSeconds(3 * 86400L))
            ?: NatalNext.nextMercuryStation(retrogradeStart = false, now = now.minusSeconds(3 * 86400L))
            ?: return false
        return abs(next.epochSecond - now.epochSecond) <= 2 * 86400L
    }

    fun needsAscendant(kind: NatalStoryKind): Boolean = when (kind) {
        NatalStoryKind.AscSun, NatalStoryKind.AscMoon, NatalStoryKind.AscMercury,
        NatalStoryKind.MoonRisingSign -> true
        else -> false
    }
}
