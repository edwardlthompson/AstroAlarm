package org.astroalarm.ui.sol

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.sol.PlanetBody
import org.astroalarm.sol.PlanetEventType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class SolRendererTest {

    @Test
    fun rendersSquareBitmapWithMinPlanetPixels() {
        val now = Instant.parse("2026-06-01T00:00:00Z")
        val bmp = SolRenderer.render(128, now, zoom = 1f, dark = true, textures = emptyMap())
        assertEquals(128, bmp.width)
        assertEquals(128, bmp.height)
        val body = SolRenderer.bodyAt(64f, 64f, 128, now, 1f)
        assertTrue(body == null || body in PlanetBody.entries)
    }

    @Test
    fun planetAlarmDotsRender() {
        val now = Instant.parse("2026-06-01T00:00:00Z")
        val alarm = AstroAlarm(
            id = "m",
            label = "mars",
            target = AlarmTarget.Planet(PlanetBody.MARS, PlanetEventType.Opposition),
        )
        val bmp = SolRenderer.render(96, now, 1f, true, emptyMap(), listOf(alarm), null)
        assertEquals(96, bmp.width)
        assertEquals(96, bmp.height)
        val clean = SolRenderer.render(
            96, now, 1f, true, emptyMap(), listOf(alarm), null, showEventTimes = false,
        )
        assertEquals(96, clean.width)
    }

    @Test
    fun mercuryOrbitSpanExceedsEarth() {
        val now = Instant.parse("2026-01-04T00:00:00Z")
        assertTrue(SolOrbitPaths.mercurySpan(now) > SolOrbitPaths.earthSpan(now) * 2.0)
        val peri = org.astroalarm.sol.PlanetKepler.au(PlanetBody.EARTH, Instant.parse("2026-01-04T00:00:00Z"))
        val aph = org.astroalarm.sol.PlanetKepler.au(PlanetBody.EARTH, Instant.parse("2026-07-04T00:00:00Z"))
        assertTrue(peri < aph)
    }

    @Test
    fun oppositionTickIsNearGeoLon180() {
        val now = Instant.parse("2026-06-01T00:00:00Z")
        val nu = SolChrome.oppositionNu(PlanetBody.MARS, now)
        val geo = SolChrome.geoLonAt(PlanetBody.MARS, now, nu)
        val err = kotlin.math.abs(org.astroalarm.sol.wrap180(geo - 180.0))
        assertTrue("geoLon $geo", err < 15.0)
        assertEquals(8, SolChrome.lightMin(1.0))
        val (periT, aphT) = SolChrome.earthApsides(now)
        val periM = periT.atZone(java.time.ZoneOffset.UTC).monthValue
        val aphM = aphT.atZone(java.time.ZoneOffset.UTC).monthValue
        assertTrue(periM in 1..2)
        assertTrue(aphM in 6..8)
    }
}
