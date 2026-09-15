package org.astroalarm.ui

import org.astroalarm.astro.settings.DailyChip
import org.astroalarm.astro.settings.SkyChip
import org.junit.Assert.assertEquals
import org.junit.Test

class HubSegmentTest {
    @Test
    fun dailyStartsAt2d() {
        assertEquals(0, HubSegment.dailyIndex(DailyChip.TwoD))
        assertEquals(1, HubSegment.dailyIndex(DailyChip.ThreeD))
    }

    @Test
    fun skyChartIsLast() {
        assertEquals(HubSegment.sky.last(), SkyChip.Chart)
        assertEquals(2, HubSegment.skyIndex(SkyChip.Chart))
    }
}
