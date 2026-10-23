# Feature: wear-watchface

FOSS Wear OS analog watch face styled like Daily 2D: now-up red hand and an optional simple day/night pie from the device `ZoneId` (no phone sync in v1).

## Acceptance criteria

- ✅ User-visible behavior: Wear OS picker shows AstroAlarm Daily face; interactive mode draws a day/night pie with the red now-hand fixed at the top (Daily 2D now-up)
- ✅ Offline/error behavior: face uses device `ZoneId` only; no wearable data layer / phone sync required; ambient mode dims the pie and keeps the hand
- ✅ Accessibility: watch-face label and picker preview are present; TalkBack uses the system watch-face picker chrome
- ✅ i18n: `wear_watch_face_name` (en; es/fr can follow phone parity later)

## Smoke scenario

1. _Given_ `:wear` debug APK installed on a Wear OS emulator or watch
2. _When_ the user selects AstroAlarm Daily in the watch-face picker
3. _Then_ the red hand stays at 12 o’clock and the day sector rotates under it with civil time

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/wear/.../WearCivilAngles.kt`, `WearDayNightApprox.kt`, `WearDialLayout.kt` |
| View | `AstroWearWatchFaceService.kt`, `WearDialCanvas.kt` |
| Tests | `WearCivilAnglesTest`, `WearDayNightApproxTest`, `WearDialLayoutTest` |
| Wiring | `examples/android/settings.gradle.kts` `include(":wear")`, wear `AndroidManifest.xml` |
## Tests

- Automated: yes — civil angle / now-up hand / screen degrees; approximate rise–set minutes from `ZoneId`; sweep helper

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android` (covers `:wear:test` via Android gate)

## Definition of Done

See `docs/FEATURE_MODULES.md` per-feature checklist and BUILD_PLAN Sprint 2+ feature template rows.

## Notes

- Depends on `androidx.wear.watchface:watchface` (Apache-2.0) only — no Play Services, no GMS wearable data layer, no HMS.
- Day/night pie is a ZoneId-based seasonal approximation (no lat/lon in v1); phone place sync is a later enhancement.
- After each AGENT step: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## [HUMAN] follow-up — Huawei LiteOS / Harmony

Ship a `.hwt` watch-face package for Huawei LiteOS/Harmony **without** HMS or other proprietary Huawei SDKs. Keep FOSS tooling and assets only; do not add HMS Core, Huawei Account, or closed watch-face SDKs to this repo. Track packaging, sideload, and device validation as a human distribution task.
