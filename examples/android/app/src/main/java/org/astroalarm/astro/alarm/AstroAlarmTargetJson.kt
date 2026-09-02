package org.astroalarm.astro.alarm

import org.json.JSONObject
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.LunarEventType
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.sol.PlanetBody
import org.astroalarm.sol.PlanetEventType
import org.astroalarm.solarterm.SolarTerm
import org.astroalarm.astro.zodiac.ZodiacPoint
import org.astroalarm.astro.zodiac.ZodiacSign

internal object AstroAlarmTargetJson {
    fun write(target: AlarmTarget): JSONObject {
        val obj = JSONObject()
        when (target) {
            is AlarmTarget.CustomClock -> {
                obj.put("kind", "clock"); obj.put("hour", target.hour); obj.put("minute", target.minute)
            }
            is AlarmTarget.Solar -> {
                obj.put("kind", "solar"); obj.put("event", target.event.name); obj.put("offset", target.offsetMinutes)
            }
            is AlarmTarget.Lunar -> {
                obj.put("kind", "lunar"); obj.put("event", target.event.name); obj.put("offset", target.offsetMinutes)
            }
            is AlarmTarget.Zodiac -> {
                obj.put("kind", "zodiac"); obj.put("sign", target.sign.name)
                obj.put("point", target.point.name); obj.put("offset", target.offsetMinutes)
            }
            is AlarmTarget.SolarTerm -> {
                obj.put("kind", "solarterm"); obj.put("term", target.term.name); obj.put("offset", target.offsetMinutes)
            }
            is AlarmTarget.Planet -> {
                obj.put("kind", "planet"); obj.put("body", target.body.name)
                obj.put("event", target.event.name); obj.put("offset", target.offsetMinutes)
            }
            is AlarmTarget.PlanetAlign -> {
                obj.put("kind", "planet_align"); obj.put("bodyA", target.bodyA.name)
                obj.put("bodyB", target.bodyB.name); obj.put("offset", target.offsetMinutes)
            }
            is AlarmTarget.AllPlanetsAlign -> {
                obj.put("kind", "all_planets_align"); obj.put("offset", target.offsetMinutes)
            }
        }
        return obj
    }

    fun read(obj: JSONObject): AlarmTarget? = when (obj.optString("kind")) {
        "clock" -> AlarmTarget.CustomClock(obj.optInt("hour", 7), obj.optInt("minute", 0))
        "solar" -> {
            val name = when (val ev = obj.optString("event")) {
                "Dawn" -> "CivilDawn"; "Dusk" -> "CivilDusk"; else -> ev
            }
            val ev = runCatching { SolarEventType.valueOf(name) }.getOrDefault(SolarEventType.Sunrise)
            AlarmTarget.Solar(ev, obj.optInt("offset", 0))
        }
        "lunar" -> {
            val ev = runCatching { LunarEventType.valueOf(obj.optString("event")) }.getOrDefault(LunarEventType.Moonrise)
            AlarmTarget.Lunar(ev, obj.optInt("offset", 0))
        }
        "zodiac" -> {
            val sign = runCatching { ZodiacSign.valueOf(obj.optString("sign")) }.getOrDefault(ZodiacSign.Aries)
            val point = runCatching { ZodiacPoint.valueOf(obj.optString("point")) }.getOrDefault(ZodiacPoint.Beginning)
            AlarmTarget.Zodiac(sign, point, obj.optInt("offset", 0))
        }
        "solarterm" -> {
            val term = runCatching { SolarTerm.valueOf(obj.optString("term")) }.getOrDefault(SolarTerm.LICHUN)
            AlarmTarget.SolarTerm(term, obj.optInt("offset", 0))
        }
        "planet" -> {
            val body = runCatching { PlanetBody.valueOf(obj.optString("body")) }.getOrDefault(PlanetBody.MARS)
            val ev = runCatching { PlanetEventType.valueOf(obj.optString("event")) }.getOrDefault(PlanetEventType.Rise)
            AlarmTarget.Planet(body, ev, obj.optInt("offset", 0))
        }
        "planet_align" -> {
            val a = runCatching { PlanetBody.valueOf(obj.optString("bodyA")) }.getOrDefault(PlanetBody.VENUS)
            val b = runCatching { PlanetBody.valueOf(obj.optString("bodyB")) }.getOrDefault(PlanetBody.MARS)
            AlarmTarget.PlanetAlign(a, b, obj.optInt("offset", 0))
        }
        "all_planets_align" -> AlarmTarget.AllPlanetsAlign(obj.optInt("offset", 0))
        else -> null
    }
}
