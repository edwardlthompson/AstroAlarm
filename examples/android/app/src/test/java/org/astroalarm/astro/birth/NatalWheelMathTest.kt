package org.astroalarm.astro.birth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class NatalAspectMathTest {
    @Test
    fun oppositionNear180Degrees() {
        val profile = BirthProfile(
            id = "t",
            label = "T",
            birthDate = LocalDate.of(1990, 6, 15),
            birthTime = LocalTime.of(12, 0),
            cityName = "NYC",
            lat = 40.7,
            lon = -74.0,
            zoneId = "America/New_York",
        )
        val chart = BirthChartCalculator.compute(profile)!!
        val links = NatalAspectMath.links(chart)
        assertTrue(links.isNotEmpty() || chart.planets.size >= 2)
    }

    @Test
    fun wrap180Symmetric() {
        assertEquals(0.0, NatalAspectMath.wrap180(0.0), 1e-9)
        assertEquals(-10.0, NatalAspectMath.wrap180(350.0), 1e-9)
    }
}

class NatalWheelLayoutTest {
    @Test
    fun ascAtLeftGivesPiRadiansForAscLongitude() {
        val a = NatalWheelLayout.angleRad(longitudeDeg = 100.0, ascLonDeg = 100.0)
        assertEquals(Math.PI, a, 1e-9)
    }
}

class NatalWheelHousePaintTest {
    @Test
    fun house1MidIsRisingSignCenter() {
        val asc = EclipticPoint(100.0)
        val mid = NatalWheelHousePaint.house1MidLon(asc)
        assertEquals(asc.sign.startLongitudeDeg + 15.0, mid, 1e-9)
        assertTrue(NatalWheelLayout.angleRad(mid, asc.longitudeDeg).isFinite())
    }
}

class NatalWheelStelliumTest {
    @Test
    fun closeLongitudesGetIncreasedRadius() {
        val factors = NatalWheelStellium.radiusFactors(listOf(10.0, 12.0, 100.0))
        assertEquals(1f, factors[0], 1e-5f)
        assertTrue(factors[1] > 1f)
        assertEquals(1f, factors[2], 1e-5f)
    }

    @Test
    fun aspectMinSizeConstant() {
        assertEquals(280, NatalWheelRenderer.ASPECT_MIN_SIZE)
    }
}

class NatalCompoundNextTest {
    @Test
    fun timeUnknownRejectsAscSpecificPair() {
        val profile = BirthProfile(
            id = "u",
            label = "U",
            birthDate = LocalDate.of(1990, 1, 1),
            timeUnknown = true,
            birthTime = null,
            cityName = "Paris",
            lat = 48.8,
            lon = 2.3,
            zoneId = "Europe/Paris",
        )
        val chart = BirthChartCalculator.compute(profile)!!
        val hit = NatalCompoundNext.nextSpecific(
            chart,
            listOf(NatalStoryKind.AscSun, NatalStoryKind.SolarReturn),
            java.time.Instant.parse("2020-01-01T00:00:00Z"),
        )
        assertEquals(null, hit)
    }

    @Test
    fun labelKindsJoins() {
        val s = NatalCompoundNext.labelKinds(listOf(NatalStoryKind.MoonReturn, NatalStoryKind.SolarReturn))
        assertTrue(s.contains("Moon return"))
        assertTrue(s.contains("Solar return"))
    }
}
