# Feature: circular-disk

> Yearly, Sol burst, natal, and Daily disks are circular; corners of the square bitmap stay transparent.

## Acceptance criteria

- ✅ User-visible: no black plate outside the ring on Yearly, Sol, natal, Daily 2D/3D, widgets, or picker previews
- ✅ Offline/error: empty natal widget still draws a circular disk
- ✅ Accessibility: TalkBack content descriptions unchanged
- ✅ i18n: none

## Smoke scenario

1. _Given_ Sky Yearly, Sol, Chart, and a natal chart widget
2. _When_ the disk paints on a light or home-screen wallpaper
3. _Then_ only the wheel is opaque; corners show the surface or wallpaper

## Container map

| Layer | Path |
|-------|------|
| Logic | `ui/WheelDisk.kt` |
| View | Yearly/Sol/natal/Daily renderers, widget previews, `widget_astro.xml` |
| Tests | `ui/WheelDiskTest.kt` |
| Wiring | none |

## Tests

- Automated: yes — `WheelDiskTest.kt` radius math, ARGB render, no square `drawColor`
- Coverage: Yearly, Sol, natal, Daily 2D/3D, empty natal widget; Robolectric does not rasterize pixels

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md`.
