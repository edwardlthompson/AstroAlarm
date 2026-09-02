# Feature: solar-terms

> Always-on 24 Solar Terms (二十四节气) Year tab: frozen jieqi ring with Earth walking CCW, sun–Earth orbit in the hub, and a compact home-screen widget. Daily 2D/3D clocks are unchanged except Daily 3D axis chrome.

## Acceptance criteria

- ✅ User-visible behavior: Year tab is always present; Compact is last after disk and pin; locale names (zh pack when the device is zh); southern latitudes remap names and colors automatically; Lìchūn stays at 12 o’clock; Earth and the needle travel CCW; widget pin button on the tab
- ✅ Offline/error behavior: Times are computed on-device from NOAA apparent longitude (`SolarMath` / `SolarSeasons.apparentLon`) and cached for the current and next tropical year
- ✅ Accessibility: TalkBack reads pinyin + locale name + local time via content descriptions on the wheel and widget
- ✅ i18n: keys under `solar_term_*` in `res/values/solar_terms.xml` (plus `values-es` / `values-fr` / `values-zh`)

## Smoke scenario

1. _Given_ AstroAlarm is running with a saved city
2. _When_ the user opens the Year tab
3. _Then_ Lìchūn is at the top, Earth is at the current longitude, countdown text, Compact switch last, and a pin-widget button, with no logcat crash

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/app/src/main/java/org/astroalarm/solarterm/` |
| View | `examples/android/app/src/main/java/org/astroalarm/ui/solarterm/` |
| Tests | `examples/android/app/src/test/java/org/astroalarm/solarterm/` and `.../ui/solarterm/` |
| Wiring | `AstroScreen.kt` Yearly tab (page 3 of 5); `AndroidManifest.xml` widget receiver |
| Widget | `org.astroalarm.widget.SolarTermWidgetProvider` |
## Tests

- Automated: yes — `examples/android/app/src/test/java/org/astroalarm/solarterm/` (equinox windows, January wrap, leap year, DST offset, polar timezone identity, southern remap, cache, frozen-ring layout, perihelion AU) plus wheel bitmap smoke

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md` per-feature checklist.

## Notes

- Reuse NOAA tropical longitude already used for seasons/zodiac. Do not use `SolarSeasons.instant()` for jieqi — its month map places 315° in December; Lìchūn is February.
- Southern latitudes remap locale names and colors only (shift 12 terms). Longitudes stay astronomical. Hanzi appear via `values-zh`, not a Traditional toggle.
- Earth–Sun distance uses NOAA eccentricity: perihelion ~early January (closer), aphelion ~early July (farther). The hub exaggerates eccentricity so the orbit is visible. That is southern summer nearest, northern winter nearest — not northern-summer-closest.
- Location caption is the city name (or coords / device zone) with no “at your location” prefix.
- Green north and red south traces weave the Kepler path; the orange Earth stroke is a comet wake.
- Accuracy is the in-app NOAA model (seconds of that model; typically within about a minute of JPL solar longitude).
