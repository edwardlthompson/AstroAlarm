# Feature: natal-align-widget

> Home widget showing next chart double and triple for the active birth profile.

## Acceptance criteria

- ✅ Profile label + next double + next triple lines
- ✅ Empty state without profile
- ✅ Refreshes with `AstroAlarmScheduler` widget refresh

## Smoke scenario

1. _Given_ birth profile saved
2. _When_ Chart doubles widget added
3. _Then_ next double/triple text appears

## Container map

| Layer | Path |
|-------|------|
| Logic | `NatalAlignWidgetCopy.kt` |
| View | `NatalAlignWidgetProvider`, `widget_natal_align.xml` |

## Tests

- Automated: yes — copy builder empty profile
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
