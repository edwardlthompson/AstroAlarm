# Feature: daily-sky-segmented

> Daily and Sky chip rows use a Nothing-style segmented control.

## Acceptance criteria

- ✅ User-visible: Daily 2D|3D and Sky Yearly|Sol|Chart are single-choice segmented buttons, not FilterChips
- ✅ Offline/error: last chip still persisted in AstroNavPreferences
- ✅ Accessibility: each segment has visible text
- ✅ i18n: existing tab strings

## Smoke scenario

1. _Given_ Daily hub
2. _When_ the user taps 3D
3. _Then_ the 3D globe shows and 2D is unselected

## Container map

| Layer | Path |
|-------|------|
| Logic | `HubSegment.kt` (index helpers) |
| View | `AstroHubPages.kt` |
| Tests | `HubSegmentTest.kt` |
| Wiring | none |

## Tests

- Automated: yes — DailyChip index 0 is 2D
- Coverage: SkyChip Chart is last

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md`.
