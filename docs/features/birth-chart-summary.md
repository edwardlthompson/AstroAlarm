# Feature: birth-chart-summary

> Chart tab summary of Sun / Rising / Moon / MC for the active birth profile.

## Acceptance criteria

- ✅ Shows Sun, Moon, Rising, MC when computable
- ✅ Warns on unknown time, polar latitude, zone fallback
- ✅ Empty state when no profile

## Smoke scenario

1. _Given_ Chart tab with a saved profile
2. _When_ summary renders
3. _Then_ Sun/Moon/Rising lines appear (Rising blank if time unknown)

## Container map

| Layer | Path |
|-------|------|
| View | `ui/birth/BirthChartSummary.kt` |
| Logic | `BirthChartCalculator` |

## Tests

- Automated: yes — covered via AscendantMath / chart compute guards
- Coverage: chart null Asc when time unknown

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
