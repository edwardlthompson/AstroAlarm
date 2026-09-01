package org.astroalarm.widget

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DiskEventTimeLayersTest {

    @Test
    fun enabledToggleShowsSunriseSunsetAndAlarms() {
        val layers = DiskEventTimeLayers.fromToggle(true)
        assertTrue(layers.sunriseSunsetBadges)
        assertTrue(layers.alarmMarkers)
    }

    @Test
    fun cleanViewHidesSunriseSunsetAndAlarms() {
        val layers = DiskEventTimeLayers.fromToggle(false)
        assertFalse(layers.sunriseSunsetBadges)
        assertFalse(layers.alarmMarkers)
    }
}
