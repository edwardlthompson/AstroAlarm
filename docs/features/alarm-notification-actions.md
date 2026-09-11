# Feature: alarm-notification-actions

> Ringing notification exposes Snooze and Stop in the shade (FOSS; no proprietary push).

## Acceptance criteria

- ✅ User-visible behavior: While an alarm rings, the ongoing notification shows Stop (first) and Snooze actions; Stop dismisses and reschedules next natural fire; Snooze arms `now + snoozeMinutes`
- ✅ Offline/error behavior: Missing/blank alarm id cancels notification only (fail-soft); lockscreen Activity stops tone when shade action fires
- ✅ Accessibility: Action titles use string resources; Stop listed before Snooze (same order as lockscreen)
- ✅ i18n: reuses `astro_action_stop` / `astro_action_snooze` (en/es/fr)

## Smoke scenario

1. _Given_ an alarm is ringing (notification posted)
2. _When_ user taps Stop on the notification
3. _Then_ tone stops, notification clears, and no near-term snooze arm remains

## Container map

| Layer | Path |
|-------|------|
| Logic | `AlarmNotificationChannel`, `AstroAlarmReceiver`, `AlarmDismissActions` |
| View | Notification actions (shade); Activity dismiss broadcast |
| Tests | `AlarmNotificationChannelTest`, `AlarmNotificationActionsTest` |
| Wiring | Receiver fire path only |

## Tests

- Automated: yes — ringing notification includes two actions; receiver snooze/stop cancel notification
- Coverage: action titles + PendingIntent actions

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Tracked follow-up from Birth Chart plan (notification-shade actions)
- No UnifiedPush / FCM
