# Feature: clock-clean-view

Toggle on the 2D clock that hides alarm, sunrise, and sunset time labels so the dial stays readable.

## Acceptance criteria

- ✅ User-visible behavior: 2D, 3D, Yearly, and Sol each have **Show event times**; off hides that disk’s alarm markers (2D also hides sunrise/sunset badges and callouts). Home widgets follow the matching tab pref. Zodiac, compact, month ticks, and the current-time hand stay.
- ✅ Offline/error behavior: each pref is local SharedPreferences; default is times shown; missing place still renders the dial
- ✅ Accessibility: each switch uses `astro_toggle_show_event_times` as TalkBack; state persists across restarts
- ✅ i18n: `astro_toggle_show_event_times` and `astro_toggle_show_event_times_desc` in en/es/fr

## Smoke scenario

1. _Given_ the 2D clock tab is open with a location and at least one enabled alarm
2. _When_ the user turns off **Show event times**
3. _Then_ sunrise, sunset, and alarm time labels disappear from the dial and stay hidden after leaving the tab

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../widget/DiskEventTimeLayers.kt` |
| View | `examples/android/.../ui/AstroClockScreen.kt` |
| Prefs | `examples/android/.../settings/AstroDisplayPreferences.kt` |
| Tests | `examples/android/app/src/test/.../widget/DiskEventTimeLayersTest.kt` |
| Wiring | `AstroClockWidgetProvider` reads the same pref |
## Tests

- Automated: yes — `DiskEventTimeLayersTest.kt`, `AstroDisplayPreferencesTest.kt`

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md` per-feature checklist.

## Notes

- Zodiac ring stays on its own existing toggle
- Home-screen 2D / 3D / Yearly / Sol widgets follow their tab’s event-times preference
- Yearly off hides jieqi/solstice alarm dots so they do not cover the S pole letter
