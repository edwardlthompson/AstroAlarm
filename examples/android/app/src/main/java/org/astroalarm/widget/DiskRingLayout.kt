package org.astroalarm.widget

/** 2D dial radii: hour numbers on the pie rim, ticks inside, Moon around Earth. */
object DiskRingLayout {
    /** Keep bubbles/labels inside WheelDisk clip (r = size/2). */
    const val EDGE = 3f

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
        val clipSafe = center - EDGE
        val glyph = ZodiacGlyph.radius(size)
        val stroke = (glyph * 0.10f).coerceIn(0.8f, 1.5f)
        val tickLen = (size * 0.028f).coerceIn(6f, 12f)
        val textEst = (size * 0.048f).coerceIn(DiskLabelFit.MIN_READABLE, DiskLabelFit.MAX_SIZE)
        val halfW = DiskLabelFit.glyphWidth(textEst, "Sep") / 2f
        val monthBand = if (months) {
            (tickLen + textEst * 0.70f + halfW).coerceAtLeast((size * 0.070f).coerceIn(12f, 26f))
        } else {
            0f
        }
        val zodiacBand = if (zodiac) glyph * 2.15f + stroke else 0f
        // Outer rings win over pie floor so labels/glyphs stay inside the clip.
        val innerR = (clipSafe - monthBand - zodiacBand).coerceAtLeast(size * 0.20f)
        val hourLabelR = innerR * 0.94f
        val hourTickR = innerR * 0.86f
        val earthR = innerR * 0.16f
        val moonOrbitR = innerR * 0.30f
        val moonR = innerR * 0.045f
        val handStartR = moonOrbitR + moonR + (size * 0.012f).coerceIn(3f, 8f)
        val monthLabelR = if (months) {
            (innerR + tickLen + textEst * 0.70f).coerceAtMost(clipSafe - halfW)
        } else {
            innerR
        }
        val zodiacR = if (zodiac) {
            (innerR + monthBand + glyph * 1.15f).coerceAtMost(clipSafe - glyph - stroke)
        } else {
            0f
        }
        val noonPointerEndR = when {
            zodiac -> zodiacR
            months -> innerR + monthBand
            else -> 0f
        }
        return Rings(
            innerR = innerR,
            hourTickR = hourTickR,
            hourLabelR = hourLabelR,
            monthLabelR = monthLabelR,
            zodiacR = zodiacR,
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
