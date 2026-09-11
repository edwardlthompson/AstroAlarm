package org.astroalarm.ui.birth

import android.content.Context
import android.content.res.Resources
import dev.foss.goldenpath.R
import org.astroalarm.astro.birth.NatalBody
import org.astroalarm.astro.birth.NatalWheelHit
import org.astroalarm.astro.zodiac.ZodiacSign

data class NatalExplainCopy(val title: String, val body: String)

object NatalGlyphExplain {
    fun of(hit: NatalWheelHit, res: Resources): NatalExplainCopy = when (hit) {
        NatalWheelHit.Asc -> NatalExplainCopy(
            res.getString(R.string.astro_explain_asc_title),
            res.getString(R.string.astro_explain_asc_body),
        )
        NatalWheelHit.Dsc -> NatalExplainCopy(
            res.getString(R.string.astro_explain_dsc_title),
            res.getString(R.string.astro_explain_dsc_body),
        )
        NatalWheelHit.Mc -> NatalExplainCopy(
            res.getString(R.string.astro_explain_mc_title),
            res.getString(R.string.astro_explain_mc_body),
        )
        NatalWheelHit.Ic -> NatalExplainCopy(
            res.getString(R.string.astro_explain_ic_title),
            res.getString(R.string.astro_explain_ic_body),
        )
        is NatalWheelHit.House -> NatalExplainCopy(
            res.getString(R.string.astro_explain_house_title, hit.number),
            res.getString(houseBodyId(hit.number)),
        )
        is NatalWheelHit.Zodiac -> NatalExplainCopy(
            "${hit.sign.symbol} ${hit.sign.englishName}",
            res.getString(zodiacBodyId(hit.sign)),
        )
        is NatalWheelHit.NatalPlanet -> NatalExplainCopy(
            glyphTitle(hit.body, natal = true, res),
            res.getString(planetBodyId(hit.body)),
        )
        is NatalWheelHit.LiveBody -> NatalExplainCopy(
            glyphTitle(hit.body, natal = false, res),
            res.getString(R.string.astro_explain_live_body, res.getString(planetNameId(hit.body))),
        )
    }

    fun of(hit: NatalWheelHit, context: Context): NatalExplainCopy = of(hit, context.resources)

    private fun glyphTitle(body: NatalBody, natal: Boolean, res: Resources): String {
        val name = res.getString(planetNameId(body))
        val glyph = when (body) {
            NatalBody.SUN -> "☉"
            NatalBody.MOON -> "☽"
            NatalBody.MERCURY -> "☿"
            NatalBody.VENUS -> "♀"
            NatalBody.MARS -> "♂"
            NatalBody.JUPITER -> "♃"
            NatalBody.SATURN -> "♄"
            NatalBody.URANUS -> "♅"
            NatalBody.NEPTUNE -> "♆"
        }
        val kind = if (natal) {
            res.getString(R.string.astro_explain_natal_label)
        } else {
            res.getString(R.string.astro_explain_live_label)
        }
        return "$glyph $name · $kind"
    }

    private fun planetNameId(b: NatalBody): Int = when (b) {
        NatalBody.SUN -> R.string.astro_birth_sun
        NatalBody.MOON -> R.string.astro_birth_moon
        NatalBody.MERCURY -> R.string.astro_explain_mercury
        NatalBody.VENUS -> R.string.astro_explain_venus
        NatalBody.MARS -> R.string.astro_explain_mars
        NatalBody.JUPITER -> R.string.astro_explain_jupiter
        NatalBody.SATURN -> R.string.astro_explain_saturn
        NatalBody.URANUS -> R.string.astro_explain_uranus
        NatalBody.NEPTUNE -> R.string.astro_explain_neptune
    }

    private fun planetBodyId(b: NatalBody): Int = when (b) {
        NatalBody.SUN -> R.string.astro_explain_sun_body
        NatalBody.MOON -> R.string.astro_explain_moon_body
        NatalBody.MERCURY -> R.string.astro_explain_mercury_body
        NatalBody.VENUS -> R.string.astro_explain_venus_body
        NatalBody.MARS -> R.string.astro_explain_mars_body
        NatalBody.JUPITER -> R.string.astro_explain_jupiter_body
        NatalBody.SATURN -> R.string.astro_explain_saturn_body
        NatalBody.URANUS -> R.string.astro_explain_uranus_body
        NatalBody.NEPTUNE -> R.string.astro_explain_neptune_body
    }

    private fun zodiacBodyId(s: ZodiacSign): Int = when (s) {
        ZodiacSign.Aries -> R.string.astro_explain_aries
        ZodiacSign.Taurus -> R.string.astro_explain_taurus
        ZodiacSign.Gemini -> R.string.astro_explain_gemini
        ZodiacSign.Cancer -> R.string.astro_explain_cancer
        ZodiacSign.Leo -> R.string.astro_explain_leo
        ZodiacSign.Virgo -> R.string.astro_explain_virgo
        ZodiacSign.Libra -> R.string.astro_explain_libra
        ZodiacSign.Scorpio -> R.string.astro_explain_scorpio
        ZodiacSign.Sagittarius -> R.string.astro_explain_sagittarius
        ZodiacSign.Capricorn -> R.string.astro_explain_capricorn
        ZodiacSign.Aquarius -> R.string.astro_explain_aquarius
        ZodiacSign.Pisces -> R.string.astro_explain_pisces
    }

    private fun houseBodyId(n: Int): Int = when (n) {
        1 -> R.string.astro_explain_house_1
        2 -> R.string.astro_explain_house_2
        3 -> R.string.astro_explain_house_3
        4 -> R.string.astro_explain_house_4
        5 -> R.string.astro_explain_house_5
        6 -> R.string.astro_explain_house_6
        7 -> R.string.astro_explain_house_7
        8 -> R.string.astro_explain_house_8
        9 -> R.string.astro_explain_house_9
        10 -> R.string.astro_explain_house_10
        11 -> R.string.astro_explain_house_11
        else -> R.string.astro_explain_house_12
    }
}
