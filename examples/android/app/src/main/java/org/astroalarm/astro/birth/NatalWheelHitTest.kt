package org.astroalarm.astro.birth

import org.astroalarm.astro.zodiac.ZodiacSign
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot

/** What a tap on the natal wheel resolved to. */
sealed class NatalWheelHit {
    data class NatalPlanet(val body: NatalBody) : NatalWheelHit()
    data class LiveBody(val body: NatalBody) : NatalWheelHit()
    data class Zodiac(val sign: ZodiacSign) : NatalWheelHit()
    data class House(val number: Int) : NatalWheelHit()
    data object Asc : NatalWheelHit()
    data object Dsc : NatalWheelHit()
    data object Mc : NatalWheelHit()
    data object Ic : NatalWheelHit()
}

object NatalWheelHitTest {
    fun at(
        x: Float,
        y: Float,
        size: Int,
        chart: NatalChart,
        sky: NatalSkySnapshot? = null,
        showSun: Boolean = true,
        showMoon: Boolean = true,
        showMercury: Boolean = true,
    ): NatalWheelHit? {
        val cx = size / 2f
        val cy = size / 2f
        val rOuter = size * 0.46f
        val rHouse = size * 0.38f
        val rPlanet = size * 0.30f
        val rLive = size * 0.24f
        val asc = NatalWheelLayout.frameAscLon(chart)
        val thresh = size * 0.055f
        var best: NatalWheelHit? = null
        var bestD = thresh.toDouble()

        fun consider(hit: NatalWheelHit, px: Float, py: Float) {
            val d = hypot((x - px).toDouble(), (y - py).toDouble())
            if (d < bestD) {
                bestD = d
                best = hit
            }
        }

        for ((body, pt) in chart.planets) {
            val (px, py) = NatalWheelLayout.xy(cx, cy, rPlanet, pt.longitudeDeg, asc)
            consider(NatalWheelHit.NatalPlanet(body), px, py)
        }
        if (sky != null) {
            if (showSun) {
                val (px, py) = NatalWheelLayout.xy(cx, cy, rLive, sky.sun, asc)
                consider(NatalWheelHit.LiveBody(NatalBody.SUN), px, py)
            }
            if (showMoon) {
                val (px, py) = NatalWheelLayout.xy(cx, cy, rLive, sky.moon, asc)
                consider(NatalWheelHit.LiveBody(NatalBody.MOON), px, py)
            }
            if (showMercury) {
                val (px, py) = NatalWheelLayout.xy(cx, cy, rLive, sky.mercury, asc)
                consider(NatalWheelHit.LiveBody(NatalBody.MERCURY), px, py)
            }
        }
        if (best != null) return best

        // Spokes before zodiac/houses so amber/blue rays explain Asc/MC, not the nearest cusp mid.
        val dist = hypot((x - cx).toDouble(), (y - cy).toDouble()).toFloat()
        if (dist in (rPlanet * 0.15f)..(rOuter * 1.05f)) {
            val tapAng = atan2((y - cy).toDouble(), (x - cx).toDouble())
            fun nearSpoke(lon: Double): Boolean {
                val a = NatalWheelLayout.angleRad(lon, asc)
                var d = abs(tapAng - a)
                if (d > Math.PI) d = 2 * Math.PI - d
                return d < 0.18
            }
            if (chart.ascendant != null) {
                if (nearSpoke(asc)) return NatalWheelHit.Asc
                if (nearSpoke(NatalAspectMath.wrap360(asc + 180.0))) return NatalWheelHit.Dsc
            }
            val mc = chart.midheaven?.longitudeDeg
            if (mc != null) {
                if (nearSpoke(mc)) return NatalWheelHit.Mc
                if (nearSpoke(NatalAspectMath.wrap360(mc + 180.0))) return NatalWheelHit.Ic
            }
        }

        for (sign in ZodiacSign.entries) {
            val mid = sign.startLongitudeDeg + 15.0
            val (px, py) = NatalWheelLayout.xy(cx, cy, rOuter * 0.92f, mid, asc)
            consider(NatalWheelHit.Zodiac(sign), px, py)
        }
        val ascPt = chart.ascendant ?: EclipticPoint(asc)
        for (c in WholeSignHouses.cusps(ascPt)) {
            val mid = c.sign.startLongitudeDeg + 15.0
            val (px, py) = NatalWheelLayout.xy(cx, cy, rHouse * 0.88f, mid, asc)
            consider(NatalWheelHit.House(c.house), px, py)
        }
        return best
    }
}
