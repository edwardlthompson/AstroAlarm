# Feature: alarm-calendar-peers

Keep Sun seasonal and Yearly jieqi as separate list rows. Matching equinox/solstice calendars share one fire so only one lockscreen rings. Widgets show each alarm only on the disks where it belongs.

## Acceptance criteria

- ✅ User-visible behavior: saving June Solstice and Xiàzhì keeps both rows; editor warns “Same moment as …”; Alarms list shows “Also listed as …”; one notification; Yearly shows one ray dot; Daily/Upcoming collapse peers; Sol shows yearly peers on Earth's path (see `sol-yearly-alarms.md`)
- ✅ Offline/error behavior: grouping does not need a place; Solar seasonal without a place has no next fire; jieqi still computes; empty list cancels the OS alarm
- ✅ Accessibility: warning text is visible body copy next to the target picker and under the peer row label
- ✅ i18n: `astro_alarm_duplicate_calendar` and `astro_alarm_also_listed` in en/es/fr

## Smoke scenario

1. _Given_ an enabled June Solstice (Sun) alarm
2. _When_ the user adds Xiàzhì (Seasonal) with the same offset
3. _Then_ both rows stay, a same-moment note appears, and only one lockscreen rings

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../astro/alarm/AlarmFireIdentity.kt`, `AlarmWidgetScope.kt` |
| View | `AstroEditDialog.kt`, `AstroAlarmList.kt`, widget overlays |
| Tests | `AlarmFireIdentityTest.kt`, `AlarmWidgetScopeTest.kt`, `AstroNextFireTest.kt`, `AstroNextFirePeerTest.kt`, `AstroAlarmStoreSaveAllTest.kt` |
| Wiring | `AstroAlarmScheduler`, `AstroAlarmActivity`, widget providers |

## Tests

- Automated: yes — files above
- Coverage: identity, next-fire skip, saveAll, widget scope, consume on snooze

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Critique resolutions

- Null/empty: `keyOf` / `nextInstant` return null; scheduler cancels
- Network: N/A
- Race: `saveAll` + `consumeOccurrence` on Stop and Snooze
- Exceptions: exhaustive `when` + one persist `apply()`

## Definition of Done

See `docs/FEATURE_MODULES.md` per-feature checklist.

## Notes

- Zodiac Beginning of Aries/Cancer/Libra/Capricorn shares the same fire key
- Snooze-until delay is out of scope; this slice only stops the twin from stealing the slot
