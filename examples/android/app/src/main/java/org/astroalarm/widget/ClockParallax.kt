package org.astroalarm.widget

/** Map accelerometer axes to 3D clock parallax. Z is pitch so upright rest is ~0 vertical shift. */
object ClockParallax {
    fun fromAccelerometer(ax: Float, az: Float): Pair<Float, Float> {
        val x = (-ax * 2.2f).coerceIn(-16f, 16f)
        val y = (-az * 2.2f).coerceIn(-16f, 16f)
        return x to y
    }
}
