package org.astroalarm.astro.alarm

import org.astroalarm.astro.birth.NatalBody
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.MercuryStationKind
import org.astroalarm.astro.model.NatalAspect
import org.astroalarm.astro.zodiac.ZodiacSign
import org.json.JSONObject

/** Natal / compound AlarmTarget JSON (keeps AstroAlarmTargetJson ≤150). */
internal object AstroAlarmNatalTargetJson {
    fun write(target: AlarmTarget, obj: JSONObject): Boolean {
        when (target) {
            is AlarmTarget.NatalAscAspect -> {
                obj.put("kind", "natal_asc")
                obj.put("body", target.body.name)
                obj.put("aspect", target.aspect.name)
                obj.put("profileId", target.profileId)
                obj.put("offset", target.offsetMinutes)
            }
            is AlarmTarget.NatalMcAspect -> {
                obj.put("kind", "natal_mc")
                obj.put("body", target.body.name)
                obj.put("aspect", target.aspect.name)
                obj.put("profileId", target.profileId)
                obj.put("offset", target.offsetMinutes)
            }
            is AlarmTarget.MoonReturn -> {
                obj.put("kind", "moon_return"); obj.put("profileId", target.profileId)
                obj.put("offset", target.offsetMinutes)
            }
            is AlarmTarget.SolarReturn -> {
                obj.put("kind", "solar_return"); obj.put("profileId", target.profileId)
                obj.put("offset", target.offsetMinutes)
            }
            is AlarmTarget.MercuryStation -> {
                obj.put("kind", "mercury_station"); obj.put("station", target.kind.name)
                obj.put("offset", target.offsetMinutes)
            }
            is AlarmTarget.MoonSignIngress -> {
                obj.put("kind", "moon_ingress"); obj.put("sign", target.sign.name)
                obj.put("offset", target.offsetMinutes)
            }
            is AlarmTarget.NatalCompoundSpecific -> {
                obj.put("kind", "natal_compound_specific")
                obj.put("kinds", target.kinds.joinToString(","))
                obj.put("profileId", target.profileId)
                obj.put("offset", target.offsetMinutes)
            }
            is AlarmTarget.NatalCompoundAny -> {
                obj.put("kind", "natal_compound_any")
                obj.put("arity", target.arity)
                obj.put("profileId", target.profileId)
                obj.put("offset", target.offsetMinutes)
            }
            else -> return false
        }
        return true
    }

    fun read(kind: String, obj: JSONObject): AlarmTarget? = when (kind) {
        "natal_asc" -> AlarmTarget.NatalAscAspect(
            body = runCatching { NatalBody.valueOf(obj.optString("body")) }.getOrDefault(NatalBody.SUN),
            aspect = runCatching { NatalAspect.valueOf(obj.optString("aspect")) }
                .getOrDefault(NatalAspect.Conjunction),
            profileId = obj.optString("profileId"),
            offsetMinutes = obj.optInt("offset", 0),
        )
        "natal_mc" -> AlarmTarget.NatalMcAspect(
            body = runCatching { NatalBody.valueOf(obj.optString("body")) }.getOrDefault(NatalBody.SUN),
            aspect = runCatching { NatalAspect.valueOf(obj.optString("aspect")) }
                .getOrDefault(NatalAspect.Conjunction),
            profileId = obj.optString("profileId"),
            offsetMinutes = obj.optInt("offset", 0),
        )
        "moon_return" -> AlarmTarget.MoonReturn(obj.optString("profileId"), obj.optInt("offset", 0))
        "solar_return" -> AlarmTarget.SolarReturn(obj.optString("profileId"), obj.optInt("offset", 0))
        "mercury_station" -> AlarmTarget.MercuryStation(
            kind = runCatching { MercuryStationKind.valueOf(obj.optString("station")) }
                .getOrDefault(MercuryStationKind.RetrogradeStart),
            offsetMinutes = obj.optInt("offset", 0),
        )
        "moon_ingress" -> AlarmTarget.MoonSignIngress(
            sign = runCatching { ZodiacSign.valueOf(obj.optString("sign")) }.getOrDefault(ZodiacSign.Aries),
            offsetMinutes = obj.optInt("offset", 0),
        )
        "natal_compound_specific" -> AlarmTarget.NatalCompoundSpecific(
            kinds = obj.optString("kinds").split(',').map { it.trim() }.filter { it.isNotEmpty() },
            profileId = obj.optString("profileId"),
            offsetMinutes = obj.optInt("offset", 0),
        )
        "natal_compound_any" -> AlarmTarget.NatalCompoundAny(
            arity = obj.optInt("arity", 2).coerceIn(2, 3),
            profileId = obj.optString("profileId"),
            offsetMinutes = obj.optInt("offset", 0),
        )
        else -> null
    }
}
