# Feature: onboarding

First-launch checklist so AstroAlarm can ring on time over the lock screen.

## Acceptance criteria

- ✅ User-visible behavior: first launch shows Allow for notifications, location, exact alarms, full-screen intent, and battery unrestricted; Continue is always available; Settings can reopen the list
- ✅ Offline/error behavior: special-access settings intents fail soft; grants refresh on resume
- ✅ Accessibility: each row has a title; Allowed/Allow are tappable; Continue is a full-width button
- ✅ i18n: `onboard_*` in en/es/fr

## Smoke scenario

1. _Given_ a fresh install
2. _When_ the app opens
3. _Then_ the permission checklist appears and battery unrestricted is one of the rows

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../onboard/OnboardingPolicy.kt` |
| View | `examples/android/.../ui/onboard/OnboardingScreen.kt` |
| Tests | `OnboardingPolicyTest.kt` |
| Wiring | `GoldenPathApp` `OnboardingGate` ≤10 lines |

## Tests

- Automated: yes — `OnboardingPolicyTest.kt`; instrumented Golden Path tests skip the gate via `OnboardingChecker.skipUiGate()`

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Install-time only (no dialog): Internet, boot completed, wake lock, vibrate
- OEM autostart on ColorOS is not a standard Android permission
