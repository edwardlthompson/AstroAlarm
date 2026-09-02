# Feature: clock-3d-transit-ticks

Ticks on the 3D sun and moon transit rings for the day's main events.

## Acceptance criteria

- ✅ User-visible behavior: 3D clock marks solar midnight, sunrise, solar noon, and sunset on the sun ring from the same hour angle as the alarm engine (noon in line with the location pin; southern noon toward geographic north). Moonrise/moonset ticks at the moon-ring horizon; a teal tick at lunar culmination (local meridian). Yellow/white ground-track rings and dots on the facing hemisphere show the subsolar and sublunar paths. A thin geographic N–S spindle uses the same orthographic camera as those tracks, then the globe group nods by `|sunDec|` around the pin. Sun and moon rings use a comet wake (gap ahead, thick behind); zodiac stays even.
- ✅ Offline/error behavior: missing place draws no ticks; polar missing sunrise still draws other solar marks that calculate
- ✅ Accessibility: existing 3D content description unchanged
- ✅ i18n: N/A — graphic marks only

## Smoke scenario

1. _Given_ a saved city on the 3D tab
2. _When_ the globe renders
3. _Then_ the gold solar-noon tick sits at the front of the sun ring (in line with the red location pin), a teal tick marks lunar culmination on the moon ring, and outlined white ticks mark moonrise and moonset

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../widget/TransitTicks.kt` |
| View | `Astro3DTransitOverlay.kt`, `Astro3DRenderer.kt`, `Astro3DRingWake.kt`, `GlobeAxis.kt`, `GlobeObliquity.kt` |
| Tests | `TransitTicksTest.kt`, `GlobeGroundTracksTest.kt` |
| Wiring | renderer call only |
## Tests

- ✅ Automated: yes — `TransitTicksTest.kt`, `ClockParallaxTest.kt`, `ZodiacGlyphTest.kt`, `GlobeGroundTracksTest.kt`

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
