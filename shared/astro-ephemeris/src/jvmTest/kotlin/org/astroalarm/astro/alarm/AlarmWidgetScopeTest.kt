package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.sol.PlanetBody
import org.astroalarm.sol.PlanetEventType
import org.astroalarm.solarterm.SolarTerm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class AlarmWidgetScopeTest {
    private fun alarm(id: String, target: AlarmTarget) =
        AstroAlarm(id = id, label = id, target = target)

    @Test
    fun yearlyExcludesSunrise() {
        assertFalse(AlarmWidgetScope.onYearly(AlarmTarget.Solar(SolarEventType.Sunrise)))
        assertTrue(AlarmWidgetScope.onYearly(AlarmTarget.Solar(SolarEventType.JuneSolstice)))
        assertTrue(AlarmWidgetScope.onYearly(AlarmTarget.SolarTerm(SolarTerm.LICHUN)))
    }

    @Test
    fun solPutsYearlyOnEarthAndKeepsPlanets() {
        assertTrue(AlarmWidgetScope.onSol(AlarmTarget.SolarTerm(SolarTerm.XIAZHI)))
        assertTrue(AlarmWidgetScope.onSol(AlarmTarget.Solar(SolarEventType.JuneSolstice)))
        assertFalse(AlarmWidgetScope.onSol(AlarmTarget.Solar(SolarEventType.Sunrise)))
        assertEquals(listOf(PlanetBody.EARTH), AlarmWidgetScope.solBodies(AlarmTarget.SolarTerm(SolarTerm.XIAZHI)))
        assertEquals(listOf(PlanetBody.MARS), AlarmWidgetScope.solBodies(AlarmTarget.Planet(PlanetBody.MARS, PlanetEventType.Rise)))
        assertTrue(AlarmWidgetScope.onSol(AlarmTarget.Planet(PlanetBody.MARS, PlanetEventType.Rise)))
    }

    @Test
    fun solMarksCollapsePeersAndSkipEmpty() {
        val now = Instant.parse("2026-01-15T00:00:00Z")
        assertTrue(AlarmWidgetScope.solMarks(emptyList(), null, now).isEmpty())
        val sun = alarm("b", AlarmTarget.Solar(SolarEventType.JuneSolstice))
        assertTrue(AlarmWidgetScope.solMarks(listOf(sun), null, now).isEmpty())
        val jieqi = alarm("a", AlarmTarget.SolarTerm(SolarTerm.XIAZHI))
        val marks = AlarmWidgetScope.solMarks(listOf(sun, jieqi), null, now)
        assertEquals(1, marks.size)
        assertEquals("a", marks.single().first.id)
        assertTrue(marks.single().second.isAfter(now))
    }

    @Test
    fun dailyExcludesPlanets() {
        assertFalse(AlarmWidgetScope.onDaily(AlarmTarget.Planet(PlanetBody.MARS, PlanetEventType.Rise)))
        assertTrue(AlarmWidgetScope.onDaily(AlarmTarget.CustomClock(7, 0)))
        assertTrue(AlarmWidgetScope.onDaily(AlarmTarget.SolarTerm(SolarTerm.XIAZHI)))
    }

    @Test
    fun upcomingCollapsesTwoPeersToOneLine() {
        val sun = alarm("b", AlarmTarget.Solar(SolarEventType.JuneSolstice))
        val jieqi = alarm("a", AlarmTarget.SolarTerm(SolarTerm.XIAZHI))
        val collapsed = AlarmWidgetScope.collapse(listOf(sun, jieqi))
        assertEquals(1, collapsed.size)
        assertEquals("a", collapsed.single().id)
    }

    @Test
    fun emptyAlarmsYieldEmptyMarks() {
        val now = Instant.parse("2026-01-15T00:00:00Z")
        assertTrue(AlarmWidgetScope.dailyMarks(emptyList(), null, now, now.plusSeconds(86400)).isEmpty())
        assertTrue(AlarmWidgetScope.upcomingLines(emptyList(), null, now, now.plusSeconds(86400)).isEmpty())
    }
}
