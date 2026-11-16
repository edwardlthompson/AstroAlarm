# AstroAlarm parity checklist

Seeded from [`INVENTORY.md`](INVENTORY.md) Appendix B, §§1.2–1.20, and [`DESIGN_BRIEF.md`](DESIGN_BRIEF.md). All items start **unchecked**.

**Status:** 🔲 not verified · ✅ verified on that target · ❌ waived only with an **approved** DESIGN_BRIEF Deviation.

**How to use:** after each migration BUILD_PLAN chunk, tick the rows that chunk claims. The final parity sprint must leave no 🔲 on Durable in-app items. Fallback/Omit rows tick as “honest Fallback documented” or “Omit confirmed,” never as a fake Android clone.

**Targets:** Android (current) · iOS · desktop (Windows / macOS / Linux).

---

## Rule

Re-read `INVENTORY.md`, `DESIGN_BRIEF.md`, and `TECH_DECISION.md` before checking anything off. Visual ticks require DESIGN_BRIEF fidelity, not “close enough.”

---

## A. Screens (INVENTORY §1.2 — 11)

| # | Surface | Inv | Android | iOS | Desktop |
|---|---------|-----|:-------:|:---:|:-------:|
| 1 | Onboarding gate (`PermissionNagDialog`) | dest. 1 | 🔲 | 🔲 | 🔲 |
| 2 | Alarms tab | dest. 2 | 🔲 | 🔲 | 🔲 |
| 3 | Daily · 2D | dest. 3 | 🔲 | 🔲 | 🔲 |
| 4 | Daily · 3D | dest. 4 | 🔲 | 🔲 | 🔲 |
| 5 | Sky · Yearly (jieqi) | dest. 5 | 🔲 | 🔲 | 🔲 |
| 6 | Sky · Sol | dest. 6 | 🔲 | 🔲 | 🔲 |
| 7 | Sky · Chart | dest. 7 | 🔲 | 🔲 | 🔲 |
| 8 | Settings | dest. 8 | 🔲 | 🔲 | 🔲 |
| 9 | About | dest. 9 | 🔲 | 🔲 | 🔲 |
| 10 | Feedback | dest. 10 | 🔲 | 🔲 | 🔲 |
| 11 | Ring UI (Stop / time / Snooze / math) | dest. 11 | 🔲 | 🔲 | 🔲 |

Design-fidelity notes: DESIGN_BRIEF §§5.1–5.11. iOS/desktop dest. 11 is the **shared composable** in OS-honest chrome (TECH_DECISION §4), not an FSI clone.

---

## B. Dialogs (INVENTORY §1.3 — 13)

| Dialog | Android | iOS | Desktop |
|--------|:-------:|:---:|:-------:|
| Permission nag (non-cancelable; ring steps) | 🔲 | 🔲 | 🔲 |
| `AstroEditDialog` (full catalog) | 🔲 | 🔲 | 🔲 |
| Sunrise offer | 🔲 | 🔲 | 🔲 |
| Delete confirm | 🔲 | 🔲 | 🔲 |
| Math challenge (20 dp card, 32 sp ExtraBold) | 🔲 | 🔲 | 🔲 |
| Birth chart details | 🔲 | 🔲 | 🔲 |
| Solar event list | 🔲 | 🔲 | 🔲 |
| Lunar event list | 🔲 | 🔲 | 🔲 |
| Zodiac event list | 🔲 | 🔲 | 🔲 |
| Jieqi / seasonal term picker | 🔲 | 🔲 | 🔲 |
| Launch donate prompt | 🔲 | 🔲 | 🔲 |
| Launch update prompt | 🔲 | 🔲 | 🔲 |
| Ringtone / tone picker (system or bundled Fallback) | 🔲 | 🔲 | 🔲 |

---

## C. Sheets (2)

| Sheet | Android | iOS | Desktop |
|-------|:-------:|:---:|:-------:|
| Jieqi detail | 🔲 | 🔲 | 🔲 |
| Natal glyph explain | 🔲 | 🔲 | 🔲 |

---

## D. In-screen chrome

