# Feature: brand-voice

> Real AstroAlarm voice and Fastlane/F-Droid listing copy from `branding/product.json`. No store PNGs.

## Acceptance criteria

- ✅ User-visible: Fastlane and F-Droid en-US short/full descriptions match the product tagline and pitch
- ✅ Offline/error: copy is static metadata; missing files fail the store-copy test
- ✅ Accessibility: N/A (store text)
- ✅ i18n: en-US listing only this row (app UI catalogs unchanged)

## Smoke scenario

1. _Given_ `product.json` tagline
2. _When_ Fastlane `short_description.txt` is read
3. _Then_ it is the tagline and full text is not Golden Path stub copy

## Container map

| Layer | Path |
|-------|------|
| Logic | `branding/voice.md`, `branding/product.json` |
| View | `examples/android/fastlane/metadata/android/en-US/`, `examples/android/metadata/en-US/` |
| Tests | `StoreCopyTest.kt` |
| Wiring | none |

## Tests

- Automated: yes — tagline match; no Golden Path Android stub
- Coverage: Fastlane and F-Droid mirrors

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md`.
