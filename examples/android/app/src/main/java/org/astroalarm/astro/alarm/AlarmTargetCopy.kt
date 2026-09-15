package org.astroalarm.astro.alarm

import android.content.res.Resources
import dev.foss.goldenpath.R
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.sol.PlanetEventType
import org.astroalarm.ui.AstroEventLabels

object AlarmTargetCopy {
    fun icon(t: AlarmTarget): String = when (t) {
        is AlarmTarget.Solar -> "☀️ "
        is AlarmTarget.Lunar -> "🌙 "
        is AlarmTarget.Zodiac -> t.sign.symbol + " "
        is AlarmTarget.CustomClock -> "⏰ "
        is AlarmTarget.SolarTerm -> "🍃 "
        is AlarmTarget.Planet, is AlarmTarget.PlanetAlign, is AlarmTarget.AllPlanetsAlign -> "🪐 "
        is AlarmTarget.NatalAscAspect, is AlarmTarget.NatalMcAspect,
        is AlarmTarget.MoonReturn, is AlarmTarget.SolarReturn,
        is AlarmTarget.MercuryStation, is AlarmTarget.MoonSignIngress,
        is AlarmTarget.NatalCompoundSpecific, is AlarmTarget.NatalCompoundAny -> "✨ "
    }

    fun fallback(res: Resources, t: AlarmTarget): String = when (t) {
        is AlarmTarget.Solar -> AstroEventLabels.solarLabel(res, t.event)
        is AlarmTarget.Lunar -> AstroEventLabels.lunarLabel(res, t.event)
        is AlarmTarget.Zodiac -> t.sign.englishName
        is AlarmTarget.CustomClock -> String.format("%02d:%02d", t.hour, t.minute)
        is AlarmTarget.SolarTerm -> t.term.pinyin
        is AlarmTarget.Planet -> "${t.body.name} ${planetEventLabel(res, t.event)}"
        is AlarmTarget.PlanetAlign -> "${t.bodyA.name} + ${t.bodyB.name}"
        is AlarmTarget.AllPlanetsAlign -> res.getString(R.string.event_all_planets)
        is AlarmTarget.NatalAscAspect -> "Asc ${t.aspect.name} ${t.body.name}"
        is AlarmTarget.NatalMcAspect -> "MC ${t.aspect.name} ${t.body.name}"
        is AlarmTarget.MoonReturn -> res.getString(R.string.event_moon_return)
        is AlarmTarget.SolarReturn -> res.getString(R.string.event_solar_return)
        is AlarmTarget.MercuryStation -> "Mercury ${t.kind.name}"
        is AlarmTarget.MoonSignIngress -> "Moon enters ${t.sign.englishName}"
        is AlarmTarget.NatalCompoundSpecific -> "Chart ${t.kinds.size}-way · ${t.kinds.joinToString("+")}"
        is AlarmTarget.NatalCompoundAny ->
            res.getString(if (t.arity >= 3) R.string.event_any_triple else R.string.event_any_double)
    }

    fun planetEventLabel(res: Resources, event: PlanetEventType): String = res.getString(
        when (event) {
            PlanetEventType.Rise -> R.string.event_planet_rise
            PlanetEventType.Set -> R.string.event_planet_set
            PlanetEventType.RetrogradeStart -> R.string.event_planet_retro
            PlanetEventType.DirectStart -> R.string.event_planet_direct
            PlanetEventType.Opposition -> R.string.event_planet_opp
            PlanetEventType.InferiorConjunction -> R.string.event_planet_inf
            PlanetEventType.SuperiorConjunction -> R.string.event_planet_sup
        },
    )
}
