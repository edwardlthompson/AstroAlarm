package org.astroalarm.ui.onboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.astroalarm.onboard.OnboardingChecker
import org.astroalarm.onboard.OnboardingPreferences

/**
 * First-run and ongoing: a non-dismissible dialog blocks the main UI until ring
 * permissions are granted (location is optional; sideload / skipped / revoked).
 */
@Composable
fun OnboardingGate(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { OnboardingPreferences(context) }
    var epoch by remember { mutableIntStateOf(0) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) epoch++
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val suppress = OnboardingChecker.skipUiGate()
    val missing = remember(epoch) {
        if (suppress) emptyList() else OnboardingChecker.missingRingSteps(context)
    }
    val needsGate = !suppress && (missing.isNotEmpty() || !prefs.isComplete())

    Box(modifier = Modifier.fillMaxSize()) {
        content()
        if (needsGate) {
            PermissionNagDialog(
                onComplete = {
                    prefs.markComplete()
                    epoch++
                },
            )
        }
    }
}
