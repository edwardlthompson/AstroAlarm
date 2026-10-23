package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.place.AstroPlace
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class SkyOccasionCatalogTest {
    private val zone = ZoneId.of("UTC")
    private val place = AstroPlace("Test", 40.0, -74.0, zone.id)

    @Test
    fun sunset2040AfterNow() {
        val now = Instant.parse("2026-01-01T00:00:00Z")
        val fire = SkyOccasionCatalog.nextInstant(SkyOccasionId.NAKED_EYE_2040, place, now)
        assertNotNull(fire)
        val z = fire!!.atZone(zone)
        assertEquals(2040, z.year)
        assertEquals(9, z.monthValue)
        assertEquals(8, z.dayOfMonth)
    }

    @Test
    fun fallback1800WithoutPlace() {
        val now = Instant.parse("2026-01-01T00:00:00Z")
        val fire = SkyOccasionCatalog.nextInstant(SkyOccasionId.NAKED_EYE_2040, null, now)!!
        val z = fire.atZone(ZoneId.systemDefault())
        assertEquals(LocalDate.of(2040, 9, 8), z.toLocalDate())
        assertEquals(18, z.hour)
        assertEquals(0, z.minute)
    }

    @Test
    fun noon2492() {
        val now = Instant.parse("2026-01-01T00:00:00Z")
        val fire = SkyOccasionCatalog.nextInstant(SkyOccasionId.ALL_PLANETS_2492, place, now)!!
        val z = fire.atZone(zone)
        assertEquals(2492, z.year)
        assertEquals(5, z.monthValue)
        assertEquals(6, z.dayOfMonth)
        assertEquals(12, z.hour)
    }

    @Test
    fun pastOccasionNull() {
        val now = Instant.parse("2500-01-01T00:00:00Z")
        assertNull(SkyOccasionCatalog.nextInstant(SkyOccasionId.NAKED_EYE_2040, place, now))
    }

    @Test
    fun jsonRoundTripAndUnknownSkipped() {
        val target = AlarmTarget.Occasion(SkyOccasionId.ALL_PLANETS_2492.id)
        val alarm = AstroAlarm(id = "id", label = "lab", target = target)
        val parsed = AstroAlarmJson.fromJson(AstroAlarmJson.toJson(alarm))
        assertEquals(target, parsed!!.target)
        assertTrue(parsed.isOnce)
        assertNull(
            AstroAlarmJson.fromJson(
                JSONObject("""{"id":"x","label":"y","target":{"kind":"occasion","catalogId":"nope"}}"""),
            ),
        )
    }
}
