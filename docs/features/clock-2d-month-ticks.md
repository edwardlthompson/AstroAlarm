# Feature: clock-2d-month-ticks

Toggle on the 2D clock that draws the start of each calendar month on the outer rim.

## Acceptance criteria

- ✅ User-visible behavior: 2D clock has a switch that shows 12 short white rim ticks at each calendar month start; 2-letter labels (Ja–De) sit at the ecliptic midpoint of that month, not on the tick
- ✅ Offline/error behavior: preference is local SharedPreferences; default is hidden; missing place still renders using UTC/system zone
- ✅ Accessibility: switch uses localized title and description; state persists across restarts
- ✅ i18n: `astro_toggle_show_month_ticks` and `astro_toggle_show_month_ticks_desc` in en/es/fr

## Smoke scenario

1. _Given_ the 2D clock tab is open
2. _When_ the user turns on **Show month ticks**
3. _Then_ two-letter month labels appear around the outer rim and stay after leaving the tab

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../widget/MonthRimTicks.kt` |
| View | `AstroDiskOverlays.drawMonthRim`, `AstroClockScreen` |
| Prefs | `AstroDisplayPreferences` |
| Tests | `MonthRimTicksTest.kt`, `AstroDisplayPreferencesTest.kt` |
| Wiring | `AstroClockWidgetProvider` reads the same pref |

## Tests

- Automated: yes — `MonthRimTicksTest.kt`, `AstroDisplayPreferencesTest.kt`

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
