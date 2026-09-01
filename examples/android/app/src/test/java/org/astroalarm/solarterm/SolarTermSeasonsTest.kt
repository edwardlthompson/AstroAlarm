package org.astroalarm.solarterm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class SolarTermSeasonsTest {

    @Test
    fun southernLocalSeasonsRemapNamesByHalfYear() {
        assertEquals(SolarTerm.LIQIU, SolarTerm.LICHUN.localAlias(localSeasons = true, southern = true))
        assertEquals(SolarTerm.LICHUN, SolarTerm.LICHUN.localAlias(localSeasons = false, southern = true))
        assertEquals(SolarTerm.LICHUN, SolarTerm.LICHUN.localAlias(localSeasons = true, southern = false))
    }

    @Test
    fun southernLocalSeasonsFlipPaletteSeason() {
        assertEquals(
            SolarSeason.AUTUMN,
            SolarTermPalette.displaySeason(SolarSeason.SPRING, localSeasons = true, southern = true),
        )
        assertEquals(
            SolarSeason.SPRING,
            SolarTermPalette.displaySeason(SolarSeason.SPRING, localSeasons = true, southern = false),
        )
        assertNotEquals(
            SolarTermPalette.sectorColor(SolarTerm.LICHUN, localSeasons = false, southern = false, dark = true),
            SolarTermPalette.sectorColor(SolarTerm.LICHUN, localSeasons = true, southern = true, dark = true),
        )
    }

    @Test
    fun containingLongitudeUsesFifteenDegreeSectors() {
        assertEquals(SolarTerm.LICHUN, SolarTerm.containing(315.0))
        assertEquals(SolarTerm.LICHUN, SolarTerm.containing(329.9))
        assertEquals(SolarTerm.YUSHUI, SolarTerm.containing(330.0))
        assertEquals(SolarTerm.DAHAN, SolarTerm.containing(300.0))
        assertEquals(SolarTerm.DAHAN, SolarTerm.containing(314.9))
        assertEquals(SolarTerm.CHUNFEN, SolarTerm.containing(0.0))
        assertEquals(SolarTerm.CHUNFEN, SolarTerm.containing(359.0 + 1.0))
    }

    @Test
    fun traditionalHanziDiffersForJingzhe() {
        assertEquals("惊蛰", SolarTerm.JINGZHE.hanzi(false))
        assertEquals("驚蟄", SolarTerm.JINGZHE.hanzi(true))
    }
}
