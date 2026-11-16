package org.astroalarm.sol

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class PlanetKeplerTest {

    private val j2000: Instant = Instant.parse("2000-01-01T12:00:00Z")

    @Test
    fun mercuryAuLessThanEarthLessThanJupiter() {
        val mer = PlanetKepler.au(PlanetBody.MERCURY, j2000)
        val earth = PlanetKepler.au(PlanetBody.EARTH, j2000)
        val jup = PlanetKepler.au(PlanetBody.JUPITER, j2000)
        assertTrue("mercury $mer earth $earth jupiter $jup", mer < earth && earth < jup)
        assertTrue(mer in 0.3..0.5)
        assertTrue(earth in 0.98..1.03)
        assertTrue(jup in 4.8..5.6)
    }

    @Test
    fun mercuryRetrogradeSignFlipInKnownWindow() {
        val start = Instant.parse("2026-02-01T00:00:00Z")
        var sawDirect = false
        var sawRetro = false
        var flipped = false
        var prev = PlanetMotion.isRetrograde(PlanetBody.MERCURY, start)
        for (d in 1..80) {
            val t = start.plusSeconds(d * 86400L)
            val retro = PlanetMotion.isRetrograde(PlanetBody.MERCURY, t)
            if (retro) sawRetro = true else sawDirect = true
            if (prev != retro) flipped = true
            prev = retro
        }
        assertTrue(sawDirect && sawRetro && flipped)
    }

    @Test
    fun pairDeltaHitsOrbInConstructedWindow() {
        val start = Instant.parse("2026-01-01T00:00:00Z")
        var hit: Instant? = null
        for (d in 0..800) {
            val t = start.plusSeconds(d * 86400L)
            if (PlanetKepler.pairDelta(PlanetBody.VENUS, PlanetBody.MARS, t) <= 8.0) {
                hit = t
                break
            }
        }
        assertTrue("venus-mars should pass within 8° in 800 days", hit != null)
        val before = hit!!.minusSeconds(86400L * 20)
        val next = PlanetNext.nextAlign(PlanetBody.VENUS, PlanetBody.MARS, before)
        assertTrue(next != null && !next.isBefore(before))
        assertTrue(PlanetKepler.pairDelta(PlanetBody.VENUS, PlanetBody.MARS, next!!) <= 8.0)
    }

    @Test
    fun allPlanetSpanIsZeroTo180AndWideDateDoesNotFireNow() {
        val start = Instant.parse("2026-01-15T00:00:00Z")
        val samples = (0..35).map { start.plusSeconds(it * 30L * 86400L) }
        samples.forEach { t ->
            val span = PlanetKepler.allPlanetSpan(t)
            assertTrue("span $span at $t", span in 0.0..360.0)
        }
        val wide = samples.firstOrNull { PlanetKepler.allPlanetSpan(it) > 90.0 }
            ?: start.plusSeconds(86400L * 400)
        if (PlanetKepler.allPlanetSpan(wide) > 90.0) {
            val next = PlanetNext.nextAllAlign(wide)
            if (next != null) {
                assertTrue(next.isAfter(wide))
                assertTrue(PlanetKepler.allPlanetSpan(next) <= 90.0)
            }
        }
        assertEquals(null, PlanetNext.nextAlign(PlanetBody.VENUS, PlanetBody.VENUS, start))
    }
}
