# ADR-0001: Core Application Architecture

- **Status:** Accepted
- **Date:** 2026-08-30
- **Deciders:** Project team

## Context

AstroAlarm is a single-purpose Android alarm app. The product surface is Compose screens, a lockscreen Activity, a BroadcastReceiver, and a home-screen widget. Calculations must stay on-device and offline-first.

## Decision

**Selected pattern:** MVVM with a thin platform layer

- **View:** Jetpack Compose (`org.astroalarm.ui`) plus `AstroAlarmActivity` / widget `RemoteViews`
- **ViewModel-equivalent:** `AstroAlarmStore` and `AstroPlaceStore` expose `StateFlow`; screens collect state and call store mutations
- **Model:** `AstroAlarm`, `AlarmTarget`, suncalc-backed `SolarCalculator` / `LunarCalculator`, `AstroNextFire`

Persistence is JSON in SharedPreferences (`AstroAlarmJson`) rather than Room for the first slice. Room and Hilt are on the classpath for later stores. Exact alarms use `AlarmManager.setAlarmClock`.

## Consequences

- Stores are constructable from `Context` in Compose (`remember`) and in receivers/widgets without a graph
- Adding a second persistence backend later means swapping the store, not the UI
- File-size gates stay enforceable: logic ≤ 150 lines, UI ≤ 300 lines
