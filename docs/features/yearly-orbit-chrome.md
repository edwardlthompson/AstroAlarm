# Feature: yearly-orbit-chrome

> Freeze the jieqi ring (Lìchūn at 12). Earth, needle, and true anomaly travel CCW with the year. Daily 3D geographic axis, Yearly tilt axis + green spiral, tab chrome: preview then pin then toggles.

## Acceptance criteria

- ✅ User-visible behavior: Lìchūn stays at 12 o’clock; Earth and the needle walk CCW; January AU still closer than July; compact glyphs sit on the color-band midline; Daily 2D/3D/Yearly/Sol squares top-align with pin and first caption sharing Y; Alarms pin at top, list, Next due/Grouped last; Daily 3D nods globe+axis+tracks by solar declination around the pin; Yearly green north / red south traces with N/S at the solstices (no stick through Earth)
- ✅ Offline/error behavior: Invalid place skips the Yearly user pin; Daily 3D still defaults NYC-ish lat/lon for the globe camera
- ✅ Accessibility: existing Yearly TalkBack and 3D content descriptions unchanged
- ✅ i18n: N/A — chrome order only; N/S ticks are letters

## Smoke scenario

1. _Given_ a saved city on Yearly in June
2. _When_ the user opens Yearly then Daily 3D
3. _Then_ Lìchūn is still at 12, Earth is not at 12, the green spiral is visible, and the globe shows an N–S stick under the sun/moon tracks

## Container map

| Layer | Path |
|-------|------|
| Logic | `SolarTermLayout.kt`, `GlobeAxis.kt`, `SolarTermAxisOverlay.kt` |
| View | `SolarTermWheelRenderer.kt`, `Astro3DRenderer.kt`, tab screens |
| Tests | `SolarTermLayoutTest`, `SolarTermWheelRenderTest`, `SolarTermAxisOverlayTest`, `GlobeGroundTracksTest` |
| Wiring | renderer calls only |

## Tests

- Automated: yes — frozen ring, Yǔshuǐ CCW, June Earth not at 12, January AU, NYC north pole y, June sunDec vs equator, NH June/December sunward, spiral in/out at ν=90/270; existing noon/west track tests

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md` per-feature checklist.

## Notes

- True perihelion stays in January; do not invert the ellipse.
- Ground-track formulas in `GlobeGroundTracks` are unchanged.
- Follow-up chrome (crosshairs, radial labels, city-only location, Sol Kepler widget, wakes) lives in `yearly-sol-learn.md`.
