# Feature: clock-2d-hour-marks

Toggle on the 2D clock that draws civil hours 0–23 on the 24h dial.

## Acceptance criteria

- ✅ User-visible behavior: 2D clock has a switch that shows hour labels at civil clock positions; 12 stays at local 12:00, not solar noon; daylight hours are black and night hours white; numbers sit on the pie rim with ticks inside; the red now-hand touches those ticks, starts outside the Moon orbit, and draws under the sun
- ✅ Offline/error behavior: preference is local SharedPreferences; default is hidden; missing place still uses the device zone
- ✅ Accessibility: switch uses localized title; state persists across restarts
- ✅ i18n: `astro_toggle_show_hour_marks` and `astro_toggle_show_hour_marks_desc` in en/es/fr

## Smoke scenario

1. _Given_ the 2D clock tab is open
2. _When_ the user turns on **Show hour marks**
3. _Then_ 0–23 sit on the rotating 24h pie (current hour at the top) and stay after leaving the tab

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../widget/CivilHourMarks.kt` |
| View | `AstroDiskOverlays.drawHourRim`, `AstroClockScreen` |
| Prefs | `AstroDisplayPreferences` |
| Tests | `CivilHourMarksTest.kt`, `AstroDisplayPreferencesTest.kt` |
| Wiring | `AstroClockWidgetProvider` reads the same pref |

## Tests

- Automated: yes — `CivilHourMarksTest.kt`, `AstroDisplayPreferencesTest.kt`

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Same civil `ang()` frame as alarms and sunrise badges: `hour * 15 - nowAngle - 90`
- Zodiac sits outside months; both selected shrinks the pie so the rings do not overlap
- Alarm dots sit on the hour tick, inside the numerals; the Sun sits further in; the Moon orbits Earth at the hub (see `clock-earth-moon-hub.md`)
- Hour type shrinks or drops odd hours if 24 labels would overlap (`DiskLabelFit`)
