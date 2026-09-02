# Feature: celestial-alarms

> Separate Seasonal (24 jieqi) and Planet alarm categories, including pair alignments and the next all-planet cluster.

## Acceptance criteria

- ✅ User-visible behavior: create-alarm has six chips (Sun, Moon, Zodiac, Clock, Seasonal, Planet). Seasonal is jieqi only. Planet covers rise/set/retrograde/direct/opposition or inner conjunctions, Align with…, and All planets align. Grouped list has Seasonal and Planet sections
- ✅ Offline/error behavior: Rise/set need a valid `AstroPlace` (`canSave`). Jieqi and alignments do not. Unknown JSON `kind` is skipped. Alignment search is bounded at 50 years then `nextInstant` is null
- ✅ Accessibility: existing alarm row TalkBack; yearly repeat copy for jieqi and alignments
- ✅ i18n: `astro_tab_seasonal` / `astro_tab_planet` and grouped empty strings (en/es/fr)

## Smoke scenario

1. _Given_ no city set
2. _When_ the user adds a Seasonal Lìchūn alarm
3. _Then_ Save is enabled and the next fire is the coming February jieqi

## Container map

| Layer | Path |
|-------|------|
| Logic | `org.astroalarm.sol`, `AstroAlarmJson`, `AstroNextFire` |
| View | `AstroCelestialPickers.kt`, `AstroEditTargetSection.kt`, `AstroAlarmList.kt` |
| Tests | `AstroAlarmJsonCelestialTest`, `PlanetKeplerTest` |
| Wiring | existing alarm store / scheduler |

## Tests

- Automated: yes — JSON round-trip; Lìchūn after January; Mercury retrograde window; pair Δλ ≤ 8°; all-planet covering span in 0…360 and fire threshold 90°

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Pair alignment: next geocentric ecliptic conjunction when `|wrap180(λ_a − λ_b)|` crosses below **8°**. Same body and Earth are rejected.
- All planets align: smallest circular heliocentric longitude span of the eight planets **≤ 90°**. If already inside a cluster, fire at the next entry after leaving. A perfect line never happens.
- Kepler is on-device (Meeus/JPL-style elements), not a live JPL service.
