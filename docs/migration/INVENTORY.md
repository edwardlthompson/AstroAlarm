# AstroAlarm Phase 0 inventory

> Documentation only. Phase 1 visual contract: [`DESIGN_BRIEF.md`](DESIGN_BRIEF.md). Phase 2 stack pick: [`TECH_DECISION.md`](TECH_DECISION.md) (KMP + CMP). Phase 3 board: `BUILD_PLAN.md` migration sprints + [`PARITY_CHECKLIST.md`](PARITY_CHECKLIST.md).
> Scope: locked multiplatform migration process — Android today; **stated** targets are iOS (high priority, user-requested) and desktop widgets on Windows, macOS, and Linux, with feature/design parity where a durable OS path exists.
> Method: exhaustive source, tests, feature specs, ADRs, CHANGELOG, BUILD_PLAN, GitHub issues/PRs, commit history, plus Edward’s 2026-11-16 trajectory notes. This Cloud Agent environment had **no Android SDK** (`ANDROID_HOME` unset), so the APK was **not** assembled or run on a device; behavior is confirmed from Kotlin sources, unit tests, and shipped feature docs.
>
> **Porting policy (stated, Edward 2026-11-16):** do not use fragile hacks to force Android-shaped features onto iOS or desktop where they would break. Each Android-specific behavior is tagged **Durable** (first-class public OS API) or **Fallback** (honest platform-native substitute; document the gap). Private APIs, entitlement abuse, hidden keep-alive processes, and background-mode gaming are out of scope.

