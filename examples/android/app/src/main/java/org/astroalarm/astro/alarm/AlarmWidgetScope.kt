package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.LunarEventType
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.sol.PlanetBody
import java.time.Instant

object AlarmWidgetScope {
    fun onYearly(target: AlarmTarget): Boolean = when (target) {
        is AlarmTarget.SolarTerm -> true
        is AlarmTarget.Solar -> AlarmFireIdentity.keyOf(target) != null
        else -> false
    }

    fun onSol(target: AlarmTarget): Boolean = target is AlarmTarget.Planet ||
        target is AlarmTarget.PlanetAlign ||
        target is AlarmTarget.AllPlanetsAlign ||
        onYearly(target) ||
        AlarmFireIdentity.keyOf(target) != null

    fun solBodies(target: AlarmTarget): List<PlanetBody> = when (target) {
        is AlarmTarget.Planet -> listOf(target.body)
        is AlarmTarget.PlanetAlign -> listOf(target.bodyA, target.bodyB)
        is AlarmTarget.AllPlanetsAlign -> PlanetBody.entries.toList()
        else -> if (onSol(target)) listOf(PlanetBody.EARTH) else emptyList()
    }

    fun solMarks(
        alarms: List<AstroAlarm>,
        place: AstroPlace?,
        now: Instant,
    ): List<Pair<AstroAlarm, Instant>> {
        val scoped = collapse(alarms.filter { it.enabled && onSol(it.target) })
        return scoped.mapNotNull { alarm ->
            val next = AstroNextFire.nextInstant(alarm, place, now, all = alarms) ?: return@mapNotNull null
            alarm to next
        }
    }

    fun onDaily(target: AlarmTarget): Boolean = when (target) {
        is AlarmTarget.CustomClock -> true
        is AlarmTarget.Solar -> true
        is AlarmTarget.Lunar -> target.event == LunarEventType.Moonrise ||
            target.event == LunarEventType.Moonset ||
            target.event == LunarEventType.MoonTransit
        is AlarmTarget.SolarTerm -> true
        is AlarmTarget.Zodiac -> AlarmFireIdentity.keyOf(target) != null
        else -> false
    }

    fun collapse(alarms: List<AstroAlarm>): List<AstroAlarm> {
        val enabled = alarms.filter { it.enabled }
        val unkeyed = enabled.filter { AlarmFireIdentity.keyOf(it.target) == null }
        val primaries = enabled.filter { AlarmFireIdentity.keyOf(it.target) != null }
            .groupBy { AlarmFireIdentity.keyOf(it.target)!! }
            .values
            .mapNotNull { AlarmFireIdentity.primary(it) }
        return unkeyed + primaries
    }

    fun dailyMarks(alarms: List<AstroAlarm>, place: AstroPlace?, now: Instant, horizon: Instant): List<Pair<AstroAlarm, Instant>> {
        val scoped = collapse(alarms.filter { it.enabled && onDaily(it.target) })
        return scoped.mapNotNull { alarm ->
            val next = AstroNextFire.nextInstant(alarm, place, now, all = alarms) ?: return@mapNotNull null
            if (next.isAfter(now) && !next.isAfter(horizon)) alarm to next else null
        }
    }

    fun upcomingLines(alarms: List<AstroAlarm>, place: AstroPlace?, now: Instant, horizon: Instant): List<Pair<AstroAlarm, Instant>> {
        val scoped = collapse(alarms.filter { it.enabled })
        return scoped.mapNotNull { alarm ->
            val next = AstroNextFire.nextInstant(alarm, place, now, all = alarms) ?: return@mapNotNull null
            if (next.isAfter(now) && !next.isAfter(horizon)) alarm to next else null
        }.sortedBy { it.second }
    }
}