| Piece | Android | iOS | Desktop |
|-------|:-------:|:---:|:-------:|
| Alarm permission banner | 🔲 | 🔲 | 🔲 |
| Tonight sky card | 🔲 | 🔲 | 🔲 |
| Location card + city catalog | 🔲 | 🔲 | 🔲 |
| GPS locate (Durable phones; desktop catalog Fallback) | 🔲 | 🔲 | 🔲 |
| Empty alarms mark (96 dp brand) | 🔲 | 🔲 | 🔲 |
| Clock overlay toggles (48 dp rows, 85% surfaceVariant, 12 dp) | 🔲 | 🔲 | 🔲 |
| Natal live toggles | 🔲 | 🔲 | 🔲 |
| Sky share 2160 PNG (1080 OOM fallback) | 🔲 | 🔲 | 🔲 |
| Pin-widget CTA (12 dp radius primary) | 🔲 | 🔲 | 🔲 |
| FAB Add Alarms-only (200 ms fade+scale) | 🔲 | 🔲 | 🔲 |
| PrimaryTabRow Alarms/Daily/Sky | 🔲 | 🔲 | 🔲 |
| Daily 2D\|3D segmented | 🔲 | 🔲 | 🔲 |
| Sky Yearly\|Sol\|Chart segmented | 🔲 | 🔲 | 🔲 |
| Next Due / Grouped chips | 🔲 | 🔲 | 🔲 |
| Snackbars (not Toast on clock pin) | 🔲 | 🔲 | 🔲 |

---

## E. Alarm target families (16)

| `AlarmTarget` | Editor | Next-fire | Row icon | Android | iOS | Desktop |
|---------------|:------:|:---------:|:--------:|:-------:|:---:|:-------:|
| CustomClock | 🔲 | 🔲 | ⏰ | 🔲 | 🔲 | 🔲 |
| Solar (18 event types) | 🔲 | 🔲 | ☀️ | 🔲 | 🔲 | 🔲 |
| Lunar (11) | 🔲 | 🔲 | 🌙 | 🔲 | 🔲 | 🔲 |
| Zodiac | 🔲 | 🔲 | sign | 🔲 | 🔲 | 🔲 |
| SolarTerm (24 jieqi) | 🔲 | 🔲 | 🍃 | 🔲 | 🔲 | 🔲 |
| Planet | 🔲 | 🔲 | 🪐 | 🔲 | 🔲 | 🔲 |
| PlanetAlign | 🔲 | 🔲 | 🪐 | 🔲 | 🔲 | 🔲 |
| AllPlanetsAlign | 🔲 | 🔲 | 🪐 | 🔲 | 🔲 | 🔲 |
| Occasion | 🔲 | 🔲 | ✨ | 🔲 | 🔲 | 🔲 |
| NatalAscAspect | 🔲 | 🔲 | ✨ | 🔲 | 🔲 | 🔲 |
| NatalMcAspect | 🔲 | 🔲 | ✨ | 🔲 | 🔲 | 🔲 |
| MoonReturn | 🔲 | 🔲 | ✨ | 🔲 | 🔲 | 🔲 |
| SolarReturn | 🔲 | 🔲 | ✨ | 🔲 | 🔲 | 🔲 |
| MercuryStation | 🔲 | 🔲 | ✨ | 🔲 | 🔲 | 🔲 |
| MoonSignIngress | 🔲 | 🔲 | ✨ | 🔲 | 🔲 | 🔲 |
| NatalCompoundSpecific / Any | 🔲 | 🔲 | ✨ | 🔲 | 🔲 | 🔲 |

---

## F. Widgets (INVENTORY §1.5, DESIGN_BRIEF §8 — 7)

Snapshot cadence (not 1 Hz) is required on iOS/desktop.

| Widget | Android AppWidget | iOS WidgetKit | macOS WidgetKit | Windows Adaptive Card | Linux window/applet |
|--------|:-----------------:|:-------------:|:---------------:|:---------------------:|:-------------------:|
| 2D clock (plain hub, night canvas) | 🔲 | 🔲 | 🔲 | 🔲 | 🔲 |
| 3D clock (globe snapshot) | 🔲 | 🔲 | 🔲 | 🔲 | 🔲 |
| Upcoming alarms (hex card) | 🔲 | 🔲 | 🔲 | 🔲 | 🔲 |
| Yearly jieqi | 🔲 | 🔲 | 🔲 | 🔲 | 🔲 |
| Sol | 🔲 | 🔲 | 🔲 | 🔲 | 🔲 |
| Natal chart | 🔲 | 🔲 | 🔲 | 🔲 | 🔲 |
| Natal align (no in-app pin on Android) | 🔲 | 🔲 | 🔲 | 🔲 | 🔲 |

---

## G. Design tokens (DESIGN_BRIEF §2)

