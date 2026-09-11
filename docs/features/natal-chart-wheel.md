# Feature: natal-chart-wheel

> Chart-tab Canvas natal wheel (Asc-left, whole-sign house numbers, soft natal–natal aspects, live overlay). FOSS alarm-grade.

## Acceptance criteria

- ✅ Active profile shows zodiac ring, **house numbers 1–12** on mid band, natal glyphs, Asc/MC when time known
- ✅ Soft major aspect chords (conj/sextile/square/trine/opp) when size ≥ 280px
- ✅ **Distinct colors** per planet / zodiac / Asc (amber) / MC (violet); live marks match body hue with outline
- ✅ Personal planets full weight; outer planets slightly softer alpha; stellium radial nudge + glyph halo
- ✅ Empty / timeUnknown degraded modes
- ✅ i18n legend strings en/es/fr
- ✅ Tap glyphs for plain-language explain sheet (see `natal-glyph-explain.md`)

## Smoke scenario

1. _Given_ Chart tab with known-time profile
2. _When_ wheel renders
3. _Then_ Asc spoke at left, house “1” in rising sign band, natal Sun glyph visible

## Container map

| Layer | Path |
|-------|------|
| Logic | `NatalAspectMath.kt`, `NatalWheelLayout.kt`, `NatalWheelHousePaint.kt`, `NatalWheelStellium.kt`, `NatalWheelHitTest.kt`, `NatalGlyphPalette.kt` |
| View | `NatalWheelSection.kt`, `NatalWheelRenderer.kt`, `NatalGlyphExplain*.kt` |
| Tests | `NatalWheelMathTest` (layout / houses / stellium / hit-test / palette) |

## Tests

- Automated: yes — Asc-left angle; house 1 mid; stellium radius; aspect size gate; Asc/MC/Sun hit-test; unique body/zodiac colors
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
