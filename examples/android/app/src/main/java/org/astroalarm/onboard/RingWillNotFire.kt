package org.astroalarm.onboard

object RingWillNotFire {
    fun primary(missingRing: List<OnboardingStep>): OnboardingStep? =
        missingRing.firstOrNull { it != OnboardingStep.Location }

    fun shouldShow(missingRing: List<OnboardingStep>): Boolean =
        primary(missingRing) != null
}
