# Build Plan

<!-- remaining-tally -->
**Remaining:** AGENT 11 · AUTO 1 · HUMAN 6 · ADB 0 · **18 open**
<!-- /remaining-tally -->
> Done work: [COMPLETED_TASKS.md](COMPLETED_TASKS.md). Feature protocol: [docs/FEATURE_MODULES.md](docs/FEATURE_MODULES.md).

**Status:** 🔲 open · ✅ done · ❌ blocked · **Owners:** AGENT · HUMAN · ADB · AUTO

Sequential [AGENT] first, then Parallel. After each AGENT step: python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto.

---

## Persistent rule — migration docs before any implementation

**Before writing or moving app code for this product, re-read all three:**

1. [`docs/migration/INVENTORY.md`](docs/migration/INVENTORY.md) — current surface, §1.20 Durable/Fallback/Omit
2. [`docs/migration/DESIGN_BRIEF.md`](docs/migration/DESIGN_BRIEF.md) — existing visual/UX contract (binding statement)
3. [`docs/migration/TECH_DECISION.md`](docs/migration/TECH_DECISION.md) — KMP + CMP; native widgets/alarm chrome

Do not invent design. Do not fake Android lockscreen/FSI/Doze/Wear on iOS or desktop. Tick [`docs/migration/PARITY_CHECKLIST.md`](docs/migration/PARITY_CHECKLIST.md) as chunks land. **This file’s migration sprints are planning only until an AGENT row is executed in a later session.**

---

## Migration — Kotlin Multiplatform (large chunks)

Stack: **KMP + Compose Multiplatform** for domain + in-app UI; **native** WidgetKit / AlarmKit / Windows `IWidgetProvider` / Linux window-or-applet (`TECH_DECISION.md`). `examples/android` remains the production APK until Sprint M5 cutover criteria pass.

**Five unresolved questions** (INVENTORY 1–5) block named later rows. Recommended defaults live in `TECH_DECISION.md` §7 — they are **not** Edward-approved until the HUMAN rows below are ✅.

Each AGENT/AUTO row: inventory refs · parity AC · design-fidelity AC · gate vs inventory + brief. After the last AGENT/AUTO row in a sprint is ✅, run `python3 scripts/agent-run.py smoke-sprint --require` before the next sprint.

<!-- parallel_exception: sequential extracts; Parallel only after M2 public API of :astro-domain is locked -->

### Sprint M0 — Unresolved questions

1. 🔲 [HUMAN] **Q1 Ringing Fallback UX.** Time-sensitive/AlarmKit alert vs math-from-notification-action. Recommended default: system Stop/Snooze on the alert; **math only in the in-app ring UI** after open (`TECH_DECISION.md` §7, DESIGN_BRIEF Deviations). Inventory: §1.6, §1.16, §1.20.
2. 🔲 [HUMAN] **Q2 Single OS timer.** Keep one next Instant (`REQUEST_CODE_ALARM = 8801`) on AlarmKit `fixed` / UNNotification / desktop session notifications? Recommended default: **yes** (AlarmKit Relative cannot express sunrise). Inventory: §1.13, §1.20.
3. 🔲 [HUMAN] **Q3 Notification Stop vs in-app Stop.** Shade skips math today. Recommended default: **preserve the split**. Inventory: §1.6, §1.19 item 7.
4. 🔲 [HUMAN] **Q4 F-Droid listing.** Still later (ADR-0003)? Recommended default: **keep Android FLOSS-clean and Linux-buildable; do not block iOS**. Inventory: §1.18, §2.1.
5. 🔲 [HUMAN] **Q5 Wear on iOS.** WatchOS complications vs Android-only face? Recommended default: **Android Wear only**; AlarmKit already rings a paired Watch. Inventory: §1.15, §1.20.

### Sprint M1 — KMP/CMP skeleton (Android still ships)

1. 🔲 [AGENT] Add KMP modules beside `examples/android` (`:astro-domain`, `:astro-ephemeris`, `:astro-ui` as empty/android-first targets). Do not delete or rename `applicationId org.astroalarm`. iOS/desktop targets may be declared; Linux CI must `kotlin.native.ignoreDisabledTargets`.
   - Inventory: §1.1, §1.18
   - Parity: existing APK still installs; no feature dropped
   - Design: no UI change
   - Gate: `python3 scripts/agent-run.py feature-gate --stack android` + `check-repo-hygiene`; diff vs INVENTORY §1.1 (still Android application module)

### Sprint M2 — Extract common domain (models, persistence contracts, ephemeris, next-fire)

1. 🔲 [AGENT] Move **whole** alarm/place/birth models, JSON codecs, NOAA `SolarMath`, Kepler, suncalc façade, `AstroNextFire`, `AlarmFireIdentity`, math generator into `commonMain` (JVM `actual` may keep `commons-suncalc` until the Kotlin surface matches). Android app depends on the modules; no screen rewrites.
   - Inventory: §§1.9–1.13, §1.16 math, Appendix A
   - Parity: all 16 `AlarmTarget` subtypes round-trip; 18 solar + 11 lunar + 24 jieqi next-fire tests; 54-city catalog loads
   - Design: N/A (no pixels)
   - Gate: existing JVM unit tests + `scripts/check-ephemeris-accuracy.sh` / `EphemerisAccuracyTest` (±180 s, 30 NOAA dates, INVENTORY §1.11); `feature-gate --stack android`; checklist §E/§L still 🔲 until iOS runs the same tests

