package org.astroalarm.ui.solarterm

import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class SolarTermAxisOverlayTest {

    @Test
    fun nhJuneAxisNorthIsSunward() {
        assertTrue(SolarTermAxisOverlay.northSunwardAlign(90.0) > 0.0)
    }

    @Test
    fun nhDecemberAxisNorthIsAntiSunward() {
        assertTrue(SolarTermAxisOverlay.northSunwardAlign(270.0) < 0.0)
    }

    @Test
    fun northTraceIsFartherAtDecember() {
        val a = 100f
        val amp = 3.5f
        val peri = 0.0
        assertTrue(SolarTermAxisOverlay.spiralR(a, 270.0, peri, amp) > SolarTermAxisOverlay.polar(a, 270.0))
        assertTrue(SolarTermAxisOverlay.spiralR(a, 90.0, peri, amp) < SolarTermAxisOverlay.polar(a, 90.0))
        assertTrue(SolarTermAxisOverlay.spiralR(a, 90.0, peri, -amp) > SolarTermAxisOverlay.polar(a, 90.0))
    }

    @Test
    fun poleLettersClearTheTracesByHalfGlyph() {
        val inner = 200f
        val sz = SolarTermPoleLabels.letterSize(inner)
        listOf(90.0, 270.0).forEach { lon ->
            listOf(true, false).forEach { north ->
                val r = SolarTermPoleLabels.letterR(inner, 0.0, lon, north)
                val trace = SolarTermPoleLabels.traceR(inner, 0.0, lon, north)
                assertTrue(abs(r - trace) > sz * 0.5f)
            }
        }
    }

    @Test
    fun northOutsideGreenInWinterInsideInSummer() {
        val inner = 200f
        val peri = 0.0
        val nDec = SolarTermPoleLabels.letterR(inner, peri, 270.0, north = true)
        val gDec = SolarTermPoleLabels.traceR(inner, peri, 270.0, north = true)
        val nJun = SolarTermPoleLabels.letterR(inner, peri, 90.0, north = true)
        val gJun = SolarTermPoleLabels.traceR(inner, peri, 90.0, north = true)
        val sDec = SolarTermPoleLabels.letterR(inner, peri, 270.0, north = false)
        val rDec = SolarTermPoleLabels.traceR(inner, peri, 270.0, north = false)
        val sJun = SolarTermPoleLabels.letterR(inner, peri, 90.0, north = false)
        val rJun = SolarTermPoleLabels.traceR(inner, peri, 90.0, north = false)
        assertTrue(nDec > gDec)
        assertTrue(nJun < gJun)
        assertTrue(sDec < rDec)
        assertTrue(sJun > rJun)
    }
}
