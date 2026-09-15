package org.astroalarm.widget

import kotlin.math.sin

/** Scale ring labels so adjacent glyphs do not overlap. */
object DiskLabelFit {
    const val MIN_READABLE = 11f
    const val MAX_SIZE = 28f
    private const val FIT = 0.72f
    private const val EM = 0.62f
    private val capOverride = ThreadLocal<Float>()

    /** Lift the widget 28px cap so a 2160 export is not postage-stamp type. */
    fun <T> withExportCap(canvasSize: Int, block: () -> T): T {
        capOverride.set((canvasSize * 0.048f).coerceAtLeast(MIN_READABLE))
        return try {
            block()
        } finally {
            capOverride.remove()
        }
    }

    fun textSize(size: Int, labelR: Float, count: Int, sample: String): Float {
        val cap = capOverride.get() ?: MAX_SIZE
        val target = (size * 0.048f).coerceIn(MIN_READABLE, cap)
        if (count <= 1 || labelR <= 1f) return target
        val limit = chord(labelR, count) * FIT
        val width = glyphWidth(target, sample)
        if (width <= limit) return target
        return (target * (limit / width)).coerceAtLeast(MIN_READABLE)
    }

    fun evenHoursOnly(size: Int, labelR: Float, sample: String = "23"): Boolean {
        val ts = textSize(size, labelR, 24, sample)
        return glyphWidth(ts, sample) > chord(labelR, 24) * FIT
    }

    fun glyphWidth(textSize: Float, sample: String): Float =
        textSize * EM * sample.length.coerceAtLeast(1)

    private fun chord(labelR: Float, count: Int): Float =
        2f * labelR * sin(Math.PI / count).toFloat()
}
