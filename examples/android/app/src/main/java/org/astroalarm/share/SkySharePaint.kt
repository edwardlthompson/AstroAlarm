package org.astroalarm.share

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import org.astroalarm.astro.birth.NatalChart
import org.astroalarm.astro.birth.NatalSkySnapshot
import org.astroalarm.astro.birth.NatalWheelRenderer
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.sol.PlanetBody
import org.astroalarm.solarterm.SolarTermPalette
import org.astroalarm.ui.sol.SolRenderer
import org.astroalarm.ui.solarterm.SolarTermDrawRequest
import org.astroalarm.ui.solarterm.SolarTermWheelRenderer
import org.astroalarm.ui.solarterm.WheelZoomPan
import org.astroalarm.widget.Astro3DRenderer
import org.astroalarm.widget.AstroDiskRenderer
import org.astroalarm.widget.DiskLabelFit
import java.time.Instant

object SkySharePaint {
    fun daily2d(
        size: Int,
        place: AstroPlace?,
        alarms: List<AstroAlarm>,
        now: Instant,
        viewport: WheelZoomPan,
        showZodiac: Boolean,
        showEventTimes: Boolean,
        showMonthTicks: Boolean,
        showHourMarks: Boolean,
        earth: Bitmap?,
        moon: Bitmap?,
    ): Bitmap = DiskLabelFit.withExportCap(size) {
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.rgb(14, 22, 38))
        canvas.save()
        SkyShare.applyViewport(canvas, viewport, size)
        AstroDiskRenderer.draw(
            canvas, place, alarms, now, size,
            showZodiac, showEventTimes, showMonthTicks, showHourMarks, earth, moon,
        )
        canvas.restore()
        bmp
    }

    fun daily3d(
        size: Int,
        place: AstroPlace?,
        alarms: List<AstroAlarm>,
        now: Instant,
        viewport: WheelZoomPan,
        showZodiac: Boolean,
        showEventTimes: Boolean,
        parallaxX: Float,
        parallaxY: Float,
        earth: Bitmap?,
        moon: Bitmap?,
    ): Bitmap = DiskLabelFit.withExportCap(size) {
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.rgb(14, 22, 38))
        canvas.save()
        SkyShare.applyViewport(canvas, viewport, size)
        Astro3DRenderer.draw(
            canvas, place, alarms, now, size,
            showZodiac, showEventTimes, parallaxX, parallaxY, earth, moon,
        )
        canvas.restore()
        bmp
    }

    fun yearly(
        size: Int,
        req: SolarTermDrawRequest,
        viewport: WheelZoomPan,
        earth: Bitmap?,
        moon: Bitmap?,
    ): Bitmap {
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(SolarTermPalette.wheelBg(req.dark))
        canvas.save()
        SkyShare.applyViewport(canvas, viewport, size)
        SolarTermWheelRenderer.draw(canvas, req, size, earth, moon)
        canvas.restore()
        return bmp
    }

    fun sol(
        size: Int,
        now: Instant,
        zoom: Float,
        dark: Boolean,
        textures: Map<PlanetBody, Bitmap?>,
        alarms: List<AstroAlarm>,
        place: AstroPlace?,
        scaleLabel: String,
        showEventTimes: Boolean,
        natalProfile: org.astroalarm.astro.birth.BirthProfile?,
        showNatalGhosts: Boolean,
    ): Bitmap = SolRenderer.render(
        size, now, zoom, dark, textures, alarms, place, scaleLabel,
        showEventTimes, natalProfile, showNatalGhosts,
    )

    fun chart(
        size: Int,
        chart: NatalChart,
        viewport: WheelZoomPan,
        sky: NatalSkySnapshot?,
        showSun: Boolean,
        showMoon: Boolean,
        showMercury: Boolean,
        dark: Boolean,
    ): Bitmap {
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(if (dark) 0xFF121212.toInt() else 0xFFF5F5F5.toInt())
        canvas.save()
        SkyShare.applyViewport(canvas, viewport, size)
        NatalWheelRenderer.draw(
            canvas, size, chart, sky, emptyList(),
            showSun, showMoon, showMercury, focusBody = null, dark = dark,
        )
        canvas.restore()
        return bmp
    }
}
