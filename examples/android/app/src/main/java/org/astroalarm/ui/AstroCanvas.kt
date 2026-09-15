package org.astroalarm.ui

/** Packed ARGB matching `design-tokens.json` `canvas.{night,gold}`. Call sites use these vals. */
object AstroCanvas {
    val night: Int = argb(0x1A1A2E)
    val gold: Int = argb(0xC9A227)

    fun argb(rgb: Int): Int = (0xFF shl 24) or (rgb and 0x00FFFFFF)
}
