# Feature: sol-system

> Fifth tab **Sol**: 2D true-AU heliocentric map, planets only (no moons), pinch-zoom, Kepler orbit polylines, pin + home widget, and planet-alarm dots.

## Acceptance criteria

- ✅ User-visible behavior: Sol tab shows Mercury–Neptune at Kepler positions on a true-AU plane; projected Kepler polylines (real *e*) with perihelion ticks and comet wakes; ♈ / vernal tick on +X (J2000); pinch zoom reaches Neptune; each body is at least ~4 px; Sun is a bright disk; no moons; pin + home widget; enabled Planet / PlanetAlign / AllPlanetsAlign alarms draw red dots at `nextInstant`
- ✅ Offline/error behavior: Missing textures fall back to `EarthGlobeRenderer` colored disks; Kepler is on-device (no network); rise/set dots omit when `nextInstant` is null
- ✅ Accessibility: content description `sol_cd`; caption explains true-AU, no moons, and that yellow + is perihelion
- ✅ i18n: `astro_tab_sol`, `sol_caption`, `sol_hint`, `sol_cd`, `sol_pin_widget`, `sol_widget_name`, `sol_widget_desc` (en/es/fr)

## Smoke scenario

1. _Given_ AstroAlarm is open with a Planet alarm enabled
2. _When_ the user opens Sol and pinches out
3. _Then_ outer planets remain findable, a red alarm dot sits on the relevant orbit, and tapping a disk shows name + AU

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/app/src/main/java/org/astroalarm/sol/` |
| View | `examples/android/app/src/main/java/org/astroalarm/ui/sol/` |
| Tests | `PlanetKeplerTest`, `SolRendererTest` |
| Wiring | `AstroScreen.kt` page 4 |

## Tests

- Automated: yes — Mercury AU < Earth < Jupiter; Sol bitmap smoke; planet-alarm render; Mercury retrograde sign flip; Mercury orbit span exceeds Earth; January Earth AU < July

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- FOSS NASA/JPL-style maps in `drawable-nodpi` (downsampled). No proprietary SDKs.
- Moons are omitted on Sol by product choice; Earth’s Moon remains on Yearly.
- Sol no longer draws AU-circles at today’s radius; orbits are sampled true-anomaly polylines.
