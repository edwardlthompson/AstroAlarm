package org.astroalarm.astro.alarm

import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.sol.PlanetEventType

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

    fun fallback(t: AlarmTarget): String = when (t) {
        is AlarmTarget.Solar -> t.event.name
        is AlarmTarget.Lunar -> t.event.name
        is AlarmTarget.Zodiac -> t.sign.englishName
        is AlarmTarget.CustomClock -> String.format("%02d:%02d", t.hour, t.minute)
        is AlarmTarget.SolarTerm -> t.term.pinyin
        is AlarmTarget.Planet -> "${t.body.name} ${planetEventLabel(t.event)}"
        is AlarmTarget.PlanetAlign -> "${t.bodyA.name} + ${t.bodyB.name}"
        is AlarmTarget.AllPlanetsAlign -> "All planets align"
        is AlarmTarget.NatalAscAspect -> "Asc ${t.aspect.name} ${t.body.name}"
        is AlarmTarget.NatalMcAspect -> "MC ${t.aspect.name} ${t.body.name}"
        is AlarmTarget.MoonReturn -> "Moon return"
        is AlarmTarget.SolarReturn -> "Solar return"
        is AlarmTarget.MercuryStation -> "Mercury ${t.kind.name}"
        is AlarmTarget.MoonSignIngress -> "Moon enters ${t.sign.englishName}"
        is AlarmTarget.NatalCompoundSpecific -> "Chart ${t.kinds.size}-way · ${t.kinds.joinToString("+")}"
        is AlarmTarget.NatalCompoundAny -> if (t.arity >= 3) "Any chart triple" else "Any chart double"
    }

    fun planetEventLabel(event: PlanetEventType): String = when (event) {
        PlanetEventType.Rise -> "Rise"
        PlanetEventType.Set -> "Set"
        PlanetEventType.RetrogradeStart -> "Retrograde"
        PlanetEventType.DirectStart -> "Direct"
        PlanetEventType.Opposition -> "Opposition"
        PlanetEventType.InferiorConjunction -> "Inferior conjunction"
        PlanetEventType.SuperiorConjunction -> "Superior conjunction"
    }
}
