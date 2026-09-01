package org.astroalarm.astro.alarm

import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import org.astroalarm.tts.TtsVoice
import java.util.Locale

class AlarmTtsSession private constructor(
    private val engine: TextToSpeech,
    private val repeater: AlarmTtsRepeater,
) {
    fun stop() {
        repeater.stop()
        runCatching { engine.stop() }
        runCatching { engine.shutdown() }
    }

    companion object {
        const val TAG = "AstroAlarmTts"

        fun bind(
            engine: TextToSpeech,
            handler: Handler,
            text: String,
            voice: TtsVoice,
        ): AlarmTtsSession {
            engine.setAudioAttributes(AlarmNotificationChannel.alarmAudioAttributes())
            engine.setPitch(voice.pitch)
            engine.language = if (voice.languageTag.isNotBlank()) {
                Locale.forLanguageTag(voice.languageTag)
            } else {
                Locale.getDefault()
            }
            if (voice.voiceName.isNotBlank()) {
                engine.voices?.firstOrNull { it.name == voice.voiceName }?.let { engine.voice = it }
            }
            val repeater = AlarmTtsRepeater(
                scheduleAfter = { delayMs, action ->
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({ action() }, delayMs)
                },
                cancelScheduled = { handler.removeCallbacksAndMessages(null) },
                speak = {
                    Log.i(TAG, "speak")
                    engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, AlarmTtsRepeater.UTTERANCE_ID)
                },
            )
            engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) = Unit
                override fun onDone(utteranceId: String?) {
                    if (utteranceId == AlarmTtsRepeater.UTTERANCE_ID) repeater.onUtteranceFinished()
                }
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    if (utteranceId == AlarmTtsRepeater.UTTERANCE_ID) repeater.onUtteranceFinished()
                }
                override fun onError(utteranceId: String?, errorCode: Int) {
                    if (utteranceId == AlarmTtsRepeater.UTTERANCE_ID) repeater.onUtteranceFinished()
                }
            })
            repeater.start()
            return AlarmTtsSession(engine, repeater)
        }
    }
}
