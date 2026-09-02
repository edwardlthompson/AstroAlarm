# Feature: yearly-sol-align

> Align pin/captions across disk tabs; nod Daily 3D Earth by solar declination while the pin stays center; tighter compact jieqi; N/S on pole traces; Sol perihelion caption; non-scrolling tabs.

## Acceptance criteria

- ✅ User-visible behavior: Daily 2D/3D, Yearly, and Sol share pin Y and first-caption Y (`DiskChrome.Reserve`). Daily 3D globe+axis+tracks canvas-roll by `|sunDec|` toward/away from the Sun; pin stays at disk center; sun/moon rings do not roll. Compact jieqi band is glyph-tall. Green north trace is farther from the Sun at December; N/S sit on the traces at the solstices (no stick through Earth). Top tabs are equal-width and do not scroll. Sol caption states yellow + is perihelion.
- ✅ Offline/error behavior: `rollDeg` is 0 if the sun is missing, `|sunDec| < 0.5`, or the pole/subsolar disk vectors are degenerate
- ✅ Accessibility: Daily 2D/3D tabs keep a “Daily 2D/3D” content description when the visible label collapses on a narrow width
- ✅ i18n: `sol_caption` perihelion clause in en/es/fr

## Smoke scenario

1. _Given_ a saved city and the five astro tabs
2. _When_ the user pages Daily 2D → 3D → Yearly → Sol
3. _Then_ Add Widget lines up, Compact leaves a thin emoji ring, Yearly shows N/S on the green/red traces without a stick through Earth, and Sol’s caption mentions perihelion

## Container map

| Layer | Path |
|-------|------|
| Logic | `GlobeObliquity.kt`, `SolarTermAxisOverlay.kt`, `SolarTermWheelRenderer.innerFrac` |
| View | disk screens, `Astro3DRenderer.kt`, `SolarTermPoleLabels.kt`, `AstroScreen.kt` |
| Tests | `GlobeObliquityTest`, `SolarTermAxisOverlayTest`, `SolarTermWheelRenderTest` |
| Wiring | renderer/tab calls only |

## Tests

- Automated: yes — June/December roll sign; polar/equinox zero; north farther at lon 270; compact emoji in band; compact Lìchūn hit

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md` per-feature checklist.

## Notes

- Rings stay local-sky. After the globe nod, the gold sun glyph need not sit on the rotated subsolar dot.
- Compact `innerFrac` is shared with `sectorAt`.
