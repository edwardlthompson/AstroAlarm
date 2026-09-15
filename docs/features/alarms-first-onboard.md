# Feature: alarms-first-onboard

> Ring permissions stay required. Location is optional until a solar/lunar alarm is saved. Denied FSI/notifications show an Alarms banner.

## Acceptance criteria

- ✅ Continue works with location denied if Exact, FSI, Notifications, and Battery are granted (SDK-dependent)
- ✅ Location still appears on the onboarding list as optional
- ✅ Revoking FSI or notifications re-opens the gate via missing ring steps
- ✅ Alarms tab shows `settings_notifications_denied_*` when notifications or FSI are off
- ✅ GPS failure uses a Snackbar, not a Toast
- ✅ i18n: existing keys; no new `strings.xml` lines required

## Smoke scenario

1. _Given_ location is denied and ring permissions are granted
2. _When_ the user taps Continue
3. _Then_ home opens and a custom clock alarm can still be saved

## Container map

| Layer | Path |
|-------|------|
| Logic | `OnboardingPolicy.kt`, `OnboardingChecker.kt` |
| View | `OnboardingScreen.kt`, `OnboardingGate.kt`, `AstroAlarmsPage.kt`, `SettingsScreen.kt` |
| Tests | `OnboardingPolicyTest.kt`, `OnboardingCheckerMissingTest.kt` |
| Wiring | none |

## Tests

- Automated: yes — ringSteps omits Location; missingRingSteps ignores location deny
- Coverage: API 26 Battery-only ring; API 34 ring list without Location

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md`.
