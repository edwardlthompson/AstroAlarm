# AstroAlarm Phase 2 — technology decision

> Documentation / planning only. No app code in this phase.
> Inputs: [`INVENTORY.md`](INVENTORY.md), [`DESIGN_BRIEF.md`](DESIGN_BRIEF.md).
> Output: **one** path, with shared vs native split, Android stay-buildable rule, and cited API facts.

**Decision:** Kotlin Multiplatform (KMP) + Compose Multiplatform (CMP) for in-app UI and domain logic; **native** widget and alarm-chrome adapters per OS (WidgetKit / AlarmKit / Windows Widget Provider / Linux window-or-applet).

**The current Android app stays the production binary until a later cutover sprint proves feature-gate parity.** Extraction is additive (`commonMain` / `iosMain` / `desktopMain` modules). Do not replace `examples/android` in place.

---

## 1. What we are choosing for

Stated trajectory (`INVENTORY.md`): MIT FOSS Android app today; **iOS is a high-priority user-requested target**; **desktop widgets** on Windows, macOS, and Linux; no fragile hacks. Design must match [`DESIGN_BRIEF.md`](DESIGN_BRIEF.md). Platform behaviors follow INVENTORY **§1.20** Durable / Fallback / Omit.

The existing product is already **Kotlin + Jetpack Compose Material 3**, with:

- Canvas-drawn 2D/3D/jieqi/Sol/natal disks (`AstroDiskRenderer`, `WheelDisk`, Skia/Android `Canvas`)
- Lists, `PrimaryTabRow`, M3 sheets/dialogs, overlay `Switch` rows
- JSON models + NOAA/Kepler Kotlin + JVM `commons-suncalc` 3.11
- One `AlarmManager.setAlarmClock` slot + FSI lockscreen Activity + seven `AppWidgetProvider`s

The stack that **extracts** that code beats any stack that **rewrites** it, provided widgets and alarm chrome stay honest native APIs.

---

## 2. Options considered

Scored 1 (poor) – 5 (excellent) for *this* repo, not generic popularity.

