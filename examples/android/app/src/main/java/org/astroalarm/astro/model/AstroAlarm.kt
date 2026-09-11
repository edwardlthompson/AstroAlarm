package org.astroalarm.astro.model

import org.astroalarm.astro.birth.NatalBody
import org.astroalarm.astro.zodiac.ZodiacPoint
import org.astroalarm.astro.zodiac.ZodiacSign
import org.astroalarm.sol.PlanetBody
import org.astroalarm.sol.PlanetEventType
import java.time.DayOfWeek

enum class NatalAspect(val degrees: Double) {
    Conjunction(0.0),
    Square(90.0),
    Opposition(180.0),
}

enum class MercuryStationKind {
    RetrogradeStart,
    DirectStart,
}

enum class SolarEventType {
    Sunrise,
    Sunset,
    CivilDawn,
    CivilDusk,
    NauticalDawn,
    NauticalDusk,
    AstronomicalDawn,
    AstronomicalDusk,
    SolarNoon,
    SolarMidnight,
    GoldenHourMorning,
    GoldenHourEvening,
    BlueHourMorning,
    BlueHourEvening,
    MarchEquinox,
    SeptemberEquinox,
    JuneSolstice,
    DecemberSolstice
}

enum class LunarEventType {
    Moonrise,
    Moonset,
    MoonTransit,
    NewMoon,
    FullMoon,
    WaxingCrescent,
    FirstQuarter,
    WaxingGibbous,
    WaningGibbous,
    LastQuarter,
    WaningCrescent
}

sealed interface AlarmTarget {
    data class CustomClock(val hour: Int, val minute: Int) : AlarmTarget
    data class Solar(val event: SolarEventType, val offsetMinutes: Int = 0) : AlarmTarget
    data class Lunar(val event: LunarEventType, val offsetMinutes: Int = 0) : AlarmTarget
    data class Zodiac(val sign: ZodiacSign, val point: ZodiacPoint, val offsetMinutes: Int = 0) : AlarmTarget
    data class SolarTerm(val term: org.astroalarm.solarterm.SolarTerm, val offsetMinutes: Int = 0) : AlarmTarget
    data class Planet(val body: PlanetBody, val event: PlanetEventType, val offsetMinutes: Int = 0) : AlarmTarget
    data class PlanetAlign(val bodyA: PlanetBody, val bodyB: PlanetBody, val offsetMinutes: Int = 0) : AlarmTarget
    data class AllPlanetsAlign(val offsetMinutes: Int = 0) : AlarmTarget
    data class NatalAscAspect(
        val body: NatalBody,
        val aspect: NatalAspect,
        val profileId: String,
        val offsetMinutes: Int = 0,
    ) : AlarmTarget
    data class NatalMcAspect(
        val body: NatalBody,
        val aspect: NatalAspect,
        val profileId: String,
        val offsetMinutes: Int = 0,
    ) : AlarmTarget
    data class MoonReturn(val profileId: String, val offsetMinutes: Int = 0) : AlarmTarget
    data class SolarReturn(val profileId: String, val offsetMinutes: Int = 0) : AlarmTarget
    data class MercuryStation(
        val kind: MercuryStationKind,
        val offsetMinutes: Int = 0,
    ) : AlarmTarget
    data class MoonSignIngress(val sign: ZodiacSign, val offsetMinutes: Int = 0) : AlarmTarget
    /** Specific double/triple of natal story events (2 or 3 kinds). */
    data class NatalCompoundSpecific(
        val kinds: List<String>,
        val profileId: String,
        val offsetMinutes: Int = 0,
    ) : AlarmTarget
    data class NatalCompoundAny(
        val arity: Int,
        val profileId: String,
        val offsetMinutes: Int = 0,
    ) : AlarmTarget
}

data class AstroAlarm(
    val id: String,
    val label: String,
    val enabled: Boolean = true,
    val target: AlarmTarget,
    val daysOfWeek: Set<DayOfWeek> = emptySet(),
    val toneEnabled: Boolean = true,
    val toneUri: String? = null,
    val ttsEnabled: Boolean = true,
    val vibrateEnabled: Boolean = true,
    val snoozeMinutes: Int = 10,
    val mathUnlockEnabled: Boolean = false,
    val lastFiredEpochMs: Long = 0L
) {
    val isOnce: Boolean get() = target is AlarmTarget.CustomClock && daysOfWeek.isEmpty()
}
