# Feature: alarm-snooze

> Lockscreen Snooze arms `now + snoozeMinutes` via AlarmManager; Stop dismisses. Stop at top, Snooze at bottom.

## Acceptance criteria

- ✅ User-visible behavior: Snooze silences the ring and re-fires after the configured snooze duration; Stop silences and schedules the next natural occurrence only
- ✅ Offline/error behavior: snooze uses on-device AlarmManager; blank/`unknown` alarm ids skip schedule (fail-soft finish)
- ✅ Accessibility: Stop and Snooze remain full-width 56dp buttons with string resources; Stop top / Snooze bottom reduces accidental dismiss
- ✅ i18n: reuses `astro_action_snooze` / `astro_action_stop` (en/es/fr)

## Smoke scenario

1. _Given_ lockscreen is ringing for an enabled alarm with snooze 5–10 min
2. _When_ the user taps Snooze
3. _Then_ sound/vibe stop and `adb shell dumpsys alarm` shows the next `org.astroalarm` AlarmClock ~N minutes out; Stop does not leave that near-term arm

## Container map

| Layer | Path |
|-------|------|
| Logic | `AstroAlarmScheduler.scheduleSnooze` / `snoozeTriggerEpochMs`, `AlarmDismissActions`, `AlarmFireIdentity.consumeOccurrence(disableOnce)` |
| View | `AstroAlarmLockscreenView.kt` |
| Tests | `AstroAlarmSchedulerSnoozeTest.kt`, `AlarmFireIdentityOnceTest.kt`, peer stamp in `AlarmFireIdentityTest` |
| Wiring | `AstroAlarmActivity` snooze vs stop handlers |

## Tests

- Automated: yes — `AstroAlarmSchedulerSnoozeTest.kt`, `AlarmFireIdentityOnceTest.kt`, `AlarmFireIdentityTest.kt`
- Coverage: trigger epoch math; once kept enabled on snooze / disabled on stop; peer stamp still applies

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md` per-feature checklist.

## Notes

- Math unlock remains Stop-only
- Peer consume on snooze still stamps `lastFired` so twins do not steal the slot; see `alarm-calendar-peers.md`
