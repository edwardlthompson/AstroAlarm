# Feature: clock-2d-noon-up

Daily 2D option to lock solar noon at the top of the dial so daylight stays up and the now-hand moves.

## Acceptance criteria

- ✅ User-visible behavior: pref `solar_noon_up_2d` (default off) keeps current now-up dial; when on, solar noon stays at 12 o’clock, the red now-hand moves with civil time, and the extra noon ray is hidden
- ✅ Offline/error behavior: missing place or solar noon falls back to now-up
- ✅ Accessibility: toggle labeled for TalkBack with the other Daily 2D overlays
- ✅ i18n: `astro_toggle_solar_noon_up` (en/es/fr)

## Smoke scenario

1. _Given_ Daily 2D with a valid city and noon-up on
2. _When_ it is not solar noon
3. _Then_ the red hand is not at the top and the bright day sector sits in the upper half

## Container map

| Layer | Path |
|-------|------|
| Logic | `DiskAngleAnchor.kt`, `AstroDiskRenderer.kt`, `AstroDiskBodies.kt`, `CivilHourMarks.kt` |
| View | `AstroDisplayPreferences.kt`, `AstroClockScreen.kt`, `ZodiacToggleRow.kt` |
| Tests | `DiskAngleAnchorTest`, `AstroDisplayPreferencesTest` |
| Wiring | `AstroClockWidgetProvider` |
## Tests

- Automated: yes — noon-up locks noon at `-90°`; hand angle leaves top when civil ≠ noon; missing noon falls back

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
