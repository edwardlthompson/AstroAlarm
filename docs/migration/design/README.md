# Design captures

Provenance for files next to `docs/migration/DESIGN_BRIEF.md`.

| File | Source | Notes |
|------|--------|-------|
| `01-alarms.png` | Fastlane `examples/android/fastlane/metadata/android/en-US/images/phoneScreenshots/01-alarms.png` | Dark Alarms tab, ~2026-09-15. **Stale vs current code:** no Tonight sky card; Edit/Delete were text buttons (now icon buttons). |
| `02-daily.png` | Fastlane `02-daily.png` | Dark Daily 2D. **Stale vs current code:** 2D center is an Earth globe here; `DiskCenterHub` now draws a plain night hub. Overlay list in this shot omits “Solar noon up”. |
| `03-sky.png` | Fastlane `03-sky.png` | Dark Sky Yearly (jieqi). Matches Yearly layout more closely. |
| `token-swatches.svg` | Generated from `design-tokens/design-tokens.json` plus widget XML hex | Exact hex, not a device screenshot. |
| `app-icon-512.svg` | `branding/assets/app-icon-512.svg` | Launcher mark. |
| `splash.svg` | `branding/assets/splash.svg` | Brand splash art; **not** wired as a splash Activity. |

No emulator or Compose preview renderer was available in the Phase 1 environment (`ANDROID_HOME` unset). Live lockscreen, Settings, editor, 3D, Sol, Chart, and widget picker shots are **missing** — reproduce from the brief + sources, not from these three Fastlane frames.
