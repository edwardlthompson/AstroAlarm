# Feature: empty-alarms-mark

> Empty Alarms list shows the brand disk mark above “Tap +”.

## Acceptance criteria

- ✅ User-visible: empty list shows `ic_brand_mark` then `astro_empty_all`
- ✅ Offline/error: N/A
- ✅ Accessibility: mark is decorative; empty copy remains the accessible name
- ✅ i18n: no new keys

## Smoke scenario

1. _Given_ no alarms
2. _When_ the user opens Alarms
3. _Then_ the disk mark sits above Tap +

## Container map

| Layer | Path |
|-------|------|
| Logic | `ui/EmptyAlarmsMark.kt` |
| View | `AstroAlarmsPage.kt` |
| Tests | `EmptyAlarmsMarkTest.kt` |
| Wiring | none |

## Tests

- Automated: yes — drawable is `ic_brand_mark`, size 96
- Coverage: constant

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md`.
