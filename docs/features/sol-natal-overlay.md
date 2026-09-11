# Feature: sol-natal-overlay

> Sol tab toggle draws heliocentric birth-epoch ghost planets (not geocentric aspect chords).

## Acceptance criteria

- ✅ Toggle “Natal ghosts” when a birth profile exists
- ✅ Ghosts at Kepler positions for birth Instant
- ✅ Alarm dots unchanged

## Smoke scenario

1. _Given_ Sol tab + active birth profile
2. _When_ Natal ghosts enabled
3. _Then_ pink outline markers appear near orbits

## Container map

| Layer | Path |
|-------|------|
| Logic | `SolNatalOverlay.kt` |
| View | `SolScreen` toggle + `SolRenderer` |

## Tests

- Automated: yes — overlay no-op without profile (manual draw guard)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
