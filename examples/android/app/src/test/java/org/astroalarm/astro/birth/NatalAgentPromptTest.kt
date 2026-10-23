package org.astroalarm.astro.birth

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

class NatalAgentPromptTest {
    private fun profile(
        timeUnknown: Boolean = false,
        time: LocalTime? = LocalTime.of(14, 30),
    ) = BirthProfile(
        id = "t1",
        label = "NY",
        birthDate = LocalDate.of(1990, 6, 15),
        birthTime = time,
        timeUnknown = timeUnknown,
        cityName = "New York",
        lat = 40.7128,
        lon = -74.0060,
        zoneId = "America/New_York",
    )

    private fun completeChart(): NatalChart {
        val planets = linkedMapOf(
            NatalBody.SUN to EclipticPoint(84.5),
            NatalBody.MOON to EclipticPoint(200.0),
            NatalBody.MERCURY to EclipticPoint(70.0),
            NatalBody.VENUS to EclipticPoint(50.0),
            NatalBody.MARS to EclipticPoint(120.0),
            NatalBody.JUPITER to EclipticPoint(240.0),
            NatalBody.SATURN to EclipticPoint(300.0),
            NatalBody.URANUS to EclipticPoint(10.0),
            NatalBody.NEPTUNE to EclipticPoint(330.0),
        )
        return NatalChart(
            instant = Instant.parse("1990-06-15T18:30:00Z"),
            ascendant = EclipticPoint(15.0),
            midheaven = EclipticPoint(280.0),
            sun = planets[NatalBody.SUN]!!,
            moon = planets[NatalBody.MOON]!!,
            planets = planets,
            approximate = false,
            timeUnknown = false,
        )
    }

    @Test
    fun buildIsDeterministicAndAsksPersonality() {
        val p = profile()
        val chart = completeChart()
        val a = NatalAgentPrompt.build(p, chart)
        val b = NatalAgentPrompt.build(p, chart)
        assertNotNull(a)
        assertTrue(a == b)
        assertTrue(a!!.contains("personality profile"))
        assertTrue(a.contains("| Body | Sign | Degree | House |"))
        assertTrue(a.contains("Sun"))
        assertTrue(a.contains("Neptune"))
        assertTrue(a.contains("New York"))
        assertTrue(NatalAgentPrompt.canCopy(p, chart))
    }

    @Test
    fun incompleteChartDoesNotCopy() {
        val p = profile(timeUnknown = true, time = null)
        val incomplete = completeChart().copy(ascendant = null, timeUnknown = true)
        assertNull(NatalAgentPrompt.build(p, incomplete))
        assertFalse(NatalAgentPrompt.canCopy(p, incomplete))
        assertFalse(NatalAgentPrompt.canCopy(null, null))
    }
}
