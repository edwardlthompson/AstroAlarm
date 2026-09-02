# Feature: yearly-sol-labels

> Unify pin-button copy; place the Yearly Moon on the ecliptic; keep N/S off the pole traces; put jieqi name and date on opposite inset edges; add Sol teaching chrome. Daily 3D ground tracks stay glued to Earth.

## Acceptance criteria

- ✅ User-visible behavior: All five pin buttons read **Add Widget to Home Screen** (es/fr equivalents). Yearly Moon sits at ecliptic longitude (`sun lon + elongation`) with a 180° hub offset so full moon is anti-sunward (far side of Earth from the Sun). N is outside the green ring at winter solstice and inside at summer; S is inside the red ring at winter and outside at summer. Non-compact jieqi name and date sit on opposite inset wedge edges, larger and both bold; names wrap in the band and stop outside the emoji. Sol draws faint sun–planet spokes, a 1 AU scale bar, opposition ticks on Mars–Neptune, a local date, and tap readouts (AU, ν, light-time; Earth also perihelion/aphelion).
- ✅ Offline/error behavior: Moon illumination failure falls back to mean ecliptic longitude `218.316 + 13.176396 * d`; Daily sky path is unchanged
- ✅ Accessibility: Each pin button keeps a distinct TalkBack `contentDescription` (clock / 3D / alarms / Yearly / Sol)
- ✅ i18n: unified pin strings plus `sol_scale_au`, `sol_light_min`, `sol_perihelion`, `sol_aphelion` in en/es/fr

## Smoke scenario

1. _Given_ the five astro tabs and a saved city
2. _When_ the user pins from Daily 2D, Daily 3D, Alarms, Yearly, and Sol, then opens Yearly at full moon and taps Earth on Sol
3. _Then_ pin labels match, TalkBack still names each widget, the Yearly Moon is opposite the Sun at full, N/S do not sit on the traces, jieqi name and date are on opposite sides of each wedge, and Sol shows date, AU bar, spokes, and a perihelion readout

## Container map

| Layer | Path |
|-------|------|
| Logic | `LunarCalculator.eclipticLon`, `SolChrome.kt`, `SolarTermPoleLabels.kt`, `SolarTermRadialLabels.kt` |
| View | pin buttons, `SolarTermHubRenderer.drawMoon`, `SolScreen.kt`, `SolRenderer.kt` |
| Tests | `SolarAndLunarCalculatorTest`, `SolarTermAxisOverlayTest`, `SolarTermWheelRenderTest`, `SolRendererTest` |
| Wiring | existing screens only |

## Tests

- Automated: yes — full-moon elongation ≈ 180°; new-moon ≈ 0°; `|rN − green|` and `|rS − red|` > half glyph; N outside green in winter / inside in summer; S inside red in winter / outside in summer; index-0 name angle ≠ date and closer to `midDeg(0)` than `midDeg(23)`; wrap “Beginning of Spring”; 2023-08-02 full moon anti-sunward on hub; Mars opposition `geoLon ≈ 180°`; Earth apsides months

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md` per-feature checklist.

## Notes

- Daily 3D yellow/white ground tracks share the globe camera and `canvas.rotate` around the pin. They stay on the continents. Rings stay outside that rotate (local-sky clock). The gold sun on the transit ring is not meant to sit on the subsolar parallel. Do not un-rotate the tracks.
- Yearly is a heliocentric calendar hub. Do not use Daily hour angle for the Yearly Moon. Geocentric ecliptic lon as a canvas offset from Earth is 180° off: Earth sits at the Sun’s apparent lon, so full moon is anti-sunward (`canvasDeg(moonLon) + 180`).
- Jieqi names wrap up to three lines in the band; the inner stop stays outside the emoji radius.
- Sol light-time is geometric (`AU × 8.317` minutes); positions stay Kepler geometric.
