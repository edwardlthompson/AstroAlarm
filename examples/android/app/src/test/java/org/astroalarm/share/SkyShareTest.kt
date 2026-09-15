package org.astroalarm.share

import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.ui.solarterm.WheelZoomPan
import org.astroalarm.widget.DiskRingLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.time.Instant
import java.time.ZoneId

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SkyShareTest {
    private val now = Instant.parse("2026-06-21T12:00:00Z")
    private val place = AstroPlace("NYC", 40.7128, -74.0060, ZoneId.of("America/New_York").id)

    @Test
    fun pngMagicFromTinyBitmap() {
        val bmp = Bitmap.createBitmap(SkyShare.TEST_SIZE, SkyShare.TEST_SIZE, Bitmap.Config.ARGB_8888)
        val png = SkyShare.encodePng(bmp)
        assertTrue(SkyShare.isPng(png))
        bmp.recycle()
    }

    @Test
    fun daily2dSizeAndZodiacChangeRingRadii() {
        val on = SkySharePaint.daily2d(
            256, place, emptyList(), now, WheelZoomPan(),
            showZodiac = true, showEventTimes = true,
            showMonthTicks = false, showHourMarks = false, earth = null, moon = null,
        )
        val off = SkySharePaint.daily2d(
            256, place, emptyList(), now, WheelZoomPan(),
            showZodiac = false, showEventTimes = true,
            showMonthTicks = false, showHourMarks = false, earth = null, moon = null,
        )
        assertEquals(256, on.width)
        assertEquals(256, off.width)
        val withZodiac = DiskRingLayout.of(256, months = false, zodiac = true)
        val without = DiskRingLayout.of(256, months = false, zodiac = false)
        assertTrue(withZodiac.noonPointerEndR > without.noonPointerEndR)
        assertEquals(0f, without.noonPointerEndR, 0.01f)
        on.recycle()
        off.recycle()
    }

    @Test
    fun concatZoomIsNotIdentity() {
        val identity = WheelZoomPan()
        val zoomed = WheelZoomPan(scale = 2.5f, offsetX = 12f, offsetY = -8f)
        assertNotEquals(identity.scale, zoomed.scale)
        val bmp = Bitmap.createBitmap(SkyShare.TEST_SIZE, SkyShare.TEST_SIZE, Bitmap.Config.ARGB_8888)
        SkyShare.applyViewport(Canvas(bmp), zoomed, SkyShare.TEST_SIZE)
        bmp.recycle()
    }

    @Test
    fun shareIntentIsPngSend() {
        val intent = SkyShare.shareIntent(Uri.parse("content://dev.foss.goldenpath.fileprovider/shares/a.png"))
        assertEquals("image/png", intent.type)
        assertEquals(android.content.Intent.ACTION_SEND, intent.action)
    }

    @Test
    fun oomFallsBackTo1080() {
        var first = true
        val (bmp, fellBack) = SkyShare.bitmapWithFallback { size ->
            if (size == SkyShare.SIZE && first) {
                first = false
                throw OutOfMemoryError("test")
            }
            Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        }
        assertTrue(fellBack)
        assertEquals(SkyShare.FALLBACK, bmp.width)
        bmp.recycle()
    }

    @Test
    fun purgeRemovesOldShareFiles() {
        val dir = File.createTempFile("shares", "dir").apply {
            delete()
            mkdirs()
        }
        val now = 5_000_000L
        val old = SkyShare.writePng(dir, byteArrayOf(1, 2, 3), nowMs = 1L)
        old.setLastModified(now - SkyShare.MAX_AGE_MS - 1)
        val keep = SkyShare.writePng(dir, byteArrayOf(4, 5, 6), nowMs = 2L)
        keep.setLastModified(now)
        SkyShare.purgeOlderThan(dir, nowMs = now)
        assertFalse(old.exists())
        assertTrue(keep.exists())
        dir.deleteRecursively()
    }
}
