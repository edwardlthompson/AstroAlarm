package org.astroalarm.onboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingPolicyTest {
    @Test
    fun api26AsksLocationAndBatteryOnly() {
        assertEquals(
            listOf(OnboardingStep.Location, OnboardingStep.Battery),
            OnboardingPolicy.steps(26),
        )
    }

    @Test
    fun api31AddsExactAlarms() {
        val steps = OnboardingPolicy.steps(31)
        assertTrue(steps.contains(OnboardingStep.ExactAlarms))
        assertFalse(steps.contains(OnboardingStep.Notifications))
        assertFalse(steps.contains(OnboardingStep.FullScreenIntent))
    }

    @Test
    fun api34AsksEverySpecialAccess() {
        assertEquals(
            listOf(
                OnboardingStep.Notifications,
                OnboardingStep.Location,
                OnboardingStep.ExactAlarms,
                OnboardingStep.FullScreenIntent,
                OnboardingStep.Battery,
            ),
            OnboardingPolicy.steps(34),
        )
    }
}
