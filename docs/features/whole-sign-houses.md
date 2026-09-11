# Feature: whole-sign-houses

> Whole-sign house table on the Chart tab from Ascendant sign (FOSS; alarm-grade; ADR-0003). No Placidus.

## Acceptance criteria

- ✅ User-visible behavior: With active profile + known birth time, Chart summary shows houses 1–12 as whole signs starting at rising sign; planet placements list house number
- ✅ Offline/error behavior: Hidden when time unknown or Asc missing; polar warning already on summary
- ✅ Accessibility: House rows are plain text via string resources
- ✅ i18n: en/es/fr keys in `birth_strings.xml`

## Smoke scenario

1. _Given_ Chart tab with NYC profile and known birth time
2. _When_ summary loads
3. _Then_ House 1 equals Rising sign; Sun shows a house 1–12

## Container map

| Layer | Path |
|-------|------|
| Logic | `astro/birth/WholeSignHouses.kt` |
| View | `ui/birth/BirthChartHouses.kt` |
| Tests | `WholeSignHousesTest` |
| Wiring | `BirthChartSummarySection` only |

## Tests

- Automated: yes — cusp signs for known Asc; body→house; null Asc → empty
- Coverage: pure house math
- Command: `./gradlew :app:testDebugUnitTest --tests 'org.astroalarm.astro.birth.WholeSignHousesTest'`

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Whole-sign only (tracked Placidus remains out of scope)
- Kepler/NOAA natal longitudes — not Swiss Ephemeris certification
