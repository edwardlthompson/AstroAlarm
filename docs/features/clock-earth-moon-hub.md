# Feature: clock-earth-moon-hub

Daily 2D Earth/Moon hub, readable hours, solar-noon pointer, and solar terminator on Earth and Moon.

## Acceptance criteria

- ✅ User-visible behavior: Daily 2D hour labels sit on the pie rim with ticks inside; the now-hand touches those ticks and starts outside the Moon orbit; a red solar-noon ray runs under month/zodiac (hidden if both rings off); Earth is a pole globe at center with the Moon orbiting it (no moon emoji); Daily 3D Moon is a shaded near-side globe on the transit ring; Earth and Moon use a solar day/night terminator (top-down on 2D/Yearly, Earth-view phases on 3D)
- ✅ Offline/error behavior: textures ship in the APK; missing place skips the user pin and still shades from subsolar longitude; missing texture falls back to a colored disk with the same terminator; failed elongation skips the 3D moon globe (wake still draws)
- ✅ Accessibility: existing 2D/3D content descriptions unchanged
- ✅ i18n: N/A — geometry and assets only

## Smoke scenario

1. _Given_ the Daily 2D tab with hour marks, months, and zodiac on
2. _When_ the user looks at the dial without zooming
3. _Then_ hour numbers are readable, Earth faces the Sun, the Moon sits on its orbit with a comet wake, and the red noon pointer reaches the zodiac under the glyphs

## Container map

| Layer | Path |
|-------|------|
| Logic | `DiskRingLayout.kt`, `DiskLabelFit.kt`, `GlobeIllumination.kt`, `MoonShade.kt`, `LunarHub.kt`, `SolarNoonPointer.kt` |
| View | `AstroDiskRenderer.kt`, `Astro3DRenderer.kt`, `EarthGlobeRenderer.kt`, `SolarTermHubRenderer.kt` |
| Tests | `DiskLabelFitTest`, `DiskRingLayoutTest`, `SolarNoonPointerTest`, `GlobeIlluminationTest`, `MoonShadeTest` |
| Wiring | `AstroClockScreen`, `Astro3DClockScreen`, 2D/3D widget providers pass `EarthTexture`/`MoonTexture` |

## Tests

- Automated: yes — layout, illumination, moon cameras, solar-noon pointer, existing Yearly anti-sunward and 3D transit tests

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Moon FOSS map: NASA SVS CGI Moon Kit 1k color (public domain). No GLTF/Filament.
- Libration (~7°) and lunar obliquity (~1.5°) are omitted on purpose.
- Top-down Moon is always ~half lit; Daily 3D uses Earth-view phase from elongation.
