package org.astroalarm.ui.solarterm

/**
 * Pinch-zoom + pan for the Year wheel when gestures run outside [graphicsLayer]
 * (pan units match translationX/Y). Scale pivots on layout center.
 */
data class WheelZoomPan(
    val scale: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
)

object WheelZoomPanMath {
    const val MIN_SCALE = 1f
    const val MAX_SCALE = 3.5f

    fun apply(
        current: WheelZoomPan,
        centroidX: Float,
        centroidY: Float,
        panX: Float,
        panY: Float,
        zoom: Float,
        width: Float,
        height: Float,
    ): WheelZoomPan {
        if (width <= 0f || height <= 0f) return current
        val newScale = (current.scale * zoom).coerceIn(MIN_SCALE, MAX_SCALE)
        if (newScale <= MIN_SCALE + 1e-4f) {
            return WheelZoomPan(MIN_SCALE, 0f, 0f)
        }
        val cx = width / 2f
        val cy = height / 2f
        // Keep the pinch point fixed, then add drag in parent pixels.
        val ox = current.offsetX + (centroidX - cx) * (current.scale - newScale) + panX
        val oy = current.offsetY + (centroidY - cy) * (current.scale - newScale) + panY
        val maxX = cx * (newScale - 1f)
        val maxY = cy * (newScale - 1f)
        return WheelZoomPan(
            scale = newScale,
            offsetX = ox.coerceIn(-maxX, maxX),
            offsetY = oy.coerceIn(-maxY, maxY),
        )
    }

    /** Map a tap in the unscaled viewport back into content/local coordinates. */
    fun contentPoint(
        tapX: Float,
        tapY: Float,
        viewport: WheelZoomPan,
        width: Float,
        height: Float,
    ): Pair<Float, Float> {
        val cx = width / 2f
        val cy = height / 2f
        val lx = (tapX - cx - viewport.offsetX) / viewport.scale + cx
        val ly = (tapY - cy - viewport.offsetY) / viewport.scale + cy
        return lx to ly
    }

    /** Screen point of a content pixel; inverse of [contentPoint]. */
    fun mapContentToView(
        contentX: Float,
        contentY: Float,
        viewport: WheelZoomPan,
        width: Float,
        height: Float,
    ): Pair<Float, Float> {
        val cx = width / 2f
        val cy = height / 2f
        val sx = (contentX - cx) * viewport.scale + cx + viewport.offsetX
        val sy = (contentY - cy) * viewport.scale + cy + viewport.offsetY
        return sx to sy
    }

    /** Same transform as Compose graphicsLayer (scale about center, then translation). */
    fun concat(canvas: android.graphics.Canvas, viewport: WheelZoomPan, size: Float) {
        val mid = size / 2f
        canvas.translate(viewport.offsetX, viewport.offsetY)
        canvas.scale(viewport.scale, viewport.scale, mid, mid)
    }
}
