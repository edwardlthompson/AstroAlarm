package org.astroalarm.wear

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class WearCivilAnglesTest {
    private val zone = ZoneId.of("UTC")

    @Test
    fun civilAngleAtMidnightIsZero() {
        val zdt = LocalDateTime.of(2026, 6, 21, 0, 0).atZone(zone)
        assertEquals(0f, WearCivilAngles.civilAngle(zdt), 0.01f)
    }

    @Test
    fun civilAngleAtNoonIsHalfTurn() {
        val zdt = LocalDateTime.of(2026, 6, 21, 12, 0).atZone(zone)
        assertEquals(180f, WearCivilAngles.civilAngle(zdt), 0.01f)
    }

    @Test
    fun nowUpHandStaysAtTop() {
        assertEquals(-90f, WearCivilAngles.nowUpHandDeg(), 0.001f)
    }

    @Test
    fun screenDegPutsNowAtTop() {
        val now = LocalDateTime.of(2026, 6, 21, 15, 0).atZone(zone)
        val nowAngle = WearCivilAngles.civilAngle(now)
        assertEquals(-90f, WearCivilAngles.screenDeg(nowAngle, nowAngle), 0.01f)
    }
}
