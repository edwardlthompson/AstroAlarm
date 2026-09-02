package org.astroalarm.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GlobeObliquityTest {
    @Test
    fun juneRollsNorthTowardSubsolar() {
        val pole = Triple(0.0, 1.0, 0.0)
        val sub = Triple(1.0, 0.0, 0.0)
        val roll = GlobeObliquity.rollDeg(23.44, pole, sub)
        assertTrue(roll in 20f..26f)
    }

    @Test
    fun decemberIsOppositeSignOfJune() {
        val pole = Triple(0.0, 1.0, 0.0)
        val sub = Triple(1.0, 0.0, 0.0)
        val june = GlobeObliquity.rollDeg(23.44, pole, sub)
        val dec = GlobeObliquity.rollDeg(-23.44, pole, sub)
        assertTrue(june * dec < 0f)
        assertTrue(kotlin.math.abs(dec) in 20f..26f)
    }

    @Test
    fun equinoxAndPolarAreZero() {
        val pole = Triple(0.0, 1.0, 0.0)
        val sub = Triple(1.0, 0.0, 0.0)
        assertEquals(0f, GlobeObliquity.rollDeg(0.2, pole, sub), 1e-4f)
        assertEquals(0f, GlobeObliquity.rollDeg(23.44, Triple(0.0, 0.0, 1.0), sub), 1e-4f)
        assertEquals(0f, GlobeObliquity.rollDeg(23.44, pole, Triple(0.0, 0.0, 1.0)), 1e-4f)
    }
}
