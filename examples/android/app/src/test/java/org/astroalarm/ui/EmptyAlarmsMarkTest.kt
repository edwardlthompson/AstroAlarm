package org.astroalarm.ui

import dev.foss.goldenpath.R
import org.junit.Assert.assertEquals
import org.junit.Test

class EmptyAlarmsMarkTest {
    @Test
    fun usesBrandDiskMark() {
        assertEquals(R.drawable.ic_brand_mark, EmptyAlarmsMark.drawableId)
        assertEquals(96, EmptyAlarmsMark.SIZE_DP)
    }
}
