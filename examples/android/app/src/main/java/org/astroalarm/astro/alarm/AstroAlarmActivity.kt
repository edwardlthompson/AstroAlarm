package org.astroalarm.astro.alarm

import android.content.BroadcastReceiver
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.foss.goldenpath.R
import dev.foss.goldenpath.ui.theme.GoldenPathTheme
import dev.foss.goldenpath.ui.theme.ThemeMode
import org.astroalarm.ui.AstroAlarmLockscreenView

class AstroAlarmActivity : ComponentActivity() {
    private val ttsHandler = Handler(Looper.getMainLooper())
    private var activeAlarm = AlarmOutputPolicy.resolveAlarm(null, "unknown", "Alarm")
    private var ttsHint by mutableStateOf<String?>(null)
    private var uiDismissReceiver: BroadcastReceiver? = null
    private lateinit var output: AlarmLockscreenOutput

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        turnOnScreenAndShowWhenLocked()
        uiDismissReceiver = AlarmUiDismissBridge.register(this) {
            output.stop()
            finish()
        }

        val alarmId = intent.getStringExtra(AstroAlarmScheduler.EXTRA_ALARM_ID) ?: ""
        val store = AstroAlarmStore(this)
        activeAlarm = AlarmOutputPolicy.resolveAlarm(
            store.getById(alarmId),
            alarmId,
            getString(R.string.astro_custom_alarm_title),
        )
        output = AlarmLockscreenOutput(this, ttsHandler) {
            ttsHint = getString(R.string.astro_tts_unavailable)
        }
        output.start(activeAlarm)
        AlarmRingPresenter.postRinging(this, activeAlarm.id, activeAlarm.snoozeMinutes)
        setContent {
            GoldenPathTheme(themeMode = ThemeMode.Dark) {
                AstroAlarmLockscreenView(
                    alarm = activeAlarm,
                    ttsHint = ttsHint,
                    onSnooze = { onSnoozeClicked() },
                    onStop = { onStopClicked() },
                )
            }
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

    private fun onSnoozeClicked() {
        output.stop()
        AlarmNotificationChannel.cancel(this)
        AlarmDismissActions.snooze(this, activeAlarm)
        finish()
    }

    private fun onStopClicked() {
        output.stop()
        AlarmNotificationChannel.cancel(this)
        AlarmDismissActions.stop(this, activeAlarm)
        finish()
    }

    override fun onDestroy() {
        AlarmUiDismissBridge.unregister(this, uiDismissReceiver)
        uiDismissReceiver = null
        if (::output.isInitialized) output.stop()
        super.onDestroy()
    }
}
