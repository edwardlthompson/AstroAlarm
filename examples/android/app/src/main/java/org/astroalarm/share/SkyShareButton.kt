package org.astroalarm.share

import android.graphics.Bitmap
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.goldenpath.R
import kotlinx.coroutines.launch
import org.astroalarm.widget.AppSnackbar

@Composable
fun SkyShareButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    emptyMessage: String? = null,
    paint: () -> (Int) -> Bitmap,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val chooser = stringResource(R.string.sky_share_chooser)
    val failed = stringResource(R.string.sky_share_failed)
    val oom = stringResource(R.string.sky_share_oom)
    IconButton(
        onClick = {
            if (!enabled) {
                emptyMessage?.let { AppSnackbar.emit(it) }
                return@IconButton
            }
            val draw = paint()
            scope.launch { sharePaintedSky(context, draw, chooser, failed, oom) }
        },
        modifier = modifier.size(48.dp),
    ) {
        Icon(
            Icons.Default.Share,
            contentDescription = stringResource(R.string.sky_share_cd),
        )
    }
}
