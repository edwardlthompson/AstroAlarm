package org.astroalarm.astro.birth

import java.time.Instant
import kotlin.math.abs

data class NatalCompoundHit(
    val at: Instant,
    val kinds: List<NatalStoryKind>,
)

/**
 * Next double/triple of natal story-events (alarm-grade scan).
 * Horizon ~2y; 12h step; co-active set within highlight orb.
 */
object NatalCompoundNext {
    private const val HORIZON_DAYS = 730
    private const val STEP_SEC = 12 * 3600L

    fun nextSpecific(
        chart: NatalChart,
        kinds: List<NatalStoryKind>,
        now: Instant,
    ): NatalCompoundHit? {
        val need = kinds.distinct().sortedBy { it.name }
        if (need.size !in 2..3) return null
        if (need.any { NatalEventLinks.needsAscendant(it) } && (chart.timeUnknown || chart.ascendant == null)) {
            return null
        }
        return scan(chart, now) { active -> need.all { it in active } }?.let { (t, active) ->
            NatalCompoundHit(t, need.filter { it in active })
        }
    }

    fun nextAny(chart: NatalChart, arity: Int, now: Instant): NatalCompoundHit? {
        if (arity !in 2..3) return null
        return scan(chart, now) { it.size >= arity }?.let { (t, active) ->
            NatalCompoundHit(t, active.take(arity))
        }
    }

    private fun scan(
        chart: NatalChart,
        now: Instant,
        pred: (Set<NatalStoryKind>) -> Boolean,
    ): Pair<Instant, List<NatalStoryKind>>? {
        var t = now
        val steps = (HORIZON_DAYS * 86400L / STEP_SEC).toInt()
        repeat(steps) {
            t = t.plusSeconds(STEP_SEC)
            val sky = NatalSkySnapshot.now(t)
            val active = NatalEventLinks.active(chart, sky, t).map { it.kind }.toSet()
            if (pred(active)) return t to active.sortedBy { it.name }
        }
        return null
    }

    fun labelKinds(kinds: List<NatalStoryKind>): String =
        kinds.joinToString(" + ") { short(it) }

    private fun short(k: NatalStoryKind): String = when (k) {
        NatalStoryKind.AscSun -> "Sun→Asc"
        NatalStoryKind.AscMoon -> "Moon→Asc"
        NatalStoryKind.AscMercury -> "Merc→Asc"
        NatalStoryKind.SolarReturn -> "Solar return"
        NatalStoryKind.MoonReturn -> "Moon return"
        NatalStoryKind.MoonRisingSign -> "Moon rising"
        NatalStoryKind.MercuryStation -> "Merc station"
    }
}
