package org.astroalarm.astro.birth

import org.astroalarm.astro.zodiac.ZodiacSign
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WholeSignHousesTest {
    @Test
    fun ariesRisingGivesWholeSignSequence() {
        val cusps = WholeSignHouses.cusps(EclipticPoint(5.0))
        assertEquals(12, cusps.size)
        assertEquals(1, cusps[0].house)
        assertEquals(ZodiacSign.Aries, cusps[0].sign)
        assertEquals(ZodiacSign.Taurus, cusps[1].sign)
        assertEquals(ZodiacSign.Pisces, cusps[11].sign)
    }

    @Test
    fun scorpioRisingHouseOneIsScorpio() {
        val cusps = WholeSignHouses.cusps(EclipticPoint(215.0))
        assertEquals(ZodiacSign.Scorpio, cusps.first().sign)
        assertEquals(ZodiacSign.Libra, cusps.last().sign)
    }

    @Test
    fun bodyInRisingSignIsHouseOne() {
        val asc = EclipticPoint(40.0) // Taurus
        assertEquals(1, WholeSignHouses.houseOf(45.0, asc))
        assertEquals(2, WholeSignHouses.houseOf(70.0, asc)) // Gemini
        assertEquals(12, WholeSignHouses.houseOf(15.0, asc)) // Aries
    }

    @Test
    fun nullAscendantYieldsEmpty() {
        assertTrue(WholeSignHouses.cusps(null).isEmpty())
        assertEquals(null, WholeSignHouses.houseOf(100.0, null))
    }
}
