package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.LunarEventType
import org.astroalarm.astro.model.SolarEventType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressiveAddTest {
    @Test
    fun catalogKeepsMoonSeasonPlanet() {
        val kinds = ProgressiveAdd.kinds(hasNatal = false)
        assertTrue(kinds.contains(ProgressiveKind.Lunar))
        assertTrue(kinds.contains(ProgressiveKind.Seasonal))
        assertTrue(kinds.contains(ProgressiveKind.Planet))
        assertEquals(6, kinds.size)
    }

    @Test
    fun natalOnlyWhenProfile() {
        assertTrue(ProgressiveKind.Natal in ProgressiveAdd.kinds(hasNatal = true))
        assertEquals(7, ProgressiveAdd.kinds(hasNatal = true).size)
    }

    @Test
    fun sunriseIsSolarKind() {
        assertEquals(
            ProgressiveKind.Solar,
            ProgressiveAdd.kindOf(AlarmTarget.Solar(SolarEventType.Sunrise, 0)),
        )
        assertEquals(
            ProgressiveKind.Lunar,
            ProgressiveAdd.kindOf(AlarmTarget.Lunar(LunarEventType.FullMoon, 0)),
        )
    }
}
