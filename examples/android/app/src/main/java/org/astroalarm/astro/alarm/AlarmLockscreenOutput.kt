package org.astroalarm.astro.alarm

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.tts.TtsPreferences

/** Owns ringtone, vibrator, and TTS for the lockscreen Activity. */
class AlarmLockscreenOutput(
    private val context: Context,
    private val handler: Handler,
    private val onTtsUnavailable: () -> Unit,
) : TextToSpeech.OnInitListener {
    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null
    private var tts: TextToSpeech? = null
    private var ttsSession: AlarmTtsSession? = null
    private var alarm: AstroAlarm? = null
    private var preferredEngineTried = false
    private var retriedDefaultEngine = false

    fun start(alarm: AstroAlarm) {
        this.alarm = alarm
        if (AlarmOutputPolicy.shouldPlayTone(alarm)) startRingtone(alarm)
        if (alarm.vibrateEnabled) startVibrate()
        if (AlarmOutputPolicy.shouldSpeak(alarm)) createTtsEngine(preferConfigured = true)
    }

    fun stop() {
        runCatching { ringtone?.stop() }
        runCatching { vibrator?.cancel() }
        tts = null
        ttsSession?.stop()
        ttsSession = null
    }

    private fun startRingtone(alarm: AstroAlarm) {
        val uri = alarm.toneUri?.let { Uri.parse(it) }
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        ringtone = RingtoneManager.getRingtone(context.applicationContext, uri)?.apply {
            audioAttributes = AlarmNotificationChannel.alarmAudioAttributes()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) isLooping = true
            play()
        }
    }

    private fun startVibrate() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION") context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
        val pattern = longArrayOf(0, 800, 400, 800, 400)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION") vibrator?.vibrate(pattern, 0)
        }
    }

    private fun createTtsEngine(preferConfigured: Boolean) {
        val voice = TtsPreferences(context.applicationContext).getVoice()
        val enginePkg = voice.engine.takeIf { it.isNotBlank() }
        preferredEngineTried = preferConfigured && enginePkg != null
        tts = if (preferConfigured && enginePkg != null) {
            TextToSpeech(context, this, enginePkg)
        } else {
            TextToSpeech(context, this)
        }
    }

    override fun onInit(status: Int) {
        val engine = tts ?: return
        if (status != TextToSpeech.SUCCESS) {
            runCatching { engine.shutdown() }
            tts = null
            if (preferredEngineTried && !retriedDefaultEngine) {
                retriedDefaultEngine = true
                preferredEngineTried = false
                createTtsEngine(preferConfigured = false)
                return
            }
            onTtsUnavailable()
            return
        }
        val ringing = alarm ?: return
        val pauseTone = AlarmOutputPolicy.shouldPlayTone(ringing)
        ttsSession = AlarmTtsSession.bind(
            engine,
            handler,
            ringing.label,
            TtsPreferences(context.applicationContext).getVoice(),
            onUtteranceStart = { if (pauseTone) runCatching { ringtone?.stop() } },
            onUtteranceDone = {
                if (pauseTone && AlarmOutputPolicy.shouldPlayTone(ringing)) {
                    runCatching { startRingtone(ringing) }
                }
            },
        )
    }
}
