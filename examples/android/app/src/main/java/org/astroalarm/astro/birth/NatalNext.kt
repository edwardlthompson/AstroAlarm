package org.astroalarm.astro.birth

import org.astroalarm.astro.moon.LunarCalculator
import org.astroalarm.astro.model.NatalAspect
import org.astroalarm.astro.zodiac.ZodiacSign
import org.astroalarm.sol.PlanetBody
import org.astroalarm.sol.PlanetEventType
import org.astroalarm.sol.PlanetNext
import java.time.Instant
import kotlin.math.abs

object NatalNext {
    private const val ORB = 1.0

    private fun wrap360(deg: Double): Double {
        var d = deg % 360.0
        if (d < 0.0) d += 360.0
        return d
    }

    private fun wrap180(deg: Double): Double {
        var d = wrap360(deg)
        if (d > 180.0) d -= 360.0
        return d
    }
    fun nextAspectToNatal(
        body: NatalBody,
        natalLon: Double,
        aspect: NatalAspect,
        now: Instant,
    ): Instant? {
        val targets = aspectTargets(natalLon, aspect)
        var best: Instant? = null
        for (target in targets) {
            val hit = nextLongitudeHit(body, target, now) ?: continue
            if (best == null || hit.isBefore(best)) best = hit
        }
        return best
    }

    fun nextReturn(body: NatalBody, natalLon: Double, now: Instant): Instant? =
        nextLongitudeHit(body, natalLon, now)

    fun nextMoonSignIngress(sign: ZodiacSign, now: Instant): Instant? =
        nextLongitudeHit(NatalBody.MOON, sign.startLongitudeDeg, now)

    fun nextMercuryStation(retrogradeStart: Boolean, now: Instant): Instant? {
        val ev = if (retrogradeStart) PlanetEventType.RetrogradeStart else PlanetEventType.DirectStart
        return PlanetNext.nextPlanetEvent(PlanetBody.MERCURY, ev, place = null, now)
    }

    private fun aspectTargets(natalLon: Double, aspect: NatalAspect): List<Double> = when (aspect) {
        NatalAspect.Conjunction -> listOf(wrap360(natalLon))
        NatalAspect.Opposition -> listOf(wrap360(natalLon + 180.0))
        NatalAspect.Square -> listOf(wrap360(natalLon + 90.0), wrap360(natalLon - 90.0))
    }

    private fun nextLongitudeHit(body: NatalBody, targetLon: Double, now: Instant): Instant? {
        var t = now
        var prev = abs(wrap180((BirthChartCalculator.longitudeOf(body, t) ?: return null) - targetLon))
        val step = if (body == NatalBody.MOON) 3600L else 86400L
        val maxSteps = if (body == NatalBody.MOON) 40 * 24 else 800
        repeat(maxSteps) {
            t = t.plusSeconds(step)
            val lon = BirthChartCalculator.longitudeOf(body, t) ?: return null
            val d = abs(wrap180(lon - targetLon))
            if (prev > ORB && d <= ORB) {
                return refine(body, targetLon, t.minusSeconds(step), t)
            }
            prev = d
        }
        return null
    }

    private fun refine(body: NatalBody, targetLon: Double, lo: Instant, hi: Instant): Instant {
        var a = lo
        var b = hi
        repeat(14) {
            val mid = Instant.ofEpochSecond((a.epochSecond + b.epochSecond) / 2)
            val lon = BirthChartCalculator.longitudeOf(body, mid) ?: return b
            val d = abs(wrap180(lon - targetLon))
            if (d <= ORB) b = mid else a = mid
        }
        return b
    }
}
