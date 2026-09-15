# Feature: ring-will-not-fire

> Alarms banner when Exact, FSI, notifications, or battery ring steps are missing.

## Acceptance criteria

- ✅ User-visible: Exact (and other ring steps) show “This alarm will not ring” with the step body and a Fix action
- ✅ Offline/error: banner hidden when all ring steps granted; Location never counts
- ✅ Accessibility: title + body + button text
- ✅ i18n: `ring_fire_strings.xml` en/es/fr; reuses onboard bodies

## Smoke scenario

1. _Given_ Exact alarms denied on API 31+
2. _When_ the user opens Alarms
3. _Then_ the banner is visible and Fix opens exact-alarm settings

## Container map

| Layer | Path |
|-------|------|
| Logic | `onboard/RingWillNotFire.kt` |
| View | `AlarmPermissionBanner.kt`, `OnboardingIntents.kt` |
| Tests | `RingWillNotFireTest.kt` |
| Wiring | none |

## Tests

- Automated: yes — primary missing prefers Exact when only Exact is missing
- Coverage: empty missing list hides banner

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md`.
