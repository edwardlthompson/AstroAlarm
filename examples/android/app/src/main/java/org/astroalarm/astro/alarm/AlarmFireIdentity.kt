package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.zodiac.ZodiacPoint
import org.astroalarm.astro.zodiac.ZodiacSign
import org.astroalarm.solarterm.SolarTerm
import java.time.Instant
import kotlin.math.abs

data class FireKey(val lonDeg: Int, val offsetMinutes: Int)

object AlarmFireIdentity {
    const val JITTER_SEC = 2L

    fun keyOf(target: AlarmTarget): FireKey? = when (target) {
        is AlarmTarget.Solar -> seasonLon(target.event)?.let { FireKey(it, target.offsetMinutes) }
        is AlarmTarget.SolarTerm -> cardinalLon(target.term)?.let { FireKey(it, target.offsetMinutes) }
        is AlarmTarget.Zodiac ->
            if (target.point == ZodiacPoint.Beginning) {
                cardinalLon(target.sign.startLongitudeDeg)?.let { FireKey(it, target.offsetMinutes) }
            } else {
                null
            }
        else -> null
    }

    fun peers(alarm: AstroAlarm, all: List<AstroAlarm>): List<AstroAlarm> {
        val key = keyOf(alarm.target) ?: return emptyList()
        if (alarm.id.isBlank()) return emptyList()
        return all.filter { it.enabled && it.id.isNotBlank() && keyOf(it.target) == key }
    }

    fun primary(peers: List<AstroAlarm>): AstroAlarm? =
        peers.filter { it.enabled && it.id.isNotBlank() }.minByOrNull { it.id }

    fun otherPeer(target: AlarmTarget, excludeId: String?, all: List<AstroAlarm>): AstroAlarm? {
        val key = keyOf(target) ?: return null
        return all.firstOrNull {
            it.enabled && it.id.isNotBlank() && it.id != (excludeId ?: "") && keyOf(it.target) == key
        }
    }

    fun sameOccurrence(a: AstroAlarm, b: AstroAlarm, instantA: Instant, instantB: Instant): Boolean {
        val ka = keyOf(a.target) ?: return false
        val kb = keyOf(b.target) ?: return false
        return ka == kb && abs(instantA.epochSecond - instantB.epochSecond) <= JITTER_SEC
    }

    fun occurrenceConsumed(instant: Instant, alarm: AstroAlarm, all: List<AstroAlarm>): Boolean {
        val group = peers(alarm, if (all.isEmpty()) listOf(alarm) else all)
        val check = if (group.isEmpty()) listOf(alarm) else group
        return check.any { fired ->
            fired.lastFiredEpochMs > 0L &&
                fired.lastFiredEpochMs / 1000L >= instant.epochSecond - JITTER_SEC
        }
    }

    fun consumeOccurrence(all: List<AstroAlarm>, ringing: AstroAlarm, firedAtMs: Long): List<AstroAlarm> {
        val key = keyOf(ringing.target)
        return all.map { alarm ->
            val match = alarm.id == ringing.id ||
                (alarm.enabled && alarm.id.isNotBlank() && key != null && keyOf(alarm.target) == key)
            if (!match) {
                alarm
            } else {
                alarm.copy(
                    lastFiredEpochMs = firedAtMs,
                    enabled = if (alarm.id == ringing.id && ringing.isOnce) false else alarm.enabled,
                )
            }
        }
    }

    fun collapsePairs(pairs: List<Pair<AstroAlarm, Instant>>): List<Pair<AstroAlarm, Instant>> {
        val unkeyed = pairs.filter { keyOf(it.first.target) == null }
        val keyed = pairs.filter { keyOf(it.first.target) != null }
        val collapsed = keyed.groupBy { keyOf(it.first.target)!! }.values.mapNotNull { group ->
            val earliest = group.minByOrNull { it.second } ?: return@mapNotNull null
            val nearby = group.filter { sameOccurrence(earliest.first, it.first, earliest.second, it.second) }
            val prim = primary(nearby.map { it.first }) ?: earliest.first
            prim to earliest.second
        }
        return unkeyed + collapsed
    }

    fun armedPair(pairs: List<Pair<AstroAlarm, Instant>>): Pair<AstroAlarm, Instant>? =
        collapsePairs(pairs).minWithOrNull(compareBy<Pair<AstroAlarm, Instant>> { it.second }.thenBy { it.first.id })

    private fun seasonLon(event: SolarEventType): Int? = when (event) {
        SolarEventType.MarchEquinox -> 0
        SolarEventType.JuneSolstice -> 90
        SolarEventType.SeptemberEquinox -> 180
        SolarEventType.DecemberSolstice -> 270
        else -> null
    }

    private fun cardinalLon(term: SolarTerm): Int? = when (term) {
        SolarTerm.CHUNFEN -> 0
        SolarTerm.XIAZHI -> 90
        SolarTerm.QIUFEN -> 180
        SolarTerm.DONGZHI -> 270
        else -> null
    }

    private fun cardinalLon(lonDeg: Double): Int? {
        val n = lonDeg.toInt()
        return if (n == 0 || n == 90 || n == 180 || n == 270) n else null
    }
}
