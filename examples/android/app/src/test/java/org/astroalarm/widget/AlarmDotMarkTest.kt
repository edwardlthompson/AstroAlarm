package org.astroalarm.widget

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.LunarEventType
import org.astroalarm.astro.model.SolarEventType
import org.junit.Assert.assertEquals
import org.junit.Test

class AlarmDotMarkTest {
    @Test
    fun lunarUsesDiamondAndSolarUsesCircle() {
        assertEquals(
            AlarmDotMark.Shape.Circle,
            AlarmDotMark.shapeOf(AlarmTarget.Solar(SolarEventType.Sunrise, 0)),
        )
        assertEquals(
            AlarmDotMark.Shape.Diamond,
            AlarmDotMark.shapeOf(AlarmTarget.Lunar(LunarEventType.FullMoon, 0)),
        )
        assertEquals(
            AlarmDotMark.Shape.Square,
            AlarmDotMark.shapeOf(AlarmTarget.CustomClock(7, 0)),
        )
    }
}
