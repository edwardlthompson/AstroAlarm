package org.astroalarm.solarterm

/** Packed ARGB ints, same layout as android.graphics.Color, so JVM tests need no Robolectric. */
object SolarTermPalette {
    fun displaySeason(season: SolarSeason, southern: Boolean): SolarSeason {
        if (!southern) return season
        return when (season) {
            SolarSeason.SPRING -> SolarSeason.AUTUMN
            SolarSeason.SUMMER -> SolarSeason.WINTER
            SolarSeason.AUTUMN -> SolarSeason.SPRING
            SolarSeason.WINTER -> SolarSeason.SUMMER
        }
    }

    fun sectorColor(term: SolarTerm, southern: Boolean, dark: Boolean): Int {
        val season = displaySeason(term.season, southern)
        val phase = term.ordinal % 6
        return when (season) {
            SolarSeason.SPRING -> lerpRgb(rgb(46, 160, 120), rgb(90, 200, 170), phase, dark)
            SolarSeason.SUMMER -> lerpRgb(rgb(230, 160, 40), rgb(200, 70, 50), phase, dark)
            SolarSeason.AUTUMN -> lerpRgb(rgb(210, 120, 50), rgb(140, 80, 40), phase, dark)
            SolarSeason.WINTER -> lerpRgb(rgb(90, 140, 190), rgb(180, 200, 220), phase, dark)
        }
    }

    fun ink(dark: Boolean): Int = if (dark) rgb(236, 240, 248) else rgb(28, 32, 40)

    fun muted(dark: Boolean): Int = if (dark) rgb(170, 180, 196) else rgb(70, 78, 90)

    fun wheelBg(dark: Boolean): Int = if (dark) rgb(12, 16, 28) else rgb(246, 242, 232)

    fun hub(dark: Boolean): Int = if (dark) rgb(22, 28, 44) else rgb(255, 252, 244)

    internal fun rgb(r: Int, g: Int, b: Int): Int =
        (0xFF shl 24) or ((r and 0xFF) shl 16) or ((g and 0xFF) shl 8) or (b and 0xFF)

    private fun red(c: Int): Int = (c shr 16) and 0xFF
    private fun green(c: Int): Int = (c shr 8) and 0xFF
    private fun blue(c: Int): Int = c and 0xFF

    private fun lerpRgb(a: Int, b: Int, phase: Int, dark: Boolean): Int {
        val t = phase / 5f
        val r = red(a) + ((red(b) - red(a)) * t).toInt()
        val g = green(a) + ((green(b) - green(a)) * t).toInt()
        val bl = blue(a) + ((blue(b) - blue(a)) * t).toInt()
        val dim = if (dark) 0.82f else 1f
        return rgb(
            (r * dim).toInt().coerceIn(0, 255),
            (g * dim).toInt().coerceIn(0, 255),
            (bl * dim).toInt().coerceIn(0, 255),
        )
    }
}
