# Feature: math-difficulty

> Lockscreen math unlock: persist difficulty with `commit()`, Elementary single-digit `a ± b`, drop Genius.

## Acceptance criteria

- ✅ User-visible behavior: Settings chips are Elementary, Easy, Medium, Hard (single-select); Elementary is one-digit addition/subtraction with non-negative results
- ✅ Offline/error behavior: Stored `GENIUS` maps to Hard; writes use `SharedPreferences.commit()` on `applicationContext`
- ✅ Accessibility: existing math Settings chips and TalkBack labels
- ✅ i18n: `astro_math_diff_elementary` in en/es/fr; Genius string removed

## Smoke scenario

1. _Given_ Settings → Math Alarm Stop Challenge
2. _When_ the user picks Elementary and leaves Settings
3. _Then_ a new `MathPreferences` instance reads Elementary

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/app/src/main/java/org/astroalarm/math/` |
| View | `examples/android/app/src/main/java/org/astroalarm/ui/math/MathSettingsSection.kt` |
| Tests | `examples/android/app/src/test/java/org/astroalarm/math/` |
| Wiring | Settings screen existing math section |

## Tests

- Automated: yes — `MathPreferencesTest`, `MathProblemGeneratorTest`

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Four levels stay chips; switches do not fit mutually exclusive difficulties.