| Token | Android | iOS | Desktop |
|-------|:-------:|:---:|:-------:|
| Light/dark Material roles (exact hex) | 🔲 | 🔲 | 🔲 |
| Canvas night `#1a1a2e` / gold `#C9A227` | 🔲 | 🔲 | 🔲 |
| No `dynamicColorScheme` | 🔲 | 🔲 | 🔲 |
| Type scale 22/28/16/14/14 sp system font | 🔲 | 🔲 | 🔲 |
| Spacing 4/8/16/24/32 · radius 4/8/16 · elevation 0/1/3 | 🔲 | 🔲 | 🔲 |
| Overlay 12 dp / 85% surfaceVariant | 🔲 | 🔲 | 🔲 |
| Alarm card 16 dp · math card 20 dp | 🔲 | 🔲 | 🔲 |
| Lockscreen 64/24/18 sp, 56 dp Stop/Snooze | 🔲 | 🔲 | 🔲 |
| NatalGlyphPalette + SolarTermPalette | 🔲 | 🔲 | 🔲 |
| Upcoming widget `#FFD54F` / `#E6121824` (not canvas gold) | 🔲 | 🔲 | 🔲 |
| Theme System / Light / Dark; lockscreen forced dark | 🔲 | 🔲 | 🔲 |

---

## H. Motion, a11y, i18n (DESIGN_BRIEF §§7, 9)

| Item | Android | iOS | Desktop |
|------|:-------:|:---:|:-------:|
| Reduce motion skips FAB/empty/pager/Stop-scale | 🔲 | 🔲 | 🔲 |
| FAB 200 ms · empty 120 ms · Stop 80 ms @ 0.92 | 🔲 | 🔲 | 🔲 |
| Yearly highlight 150 ms | 🔲 | 🔲 | 🔲 |
| Haptics: switch Confirm, Stop LongPress, Snooze Confirm | 🔲 | 🔲 | 🔲 |
| Wheel TalkBack custom actions | 🔲 | 🔲 | 🔲 |
| Overlay min 48 dp | 🔲 | 🔲 | 🔲 |
| Locales en / es / fr; zh jieqi names | 🔲 | 🔲 | 🔲 |

---

## I. Scheduling and ring path (INVENTORY §§1.6, 1.13, 1.16, 1.20)

| Item | Android | iOS | Desktop |
|------|:-------:|:---:|:-------:|
| One next Instant armed (Q2 default) | 🔲 | 🔲 | 🔲 |
| Durable OS scheduler (no hidden agent) | 🔲 | 🔲 | 🔲 |
| Reboot / login persistence (no fake BOOT_COMPLETED on iOS) | 🔲 | 🔲 | 🔲 |
| Timezone / time-set recompute | 🔲 | 🔲 | 🔲 |
| Notification Stop/Snooze skip math (Q3 default) | 🔲 | 🔲 | 🔲 |
| In-app Stop honors math (Q1 default: math not on shade) | 🔲 | 🔲 | 🔲 |
| Tone + TTS + vibrate policy | 🔲 | 🔲 | 🔲 |
| AlarmKit (iOS 26+) or UserNotifications Fallback | — | 🔲 | — |
| Session-only fire when logged out (Q22, not in five) | — | — | 🔲 |

---

## J. §1.20 Omit / Fallback named (must not be faked)

| Item | iOS | Desktop |
|------|:---:|:-------:|
| No FSI overlay on OS lock UI | 🔲 | 🔲 |
| No exact-alarm / FSI / battery Settings clones | 🔲 | 🔲 |
| No 1 Hz widget canvas | 🔲 | 🔲 |
| No Wear-face clone (Q5 default) | 🔲 | 🔲 |
| No desktop keyguard widgets | — | 🔲 |
| No UnifiedPush/FCM | 🔲 | 🔲 |
| Linux widgets = window/applet, not AppWidget | — | 🔲 |

---

## K. Wear (INVENTORY §1.15)

| Item | Android | iOS | Desktop |
|------|:-------:|:---:|:-------:|
| Analog day/night face | 🔲 | Omit unless Q5 overturned | Omit |

---

## L. Ephemeris (INVENTORY §1.11)

| Item | Shared tests |
|------|:------------:|
| NOAA 30-date ±180 s harness | 🔲 |
| Polar null rise/set | 🔲 |
| Southern jieqi season alias | 🔲 |
| Whole-sign houses only | 🔲 |

---

## M. Known Android gaps (INVENTORY §1.19) — do not “fix” silently

Tick only if Edward explicitly wants the gap closed; otherwise preserve.

| Gap | Preserve | Close (needs HUMAN) |
|-----|:--------:|:-------------------:|
| No NavHost / widget tab extra | 🔲 | 🔲 |
| `canApplyUpdate = false` | 🔲 | 🔲 |
| Settings export/import strings only | 🔲 | 🔲 |
| UnifiedPush payload ignored | 🔲 | 🔲 |
| NatalAlign no in-app pin | 🔲 | 🔲 |
| Notification Stop bypasses math | 🔲 | 🔲 |
