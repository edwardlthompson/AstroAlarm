# Feature: alarms-first-chrome

> Alarm-clock chrome: honest copy, demoted pin, delete confirm, 48dp targets, lockscreen brand theme.

## Acceptance criteria

- ✅ User-visible: Alarms list is first; widget pin is a bottom text button with a unique CTA; swipe hint gone; OpenShouter is on About only; About inset is debug-only; donate launch prompt waits for an enabled alarm and is not first-run
- ✅ Offline/error: pin success uses the system callback; unsupported launchers get a Snackbar how-to; delete confirm still reschedules
- ✅ Accessibility: Edit, Delete, and number-wheel steppers are at least 48dp
- ✅ i18n: EN/es/fr string rewrites in place; delete confirm keys added

## Smoke scenario

1. _Given_ a clean install with no alarms
2. _When_ the user reaches the Alarms tab
3. _Then_ there is no donate dialog, no swipe hint, no OpenShouter card, and the pin control is below the list

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../astro/alarm/AlarmGroupSections.kt`, `about/ProductUpdate.kt`, `widget/WidgetPin.kt` |
| View | `AstroAlarmsPage.kt`, `AstroAlarmRow.kt`, `AstroWheelPicker.kt`, Settings/About, lockscreen |
| Tests | `AlarmGroupSectionsTest.kt`, `ProductUpdateTest.kt`, `WidgetPinTest.kt` |
| Wiring | `GoldenPathScreen.kt` snackbar collect; `AstroAlarmActivity` theme wrap |

## Tests

- Automated: yes — `AlarmGroupSectionsTest`, donate nudge tests, pin unsupported emits snackbar id
- Coverage: grouped empty omission; first-run donate false

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md` per-feature checklist.

## Notes

- Lockscreen uses `GoldenPathTheme(ThemeMode.Dark)`.
- Do not add a second full-width pin CTA.