### Sprint M3 — Shared disk renderers (all clock families)

1. 🔲 [AGENT] Extract **all** Canvas disk/globe/jieqi/Sol/natal painters and palettes (`NatalGlyphPalette`, `SolarTermPalette`, `DiskCenterHub`, `WheelDisk`, overlays) to shared code that can paint Compose Canvas **and** PNG snapshots. Android widgets keep `RemoteViews` + those bitmaps.
   - Inventory: §1.5, §1.7, Appendix A widgets
   - Parity: seven snapshot types exist; 2D hub is plain night disk (not Earth)
   - Design: DESIGN_BRIEF §§2.9, 5.3–5.7, 8 (hex, palettes, transparent square corners)
   - Gate: renderer unit tests + visual compare against DESIGN_BRIEF §8 and Fastlane notes; Android widgets still update; `feature-gate --stack android`

### Sprint M4 — Shared in-app Compose (screen group: home + settings chrome)

1. 🔲 [AGENT] Move the **entire** in-app surface to `commonMain` Compose: onboarding nag layout, Alarms (list, Tonight card, editor, all pickers, sheets), Daily 2D/3D, Sky Yearly/Sol/Chart, Settings/About/Feedback. `GoldenPathApp` / `MainActivity` become thin hosts (FEATURE_MODULES ≤10 lines). Lockscreen **Activity** stays Android until M5; it should already call a shared ring composable.
   - Inventory: §1.2 dest. 1–10, §1.3 dialogs/sheets, §1.4, §1.8
   - Parity: 11 destinations still reachable; 13 dialogs + 2 sheets; FAB Alarms-only; chips persisted
   - Design: DESIGN_BRIEF §§4–7, 9 (tokens, 48 dp overlays, motion, a11y)
   - Gate: `feature-gate --stack android`; walk PARITY_CHECKLIST A–D/G/H against DESIGN_BRIEF (Android column may tick when visually matched)

### Sprint M5 — Android platform adapters (keep shipping)

1. 🔲 [AGENT] Rewire **only** androidMain: `AlarmManager.setAlarmClock` one slot, FSI lockscreen Activity, channel `astroalarm_alarm_v3`, seven `AppWidgetProvider`s, Wear face, onboarding permission rows, GPS. Production APK remains this module.
   - Inventory: §§1.5, 1.6, 1.14, 1.15, 1.20 Durable Android column
   - Parity: fire path, widgets, Wear unchanged for Android users
   - Design: DESIGN_BRIEF §4.7 lockscreen; §8 widgets
   - Gate: `feature-gate --stack android`; instrumented/emulator if SDK present; PARITY_CHECKLIST I (Android) + F (Android)

### Sprint M6 — iOS app host + AlarmKit / notifications

1. ❌ [AGENT] **Blocked on M0 Q1–Q3.** iOS target: shared Compose UI in `ComposeUIViewController`; Durable scheduler = AlarmKit `fixed` next Instant on iOS 26+ else UserNotifications; shared ring composable when the user opens the app; system Stop/Snooze. **Recommended defaults if HUMAN accepts them:** Q1 math in-app only; Q2 one timer; Q3 shade/system Stop skips math. Do not draw over SpringBoard.
   - Inventory: §1.2 dest. 11, §1.6, §1.14, §1.20 Fallback lockscreen/DND/math
   - Parity: dest. 1–11 present; 16 alarm families schedule; Omit exact-alarm/FSI/battery rows
   - Design: DESIGN_BRIEF §4.7 inside in-app surface; AlarmKit Live Activity is SwiftUI (TECH_DECISION §3.1) — document as OS chrome, not a Deviation from in-app Stop/time/Snooze
   - Gate: iOS build; PARITY_CHECKLIST A/I/J iOS vs INVENTORY §1.20 + DESIGN_BRIEF; no private APIs

### Sprint M7 — iOS + macOS WidgetKit (all seven faces)

1. 🔲 [AGENT] Native **SwiftUI WidgetKit** extensions (home + iOS Lock Screen snapshots + macOS desktop widgets). Shared Kotlin PNG/JSON only. Timeline cadence, not 1 Hz. Pin CTA = instructions Fallback.
   - Inventory: §1.5, §1.20 widgets / pin / keyguard / 1 Hz
   - Parity: seven widgets exist on iOS; macOS desktop widgets for the same seven unless Edward later subsets (Q16 is not in the five — default **all seven**)
   - Design: DESIGN_BRIEF §8 sizes/hex/plain hub
   - Gate: PARITY_CHECKLIST F iOS/macOS vs DESIGN_BRIEF §8 and INVENTORY §1.5

### Sprint M8 — Desktop JVM apps (Windows / macOS / Linux windows)

