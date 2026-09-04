package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.solarterm.SolarTerm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset

class AstroNextFirePeerTest {
    @Test
    fun solarSeasonalWithoutPlaceIsNull() {
        val alarm = AstroAlarm("8", "June", target = AlarmTarget.Solar(SolarEventType.JuneSolstice))
        assertNull(AstroNextFire.nextInstant(alarm, place = null, now = Instant.parse("2026-01-15T00:00:00Z")))
    }

    @Test
    fun jieqiWithoutPlaceStillFires() {
        val alarm = AstroAlarm("9", "Xiazhi", target = AlarmTarget.SolarTerm(SolarTerm.XIAZHI))
        assertNotNull(AstroNextFire.nextInstant(alarm, place = null, now = Instant.parse("2026-01-15T00:00:00Z")))
    }

    @Test
    fun lastFiredOnPeerSkipsThisYear() {
        val now = Instant.parse("2026-01-15T00:00:00Z")
        val jieqi = AstroAlarm("a", "Xiazhi", target = AlarmTarget.SolarTerm(SolarTerm.XIAZHI))
        val thisYear = AstroNextFire.nextInstant(jieqi, place = null, now = now)!!
        val sun = AstroAlarm(
            "b", "June", target = AlarmTarget.Solar(SolarEventType.JuneSolstice),
            lastFiredEpochMs = thisYear.toEpochMilli(),
        )
        val skipped = AstroNextFire.nextInstant(jieqi, place = null, now = now, all = listOf(jieqi, sun))
        assertNotNull(skipped)
        assertTrue(skipped!!.isAfter(thisYear.plusSeconds(86400L * 30)))
    }

    @Test
    fun lastFiredThirtySecondsLaterStillSkipsThisYear() {
        val now = Instant.parse("2026-01-15T00:00:00Z")
        val jieqi = AstroAlarm("a", "Xiazhi", target = AlarmTarget.SolarTerm(SolarTerm.XIAZHI))
        val thisYear = AstroNextFire.nextInstant(jieqi, place = null, now = now)!!
        val sun = AstroAlarm(
            "b", "June", target = AlarmTarget.Solar(SolarEventType.JuneSolstice),
            lastFiredEpochMs = thisYear.plusSeconds(30).toEpochMilli(),
        )
        val skipped = AstroNextFire.nextInstant(jieqi, place = null, now = now, all = listOf(jieqi, sun))
        assertNotNull(skipped)
        assertTrue(skipped!!.isAfter(thisYear.plusSeconds(86400L * 30)))
    }

    @Test
    fun offsetSixtyStillThisYear() {
        val plus = AstroAlarm("p", "June+60", target = AlarmTarget.SolarTerm(SolarTerm.XIAZHI, 60))
        val next = AstroNextFire.nextInstant(plus, place = null, now = Instant.parse("2026-01-15T00:00:00Z"))
        assertNotNull(next)
        assertEquals(2026, next!!.atZone(ZoneOffset.UTC).year)
    }
}
