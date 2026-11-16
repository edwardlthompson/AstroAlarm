package org.astroalarm.ui.solarterm

import org.astroalarm.solarterm.SolarSeason
import org.astroalarm.solarterm.SolarTerm
import org.astroalarm.solarterm.SolarTermPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class SolarTermPaletteSeasonTest {
    @Test
    fun southernFlipPaletteSeason() {
        assertEquals(SolarSeason.AUTUMN, SolarTermPalette.displaySeason(SolarSeason.SPRING, southern = true))
        assertEquals(SolarSeason.SPRING, SolarTermPalette.displaySeason(SolarSeason.SPRING, southern = false))
        assertNotEquals(
            SolarTermPalette.sectorColor(SolarTerm.LICHUN, southern = false, dark = true),
            SolarTermPalette.sectorColor(SolarTerm.LICHUN, southern = true, dark = true),
        )
    }
}
