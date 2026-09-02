package org.astroalarm.ui.solarterm

import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.solarterm.SolarTerm
import org.astroalarm.solarterm.SolarTermLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.time.Instant
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class SolarTermWheelRenderTest {

    @Test
    fun topHitIsLichunEvenInJuneAndEarthTravels() {
        val ctx = RuntimeEnvironment.getApplication()
        val place = AstroPlace("Wellington", -41.2865, 174.7762, "Pacific/Auckland")
        val now = Instant.parse("2026-06-21T00:00:00Z")
        val (_, req) = SolarTermDrawFactory.request(
            ctx.resources, place, now, dark = true, compact = true,
        )
        val wheel = SolarTermWheelRenderer.render(req, 96)
        assertEquals(96, wheel.width)
        assertEquals(96, wheel.height)
        val midR = (SolarTermWheelRenderer.innerFrac(true) + SolarTermWheelRenderer.outerFrac()) / 2f * 96f
        val idx = SolarTermWheelRenderer.sectorAt(48f, 48f - midR, 96, req.nowLon, compact = true)
        assertNotNull(idx)
        assertEquals(SolarTerm.LICHUN.ordinal, idx)
        assertEquals(SolarTermLayout.canvasDeg(req.nowLon), SolarTermHubRenderer.earthCanvasDeg(req.nowLon, 0f), 0.5f)
        assertTrue(abs(SolarTermHubRenderer.earthCanvasDeg(req.nowLon, 0f) + 90f) > 1f)
        assertFalse(req.whenLocal.any { Regex("""\d{4}""").containsMatchIn(it) })
    }

    @Test
    fun lichunSitsAtTopWhenLongitudeIs315() {
        val idx = SolarTermWheelRenderer.sectorAt(48f, 12f, 96, 315.0)
        assertEquals(SolarTerm.LICHUN.ordinal, idx)
    }

    @Test
    fun yushuiIsCounterClockwiseNearEleven() {
        val ang = Math.toRadians(-112.5)
        val x = (48.0 + 40.0 * cos(ang)).toFloat()
        val y = (48.0 + 40.0 * sin(ang)).toFloat()
        val idx = SolarTermWheelRenderer.sectorAt(x, y, 96, 315.0)
        assertEquals(SolarTerm.YUSHUI.ordinal, idx)
    }

    @Test
    fun noPlaceYearlyStillRenders() {
        val ctx = RuntimeEnvironment.getApplication()
        val now = Instant.parse("2026-01-10T00:00:00Z")
        val (_, req) = SolarTermDrawFactory.request(
            ctx.resources, null, now, dark = false, compact = false,
        )
        val wheel = SolarTermWheelRenderer.render(req, 64)
        assertEquals(64, wheel.width)
        assertEquals(64, wheel.height)
    }

    @Test
    fun seasonalAlarmDotsRender() {
        val ctx = RuntimeEnvironment.getApplication()
        val now = Instant.parse("2026-01-10T00:00:00Z")
        val (_, req) = SolarTermDrawFactory.request(
            ctx.resources, null, now, dark = false, compact = true,
            alarmOrds = setOf(SolarTerm.LICHUN.ordinal),
        )
        val wheel = SolarTermWheelRenderer.render(req, 80)
        assertEquals(80, wheel.width)
    }

    @Config(sdk = [26], qualifiers = "zh")
    @Test
    fun zhPackUsesSimplifiedHans() {
        val ctx = RuntimeEnvironment.getApplication()
        assertEquals("惊蛰", SolarTermCopy.name(ctx.resources, SolarTerm.JINGZHE))
    }

    @Test
    fun compactEmojiSitsInBand() {
        val inner = SolarTermWheelRenderer.innerFrac(true)
        val outer = SolarTermWheelRenderer.outerFrac()
        val emoji = (inner + outer) / 2f
        assertTrue(emoji > inner && emoji < outer)
        assertTrue(inner > 0.40f)
    }

    @Test
    fun jieqiNameAndDateSitOnOppositeInsetEdges() {
        val name = SolarTermRadialLabels.nameAng(0)
        val date = SolarTermRadialLabels.dateAng(0)
        assertTrue(name != date)
        val mid0 = SolarTermWheelRenderer.midDeg(0)
        val mid23 = SolarTermWheelRenderer.midDeg(23)
        assertTrue(
            SolarTermRadialLabels.angDist(name, mid0) < SolarTermRadialLabels.angDist(name, mid23)
        )
        assertTrue(SolarTermRadialLabels.NAME_FRAC > 0.018f)
        assertTrue(SolarTermRadialLabels.DATE_FRAC > 0.015f)
        assertTrue(SolarTermRadialLabels.DATE_FRAC >= 0.018f)
    }

    @Test
    fun jieqiNamesWrapInsteadOfCropping() {
        val measure: (String) -> Float = { it.length * 10f }
        val lines = SolarTermRadialLabels.wrapLines("Beginning of Spring", 80f, measure, 3)
        assertTrue(lines.size >= 2)
        assertTrue(lines.all { measure(it) <= 80f })
        assertTrue(lines.any { it.contains("Spring") })
        val short = SolarTermRadialLabels.wrapLines("Rain", 80f, measure, 3)
        assertEquals(listOf("Rain"), short)
    }

    @Test
    fun fullMoonSitsAntiSunwardOnYearlyHub() {
        val now = Instant.parse("2023-08-02T01:23:23Z")
        val sun = org.astroalarm.astro.sun.SolarSeasons.apparentLon(now)
        val moon = org.astroalarm.astro.moon.LunarCalculator.eclipticLon(now)
        val earth = SolarTermHubRenderer.earthCanvasDeg(sun, 0f)
        val moonAng = SolarTermHubRenderer.moonAroundEarthDeg(moon, 0f)
        assertTrue(org.astroalarm.astro.moon.LunarCalculator.elongationDeg(now) > 150.0)
        assertTrue(SolarTermRadialLabels.angDist(moonAng, earth) < 25f)
        val newMoon = org.astroalarm.astro.moon.LunarCalculator.calculate(
            org.astroalarm.astro.model.LunarEventType.NewMoon,
            java.time.LocalDate.of(2026, 8, 1), 0.0, 0.0, java.time.ZoneOffset.UTC,
        )!!
        val nSun = org.astroalarm.astro.sun.SolarSeasons.apparentLon(newMoon)
        val nMoon = org.astroalarm.astro.moon.LunarCalculator.eclipticLon(newMoon)
        val nEarth = SolarTermHubRenderer.earthCanvasDeg(nSun, 0f)
        val nAng = SolarTermHubRenderer.moonAroundEarthDeg(nMoon, 0f)
        assertTrue(SolarTermRadialLabels.angDist(nAng, nEarth + 180f) < 25f)
    }
}
