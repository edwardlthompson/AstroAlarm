package org.astroalarm.sol

import org.astroalarm.astro.place.AstroPlace
import java.time.Instant
import kotlin.math.abs

object PlanetNext {
    fun nextPlanetEvent(body: PlanetBody, event: PlanetEventType, place: AstroPlace?, now: Instant): Instant? {
        if (body == PlanetBody.EARTH) return null
        return when (event) {
            PlanetEventType.Rise -> place?.takeIf { it.isValid }?.let { PlanetHorizon.nextCrossing(body, it, now, true) }
            PlanetEventType.Set -> place?.takeIf { it.isValid }?.let { PlanetHorizon.nextCrossing(body, it, now, false) }
            PlanetEventType.RetrogradeStart -> nextRetrogradeEdge(body, now, entering = true)
            PlanetEventType.DirectStart -> nextRetrogradeEdge(body, now, entering = false)
            PlanetEventType.Opposition -> if (body.isInner) null else nextSep(body, now, 180.0)
            PlanetEventType.InferiorConjunction -> if (!body.isInner) null else nextSep(body, now, 0.0)
            PlanetEventType.SuperiorConjunction -> if (!body.isInner) null else nextSep(body, now, 180.0)
        }
    }

    fun nextAlign(a: PlanetBody, b: PlanetBody, now: Instant): Instant? {
        if (a == b || a == PlanetBody.EARTH || b == PlanetBody.EARTH) return null
        var t = now
        var prev = PlanetKepler.pairDelta(a, b, t)
        repeat(800) {
            t = t.plusSeconds(86400L)
            val d = PlanetKepler.pairDelta(a, b, t)
            if (prev > 8.0 && d <= 8.0) return refinePair(a, b, t.minusSeconds(86400L), t)
            prev = d
        }
        return null
    }

    fun nextAllAlign(now: Instant): Instant? {
        val inCluster = PlanetKepler.allPlanetSpan(now) <= 90.0
        var t = now
        var seenOut = !inCluster
        repeat(365 * 50) {
            t = t.plusSeconds(86400L)
            val span = PlanetKepler.allPlanetSpan(t)
            if (span > 90.0) seenOut = true
            if (seenOut && span <= 90.0) return refineAll(t.minusSeconds(86400L), t)
        }
        return null
    }

    private fun nextSep(body: PlanetBody, now: Instant, target: Double): Instant? {
        var t = now
        var prev = abs(wrap180(PlanetKepler.helioLon(body, t) - PlanetKepler.helioLon(PlanetBody.EARTH, t)))
        val want = if (target == 0.0) 0.0 else 180.0
        repeat(800) {
            t = t.plusSeconds(86400L)
            val s = abs(wrap180(PlanetKepler.helioLon(body, t) - PlanetKepler.helioLon(PlanetBody.EARTH, t)))
            val hit = if (want == 0.0) prev > 8.0 && s <= 8.0 else prev < 170.0 && s >= 170.0
            if (hit) return t
            prev = s
        }
        return null
    }

    private fun nextRetrogradeEdge(body: PlanetBody, now: Instant, entering: Boolean): Instant? {
        var t = now
        var prev = PlanetMotion.isRetrograde(body, t)
        repeat(800) {
            t = t.plusSeconds(86400L)
            val retro = PlanetMotion.isRetrograde(body, t)
            if (entering && !prev && retro) return t
            if (!entering && prev && !retro) return t
            prev = retro
        }
        return null
    }

    private fun refinePair(a: PlanetBody, b: PlanetBody, lo: Instant, hi: Instant): Instant {
        var aT = lo
        var bT = hi
        repeat(12) {
            val mid = Instant.ofEpochSecond((aT.epochSecond + bT.epochSecond) / 2)
            if (PlanetKepler.pairDelta(a, b, mid) <= 8.0) bT = mid else aT = mid
        }
        return bT
    }

    private fun refineAll(lo: Instant, hi: Instant): Instant {
        var aT = lo
        var bT = hi
        repeat(14) {
            val mid = Instant.ofEpochSecond((aT.epochSecond + bT.epochSecond) / 2)
            if (PlanetKepler.allPlanetSpan(mid) <= 90.0) bT = mid else aT = mid
        }
        return bT
    }
}
