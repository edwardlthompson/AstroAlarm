package dev.foss.goldenpath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.foss.goldenpath.about.AppUpdatePreferences
import dev.foss.goldenpath.display.WindowRefresh
import dev.foss.goldenpath.feedback.FeedbackDeepLink
import dev.foss.goldenpath.network.NetworkStatusMonitor
import dev.foss.goldenpath.ui.GoldenPathApp
import dev.foss.goldenpath.ui.theme.ThemePreferences
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var networkStatusMonitor: NetworkStatusMonitor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val themePreferences = ThemePreferences(applicationContext)
        val appUpdatePreferences = AppUpdatePreferences(applicationContext)
        networkStatusMonitor = NetworkStatusMonitor(applicationContext).also { it.start() }
        val deepLink = FeedbackDeepLink.parse(intent?.data?.toString())

        lifecycleScope.launch {
            appUpdatePreferences.clearPendingRestart()
            appUpdatePreferences.ensureInstalledFormat()
        }

        setContent {
            GoldenPathApp(
                context = this,
                scope = lifecycleScope,
                themePreferences = themePreferences,
                appUpdatePreferences = appUpdatePreferences,
                networkStatusMonitor = networkStatusMonitor!!,
                initialFeedbackKind = deepLink?.kind,
            )
        }
    }

    override fun onStart() {
        super.onStart()
        WindowRefresh.applyTo(this)
    }

    override fun onDestroy() {
        networkStatusMonitor?.stop()
        super.onDestroy()
    }
}
