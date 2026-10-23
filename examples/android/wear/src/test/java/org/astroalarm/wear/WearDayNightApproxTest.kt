package org.astroalarm.wear

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class WearDayNightApproxTest {
    private val zone = ZoneId.of("America/New_York")

    @Test
    fun summerDayLongerThanWinter() {
        val summer = WearDayNightApprox.riseSetMinutes(LocalDate.of(2026, 6, 21), zone)
        val winter = WearDayNightApprox.riseSetMinutes(LocalDate.of(2026, 12, 21), zone)
        val summerLen = summer.setMinutes - summer.riseMinutes
        val winterLen = winter.setMinutes - winter.riseMinutes
        assertTrue(summerLen > winterLen)
        assertTrue(summer.riseMinutes < summer.setMinutes)
    }

    @Test
    fun civilAnglesMatchMinutes() {
        val date = LocalDate.of(2026, 3, 20)
        val rs = WearDayNightApprox.riseSetMinutes(date, zone)
        val (rise, set) = WearDayNightApprox.riseSetCivilAngles(date, zone)
        assertEquals(WearDayNightApprox.minutesToCivil(rs.riseMinutes), rise, 0.01f)
        assertEquals(WearDayNightApprox.minutesToCivil(rs.setMinutes), set, 0.01f)
    }
}
