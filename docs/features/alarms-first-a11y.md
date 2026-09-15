# Feature: alarms-first-a11y

> TalkBack custom actions on Daily and Yearly wheels, reduced-motion tab/parallax, alarm dots with color and shape, i18n event catalogs, Fastlane title.

## Acceptance criteria

- ✅ User-visible: Fastlane/F-Droid title is AstroAlarm; alarm marks use a second shape besides red circles
- ✅ Offline/error: Next-event action Snackbars when nothing is due
- ✅ Accessibility: customActions Next event / Reset zoom / Share; expand/GPS/wheel steppers have content descriptions; animator scale 0 skips tab animation and 3D tilt
- ✅ i18n: `event_strings.xml` + `a11y_strings.xml` en/es/fr; next-fire subtitle already on rows

## Smoke scenario

1. _Given_ TalkBack on Daily 2D with zoom applied
2. _When_ the user opens custom actions
3. _Then_ Reset zoom returns the wheel to 1× and Share still exports

## Container map

| Layer | Path |
|-------|------|
| Logic | `ReduceMotion.kt`, `AlarmDotMark.kt`, `AstroEventLabels.kt`, `AlarmTargetCopy.kt` |
| View | clock/yearly screens, `AstroWheelPicker.kt`, `AstroLocationCard.kt`, `AstroScreen.kt` |
| Tests | `ReduceMotionTest`, `AlarmDotMarkTest`, `AstroEventLabelsTest` |
| Wiring | Fastlane `title.txt` |

## Tests

- Automated: yes — reduce-motion helper; diamond vs circle marks; Sunrise label from resources
- Coverage: animator scale 0 is reduced motion

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md`.
