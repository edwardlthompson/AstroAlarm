package org.astroalarm.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.foss.goldenpath.R

@Composable
fun SunriseOfferDialog(
    timeHm: String,
    onAdd: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.sunrise_offer_title, timeHm)) },
        confirmButton = {
            TextButton(onClick = onAdd) {
                Text(stringResource(R.string.astro_action_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.sunrise_offer_not_now))
            }
        },
    )
}
