package org.astroalarm.astro

import org.astroalarm.astro.place.CityCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CityCatalogTest {
    @Test
    fun findsLondonOffline() {
        val hits = CityCatalog.search("lon")
        assertTrue(hits.any { it.cityName == "London" })
        assertEquals("Europe/London", hits.first { it.cityName == "London" }.zoneId)
    }

    @Test
    fun ignoresShortQueries() {
        assertTrue(CityCatalog.search("L").isEmpty())
    }

    @Test
    fun loadsBundledGazetteer() {
        // INVENTORY.md §1.12 said 54; the extracted table has 55 (Amsterdam…Zurich).
        assertEquals(55, CityCatalog.entryCount)
    }
}
