package org.astroalarm.widget

/** 2D dial radii: hour numbers on the pie rim, ticks inside, Moon around Earth. */
object DiskRingLayout {
    data class Rings(
        val innerR: Float,
        val hourTickR: Float,
        val hourLabelR: Float,
        val monthLabelR: Float,
        val zodiacR: Float,
        val bodyR: Float,
        val badgeR: Float,
        val handStartR: Float,
        val handEndR: Float,
        val alarmR: Float,
        val earthR: Float,
        val moonOrbitR: Float,
        val moonR: Float,
        val noonPointerEndR: Float,
        val monthBand: Float,
    )

    fun of(size: Int, months: Boolean, zodiac: Boolean): Rings {
        val center = size / 2f
        val glyph = ZodiacGlyph.radius(size)
        val monthBand = if (months) (size * 0.070f).coerceIn(12f, 26f) else 0f
        val zodiacBand = if (zodiac) glyph * 2.35f else 0f
        val pad = (size * 0.018f).coerceIn(4f, 10f)
        val innerR = (center - pad - monthBand - zodiacBand).coerceAtLeast(size * 0.28f)
        val hourLabelR = innerR * 0.94f
        val hourTickR = innerR * 0.86f
        val earthR = innerR * 0.16f
        val moonOrbitR = innerR * 0.30f
        val moonR = innerR * 0.045f
        val handStartR = moonOrbitR + moonR + (size * 0.012f).coerceIn(3f, 8f)
        val noonPointerEndR = when {
            zodiac -> innerR + monthBand + glyph * 1.15f
            months -> innerR + monthBand
            else -> 0f
        }
        return Rings(
            innerR = innerR,
            hourTickR = hourTickR,
            hourLabelR = hourLabelR,
            monthLabelR = innerR + monthBand * 0.55f,
            zodiacR = innerR + monthBand + if (zodiac) glyph * 1.15f else 0f,
            bodyR = innerR * 0.56f,
            badgeR = innerR * 0.48f,
            handStartR = handStartR,
            handEndR = hourTickR,
            alarmR = hourTickR,
            earthR = earthR,
            moonOrbitR = moonOrbitR,
            moonR = moonR,
            noonPointerEndR = noonPointerEndR,
            monthBand = monthBand,
        )
    }
}
