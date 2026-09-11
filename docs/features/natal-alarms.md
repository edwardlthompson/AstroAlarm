# Feature: natal-alarms

> Natal* AlarmTarget kinds + AstroNextFire via NatalAlarmFire / NatalNext.

## Acceptance criteria

- ✅ Natal Asc/MC aspects, Moon/Solar return, Mercury station, Moon rising-sign ingress
- ✅ Editor Natal chip when an active birth profile exists
- ✅ Missing profile → null next fire (fail-soft)

## Smoke scenario

1. _Given_ an active birth profile with known time
2. _When_ user adds a Natal Asc aspect alarm
3. _Then_ next-fire resolves and the alarm appears in the list

## Container map

| Layer | Path |
|-------|------|
| Logic | `NatalNext.kt`, `NatalAlarmFire.kt`, `AstroNextFire`, `AstroAlarmTargetJson` |
| View | Natal chip in `TargetTypeSelector` |
| Tests | `NatalNextTest` |

## Tests

- Automated: yes — aspect target math / mercury station wiring
- Coverage: pure next-fire helpers

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
