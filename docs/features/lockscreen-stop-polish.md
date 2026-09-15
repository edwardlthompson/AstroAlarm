# Feature: lockscreen-stop-polish

> Lockscreen Stop/Snooze haptic, 80ms Stop scale, TalkBack names the alarm.

## Acceptance criteria

- ✅ User-visible: Stop scales down 80ms then silences when reduced-motion is off
- ✅ Offline/error: reduced-motion skips scale; math unlock still opens before Stop
- ✅ Accessibility: Stop/Snooze Role.Button and CD includes the alarm label
- ✅ i18n: `a11y_stop_alarm` / `a11y_snooze_alarm` en/es/fr

## Smoke scenario

1. _Given_ a ringing sunrise alarm without math
2. _When_ the user taps Stop
3. _Then_ a confirm haptic fires and sound stops after the short scale

## Container map

| Layer | Path |
|-------|------|
| Logic | `ui/LockscreenStop.kt` |
| View | `AstroAlarmLockscreenView.kt` |
| Tests | `LockscreenStopTest.kt` |
| Wiring | none |

## Tests

- Automated: yes — spoken CD concatenates action + label; scale skipped when reduced
- Coverage: empty label still has the action word

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md`.
