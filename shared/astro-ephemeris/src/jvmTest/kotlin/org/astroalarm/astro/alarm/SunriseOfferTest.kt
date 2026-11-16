package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.place.AstroPlace
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class SunriseOfferTest {
    private val nyc = AstroPlace("New York", 40.7128, -74.0060, "America/New_York")
    private val now = Instant.parse("2026-06-15T12:00:00Z")

    @Test
    fun shouldShowWhenPendingFirstCity() {
        assertTrue(SunriseOffer.shouldShow(nyc, emptyList(), dismissed = false, pending = true))
    }

    @Test
    fun hidesWhenDismissedOrNotPendingOrNoPlace() {
        assertFalse(SunriseOffer.shouldShow(nyc, emptyList(), dismissed = true, pending = true))
        assertFalse(SunriseOffer.shouldShow(nyc, emptyList(), dismissed = false, pending = false))
        assertFalse(SunriseOffer.shouldShow(null, emptyList(), dismissed = false, pending = true))
    }

    @Test
    fun hidesWhenSunriseAlarmExists() {
        val existing = AstroAlarm("1", "Sunrise", target = AlarmTarget.Solar(SolarEventType.Sunrise, 0))
        assertTrue(SunriseOffer.hasSunriseAlarm(listOf(existing)))
        assertFalse(SunriseOffer.shouldShow(nyc, listOf(existing), dismissed = false, pending = true))
    }

    @Test
    fun nextFireAndAlarm() {
        val fire = SunriseOffer.nextFire(nyc, now)
        assertNotNull(fire)
        val hm = SunriseOffer.formatHm(fire!!, ZoneId.of("America/New_York"))
        assertTrue(hm.contains(":"))
        val alarm = SunriseOffer.alarm("Sunrise", id = "x")
        assertEquals(SunriseOffer.target, alarm.target)
        assertTrue(alarm.enabled)
        val zdt = ZonedDateTime.ofInstant(fire, ZoneId.of("America/New_York"))
        assertTrue(zdt.hour in 4..7)
    }
}
