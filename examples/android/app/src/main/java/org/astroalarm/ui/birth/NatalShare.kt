package org.astroalarm.ui.birth

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.astroalarm.astro.birth.NatalChart
import org.astroalarm.share.SkySharePaint
import org.astroalarm.share.sharePaintedSky
import org.astroalarm.ui.solarterm.WheelZoomPan
import dev.foss.goldenpath.R

fun natalShare(
    context: Context,
    scope: CoroutineScope,
    chart: NatalChart?,
    viewport: WheelZoomPan,
    sky: org.astroalarm.astro.birth.NatalSkySnapshot,
    dark: Boolean,
    showSun: Boolean,
    showMoon: Boolean,
    showMercury: Boolean,
): Boolean {
    val c = chart ?: return false
    scope.launch {
        sharePaintedSky(
            context,
            { px -> SkySharePaint.chart(px, c, viewport, sky, showSun, showMoon, showMercury, dark) },
            context.getString(R.string.sky_share_chooser),
            context.getString(R.string.sky_share_failed),
            context.getString(R.string.sky_share_oom),
        )
    }
    return true
}
