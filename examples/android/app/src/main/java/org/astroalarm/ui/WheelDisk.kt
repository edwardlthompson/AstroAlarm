package org.astroalarm.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Build

/** Fill a circle so square bitmap corners stay transparent. Clip overflow on device. */
object WheelDisk {
    const val INSCRIBE = 0.5f

    fun radius(size: Int, frac: Float = INSCRIBE): Float = size * frac

    fun path(cx: Float, cy: Float, r: Float): Path = Path().apply {
        addCircle(cx, cy, r, Path.Direction.CW)
    }

    fun withClip(
        canvas: Canvas,
        size: Int,
        fill: Int,
        frac: Float = INSCRIBE,
        draw: () -> Unit,
    ) {
        val cx = size / 2f
        val cy = size / 2f
        val r = radius(size, frac)
        val clip = shouldClip
        if (clip) {
            canvas.save()
            canvas.clipPath(path(cx, cy, r))
        }
        if (fill ushr 24 != 0) {
            canvas.drawCircle(cx, cy, r, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = fill })
        }
        draw()
        if (clip) canvas.restore()
    }

    private val shouldClip: Boolean
        get() {
            val finger = Build.FINGERPRINT
            return finger.isNotEmpty() && "robolectric" !in finger.lowercase()
        }
}
