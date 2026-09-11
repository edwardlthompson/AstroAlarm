package org.astroalarm.astro.birth

import android.graphics.Canvas
import android.graphics.Paint
import org.astroalarm.astro.zodiac.ZodiacSign

/** Whole-sign house numbers on the mid band (Asc-left). */
object NatalWheelHousePaint {
    fun drawNumbers(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        rHouse: Float,
        chart: NatalChart,
        paint: Paint,
    ) {
        val ascPt = chart.ascendant ?: EclipticPoint(NatalWheelLayout.frameAscLon(chart))
        val cusps = WholeSignHouses.cusps(ascPt)
        if (cusps.isEmpty()) return
        val ascLon = NatalWheelLayout.frameAscLon(chart)
        val r = rHouse * 0.88f
        for (c in cusps) {
            val mid = c.sign.startLongitudeDeg + 15.0
            val (x, y) = NatalWheelLayout.xy(cx, cy, r, mid, ascLon)
            canvas.drawText(c.house.toString(), x, y + paint.textSize / 3f, paint)
        }
    }

    /** Mid-longitude of whole-sign house 1 (rising sign center) for tests. */
    fun house1MidLon(ascendant: EclipticPoint): Double =
        ascendant.sign.startLongitudeDeg + 15.0

    fun risingSign(frameAscLon: Double): ZodiacSign =
        ZodiacSign.fromEclipticLongitude(frameAscLon)
}
