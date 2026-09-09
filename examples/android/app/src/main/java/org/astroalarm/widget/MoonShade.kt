package org.astroalarm.widget

/** Moon cameras: Earth-view phase vs ecliptic-north top-down. */
object MoonShade {
    data class Cam(
        val lat0: Double,
        val lon0: Double,
        val sunDec: Double,
        val subsolarLon: Double,
        val sunwardDeg: Float,
    )

    fun wrap180(deg: Double): Double {
        var d = deg % 360.0
        if (d > 180.0) d -= 360.0
        if (d <= -180.0) d += 360.0
        return d
    }

    fun earthView(elongationDeg: Double): Cam {
        val elong = ((elongationDeg % 360.0) + 360.0) % 360.0
        return Cam(0.0, 0.0, 0.0, wrap180(180.0 - elong), 0f)
    }

    fun topDown(sunwardDeg: Float): Cam = Cam(90.0, 0.0, 0.0, 0.0, sunwardDeg)
}
