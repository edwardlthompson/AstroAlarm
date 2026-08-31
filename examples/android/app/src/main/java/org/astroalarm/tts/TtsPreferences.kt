package org.astroalarm.tts

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TtsPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("astro_tts_prefs", Context.MODE_PRIVATE)

    private val _voice = MutableStateFlow(load())
    val voice: StateFlow<TtsVoice> = _voice.asStateFlow()

    fun getVoice(): TtsVoice = _voice.value

    fun setVoice(voice: TtsVoice) {
        val v = voice.clamp()
        prefs.edit()
            .putFloat(KEY_PITCH, v.pitch)
            .putString(KEY_LANG, v.languageTag)
            .putString(KEY_ENGINE, v.engine)
            .putString(KEY_VOICE_NAME, v.voiceName)
            .putInt(KEY_MIN_QUALITY, v.minQuality)
            .apply()
        _voice.value = v
    }

    private fun load(): TtsVoice {
        val pitch = prefs.getFloat(KEY_PITCH, TtsVoice.DEFAULT_PITCH)
        val lang = prefs.getString(KEY_LANG, "") ?: ""
        val engine = prefs.getString(KEY_ENGINE, "") ?: ""
        val voiceName = prefs.getString(KEY_VOICE_NAME, "") ?: ""
        val minQuality = prefs.getInt(KEY_MIN_QUALITY, TtsVoice.QUALITY_VERY_HIGH)
        return TtsVoice(pitch, lang, engine, voiceName, minQuality).clamp()
    }

    companion object {
        private const val KEY_PITCH = "tts_pitch"
        private const val KEY_LANG = "tts_lang"
        private const val KEY_ENGINE = "tts_engine"
        private const val KEY_VOICE_NAME = "tts_voice_name"
        private const val KEY_MIN_QUALITY = "tts_min_quality"
    }
}
