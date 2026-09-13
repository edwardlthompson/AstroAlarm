package org.astroalarm.ui.onboard

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/** Non-cancelable dialog that owns the screen until alarm permissions are granted. */
@Composable
fun PermissionNagDialog(
    onComplete: () -> Unit,
) {
    BackHandler(enabled = true) { /* block back until complete */ }
    Dialog(
        onDismissRequest = { /* non-cancelable */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true,
        ),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            OnboardingScreen(
                onDone = onComplete,
                requireAllGranted = true,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
