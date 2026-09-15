package org.astroalarm.share

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import org.astroalarm.ui.solarterm.WheelZoomPan
import org.astroalarm.ui.solarterm.WheelZoomPanMath
import java.io.ByteArrayOutputStream
import java.io.File

object SkyShare {
    const val SIZE = 2160
    const val FALLBACK = 1080
    const val TEST_SIZE = 64
    const val DIR = "shares"
    const val MAX_AGE_MS = 3_600_000L

    fun bitmapWithFallback(
        primary: Int = SIZE,
        fallback: Int = FALLBACK,
        draw: (Int) -> Bitmap,
    ): Pair<Bitmap, Boolean> {
        return try {
            draw(primary) to false
        } catch (_: OutOfMemoryError) {
            draw(fallback) to true
        }
    }

    fun applyViewport(canvas: Canvas, viewport: WheelZoomPan, size: Int) {
        WheelZoomPanMath.concat(canvas, viewport, size.toFloat())
    }

    fun encodePng(bitmap: Bitmap): ByteArray {
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        return out.toByteArray()
    }

    fun isPng(bytes: ByteArray): Boolean =
        bytes.size >= 8 &&
            bytes[0] == 0x89.toByte() &&
            bytes[1] == 0x50.toByte() &&
            bytes[2] == 0x4E.toByte() &&
            bytes[3] == 0x47.toByte()

    fun shareIntent(uri: Uri): Intent =
        Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

    fun writePng(dir: File, bytes: ByteArray, nowMs: Long = System.currentTimeMillis()): File {
        dir.mkdirs()
        val file = File(dir, "sky-$nowMs.png")
        file.writeBytes(bytes)
        return file
    }

    fun purgeOlderThan(dir: File, nowMs: Long, maxAgeMs: Long = MAX_AGE_MS) {
        if (!dir.isDirectory) return
        dir.listFiles()?.forEach { file ->
            if (nowMs - file.lastModified() > maxAgeMs) file.delete()
        }
    }

    fun pixelHash(bitmap: Bitmap): Int {
        val n = bitmap.width * bitmap.height
        val px = IntArray(n)
        bitmap.getPixels(px, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        return px.contentHashCode()
    }
}
