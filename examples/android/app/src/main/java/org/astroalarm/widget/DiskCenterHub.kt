package org.astroalarm.widget

import org.astroalarm.ui.AstroCanvas

/** Plain hub drawn at the 2D dial center instead of the Earth globe. */
object DiskCenterHub {
    val COLOR: Int = AstroCanvas.night

    fun radius(size: Int): Float = (size * 0.022f).coerceIn(3.5f, 9f)
}
