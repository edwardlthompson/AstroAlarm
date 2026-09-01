package org.astroalarm.onboard

import android.content.Context

class OnboardingPreferences(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isComplete(): Boolean = prefs.getBoolean(KEY_COMPLETE, false)

    fun markComplete() {
        prefs.edit().putBoolean(KEY_COMPLETE, true).apply()
    }

    companion object {
        private const val PREFS = "astro_onboarding_prefs"
        private const val KEY_COMPLETE = "onboarding_complete"
    }
}
