package org.astroalarm.ui

object SkyHandTick {
    fun wrap(deg: Float): Float {
        var d = deg % 360f
        if (d < 0f) d += 360f
        return d
    }

    fun shortest(from: Float, to: Float): Float {
        var d = wrap(to) - wrap(from)
        if (d > 180f) d -= 360f
        if (d < -180f) d += 360f
        return d
    }

    fun crossed(prev: Float, now: Float, event: Float): Boolean {
        val step = shortest(prev, now)
        if (kotlin.math.abs(step) < 0.0001f) return false
        val toEvent = shortest(prev, event)
        return if (step >= 0f) toEvent in 0f..step else toEvent in step..0f
    }
}

object YearlyHighlight {
    const val MS = 150
}
