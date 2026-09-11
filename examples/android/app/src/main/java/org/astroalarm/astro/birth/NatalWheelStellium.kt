package org.astroalarm.astro.birth

/**
 * Radial offsets so natal glyphs in a stellium do not stack on the same radius.
 * Angle (longitude) stays true; only radius grows with cluster index.
 */
object NatalWheelStellium {
    const val CLUSTER_DEG = 8.0
    const val STEP_FRAC = 0.035f

    /** Sorted by longitude; returns parallel list of radius multipliers (≥ 1). */
    fun radiusFactors(longitudes: List<Double>): List<Float> {
        if (longitudes.isEmpty()) return emptyList()
        val indexed = longitudes.mapIndexed { i, lon -> i to NatalAspectMath.wrap360(lon) }
            .sortedBy { it.second }
        val factors = FloatArray(longitudes.size) { 1f }
        var cluster = 0
        for (k in indexed.indices) {
            if (k > 0) {
                val gap = NatalAspectMath.wrap360(indexed[k].second - indexed[k - 1].second)
                if (gap > CLUSTER_DEG && gap < 360.0 - CLUSTER_DEG) cluster = 0 else cluster++
            }
            factors[indexed[k].first] = 1f + cluster * STEP_FRAC
        }
        return factors.toList()
    }
}
