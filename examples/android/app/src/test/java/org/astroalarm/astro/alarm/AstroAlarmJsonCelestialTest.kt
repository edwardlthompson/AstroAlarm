package org.astroalarm.astro.alarm

import org.json.JSONObject
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.sol.PlanetBody
import org.astroalarm.sol.PlanetEventType
import org.astroalarm.solarterm.SolarTerm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant
import java.time.ZoneOffset

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class AstroAlarmJsonCelestialTest {

    private fun roundTrip(target: AlarmTarget): AlarmTarget {
        val alarm = AstroAlarm(id = "id", label = "lab", target = target)
        val parsed = AstroAlarmJson.fromJson(AstroAlarmJson.toJson(alarm))
        return parsed!!.target
    }

    @Test
    fun solarTermRoundTrip() {
        val t = roundTrip(AlarmTarget.SolarTerm(SolarTerm.LICHUN, 15)) as AlarmTarget.SolarTerm
        assertEquals(SolarTerm.LICHUN, t.term)
        assertEquals(15, t.offsetMinutes)
    }

    @Test
    fun planetRoundTrip() {
        val t = roundTrip(AlarmTarget.Planet(PlanetBody.MERCURY, PlanetEventType.RetrogradeStart, -5)) as AlarmTarget.Planet
        assertEquals(PlanetBody.MERCURY, t.body)
        assertEquals(PlanetEventType.RetrogradeStart, t.event)
        assertEquals(-5, t.offsetMinutes)
    }

    @Test
    fun planetAlignAndAllAlignRoundTrip() {
        val a = roundTrip(AlarmTarget.PlanetAlign(PlanetBody.VENUS, PlanetBody.MARS, 0)) as AlarmTarget.PlanetAlign
        assertEquals(PlanetBody.VENUS, a.bodyA)
        assertEquals(PlanetBody.MARS, a.bodyB)
        val all = roundTrip(AlarmTarget.AllPlanetsAlign(10)) as AlarmTarget.AllPlanetsAlign
        assertEquals(10, all.offsetMinutes)
    }

    @Test
    fun unknownKindSkipped() {
        val obj = JSONObject("""{"id":"x","label":"y","target":{"kind":"nope"}}""")
        assertNull(AstroAlarmJson.fromJson(obj))
    }

    @Test
    fun nextLichunAfterJanuary() {
        val alarm = AstroAlarm(id = "l", label = "l", target = AlarmTarget.SolarTerm(SolarTerm.LICHUN, 0))
        val now = Instant.parse("2026-01-15T00:00:00Z")
        val next = AstroNextFire.nextInstant(alarm, null, now)!!
        assertTrue(next.isAfter(now))
        val zdt = next.atZone(ZoneOffset.UTC)
        assertEquals(2026, zdt.year)
        assertEquals(2, zdt.monthValue)
    }
}
