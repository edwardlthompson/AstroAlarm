package org.astroalarm.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class DiskAngleAnchorTest {
    private val zone = ZoneId.of("UTC")

    @Test
    fun noonUpLocksNoonAtTop() {
        val noon = LocalDateTime.of(2026, 6, 21, 12, 0).atZone(zone).toInstant()
        val now = LocalDateTime.of(2026, 6, 21, 15, 0).atZone(zone).toInstant()
        val anchor = DiskAngleAnchor.anchor(now, noon, zone, solarNoonUp = true)
        val noonDeg = DiskAngleAnchor.screenDeg(DiskAngleAnchor.civilAngle(noon, zone), anchor)
        assertEquals(-90f, noonDeg, 0.01f)
        val hand = DiskAngleAnchor.handDeg(DiskAngleAnchor.civilAngle(now, zone), anchor, true)
        assertTrue(hand > -90f + 1f || hand < -90f - 1f)
        assertEquals(45f - 90f, hand, 0.01f)
    }

    @Test
    fun missingNoonFallsBackToNowUp() {
        val now = Instant.parse("2026-06-21T15:00:00Z")
        val anchor = DiskAngleAnchor.anchor(now, null, zone, solarNoonUp = true)
        assertEquals(DiskAngleAnchor.civilAngle(now, zone), anchor, 0.01f)
        assertEquals(-90f, DiskAngleAnchor.handDeg(anchor, anchor, false), 0.01f)
    }
}
