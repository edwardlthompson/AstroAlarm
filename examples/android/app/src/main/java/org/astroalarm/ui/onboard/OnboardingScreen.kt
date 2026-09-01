package org.astroalarm.ui.onboard

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.foss.goldenpath.R
import org.astroalarm.onboard.OnboardingChecker
import org.astroalarm.onboard.OnboardingIntents
import org.astroalarm.onboard.OnboardingPolicy
import org.astroalarm.onboard.OnboardingStep

@Composable
fun OnboardingScreen(onDone: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var epoch by remember { mutableIntStateOf(0) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) epoch++
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val snapshot = remember(epoch) { OnboardingChecker.snapshot(context) }
    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { epoch++ }
    val locLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { epoch++ }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.onboard_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.onboard_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OnboardingPolicy.steps(Build.VERSION.SDK_INT).forEach { step ->
            val granted = snapshot[step] == true
            PermissionRow(
                title = stringResource(titleRes(step)),
                body = stringResource(bodyRes(step)),
                granted = granted,
                onGrant = {
                    when (step) {
                        OnboardingStep.Notifications ->
                            if (Build.VERSION.SDK_INT >= 33) {
                                notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        OnboardingStep.Location -> locLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                            ),
                        )
                        else -> OnboardingIntents.openSpecialAccess(context, step)
                    }
                },
            )
        }
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.onboard_continue))
        }
    }
}

@Composable
private fun PermissionRow(title: String, body: String, granted: Boolean, onGrant: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
        Text(text = title, fontWeight = FontWeight.SemiBold)
        Text(text = body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            if (granted) {
                Text(
                    text = stringResource(R.string.onboard_granted),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics { contentDescription = title },
                )
            } else {
                FilledTonalButton(onClick = onGrant) {
                    Text(stringResource(R.string.onboard_grant))
                }
            }
        }
    }
}

private fun titleRes(step: OnboardingStep): Int = when (step) {
    OnboardingStep.Notifications -> R.string.onboard_notifications_title
    OnboardingStep.Location -> R.string.onboard_location_title
    OnboardingStep.ExactAlarms -> R.string.onboard_exact_title
    OnboardingStep.FullScreenIntent -> R.string.onboard_fsi_title
    OnboardingStep.Battery -> R.string.onboard_battery_title
}

private fun bodyRes(step: OnboardingStep): Int = when (step) {
    OnboardingStep.Notifications -> R.string.onboard_notifications_body
    OnboardingStep.Location -> R.string.onboard_location_body
    OnboardingStep.ExactAlarms -> R.string.onboard_exact_body
    OnboardingStep.FullScreenIntent -> R.string.onboard_fsi_body
    OnboardingStep.Battery -> R.string.onboard_battery_body
}
