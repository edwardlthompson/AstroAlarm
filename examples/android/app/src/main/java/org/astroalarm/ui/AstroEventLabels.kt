package org.astroalarm.ui

import android.content.res.Resources
import dev.foss.goldenpath.R
import org.astroalarm.astro.model.LunarEventType
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.zodiac.ZodiacPoint
import org.astroalarm.astro.zodiac.ZodiacSign

object AstroEventLabels {

    fun solarLabel(res: Resources, event: SolarEventType): String = res.getString(
        when (event) {
            SolarEventType.Sunrise -> R.string.event_sunrise
            SolarEventType.Sunset -> R.string.event_sunset
            SolarEventType.CivilDawn -> R.string.event_civil_dawn
            SolarEventType.CivilDusk -> R.string.event_civil_dusk
            SolarEventType.NauticalDawn -> R.string.event_nautical_dawn
            SolarEventType.NauticalDusk -> R.string.event_nautical_dusk
            SolarEventType.AstronomicalDawn -> R.string.event_astro_dawn
            SolarEventType.AstronomicalDusk -> R.string.event_astro_dusk
            SolarEventType.SolarNoon -> R.string.event_solar_noon
            SolarEventType.SolarMidnight -> R.string.event_solar_midnight
            SolarEventType.GoldenHourMorning -> R.string.event_golden_am
            SolarEventType.GoldenHourEvening -> R.string.event_golden_pm
            SolarEventType.BlueHourMorning -> R.string.event_blue_am
            SolarEventType.BlueHourEvening -> R.string.event_blue_pm
            SolarEventType.MarchEquinox -> R.string.event_mar_equinox
            SolarEventType.SeptemberEquinox -> R.string.event_sep_equinox
            SolarEventType.JuneSolstice -> R.string.event_jun_solstice
            SolarEventType.DecemberSolstice -> R.string.event_dec_solstice
        },
    )

    fun solarDescription(res: Resources, event: SolarEventType): String = res.getString(
        when (event) {
            SolarEventType.Sunrise -> R.string.event_sunrise_d
            SolarEventType.Sunset -> R.string.event_sunset_d
            SolarEventType.CivilDawn -> R.string.event_civil_dawn_d
            SolarEventType.CivilDusk -> R.string.event_civil_dusk_d
            SolarEventType.NauticalDawn -> R.string.event_nautical_dawn_d
            SolarEventType.NauticalDusk -> R.string.event_nautical_dusk_d
            SolarEventType.AstronomicalDawn -> R.string.event_astro_dawn_d
            SolarEventType.AstronomicalDusk -> R.string.event_astro_dusk_d
            SolarEventType.SolarNoon -> R.string.event_solar_noon_d
            SolarEventType.SolarMidnight -> R.string.event_solar_midnight_d
            SolarEventType.GoldenHourMorning -> R.string.event_golden_am_d
            SolarEventType.GoldenHourEvening -> R.string.event_golden_pm_d
            SolarEventType.BlueHourMorning -> R.string.event_blue_am_d
            SolarEventType.BlueHourEvening -> R.string.event_blue_pm_d
            SolarEventType.MarchEquinox -> R.string.event_mar_equinox_d
            SolarEventType.SeptemberEquinox -> R.string.event_sep_equinox_d
            SolarEventType.JuneSolstice -> R.string.event_jun_solstice_d
            SolarEventType.DecemberSolstice -> R.string.event_dec_solstice_d
        },
    )

    fun lunarLabel(res: Resources, event: LunarEventType): String = res.getString(
        when (event) {
            LunarEventType.Moonrise -> R.string.event_moonrise
            LunarEventType.Moonset -> R.string.event_moonset
            LunarEventType.MoonTransit -> R.string.event_moon_transit
            LunarEventType.NewMoon -> R.string.event_new_moon
            LunarEventType.WaxingCrescent -> R.string.event_waxing_crescent
            LunarEventType.FirstQuarter -> R.string.event_first_quarter
            LunarEventType.WaxingGibbous -> R.string.event_waxing_gibbous
            LunarEventType.FullMoon -> R.string.event_full_moon
            LunarEventType.WaningGibbous -> R.string.event_waning_gibbous
            LunarEventType.LastQuarter -> R.string.event_last_quarter
            LunarEventType.WaningCrescent -> R.string.event_waning_crescent
        },
    )

    fun lunarDescription(res: Resources, event: LunarEventType): String = res.getString(
        when (event) {
            LunarEventType.Moonrise -> R.string.event_moonrise_d
            LunarEventType.Moonset -> R.string.event_moonset_d
            LunarEventType.MoonTransit -> R.string.event_moon_transit_d
            LunarEventType.NewMoon -> R.string.event_new_moon_d
            LunarEventType.WaxingCrescent -> R.string.event_waxing_crescent_d
            LunarEventType.FirstQuarter -> R.string.event_first_quarter_d
            LunarEventType.WaxingGibbous -> R.string.event_waxing_gibbous_d
            LunarEventType.FullMoon -> R.string.event_full_moon_d
            LunarEventType.WaningGibbous -> R.string.event_waning_gibbous_d
            LunarEventType.LastQuarter -> R.string.event_last_quarter_d
            LunarEventType.WaningCrescent -> R.string.event_waning_crescent_d
        },
    )

    fun zodiacLabel(sign: ZodiacSign, point: ZodiacPoint): String =
        "${sign.symbol} ${sign.englishName} ${point.englishName}"

    fun zodiacDescription(res: Resources, sign: ZodiacSign, point: ZodiacPoint): String {
        val deg = (sign.startLongitudeDeg + point.degreeOffset) % 360.0
        val name = sign.englishName
        return when (point) {
            ZodiacPoint.Beginning -> res.getString(R.string.event_zodiac_begin, name, deg.toInt())
            ZodiacPoint.Middle -> res.getString(R.string.event_zodiac_mid, name, deg.toInt())
            ZodiacPoint.End -> res.getString(R.string.event_zodiac_end, name)
        }
    }

    fun isSeasonal(event: SolarEventType): Boolean = when (event) {
        SolarEventType.MarchEquinox,
        SolarEventType.SeptemberEquinox,
        SolarEventType.JuneSolstice,
        SolarEventType.DecemberSolstice -> true
        else -> false
    }

    fun offsetSummary(res: Resources, offsetMinutes: Int, eventName: String): String = when {
        offsetMinutes == 0 -> res.getString(R.string.event_offset_at, eventName)
        offsetMinutes < 0 -> res.getString(R.string.event_offset_before, -offsetMinutes, eventName)
        else -> res.getString(R.string.event_offset_after, offsetMinutes, eventName)
    }
}
