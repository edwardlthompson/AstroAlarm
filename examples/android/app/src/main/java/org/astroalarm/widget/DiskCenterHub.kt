package org.astroalarm.widget

/** Plain hub drawn at the 2D dial center instead of the Earth globe. */
object DiskCenterHub {
    const val COLOR = 0xFF1C2230.toInt()

    fun radius(size: Int): Float = (size * 0.022f).coerceIn(3.5f, 9f)
}
