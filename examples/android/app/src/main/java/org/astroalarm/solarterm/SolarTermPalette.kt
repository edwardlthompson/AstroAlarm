package org.astroalarm.solarterm

import android.graphics.Color

object SolarTermPalette {
    fun displaySeason(season: SolarSeason, localSeasons: Boolean, southern: Boolean): SolarSeason {
        if (!localSeasons || !southern) return season
        return when (season) {
            SolarSeason.SPRING -> SolarSeason.AUTUMN
            SolarSeason.SUMMER -> SolarSeason.WINTER
            SolarSeason.AUTUMN -> SolarSeason.SPRING
            SolarSeason.WINTER -> SolarSeason.SUMMER
        }
    }

    fun sectorColor(term: SolarTerm, localSeasons: Boolean, southern: Boolean, dark: Boolean): Int {
        val season = displaySeason(term.season, localSeasons, southern)
        val phase = term.ordinal % 6
        return when (season) {
            SolarSeason.SPRING -> lerpRgb(Color.rgb(46, 160, 120), Color.rgb(90, 200, 170), phase, dark)
            SolarSeason.SUMMER -> lerpRgb(Color.rgb(230, 160, 40), Color.rgb(200, 70, 50), phase, dark)
            SolarSeason.AUTUMN -> lerpRgb(Color.rgb(210, 120, 50), Color.rgb(140, 80, 40), phase, dark)
            SolarSeason.WINTER -> lerpRgb(Color.rgb(90, 140, 190), Color.rgb(180, 200, 220), phase, dark)
        }
    }

    fun ink(dark: Boolean): Int = if (dark) Color.rgb(236, 240, 248) else Color.rgb(28, 32, 40)

    fun muted(dark: Boolean): Int = if (dark) Color.rgb(170, 180, 196) else Color.rgb(70, 78, 90)

    fun wheelBg(dark: Boolean): Int = if (dark) Color.rgb(12, 16, 28) else Color.rgb(246, 242, 232)

    fun hub(dark: Boolean): Int = if (dark) Color.rgb(22, 28, 44) else Color.rgb(255, 252, 244)

    private fun lerpRgb(a: Int, b: Int, phase: Int, dark: Boolean): Int {
        val t = phase / 5f
        val r = Color.red(a) + ((Color.red(b) - Color.red(a)) * t).toInt()
        val g = Color.green(a) + ((Color.green(b) - Color.green(a)) * t).toInt()
        val bl = Color.blue(a) + ((Color.blue(b) - Color.blue(a)) * t).toInt()
        val dim = if (dark) 0.82f else 1f
        return Color.rgb(
            (r * dim).toInt().coerceIn(0, 255),
            (g * dim).toInt().coerceIn(0, 255),
            (bl * dim).toInt().coerceIn(0, 255),
        )
    }
}
