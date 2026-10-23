package org.astroalarm.astro.birth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Opt-in local HTTP LLM settings (off by default; no API key). */
class LocalLlmPrefs(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private val _enabled = MutableStateFlow(prefs.getBoolean(KEY_ENABLED, false))
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    private val _baseUrl = MutableStateFlow(
        prefs.getString(KEY_URL, DEFAULT_URL) ?: DEFAULT_URL,
    )
    val baseUrl: StateFlow<String> = _baseUrl.asStateFlow()

    fun setEnabled(on: Boolean) {
        prefs.edit().putBoolean(KEY_ENABLED, on).apply()
        _enabled.value = on
    }

    fun setBaseUrl(url: String) {
        val cleaned = url.trim().ifBlank { DEFAULT_URL }
        prefs.edit().putString(KEY_URL, cleaned).apply()
        _baseUrl.value = cleaned
    }

    companion object {
        const val PREFS = "os_birth_llm_prefs"
        const val DEFAULT_URL = "http://127.0.0.1:11434"
        private const val KEY_ENABLED = "enabled"
        private const val KEY_URL = "base_url"
    }
}
