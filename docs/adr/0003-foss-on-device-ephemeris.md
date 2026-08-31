# ADR-0003: FOSS on-device ephemeris (ADR-0002 invariants)

- **Status:** Accepted
- **Date:** 2026-08-30
- **Deciders:** Project team

> Product FOSS invariants referenced in the init brief as ADR-0002. Template ADR-0002 remains GitHub feedback privacy. This ADR records the AstroAlarm distribution rule.

## Context

Astronomical alarm times can be fetched from a weather or astronomy API. That would add accounts, trackers, and a network dependency. The product is MIT FOSS and must run offline.

## Decision

- 100% pure FOSS under MIT. No Google Play Services, Firebase, or proprietary telemetry.
- Solar events use NOAA algorithms (`SolarCalculator` / `SolarMath`).
- Lunar events use Meeus algorithms (`LunarCalculator`).
- City search uses a bundled offline catalog first; system `Geocoder` / `LocationManager` are optional fallbacks.
- Distribution is GitHub Releases (F-Droid later). No Play Core.

## Consequences

- Ephemeris quality is bounded by the implemented algorithms, not a remote service
- Location still needs a one-time city pick or a runtime location permission
- CI FOSS greps must scan Gradle/TOML only
