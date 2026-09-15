package org.astroalarm.onboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RingWillNotFireTest {
    @Test
    fun hidesWhenRingComplete() {
        assertFalse(RingWillNotFire.shouldShow(emptyList()))
        assertNull(RingWillNotFire.primary(emptyList()))
    }

    @Test
    fun exactIsShownWhenOnlyExactMissing() {
        val missing = listOf(OnboardingStep.ExactAlarms)
        assertTrue(RingWillNotFire.shouldShow(missing))
        assertEquals(OnboardingStep.ExactAlarms, RingWillNotFire.primary(missing))
    }

    @Test
    fun prefersNotificationsWhenSeveralMissing() {
        val missing = listOf(
            OnboardingStep.Notifications,
            OnboardingStep.ExactAlarms,
            OnboardingStep.FullScreenIntent,
        )
        assertEquals(OnboardingStep.Notifications, RingWillNotFire.primary(missing))
    }

    @Test
    fun ignoresLocationIfPresent() {
        assertEquals(
            OnboardingStep.ExactAlarms,
            RingWillNotFire.primary(listOf(OnboardingStep.Location, OnboardingStep.ExactAlarms)),
        )
    }
}
