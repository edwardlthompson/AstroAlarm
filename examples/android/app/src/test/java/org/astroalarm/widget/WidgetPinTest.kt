package org.astroalarm.widget

import android.app.Application
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import dev.foss.goldenpath.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class WidgetPinTest {
    @Test
    fun receiverEmitsPinnedSuccess() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Application>()
        var got: Int? = null
        val job = launch { WidgetPinEvents.messages.collect { got = it } }
        yield()
        WidgetPinReceiver().onReceive(context, Intent(WidgetPin.ACTION))
        yield()
        job.cancel()
        assertEquals(R.string.astro_widget_pinned_success, got)
    }
}
