# ADR-0003: FOSS on-device ephemeris (ADR-0002 invariants)

- **Status:** Accepted
- **Date:** 2026-08-30
- **Updated:** 2026-09-01
- **Deciders:** Project team

> Product FOSS invariants referenced in the init brief as ADR-0002. Template ADR-0002 remains GitHub feedback privacy. This ADR records the AstroAlarm distribution rule.

## Context

Astronomical alarm times can be fetched from a weather or astronomy API. That would add accounts, trackers, and a network dependency. The product is MIT FOSS and must run offline. The first slice used NOAA solar date arithmetic and synodic-age lunar cartoons; those did not match the sky at the observer (southern-hemisphere noon pinned south, moon as elongation from the sun).

## Decision

- 100% pure FOSS under MIT. No Google Play Services, Firebase, or proprietary telemetry.
- Sun and moon **positions and event times** use Apache-2.0 `org.shredzone.commons:commons-suncalc:3.11` (`SkyBodies`, `SolarCalculator`, `LunarCalculator`). No remote astronomy APIs. Do not vendor GPLv3 planetarium code.
- NOAA `SolarMath` stays only for **tropical apparent longitude** (zodiac ring + equinox/solstice binary search in `SolarSeasons`).
- Geometric hour angle / declination for 2D/3D clocks uses suncalc **true altitude** (no refraction). Civil rise/set use suncalc **apparent** horizon. Polar night/day yields `null` rise/set.
- Observer height is sea level (known ~1 minute sunrise bias at typical elevations).
- City search uses a bundled offline catalog first; system `Geocoder` / `LocationManager` are optional fallbacks.
- Distribution is GitHub Releases (F-Droid later). No Play Core.

## Consequences

- Alarm Instants and clock marks share one engine; previously cartoon moon/sun times will shift
- Ephemeris quality is bounded by suncalc + NOAA longitude, not a remote service
- The 3D view remains a transit clock, not a planetarium (fake stars, tropical zodiac bubbles)
- Location still needs a one-time city pick or a runtime location permission
- CI FOSS greps must scan Gradle/TOML only
