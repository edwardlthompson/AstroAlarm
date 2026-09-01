package org.astroalarm.widget

import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.sky.BodySky
import org.astroalarm.astro.sky.SkyBodies
import org.astroalarm.astro.sun.SolarCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.abs

class TransitTicksTest {
    private val zone: ZoneId = ZoneId.of("America/New_York")
    private val place = AstroPlace("NYC", 40.7128, -74.0060, zone.id)

    @Test
    fun midnightClockAngleIsTopOfEllipse() {
        val instant = ZonedDateTime.of(2026, 6, 21, 0, 0, 0, 0, zone).toInstant()
        assertEquals(-Math.PI / 2.0, TransitTicks.clockAngle(instant, zone), 1e-6)
    }

    @Test
    fun solarNoonTickSitsInLineWithLocationPin() {
        val date = LocalDate.of(2026, 6, 21)
        val noon = SolarCalculator.calculate(
            SolarEventType.SolarNoon, date, place.latitude, place.longitude, place.zone,
        )!!
        val marks = TransitTicks.marks(place, noon)
        assertTrue(marks.sun.isNotEmpty())
        assertTrue(marks.sun.any { abs(it - Math.PI / 2.0) < 0.12 })
        assertEquals(1, marks.moonMeridian.size)
        assertTrue(abs(marks.moonMeridian[0] - Math.PI / 2.0) < 0.25)
    }

    @Test
    fun diskNoonStaysAtNoonMark() {
        assertEquals(-90f, TransitTicks.diskAngleDeg(0.0, -90f), 1e-4f)
    }

    @Test
    fun sunriseTickMatchesSunBodyOnTheRing() {
        val date = LocalDate.of(2026, 6, 21)
        val noon = SolarCalculator.calculate(
            SolarEventType.SolarNoon, date, place.latitude, place.longitude, place.zone,
        )!!
        val sunrise = SolarCalculator.calculate(
            SolarEventType.Sunrise, date, place.latitude, place.longitude, place.zone,
        )!!
        val marks = TransitTicks.marks(place, noon)
        val expected = SkyBodies.sun(sunrise, place.latitude, place.longitude)!!.let {
            BodySky.ringAngle(it.haRad, place.latitude)
        }
        assertTrue(marks.sun.any { abs(it - expected) < 0.05 })
    }

    @Test
    fun missingPlaceYieldsNoTicks() {
        val empty = TransitTicks.marks(null, Instant.EPOCH)
        assertTrue(empty.sun.isEmpty())
        assertTrue(empty.moonHorizon.isEmpty())
        assertTrue(empty.moonMeridian.isEmpty())
    }
}
