package org.astroalarm.astro.birth

import java.time.Instant

data class NatalChart(
    val instant: Instant,
    val ascendant: EclipticPoint?,
    val midheaven: EclipticPoint?,
    val sun: EclipticPoint,
    val moon: EclipticPoint,
    val planets: Map<NatalBody, EclipticPoint>,
    val approximate: Boolean,
    val timeUnknown: Boolean,
)
