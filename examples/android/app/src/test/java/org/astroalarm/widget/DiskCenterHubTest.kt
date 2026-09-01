package org.astroalarm.widget

import android.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class DiskCenterHubTest {

    @Test
    fun hubRadiusStaysInPlainCapRange() {
        assertEquals(3.5f, DiskCenterHub.radius(10), 0.01f)
        assertEquals(9f, DiskCenterHub.radius(2000), 0.01f)
        assertEquals(4.4f, DiskCenterHub.radius(200), 0.01f)
    }

    @Test
    fun twoDDiskPaintsPlainHubAtCenter() {
        val now = LocalDateTime.of(2026, 8, 31, 12, 0).toInstant(ZoneOffset.UTC)
        val bmp = AstroDiskRenderer.renderDisk(
            place = null,
            alarms = emptyList(),
            now = now,
            size = 200,
            showZodiac = false,
        )
        val cx = bmp.width / 2
        val cy = bmp.height / 2
        assertEquals(DiskCenterHub.COLOR, bmp.getPixel(cx, cy))
        val hubR = DiskCenterHub.radius(200)
        val inside = bmp.getPixel(cx + (hubR * 0.3f).toInt(), cy)
        assertEquals(DiskCenterHub.COLOR, inside)
        val outside = bmp.getPixel(cx + hubR.toInt() + 8, cy)
        assertTrue(outside != DiskCenterHub.COLOR)
        assertTrue(Color.alpha(outside) > 0)
    }
}
