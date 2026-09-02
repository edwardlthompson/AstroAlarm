# Feature: yearly-sol-learn

> Top-align Daily/Yearly/Sol squares. Yearly hub crosshairs, dual pole traces, radial jieqi labels, city name only. Sol true Kepler orbits plus home widget. Shared comet-style wakes.

## Acceptance criteria

- ✅ User-visible behavior: Daily 2D/3D, Yearly, and Sol squares sit at the top of the tab (same size when paging). Yearly draws faint VH + solstice + equinox diameters behind hub content; green north and red south pole traces with larger amplitude; non-compact name/date along dividing lines reading inward; emoji on the inner rim; location is the city name only. Sol strokes true Kepler polylines (real *e*) with perihelion ticks, ♈, pin, and a home widget. Comet wakes (gap ahead, thick behind) on Sol planets, Yearly Earth, and Daily 3D sun/moon.
- ✅ Offline/error behavior: Invalid place falls back to the device-zone string; Sol widget still renders with `place = null`; missing globe textures fall back to disks
- ✅ Accessibility: existing Yearly TalkBack and Sol `sol_cd` unchanged; Sol widget uses `sol_cd`
- ✅ i18n: `sol_pin_widget`, `sol_widget_name`, `sol_widget_desc` (en/es/fr); `solar_term_at_location` / `_at_coords` are city/coords only

## Smoke scenario

1. _Given_ a saved city and the five astro tabs
2. _When_ the user swipes Daily 2D → 3D → Yearly → Sol
3. _Then_ the disks share a top edge, Yearly shows dual traces and radial type, Sol orbits are eccentric (Mercury more than Earth), and a wake sits behind each moving body

## Container map

| Layer | Path |
|-------|------|
| Logic | `OrbitWake.kt`, `PlanetKepler.helioXy`, `SolarTermAxisOverlay`, `SolarTermFormat.locationLabel` |
| View | `SolarTermCrosshairs.kt`, `SolarTermRadialLabels.kt`, `SolOrbitPaths.kt`, `Astro3DRingWake.kt`, `SolWidgetProvider.kt`, tab screens |
| Tests | `OrbitWakeTest`, `SolRendererTest`, `SolarTermFormatTest`, `SolarTermAxisOverlayTest` |
| Wiring | `AndroidManifest.xml` Sol widget receiver; `AstroAlarmScheduler` `SolWidgetProvider.updateAll` |

## Tests

- Automated: yes — city-only location label; south spiral inside polar at June; Mercury orbit span > Earth; Earth January AU < July; `OrbitWake` gap/width; existing Yearly winding tests

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md` per-feature checklist.

## Notes

- Do not copy Yearly `E_VIS = 0.22` onto Sol. Earth stays nearly round; Mercury and Mars teach eccentricity.
- Wake `aheadRad` is 0 at the body and increases in the direction of travel. Gap ≈ 28°.
- Daily 3D zodiac rings stay even; only sun and moon use the wake, split back then front around the globe.
