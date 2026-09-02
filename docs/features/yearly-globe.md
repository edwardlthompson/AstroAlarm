# Feature: yearly-globe

> Yearly tab: frozen jieqi ring (Lìchūn at 12), Earth+needle walking CCW, compact midline glyphs, photoreal Earth+Moon hub.

## Acceptance criteria

- ✅ User-visible behavior: Compact is last on Yearly (after disk, pin, location/date); ring labels stay screen-upright and use locale names (zh pack when the device language is zh); compact glyphs sit on the color-band midline; Lìchūn stays at 12 o’clock while Earth and the needle travel CCW; true perihelion stays in January; tilt axis + green spiral on the hub; tabs are Alarms / stacked Daily 2D / Daily 3D / Yearly / Sol
- ✅ Offline/error behavior: Invalid `AstroPlace` skips the user pin and uses the device zone; southern names/colors follow latitude `< 0`; globe rasterize failures fall back to a colored disk
- ✅ Accessibility: TalkBack reads pinyin + locale name + local time on the wheel
- ✅ i18n: `solar_term_toggle_compact` plus `astro_tab_daily`, `astro_tab_2d`, `astro_tab_3d`, `astro_tab_yearly` (en/es/fr); `values-zh/solar_terms.xml`

## Smoke scenario

1. _Given_ AstroAlarm with or without a saved city
2. _When_ the user opens Yearly and toggles Compact
3. _Then_ the ring stays fixed, Earth moves with the year under the needle, and the hub shows Earth (and Moon) without a crash

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/app/src/main/java/org/astroalarm/solarterm/` |
| View | `examples/android/app/src/main/java/org/astroalarm/ui/solarterm/` |
| Tests | `SolarTermWheelRenderTest`, `AstroDisplayPreferencesTest`, `EarthGlobePinTest` |
| Wiring | `AstroScreen.kt` Yearly tab (page 3) |

## Tests

- Automated: yes — compact pref persistence, no-place Yearly bitmap, frozen Lìchūn at 12, Earth follows `canvasDeg`, Yǔshuǐ CCW

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Earth’s Moon stays on Yearly only. The lunar path is visually enlarged; true 0.0026 AU is unreadable in the hub.
- Compact mode still opens the detail sheet on sector tap.
- Hub refresh is every 60 seconds.
- Traditional/Local-seasons switches were removed; southern remap is automatic from latitude.
