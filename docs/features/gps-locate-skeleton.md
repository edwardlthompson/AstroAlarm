# Feature: gps-locate-skeleton

> GPS locate uses three muted bars, not only a circular spinner.

## Acceptance criteria

- ✅ User-visible: locating shows three muted bars under the city row
- ✅ Offline/error: bars hide when locate finishes; button stays disabled while locating
- ✅ Accessibility: locating announced via existing GPS content description
- ✅ i18n: no new keys

## Smoke scenario

1. _Given_ location permission granted
2. _When_ the user taps Use my location
3. _Then_ three bars appear until the Snackbar reports success or failure

## Container map

| Layer | Path |
|-------|------|
| Logic | `ui/GpsLocateSkeleton.kt` (`BAR_COUNT`) |
| View | `AstroLocationCard.kt` |
| Tests | `GpsLocateSkeletonTest.kt` |
| Wiring | none |

## Tests

- Automated: yes — BAR_COUNT is 3
- Coverage: constant used by the composable

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md`.
