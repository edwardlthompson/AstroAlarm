# Feature: progressive-add

> Add-alarm type list is the full catalog: Sun, Moon, Zodiac, Clock, Seasonal, Planet, and Natal when a chart exists.

## Acceptance criteria

- ✅ User-visible: type menu lists moon, seasonal, and planet (alignments live under Planet) without a More events gate
- ✅ Offline/error: types stay listed without a place; save still requires a city for sun/moon/rise-set
- ✅ Accessibility: dropdown remains TalkBack-labeled
- ✅ i18n: existing tab labels

## Smoke scenario

1. _Given_ Add alarm
2. _When_ the type menu opens
3. _Then_ Moon, Seasonal, and Planet are in the list

## Container map

| Layer | Path |
|-------|------|
| Logic | `ProgressiveAdd.kt` |
| View | `AstroCelestialPickers.kt`, `AstroEditDialog.kt` |
| Tests | `ProgressiveAddTest.kt` |
| Wiring | none |

## Tests

- Automated: yes — catalog includes lunar/seasonal/planet; natal only with a profile
- Coverage: kindOf sunrise and full moon

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md`.
