package org.astroalarm.wear

import android.graphics.Canvas
import android.graphics.Rect
import android.view.SurfaceHolder
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchFace
import androidx.wear.watchface.WatchFaceService
import androidx.wear.watchface.WatchFaceType
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import java.time.ZonedDateTime

/**
 * FOSS Wear OS face: Daily 2D now-up red hand + ZoneId day/night pie.
 * Uses androidx.wear.watchface only (no GMS / HMS).
 */
class AstroWearWatchFaceService : WatchFaceService() {
    private class EmptyAssets : Renderer.SharedAssets {
        override fun onDestroy() = Unit
    }

    override suspend fun createWatchFace(
        surfaceHolder: SurfaceHolder,
        watchState: WatchState,
        complicationSlotsManager: ComplicationSlotsManager,
        currentUserStyleRepository: CurrentUserStyleRepository,
    ): WatchFace {
        val renderer =
            object : Renderer.CanvasRenderer2<EmptyAssets>(
                surfaceHolder,
                currentUserStyleRepository,
                watchState,
                CanvasType.HARDWARE,
                interactiveDrawModeUpdateDelayMillis = 1000L,
                clearWithBackgroundTintBeforeRenderingHighlightLayer = false,
            ) {
                override suspend fun createSharedAssets(): EmptyAssets = EmptyAssets()

                override fun render(
                    canvas: Canvas,
                    bounds: Rect,
                    zonedDateTime: ZonedDateTime,
                    sharedAssets: EmptyAssets,
                ) {
                    val ambient = renderParameters.drawMode == DrawMode.AMBIENT
                    WearDialCanvas.draw(canvas, bounds, zonedDateTime, ambient)
                }

                override fun renderHighlightLayer(
                    canvas: Canvas,
                    bounds: Rect,
                    zonedDateTime: ZonedDateTime,
                    sharedAssets: EmptyAssets,
                ) = Unit
            }
        return WatchFace(WatchFaceType.ANALOG, renderer)
    }
}
