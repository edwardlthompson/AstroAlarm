# Feature: natal-compound-alarm

> Specific and any double/triple natal story-event alarms (FOSS on-device).

## Acceptance criteria

- ✅ `NatalCompoundSpecific` (2–3 kinds) and `NatalCompoundAny` (arity 2|3)
- ✅ Templates for any double / any triple
- ✅ Missing Asc kinds blocked when time unknown
- ✅ Distinct from AllPlanetsAlign copy

## Smoke scenario

1. _Given_ active birth profile
2. _When_ user enables Any chart double template
3. _Then_ alarm schedules via AlarmManager

## Container map

| Layer | Path |
|-------|------|
| Logic | `NatalCompoundNext.kt`, `NatalAlarmFire`, model targets |
| View | templates + editor natal category |
| Tests | `NatalCompoundNextTest` |

## Tests

- Automated: yes
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
