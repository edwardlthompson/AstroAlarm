package org.astroalarm.astro.alarm

import android.content.Context
import android.content.SharedPreferences

class SunriseOfferStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun dismissed(): Boolean = prefs.getBoolean(KEY_DONE, false)

    fun pending(): Boolean = prefs.getBoolean(KEY_PENDING, false)

    fun markPendingIfFirstCity(hadPlace: Boolean) {
        if (!hadPlace && !dismissed()) {
            prefs.edit().putBoolean(KEY_PENDING, true).apply()
        }
    }

    fun consumePending(): Boolean {
        if (!prefs.getBoolean(KEY_PENDING, false)) return false
        prefs.edit().putBoolean(KEY_PENDING, false).apply()
        return true
    }

    fun markDone() {
        prefs.edit().putBoolean(KEY_DONE, true).putBoolean(KEY_PENDING, false).apply()
    }

    companion object {
        private const val PREFS = "astro_sunrise_offer"
        private const val KEY_DONE = "done"
        private const val KEY_PENDING = "pending"
    }
}
