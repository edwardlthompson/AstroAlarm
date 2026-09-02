# Feature: yearly-sol-polish

> Stack Daily tab labels; locale jieqi names with auto-southern remap; compact hub; Earth at 12 o’clock; Sol rings and alarm dots.

## Acceptance criteria

- ✅ User-visible behavior: Daily 2D/3D tabs stack “Daily” over “2D”/“3D”; Yearly drops Traditional/Local switches (Compact remains); names follow device locale (zh pack for hanzi); southern latitudes remap names/colors by 12 terms; compact hub is larger with emoji on the inner rim; jieqi stamps are `MMM d HH:mm`; Earth sits under the now-up needle; user pin faces the hub Sun; Sol shows Kepler orbits, a ♈ tick, and red planet-alarm dots; Yearly plots red dots on armed jieqi sectors
- ✅ Offline/error behavior: Invalid place skips the pin and treats latitude 0 as north; Sol rise/set dots omit when `nextInstant` is null; globe rasterize falls back to a disk
- ✅ Accessibility: TalkBack on the Yearly wheel uses pinyin + locale name + local stamp (no year)
- ✅ i18n: `astro_tab_daily`, `astro_tab_2d`, `astro_tab_3d` (en/es/fr); `values-zh/solar_terms.xml` simplified names

## Smoke scenario

1. _Given_ a saved city (north or south) and at least one Seasonal and one Planet alarm
2. _When_ the user opens Yearly then Sol
3. _Then_ Lìchūn is at 12 o’clock, Compact still works, and red dots appear on the armed jieqi sector and planet orbits

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/app/src/main/java/org/astroalarm/solarterm/`, `.../astro/sun/SolarMath.kt` |
| View | `examples/android/app/src/main/java/org/astroalarm/ui/solarterm/`, `.../ui/sol/` |
| Tests | `SolarTermWheelRenderTest`, `SolarTermFormatTest`, `EarthGlobePinTest`, `SolRendererTest` |
| Wiring | `AstroScreen.kt` pages 1–4 |

## Tests

- Automated: yes — stamp has no four-digit year; Earth canvas angle −90°; NH noon pin sunward; SH `lat0 = -90`; zh pack; Sol alarm bitmap

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md` per-feature checklist.

## Notes

- Yearly hub is a calendar diagram: the jieqi ring is frozen and Earth stays under the traveling needle so the orbit and jieqi stay in sync.
- Compact inner radius leaves a 2% gutter before the color ring.
