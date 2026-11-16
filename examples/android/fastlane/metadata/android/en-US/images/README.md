# F-Droid / Fastlane images

Canonical listing F-Droid scrapes: `fastlane/metadata/android/en-US/` at the **repository root**. This nested copy is a Golden Path mirror.

| File | Size (typical) |
|------|----------------|
| `icon.png` | 512×512 — exported from `branding/assets/app-icon-512.svg` |
| `featureGraphic.png` | 1024×500 (optional; not shipped yet) |
| `phoneScreenshots/*.png` | real device captures |

`01-alarms.png`, `02-daily.png`, and `03-sky.png` are real screenshots. They are **somewhat outdated** versus current UI. Do not block F-Droid on recapturing them.

Dummy, placeholder, sample, or tiny (≤8px) PNGs fail `scripts/check-fdroid-screenshots.sh`.
