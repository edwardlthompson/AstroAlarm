package org.astroalarm.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MoonShadeTest {
    @Test
    fun earthViewFullLightsDiskCenter() {
        val cam = MoonShade.earthView(180.0)
        assertEquals(0.0, cam.subsolarLon, 1e-4)
        assertTrue(GlobeIllumination.dayFactor(0.0, 0.0, cam.sunDec, cam.subsolarLon) > 0.8f)
    }

    @Test
    fun earthViewNewDarkensDiskCenter() {
        val cam = MoonShade.earthView(0.0)
        assertEquals(180.0, kotlin.math.abs(cam.subsolarLon), 1e-4)
        assertTrue(GlobeIllumination.dayFactor(0.0, 0.0, cam.sunDec, cam.subsolarLon) < 0.25f)
    }

    @Test
    fun firstQuarterLightsRightHalf() {
        val cam = MoonShade.earthView(90.0)
        val right = SphereProjection.diskToLatLon(0.8, 0.0, cam.lat0, cam.lon0)!!
        val left = SphereProjection.diskToLatLon(-0.8, 0.0, cam.lat0, cam.lon0)!!
        assertTrue(GlobeIllumination.dayFactor(right.first, right.second, cam.sunDec, cam.subsolarLon) > 0.5f)
        assertTrue(GlobeIllumination.dayFactor(left.first, left.second, cam.sunDec, cam.subsolarLon) < 0.5f)
    }

    @Test
    fun topDownSunwardIsDayIndependentOfElongation() {
        val cam = MoonShade.topDown(45f)
        assertTrue(GlobeIllumination.dayFactor(45.0, 0.0, cam.sunDec, cam.subsolarLon) > 0.5f)
        assertTrue(GlobeIllumination.dayFactor(45.0, 180.0, cam.sunDec, cam.subsolarLon) < 0.5f)
        assertEquals(MoonShade.topDown(0f).subsolarLon, MoonShade.topDown(90f).subsolarLon, 0.0)
    }

    @Test
    fun dailyHubFullMoonOppositeSun() {
        assertEquals(90f, LunarHub.moonDeg(sunDeg = -90f, elongationDeg = 180.0), 1e-3f)
    }
}