| Criterion | KMP + CMP | Flutter | .NET MAUI | Avalonia | Tauri + shared logic |
|-----------|:---------:|:-------:|:---------:|:--------:|:--------------------:|
| Share UI (in-app) | 5 | 5 | 3 | 3 (desktop-strong, mobile weaker) | 2 (WebView, not Compose) |
| Share logic | 5 | 2 (rewrite Dart) | 2 (rewrite C#) | 2 (rewrite C#) | 3 (rewrite Rust/TS) |
| Fit with existing Kotlin/Compose + Canvas clocks | 5 | 2 | 1 | 2 | 1 |
| Extract vs rewrite ephemeris / models / next-fire | 5 | 1 | 1 | 1 | 2 |
| Native §1.20 access (AlarmKit, WidgetKit, Win widgets, Linux fallback, schedulers) | 4 | 3 (plugins + custom native) | 4 on Windows, 2 on Apple | 2 | 2 |
| Trajectory (iOS + desktop widgets, no hacks) | 5 | 4 | 3 | 3 | 2 |
| Maturity (2026 official status) | 5 in-app (CMP iOS/Desktop **Stable**) | 5 in-app | 3 mobile | 4 desktop / 2 iOS widgets | 4 desktop / 1 widgets |
| Effort to design+feature parity | 5 | 2 | 1 | 2 | 1 |
| FOSS / F-Droid | 5 (Kotlin/Java APK) | 3 (Flutter engine + native) | 2 (not F-Droid-native) | 3 | 3 (Rust/WebView) |
| **Weighted total (fit-heavy)** | **44** | **27** | **20** | **23** | **20** |

Weights implicit: fit + extract + FOSS count more than generic UI sharing.

Also considered and rejected without a full column: **rewrite three native UIs** (SwiftUI + Compose + WinUI) — maximum native chrome, triples the DESIGN_BRIEF work, throws away Compose. **React Native / Expo** — lists/sheets OK, Canvas clocks and F-Droid worse than Flutter.

### 2.1 Kotlin Multiplatform + Compose Multiplatform (**chosen**)

**Official stability (retrieved 2026-11-16):**

- KMP: Android, iOS, Desktop (JVM) **Stable**. watchOS **Beta**. Web Wasm **Beta**. Source: [Stability of supported platforms](https://kotlinlang.org/docs/multiplatform/supported-platforms.html).
- CMP UI: Android, iOS, Desktop (JVM) **Stable** (“use it even in the most conservative of scenarios”). Web Wasm **Beta**. Same page, and [Compose Multiplatform](https://kotlinlang.org/compose-multiplatform/).
- CMP **1.12.2** minimums: Android API 21, **iOS 14**, **macOS 13 arm64**, Windows 10, Ubuntu 20.04. Desktop packaging needs **JDK 17+** (`jpackage`). [Compatibility and versions](https://kotlinlang.org/docs/multiplatform/compose-compatibility-and-versioning.html).
- On Android, CMP uses Google’s Jetpack Compose artifacts (1.12.2 → Compose 1.12.1). Material3 artifacts on CMP 1.12.2 track `material3:1.12.0-alpha03` — token-level DESIGN_BRIEF still applies; do not invent a new theme.
- iOS rendering is Skia on a Metal-backed `UIView`, not a UIKit widget tree. Native views embed via `UIKitView` / `UIKitViewController`. [UIKit integration](https://kotlinlang.org/docs/multiplatform/compose-uikit-integration.html), [platform-specifics](https://kotlinlang.org/docs/multiplatform/compose-platform-specifics.html).
- **Home-screen widgets are not Compose.** They are OS extensions. CMP does not run inside WidgetKit, Windows Widgets Board, or a Linux panel.

**Why it wins:** the clocks *are* Canvas. Sharing Skia Canvas + Compose lists/sheets is the shortest path to DESIGN_BRIEF fidelity. NOAA `SolarMath`, Kepler, `AstroNextFire`, JSON models, and most Composables can move to `commonMain`. Android-specific `AlarmManager`, FSI, `RemoteViews`, Wear stay in `androidMain`.

**Costs:** `commons-suncalc` is a **JVM** library (`org.shredzone.commons:commons-suncalc:3.11`). iOS Kotlin/Native cannot link it. Plan: keep it as a JVM `actual` for Android/desktop tests; put a Kotlin implementation of the *used* suncalc surface in `commonMain` (or a dedicated `astro-ephemeris` module) and require `docs/ACCURACY.md` / `EphemerisAccuracyTest` to pass on every target. AlarmKit / WidgetKit / Windows `IWidgetProvider` are still **native** modules.

### 2.2 Flutter

Mature in-app UI (Impeller is the iOS renderer; [Impeller](https://docs.flutter.dev/perf/impeller)). `CustomPainter` could redraw wheels, but every Kotlin file is a rewrite. WidgetKit is a native extension with the `home_widget` plugin ([Apple frameworks table](https://docs.flutter.dev/platform-integration/ios/apple-frameworks)). AlarmKit is **not** a first-party Flutter API; community `flutter_alarmkit` requires Flutter 3.38+ / Xcode 26 / iOS 26 and throws on older iOS ([pub.dev/flutter_alarmkit](https://pub.dev/packages/flutter_alarmkit)). F-Droid allows the Flutter SDK as a prebuilt ([Inclusion Policy](https://f-droid.org/docs/Inclusion_Policy/)) but reproducible native engine paths are harder than Kotlin/Java ([Reproducible Builds](https://f-droid.org/docs/Reproducible_Builds/)). Rejected: rewrite cost vs an already-Compose codebase.

### 2.3 .NET MAUI

C# UI sharing is real on mobile; Windows Widgets Board is the *easiest* Adaptive Cards story ([Widget providers](https://learn.microsoft.com/en-us/windows/apps/develop/widgets/widget-providers)). Apple AlarmKit/WidgetKit still need native bindings. Entire ephemeris and Compose Canvas layer would be rewritten. Not a FOSS/F-Droid-natural Android path. Rejected.

### 2.4 Avalonia

Excellent for a Windows/Linux/macOS **window**. iOS/Android exist but WidgetKit/AlarmKit are still native add-ons, and there is no Compose-shaped Canvas inventory to reuse. Rejected as the *primary* app stack; a future Linux panel host could still be a tiny native/Avalonia sidecar if CMP’s Linux window Fallback is ugly — that would be a DESIGN_BRIEF Deviation, not a stack flip.

### 2.5 Tauri + shared logic

Rust core + WebView. Widgets and AlarmKit still 100% native. Clocks would be Canvas2D/WebGL, not the current Compose/Android Canvas. F-Droid would ship a WebView/Rust APK unlike today’s Kotlin app. Rejected: maximum rewrite, weakest widget story.

---

## 3. Native API facts that constrain every option

### 3.1 iOS AlarmKit (Durable scheduler on iOS 26+)

Cited: [AlarmKit](https://developer.apple.com/documentation/alarmkit), [AlarmManager](https://developer.apple.com/documentation/alarmkit/alarmmanager), WWDC25 [Wake up to the AlarmKit API](https://developer.apple.com/videos/play/wwdc2025/230/).

- Requires user authorization (`AlarmManager.requestAuthorization`, `NSAlarmKitUsageDescription`).
- Alert **breaks through Silent mode and Focus**. Custom title + app name; Stop and optional Snooze / custom App Intent button.
- Also surfaces on Dynamic Island, StandBy, and a **paired Apple Watch** (system, not a third-party watch face).
- **Schedules:** `Alarm.Schedule.fixed` (one absolute date, does not track timezone changes) and `Alarm.Schedule.Relative` (clock-of-day + optional **weekly** recurrence, does track timezone).
- **Astronomical events cannot be expressed as Relative weekly clocks.** Sunrise/jieqi/natal must use **fixed next Instant**, then reschedule after fire — the same “one OS timer” model as Android `REQUEST_CODE_ALARM = 8801`. This is why INVENTORY Q2’s recommended default is “keep one slot.”
- Countdown / snooze UI is a **WidgetKit Live Activity** (`ActivityConfiguration(for: AlarmAttributes<…>)`), SwiftUI, not Compose. Custom sound via ActivityKit `AlertSound`.
- App Intents can run on Stop / secondary. They **cannot** present the DESIGN_BRIEF math dialog on SpringBoard before the alarm is silenced.
- **iOS 14–25:** AlarmKit is unavailable. Durable Fallback is `UserNotifications` time-sensitive / notification actions + in-app ring UI. Do not require Critical Alerts entitlement as a FOSS default (INVENTORY §1.20).

#### Math-before-silence on AlarmKit — **PENDING** (verified 2026-11-16)

Edward’s rule: wherever the platform permits, math must be solved **before** the alarm can be silenced, including notification Stop. **AlarmKit cannot enforce that.** Do not ship a workaround (hide Stop, overlay SpringBoard, intercept `mobiletimerd`).

**What is possible (Apple):**

| Capability | Source |
|------------|--------|
| Authorization, schedule, cancel | [AlarmKit](https://developer.apple.com/documentation/alarmkit), [AlarmManager](https://developer.apple.com/documentation/alarmkit/alarmmanager) |
| `Alarm.Schedule.fixed` (one Instant) vs weekly `Relative` | same; WWDC25 [230](https://developer.apple.com/videos/play/wwdc2025/230/) |
| Alert breaks Silent mode and Focus; title + app name | WWDC25 230 |
| **System Stop is always offered.** People may stop or (optionally) snooze | WWDC25 230: “People have the option of stopping the alert or snoozing” |
| Optional **secondary** button: snooze countdown **or** custom App Intent (`.custom` does not mutate alarm state; `.countdown` snoozes) | [AlarmConfiguration](https://developer.apple.com/documentation/alarmkit/alarmconfiguration) `secondaryIntent` / `SecondaryButtonBehavior` |
| `stopIntent` — App Intent that **runs when Stop is tapped** (bookkeeping, UI teardown). It does **not** veto Stop | AlarmConfiguration `stopIntent` |
| Programmatic silence after the user finishes in-app math: `AlarmManager.stop(id:)` | [stop(id:)](https://developer.apple.com/documentation/alarmkit/alarmmanager/stop(id:)) |
| Live Activity countdown UI is SwiftUI WidgetKit, not Compose | WWDC25 230 |

**What is not possible (no supported API found):**

- Hide or replace the system Stop button.
- Require a math (or any) challenge **before** Stop silences the alert.
- Make Stop open the app without first stopping the alarm.
- Run Compose / DESIGN_BRIEF `MathChallengeDialog` on SpringBoard.

Empirical (non-Apple, do not treat as contract): third-party write-ups such as Wakeprint report the system mutates alarm state **before** `stopIntent.perform()`. Even if true, that is not a supported veto.

**Honest iOS product path:** Open-from-alert still shows shared math UI; call `AlarmManager.stop(id:)` only after success. System Stop on the AlarmKit banner **will silence without math**. Mark iOS math-before-silence **PENDING** on `PARITY_CHECKLIST.md` until Apple adds a supported gate. UserNotifications Fallback on iOS 14–25 has the same honesty: notification actions cannot host the math dialog; in-app Stop can.

### 3.2 WidgetKit (iOS home + Lock Screen, macOS desktop)

Cited: [WidgetKit](https://developer.apple.com/documentation/widgetkit), [Creating a widget extension](https://developer.apple.com/documentation/widgetkit/creating-a-widget-extension), WWDC25 [What’s new in widgets](https://developer.apple.com/videos/play/wwdc2025/278/).

- Widget **extension** target; SwiftUI `Widget` + `TimelineProvider`. Sizes include extra-large **macOS** widgets; widgets exist on the **macOS desktop** and Notification Center.
- Updates are **timeline snapshots**, not 1 Hz `RemoteViews`. Matches INVENTORY §1.20 Fallback for live analog bitmaps.
- Compose Multiplatform **does not run in the widget process**. Shared work is: Kotlin renders a PNG (existing disk renderers) or supplies JSON; SwiftUI displays `Image` / text. Same split as today’s Android `RemoteViews` + bitmap. **Crisp AC:** PNG at native scale, or SwiftUI vector; never a 600 px bitmap stretched.
- **watchOS complications** (Edward 2026-11-16, Sprint M10): WidgetKit accessory families `accessoryCircular`, `accessoryRectangular`, `accessoryInline`, `accessoryCorner`. Cited: [WidgetFamily](https://developer.apple.com/documentation/widgetkit/widgetfamily), WWDC22 [Go further with Complications in WidgetKit](https://developer.apple.com/videos/play/wwdc2022/10050/), [Complications and widgets: Reloaded](https://developer.apple.com/videos/play/wwdc2022/10051/). Not a `WatchFaceService` clone; CMP watchOS remains Beta and is not required for complications (native SwiftUI extension).

### 3.3 Windows Widgets Board

Cited: [Widget providers](https://learn.microsoft.com/en-us/windows/apps/develop/widgets/widget-providers), [manifest](https://learn.microsoft.com/en-us/windows/apps/develop/widgets/widget-provider-manifest).

- Third-party widgets: **packaged** Win32 (or PWA) implementing `IWidgetProvider`, registered as `com.microsoft.windows.widgets`.
- UI is **Adaptive Card JSON** (or a web-widget HTML URL), not Compose Canvas. Disk widgets: host a **snapshot image** inside the card. Upcoming/natal-align: text columns matching DESIGN_BRIEF §8 hex.
- CMP desktop app can be the packaged host; the provider COM surface is still a small native (C++/C# or JNI) adapter.

### 3.4 Linux

No single public widget host equivalent to AppWidget / WidgetKit / Widgets Board (INVENTORY §1.20). Honest Fallback: a small always-available CMP window and/or a documented GNOME/KDE applet later. Do not fake `AppWidgetProvider`.

### 3.5 F-Droid / FOSS

Cited: [Inclusion Policy](https://f-droid.org/docs/Inclusion_Policy/), [Reproducible Builds](https://f-droid.org/docs/Reproducible_Builds/).

- FLOSS source + FLOSS deps; no Play Services / Firebase (already ADR-0003).
- Kotlin/Java apps are the easiest reproducible F-Droid builds. Flutter/Rust/native engine paths embed build paths more often.
- F-Droid CI is **Linux**: Kotlin/Native iOS targets must be `ignoreDisabledTargets` so the **Android** module still builds there.
- Q4 (F-Droid listing) is unresolved. Recommended default: keep the Android APK F-Droid-eligible; do **not** block iOS/desktop on a listing recipe.

---

## 4. Chosen architecture — shared vs platform-specific

```
                    ┌─────────────────────────────────────────┐
                    │ commonMain (KMP + CMP)                  │
                    │  models, JSON, NOAA, Kepler, suncalc-kt │
                    │  AstroNextFire, FireIdentity, tokens    │
                    │  Compose: Alarms, Daily, Sky, Settings, │
                    │           About, Feedback, editor,      │
                    │           math dialog, disk Canvas      │
                    └───────────────┬─────────────────────────┘
          ┌─────────┬───────────────┼───────────────┬─────────┐
          ▼         ▼               ▼               ▼         ▼
     androidMain  iosMain      desktopMain     swift Widget  Win IWidgetProvider
     AlarmManager AlarmKit*    notifications   WidgetKit     Adaptive Cards
     FSI Activity UserNotif.   login-item      (PNG/JSON)    (PNG/JSON)
     RemoteViews  App Intents  CMP Window      Live Activity Linux window
     Wear face    auth/location
     suncalc JVM
```

`*` AlarmKit only when iOS ≥ 26 and authorized; else UserNotifications.

| Layer | Shared (`commonMain`) | Platform-specific |
|-------|----------------------|-------------------|
| Alarm / place / birth models, JSON codecs | Yes | Storage: DataStore/SharedPreferences (Android), App Group UserDefaults/file (iOS), file (desktop) |
| Ephemeris, next-fire, fire-identity, output policy, math generator | Yes | — |
| In-app Compose screens (INVENTORY §1.2 dest. 2–10) | Yes | Entry: Activity / `ComposeUIViewController` / `Window` |
| Overlay toggles, editor, sheets, Tonight card, empty mark | Yes | System ringtone picker Fallback per OS |
| Disk / globe / jieqi / Sol / natal **render to bitmap or Compose Canvas** | Yes | Widget **hosts** are native |
| Onboarding nag | Shared layout | Permission rows: omit exact-alarm / FSI / battery on iOS/desktop (§1.20 Omit) |
| Lockscreen / ring chrome | Shared **Stop / time / Snooze / math** composable (DESIGN_BRIEF §4.7) | Android: FSI Activity; **notification Stop must present math** (M5). iOS: AlarmKit Live Activity (SwiftUI, system Stop **PENDING** for math) + in-app composable when opened; `AlarmManager.stop(id:)` after math. Desktop: notification + focused window while session awake |
| Seven widgets | Shared snapshot renderer + upcoming/align copy | Android `AppWidgetProvider`. iOS/macOS **SwiftUI WidgetKit**. Windows **Adaptive Cards**. Linux **window/applet** |
| Wear analog face | Day/night approx logic if useful | **androidMain** Wear face; **watchOS WidgetKit complications** (M10) |
| Scheduling | Next Instant in common | Android `setAlarmClock`. iOS AlarmKit `fixed` or UNNotification. Desktop OS notification/task while session can receive it |
| Location | City catalog | Core Location / GPS vs catalog-only on desktop |
| TTS / haptics / share | Policy in common | AVSpeech / Narrator; haptics APIs; share sheet / save file |
| Updates | — | Android: GitHub Releases (apply button stays off). iOS: store. Desktop: signed installer |

**Do not share:** WidgetKit extensions, AlarmKit Live Activity SwiftUI, Windows `IWidgetProvider`, Linux panel plugins, Wear watch-face XML, Android FSI Activity scaffolding, `BOOT_COMPLETED` receivers.

---

## 5. How the current Android app stays buildable

1. **No big-bang replace.** `examples/android` remains the shippable `applicationId org.astroalarm` until a named cutover row in `BUILD_PLAN.md`.
2. **Add modules beside it** (names indicative): `:astro-domain`, `:astro-ephemeris`, `:astro-ui` (`composeApp` common), with `android` target first. iOS/desktop targets may be declared but CI on Linux sets `kotlin.native.ignoreDisabledTargets=true`.
3. **First extract is JVM-identical:** move pure Kotlin that already has unit tests (`AstroNextFire`, `SolarMath`, `PlanetKepler`, JSON). `examples/android` depends on the module; `./gradlew test` and `feature-gate --stack android` must stay green after every extract sprint.
4. **UI move last among Android work:** `MainActivity` / `GoldenPathApp` become thin hosts (≤10 lines per FEATURE_MODULES rule) calling `AstroRoot()`. Until then, Composables can stay where they are and *call* shared renderers.
5. **Widgets keep `RemoteViews`.** They already consume bitmaps; point them at the shared renderer. Do not require Glance.
6. **Do not enable iOS as a gate on Android CI** until the iOS sprint. Android feature-gate is the production gate throughout.
7. **commons-suncalc** stays on the Android classpath until the common Kotlin ephemeris harness matches `docs/ACCURACY.md` (±180 s, 30 NOAA dates). Then JVM `actual` may still call suncalc as a cross-check.

---

## 6. Suitability vs INVENTORY §1.20 (chosen path)

| Behavior | Path on KMP+CMP |
|----------|-----------------|
| In-app screens, wheels, lists, sheets | Durable — CMP Stable iOS/Desktop |
| Ephemeris / JSON | Durable — common Kotlin |
| Exact fire | Durable scheduler: AlarmKit (iOS 26) / UNNotification / desktop session notifications; **one next Instant** (Q2 default) |
| FSI lockscreen Activity | Fallback — shared ring composable inside OS-honest chrome |
| MAX / bypass DND | Fallback — AlarmKit breaks Focus on iOS 26; otherwise honor Focus |
| Math on lockscreen | Fallback chrome; **product:** math-before-silence where possible. AlarmKit Stop **PENDING** |
| Exact-alarm / FSI / battery onboarding | Omit those rows |
| Home widgets | Durable hosts: WidgetKit, Windows Widgets, Android AppWidget; Linux Fallback window |
| `requestPinAppWidget` | Fallback — instructions |
| 1 Hz widget canvas | Fallback — timeline/snapshot |
| Wear face | Android Wear stays; **watchOS complications** (Q5 answered) |
| GPS | Durable on iOS; Fallback catalog on desktop |
| UnifiedPush / ntfy | Android wake path for M13; **not** an iOS distributor; no FCM |

---

## 7. M0 questions — answers and remaining defaults

INVENTORY 1–5. Edward answered Q1, Q3, Q5 on 2026-11-16. Q2 and Q4 remain recommended defaults (not approved). M6/M10 are **unblocked**.

| # | Question | Status |
|---|----------|--------|
| Q1 | Ringing Fallback UX / math-before-silence | ✅ **Answered.** Wherever the platform permits, math **must** be solved before silence, **including notification Stop**. Drop the old “shade Stop skips math” default. **iOS AlarmKit: PENDING** (§3.1). |
| Q2 | Keep one OS timer? | 🔲 Recommended: **yes.** AlarmKit `fixed` next Instant + reschedule. Relative weekly clocks are only for `CustomClock` if we later opt in. Does not block M6. |
| Q3 | Notification Stop vs lockscreen Stop | ✅ **Answered.** Do **not** preserve the split. Shade/notification Stop must not silence until math succeeds, on every OS that can enforce it. |
| Q4 | F-Droid listing still a goal? | 🔲 Recommended: **later**, unchanged from ADR-0003. Keep the Android APK FLOSS-clean and Linux-buildable. Do not block iOS. |
| Q5 | Wear on iOS? | ✅ **Answered.** Ship **watchOS complications** (WidgetKit accessory families). Android Wear analog face stays. Not a third-party watch-face clone. |

---

## 8. Critique

| Issue | Resolution |
|-------|------------|
| Null/empty at boundary | Shared JSON codecs validate the same schemas as today; empty alarm list / missing place already have DESIGN_BRIEF empty states. Gate: domain unit tests in the extract sprint. |
| Network timeout | N/A for ephemeris (on-device). Chart-agent loopback stays Android-optional (INVENTORY Q12, not in the five). |
| Race (two schedulers) | One OS timer (Q2 default) + `AlarmFireIdentity` in common; platform adapters must not arm a second clock. Test: next-fire + adapter contract tests. |
| Unhandled AlarmKit denial | `requestAuthorization` failure → UserNotifications Fallback, same ring composable; no crash path. iOS sprint acceptance. |
| commons-suncalc missing on iOS | common Kotlin ephemeris + accuracy harness **before** iOS UI. Android keeps suncalc until harness matches. |
| CMP Material3 alpha skew | Tokens from `design-tokens.json` remain source of truth (DESIGN_BRIEF). Visual gate vs brief, not vs a new theme. |
| Widget Compose temptation | Forbidden: WidgetKit/Windows/Linux hosts are native. Shared renderer only. Called out in BUILD_PLAN widget chunks. |
| Android break during extract | `feature-gate --stack android` after every AGENT extract row; `examples/android` stays the application module. |

---

## 9. What this decision is not

- Not a license to redesign (`DESIGN_BRIEF.md` binding statement). Crisp widgets are an **APPROVED** quality Deviation, not a new look.
- Not approval of remaining PROPOSED §1.20 chrome Deviations.
- Not a requirement to ship iOS < 14 (CMP floor) or AlarmKit-only (need UserNotifications Fallback).
- Not a stack pick for Huawei `.hwt`.
- Not implementation of M1+ (separate PRs). This docs branch records answers only.

## 10. Edward answers 2026-11-16 (beyond Q1–Q5)

| Topic | Decision | Sprint |
|-------|----------|--------|
| First desktop widget | Daily 2D **24-hour clock dial, solar noon pointing up** (`AstroClockWidgetProvider` / `isShowSolarNoonUp2D`) | M7, M9 first |
| Widget quality | Same design, **crisp** native density or vector on every platform | Every widget sprint AC; Android fix is a **separate PR from `main`** |
| Export/import | **New stated feature** (not parity). Versioned documented file in `commonMain`; must include natal calendar; round-trip tests | M12 |
| Desktop asleep / logged out | **Approved limitation** — alarms may be missed; no hidden agent | M8 |
| Cross-device dismiss + data sync | **New stated scope** after parity | M13; see §11 |

## 11. Cross-device sync — research and recommendation

**Wanted:** dismissing/silencing an alarm on one device stops it ringing on the others, plus general data sync (alarms, natal, prefs). FOSS, local-first, no Play Services / FCM.

### Options

| Path | Data sync | Stop a ringing alarm remotely | FOSS / local-first |
|------|-----------|-------------------------------|--------------------|
| **LAN peer-to-peer** (mDNS + TLS, pairing code, no account) | Yes, when both devices are on the same network and awake | Yes if the peer can receive the packet (app listening / already ringing) | Strong. No vendor. NAT/cellular fails. |
| **Optional self-hosted relay** (same protocol over the user’s VPS; e.g. a small HTTPS/WebSocket inbox) | Yes off-LAN | Android: can deliver a “stop id” if the device can reach the relay. iOS: background fetch is **not** reliable enough to stop AlarmKit in time | Strong if the user hosts it. Project must not run a required cloud. |
| **UnifiedPush / ntfy (Android)** | Wake + small payload | **Durable on Android** if a distributor (ntfy, NextPush, ntfy self-host) is configured. App then cancels notification + `AlarmManager` slot | FOSS. [UnifiedPush](https://unifiedpush.org/) is **Android**. ntfy is AGPLv3 self-hostable. |
| **APNs (iOS)** | Wake + payload | **Required to stop a ringing AlarmKit alarm when the app is suspended.** `AlarmManager.stop(id:)` only runs in process. There is no UnifiedPush distributor on iOS. Silent/background push still goes through Apple | Needs an Apple Developer push cert **and a server** that talks APNs. Can be a tiny self-hosted relay (ntfy can speak APNs with a cert; a dedicated FOSS relay is the same). **Not** FCM. |

**iOS remote silence — honest limit:** AlarmKit alerts are system-owned. Apple documents `AlarmManager.stop(id:)` as the programmatic stop ([docs](https://developer.apple.com/documentation/alarmkit/alarmmanager/stop(id:))). Getting that call to run while the phone is locked and the app is suspended is a **remote-notification** problem. Apple’s supported remote path is **APNs**. LAN-only stop works only if the iOS app is already running (or the user opens it). Do not use private VoIP hacks or Critical Alerts as a stand-in.

### Recommendation (one approach)

1. **Data plane (all platforms):** local-first document sync using the **M12 backup format** as the payload. Default transport = **LAN P2P** (TLS + pairing code). Optional **user-hosted relay** when devices are not on the same LAN. Last-write-wins per alarm/profile with a monotonic revision; natal included. No accounts.
2. **Dismiss fan-out:** same document + a `stop_ringing(alarmId, occurrence)` message.
   - **Android:** apply locally; also send UnifiedPush/ntfy so a sleeping phone can cancel the ring. FOSS, self-hostable.
   - **Desktop:** apply only while the session is awake (Q22).
   - **iOS:** apply when the app is running / on LAN. **Remote silence of a ringing AlarmKit alarm is PENDING unless Edward accepts APNs** via a small self-hosted relay that holds the Apple push certificate. Do not ship an APNs-free “workaround.”
3. **Do not** add Firebase Cloud Messaging, Play Services, or a vendor account.

### Decisions still open for Edward

1. **APNs for iOS remote-stop?** Yes (self-hosted relay + Apple push cert, FOSS client) vs keep iOS remote-stop **PENDING** (LAN/foreground only).
2. **Who hosts the optional relay?** User-only (recommended) vs a project-run public instance (privacy/ops cost).
3. **Does desktop participate in dismiss fan-out?** Recommended: yes, when the session is awake.
4. **Pairing:** pairing codes (recommended) vs accounts.
5. Q2 (one OS timer) and Q4 (F-Droid listing) are still unanswered and are **not** sync decisions.

M13 must not start until M11 parity ✅ unless Edward pulls it forward. Feature spec + protocol doc land with that sprint.
