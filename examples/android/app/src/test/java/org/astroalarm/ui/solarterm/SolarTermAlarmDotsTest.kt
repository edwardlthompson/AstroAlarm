package org.astroalarm.ui.solarterm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.solarterm.SolarTerm
import org.astroalarm.solarterm.SolarTermLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class SolarTermAlarmDotsTest {

    @Test
    fun solsticeDotsSitOnSeasonLines() {
        assertEquals(
            SolarTermLayout.canvasDeg(90.0),
            SolarTermAlarmDots.lineDeg(SolarTerm.XIAZHI.ordinal),
            0.05f,
        )
        assertEquals(
            SolarTermLayout.canvasDeg(270.0),
            SolarTermAlarmDots.lineDeg(SolarTerm.DONGZHI.ordinal),
            0.05f,
        )
        assertTrue(
            abs(
                SolarTermAlarmDots.lineDeg(SolarTerm.XIAZHI.ordinal) -
                    SolarTermWheelRenderer.midDeg(SolarTerm.XIAZHI.ordinal),
            ) > 7f,
        )
    }

    @Test
    fun compactEmojiKeepsTheBand() {
        val size = 160
        val inner = size * SolarTermWheelRenderer.innerFrac(true)
        val outer = size * SolarTermWheelRenderer.outerFrac()
        val hubFill = inner - size * 0.02f
        val emojiR = (inner + outer) * 0.5f
        assertTrue(SolarTermAlarmDots.ringR(hubFill) < inner)
        assertTrue(SolarTermAlarmDots.ringR(hubFill) < emojiR)

        val cx = size / 2f
        val cy = size / 2f
        val mid = Math.toRadians(SolarTermWheelRenderer.midDeg(SolarTerm.XIAZHI.ordinal).toDouble())
        val ex = cx + emojiR * cos(mid).toFloat()
        val ey = cy + emojiR * sin(mid).toFloat()
        val (dx, dy) = SolarTermAlarmDots.xy(cx, cy, hubFill, 0f, SolarTerm.XIAZHI.ordinal)
        val dist = hypot((ex - dx).toDouble(), (ey - dy).toDouble())
        assertTrue(dist > SolarTermAlarmDots.dotRad(size) + size * 0.02f)
        val got = Math.toDegrees(atan2((dy - cy).toDouble(), (dx - cx).toDouble())).toFloat()
        assertTrue(SolarTermRadialLabels.angDist(got, SolarTermAlarmDots.lineDeg(SolarTerm.XIAZHI.ordinal)) < 1f)
    }

    @Test
    fun solarSolsticeAlarmsUseSeasonLines() {
        assertEquals(
            SolarTerm.XIAZHI.ordinal,
            SolarTermAlarmDots.ordOf(AlarmTarget.Solar(SolarEventType.JuneSolstice)),
        )
        assertEquals(
            SolarTerm.DONGZHI.ordinal,
            SolarTermAlarmDots.ordOf(AlarmTarget.Solar(SolarEventType.DecemberSolstice)),
        )
        assertEquals(
            SolarTerm.CHUNFEN.ordinal,
            SolarTermAlarmDots.ordOf(AlarmTarget.Solar(SolarEventType.MarchEquinox)),
        )
        assertEquals(null, SolarTermAlarmDots.ordOf(AlarmTarget.Solar(SolarEventType.Sunrise)))
    }
}
