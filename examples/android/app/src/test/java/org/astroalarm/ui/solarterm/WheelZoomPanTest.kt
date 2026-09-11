package org.astroalarm.ui.solarterm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WheelZoomPanTest {

    @Test
    fun zoomOutToMinResetsOffset() {
        val zoomed = WheelZoomPan(scale = 2f, offsetX = 40f, offsetY = -20f)
        val next = WheelZoomPanMath.apply(
            zoomed, centroidX = 100f, centroidY = 100f,
            panX = 0f, panY = 0f, zoom = 0.4f,
            width = 200f, height = 200f,
        )
        assertEquals(1f, next.scale, 1e-4f)
        assertEquals(0f, next.offsetX, 1e-4f)
        assertEquals(0f, next.offsetY, 1e-4f)
    }

    @Test
    fun zoomTowardCornerShiftsOffsetAwayFromCenter() {
        val next = WheelZoomPanMath.apply(
            WheelZoomPan(),
            centroidX = 180f, centroidY = 20f,
            panX = 0f, panY = 0f, zoom = 2f,
            width = 200f, height = 200f,
        )
        assertEquals(2f, next.scale, 1e-4f)
        assertTrue(next.offsetX < 0f)
        assertTrue(next.offsetY > 0f)
    }

    @Test
    fun panMovesOffsetAndStaysClamped() {
        val zoomed = WheelZoomPan(scale = 2f)
        val next = WheelZoomPanMath.apply(
            zoomed, centroidX = 100f, centroidY = 100f,
            panX = 50f, panY = -30f, zoom = 1f,
            width = 200f, height = 200f,
        )
        assertEquals(2f, next.scale, 1e-4f)
        assertEquals(50f, next.offsetX, 1e-3f)
        assertEquals(-30f, next.offsetY, 1e-3f)

        val over = WheelZoomPanMath.apply(
            zoomed, centroidX = 100f, centroidY = 100f,
            panX = 200f, panY = 200f, zoom = 1f,
            width = 200f, height = 200f,
        )
        assertEquals(100f, over.offsetX, 1e-3f)
        assertEquals(100f, over.offsetY, 1e-3f)
    }

    @Test
    fun contentPointInvertsGraphicsLayer() {
        val vp = WheelZoomPan(scale = 2f, offsetX = 10f, offsetY = -20f)
        val (lx, ly) = WheelZoomPanMath.contentPoint(110f, 80f, vp, 200f, 200f)
        // (tap - center - offset) / scale + center
        assertEquals(100f, lx, 1e-3f)
        assertEquals(100f, ly, 1e-3f)
    }

    @Test
    fun mapContentToViewInvertsContentPoint() {
        val vp = WheelZoomPan(scale = 2.5f, offsetX = -30f, offsetY = 16f)
        val (sx, sy) = WheelZoomPanMath.mapContentToView(40f, 160f, vp, 200f, 200f)
        val (lx, ly) = WheelZoomPanMath.contentPoint(sx, sy, vp, 200f, 200f)
        assertEquals(40f, lx, 1e-3f)
        assertEquals(160f, ly, 1e-3f)
    }
}
