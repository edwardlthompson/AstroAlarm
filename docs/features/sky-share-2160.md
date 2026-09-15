# Feature: sky-share-2160

> Share icon on each square sky preview. Exports a 2160×2160 PNG of the current toggles and viewport via FileProvider. No GPS EXIF. No storage permission.

## Acceptance criteria

- ✅ User-visible: 48dp Share on Daily 2D, Daily 3D, Yearly, Sol, and Chart (not the Alarms list)
- ✅ Offline/error: OOM falls back to 1080 then Snackbar; empty natal Snackbar without a black square
- ✅ Accessibility: Share IconButton has a content description
- ✅ i18n: `sky_share_strings.xml` in en/es/fr (not more lines in `strings.xml`)

## Smoke scenario

1. _Given_ Daily 2D with zodiac on and the wheel pinched in
2. _When_ the user taps Share
3. _Then_ the system sheet offers a 2160 PNG that matches those toggles and zoom

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../share/SkyShare.kt`, `SkySharePaint.kt`, `DiskLabelFit.withExportCap` |
| View | `SkyShareButton.kt` on each square preview |
| Tests | `SkyShareTest.kt`, `DiskLabelFitTest` export cap |
| Wiring | `file_paths.xml` `cache-path shares/` |

## Tests

- Automated: yes — 64px PNG magic; zodiac on/off hashes differ; concat zoom ≠ identity; OOM fallback 1080
- Coverage: empty natal does not encode; purge of shares older than 1h

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md`.
