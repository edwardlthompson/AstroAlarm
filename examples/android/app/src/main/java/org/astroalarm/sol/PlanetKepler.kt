package org.astroalarm.sol

import java.time.Instant
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

data class PlanetState(
    val x: Double, val y: Double, val z: Double, val au: Double, val helioLon: Double, val nuDeg: Double,
)

object PlanetKepler {
    fun centuries(instant: Instant): Double {
        val jd = 2451545.0 + (instant.epochSecond - 946728000.0) / 86400.0
        return (jd - 2451545.0) / 36525.0
    }

    fun state(body: PlanetBody, instant: Instant): PlanetState {
        val t = centuries(instant)
        val el = PlanetElements.of(body)
        val a = el.a + el.da * t
        val e = el.e + el.de * t
        val i = Math.toRadians(el.i + el.di * t)
        val l = wrap360(el.L + el.dL * t)
        val varpi = wrap360(el.varpi + el.dVarpi * t)
        val omegaNode = wrap360(el.Omega + el.dOmega * t)
        val m = Math.toRadians(wrap180(l - varpi))
        var eAnom = m
        repeat(8) { eAnom = m + e * sin(eAnom) }
        val xv = a * (cos(eAnom) - e)
        val yv = a * kotlin.math.sqrt((1.0 - e * e).coerceAtLeast(0.0)) * sin(eAnom)
        val nu = atan2(yv, xv)
        val r = hypot(xv, yv)
        val w = Math.toRadians(wrap360(varpi - omegaNode))
        val om = Math.toRadians(omegaNode)
        val xh = r * (cos(om) * cos(w + nu) - sin(om) * sin(w + nu) * cos(i))
        val yh = r * (sin(om) * cos(w + nu) + cos(om) * sin(w + nu) * cos(i))
        val zh = r * (sin(w + nu) * sin(i))
        return PlanetState(xh, yh, zh, r, wrap360(Math.toDegrees(atan2(yh, xh))), wrap360(Math.toDegrees(nu)))
    }

    fun helioXy(body: PlanetBody, instant: Instant, nuDeg: Double): Pair<Double, Double> {
        val t = centuries(instant)
        val el = PlanetElements.of(body)
        val a = el.a + el.da * t
        val e = el.e + el.de * t
        val i = Math.toRadians(el.i + el.di * t)
        val varpi = wrap360(el.varpi + el.dVarpi * t)
        val omegaNode = wrap360(el.Omega + el.dOmega * t)
        val nu = Math.toRadians(nuDeg)
        val r = a * (1.0 - e * e) / (1.0 + e * cos(nu))
        val w = Math.toRadians(wrap360(varpi - omegaNode))
        val om = Math.toRadians(omegaNode)
        val xh = r * (cos(om) * cos(w + nu) - sin(om) * sin(w + nu) * cos(i))
        val yh = r * (sin(om) * cos(w + nu) + cos(om) * sin(w + nu) * cos(i))
        return xh to yh
    }

    fun geoLon(body: PlanetBody, instant: Instant): Double {
        if (body == PlanetBody.EARTH) return Double.NaN
        val p = state(body, instant)
        val e = state(PlanetBody.EARTH, instant)
        return wrap360(Math.toDegrees(atan2(p.y - e.y, p.x - e.x)))
    }

    fun au(body: PlanetBody, instant: Instant): Double = state(body, instant).au

    fun helioLon(body: PlanetBody, instant: Instant): Double = state(body, instant).helioLon

    fun pairDelta(a: PlanetBody, b: PlanetBody, instant: Instant): Double =
        kotlin.math.abs(wrap180(geoLon(a, instant) - geoLon(b, instant)))

    fun allPlanetSpan(instant: Instant): Double {
        val lons = PlanetBody.entries.map { helioLon(it, instant) }.sorted()
        var maxGap = 0.0
        for (i in lons.indices) {
            val next = if (i + 1 < lons.size) lons[i + 1] else lons[0] + 360.0
            maxGap = maxOf(maxGap, next - lons[i])
        }
        return 360.0 - maxGap
    }
}
