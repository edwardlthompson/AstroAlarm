package org.astroalarm.ui.onboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import org.astroalarm.onboard.OnboardingChecker
import org.astroalarm.onboard.OnboardingPreferences

@Composable
fun OnboardingGate(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { OnboardingPreferences(context) }
    var show by remember { mutableIntStateOf(if (OnboardingChecker.skipUiGate() || prefs.isComplete()) 0 else 1) }
    if (show == 1) {
        OnboardingScreen(onDone = {
            prefs.markComplete()
            show = 0
        })
    } else {
        content()
    }
}
