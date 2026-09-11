# Feature: birth-profile

> Natal birth details + on-device Ascendant/MC/Sun/Moon (FOSS Kepler/NOAA; ADR-0003). Multi-profile SharedPreferences JSON.

## Acceptance criteria

- ✅ User-visible behavior: Chart tab form saves label, date, time (or unknown), birth place via CityCatalog/Geocoder; active profile selectable
- ✅ Offline/error behavior: catalog-first place search; zone fallback flagged; Ascendant disabled when time unknown
- ✅ Accessibility: form fields use string resources; warnings for polar / unknown time / zone fallback
- ✅ i18n: `astro_tab_chart`, birth form keys in en/es/fr

## Smoke scenario

1. _Given_ Chart tab open
2. _When_ user saves a profile with NYC birth place and known time
3. _Then_ Ascendant sign appears (Slice B summary) and profile persists across restart

## Container map

| Layer | Path |
|-------|------|
| Logic | `astro/birth/` |
| View | `ui/birth/` |
| Tests | `AscendantMathTest`, `BirthTimeZonesTest`, `BirthProfileJsonTest` |
| Wiring | `AstroScreen` page 6 + `BirthProfileStore` in `GoldenPathScreen` |

## Tests

- Automated: yes — Ascendant ±2° regression cases; TZDB offsets for 3 eras/locations; polar/unknown guards
- Coverage: pure math + JSON round-trip

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Alarm-grade longitudes only — not Swiss Ephemeris chart certification
- Historical UTC via IANA `ZoneId` + TZDB
