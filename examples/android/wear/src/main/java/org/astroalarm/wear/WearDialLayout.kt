package org.astroalarm.wear

/** Geometry helpers for the Wear dial (pure, unit-tested). */
object WearDialLayout {
    fun radius(boundsSize: Float): Float = boundsSize * 0.46f

    fun handInner(radius: Float): Float = radius * 0.12f

    fun handOuter(radius: Float): Float = radius * 0.92f

    /** Forward sweep from [fromDeg] to [toDeg] in [0, 360). */
    fun sweepDeg(fromDeg: Float, toDeg: Float): Float {
        var d = toDeg - fromDeg
        while (d < 0f) d += 360f
        while (d >= 360f) d -= 360f
        return d
    }
}
