package org.astroalarm.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.foss.goldenpath.R
import org.astroalarm.onboard.OnboardingChecker
import org.astroalarm.onboard.OnboardingIntents
import org.astroalarm.onboard.OnboardingStep
import org.astroalarm.onboard.RingWillNotFire

@Composable
fun AlarmPermissionBanner(context: Context) {
    var epoch by remember { mutableIntStateOf(0) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) epoch++
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val missing = remember(epoch) { OnboardingChecker.missingRingSteps(context) }
    val step = RingWillNotFire.primary(missing) ?: return
    val body = when (step) {
        OnboardingStep.Notifications -> R.string.onboard_notifications_body
        OnboardingStep.ExactAlarms -> R.string.onboard_exact_body
        OnboardingStep.FullScreenIntent -> R.string.onboard_fsi_body
        OnboardingStep.Battery -> R.string.onboard_battery_body
        OnboardingStep.Location -> return
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(R.string.ring_will_not_fire_title),
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = stringResource(body),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextButton(onClick = { OnboardingIntents.openRingFix(context) }) {
                Text(stringResource(R.string.ring_will_not_fire_action))
            }
        }
    }
}
