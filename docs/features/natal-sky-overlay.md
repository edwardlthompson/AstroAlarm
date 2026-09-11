# Feature: natal-sky-overlay

> Live Sun/Moon/Mercury on natal wheel with bright event chords and next double/triple caption.

## Acceptance criteria

- ✅ Toggle chips for live Sun/Moon/Mercury
- ✅ Event chords within 3° highlight orb
- ✅ Under-wheel next double / next triple lines

## Smoke scenario

1. _Given_ Chart wheel with profile
2. _When_ sky overlay on
3. _Then_ cyan live marks and status lines appear

## Container map

| Layer | Path |
|-------|------|
| Logic | `NatalEventLinks.kt`, `NatalSkySnapshot` |
| View | `NatalWheelSection.kt` |

## Tests

- Automated: yes — `NatalWheelMathTest` / event link orbs
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
