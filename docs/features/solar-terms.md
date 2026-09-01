# Feature: solar-terms

> Optional 24 Solar Terms (二十四节气) year view: 2D mandala, 3D ecliptic ring, and a compact widget. Additive — daily 2D/3D clocks are unchanged unless the user enables Year View.

## Acceptance criteria

- ✅ User-visible behavior: Settings toggle **Show 24 Solar Terms in Year View** (default off) adds a Year tab with 2D wheel and 3D ring; each sector shows Chinese (simplified/traditional), English, and local datetime for the user’s `AstroPlace` timezone
- ✅ Offline/error behavior: Times are computed on-device from NOAA apparent longitude (`SolarMath` / `SolarSeasons.apparentLon`) and cached for the current and next tropical year; 3D failures fall back to the 2D wheel
- ✅ Accessibility: TalkBack reads Chinese + English + local time via content descriptions on the wheel and widget
- ✅ i18n: keys under `solar_term_*` in `res/values/solar_terms.xml` (plus `values-es` / `values-fr`); hanzi always shown

## Smoke scenario

1. _Given_ AstroAlarm is running with a saved city and the Year View toggle off (three daily tabs only)
2. _When_ the user enables **Show 24 Solar Terms in Year View** and opens the Year tab
3. _Then_ the 24-sector wheel shows the current term, “Next term in X days”, and a location label, with no logcat crash; toggling 3D shows the ecliptic ring

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/app/src/main/java/org/astroalarm/solarterm/` |
| View | `examples/android/app/src/main/java/org/astroalarm/ui/solarterm/` |
| Tests | `examples/android/app/src/test/java/org/astroalarm/solarterm/` and `.../ui/solarterm/` |
| Wiring | `AstroScreen.kt` 4th tab; `SettingsScreen.kt` section; `AndroidManifest.xml` widget receiver |
| Widget | `org.astroalarm.widget.SolarTermWidgetProvider` |

## Tests

- Automated: yes — `examples/android/app/src/test/java/org/astroalarm/solarterm/` (equinox windows, January wrap, leap year, DST offset, polar timezone identity, southern remap, cache) plus wheel/ring bitmap smoke

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md` per-feature checklist.

## Notes

- Reuse NOAA tropical longitude already used for seasons/zodiac. Do not use `SolarSeasons.instant()` for jieqi — its month map places 315° in December; Lìchūn is February.
- Southern **Local seasons** remaps English names and colors only (shift 12 terms). Chinese names and longitudes stay astronomical.
- Accuracy is the in-app NOAA model (seconds of that model; typically within about a minute of JPL solar longitude).
