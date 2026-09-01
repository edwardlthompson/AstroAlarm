# Feature: clock-3d-moon-sky

Scientific sun and moon positions and event times for the 2D/3D clocks and alarms.

The 3D view stays an Earth-centered transit clock (observer pin, schematic rings), not a planetarium. Fake background stars and tropical zodiac bubbles are still schematic.

## Acceptance criteria

- ✅ User-visible behavior: 2D disk and 3D rings place the sun and moon from the same topocentric hour angle and declination as the alarm engine. Northern-hemisphere transit sits toward geographic south; southern-hemisphere transit sits toward geographic north. Polar days omit rise/set instead of inventing a time
- ✅ Offline/error behavior: all positions and event times are on-device via Apache-2.0 `commons-suncalc` 3.11; NOAA `SolarMath` remains only for tropical sun longitude (zodiac + season search). Missing place draws no body. Observer elevation is sea level (~1 min sunrise bias)
- ✅ Accessibility: existing 2D/3D content descriptions unchanged
- ✅ i18n: N/A — geometry only

## Smoke scenario

1. _Given_ a saved city in the southern hemisphere on the 3D tab
2. _When_ solar noon arrives
3. _Then_ the sun tick sits toward the top of the globe (geographic north), and a solar-noon alarm for that day uses the same Instant as the picture

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../astro/sky/`, `SolarCalculator.kt`, `LunarCalculator.kt`, `SolarSeasons.kt` |
| View | `Astro3DRenderer.kt`, `AstroDiskRenderer.kt`, `TransitTicks.kt`, `GlobeGroundTracks.kt` |
| Tests | `BodySkyTest.kt`, `SkyBodiesTest.kt`, `TransitTicksTest.kt`, `AstroNextFireTest.kt` |
| Wiring | existing renderer calls only |

## Tests

- ✅ Automated: yes — `SkyBodiesTest.kt` (NH/SH, polar, DST Instant, leap day, year boundary, dateline, moon transit, Tokyo golden, seasons, moon age), `BodySkyTest.kt`, `AstroNextFireTest.kt`

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Civil rise/set use suncalc apparent horizon; ring geometry uses unrefracted true altitude
- 3D is not a sky map: star field is decorative; zodiac bubbles are tropical longitude, not IAU constellations
