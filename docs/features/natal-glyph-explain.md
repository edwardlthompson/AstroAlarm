# Feature: natal-glyph-explain

> Tap any natal-wheel glyph (planets, zodiac, houses, Asc/MC, live marks) for a plain-language bottom sheet.

## Acceptance criteria

- ✅ Tap near Asc (amber) or MC (**violet**) spoke opens explain sheet with Midheaven/Ascendant copy
- ✅ Tap natal planet, live ☉☽☿ (outlined, body-colored), zodiac glyph, or house number opens matching copy
- ✅ Hint under wheel: tap any symbol; FOSS alarm-grade disclaimer on sheet
- ✅ i18n en/es/fr for explain strings

## Smoke scenario

1. _Given_ Chart tab with known-time profile
2. _When_ user taps the blue MC spoke
3. _Then_ sheet titled Midheaven (MC) explains culminating ecliptic / career-public themes; teal IC spoke is opposite

## Container map

| Layer | Path |
|-------|------|
| Logic | `NatalWheelHitTest.kt` |
| View | `NatalGlyphExplain.kt`, `NatalGlyphExplainSheet.kt`, `NatalWheelSection.kt` |
| i18n | `res/values*/natal_explain_strings.xml` |
| Tests | `NatalWheelHitTestTest` in `NatalWheelMathTest.kt` |

## Tests

- Automated: yes — Asc spoke, MC spoke, natal Sun point hits
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
