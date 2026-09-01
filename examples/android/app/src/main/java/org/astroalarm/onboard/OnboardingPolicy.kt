package org.astroalarm.onboard

/** Runtime and special-access steps an alarm app must ask for on this SDK. */
enum class OnboardingStep {
    Notifications,
    Location,
    ExactAlarms,
    FullScreenIntent,
    Battery,
}

object OnboardingPolicy {
    const val NOTIFICATIONS_SDK = 33
    const val EXACT_ALARM_SDK = 31
    const val FULL_SCREEN_SDK = 34
    const val BATTERY_SDK = 23

    fun steps(sdk: Int): List<OnboardingStep> = buildList {
        if (sdk >= NOTIFICATIONS_SDK) add(OnboardingStep.Notifications)
        add(OnboardingStep.Location)
        if (sdk >= EXACT_ALARM_SDK) add(OnboardingStep.ExactAlarms)
        if (sdk >= FULL_SCREEN_SDK) add(OnboardingStep.FullScreenIntent)
        if (sdk >= BATTERY_SDK) add(OnboardingStep.Battery)
    }
}
