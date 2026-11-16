package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.SolarEventType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlarmGroupSectionsTest {
    @Test
    fun emptyListYieldsNoSections() {
        assertTrue(AlarmGroupSections.nonEmpty(emptyList()).isEmpty())
    }

    @Test
    fun onlyClockOmitsEmptyDomains() {
        val clock = AstroAlarm("1", "Wake", target = AlarmTarget.CustomClock(7, 0))
        val solar = AstroAlarm(
            "2",
            "Sun",
            target = AlarmTarget.Solar(SolarEventType.Sunrise, 0),
        )
        val grouped = AlarmGroupSections.nonEmpty(listOf(clock, solar))
        assertEquals(2, grouped.size)
        assertEquals(AlarmGroupKind.Solar, grouped[0].first)
        assertEquals(AlarmGroupKind.Clock, grouped[1].first)
    }

    @Test
    fun natalKindCoversCompound() {
        val natal = AstroAlarm(
            "3",
            "Return",
            target = AlarmTarget.MoonReturn("p", 0),
        )
        assertEquals(AlarmGroupKind.Natal, AlarmGroupSections.kindOf(natal.target))
    }
}
