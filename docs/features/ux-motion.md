# Feature: ux-motion

> FAB, list rows, and empty state animate; toggles haptic; reduced-motion skips them.

## Acceptance criteria

- ✅ User-visible: Alarms FAB fades+scales on the Alarms tab; new rows animate height; empty mark crossfades
- ✅ Offline/error: animator scale 0 skips enter/row/empty motion
- ✅ Accessibility: reduced-motion path has no scale/fade
- ✅ i18n: none

## Smoke scenario

1. _Given_ reduced-motion off
2. _When_ the user switches to Alarms
3. _Then_ the FAB appears with fade+scale

## Container map

| Layer | Path |
|-------|------|
| Logic | `UxMotion.kt` |
| View | `AstroScreen.kt`, `AstroAlarmList.kt`, `AstroAlarmsPage.kt`, `AstroAlarmRow.kt` |
| Tests | `UxMotionTest.kt` |
| Wiring | none |

## Tests

- Automated: yes — fab enter is None when reduced
- Coverage: timings 200/180/120

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md`.
