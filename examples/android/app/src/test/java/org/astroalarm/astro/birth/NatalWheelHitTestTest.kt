package org.astroalarm.astro.birth

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class NatalWheelHitTestTest {
    private fun knownTimeChart(): NatalChart {
        val profile = BirthProfile(
            id = "hit",
            label = "Hit",
            birthDate = LocalDate.of(1990, 6, 15),
            birthTime = LocalTime.of(12, 0),
            cityName = "NYC",
            lat = 40.7,
            lon = -74.0,
            zoneId = "America/New_York",
        )
        return BirthChartCalculator.compute(profile)!!
    }

    @Test
    fun tapNearAscSpokeResolvesAsc() {
        val chart = knownTimeChart()
        val size = 400
        val cx = size / 2f
        val cy = size / 2f
        val r = size * 0.42f
        val asc = NatalWheelLayout.frameAscLon(chart)
        val (px, py) = NatalWheelLayout.xy(cx, cy, r, asc, asc)
        assertEquals(NatalWheelHit.Asc, NatalWheelHitTest.at(px, py, size, chart))
    }

    @Test
    fun tapNearMcSpokeResolvesMc() {
        val chart = knownTimeChart()
        val mc = chart.midheaven!!.longitudeDeg
        val size = 400
        val cx = size / 2f
        val cy = size / 2f
        val r = size * 0.42f
        val asc = NatalWheelLayout.frameAscLon(chart)
        val (px, py) = NatalWheelLayout.xy(cx, cy, r, mc, asc)
        assertEquals(NatalWheelHit.Mc, NatalWheelHitTest.at(px, py, size, chart))
    }

    @Test
    fun tapNearDscSpokeResolvesDsc() {
        val chart = knownTimeChart()
        val asc = NatalWheelLayout.frameAscLon(chart)
        val dsc = NatalAspectMath.wrap360(asc + 180.0)
        val size = 400
        val cx = size / 2f
        val cy = size / 2f
        val r = size * 0.42f
        val (px, py) = NatalWheelLayout.xy(cx, cy, r, dsc, asc)
        assertEquals(NatalWheelHit.Dsc, NatalWheelHitTest.at(px, py, size, chart))
    }

    @Test
    fun tapNearIcSpokeResolvesIc() {
        val chart = knownTimeChart()
        val mc = chart.midheaven!!.longitudeDeg
        val ic = NatalAspectMath.wrap360(mc + 180.0)
        val size = 400
        val cx = size / 2f
        val cy = size / 2f
        val r = size * 0.42f
        val asc = NatalWheelLayout.frameAscLon(chart)
        val (px, py) = NatalWheelLayout.xy(cx, cy, r, ic, asc)
        assertEquals(NatalWheelHit.Ic, NatalWheelHitTest.at(px, py, size, chart))
    }

    @Test
    fun tapNearNatalSunResolvesPlanet() {
        val chart = knownTimeChart()
        val sun = chart.planets[NatalBody.SUN]!!.longitudeDeg
        val size = 400
        val cx = size / 2f
        val cy = size / 2f
        val rPlanet = size * 0.30f
        val asc = NatalWheelLayout.frameAscLon(chart)
        val (px, py) = NatalWheelLayout.xy(cx, cy, rPlanet, sun, asc)
        assertEquals(NatalWheelHit.NatalPlanet(NatalBody.SUN), NatalWheelHitTest.at(px, py, size, chart))
    }
}
