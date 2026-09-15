# Feature: overlay-toggles-a11y

> Overlay switches are 48dp; natal live-body labels are i18n; Sol and Chart get TalkBack custom actions.

## Acceptance criteria

- ✅ User-visible: overlay rows are at least 48dp tall
- ✅ Offline/error: Share custom action on empty natal Snackbars existing empty copy
- ✅ Accessibility: Sol/Chart Next event, Reset zoom, Share; Live Sun/Moon/Mercury have CDs
- ✅ i18n: overlay_live_* en/es/fr

## Smoke scenario

1. _Given_ TalkBack on Sol with zoom applied
2. _When_ Reset zoom runs
3. _Then_ AU zoom returns to 1

## Container map

| Layer | Path |
|-------|------|
| Logic | `OverlayToggle.kt` |
| View | `ZodiacToggleRow.kt`, `SolScreen.kt`, `NatalWheelSection.kt` |
| Tests | `OverlayToggleTest.kt` |
| Wiring | none |

## Tests

- Automated: yes — MIN_DP is 48
- Coverage: constant

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md`.
