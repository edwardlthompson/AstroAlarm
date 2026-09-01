# Feature: clock-2d-plain-center

The 2D clock center is a plain hub. The Earth globe stays on the 3D clock only.

## Acceptance criteria

- 🔲 User-visible behavior: 2D in-app clock and 2D home widget show a small plain hub at center, not the Earth texture
- 🔲 Offline/error behavior: rendering stays local; missing place still draws the hub
- 🔲 Accessibility: existing widget/content description is unchanged (no new strings)
- 🔲 i18n: N/A — no user-facing strings

## Smoke scenario

1. _Given_ the 2D clock tab is open
2. _When_ the dial renders
3. _Then_ the center is a plain cap, not a globe, and the current-time hand still meets the hub

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../widget/DiskCenterHub.kt` |
| View | `AstroDiskRenderer.kt`, `AstroDiskOverlays.kt` |
| Tests | `examples/android/app/src/test/.../widget/DiskCenterHubTest.kt` |
| Wiring | `AstroClockScreen.kt`, `AstroClockWidgetProvider.kt` drop unused earth texture |

## Tests

- Automated: yes — `DiskCenterHubTest.kt`

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- 3D clock still uses `EarthGlobeRenderer`
