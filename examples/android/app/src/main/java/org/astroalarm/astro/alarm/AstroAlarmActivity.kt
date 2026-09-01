package org.astroalarm.astro.alarm

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.foss.goldenpath.R
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.tts.TtsPreferences
import org.astroalarm.ui.AstroAlarmLockscreenView
import java.time.LocalTime
import java.util.Locale

class AstroAlarmActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null
    private var tts: TextToSpeech? = null
    private var activeAlarm: AstroAlarm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        turnOnScreenAndShowWhenLocked()

        val alarmId = intent.getStringExtra(AstroAlarmScheduler.EXTRA_ALARM_ID) ?: ""
        val store = AstroAlarmStore(this)
        activeAlarm = store.getById(alarmId)
            ?: store.getAll().firstOrNull { it.enabled }
            ?: store.getAll().lastOrNull()
            ?: AstroAlarm("unknown", getString(R.string.astro_custom_alarm_title), target = AlarmTarget.CustomClock(LocalTime.now().hour, LocalTime.now().minute))

        startAlarmOutput(activeAlarm!!)
        setContent {
            AstroAlarmLockscreenView(
                alarm = activeAlarm!!,
                onSnooze = { onSnoozeClicked() },
                onStop = { onStopClicked() }
            )
        }
    }

    private fun turnOnScreenAndShowWhenLocked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun startAlarmOutput(alarm: AstroAlarm) {
        if (alarm.toneEnabled) {
            val uri = alarm.toneUri?.let { Uri.parse(it) }
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ringtone = RingtoneManager.getRingtone(applicationContext, uri)?.apply {
                audioAttributes = AlarmNotificationChannel.alarmAudioAttributes()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) isLooping = true
                play()
            }
        }

        if (alarm.vibrateEnabled) {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
            } else {
                @Suppress("DEPRECATION") getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            val pattern = longArrayOf(0, 800, 400, 800, 400)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION") vibrator?.vibrate(pattern, 0)
            }
        }

        if (alarm.ttsEnabled) {
            tts = TextToSpeech(this, this)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.setAudioAttributes(AlarmNotificationChannel.alarmAudioAttributes())
            val ttsPrefs = TtsPreferences(applicationContext).getVoice()
            tts?.setPitch(ttsPrefs.pitch)
            if (ttsPrefs.languageTag.isNotBlank()) {
                tts?.language = Locale.forLanguageTag(ttsPrefs.languageTag)
            } else {
                tts?.language = Locale.getDefault()
            }
            if (ttsPrefs.voiceName.isNotBlank()) {
                val match = tts?.voices?.firstOrNull { it.name == ttsPrefs.voiceName }
                if (match != null) tts?.voice = match
            }
            val text = activeAlarm?.label ?: getString(R.string.astro_custom_alarm_title)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "astro_alarm_shout")
        }
    }

    private fun stopAlarmOutput() {
        runCatching { ringtone?.stop() }
        runCatching { vibrator?.cancel() }
        val engine = tts
        tts = null
        if (engine != null) {
            runCatching { engine.stop() }
            runCatching { engine.shutdown() }
        }
    }

    private fun onSnoozeClicked() {
        stopAlarmOutput()
        AlarmNotificationChannel.cancel(this)
        AstroAlarmScheduler.rescheduleAll(this)
        finish()
    }

    private fun onStopClicked() {
        stopAlarmOutput()
        AlarmNotificationChannel.cancel(this)
        activeAlarm?.let { alarm ->
            val store = AstroAlarmStore(this)
            val updated = if (alarm.isOnce) alarm.copy(enabled = false, lastFiredEpochMs = System.currentTimeMillis()) else alarm.copy(lastFiredEpochMs = System.currentTimeMillis())
            store.save(updated)
            AstroAlarmScheduler.rescheduleAll(this)
        }
        finish()
    }

    override fun onDestroy() {
        stopAlarmOutput()
        super.onDestroy()
    }
}
