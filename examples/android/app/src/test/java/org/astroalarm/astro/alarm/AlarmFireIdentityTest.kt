package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.zodiac.ZodiacPoint
import org.astroalarm.astro.zodiac.ZodiacSign
import org.astroalarm.solarterm.SolarTerm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class AlarmFireIdentityTest {
    private fun alarm(id: String, target: AlarmTarget, enabled: Boolean = true, lastFired: Long = 0L) =
        AstroAlarm(id = id, label = id, enabled = enabled, target = target, lastFiredEpochMs = lastFired)

    @Test
    fun emptyListHasNoPrimary() {
        assertNull(AlarmFireIdentity.primary(emptyList()))
        assertTrue(AlarmFireIdentity.peers(alarm("a", AlarmTarget.Solar(SolarEventType.JuneSolstice)), emptyList()).isEmpty())
    }

    @Test
    fun sunriseHasNoKey() {
        assertNull(AlarmFireIdentity.keyOf(AlarmTarget.Solar(SolarEventType.Sunrise)))
        assertNull(AlarmFireIdentity.keyOf(AlarmTarget.SolarTerm(SolarTerm.LICHUN)))
        assertNull(AlarmFireIdentity.keyOf(AlarmTarget.CustomClock(7, 0)))
    }

    @Test
    fun solsticeMatchesXiazhi() {
        val sun = alarm("b-sun", AlarmTarget.Solar(SolarEventType.JuneSolstice))
        val jieqi = alarm("a-jieqi", AlarmTarget.SolarTerm(SolarTerm.XIAZHI))
        assertEquals(AlarmFireIdentity.keyOf(sun.target), AlarmFireIdentity.keyOf(jieqi.target))
        val group = AlarmFireIdentity.peers(sun, listOf(sun, jieqi))
        assertEquals(2, group.size)
        assertEquals("a-jieqi", AlarmFireIdentity.primary(group)?.id)
    }

    @Test
    fun equinoxMatchesChunfen() {
        val sun = AlarmTarget.Solar(SolarEventType.MarchEquinox)
        val jieqi = AlarmTarget.SolarTerm(SolarTerm.CHUNFEN)
        assertEquals(AlarmFireIdentity.keyOf(sun), AlarmFireIdentity.keyOf(jieqi))
    }

    @Test
    fun ariesBeginningMatchesMarchEquinox() {
        val zodiac = AlarmTarget.Zodiac(ZodiacSign.Aries, ZodiacPoint.Beginning)
        val sun = AlarmTarget.Solar(SolarEventType.MarchEquinox)
        assertEquals(AlarmFireIdentity.keyOf(zodiac), AlarmFireIdentity.keyOf(sun))
    }

    @Test
    fun offsetSixtyIsNotAPeer() {
        val zero = alarm("a", AlarmTarget.Solar(SolarEventType.JuneSolstice, 0))
        val plus = alarm("b", AlarmTarget.SolarTerm(SolarTerm.XIAZHI, 60))
        assertTrue(AlarmFireIdentity.peers(zero, listOf(zero, plus)).none { it.id == "b" })
    }

    @Test
    fun disabledPrimaryYieldsOtherEnabled() {
        val low = alarm("a", AlarmTarget.Solar(SolarEventType.JuneSolstice), enabled = false)
        val high = alarm("b", AlarmTarget.SolarTerm(SolarTerm.XIAZHI))
        val group = AlarmFireIdentity.peers(high, listOf(low, high))
        assertEquals("b", AlarmFireIdentity.primary(group)?.id)
    }

    @Test
    fun blankIdSkippedInPrimary() {
        val blank = alarm("", AlarmTarget.Solar(SolarEventType.JuneSolstice))
        val ok = alarm("z", AlarmTarget.SolarTerm(SolarTerm.XIAZHI))
        assertNull(AlarmFireIdentity.primary(listOf(blank)))
        assertEquals("z", AlarmFireIdentity.primary(listOf(blank, ok))?.id)
    }

    @Test
    fun jitterTwoSecondsGroupsThreeDoesNot() {
        val a = alarm("a", AlarmTarget.Solar(SolarEventType.JuneSolstice))
        val b = alarm("b", AlarmTarget.SolarTerm(SolarTerm.XIAZHI))
        val t = Instant.parse("2026-06-21T08:00:00Z")
        assertTrue(AlarmFireIdentity.sameOccurrence(a, b, t, t.plusSeconds(2)))
        assertFalse(AlarmFireIdentity.sameOccurrence(a, b, t, t.plusSeconds(3)))
    }

    @Test
    fun consumeStampsEnabledPeers() {
        val sun = alarm("b", AlarmTarget.Solar(SolarEventType.JuneSolstice))
        val jieqi = alarm("a", AlarmTarget.SolarTerm(SolarTerm.XIAZHI))
        val other = alarm("c", AlarmTarget.Solar(SolarEventType.Sunrise))
        val out = AlarmFireIdentity.consumeOccurrence(listOf(sun, jieqi, other), sun, 1_700_000_000_000L)
        assertEquals(1_700_000_000_000L, out.first { it.id == "a" }.lastFiredEpochMs)
        assertEquals(1_700_000_000_000L, out.first { it.id == "b" }.lastFiredEpochMs)
        assertEquals(0L, out.first { it.id == "c" }.lastFiredEpochMs)
    }

    @Test
    fun otherPeerIgnoresSelf() {
        val sun = alarm("b", AlarmTarget.Solar(SolarEventType.JuneSolstice))
        val jieqi = alarm("a", AlarmTarget.SolarTerm(SolarTerm.XIAZHI))
        assertEquals("a", AlarmFireIdentity.otherPeer(sun.target, "b", listOf(sun, jieqi))?.id)
        assertNull(AlarmFireIdentity.otherPeer(sun.target, "b", listOf(sun)))
    }

    @Test
    fun snoozeConsumeBlocksTwinThisYear() {
        val t = Instant.parse("2026-06-21T08:00:00Z")
        val sun = alarm("b", AlarmTarget.Solar(SolarEventType.JuneSolstice))
        val twin = alarm("a", AlarmTarget.SolarTerm(SolarTerm.XIAZHI))
        val after = AlarmFireIdentity.consumeOccurrence(listOf(sun, twin), sun, t.toEpochMilli())
        assertTrue(AlarmFireIdentity.occurrenceConsumed(t.plusSeconds(1), after.first { it.id == "a" }, after))
        val armed = AlarmFireIdentity.armedPair(
            listOf(after[0] to t.plusSeconds(1), after[1] to t.plusSeconds(1)),
        )
        assertTrue(armed == null || AlarmFireIdentity.occurrenceConsumed(armed.second, armed.first, after))
    }

    @Test
    fun armedPairPicksPrimaryAtEarliestInstant() {
        val later = alarm("z", AlarmTarget.Solar(SolarEventType.JuneSolstice))
        val earlierPeer = alarm("a", AlarmTarget.SolarTerm(SolarTerm.XIAZHI))
        val t = Instant.parse("2026-06-21T08:00:00Z")
        val armed = AlarmFireIdentity.armedPair(
            listOf(later to t.plusSeconds(1), earlierPeer to t),
        )
        assertEquals("a", armed?.first?.id)
        assertEquals(t, armed?.second)
    }
}
