package org.astroalarm.widget

import android.app.Application
import android.appwidget.AppWidgetManager
import android.graphics.Bitmap
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ClockRenderSizeTest {

    @Test
    fun innerDpTimesDensityOnXxhdpi() {
        // 4×4 250 dp widget, 8 dp pad, density 3 → (250 − 16) × 3 = 702
        assertEquals(702, ClockRenderSize.fromWidgetDp(250, 250, 3f))
    }

    @Test
    fun beatsOldSixHundredCap() {
        val old = (250 * 2.5f).toInt().coerceIn(200, 600)
        assertEquals(600, old)
        assertTrue(ClockRenderSize.fromWidgetDp(250, 250, 3f) > old)
    }

    @Test
    fun usesLargerMaxOptions() {
        assertEquals(
            (300 - 16) * 2,
            ClockRenderSize.fromWidgetDp(140, 140, 2f, maxWidthDp = 300, maxHeightDp = 300),
        )
    }

    @Test
    fun capsAt1024() {
        assertEquals(1024, ClockRenderSize.fromWidgetDp(400, 400, 4f))
    }

    @Test
    fun floorsAt200() {
        assertEquals(200, ClockRenderSize.fromWidgetDp(50, 50, 1f))
    }

    @Test
    fun fromOptionsReadsMaxAndDensity() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val density = context.resources.displayMetrics.density
        val options = Bundle().apply {
            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 140)
            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 140)
            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 250)
            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 250)
        }
        assertEquals(
            ClockRenderSize.fromWidgetDp(140, 140, density, maxWidthDp = 250, maxHeightDp = 250),
            ClockRenderSize.fromOptions(context, options, fallbackDp = 140),
        )
    }

    @Test
    fun atDevicePixelsDisablesDensityScale() {
        val bmp = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
        ClockRenderSize.atDevicePixels(bmp)
        assertEquals(Bitmap.DENSITY_NONE, bmp.density)
    }
}
