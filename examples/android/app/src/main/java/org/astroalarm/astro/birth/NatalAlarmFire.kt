package org.astroalarm.astro.birth

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.NatalAspect
import java.time.Instant

/** Resolve natal longitudes for scheduling from a stored profile. */
object NatalAlarmFire {
    fun nextInstant(target: AlarmTarget, profiles: List<BirthProfile>, now: Instant): Instant? {
        return when (target) {
            is AlarmTarget.NatalAscAspect -> {
                val chart = chart(target.profileId, profiles) ?: return null
                val asc = chart.ascendant?.longitudeDeg ?: return null
                NatalNext.nextAspectToNatal(target.body, asc, target.aspect, now)
                    ?.plusSeconds(target.offsetMinutes * 60L)
            }
            is AlarmTarget.NatalMcAspect -> {
                val chart = chart(target.profileId, profiles) ?: return null
                val mc = chart.midheaven?.longitudeDeg ?: return null
                NatalNext.nextAspectToNatal(target.body, mc, target.aspect, now)
                    ?.plusSeconds(target.offsetMinutes * 60L)
            }
            is AlarmTarget.MoonReturn -> {
                val chart = chart(target.profileId, profiles) ?: return null
                NatalNext.nextReturn(NatalBody.MOON, chart.moon.longitudeDeg, now)
                    ?.plusSeconds(target.offsetMinutes * 60L)
            }
            is AlarmTarget.SolarReturn -> {
                val chart = chart(target.profileId, profiles) ?: return null
                NatalNext.nextReturn(NatalBody.SUN, chart.sun.longitudeDeg, now)
                    ?.plusSeconds(target.offsetMinutes * 60L)
            }
            is AlarmTarget.MercuryStation -> {
                NatalNext.nextMercuryStation(
                    retrogradeStart = target.kind == org.astroalarm.astro.model.MercuryStationKind.RetrogradeStart,
                    now = now,
                )?.plusSeconds(target.offsetMinutes * 60L)
            }
            is AlarmTarget.MoonSignIngress ->
                NatalNext.nextMoonSignIngress(target.sign, now)
                    ?.plusSeconds(target.offsetMinutes * 60L)
            is AlarmTarget.NatalCompoundSpecific -> {
                val chart = chart(target.profileId, profiles) ?: return null
                val kinds = target.kinds.mapNotNull {
                    runCatching { NatalStoryKind.valueOf(it) }.getOrNull()
                }
                NatalCompoundNext.nextSpecific(chart, kinds, now)
                    ?.at?.plusSeconds(target.offsetMinutes * 60L)
            }
            is AlarmTarget.NatalCompoundAny -> {
                val chart = chart(target.profileId, profiles) ?: return null
                NatalCompoundNext.nextAny(chart, target.arity, now)
                    ?.at?.plusSeconds(target.offsetMinutes * 60L)
            }
            else -> null
        }
    }

    private fun chart(profileId: String, profiles: List<BirthProfile>): NatalChart? {
        val profile = profiles.firstOrNull { it.id == profileId } ?: return null
        return BirthChartCalculator.compute(profile)
    }

    fun needsAscendant(aspect: NatalAspect): Boolean = true
}