**Session:** Cursor Agent · `CHANGELOG.md` `[Unreleased]` has list items (Tonight's sky card, accuracy harness, migration docs) · next 🔲 `[AGENT]` row after this docs branch: **M1 KMP skeleton** (implementation is a separate PR). M0 Q1/Q3/Q5 answered 2026-11-16.

---

## Summary

AstroAlarm is today a FOSS **Android** astronomical alarm clock (`applicationId` `org.astroalarm`, Compose Material 3, minSdk 26 / targetSdk 37). **Stated next platforms:** iOS (high priority, user-requested) and **desktop widgets** on Windows, macOS, and Linux — with Durable OS APIs or honest Fallbacks, not Android clones. Calculations are on-device (commons-suncalc + NOAA tropical longitude + JPL Kepler). Persistence is JSON in SharedPreferences plus two DataStore files. Exact fire uses a **single** `AlarmManager.setAlarmClock` slot, a MAX-importance silent notification channel, and a lockscreen Activity with full-screen intent.

**Inventoried (current product surface):**

| Kind | Count |
|------|------:|
| Full-page screens / destinations | 11 |
| Dialogs (in-app + system ringtone picker) | 13 |
| Modal bottom sheets | 2 |
| Home/keyguard App Widgets | 7 |
| Wear OS watch faces | 1 |
| Notification channels | 1 (`astroalarm_alarm_v3`) |
| Alarm target families on `AlarmTarget` | 16 sealed subtypes |
| Solar event types | 18 |
| Lunar event types | 11 |
| Solar terms (jieqi) | 24 |
| Offline city catalog entries | 54 |
| User-facing locales | en (full), es (full), fr (full), zh (solar terms only) |

**Top iOS / desktop constraints (Durable vs Fallback — no stack pick, no hacks):**

Legend used in this file: **Durable** = supported public OS path that can carry the behavior without private APIs. **Fallback** = Android shape cannot be reproduced honestly; ship a platform-native substitute and name the gap. **Omit** = Android-only onboarding/permission with no counterpart; do not fake it.

1. **Lockscreen Activity takeover** (`showWhenLocked`, FSI, DND-bypass MAX channel) — iOS **Fallback** (scheduled notification + in-app ring UI when the user opens it; no third-party lockscreen Activity). Desktop **Fallback** (notification + focused window on an unlocked session). Do not try to draw over the lock screen.
2. **Exact fire while the app is not in the foreground** — iOS **Durable** via the public notification/alarm scheduler (not `AlarmManager`). Desktop **Durable** via OS task/notification APIs *if* the session is allowed to run them; **approved limitation (Q22, Edward 2026-11-16):** alarms may be missed when the machine is asleep or logged out — no hidden keep-alive.
3. **Home-screen widgets** (stated desktop goal) — iOS **Durable** (WidgetKit). macOS **Durable** (WidgetKit desktop widgets). Windows **Durable** only inside the public Widgets board/gallery. Linux **Fallback** (no single widget host; a small always-available window or panel applet is honest). Live 1 Hz analog bitmaps are **Fallback** on every non-Android host (timeline/snapshot widgets, not a per-second `RemoteViews` canvas).
4. **Keyguard / lock-screen widgets** — iOS Lock Screen widgets **Durable** as WidgetKit snapshots, **not** as an alarm overlay. Desktop **Omit**.
5. **Boot receiver** — iOS **Omit** the broadcast; **Durable** is “OS still delivers pending notifications after reboot.” Desktop **Durable** (login item / LaunchAgent / shortcut — not a fake `BOOT_COMPLETED`).
6. **Battery / Doze exemption onboarding** — iOS and desktop **Omit** (no user toggle to clone). Exactness comes from the OS scheduler, not a battery-whitelist hack.
7. **Wear OS analog watch face** — Android Wear stays. **watchOS complications** are **stated** (Edward 2026-11-16, WidgetKit accessory families). A full third-party analog face on watchOS is still **not** a durable FOSS path. Desktop **Omit**.
8. **Android AlarmClock `SET_ALARM` / `DISMISS_ALARM` receiver** — iOS **Durable** substitute is App Intents / Siri, not a broadcast. Desktop **Durable** substitute is a URL/command-line “set alarm” entry if wanted; otherwise **Omit**.

Full per-behavior table: §1.20.

---

## Open questions for Edward

Answered 2026-11-16 (Edward), first pass: iOS is a **stated high-priority** goal. Desktop **widgets** on Windows, macOS, and Linux are a **stated** goal. Fragile hacks to clone Android lockscreen/FSI/Doze/Wear-face behavior are **rejected**.

Answered 2026-11-16 (Edward), second pass — recorded here and in `TECH_DECISION.md` §10:

1. ✅ **Ringing fallback UX / math-before-silence.** Wherever the platform permits, the math problem **must** be solved before the alarm can be silenced, **including the notification Stop path**. Drop the old “shade Stop skips math” default. **iOS AlarmKit: PENDING** — no supported API can gate system Stop on a challenge (`TECH_DECISION.md` §3.1). Do not ship a workaround.
2. 🔲 **Single OS timer.** Only the next fire is armed (`REQUEST_CODE_ALARM = 8801`). Keep this “one OS timer” model on every Durable scheduler? Recommended default still **yes**. Does not block M6.
3. ✅ **Notification Stop vs lockscreen Stop.** Do **not** preserve the shade-skips-math split. Notification Stop must not silence until math succeeds, on every OS that can enforce it.
4. 🔲 **F-Droid.** ADR-0003 still “later”; recipe is the Golden Path stub. Recommended default: keep Android FLOSS-clean; do not block iOS. Does not block M6/M10.
5. ✅ **Wear on iOS.** Ship **watchOS complications** (WidgetKit accessory families). Not Android-Wear-only. Sprint M10.
6. 🔲 **Huawei LiteOS `.hwt` face.** `[HUMAN]` follow-up in `docs/features/wear-watchface.md`. Still wanted?
7. 🔲 **In-app APK apply.** `GoldenPathApp` hardcodes `canApplyUpdate = false`. Stay off, or restore the About apply button?
8. ✅ **Settings export/import.** **Stated new feature** (not a parity tick): versioned shared file in `commonMain` that carries **everything**, especially natal calendar. Sprint M12.
9. 🔲 **GPS timezone.** Catalog cities store IANA ids; GPS/Geocoder writes `ZoneId.systemDefault()`. Intended?
10. 🔲 **Observer place vs birth place.** Independent stores. Keep that split on every platform?
11. 🔲 **Placidus houses.** Spec says out of scope. Confirm?
12. 🔲 **Local LLM Chart agent.** Opt-in `http://127.0.0.1:11434`. Required on iOS/desktop, or Chart copy/paste only?
13. ✅ **UnifiedPush stub.** Product intent for **M13** cross-device dismiss on Android (UnifiedPush/ntfy). Not leftover Golden Path. iOS cannot use UnifiedPush as a distributor (`TECH_DECISION.md` §11).
14. 🔲 **Room / Hilt “for later stores”.** Still planned, or JSON+DataStore is the source of truth to port?
15. 🔲 **Parked 1.7.0 WIP.** Anything still needed from `park/wip-1.7.0-features`?
16. ✅ **First desktop widget.** The **24-hour clock dial with solar noon pointing up** — Android `AstroClockWidgetProvider` (Daily 2D, `isShowSolarNoonUp2D` / `solar_noon_up_2d`). First face in M7 and M9; remaining six follow. Not a subset that drops the dial.
17. 🔲 **README “Material You wheels” vs code.** M3 without `dynamicColorScheme`. Dynamic wallpaper colors in or out?
18. 🔲 **ADR-0004 vs Settings UI.** Dropdowns vs current theme `FilterChip`s. Which chrome is canonical?
19. 🔲 **Locales.** es/fr full; zh solar terms only; Wear English only. Expand as part of iOS parity?
20. 🔲 **Version skew.** App **1.11.0** / 10110; template CHANGELOG **1.12.0**; Release Please PR #42 titled 1.14.0. Product baseline for migration?
21. 🔲 **Open GitHub issues.** Search returned **0**. Confirm nothing off-repo should be inventoried.
22. ✅ **Desktop alarm when logged out / asleep.** **Approved limitation:** alarms may be missed when the machine is asleep or logged out. Session-only fire. No hidden agent.

---

# 1. Current State

## 1.1 Product identity and stack

| Item | Value | Path |
|------|-------|------|
| Product | AstroAlarm | `branding/product.json`, `AGENTS.md` bootstrap card, `docs/spec.md` |
| Purpose | FOSS Android astronomical / solar-lunar alarm clock | `README.md`, `docs/spec.md` |
| License | MIT; no Play Services / Firebase / proprietary telemetry | `LICENSE`, `docs/adr/0003-foss-on-device-ephemeris.md` |
| App id | `org.astroalarm` | `examples/android/app/build.gradle.kts` |
| Manifest namespace | `dev.foss.goldenpath` (Golden Path leftover) | same |
| Application class | `org.astroalarm.AstroAlarmApp` extends `GoldenPathApplication` | `examples/android/app/src/main/java/org/astroalarm/AstroAlarmApp.kt` |
| Launcher activity | `dev.foss.goldenpath.MainActivity` | `examples/android/app/src/main/java/dev/foss/goldenpath/MainActivity.kt` |
| UI | Kotlin + Jetpack Compose + Material 3 | Compose BOM `2026.09.00` |
| Architecture | MVVM-ish thin stores (`StateFlow`), no NavHost | `docs/adr/0001-core-architecture.md` |
| Persistence | JSON SharedPreferences + DataStore; **Room unused** | ADR-0001 |
| Ephemeris | commons-suncalc **3.11** + NOAA longitude + JPL Kepler | ADR-0003 |
| min / compile / target SDK | 26 / 37 / 37 | `app/build.gradle.kts` |
| versionName / versionCode | **1.11.0** / **10110** | `schemas/golden-path/app-version.json`, `app/build.gradle.kts` |
| JVM | 17 | Gradle + CI Temurin 17 |
| AGP / Gradle / Kotlin Compose | 9.4.1 / 9.8.0 / 2.3.21 (CodeQL blocks Kotlin ≥ 2.3.30) | `examples/android/build.gradle.kts`, wrapper |
| Hilt | 2.60.1 — `@HiltAndroidApp` + `@AndroidEntryPoint` only | `AstroAlarmApp.kt`, `MainActivity.kt` |
| navigation-compose | 2.10.2 **on classpath, unused** (boolean overlays + pager) | `app/build.gradle.kts` |
| Wear module | `org.astroalarm.wear`, watchface 1.2.1, no GMS data layer | `examples/android/wear/` |
| Distribution | GitHub Releases APK (`astroalarm-X.Y.Z-foss.apk`); not Play Store | `README.md`, `docs/ANDROID_SIGNING.md` |
| Template lineage | agent-project-bootstrap **1.12.0** | `.agent/AGENT_MEMORY.md`, `.template-version` |

**iOS / desktop:** ephemeris + JSON models are **Durable** to port (pure logic). Compose / AlarmManager / `RemoteViews` are Android hosts — do not force them onto iOS or desktop. UI and widgets use each OS’s Durable toolkit (see §1.20).

### Notable libraries (`examples/android/app/build.gradle.kts`)

| Library | Version | Role |
|---------|---------|------|
| `org.shredzone.commons:commons-suncalc` | 3.11 | Sun/moon times and positions |
| `androidx.compose.material3` | BOM 2026.09.00 | UI |
| `androidx.datastore:datastore-preferences` | 1.2.1 | Theme, update interval |
| `androidx.room:*` | 2.8.5 | **Unused** (no `@Entity`) |
| `com.google.dagger:hilt-android` | 2.60.1 | Application/Activity injection only |
| Espresso | 3.7.0 | Instrumented (Android 16 InputManager pin) |
| Robolectric | 4.17 | Unit tests |

No `libs.versions.toml`. No WorkManager, Glance, Play Fused Location, or Firebase.

---

## 1.2 Navigation and screens

There is **no Jetpack Navigation graph**. Destinations are boolean overlays in `GoldenPathApp` / `GoldenPathScreen` plus a 3-tab `HorizontalPager` with segmented chips.

```
MainActivity
 └─ GoldenPathApp (theme, onboarding gate, launch prompts, crash → feedback)
     └─ GoldenPathScreen
         ├─ HOME  AstroScreen          (default)
         ├─ SETTINGS  SettingsScreen
         ├─ ABOUT  AboutScreen
         └─ FEEDBACK  FeedbackScreen
```

BackHandler closes Feedback → About → Settings. Home has no nested back stack; Daily/Sky chips replace the page body.

Paths: `examples/android/app/src/main/java/dev/foss/goldenpath/ui/GoldenPathApp.kt`, `GoldenPathScreen.kt`, `ui/components/GoldenPathScaffold.kt`.

### Full-page destinations (11)

| # | Surface | Composable / Activity | Path | What the user sees |
|---|---------|------------------------|------|--------------------|
| 1 | Onboarding gate | `OnboardingScreen` inside non-cancelable `PermissionNagDialog` | `org/astroalarm/ui/onboard/` | Permission rows until ring steps granted |
| 2 | Alarms tab | `AstroAlarmsPage` | `ui/AstroAlarmsPage.kt` | List, tonight card, sort chips, FAB Add |
| 3 | Daily · 2D | `AstroClockScreen` | `ui/AstroClockScreen.kt` | 24h solar disk, overlays, share, pin |
| 4 | Daily · 3D | `Astro3DClockScreen` | `ui/Astro3DClockScreen.kt` | Earth globe + transits, accelerometer parallax |
| 5 | Sky · Yearly | `SolarTermScreen` | `ui/solarterm/SolarTermScreen.kt` | 24 jieqi wheel |
| 6 | Sky · Sol | `SolScreen` | `ui/sol/SolScreen.kt` | Inner-solar-system Kepler map |
| 7 | Sky · Chart | `BirthChartScreen` → `NatalWheelSection` | `ui/birth/` | Natal wheel + profile |
| 8 | Settings | `SettingsScreen` | `dev/foss/goldenpath/ui/settings/SettingsScreen.kt` | Place, TTS, math, theme, crashes, onboarding, About |
| 9 | About | `AboutScreen` | `ui/about/AboutScreen.kt` | Version, donations, bug/feature, OpenShouter |
| 10 | Feedback | `FeedbackScreen` | `ui/feedback/FeedbackScreen.kt` | Bug/feature form → GitHub issue URL |
| 11 | Lockscreen ring | `AstroAlarmActivity` + `AstroAlarmLockscreenView` | `astro/alarm/AstroAlarmActivity.kt`, `ui/AstroAlarmLockscreenView.kt` | Stop / time / Snooze; optional math |

Home tabs (`AstroScreen.kt`): index 0 Alarms (`astro_tab_alarms`), 1 Daily (`astro_tab_daily`), 2 Sky (`astro_tab_sky`). FAB Add is **Alarms-only**.

Daily chips (`DailyChip` in `AstroNavPreferences`): default **TwoD**. Sky chips (`SkyChip`): default **Yearly**. Persisted in `astro_display_prefs` keys `daily_chip` / `sky_chip`.

**iOS / desktop:** tab + settings chrome is **Durable** (ordinary app navigation). Lockscreen Activity + separate `taskAffinity` is **Fallback** — there is no honest third-party lockscreen Activity on iOS or desktop.

### Deep link

Manifest on `MainActivity`: `goldenpath://settings/about/feedback`. Parser `FeedbackDeepLink.parse` (`dev/foss/goldenpath/feedback/FeedbackDeepLink.kt`). Query `kind=feature` → feature form, else bug. Query `subject` is sanitized then **never passed into FeedbackScreen**. **No `onNewIntent`** — warm-start deep links are ignored.

Widgets open `MainActivity` with **no tab extra** (last pager/chip state).

---

## 1.3 Dialogs, sheets, overlays, menus

### Dialogs (13)

| Dialog | Path | Trigger |
|--------|------|---------|
| `PermissionNagDialog` | `ui/onboard/PermissionNagDialog.kt` | First launch / Settings “Review permissions”; non-cancelable |
| `AstroEditDialog` | `ui/AstroEditDialog.kt` | FAB Add, row edit, sunrise offer, natal templates, `SET_ALARM` |
| `SunriseOfferDialog` | `ui/SunriseOfferDialog.kt` | First saved city, pending flag |
| Delete confirm | anonymous `AlertDialog` in `AstroAlarmsPage.kt` | Trash |
| `MathChallengeDialog` | private in `AstroAlarmLockscreenView.kt` | Lockscreen Stop when `mathUnlockEnabled` |
| `BirthChartDetailsDialog` | `ui/birth/BirthChartDetailsDialog.kt` | Chart details button |
| `SolarEventListDialog` | `ui/AstroEventListDialogs.kt` | Solar event picker |
| `LunarEventListDialog` | same | Lunar event picker |
| `ZodiacEventListDialog` | `ui/AstroZodiacPickers.kt` | Sign + point |
| Seasonal term `Dialog` | `ui/AstroCelestialPickers.kt` | 24 jieqi |
| `LaunchPromptDialogs` donate | `ui/about/LaunchPromptDialogs.kt` | `AppUpdates.onLaunch` |
| `LaunchPromptDialogs` update | same | GitHub Releases check (interval not `off`) |
| System `ACTION_RINGTONE_PICKER` | from `AstroEditDialog` | Tone URI |

### Bottom sheets (2)

| Sheet | Path | Trigger |
|-------|------|---------|
| `SolarTermDetailSheet` | `ui/solarterm/SolarTermDetailSheet.kt` | Tap a jieqi on Yearly |
| `NatalGlyphExplainSheet` | `ui/birth/NatalGlyphExplainSheet.kt` | Tap glyph / house / planet / angle |

### In-screen chrome (not dialogs)

- `AlarmPermissionBanner` — “ring will not fire”; opens `OnboardingIntents.openRingFix`
- `TonightSkyCard` — next sunrise/sunset/moonrise/moonset or “need place”
- `LocationCard` — city search + GPS (Settings only, not Alarms tab)
- `GpsLocateSkeletonBars` — three muted bars while locating
- `EmptyAlarmsMark` — disk illustration when list empty
- Overlay toggles: `ClockOverlayToggles`, `OverlayToggleLine`, `NatalLiveToggles`, `ZodiacToggleRow`
- `SkyShareButton` → system chooser (`sharePaintedSky`, 2160 PNG / 1080 OOM fallback)
- TalkBack custom actions on wheels (`WheelTalkBack`: next event, reset zoom, share)

### Pickers inside the editor

| Control | Path |
|---------|------|
| `TargetTypeSelector` | `ui/AstroCelestialPickers.kt` — Solar / Lunar / Zodiac / Clock / Seasonal / Planet / Occasion / Natal (if profile) |
| `AstroEditTargetSection` | `ui/AstroEditTargetSection.kt` |
| `SolarEventPicker` / `LunarEventPicker` | `ui/AstroTargetPickers.kt` |
| `ZodiacEventPicker` | `ui/AstroZodiacPickers.kt` |
| `ClockTimePicker` + `AstroNumberWheel` | `ui/AstroClockPicker.kt`, `ui/AstroWheelPicker.kt` |
| `OffsetSelector` | before / exact / after hours:minutes |
| `OccasionPicker` | 2040 / 2492 chips |
| `PlanetTargetPicker` | body, rise/set/retro/conjunction, pairwise / all-align |
| `NatalCompoundPicker` | specific 2/3 or any 2/3 |
| `RepeatDaysSection` / `AudioSettingsSection` | `ui/AstroDialogSections.kt` |
| `AstroAlarmRow` | switch, edit icon, delete icon, badges |

`ProgressiveAdd.kinds` lists the **full catalog** (More-events progressive disclosure was reverted; see DECISION_LOG 2026-09-15).

Snackbars via `GoldenPathScaffold` + `AppSnackbar` / `WidgetPinEvents`. Toasts remain in `BirthAgentSection` and `AstroAlarmTemplatesSection` (DESIGN_GUIDE says do not use Toast). Unused Toast imports on clock screens after WidgetPin refactor.

**iOS / desktop:** dialogs/sheets **Durable**. System `ACTION_RINGTONE_PICKER` is Android-only — **Fallback** to the platform sound picker or bundled alarm tones (do not scrape another app’s ringtone store). Non-cancelable permission nag: **Durable** as each OS’s permission UI; do not clone Android special-access Settings screens that do not exist (exact alarm, FSI, battery).

---

## 1.4 User flows

### First launch

1. `MainActivity.onCreate` → `GoldenPathApp`.
2. `OnboardingGate`: if ring steps missing **or** `OnboardingPreferences.onboarding_complete == false`, overlay non-cancelable `PermissionNagDialog`.
3. Steps from `OnboardingPolicy.steps` (`org/astroalarm/onboard/OnboardingPolicy.kt`): Notifications (API 33+), Location (**optional**), Exact alarms (31+), Full-screen intent (34+), Battery (23+).
4. Continue enabled only when **ring** steps granted (`ringSteps` excludes Location).
5. `prefs.markComplete()`; home Alarms tab.
6. Optional donate/update `LaunchPromptDialogs` from `AppUpdates.onLaunch`.
7. If `PendingCrashStore.read() != null` and no deep-link kind, open Feedback (bug) with stack. Closing feedback **clears** the pending file. There is **no crash-inbox list UI**.
8. `AlarmPermissionBanner` still shows if ring perms are later revoked.
9. Location is not required to leave onboarding; solar/lunar/planet rise-set save stays disabled until a place exists.

Instrumentation skips the gate (`OnboardingChecker.skipUiGate()`).

### Set location

1. Gear → Settings → `LocationCard`.
2. Type ≥2 characters → `AstroPlaceFinder.searchCities` (offline `CityCatalog` first, else `Geocoder`).
3. Or GPS: FINE/COARSE → `resolveLocation` (last-known if <30 min, else `getCurrentLocation` GPS→NETWORK→PASSIVE).
4. Persist `AstroPlaceStore.set` + `AstroAlarmScheduler.rescheduleAll`.
5. First successful city: `SunriseOfferStore.markPendingIfFirstCity` → Alarms may show `SunriseOfferDialog`.
6. Birth-chart city search in `BirthChartFormSection` does **not** set the observer place.

### Add / edit / delete alarm

1. Alarms FAB (or sunrise offer, natal templates, or `AlarmClock.ACTION_SET_ALARM`).
2. Default target: `Solar(Sunrise)` if place valid, else `CustomClock(7:00)`.
3. Editor: type, event/offset/time, label, repeat days, tone + ringtone, TTS, vibrate, math unlock, snooze 5/10/15/20 (default 10).
4. Save disabled for solar/lunar/planet rise-set without place.
5. `alarmStore.save` + `rescheduleAll`.
6. Natal types appear only if an active birth profile exists.
7. Edit: pencil → same dialog. Enable: row Switch → `toggle` + reschedule. Delete: trash → confirm → `delete` + reschedule.
8. List modes: **NextDue** (default) or **Grouped** (`AlarmViewMode` in display prefs). Grouped hides empty domains (`AlarmGroupSections`).

### Alarm fires

1. `AlarmManager` delivers `org.astroalarm.ACTION_ALARM_FIRE` to `AstroAlarmReceiver`.
2. `AlarmRingPresenter.postRinging` — MAX public ongoing notification, full-screen intent, Stop + Snooze.
3. Receiver `startActivity` **and** `pending.send()` with BAL bundles (`AlarmLaunchOptions`).
4. `AstroAlarmActivity`: `setShowWhenLocked` / `setTurnScreenOn` / `FLAG_KEEP_SCREEN_ON`; `AlarmLockscreenOutput.start` (looping ringtone, vibe, TTS); UI forced **Dark**.
5. Shade Stop/Snooze: `AlarmDismissActions` + `ACTION_UI_DISMISS` → activity `finish()`.

### Dismiss / math unlock

1. Stop without math: haptic + optional scale (`LockscreenStop`) → `AlarmDismissActions.stop` (consume occurrence, disable one-shot, `rescheduleAll`) → cancel notification → `finish`.
2. Stop with `mathUnlockEnabled`: `MathChallengeDialog`. Difficulty/count from Settings. Correct advances; last correct → stop. Wrong: error, new problem. Cancel returns to ringing (audio still playing).
3. **Shade Stop today does not present math** (Android gap §1.19 item 7). **Edward 2026-11-16:** this split is **rejected**. Wherever the platform permits, Stop (including notification Stop) must not silence until math succeeds.

### Snooze

1. Lockscreen Snooze: stop output, cancel notification, `scheduleSnooze` (`now + snoozeMinutes`, floor 1), `finish`. Does **not** disable a one-shot.
2. Shade Snooze: same persist/schedule, then UI dismiss.
3. Math is not required for snooze. (Unchanged; Edward’s rule is about **silence/Stop**, not snooze.)

**iOS / desktop:** next-fire math is **Durable**. Delivering the ring is **Durable** only through the public notification/alarm scheduler. The Android FSI Activity, `showWhenLocked`, and `turnScreenOn` remain **Fallback**. **Math-before-silence** is **Durable on Android** (M5 must close the shade bypass) and on desktop **while the session is awake** (alarm window). **iOS AlarmKit: PENDING** — system Stop always silences; App Intents cannot veto Stop (`TECH_DECISION.md` §3.1). Do not draw over SpringBoard or intercept `mobiletimerd`.

---

## 1.5 Widgets (home / keyguard)

Seven classic `AppWidgetProvider`s. All `RemoteViews` + bitmaps or static text. Shared disk layout `res/layout/widget_astro.xml`. Pixel budget `ClockRenderSize` in `widget/EarthTexture.kt` is today `(minSideDp * 2.5).coerceIn(200, 600)` px — **chunky; APPROVED Deviation** is native-density or vector on every platform (`DESIGN_BRIEF.md` §10). Info XML: `updatePeriodMillis="1800000"` (Android 30 min floor), `resizeMode="horizontal|vertical"`, `widgetCategory="home_screen|keyguard"`. **No** configure activity, Glance, or `RemoteViewsService`.

**First desktop / WidgetKit widget (Edward 2026-11-16):** the **24-hour clock dial with solar noon pointing up**. That is this table’s first row: `AstroClockWidgetProvider` (Daily 2D disk, `AstroDisplayPreferences.isShowSolarNoonUp2D()` / `solar_noon_up_2d`). First face in BUILD_PLAN M7 and M9; the other six follow.

| Provider | Info XML | Layout | Size | Pin CTA | Click |
|----------|----------|--------|------|---------|-------|
| `AstroClockWidgetProvider` **(first desktop widget)** | `res/xml/astro_widget_info.xml` | `widget_astro.xml` | 4×4 min 250dp | Daily 2D | MainActivity req 8830 |
| `Astro3DClockWidgetProvider` | `astro_3d_widget_info.xml` | same | 4×4 | Daily 3D | 8850 |
| `AstroUpcomingWidgetProvider` | `astro_upcoming_widget_info.xml` | `widget_upcoming.xml` | 4×2 min 250×110 | Alarms tab | 8840 |
| `SolarTermWidgetProvider` | `astro_solarterm_widget_info.xml` | `widget_astro.xml` | 2×2 min 140 | Yearly | 8870 |
| `SolWidgetProvider` | `astro_sol_widget_info.xml` | same | 2×2 | Sol | 8880 |
| `NatalChartWidgetProvider` | `astro_natal_chart_widget_info.xml` | same | 2×2 | Chart | 8849 |
| `NatalAlignWidgetProvider` | `astro_natal_align_widget_info.xml` | `widget_natal_align.xml` | 4×2 | **none in-app** | 8848 |

Six of seven set `initialKeyguardLayout` to the home layout. NatalAlign has **no** `initialKeyguardLayout` but still `keyguard` category. AOSP dropped keyguard app widgets around Lollipop; OEM / Android 16 lock-screen widgets may still honor it. **There is no lock-screen-only widget family.** The ringing UI is an Activity, not a widget.

### Update mechanics

1. System `APPWIDGET_UPDATE` every ≥30 min.
2. `AstroAlarmScheduler.refreshWidgets()` on schedule / cancel / snooze (all seven).
3. Display-pref setters call the matching `updateAll`.
4. `BirthProfileStore` refreshes natal widgets on save.
5. Manifest `TIME_TICK` on six providers: on API 26+ **implicit TIME_TICK is not delivered** to manifest receivers (effectively dead). `ACTION_WIDGET_TICK` is **defined and consumed, never broadcast**.
6. `TIMEZONE_CHANGED` / `TIME_SET` work only where `onReceive` is overridden (Clock, 3D, Sol, SolarTerm, NatalChart). Upcoming and NatalAlign ignore them except via scheduler / 30 min.

Pin: `WidgetPin.request` (`widget/WidgetPin.kt`). API 26+ `requestPinAppWidget` + `org.astroalarm.WIDGET_PINNED`. Else snackbar `astro_widget_pin_manual_guide`. `WidgetPinReceiver` is **dynamic** (`GoldenPathScreen`, `RECEIVER_NOT_EXPORTED`); success toast is lost if the app is not foreground.

Upcoming lines: `AlarmWidgetScope.upcomingLines`, 48h horizon; empty `"0 active"`.

NatalAlign: next 2×/3× natal compounds (`NatalAlignWidgetCopy.lines`); no `TIME_TICK`, no in-app pin (launcher picker only).

Renderers live under `examples/android/app/src/main/java/org/astroalarm/widget/` (disk, globe, moon, planets, zodiac, transits, hubs, labels).

**iOS:** home and Lock Screen **widgets** are **Durable** via WidgetKit timelines/snapshots (stated: iOS is in scope). `requestPinAppWidget` is Android-only — **Fallback** is “Add Widget” copy, not a private pin API. Per-second analog bitmaps are **Fallback** (timeline refresh, not `TIME_TICK`).  
**Desktop (stated: widgets on Windows, macOS, Linux):** macOS WidgetKit **Durable**. Windows 11 Widgets gallery **Durable** for snapshot widgets. Linux has no single host — **Fallback** to a small always-available window or desktop/panel applet, not a fake `AppWidgetProvider`. Keyguard widgets **Omit** on desktop. Do not ship a 1 Hz screen-scraping “widget” on any of these. First shipped desktop face = Daily 2D 24h noon-up dial (`AstroClockWidgetProvider`). Render **crisp** at native density or as vector — do not carry the 200–600 px Android cap.

---

## 1.6 Notifications and alarm fire path

### Channel — `AlarmNotificationChannel`

Path: `examples/android/app/src/main/java/org/astroalarm/astro/alarm/AlarmNotificationChannel.kt`

| Item | Value |
|------|-------|
| ID | `astroalarm_alarm_v3` |
| Legacy deleted on ensure | `astroalarm_channel`, `astroalarm_alarm`, `astroalarm_alarm_v2` |
| Notification id | `8800` |
| Importance | `IMPORTANCE_MAX` |
| DND | `setBypassDnd(true)` |
| Lockscreen | `VISIBILITY_PUBLIC` |
| Channel sound | `null` (Activity owns tone) |
| Channel vibration | off |
| Audio attrs | `USAGE_ALARM` + `FLAG_AUDIBILITY_ENFORCED` |
| Builder | `PRIORITY_MAX`, `CATEGORY_ALARM`, ongoing, not auto-cancel, `setFullScreenIntent(..., true)` |

Actions: `org.astroalarm.ACTION_NOTIFICATION_STOP` (8811), `ACTION_NOTIFICATION_SNOOZE` (8812) → `AstroAlarmReceiver`. Internal `ACTION_UI_DISMISS` via `AlarmUiDismissBridge` (`RECEIVER_NOT_EXPORTED`).

Strings: `astro_channel_name` / `astro_channel_desc` — “Full-screen lockscreen alarms you can stop without unlocking”.

### Scheduler — `AstroAlarmScheduler`

- **One** slot: `REQUEST_CODE_ALARM = 8801`.
- Only `alarmManager.setAlarmClock(AlarmClockInfo(triggerMs, showPending), fireBroadcast)`.
- No `setExact`, `setExactAndAllowWhileIdle`, `setWindow`, or inexact alarms.
- `runCatching` — fails closed if exact-alarm permission missing.
- Next fire: `AstroNextFire` + `AlarmFireIdentity.armedPair` (earliest enabled; seasonal peers collapsed).
- App start: `AstroAlarmApp.onCreate` → `rescheduleAll`.

### Receiver — `AstroAlarmReceiver` (exported)

Manifest filter: `ACTION_ALARM_FIRE`, `BOOT_COMPLETED`, `TIMEZONE_CHANGED`, `TIME_SET`, `SET_ALARM`, `DISMISS_ALARM`.

`SET_ALARM` creates `CustomClock` from `EXTRA_HOUR` / `EXTRA_MINUTES` / `EXTRA_MESSAGE`. `DISMISS_ALARM` cancels notification + reschedule (**no alarm-id match**). These sit on a **BroadcastReceiver**, not an Activity with `CATEGORY_DEFAULT`, so the usual Clock-app / Assistant contract is unreliable. No `SHOW_ALARMS`, `SET_TIMER`, or default-alarm-app metadata.

**Absent:** `LOCKED_BOOT_COMPLETED`, `MY_PACKAGE_REPLACED`, `LOCALE_CHANGED`, OEM `QUICKBOOT_POWERON`, `directBootAware`.

**No phone `<service>`**, no `startForeground`, no `FOREGROUND_SERVICE*` permissions. Ringing is FSI Activity + notification.

**iOS:** public local/alarm notifications are **Durable**. Critical Alerts are entitlement-gated and not a FOSS-default path — do not treat them as required. A lockscreen Activity, DND-bypass MAX channel, and exported `SET_ALARM` receiver are **Fallback** (App Intents / Siri for “set alarm”; notification actions for Stop/Snooze).  
**Desktop:** OS notification + optional focused window is **Durable**. There is no FSI, DND-bypass channel, or AlarmClock broadcast — **Omit** those Android APIs rather than emulate them.

---

## 1.7 Visual design

**Material version:** Jetpack Compose **Material 3**. XML shell `Theme.Material.Light.NoActionBar` (`res/values/themes.xml`). **Not** Material You dynamic wallpaper colors — no `dynamicColorScheme`. README/Fastlane still say “Material You wheels”.

**Token source:** `design-tokens/design-tokens.json` → generated `Color.kt`, `Type.kt`, `Dimens.kt` under `dev/foss/goldenpath/ui/theme/`. Wrapper `GoldenPathTheme` (`Theme.kt`): System / Light / Dark. Default **System**.

**Palette (tokens):**

| Role | Light | Dark |
|------|-------|------|
| primary | `#B3263E` | `#D3304E` |
| background | `#FFFFFF` | `#12121f` |
| surface | `#F5F5FA` | `#1a1a2e` |
| canvas.night (clocks) | `#1a1a2e` | `#1a1a2e` |
| canvas.gold | `#C9A227` | `#C9A227` |

Brand badge in `product.json`: primary `C9A227`, secondary `1A2744`.

**Typography:** system default (no bundled fonts). titleLarge 22sp, headlineMedium 28, bodyLarge 16, bodyMedium 14, labelLarge 14/500.

**Spacing / radius / elevation:** xs–xl 4/8/16/24/32 dp; radius 4/8/16; elevation 0/1/3.

**Icons:** `material-icons-extended`; XML `ic_brand_mark.xml`; raster Earth/Moon/planet maps under `res/drawable-nodpi/`; overlay live labels `overlay_live_sun|moon|mercury`. Jieqi enum still carries emoji glyphs in `SolarTerm.kt` (UX law `UX-EMOJI-ICON` is a web rule; Android uses those as term marks).

**Motion:** `UxMotion.kt` FAB 200ms / empty 120ms fade+scale; skipped when reduce-motion. `ClockParallax.kt` accelerometer on 3D in-app clock only. Wheel zoom/pan `WheelZoomPan.kt`. Yearly 150ms sector highlight (`docs/features/sky-hand-tick.md`). High-refresh scroll `DisplayModeSelector` / `highRefreshScroll` (not a user setting; applied in `onStart` because `decorView.display` is null in `onCreate` on CI emulator — DECISION_LOG 2026-08-20).

**Dark mode:** Settings chips; DataStore; lockscreen **forced Dark** regardless of Settings.

**Edge-to-edge:** `enableEdgeToEdge()`; `GoldenPathScaffold` + `bottomInsetPadding()`; `NavigationModeProvider`.

**Wheels:** tactile hour/minute and offset wheels (`AstroNumberWheel`). 2D clock: circular disk (`WheelDisk` / `CircleShape`), optional solar-noon-up, hour marks, month ticks, zodiac ring (Wikipedia URL on tap), event-time callouts, alarm dots. 3D: NASA-style globe textures, terminator, transit ticks, moon. Yearly: north-ecliptic-pole frame (♈ at +X, Dōngzhì at 12, Xiàzhì at 6), Earth+needle CCW, southern latitude aliases names/colors by +12 terms. Sol: true-AU Kepler polylines, wakes, spokes, 1 AU bar, opposition ticks, natal ghost planets toggle.

**Share:** 2160 PNG via FileProvider `shares/`; 1080 OOM fallback; widget 28px label cap lifted during export (`DiskLabelFit.withExportCap`).

**iOS / desktop:** token palette and in-app Canvas disks are **Durable** (ordinary app drawing). Accelerometer parallax is **Durable** on iPhone (Core Motion) and **Omit** on desktop. High-refresh display mode is **Durable** where the OS exposes it, **Omit** where it does not.

---

## 1.8 Settings and defaults

### Settings screen sections (order in `SettingsScreen.kt`)

1. Location (`LocationCard` + GPS)
2. TTS voice picker (`TtsVoicePicker`)
3. Math difficulty + problem count (`MathSettingsSection`)
4. Theme chips: System / Light / Dark
5. Save crash details switch
6. Re-open onboarding button
7. About button

Display overlays are **on the clock/sol/yearly/chart screens**, not Settings.

### App-level defaults

| Setting | Store / key | Default | Path |
|---------|-------------|---------|------|
| Theme | DataStore `theme_preferences` / `theme_mode` | **System** | `ThemePreferences.kt` |
| Save crashes | `gp_feedback` / `save_crashes` | **false** | `FeedbackPrefs.kt` |
| Update check interval | DataStore `app_update_preferences` / `check_interval` | **`"off"`** | `AppUpdatePreferences.kt` |
| Valid intervals | `off`, `on_session`, `daily`, `weekly`, `monthly` | turning on from off writes **weekly** | `CheckSchedule.kt`, `SettingsLogic.kt` |
| Installed artifact format | same DataStore | detect (`apk`) | `AppUpdatePreferences.kt` |
| Pending restart | `pending_restart` | false | same |
| Update launch | SharedPreferences `gp_updates` | unset | `UpdateLaunchPrefs.kt` (excluded from backup) |
| Onboarding complete | `astro_onboarding_prefs` / `onboarding_complete` | **false** | `OnboardingPreferences.kt` |
| Math difficulty | `astro_math_prefs` / `math_difficulty` | **MEDIUM** (`GENIUS` → HARD) | `MathPreferences.kt`, `MathModels.kt` |
| Math problem count | `math_problem_count` | **1** (clamp 1–5) | same |
| TTS pitch | `astro_tts_prefs` / `tts_pitch` | **1.0** (clamp 0.5–2.0) | `TtsPreferences.kt`, `TtsModels.kt` |
| TTS language / engine / voice | `tts_lang`, `tts_engine`, `tts_voice_name` | `""` (system) | same |
| TTS min quality | `tts_min_quality` | **500** (`QUALITY_VERY_HIGH`) | same |
| Show zodiac 2D / 3D | `show_zodiac_2d`, `show_zodiac_3d` | **true** | `AstroDisplayPreferences.kt` |
| Show event times 2D / 3D / Yearly / Sol | `show_event_times_*` | **true** | same |
| Natal ghosts on Sol | `show_natal_ghosts_sol` | **false** | same |
| Month ticks 2D | `show_month_ticks_2d` | **false** | same |
| Hour marks 2D | `show_hour_marks_2d` | **false** | same |
| Solar noon at top 2D | `solar_noon_up_2d` | **false** | same |
| Solar-term compact | `solar_term_compact` | **false** | same |
| Alarm list mode | `alarm_view_mode` | **NextDue** | same |
| Daily hub chip | `daily_chip` | **TwoD** | `AstroNavPreferences.kt` |
| Sky hub chip | `sky_chip` | **Yearly** | same |
| Sunrise offer done/pending | `astro_sunrise_offer` | both **false** | `SunriseOffer.kt` |
| Local LLM enabled | `os_birth_llm_prefs` / `enabled` | **false** | `LocalLlmPrefs.kt` |
| Local LLM URL | `base_url` | `http://127.0.0.1:11434` | same |
| Observer place | `os_astro_place_prefs` | **unset** | `AstroPlaceStore.kt` |

### Per-alarm defaults (`AstroAlarm.kt` / JSON)

| Field | Default |
|-------|---------|
| `enabled` | true |
| `daysOfWeek` | empty (daily for non-clock; **once** for custom clock) |
| `toneEnabled` | true |
| `toneUri` | null (system default TYPE_ALARM) |
| `ttsEnabled` | true |
| `vibrateEnabled` | true |
| `snoozeMinutes` | **10** (UI options 5/10/15/20) |
| `mathUnlockEnabled` | **false** |
| celestial `offsetMinutes` | 0 |
| incomplete clock JSON | **7:00** |
| ringing alarm missing from store | snooze fallback **5** in receiver; output policy: vibrate only, no tone/TTS |

### Math problems (`MathProblemGenerator`)

| Difficulty | Shape |
|------------|--------|
| ELEMENTARY | 0–9 add/sub |
| EASY | ~4–25 add/sub |
| MEDIUM | ~18–75 add/sub |
| HARD | a×b ± c |

### Not persisted

- Reduce motion (reads `Settings.Global` animator/transition scales — `ReduceMotion.kt`)
- High-refresh display mode
- Settings export/import (strings only — **stated new feature**, Sprint M12, not yet implemented)

**iOS / desktop:** theme, math, TTS, overlay toggles, place, and crash opt-in are **Durable**. Exact-alarm / FSI / battery Settings rows **Omit**. Local LLM `127.0.0.1` is **Fallback** on iOS (ATS/sandbox) unless the user points at an allowed local URL; Chart copy/paste remains **Durable**. Export/import is **stated** new shared-file scope (M12), not a parity tick of today's missing UI.

---

## 1.9 Data models and persistence

**Room is on the classpath and unused.** Source of truth:

| Domain | Class | Prefs name | Format |
|--------|-------|------------|--------|
| Alarms | `AstroAlarmStore` | `os_astro_alarms_prefs` / `os_astro_alarms_list_json` | JSON array `AstroAlarmJson` |
| Observer place | `AstroPlaceStore` | `os_astro_place_prefs` | primitives |
| Birth profiles | `BirthProfileStore` | `os_astro_birth_prefs` / `birth_profiles_json` | JSON `BirthProfileJson` |
| Display + nav chips | `AstroDisplayPreferences`, `AstroNavPreferences` | `astro_display_prefs` | booleans + strings |
| Math / TTS / onboarding / sunrise / LLM | see §1.8 | separate SharedPreferences | primitives |
| Theme / updates | DataStore | `theme_preferences`, `app_update_preferences` | |
| Pending crash | `PendingCrashStore` | `filesDir/gp_pending_crash.txt` | text |
| Donations | assets `donations.json` | not user data | |

Backup: `allowBackup=true`. `backup_rules.xml` / `data_extraction_rules.xml` exclude **only** `gp_updates.xml`. Alarm JSON, place, birth, theme, and crash file **are Auto Backup eligible**.

### `AstroAlarm`

Path: `examples/android/app/src/main/java/org/astroalarm/astro/model/AstroAlarm.kt`

Fields: `id`, `label`, `enabled`, `target: AlarmTarget`, `daysOfWeek`, `toneEnabled`, `toneUri`, `ttsEnabled`, `vibrateEnabled`, `snoozeMinutes`, `mathUnlockEnabled`, `lastFiredEpochMs`.

`isOnce` = custom clock with empty days **or** `Occasion`.

JSON codecs: `AstroAlarmJson.kt`, `AstroAlarmTargetJson.kt` (`kind`: `clock|solar|lunar|zodiac|solarterm|planet|planet_align|all_planets_align|occasion`), `AstroAlarmNatalTargetJson.kt` (`natal_asc|natal_mc|moon_return|solar_return|mercury_station|moon_ingress|natal_compound_specific|natal_compound_any`). Legacy solar names `Dawn`/`Dusk` map to `CivilDawn`/`CivilDusk`.

### `AstroPlace`

`cityName`, `latitude`, `longitude`, `zoneId` default `ZoneId.systemDefault().id`. Valid iff name non-blank and lat ∈ [-90,90], lon ∈ [-180,180].

### `BirthProfile`

Path: `astro/birth/BirthProfile.kt`

`id`, `label`, `birthDate`, `birthTime` default **noon**, `timeUnknown=false`, `cityName=""`, `lat=0`, `lon=0`, `zoneId="UTC"`, `zoneIsFallback=false`, `active=false`, `savedInterpretation=""`. Polar warning if `|lat| > 66`. Ascendant requires place + known time.

Birth place is **independent** of Settings observer place.

**iOS / desktop:** JSON models are **Durable**. Android Auto Backup is **Fallback** to each OS’s optional backup (iCloud / whatever the desktop host offers) or **Omit** if none — do not silently upload birth data. Crash file in the app sandbox is **Durable**.

---

## 1.10 Event types (complete catalog)

Canonical sealed type: `AlarmTarget` in `AstroAlarm.kt`.

### Solar — `SolarEventType` (18)

Sunrise, Sunset, CivilDawn, CivilDusk, NauticalDawn, NauticalDusk, AstronomicalDawn, AstronomicalDusk, SolarNoon, SolarMidnight, GoldenHourMorning, GoldenHourEvening, BlueHourMorning, BlueHourEvening, MarchEquinox, SeptemberEquinox, JuneSolstice, DecemberSolstice.

Labels: `res/values/event_strings.xml` (+ es/fr). Equinox/solstice treated as **yearly seasonal** in `AstroEventLabels.isSeasonal` and `AstroNextFire`.

### Lunar — `LunarEventType` (11)

Moonrise, Moonset, MoonTransit, NewMoon, FullMoon, WaxingCrescent, FirstQuarter, WaxingGibbous, WaningGibbous, LastQuarter, WaningCrescent.

### Custom clock

`AlarmTarget.CustomClock(hour, minute)` — civil clock in observer/device zone.

### Zodiac (tropical sun)

`ZodiacSign` (12: Aries…Pisces, 0°–330°) + `ZodiacPoint` Beginning 0° / Middle 15° / End 30° — `astro/zodiac/ZodiacModels.kt`.  
`AlarmTarget.Zodiac(sign, point, offsetMinutes)`.

### Solar terms — `SolarTerm` (24)

`solarterm/SolarTerm.kt`: LICHUN 315° … DAHAN 300°, 15° steps, hans/hant/pinyin/glyph/`SolarSeason`. Southern latitudes **alias names/colors by +12 terms**; longitudes stay astronomical.

### Planet / Sol

`PlanetBody`: MERCURY, VENUS, EARTH, MARS, JUPITER, SATURN, URANUS, NEPTUNE. EARTH is not a fireable alarm body.

`PlanetEventType`: Rise, Set, RetrogradeStart, DirectStart, Opposition (outer), InferiorConjunction / SuperiorConjunction (inner).

Also:

- `PlanetAlign(bodyA, bodyB)` — pair within **8°** geocentric
- `AllPlanetsAlign` — heliocentric span **≤ 90°**

### Natal / compound

`NatalBody`: SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN, URANUS, NEPTUNE.

Alarm-grade `NatalAspect`: Conjunction 0°, Square 90°, Opposition 180°.

Chart chords `WheelAspect`: Conjunction, Sextile 60°, Square, Trine 120°, Opposition — orbs 8° luminary / 6° other (`NatalAspectMath.kt`).

`NatalStoryKind`: AscSun, AscMoon, AscMercury, SolarReturn, MoonReturn, MoonRisingSign, MercuryStation. Highlight orb **3°**.

Alarm targets: `NatalAscAspect`, `NatalMcAspect`, `MoonReturn`, `SolarReturn`, `MercuryStation`, `MoonSignIngress`, `NatalCompoundSpecific` (2 or 3 kinds), `NatalCompoundAny` (arity 2|3).

Houses: **whole-sign only** (`WholeSignHouses.kt`). No Placidus.

### Occasions (one-shot)

`SkyOccasionCatalog`: `naked_eye_2040` (2040-09-08 sunset or 18:00 fallback), `all_planets_2492` (2492-05-06 noon local). Fires once then disables.

### Tonight sky (not an alarm type)

`TonightSky.Kind`: Sunrise, Sunset, Moonrise, Moonset — next after now, 0–2 day search (`astro/sky/TonightSky.kt`). Shown on Alarms tab.

**iOS / desktop:** event catalog is **Durable** (pure logic). Picker UI is ordinary app chrome — **Durable**, subject to design parity, not a platform restriction.

---

## 1.11 Ephemeris and business logic

Documented in `docs/adr/0003-foss-on-device-ephemeris.md`. No Swiss Ephemeris, Skyfield, or remote astronomy APIs. Accuracy: `docs/ACCURACY.md` (30 NOAA dates, ±180s, all pass). Harness `scripts/check-ephemeris-accuracy.sh`, `EphemerisAccuracyTest.kt`.

| Domain | Engine | Path |
|--------|--------|------|
| Sun/moon times & positions | commons-suncalc 3.11 | `SolarCalculator.kt`, `LunarCalculator.kt`, `SkyBodies.kt` |
| Tropical apparent longitude, seasons, jieqi, natal Sun | NOAA Solar Calculator equations | `SolarMath.kt`, `SolarSeasons.kt`, `SolarTermCalculator.kt` |
| Planets, Sol tab, natal planets, stations | JPL Keplerian elements (J2000 + century rates) | `PlanetElements.kt`, `PlanetKepler.kt`, `PlanetNext.kt`, `PlanetHorizon.kt`, `PlanetMotion.kt` |
| Moon ecliptic lon | sun lon + suncalc elongation; mean-lon fallback `218.316 + 13.176396 * d` | `LunarCalculator.eclipticLon` |
| Asc / MC | NOAA-style LST + obliquity | `AscendantMath.kt` |
| Houses | whole-sign | `WholeSignHouses.kt` |
| Wear day/night | **not ephemeris** — sine day-length around local noon | `wear/WearDayNightApprox.kt` |

Suncalc twilight: VISUAL, CIVIL, NAUTICAL, ASTRONOMICAL, GOLDEN_HOUR, NIGHT_HOUR. Polar night/day → `null` rise/set. Observer height **sea level** (known ~1 minute sunrise bias). 3D view is a transit clock, not a planetarium (fake stars, tropical zodiac bubbles) — ADR-0003.

Local LLM Chart agent: `NatalAgentPrompt.kt`, `LocalLlmClient.kt` (10s timeout, typed error, no API key). Copy disabled without place + exact time + ascendant.

Natal templates: `AstroAlarmTemplatesSection` can add preset natal alarms to the store.

**iOS / desktop:** on-device ephemeris is **Durable** (port or share the library). Keep the FOSS/no-network invariant. No remote almanac as a “workaround.”

---

## 1.12 Location, timezone, DST

| Piece | Path |
|-------|------|
| Model | `AstroPlace.kt` |
| Store | `AstroPlaceStore.kt` |
| Offline gazetteer | `CityCatalog.kt` — **54** cities with lat/lon/**IANA tz** |
| GPS | `AstroPlaceFinder.resolveLocation` — FINE or COARSE; last-known if <30 min else `getCurrentLocation` (API 30+) GPS→NETWORK→PASSIVE |
| Geocoding | Android `Geocoder` — reverse for GPS label; forward only if catalog miss |
| UI | Settings `LocationCard`, onboarding Location step (optional) |
| Birth TZ | `BirthTimeZones.kt` — TZDB historical offset at the birth Instant |

No `ACCESS_BACKGROUND_LOCATION`, no continuous listener, no geofences, no Play Fused Location.

**Timezone behavior:**

- Catalog search writes the city’s IANA id.
- GPS/Geocoder path writes **`ZoneId.systemDefault().id`**, not a coordinate-derived zone.
- Custom-clock fire: `LocalDate.atTime(h,m).atZone(zone)` (java.time DST gaps/overlaps).
- Receiver reschedules on `TIMEZONE_CHANGED` and `TIME_SET`.
- Solar terms computed in **UTC**, shown in place zone.
- Wear uses device `ZoneId` only.

Without a place: custom clock / zodiac / solar-term / natal-without-horizon still compute; sun/moon/planet rise-set **do not**.

**iOS:** Core Location + geocoder are **Durable** (when the user grants them).  
**Desktop:** GPS is **Fallback** to the existing 54-city catalog + typed search (already the product’s offline path). Do not scrape IP-geolocation as a silent hack. IANA DST is **Durable** everywhere.

---

## 1.13 Scheduling (next fire)

Core: `astro/alarm/AstroNextFire.kt`.

| Target | Search |
|--------|--------|
| CustomClock | next 0–7 days matching `daysOfWeek` (empty = any day / once) |
| Solar daily | 0–14 days, skip days if set; needs valid place |
| Solar seasonal | this year … +3 via `SolarCalculator` / NOAA |
| Lunar | 0–35 days; needs place |
| Zodiac | `ZodiacCalculator.nextInstant` (~0.9856473°/day + 7 Newton-like iterates); skip if peer already fired |
| SolarTerm | UTC years now … +2 |
| Planet | `PlanetNext` (rise/set 30-min scan ~7 days; retro/align 800 daily steps; all-align 50 years) |
| Occasion | catalog date if still future |
| Natal* | `NatalAlarmFire` → `NatalNext` (1° orb, moon 1h/40d, others 1d/800d) or `NatalCompoundNext` (12h step, 730-day horizon, 3° co-active) |

Disabled alarms return `null`. Offset `plusSeconds(offsetMinutes * 60)`.

**Peer collapse:** equinox/solstice, cardinal jieqi (Chūnfēn/Xiàzhì/Qiūfēn/Dōngzhì), and 0° zodiac cusps share a `FireKey`; `AlarmFireIdentity` arms one (`JITTER_SEC=2`). Consume on Stop/Snooze stamps peers.

Snooze replaces the single OS slot until the next fire or `rescheduleAll`.

**iOS / desktop:** next-fire math is **Durable**. Mapping onto one public OS notification/alarm request is **Durable**. Replicating `setAlarmClock` status-bar chrome, FSI, or a second hidden timer is **Fallback/Omit**.

---

## 1.14 Permissions, background, Android platform

Phone manifest: `examples/android/app/src/main/AndroidManifest.xml`.

| Permission | Runtime? | Used for |
|------------|----------|----------|
| `ACCESS_NETWORK_STATE` | no | `NetworkStatusMonitor` (About online flag; not alarm-critical) |
| `INTERNET` | no | GitHub Releases, Geocoder, optional local LLM, donations URLs |
| `POST_NOTIFICATIONS` | yes, API 33 | ringing + onboarding |
| `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` | yes | GPS place |
| `RECEIVE_BOOT_COMPLETED` | install | reschedule |
| `WAKE_LOCK` | install | **declared; no `newWakeLock` in Kotlin** — screen stay-on is `FLAG_KEEP_SCREEN_ON` |
| `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM` | special / privileged | `canScheduleExactAlarms`; onboarding `ACTION_REQUEST_SCHEDULE_EXACT_ALARM` |
| `USE_FULL_SCREEN_INTENT` | special API 34 | `canUseFullScreenIntent`; `ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT` |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | special | required ring step; `ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` |
| `VIBRATE` | install | lockscreen vibrator |
| `${applicationId}.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` | merge | AndroidX |

`<queries>`: `https` VIEW, UnifiedPush `REGISTER`, package `io.heckel.ntfy`. **No** TTS, ringtone, or geocoder query (API 30 package visibility may hide TTS engines — `TtsEngines` uses `queryIntentServices(INTENT_ACTION_TTS_SERVICE)`).

FileProvider: `res/xml/file_paths.xml` — cache root, `updates/`, `shares/`.

UnifiedPush: `UnifiedPushMessageReceiver` exported; `UnifiedPushConfig.endpointUrl()=null`, `usesProprietaryPush()=false`; register only if ntfy or a package name containing `unifiedpush` is installed; **payload ignored**.

No WorkManager / JobScheduler.

**iOS / desktop:** Notifications + location are **Durable** permission prompts. `SCHEDULE_EXACT_ALARM`, `USE_FULL_SCREEN_INTENT`, and `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` **Omit** — do not invent fake Settings deep-links. Boot reschedule **Omit** the broadcast (see §1.20). UnifiedPush **Omit** unless a real Durable distributor exists on that OS.

---

## 1.15 Wear OS

Module: `examples/android/wear/` · `applicationId` `org.astroalarm.wear` · min 26 / target 37 · `com.google.android.wearable.standalone=true`.

- One `WatchFaceService`: `AstroWearWatchFaceService` (`BIND_WALLPAPER`, `WATCH_FACE`).
- Analog, hardware canvas, 1s interactive delay.
- Draw: `WearDialCanvas` + `WearDialLayout` + `WearCivilAngles` + `WearDayNightApprox`.
- Now-up red hand (Daily 2D style) + day/night pie from **device ZoneId only** (sine season around local noon; no lat/lon; no phone sync).
- Ambient: black + hand.
- Complications: `ComplicationSlotsManager` accepted, unused.
- No Wear Activities, alarm UI, settings, or messaging.
- i18n: English only; spec says es/fr can follow later.
- `[HUMAN]` follow-up: Huawei LiteOS/Harmony `.hwt` without HMS (`docs/features/wear-watchface.md`).

**iOS / watchOS (Edward 2026-11-16):** a full third-party analog watch face remains **Fallback** (not a durable FOSS API comparable to `WatchFaceService`). **Stated substitute:** WidgetKit **complications** — `accessoryCircular`, `accessoryRectangular`, `accessoryInline`, `accessoryCorner` (WWDC22 10050/10051). Sprint M10. **Desktop: Omit.** Do not sideload a fake watch face.

---

## 1.16 Sounds, vibration, TTS, math

Owned by `AlarmLockscreenOutput` while `AstroAlarmActivity` is visible. Channel is silent.

**Tone:** `RingtoneManager.getRingtone` with per-alarm `toneUri` or default `TYPE_ALARM` / `TYPE_NOTIFICATION`. Looping on API 28+. Alarm audio attrs + audibility enforced. No `READ_MEDIA_AUDIO`, no AudioFocus, no MediaPlayer, no `FLAG_INSISTENT`. Policy: `AlarmOutputPolicy.shouldPlayTone`. Empty URI with tone on → Default Alarm Sound. Tone off → never start Ringtone.

**Vibration:** `VibratorManager.defaultVibrator` (API 31+) else `Vibrator`. Waveform `{0,800,400,800,400}` repeating. Compose haptics on Stop/Snooze.

**TTS:** `AlarmTtsSession` (alarm audio attrs, pitch/locale/voice), `AlarmTtsRepeater` every **2s**. Tone pauses during utterance, then resumes. Preferred engine fail → retry default → `astro_tts_unavailable`. Settings: `TtsVoicePicker` / `TtsDropdown` / catalogs.

**Math:** lockscreen Stop today. Shade Stop currently skips math (§1.19 item 7). **Edward 2026-11-16:** Stop (including notification Stop) must present math wherever the OS can enforce it. iOS AlarmKit Stop is **PENDING**. See §1.8 / `TECH_DECISION.md` §3.1.

**iOS:** `AVAudioSession` + `AVSpeechSynthesizer` + haptics are **Durable**. A lockscreen-enforced `USAGE_ALARM` stream is **Fallback** (play when the notification is delivered / the app is opened).  
**Desktop:** audio + TTS APIs are **Durable** while a session is active. Alarm-stream takeover of the lock/login screen is **Omit**.

---

## 1.17 i18n and accessibility

| Locale | Coverage |
|--------|----------|
| en `res/values/` | Full |
| es `values-es/` | Full product strings (a11y, nav, birth, events, natal explain, overlay, progressive add, ring, share, solar terms, sunrise offer, tonight) |
| fr `values-fr/` | Same as es |
| zh `values-zh/` | **`solar_terms.xml` only** |

Wear: English only. Fastlane: **en-US only**.

RTL: `android:supportsRtl="true"`.

TalkBack: `res/values/a11y_strings.xml` (+ es/fr); `contentDescription` / `semantics` on location, wheels, lockscreen, widgets. Wheel custom actions `WheelTalkBack.kt`. Next-event announce `NextEventA11y.kt`. Overlay min touch **48dp**. Lockscreen Stop/Snooze 56dp full-width; Stop top / Snooze bottom (`docs/features/alarm-snooze.md`). Reduce motion: true if animator **or** transition scale is 0.

UX contract: `docs/UX_LAW.md` (Compose contentDescription, touch targets, no hardcoded Color, no literal strings). Token contrast gate exists for web; Android uses generated M3 schemes.

`NatalCompoundPicker` hardcodes `"Event ${slot + 1}"` (not i18n).

**iOS / desktop:** string tables and reduce-motion are **Durable**. TalkBack custom actions map to VoiceOver/Narrator custom actions where the OS supports them (**Durable**); otherwise **Fallback** to named buttons.

---

## 1.18 CI, FOSS, distribution

**CI workflows** under `.github/workflows/`: `ci.yml` (android-structure FOSS grep, assembleDebug, path-filtered instrumented API 34, reproducible `assembleRelease` with `SOURCE_DATE_EPOCH=1700000000`), CodeQL, Security Scan / Trivy, Scorecard, Dependabot automerge, Release Please, release SBOM, Pages (template leftover), weekly health, stale, PII nudge, Argus stubs.

Required checks: `docs/CI_REQUIRED_CHECKS.md`.

**Signing:** `docs/ANDROID_SIGNING.md`; env `ANDROID_KEYSTORE_*` / `GOLDENPATH_UPLOAD_*`; debug fallback if upload keystore unset.

**Fastlane:** `examples/android/fastlane/metadata/android/en-US/` — title AstroAlarm, pitch from `product.json`, changelogs through `10110.txt`, phone screenshots.

**F-Droid:** recipe still Golden Path stub (`examples/android/metadata/dev.foss.goldenpath.yml`: AutoName Golden Path, versionName 0.1.0, repo `agent-project-bootstrap`). Product Fastlane and F-Droid YAML **are not in sync**. AGENT_MEMORY: “F-Droid later”.

**Privacy:** `docs/PRIVACY.md` — no tracking by default; birth data on-device; crash opt-in; GitHub Releases User-Agent product+version only; GlitchTip/Bugsink stub **must stay disabled** without DPIA (`docs/features/crash-inbox.md`).

**FOSS grep:** no `com.google.android.gms` / Firebase on production path.

**Open GitHub issues:** none (search 2026-11-16). Open PRs at inventory time: Dependabot #38 (Android deps), #43 (setup-python), Release Please #42 (1.14.0).

**iOS:** App Store / TestFlight is the **Durable** distribution path (sideload is not the iOS equivalent of GitHub APKs). F-Droid **Omit** on iOS.  
**Desktop:** GitHub Releases (or later store/Winget/Flathub) is **Durable** for the app; widgets install through each OS’s widget gallery, not a fake pin API. Template `docs/WINGET.md` is still a bootstrap runbook until Edward names a desktop installer.

---

## 1.19 Incomplete / WIP observed in *current* code (not future goals)

These are present-day gaps in the Android app, inventoried so they are not silently “fixed” or dropped in a port:

1. No Navigation Compose graph — widgets cannot deep-link to a tab.
2. `canApplyUpdate = false` hardcoded; About apply button never shown.
3. Feedback `subject` unused; no `onNewIntent`.
4. UnifiedPush payload ignored.
5. NatalAlign widget has no in-app pin.
6. `NatalCompoundPicker` English `"Event N"`.
7. Math bypass via notification Stop — **Edward 2026-11-16: close this gap** (not preserve). M5 must present math before shade Stop silences.
8. `ACTION_DISMISS_ALARM` is generic (no alarm id).
9. Wear has no UI, complications, or phone sync.
10. Manifest `TIME_TICK` / `ACTION_WIDGET_TICK` dead on modern API.
11. GPS timezone = system default, not catalog/geocoded TZ.
12. `WAKE_LOCK` unused in Kotlin.
13. Settings export/import strings with no implementation — **Edward 2026-11-16: implement as new stated feature** (M12), not a silent gap preserve.
14. F-Droid YAML still Golden Path.
15. `clock-2d-plain-center.md` acceptance boxes still 🔲 though v1.11.0 shipped a plain hub (spec lag).
16. ADR-0004 dropdown Settings vs current theme `FilterChip`s.
17. Toasts in birth/templates vs DESIGN_GUIDE snackbar rule.
18. Package-replaced / locked-boot / locale receivers absent.
19. Single OS alarm slot — overlapping enabled alarms share one fire.
20. Placeholder GitHub repo can yield empty issue URL (`IssueFormUrl.isPlaceholderRepo`).

---

## 1.20 Android-specific behaviors — Durable path vs honest Fallback

Policy (stated, Edward 2026-11-16): **no fragile hacks**. If the OS has a public, supported API that can carry the behavior, that is the path. If it does not, ship an honest substitute and name the gap. Do not use private APIs, entitlement abuse, overlay windows on the lock screen, hidden keep-alive processes, or background-mode gaming.

| Android behavior (current) | Path | iOS | Desktop (Windows / macOS / Linux) |
|----------------------------|------|-----|-----------------------------------|
| In-app screens, dialogs, sheets, wheels, Canvas clocks | Durable | Ordinary app UI | Ordinary app UI (desktop **app** is implied by widgets but not separately scoped; widgets are the stated desktop deliverable) |
| Ephemeris, next-fire, JSON alarms/place/birth, tokens, i18n | Durable | Port/share logic | Port/share logic |
| Theme / math / TTS settings / overlay prefs | Durable | App settings | App settings |
| Location permission + one-shot GPS | Durable | Core Location | **Fallback:** city catalog + typed search (already shipped). No silent IP geo |
| Offline city catalog (54) + IANA TZ | Durable | Same catalog | Same catalog (primary on desktop) |
| Share 2160 PNG | Durable | Share sheet | Desktop share/save file |
| TalkBack labels + reduce-motion | Durable | VoiceOver + Reduce Motion | Narrator/Orca + OS reduce-motion |
| Scheduled exact fire (`setAlarmClock`) | Durable scheduler / Fallback chrome | **Durable:** public notification/alarm scheduler. **Fallback:** no status-bar AlarmClock chip, no FSI | **Durable:** OS notification or task while the session can receive it. **Approved limitation (Q22):** no fire when logged out/asleep — do not keep a hidden agent |
| Full-screen lockscreen Activity (`showWhenLocked`, `turnScreenOn`, FSI) | Fallback | Time-sensitive notification; full Stop/math UI when opened | Notification + focused window on an unlocked session |
| MAX channel, `bypassDnd`, silent channel + Activity-owned tone | Fallback | Honor Focus; play tone with the notification/app. Do not require Critical Alerts entitlement | Honor OS do-not-disturb; no DND-bypass channel |
| Notification Stop/Snooze actions | Durable | **Stop:** math-before-silence **PENDING** on AlarmKit (system Stop cannot be gated — `TECH_DECISION.md` §3.1). Snooze remains system/optional. Do not hide Stop. | Stop in the alarm window must run math when the session is awake. Toast-only dismiss that cannot present math is **PENDING** on that host, not a hack |
| Math unlock on the lockscreen | Fallback chrome; **stated product rule** | Same `MathChallengeDialog` in the in-app ring UI. AlarmKit system Stop **PENDING** (no supported veto). Do not inject UI onto SpringBoard | Math in the alarm window, not on the OS login screen |
| `RECEIVE_BOOT_COMPLETED` reschedule | Omit broadcast | OS still delivers pending notifications after reboot (**Durable**) | Login item / LaunchAgent / startup shortcut (**Durable**) |
| `TIMEZONE_CHANGED` / `TIME_SET` | Durable | Significant-time / timezone notifications | OS timezone-change events or recompute on launch |
| `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM` onboarding | Omit | No clone Settings screen | No clone |
| `USE_FULL_SCREEN_INTENT` onboarding | Omit | No clone | No clone |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` onboarding | Omit | No clone | No clone |
| `WAKE_LOCK` / `FLAG_KEEP_SCREEN_ON` while ringing | Fallback | Keep the screen on only while the in-app ring UI is visible | Same, while the alarm window is focused |
| Home-screen widgets (7 providers) | Durable host | **Durable:** WidgetKit. **First face:** Daily 2D 24h noon-up dial (`AstroClockWidgetProvider`). Timeline/snapshot, not 1 Hz. **Crisp** native-density/vector (APPROVED Deviation) | **Stated goal.** Same first face, then the other six. macOS WidgetKit **Durable**. Windows Widgets board **Durable** for snapshots. Linux **Fallback:** always-available window or panel applet |
| `requestPinAppWidget` | Fallback | “Add Widget” instructions | OS widget gallery / “how to pin” copy |
| Keyguard widget category | Fallback / Omit | iOS Lock Screen WidgetKit snapshots **Durable**; not an alarm overlay | **Omit** |
| Live analog bitmap every tick (`TIME_TICK` / 30 min floor) | Fallback | WidgetKit timeline (minutes, not seconds) | Same snapshot cadence as the host |
| Exported `SET_ALARM` / `DISMISS_ALARM` receiver | Fallback | App Intents / Siri | URL or CLI “set alarm” if wanted; else Omit |
| System `ACTION_RINGTONE_PICKER` | Fallback | Bundled tones + OS sound picker | Bundled tones + OS sound picker |
| Alarm `USAGE_ALARM` audio stream | Fallback | Play with notification / in-app session | Play in the alarm window |
| TTS while locked | Fallback | Speak when the notification fires or the app opens (AVSpeech) | Speak in the alarm window |
| Vibration waveform | Durable | Haptics API | **Fallback:** omit or desktop notification sound; many Linux desktops have no vibrator |
| Wear OS analog watch face | Fallback / Omit | **Stated:** watchOS **complications** (WidgetKit `accessoryCircular` / `Rectangular` / `Inline` / `Corner`). Not a WatchFaceService clone. Android Wear analog face stays Android-only | **Omit** |
| Accelerometer 3D parallax | Durable / Omit | Core Motion on iPhone | **Omit** |
| High-refresh `preferredDisplayModeId` | Durable / Omit | ProMotion where present | Where the OS exposes it |
| Android Auto Backup of prefs | Fallback | iCloud/device backup if the user enables it; never silent cloud of birth data | Optional desktop backup / export file |
| FileProvider APK update apply | Omit on iOS | Store updates | Signed installer / GitHub Release |
| UnifiedPush / ntfy | Android FOSS wake path for **M13** | **No UnifiedPush distributor.** Remote stop of a ringing AlarmKit alarm needs **APNs** (and a server) — `TECH_DECISION.md` §11. No FCM | LAN P2P while the session is awake; same asleep limitation as Q22 |
| Deep link `goldenpath://…` | Durable | Custom URL / universal link | Custom URL / protocol handler |
| Instrumentation skip of onboarding | N/A | Test harness equivalent | Test harness equivalent |

**What “full feature and design parity” means under this policy:** in-app clocks, event catalog, editor, settings, tokens, and (on iOS) WidgetKit faces aim for visual/functional parity. Android lockscreen takeover, Doze exemption, AlarmClock broadcasts, 1 Hz home-screen canvases, Wear faces, and Linux `AppWidgetProvider` clones are **not** parity targets — they are named Fallbacks or Omits.

---

# 2. Trajectory & Future Goals

Label: **stated** = written as a product/template decision; **inferred** = implied by growth, parked work, or template leftovers. No technology pick.

## 2.1 Stated direction

| Item | Label | Source |
|------|-------|--------|
| Stay MIT FOSS; no Play Services / Firebase / proprietary telemetry | stated | ADR-0003, `foss-compliance.mdc`, README |
| On-device ephemeris only; not navigation/observatory-grade | stated | ADR-0003, README “What this is not”, `docs/ACCURACY.md` |
| GitHub Releases primary; **F-Droid later** | stated | ADR-0003, `.agent/AGENT_MEMORY.md` “Distribution: F-Droid later” |
| Room/Hilt on classpath **for later stores** | stated | ADR-0001, AGENT_MEMORY |
| UnifiedPush FOSS path if product wants it; live ntfy/NextPush is `[ADB]` | stated | `docs/features/unifiedpush.md` |
| Crash proxy / GlitchTip **off** until DPIA | stated | `docs/PRIVACY.md`, `docs/features/crash-inbox.md`, `docs/CRASH_PROXY.md` |
| Wear lat/lon / phone sync is a **later enhancement** | stated | `docs/features/wear-watchface.md` Notes |
| Huawei LiteOS/Harmony `.hwt` without HMS | stated (`[HUMAN]`) | same feature spec |
| Whole-sign houses only; **Placidus remains out of scope** | stated | `docs/features/whole-sign-houses.md` |
| Chart agent uses existing `astro.birth` stack; do not reintroduce park `NatalChartCalc` | stated | `docs/features/birth-agent.md` |
| Local LLM default `127.0.0.1:11434`, off, no cloud vendor | stated | birth-agent spec, `PRIVACY.md` |
| Product base = published **v1.10.0+** on `origin/main`; do not continue on `park/wip-1.7.0-features` | stated | `BUILD_PLAN.md` |
| Duplicate `org.astroalarm.natal` calculator/UI **dropped**; published `astro.birth` Chart is canonical | stated | `BUILD_PLAN.md` |
| BUILD_PLAN migration sprints M0–M13 | stated | `BUILD_PLAN.md`; M0 Q1/Q3/Q5 answered 2026-11-16 |
| CII Best Practices login still HUMAN leftover | stated | `HUMAN_BACKLOG.md` (badge now exists on README — backlog may be stale) |
| Privacy portability: “Export settings where technically feasible” | stated | `docs/PRIVACY.md` User Rights |
| **Export/import backup file** carrying everything (especially natal calendar); versioned shared format in `commonMain` | stated | Edward, 2026-11-16. **New scope**, not Android parity. Sprint M12 |
| **Cross-device dismiss + data sync** (stop ringing on all devices; general data) | stated | Edward, 2026-11-16. **New scope** after parity. Sprint M13; options in `TECH_DECISION.md` §11 |
| **Math before silence** wherever the OS permits, including notification Stop | stated | Edward, 2026-11-16. iOS AlarmKit Stop **PENDING** |
| **First desktop widget** = 24h clock dial, solar noon up (`AstroClockWidgetProvider`) | stated | Edward, 2026-11-16 |
| **watchOS complications** (WidgetKit accessory families) | stated | Edward, 2026-11-16. Sprint M10 |
| **Desktop alarms missed when asleep/logged out** | stated (approved limitation) | Edward, 2026-11-16 (Q22) |
| **Crisp widget rendering** (native density or vector; do not copy the chunky Android bitmap cap) | stated | Edward, 2026-11-16; DESIGN_BRIEF APPROVED Deviation |
| Alarms-first IA: 3 tabs Alarms \| Daily \| Sky; location optional on Continue | stated | DECISION_LOG 2026-09-14, UX-1…5 |
| Home chrome Settings-only; theme/About/donate not in header | stated | ADR-0004 (partially realized — see §1.19) |
| File budgets 300 UI / 150 logic | stated | AGENTS.md, ADR-0001 |
| OpenSSF Best Practices project 14564 | stated | README, `docs/OPENSSF_BEST_PRACTICES.md` |
| Unreleased (not yet in a tagged app release): Tonight's sky card; NOAA accuracy harness | stated | `CHANGELOG.md` `[Unreleased]` |
| **iOS support is a firm, high-priority goal** (many users have requested it) | stated | Edward, 2026-11-16 (this Phase 0 follow-up). Not yet in README/`docs/spec.md` |
| **Desktop widgets** on Windows, macOS, and Linux | stated | Edward, 2026-11-16. Widget surface is the named desktop deliverable |
| **No fragile hacks** to force Android-only behaviors onto platforms where they would break; use Durable OS paths or honest Fallbacks | stated | Edward, 2026-11-16; applied in §1.20 |

**Not stated (do not invent):** App Store vs sideload for iOS, Winget/Flathub, accounts/vendored cloud, FCM, or that lockscreen FSI must be cloned 1:1. Stack pick is in `TECH_DECISION.md` (KMP+CMP). Template `docs/WINGET.md` remains a bootstrap runbook, not an AstroAlarm shipping decision. APNs for iOS remote-stop is **open** (M13).

## 2.2 Growth areas (stated vs inferred)

### More event types / sky catalog

- **Stated (shipped):** 18 solar, 11 lunar, 24 jieqi, planets, alignments, natal compounds, two sky occasions (2040, 2492) — `AstroAlarm.kt`, `SkyOccasionCatalog.kt`, `docs/features/sky-occasions.md`, `celestial-alarms.md`.
- **Inferred:** more catalog occasions and planet events are cheap to add (catalog pattern already exists). No open issue requesting specific new events.
- **Stated out of scope:** Placidus; second natal calculator.

### Charts / visualizations

- **Stated (shipped):** 2D disk, 3D globe, Yearly jieqi, Sol Kepler, natal wheel, 2160 share — feature specs under `docs/features/clock-*`, `sol-*`, `yearly-*`, `natal-*`, `sky-share-2160.md`.
- **Stated (Edward 2026-11-16):** those visualizations should appear as **widgets on iOS and on desktop** (Windows, macOS, Linux), using each OS’s Durable widget API — not a 1 Hz Android `RemoteViews` clone (§1.20). **First desktop widget** is the Daily 2D **24-hour clock dial with solar noon pointing up** (`AstroClockWidgetProvider`). Remaining six follow in M7/M9.
- **Inferred:** more overlay toggles (clean view already shipped as “Show event times”); remote branch names `cursor/2d-clean-view-toggle-6857` / `cursor/2d-plain-center-6857` suggest polish continued after merge. Canvas-heavy UI is the in-app visual identity to preserve where a Durable app UI exists.
- **Stated:** 3D is a transit clock, not a planetarium (ADR-0003). Do not infer a full sky atlas.

### Data-heavy lists

- **Stated (shipped):** NextDue vs Grouped alarm list; upcoming widget 48h / 3 lines.
- **Inferred:** no infinite timeline, calendar month view, or export-to-ICS in code. `docs/features/alarm-calendar-peers.md` is about **peer collapse**, not a calendar UI. A data-heavy list is **not** a stated next step.

### Hardware / sensors

- **Stated (shipped):** GPS once, accelerometer parallax on 3D in-app clock, Wear face, optional high-refresh display mode.
- **Stated (Edward 2026-11-16):** Wear-style full faces are not to be forced onto iOS/desktop. **watchOS complications** (WidgetKit accessory families) **are** in scope (M10). Android Wear analog face stays Android.
- **Inferred:** compass / gyro / barometer unused. No magnetometer heading. Phone↔watch data layer explicitly avoided (FOSS, no GMS).

### Offline

- **Stated:** calculations need no network; city catalog first; Geocoder optional. Alarms fire offline.
- **Inferred:** network is only for Updates, donations URLs, GitHub feedback, Geocoder miss, optional local LLM. Cross-platform should keep this split.

### Sync / accounts / cloud

- **Stated (Edward 2026-11-16):** users want dismissing an alarm on one device to stop it ringing on the others, plus general data sync. **New scope** (M13), not Android parity. Local-first FOSS options and recommendation: `TECH_DECISION.md` §11. No FCM. No vendor account required.
- **Stated (unchanged):** privacy is local-first; no install UUID; birth data never in crash reports.
- **Inferred:** Auto Backup of alarm JSON is an Android-only accidental sync path, not a substitute for M13.

### AI features

- **Stated (shipped):** Chart “Copy agent instructions” + paste/save interpretation + **opt-in** local HTTP LLM (`docs/features/birth-agent.md`).
- **Stated:** do not vendor an LLM in the APK; no cloud vendor / API key.
- **Inferred:** Grok Bots docs (`docs/GROK_BOTS.md`, `docs/features/grok-bots.md`) are **template optional**, not in the alarm UX. Threat model includes OWASP LLM walk because of the local-agent surface.

### Accessibility

- **Stated (shipped):** TalkBack custom actions, 48dp overlays, reduce-motion, es/fr a11y strings, Fastlane TalkBack smoke (`docs/features/alarms-first-a11y.md`, overlay-toggles-a11y).
- **Inferred:** no in-app font-scale setting, no color-blind palettes beyond tokens, no Switch Access spec. Wear es/fr “later”. Cross-platform parity should include TalkBack-equivalent custom actions on wheels.

### Localization

- **Stated (shipped):** en/es/fr product strings; zh jieqi names.
- **Inferred:** expanding zh (and others) is natural given jieqi; not on BUILD_PLAN. Wear English-only called out as follow-up.

### Distribution / platforms

- **Stated:** GitHub Releases APK; F-Droid later; no Play Store listing in pitch.
- **Stated (Edward 2026-11-16):** iOS is high-priority (user-requested). Desktop **widgets** on Windows, macOS, Linux. No hacks; Durable vs Fallback per §1.20.
- **Inferred:** iOS distribution will need a Durable store path (App Store/TestFlight); GitHub APK sideload is not that path. A full desktop alarm *app* (not only widgets) is implied if widgets need a host app; first **widget** is the noon-up 24h dial.
- **Inferred from template only (not AstroAlarm):** Winget, Flathub, web PWA (web module **pruned**).
- **Stated HUMAN:** Huawei watch-face package (Android/Harmony; **Omit** on iOS/desktop).

### Alarm reliability

- **Stated (shipped, painfully):** AlarmClock broadcast + FSI after targetSdk 37 BAL blocked Activity PendingIntents (DECISION_LOG 2026-09-14 v1.8.2); onboarding blocks until ring perms; Exact in ring banner; TTS repeats 2s; silent channel v3.
- **Inferred:** OEM FSI quirks stay **Android-only**. iOS/desktop must not chase them with overlays; they use the Fallback ring path in §1.20. Package-replaced / locked-boot gaps may be Android hardening, not a cross-platform item.

### Settings / data portability

- **Stated (Edward 2026-11-16):** export/import is a **new feature** so users can move between devices. Must carry **everything**, especially natal calendar. Versioned, documented, shared format in `commonMain` with round-trip tests. Sprint M12. Not a parity item of the current Android gap.
- **Stated (unchanged):** GDPR portability “where technically feasible”; strings already exist.

### Template / agent process (not product UX)

BUILD_PLAN empty after archiving UX-1…17, natal suite, Golden Path 1–11, outer-crop + WIP restore. Trajectory of the **app** from commits 2026-08-30 → 2026-11: bootstrap → suncalc → clocks → Sol/Yearly → natal → alarms-first UX → lockscreen reliability → Wear/TTS/occasions/chart agent → Tonight sky + accuracy harness. Growth has been **visualization + natal + reliability**, not accounts or cloud.

Remote branches at inventory time (not merged): Dependabot Android/Python, Release Please 1.14.0, several `cursor/*` polish branches (clean view, plain center, solar terms, alarm channel). Treat as possible in-flight polish, not approved roadmap.

## 2.3 Sources scanned

| Source | Result |
|--------|--------|
| `README.md`, `branding/product.json`, Fastlane en-US | Pitch: solar/lunar/custom + 2D/3D + lockscreen math; GitHub Releases only |
| `docs/spec.md` / `docs/plan.md` | FR-1…7; plan stub is bootstrap leftover |
| `BUILD_PLAN.md`, `COMPLETED_TASKS.md`, `HUMAN_BACKLOG.md` | Board empty; large archive; CII leftover |
| `CHANGELOG.md` | Unreleased tonight-sky + accuracy; product 1.8–1.12 template |
| `.agent/AGENT_MEMORY.md`, `.agent/DECISION_LOG.md` | Stack, F-Droid later, Room later, ship notes |
| `docs/adr/0001`–`0005` | Architecture, privacy, ephemeris, settings chrome |
| `docs/features/*.md` (~110 files) | Shipped vertical slices; Wear/Placidus/LLM notes |
| `docs/PRIVACY.md`, `ACCURACY.md`, `DESIGN_GUIDE.md`, `UX_LAW.md` | Constraints |
| GitHub issues | **0 open issues** |
| GitHub PRs | Dependabot + Release Please only (product issues absent) |
| `git log` | Tonight sky, accuracy harness, portfolio/docs, template 1.12/1.13 catch-up |
| Kotlin TODOs | **None** matching TODO/FIXME/XXX/HACK/WIP in `examples/android` |
| Code gaps listed in §1.19 | Current incomplete behavior |
| Edward 2026-11-16 | Stated: iOS high-priority; desktop widgets Win/macOS/Linux; no fragile hacks |
| Edward 2026-11-16 (second pass) | Math-before-silence including notification Stop; iOS AlarmKit PENDING; first desktop widget = 24h noon-up dial; watchOS complications; export/import new scope; desktop asleep approved; cross-device sync new scope; crisp widgets APPROVED |

---

## Appendix A — Key path index (for later phases)

Do not redesign from this list; use it to avoid dropping a container.

| Area | Root |
|------|------|
| Alarm model / JSON / store / next fire / scheduler / receiver / lockscreen output | `examples/android/app/src/main/java/org/astroalarm/astro/alarm/` |
| Alarm UI | `.../ui/AstroAlarm*.kt`, `AstroEdit*.kt`, `Astro*Picker*.kt`, `AstroDialogSections.kt` |
| Place | `.../astro/place/` |
| Sun / moon / zodiac | `.../astro/sun/`, `moon/`, `zodiac/`, `sky/` |
| Sol / jieqi | `.../sol/`, `solarterm/`, `ui/sol/`, `ui/solarterm/` |
| Natal | `.../astro/birth/`, `ui/birth/` |
| Widgets | `.../widget/` + `res/xml/astro_*widget*` + `res/layout/widget_*` |
| Math / TTS / onboard | `.../math/`, `tts/`, `onboard/`, `ui/math/`, `ui/tts/`, `ui/onboard/` |
| Share | `.../share/` |
| Theme / Settings / About / Feedback | `dev/foss/goldenpath/ui/` |
| Wear | `examples/android/wear/` |
| Manifest | `examples/android/app/src/main/AndroidManifest.xml` |
| Tokens | `design-tokens/design-tokens.json` |
| Specs | `docs/spec.md`, `docs/features/` |

## Appendix B — Screen / feature count (parity checklist)

Use this as a drop-nothing checklist in later phases.

**Screens (11):** Onboarding, Alarms, Daily 2D, Daily 3D, Yearly, Sol, Chart, Settings, About, Feedback, Lockscreen.

**Dialogs (13):** Permission nag, editor, sunrise offer, delete, math, birth details, solar list, lunar list, zodiac list, jieqi picker, donate prompt, update prompt, ringtone picker.

**Sheets (2):** Jieqi detail, natal glyph explain.

**Widgets (7):** 2D clock, 3D clock, upcoming, yearly, Sol, natal chart, natal align.

**Wear (1):** Daily analog face.

**Alarm families (16 `AlarmTarget` subtypes):** CustomClock, Solar, Lunar, Zodiac, SolarTerm, Planet, PlanetAlign, AllPlanetsAlign, NatalAscAspect, NatalMcAspect, MoonReturn, SolarReturn, MercuryStation, MoonSignIngress, NatalCompoundSpecific, NatalCompoundAny, Occasion.

**Platform behaviors that must be named on every target OS:** use the §1.20 Durable / Fallback / Omit column — do not treat “degraded Android clone” as the plan. Must still appear in later phases so nothing is dropped: scheduler, reboot persistence, timezone recompute, ring UI, notification actions, widgets (stated on iOS + desktop), location, TTS, haptics, tones, share, updates.