1. 🔲 [AGENT] CMP **desktop** targets: same shared in-app UI in a Window; file-backed stores; notifications/tasks **only while the session can receive them** (no hidden agent). macOS app hosts the WidgetKit bundle from M7.
   - Inventory: §1.2, §1.8, §1.20 desktop scheduler Fallback, Q22 (not in five; recommended: session-only fire)
   - Parity: dest. 2–10 usable; GPS omitted (catalog + search)
   - Design: DESIGN_BRIEF in-app sections; window chrome native
   - Gate: desktop package (JDK 17 `jpackage` per CMP docs); PARITY_CHECKLIST A–E desktop vs brief + inventory

### Sprint M9 — Windows Widgets Board + Linux Fallback host

1. 🔲 [AGENT] Windows packaged `IWidgetProvider` Adaptive Cards (image + upcoming/align text). Linux: always-available CMP window or panel applet — **not** a fake AppWidget. Shared snapshot renderer from M3.
   - Inventory: §1.5, §1.20 Linux Fallback, Windows Durable snapshots
   - Parity: seven faces or documented subset (default all seven)
   - Design: DESIGN_BRIEF §8; Adaptive Card is a host constraint (like RemoteViews), pixels from shared PNG
   - Gate: PARITY_CHECKLIST F Windows/Linux vs INVENTORY §1.20 + DESIGN_BRIEF §8

### Sprint M10 — Wear (Android-only unless Q5 overturned)

1. ❌ [AGENT] **Blocked on M0 Q5.** watchOS complications or a third-party face. Recommended default: **do not implement**; keep `examples/android/wear` as androidMain only (covered by M5). Flip this row to Omit ✅ on the checklist when HUMAN confirms Q5 default.
   - Inventory: §1.15, §1.20 Wear Fallback/Omit
   - Parity: Android Wear remains; iOS/desktop Omit
   - Design: N/A if Omit
   - Gate: PARITY_CHECKLIST K vs INVENTORY §1.20

### Sprint M11 — Final parity verification

1. 🔲 [AGENT] Walk [`PARITY_CHECKLIST.md`](docs/migration/PARITY_CHECKLIST.md) against INVENTORY Appendix B and DESIGN_BRIEF §§2–9 on every shipped target. No 🔲 on Durable in-app items. Fallback/Omit rows must be named, not faked. File DESIGN_BRIEF Deviation PRs only for Edward-approved gaps.
   - Inventory: Appendix B, §1.19 (do not silently “fix” gaps), §1.20
   - Parity: screens 11, dialogs 13, sheets 2, widgets 7, families 16
   - Design: token hex, lockscreen typography, widget hex, motion, a11y
   - Gate: checklist complete; `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`; `python3 scripts/agent-run.py smoke-sprint --require`; `feature-gate` for android plus any added iOS/desktop gates; accuracy harness
2. 🔲 [AUTO] CI: Android feature-gate + hygiene + license; iOS/desktop jobs only where runners exist; F-Droid recipe still stub unless Q4 ✅
   - Inventory: §1.18
   - Parity: FOSS path unchanged
   - Design: N/A
   - Gate: `check-github-ci.sh --wait 300` (or project equivalent); `check-license-compliance`
3. 🔲 [HUMAN] Optional device smoke: Android lockscreen math, iOS AlarmKit/UserNotifications, one widget per OS. Agents do not wait on this to mark M11.1 ✅ if automated gates passed.

---

## Child Repo Playbook

### Sprint 2+ — Incremental Features

<!-- parallel_exception: board empty after outer-crop + WIP restore archive -->

> Natal chart disk widget archived in COMPLETED_TASKS.md. Template catch-up from agent-project-bootstrap v1.4.0 on chore/template-catchup-v1.4.0.

> **Sprint 2+ alarms-first UX-1…17** archived in COMPLETED_TASKS.md.

> **Sprint 2+ Human & device** archived in COMPLETED_TASKS.md (phoneScreenshots, 60s/90s/TalkBack/share, Dependabot #21).

> **Sprint 2+ Incremental Features — outer crop + WIP restore** archived in COMPLETED_TASKS.md.

> **Product base = published v1.10.0** (origin/main, versionCode ≥ 10109). Do not continue feature work on the parked 1.7.0 WIP branch (park/wip-1.7.0-features). On tree (uncommitted until ship): alarm-tone-tts, clock-2d-noon-up, sky-occasions, wear-watchface, Chart agent (docs/features/birth-agent.md), Daily 2D outer-clip fix. Dropped: duplicate org.astroalarm.natal calculator/UI (published astro.birth Chart is canonical).

### Sequential

_(none open — product feature board empty; migration is the section above)_

### Parallel

| Task | Owner | Scope |
|------|-------|-------|
| — | — | _(none open)_ |
#### Human & device (after automation)

_(none open)_

### Open PRs (synced)

> Auto-managed. Do not hand-edit rows inside the markers.

<!-- open-prs-sync:begin -->
_No open Dependabot or Release Please PRs._
<!-- open-prs-sync:end -->
