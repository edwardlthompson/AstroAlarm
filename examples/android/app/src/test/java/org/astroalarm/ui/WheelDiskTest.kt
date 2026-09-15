package org.astroalarm.ui

import org.astroalarm.astro.birth.BirthChartCalculator
import org.astroalarm.astro.birth.BirthProfile
import org.astroalarm.astro.birth.NatalWheelRenderer
import org.astroalarm.ui.sol.SolRenderer
import org.astroalarm.ui.solarterm.SolarTermDrawFactory
import org.astroalarm.ui.solarterm.SolarTermWheelRenderer
import org.astroalarm.widget.Astro3DRenderer
import org.astroalarm.widget.AstroDiskRenderer
import org.astroalarm.widget.NatalChartWidgetEmpty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class WheelDiskTest {

    @Test
    fun inscribedRadiusIsHalfSize() {
        assertEquals(32f, WheelDisk.radius(64), 0.01f)
        assertEquals(30.72f, WheelDisk.radius(64, 0.48f), 0.01f)
    }

    @Test
    fun yearlySolNatalAndDailyStillRenderSquareArgb() {
        val ctx = RuntimeEnvironment.getApplication()
        val (_, req) = SolarTermDrawFactory.request(
            ctx.resources, null, Instant.parse("2026-01-10T00:00:00Z"), dark = true, compact = true,
        )
        val yearly = SolarTermWheelRenderer.render(req, 64)
        assertEquals(64, yearly.width)
        assertTrue(yearly.hasAlpha())
        val sol = SolRenderer.render(48, Instant.parse("2026-06-01T00:00:00Z"), 1f, true, emptyMap())
        assertEquals(48, sol.width)
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
        val natal = NatalWheelRenderer.renderBitmap(40, BirthChartCalculator.compute(profile)!!)
        assertEquals(40, natal.width)
        val now = Instant.parse("2026-06-01T00:00:00Z")
        assertEquals(32, AstroDiskRenderer.renderDisk(null, emptyList(), now, 32).width)
        assertEquals(32, Astro3DRenderer.render3D(null, emptyList(), now, 32).width)
        assertEquals(32, NatalChartWidgetEmpty.bitmap(32, true, "set").width)
    }

    @Test
    fun wheelRenderersDoNotFillTheSquare() {
        val names = listOf(
            "ui/solarterm/SolarTermWheelRenderer.kt",
            "ui/sol/SolRenderer.kt",
            "astro/birth/NatalWheelRenderer.kt",
            "widget/NatalChartWidgetEmpty.kt",
            "widget/Astro3DRenderer.kt",
        )
        names.forEach { rel ->
            val text = source(rel).readText()
            assertFalse("$rel still drawColor", text.contains("drawColor"))
            assertFalse("$rel still drawRect 0,0", text.contains("drawRect(0f"))
        }
    }

    private fun source(rel: String): File {
        val candidates = listOf(
            File("app/src/main/java/org/astroalarm/$rel"),
            File("src/main/java/org/astroalarm/$rel"),
        )
        return candidates.first { it.isFile }
    }
}
