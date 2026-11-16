package org.astroalarm.astro.alarm

import org.astroalarm.astro.birth.NatalBody
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.LunarEventType
import org.astroalarm.astro.model.MercuryStationKind
import org.astroalarm.astro.model.NatalAspect
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.zodiac.ZodiacPoint
import org.astroalarm.astro.zodiac.ZodiacSign
import org.astroalarm.sol.PlanetBody
import org.astroalarm.sol.PlanetEventType
import org.astroalarm.solarterm.SolarTerm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.json.JSONObject

class AlarmTargetJsonRoundTripTest {
    private fun roundTrip(target: AlarmTarget): AlarmTarget {
        val parsed = AstroAlarmJson.fromJson(AstroAlarmJson.toJson(AstroAlarm("id", "lab", target = target)))
        return parsed!!.target
    }

    @Test
    fun allSixteenTargetFamiliesRoundTrip() {
        val samples = listOf(
            AlarmTarget.CustomClock(7, 30),
            AlarmTarget.Solar(SolarEventType.Sunrise, -10),
            AlarmTarget.Lunar(LunarEventType.FullMoon, 5),
            AlarmTarget.Zodiac(ZodiacSign.Aries, ZodiacPoint.Beginning, 0),
            AlarmTarget.SolarTerm(SolarTerm.LICHUN, 15),
            AlarmTarget.Planet(PlanetBody.MERCURY, PlanetEventType.RetrogradeStart, -5),
            AlarmTarget.PlanetAlign(PlanetBody.VENUS, PlanetBody.MARS, 0),
            AlarmTarget.AllPlanetsAlign(10),
            AlarmTarget.Occasion(SkyOccasionId.NAKED_EYE_2040.id),
            AlarmTarget.NatalAscAspect(NatalBody.SUN, NatalAspect.Conjunction, "p1", 0),
            AlarmTarget.NatalMcAspect(NatalBody.MOON, NatalAspect.Square, "p1", 2),
            AlarmTarget.MoonReturn("p1", 0),
            AlarmTarget.SolarReturn("p1", 0),
            AlarmTarget.MercuryStation(MercuryStationKind.DirectStart, 0),
            AlarmTarget.MoonSignIngress(ZodiacSign.Leo, 0),
            AlarmTarget.NatalCompoundSpecific(listOf("AscSun", "MoonReturn"), "p1", 0),
            AlarmTarget.NatalCompoundAny(2, "p1", 0),
        )
        assertEquals(17, samples.size)
        samples.forEach { assertEquals(it, roundTrip(it)) }
    }

    @Test
    fun legacyDawnMapsToCivilDawn() {
        val obj = JSONObject("""{"id":"x","label":"y","target":{"kind":"solar","event":"Dawn"}}""")
        val t = AstroAlarmJson.fromJson(obj)!!.target as AlarmTarget.Solar
        assertEquals(SolarEventType.CivilDawn, t.event)
    }

    @Test
    fun unknownKindSkipped() {
        assertNull(AstroAlarmJson.fromJson(JSONObject("""{"id":"x","label":"y","target":{"kind":"nope"}}""")))
    }
}
