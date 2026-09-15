package org.astroalarm.share

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.astroalarm.widget.AppSnackbar

suspend fun sharePaintedSky(
    context: Context,
    draw: (Int) -> Bitmap,
    chooser: String,
    failed: String,
    oom: String,
) {
    var bmp: Bitmap? = null
    try {
        val packed = withContext(Dispatchers.Default) {
            SkyShare.bitmapWithFallback(draw = draw)
        }
        bmp = packed.first
        if (packed.second) AppSnackbar.emit(oom)
        val bytes = withContext(Dispatchers.Default) { SkyShare.encodePng(packed.first) }
        val file = withContext(Dispatchers.IO) {
            val dir = File(context.cacheDir, SkyShare.DIR)
            SkyShare.purgeOlderThan(dir, System.currentTimeMillis())
            SkyShare.writePng(dir, bytes)
        }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
        val send = android.content.Intent.createChooser(SkyShare.shareIntent(uri), chooser)
        if (context !is android.app.Activity) {
            send.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(send)
    } catch (_: OutOfMemoryError) {
        AppSnackbar.emit(oom)
    } catch (_: Exception) {
        AppSnackbar.emit(failed)
    } finally {
        bmp?.recycle()
    }
}
