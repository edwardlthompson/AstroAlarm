package org.astroalarm.ui

import androidx.test.core.app.ApplicationProvider
import android.app.Application
import org.astroalarm.astro.model.SolarEventType
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AstroEventLabelsTest {
    @Test
    fun sunriseLabelComesFromResources() {
        val res = ApplicationProvider.getApplicationContext<Application>().resources
        assertEquals("Sunrise", AstroEventLabels.solarLabel(res, SolarEventType.Sunrise))
        assertEquals("At exact time of Sunrise", AstroEventLabels.offsetSummary(res, 0, "Sunrise"))
    }
}
