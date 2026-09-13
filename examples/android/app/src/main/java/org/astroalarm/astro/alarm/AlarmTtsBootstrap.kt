package org.astroalarm.astro.alarm

import android.content.Context
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import org.astroalarm.tts.TtsPreferences

/** Builds alarm TTS with preferred engine and binds the repeating speak session. */
object AlarmTtsBootstrap {
    fun createEngine(context: Context, listener: TextToSpeech.OnInitListener): TextToSpeech {
        val preferred = TtsPreferences(context.applicationContext).getVoice().engine
        return if (preferred.isNotBlank()) {
            TextToSpeech(context, listener, preferred)
        } else {
            TextToSpeech(context, listener)
        }
    }

    fun onInit(
        status: Int,
        engine: TextToSpeech?,
        handler: Handler,
        text: String,
        context: Context,
    ): AlarmTtsSession? {
        val tts = engine ?: return null
        if (status != TextToSpeech.SUCCESS) {
            Log.w(AlarmTtsSession.TAG, "TTS init failed: $status")
            return null
        }
        return AlarmTtsSession.bind(
            tts,
            handler,
            text,
            TtsPreferences(context.applicationContext).getVoice(),
        )
    }
}
