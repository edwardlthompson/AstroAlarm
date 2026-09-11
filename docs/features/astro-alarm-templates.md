# Feature: astro-alarm-templates

> Chart tab curated templates insert enabled AstroAlarm rows + rescheduleAll.

## Acceptance criteria

- ✅ Templates for Asc∩Sun, Moon return, Solar return, Mercury retro, Moon→rising sign
- ✅ Asc-dependent templates disabled when time unknown
- ✅ No background poller — AlarmManager only

## Smoke scenario

1. _Given_ Chart tab with an active birth profile
2. _When_ user taps Enable on a template
3. _Then_ an enabled alarm row is saved and scheduler reschedules

## Container map

| Layer | Path |
|-------|------|
| Logic | `AstroAlarmTemplates.kt` |
| View | `AstroAlarmTemplatesSection.kt` |

## Tests

- Automated: yes — template list non-empty for a valid profile (unit)
- Coverage: template factory

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
