package org.astroalarm.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant

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
    fun twoDDiskRendersWithoutEarthTexture() {
        val bmp = AstroDiskRenderer.renderDisk(
            place = null,
            alarms = emptyList(),
            now = Instant.EPOCH,
            size = 64,
            showZodiac = false,
        )
        assertNotNull(bmp)
        assertEquals(64, bmp.width)
        assertEquals(64, bmp.height)
    }
}
