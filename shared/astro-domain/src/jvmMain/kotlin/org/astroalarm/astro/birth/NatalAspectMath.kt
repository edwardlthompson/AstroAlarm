package org.astroalarm.astro.birth

import kotlin.math.abs

/** Major aspects for natal–natal wheel chords (not transit Asc enum). */
enum class WheelAspect(val degrees: Double, val orbLum: Double = 8.0, val orbOther: Double = 6.0) {
    Conjunction(0.0),
    Sextile(60.0),
    Square(90.0),
    Trine(120.0),
    Opposition(180.0),
}

data class NatalAspectLink(
    val a: NatalBody,
    val b: NatalBody,
    val aspect: WheelAspect,
    val deltaDeg: Double,
)

object NatalAspectMath {
    fun wrap360(deg: Double): Double {
        var d = deg % 360.0
        if (d < 0.0) d += 360.0
        return d
    }

    fun wrap180(deg: Double): Double {
        var d = wrap360(deg)
        if (d > 180.0) d -= 360.0
        return d
    }

    fun links(chart: NatalChart): List<NatalAspectLink> {
        val bodies = chart.planets.entries.toList()
        val out = ArrayList<NatalAspectLink>()
        for (i in bodies.indices) {
            for (j in i + 1 until bodies.size) {
                val (ba, pa) = bodies[i]
                val (bb, pb) = bodies[j]
                val diff = abs(wrap180(pa.longitudeDeg - pb.longitudeDeg))
                val lum = ba == NatalBody.SUN || ba == NatalBody.MOON ||
                    bb == NatalBody.SUN || bb == NatalBody.MOON
                for (asp in WheelAspect.entries) {
                    val orb = if (lum) asp.orbLum else asp.orbOther
                    if (abs(diff - asp.degrees) <= orb) {
                        out.add(NatalAspectLink(ba, bb, asp, diff))
                        break
                    }
                }
            }
        }
        return out
    }
}
