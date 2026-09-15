# Feature: sky-hand-tick

> 24h hand crossing noon ticks; Yearly tap flashes the sector 150ms.

## Acceptance criteria

- ✅ User-visible: Yearly sector stroke flashes 150ms on tap; Daily 2D haptic when the sun hour-angle crosses noon if motion is allowed
- ✅ Offline/error: reduced-motion skips haptic; missing place skips tick
- ✅ Accessibility: flash is visual-only; TalkBack still uses the term sheet
- ✅ i18n: none

## Smoke scenario

1. _Given_ Yearly wheel
2. _When_ the user taps a jieqi sector
3. _Then_ the sector outline flashes then the sheet opens

## Container map

| Layer | Path |
|-------|------|
| Logic | `SkyHandTick.kt`, `YearlyHighlight.kt` |
| View | `AstroClockScreen.kt`, `SolarTermScreen.kt`, `SolarTermWheelRenderer.kt` |
| Tests | `SkyHandTickTest.kt` |
| Wiring | none |

## Tests

- Automated: yes — crossed noon; highlight duration 150
- Coverage: wrap-around 359→1

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md`.
