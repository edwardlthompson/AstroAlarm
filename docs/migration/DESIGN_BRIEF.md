# AstroAlarm Phase 1 design brief

> Documentation only. Captures the **existing** Android product as shipped in source (app **1.11.0** / `versionCode` 10110), not a redesign and not a technology pick.
> Companion: [`INVENTORY.md`](INVENTORY.md) (behavior, data, platform paths). This file is the visual/UX contract.

**The new implementation must match this brief. Any deviation must be documented in the Deviations section with justification and approved by Edward before implementation.**

Deviations: §1.20 Fallback/Omit rows stay **PROPOSED** unless marked **APPROVED** below. Edward 2026-11-16 **approved** the crisp-widget rendering Deviation (same design, native density or vector). Math-before-silence is a **product rule**, not a visual Deviation.

**Method:** Compose, XML widget layouts, generated theme files, Fastlane screenshots, and `design-tokens/design-tokens.json`. This Cloud Agent environment had **no Android SDK** (`ANDROID_HOME` unset), so Compose previews and an emulator were **not** run. Live lockscreen, Settings, editor, 3D, Sol, Chart, and widget-picker shots are missing; Fastlane frames are cited with staleness notes. See [What could not be captured](#what-could-not-be-captured).

---

## 1. Design principles (as evidenced by the current app)

These are observed from the running UI and token files — not invented product slogans.

| Principle | Evidence |
|-----------|----------|
| **Night-sky chrome, gold accents** | Dark background `#12121f`, surface `#1a1a2e`, canvas gold `#C9A227`. Primary is a wine/rose (`#B3263E` / `#D3304E`), not gold. Gold is reserved for canvas/brand, not for Material primary. |
| **Single token source, with known exceptions** | App chrome uses `design-tokens/design-tokens.json`. Wheels add dedicated palettes (`NatalGlyphPalette`, `SolarTermPalette`). Upcoming/natal-align widgets hardcode hex in XML (see §3). |
| **Material 3, not Material You** | `GoldenPathTheme` uses static light/dark schemes. **No** `dynamicColorScheme`. Do not tint wheels from wallpaper. |
| **Circular disks, not rectangular clocks** | Daily 2D / 3D / Yearly / Sol / Chart inscribe a circle (`WheelDisk` / `CircleShape`). Square bitmap corners stay transparent. |
| **Plain 2D hub** | Daily 2D center is a small night-colored disk (`DiskCenterHub`), **not** Earth. 3D still uses the globe. Empty-alarms illustration still shows an Earth hub (brand mark). |
| **Nothing-style segments, Material tabs** | Daily 2D\|3D and Sky Yearly\|Sol\|Chart are `SingleChoiceSegmentedButtonRow`. Home Alarms/Daily/Sky is `PrimaryTabRow`. |
| **Alarms-first** | Default landing is Alarms. FAB Add is Alarms-only. Editor is a full-catalog dialog, not a wizard. |
| **Lockscreen is a dedicated dark surface** | Ring UI is a full-bleed Activity forced **Dark**, Stop on top (error), time + label center, Snooze bottom. Math is a 20 dp-radius card dialog, not a second Activity. |
| **Reduce motion is OS-driven** | Animator/transition scale 0 skips FAB/empty/pager/Stop-scale animation. No in-app motion toggle. |
| **TalkBack on wheels, 48 dp overlay rows** | Custom actions: next event, reset zoom, share. Overlay switches `heightIn(min = 48.dp)`. |
| **Copy in strings, not code** | User-visible labels from `strings.xml` (en/es/fr; zh solar-term names). Alarm-row type marks are emoji strings in `AlarmTargetCopy.icon`. |
| **FOSS, no proprietary chrome** | No Google-branded widgets, no Play Services identity, no dynamic-color wallpaper SDK. |

Canonical token workflow (existing): `docs/DESIGN_GUIDE.md`. Product identity: `INVENTORY.md` §1.1.

---

## 2. Theme tokens

### 2.1 Source of truth

| Artifact | Path |
|----------|------|
| JSON tokens | `design-tokens/design-tokens.json` |
| Generated colors | `examples/android/app/src/main/java/dev/foss/goldenpath/ui/theme/Color.kt` |
| Generated type | `…/ui/theme/Type.kt` |
| Generated dimens | `…/ui/theme/Dimens.kt` |
| Theme wrapper | `…/ui/theme/Theme.kt` (`GoldenPathTheme`) |
| XML shell | `examples/android/app/src/main/res/values/themes.xml` (`Theme.Material.Light.NoActionBar`) |
| Canvas packed ARGB | `examples/android/app/src/main/java/org/astroalarm/ui/AstroCanvas.kt` |
| Swatch preview | [`design/token-swatches.svg`](design/token-swatches.svg) |

Do not invent extra roles. Reproduce these hex values.

### 2.2 Color — light / dark

`meta.themeColorLight` `#F5F5FA` · `meta.themeColorDark` `#1a1a2e`.

| Role | Light | Dark |
|------|-------|------|
| primary | `#B3263E` | `#D3304E` |
| onPrimary | `#FFFFFF` | `#FFFFFF` |
| primaryContainer | `#FFDAD6` | `#8B1A35` |
| onPrimaryContainer | `#410009` | `#FFDAD6` |
| secondary | `#4A5F82` | `#9BB8E8` |
| onSecondary | `#FFFFFF` | `#1a1a2e` |
| secondaryContainer | `#D8E3FF` | `#2a3a5c` |
| onSecondaryContainer | `#1a1a2e` | `#D8E3FF` |
| tertiary | `#6B5778` | `#D4BEE8` |
| onTertiary | `#FFFFFF` | `#1a1a2e` |
| error | `#BA1A1A` | `#FFB4AB` |
| onError | `#FFFFFF` | `#690005` |
| background | `#FFFFFF` | `#12121f` |
| onBackground | `#1a1a2e` | `#E6E6F0` |
| surface | `#F5F5FA` | `#1a1a2e` |
| onSurface | `#1a1a2e` | `#E6E6F0` |
| surfaceVariant | `#E2E2EC` | `#2a2a42` |
| onSurfaceVariant | `#45465A` | `#C4C4D4` |
| outline | `#767680` | `#8E8EA0` |
| **canvas.night** | `#1a1a2e` | `#1a1a2e` |
| **canvas.gold** | `#C9A227` | `#C9A227` |

Canvas night/gold are **identical** in light and dark. Daily 2D disk always paints on night, even when app chrome is light.

Brand JSON (`product.json`) lists primary `C9A227` and secondary `1A2744`. Those are marketing/badge colors. **In-app Material primary is the wine/rose pair above**, not gold.

### 2.3 Dynamic color

**Not used.** Search of the Android tree finds no `dynamicColorScheme`. README/Fastlane “Material You wheels” is copy drift (`INVENTORY.md` open question 17). Reproduce **static** schemes.

### 2.4 Dark / light behavior

| Surface | Behavior | Source |
|---------|----------|--------|
| App chrome | Settings theme chips: System (default) / Light / Dark. Persisted in DataStore (`ThemePreferences`). | `Theme.kt`, `INVENTORY.md` §1.8 |
| Lockscreen ring | **Forced Dark** regardless of Settings. | `INVENTORY.md` §1.7 |
| Daily 2D disk | Always canvas night `#1a1a2e`. | `AstroCanvas`, `DiskCenterHub` |
| Yearly jieqi wheel | Has its own dark/light `SolarTermPalette` (`wheelBg`, `ink`, `hub`). | `SolarTermPalette.kt` |
| Chart natal wheel | Glyph hues dim to 75% RGB in light. Halo is near-black on dark, near-white on light. | `NatalGlyphPalette.kt` |
| Upcoming / natal-align widgets | Always the hardcoded dark card (no light variant). | `widget_upcoming.xml`, `widget_natal_align.xml` |
| Disk widgets | Transparent frame; disk bitmap is the night (or jieqi) canvas. | `widget_astro.xml` |

### 2.5 Typography

Family: `system-ui, -apple-system, sans-serif`. **No bundled font files.**

Material roles from tokens (Compose `GoldenPathTypography`):

| Role | size | line-height | weight |
|------|------|-------------|--------|
| titleLarge | 22 sp | 27.9 sp (1.27) | 400 |
| headlineMedium | 28 sp | 33.6 sp (1.2) | 400 |
| bodyLarge | 16 sp | 24 sp (1.5) | 400 |
| bodyMedium | 14 sp | 20 sp (1.43) | 400 |
| labelLarge | 14 sp | 20 sp (1.43) | **500** |

**Local overrides** (not in the token file — reproduce as used):

| Use | Size / weight | Color |
|-----|---------------|-------|
| Alarm row label | 17 sp Bold | `onSurfaceVariant` |
| Alarm next-fire | 13 sp SemiBold | `primary` |
| Alarm disabled | 12 sp | `onSurfaceVariant` @ 60% |
| Alarm peer note | 12 sp | `tertiary` |
| Repeat day chip | 11 sp SemiBold | `onPrimary` selected / `onSurface` @ 50% unselected |
| Overlay toggle title | 14 sp Bold | default onSurface |
| Daily noon/midnight line | 13 sp Medium, maxLines 2 | default |
| Lockscreen time | **64 sp Bold** | `onBackground` |
| Lockscreen alarm label | **24 sp Medium**, centered | `primary` |
| Lockscreen TTS hint | 14 sp centered | `error` |
| Stop / Snooze labels | 18 sp Bold / SemiBold | `onError` / `onSecondaryContainer` |
| Math expression | **32 sp ExtraBold** | `primary` |
| Widget upcoming title | 14 sp Bold | `#FFD54F` |
| Widget line 1 | 13 sp Bold | `#FFFFFF` |
| Widget lines 2–3 | 13 / 12 sp | `#E0E0E0` / `#CFD8DC` |

### 2.6 Spacing

Token dp: **xs 4 · sm 8 · md 16 · lg 24 · xl 32**.

Observed layout (not extra tokens):

- Home content horizontal padding **16 dp** (`AstroAlarmsPage`, hub segmented rows).
- Alarm cards: inner padding **16 dp horizontal, 12 dp vertical**; list `spacedBy(10.dp)`.
- Overlay Surface inner: **12 dp horizontal, 2 dp vertical**.
- Lockscreen column: **24 dp** + status/nav bar padding.
- Widget disk frame: **8 dp** padding.
- Widget list card: **12 dp** padding.

### 2.7 Shapes / corner radii

Token: **sm 4 · md 8 · lg 16** dp.

| Element | Radius | Source |
|---------|--------|--------|
| Alarm row `Card` | **16 dp** | `AstroAlarmRow.kt` |
| Overlay toggle `Surface` | **12 dp** (not in token file) | `ZodiacToggleRow.kt` |
| Pin-widget `Button` | **12 dp** | `AstroClockScreen.kt` |
| Stop / Snooze | **16 dp** (`RadiusLg`) | `AstroAlarmLockscreenView.kt` |
| Math dialog `Card` | **20 dp** (not in token file) | same |
| Repeat-day chip `Surface` | **4 dp** | `AstroAlarmRow.kt` |
| Upcoming widget background | **16 dp** | `bg_widget_upcoming.xml` |
| Brand mark outer | 16 units on 200 viewport | `ic_brand_mark.xml` |
| Segmented buttons | Material 3 `SegmentedButtonDefaults.itemShape` | `AstroHubPages.kt` |
| FAB | Material 3 default FAB shape | `AstroScreen.kt` |
| Disk widgets | **circle** (`CircleShape` / `WheelDisk`); square bitmap corners transparent | `INVENTORY.md` §1.7 |

### 2.8 Elevation

Token: **level0 0 · level1 1 · level2 3** dp.

Alarm cards and overlay Surfaces use default M3 card/surface elevation (not a custom shadow). Disk widgets are **flat** (transparent `FrameLayout`). Upcoming card is a filled rounded rect, no elevation XML.

### 2.9 Extra palettes (wheels)

#### Natal glyphs — `NatalGlyphPalette.kt`

ARGB packed; dark is default.

| Role | RGB | Hex |
|------|-----|-----|
| ASC | 255,193,7 | `#FFC107` |
| MC | 186,104,200 | `#BA68C8` |
| IC | 0,121,107 | `#00796B` |
| DSC | 33,150,243 | `#2196F3` |
| house (dark) | 176,160,192 | `#B0A0C0` |
| house (light) | 90,70,110 | `#5A466E` |
| Sun | 255,196,48 | `#FFC430` |
| Moon | 220,228,240 | `#DCE4F0` |
| Mercury | 158,158,158 | `#9E9E9E` |
| Venus | 255,193,7 | `#FFC107` |
| Mars | 239,83,80 | `#EF5350` |
| Jupiter | 255,152,0 | `#FF9800` |
| Saturn | 161,136,127 | `#A1887F` |
| Uranus | 0,188,212 | `#00BCD4` |
| Neptune | 92,107,192 | `#5C6BC0` |

Outer planets (Jupiter–Neptune) draw at **alpha 200/255** on the wheel. Light-mode zodiac hues are **75%** of the dark RGB. Halo: dark `argb(220,18,18,18)`; light `argb(200,255,255,255)` — four-offset stroke then fill (`drawGlyphHalo`).

Zodiac (dark):

| Sign | Hex |
|------|-----|
| Aries | `#F4511E` |
| Leo | `#FF8F00` |
| Sagittarius | `#FF5722` |
| Taurus | `#43A047` |
| Virgo | `#66BB6A` |
| Capricorn | `#2E7D32` |
| Gemini | `#FFD600` |
| Libra | `#AB47BC` |
| Aquarius | `#7E57C2` |
| Cancer | `#29B6F6` |
| Scorpio | `#039BE5` |
| Pisces | `#0097A7` |

#### Jieqi (Yearly) — `SolarTermPalette.kt`

Season lerp across 6 terms (`phase = ordinal % 6`, `t = phase/5`). Dark multiplies RGB by **0.82**. Southern hemisphere **aliases season colors** by swapping spring↔autumn, summer↔winter (`displaySeason`).

| Season | From | To |
|--------|------|----|
| Spring | `rgb(46,160,120)` `#2EA078` | `rgb(90,200,170)` `#5AC8AA` |
| Summer | `rgb(230,160,40)` `#E6A028` | `rgb(200,70,50)` `#C84632` |
| Autumn | `rgb(210,120,50)` `#D27832` | `rgb(140,80,40)` `#8C5028` |
| Winter | `rgb(90,140,190)` `#5A8CBE` | `rgb(180,200,220)` `#B4C8DC` |

| Role | Dark | Light |
|------|------|-------|
| ink | `#ECF0F8` | `#1C2028` |
| muted | `#AAB4C4` | `#464E5A` |
| wheelBg | `#0C101C` | `#F6F2E8` |
| hub | `#161C2C` | `#FFFCFA` |

Armed jieqi marks: red dots on the term ray (`SolarTermAlarmDots.kt`).

#### Upcoming / natal-align widget (XML, not tokens)

| Role | Hex |
|------|-----|
| Card fill | `#E6121824` (`#121824` @ ~90%) |
| Card stroke | `#33FFFFFF` 1 dp |
| Title | `#FFD54F` |
| Line 1 / profile | `#FFFFFF` |
| Line 2 / double | `#E0E0E0` |
| Line 3 / triple | `#CFD8DC` |
| Count | `#B0BEC5` |
| Empty | `#90A4AE` |

Note: widget gold `#FFD54F` is **not** canvas gold `#C9A227`. Reproduce both.

---

## 3. Iconography and imagery

### 3.1 Brand

| Asset | Path | Use |
|-------|------|-----|
| Vector mark | `examples/android/app/src/main/res/drawable/ic_brand_mark.xml` | Empty Alarms illustration (96 dp, `contentDescription = null`) |
| SVG copies | [`design/app-icon-512.svg`](design/app-icon-512.svg), [`design/splash.svg`](design/splash.svg) from `branding/assets/` | Launcher / splash **art**. Splash SVG is **not** a splash Activity. |
| Adaptive icon | `res/mipmap-*` / `ic_launcher` | OS launcher |

Brand mark composition (viewport 200): rounded-rect space `#0D121D`; outer disk `#151E2E` stroke `#78909C`; day arc `#D8E8F8`; golden-hour wedges `#FFA726`; solar-noon white wedge; Earth `#1976D2` / land `#388E3C`. **This illustration still shows Earth in the hub.** The live Daily 2D disk does not.

### 3.2 Material icons (in-app)

From `material-icons-extended`:

| Icon | Where |
|------|-------|
| `Icons.Filled.Settings` | Home TopAppBar action |
| `Icons.AutoMirrored.Filled.ArrowBack` | Settings / About / Feedback |
| `Icons.Default.Add` | Alarms FAB (`astro_cd_add_alarm`) |
| `Icons.Default.AddCircle` | “Add 24-hour clock” / pin-widget buttons |
| `Icons.Default.Edit` | Alarm row edit (`astro_action_edit`) |
| `Icons.Default.Delete` | Alarm row delete, **tinted `error`** |

### 3.3 Emoji type marks (alarm rows)

`AlarmTargetCopy.icon` — leading emoji + space, **24 sp** in the row:

| Target family | Glyph |
|---------------|-------|
| Solar | ☀️ |
| Lunar | 🌙 |
| Zodiac | Unicode sign symbol (♈–♓) |
| CustomClock | ⏰ |
| SolarTerm | 🍃 |
| Planet / PlanetAlign / AllPlanetsAlign | 🪐 |
| Occasion, natal aspects, returns, stations, ingress, compounds | ✨ |

Jieqi enum still carries emoji in `SolarTerm.kt` for term marks on the Yearly wheel.

### 3.4 Raster textures (3D / Sol — cite, do not invent)

NASA-style Earth/Moon/planet maps live under `examples/android/app/src/main/res/drawable-nodpi/` (`EarthTexture.kt` samples them). Daily **2D no longer uses Earth** at the hub. 3D globe and Sol bodies still do. **Current Android pixel budget** `ClockRenderSize.fromMinDp` → `(minSideDp * 2.5).coerceIn(200, 600)` px is **chunky** (Edward). **APPROVED Deviation:** render at native widget pixel size × density, or as vector; do not keep the 600 px cap look on any platform.

### 3.5 Widget picker previews

Vector/XML drawables (not copied here): `preview_widget_clock_wheel`, `preview_widget_3d_clock`, `preview_widget_alarms`, `preview_widget_solar_terms`, `preview_widget_sol`, `preview_widget_natal`, `preview_widget_natal_align`. Each of the seven widgets has a **distinct** picker preview (`DECISION_LOG` 2026-09-15).

---

## 4. Component inventory

Reusable pieces with states. Screen composition is §5.

### 4.1 Chrome

| Component | States | Spec |
|-----------|--------|------|
| `TopAppBar` | Home: title `app_title` (“AstroAlarm”) + Settings icon. Sub-screens: back + Settings/About/Feedback title. | `GoldenPathScreen.kt`. Default M3 TopAppBar colors (surface / onSurface). |
| `PrimaryTabRow` | Selected index 0/1/2; indicator uses **primary**. Labels Alarms / Daily / Sky. | `AstroScreen.kt`. Swipe + tap. |
| `FloatingActionButton` | Visible only on Alarms tab. Enter/exit: fade+scale **200 ms**, skipped if reduce-motion. Primary container, onPrimary icon. | `UxMotion.FAB_MS` |
| `GoldenPathScaffold` | Snackbar host; edge-to-edge insets. | `ui/components/GoldenPathScaffold.kt` |
| `AppSnackbar` | Pin success / pin manual guide. | `WidgetPinEvents` |

### 4.2 Hub segments

| Component | States | Spec |
|-----------|--------|------|
| Daily `SingleChoiceSegmentedButtonRow` | 2D selected (default) / 3D selected. Full width, 16 dp horizontal, 4 dp vertical. Visible text labels. | `AstroHubPages.kt`, `docs/features/daily-sky-segmented.md` |
| Sky same | Yearly (default) / Sol / Chart. | same |

### 4.3 Alarm list

| Component | States | Spec |
|-----------|--------|------|
| `AstroAlarmRow` | Enabled + next-fire (primary 13 sp); enabled without fire time (label only); disabled (“Disabled” 12 sp @ 60%); optional tertiary peer note. | Card 16 dp radius, `surfaceVariant`. Icon 24 sp + label 17 sp Bold. Switch with `HapticFeedbackType.Confirm`. |
| Repeat chips | Yearly / Once / Daily / per-day. Day chips: selected = primary fill + onPrimary; unselected = surface + onSurface 50%. Radius 4 dp, pad 6×2. | `RepeatChips` |
| Tone/TTS/Vibrate/Math badges | Present or omitted (`BadgeText`). | FlowRow spaced 6×4 dp |
| Edit / Delete | IconButtons. Delete tinted error. | Replaced Fastlane-era text buttons |
| Sort chips | Next Due vs Grouped `FilterChip`s. | `AstroAlarmsPage.kt` |
| `TonightSkyCard` | Place missing → “need place”; else title + event line (`bodyLarge`) + context (`bodySmall` `onSurfaceVariant`). | Card `surfaceVariant`, 16 dp padding, 4 dp vertical outer |
| `AlarmPermissionBanner` | Hidden when ring steps granted; else Card + text + TextButton to OS settings. Refreshes on `ON_RESUME`. | `AlarmPermissionBanner.kt` |
| Empty list | Brand mark 96 dp (decorative) + `astro_empty_all`. Fade **120 ms**. | `EmptyAlarmsMark` |

### 4.4 Overlay toggles

`Surface` 12 dp corners, `surfaceVariant` @ **85% alpha**. Each row `heightIn(min = 48.dp)`, 14 sp Bold label + Switch (`contentDescription` = title).

| Set | Toggles (top→bottom) |
|-----|----------------------|
| Daily 2D `ClockOverlayToggles` | Event times, Hour marks, Solar noon up, Month ticks, Show zodiac |
| Natal `NatalLiveToggles` | Live Sun, Moon, Mercury |
| Generic `ZodiacToggleRow` | Single titled switch |

### 4.5 Disks / wheels

| Component | States | Spec |
|-----------|--------|------|
| Daily 2D disk | Overlays on/off; zoom/pan vs identity; share; pin CTA. Center = `DiskCenterHub` (night, r = size×0.022, clamp 3.5–9 px). Now-hand, gold sun, day/night pie, optional zodiac/event times/hour/month, alarm dots. | `AstroClockScreen.kt`, `AstroDiskRenderer` |
| Daily 3D globe | Same overlay family + accelerometer parallax (`ClockParallax.kt`) when motion allowed. Earth textures. | `Astro3DClockScreen.kt` |
| Yearly jieqi | 24 colored sectors; Earth+needle; tap sector → sheet; 150 ms highlight (`YearlyHighlight.MS`). Compact / event-times overlays. | `SolarTermScreen.kt` |
| Sol map | Kepler polylines, 1 AU bar, wakes, natal ghosts toggle. | `SolScreen.kt` |
| Natal wheel | Whole-sign houses, glyph halo, live overlays. Tap glyph → explain sheet. | `BirthChartScreen` / `NatalWheelSection` |
| `WheelDisk` | Circular clip, transparent corners. | widget + in-app |
| Share button | Top-end overlay; 2160 PNG, 1080 on OOM. | `SkyShareButton` |

### 4.6 Buttons / pickers / dialogs

| Component | States | Spec |
|-----------|--------|------|
| Pin-widget `Button` | Full width, 12 dp radius, AddCircle + 8 dp gap + label. | Daily 2D/3D, Yearly, Sol, Chart (not natal-align) |
| `AstroNumberWheel` | Hour / minute / offset; tactile fling. | `AstroWheelPicker.kt` |
| `TargetTypeSelector` | Solar / Lunar / Zodiac / Clock / Seasonal / Planet / Occasion / Natal (Natal only if profile). | `AstroCelestialPickers.kt` |
| `AstroEditDialog` | Add vs Edit; validation; tone picker. | `AstroEditDialog.kt` |
| Delete `AlertDialog` | Confirm / dismiss. | `AstroAlarmsPage.kt` |
| `SunriseOfferDialog` | First city; Add / dismiss. | `SunriseOfferDialog.kt` |
| `PermissionNagDialog` | Non-cancelable; Continue enabled only when **ring** steps granted. Location optional. | `ui/onboard/` |
| `MathChallengeDialog` | Idle, error (red bodySmall + regenerated problem), progress Badge when count > 1, solved → Stop. Cancel dismisses **without** stopping. | 20 dp Card, 32 sp ExtraBold expression |
| `LaunchPromptDialogs` | Donate / update. | `LaunchPromptDialogs.kt` |
| Event list dialogs | Solar / lunar / zodiac / jieqi. | `AstroEventListDialogs.kt`, `AstroCelestialPickers.kt` |
| `SolarTermDetailSheet` | Term name, dates, add-alarm. | modal bottom sheet |
| `NatalGlyphExplainSheet` | Glyph meaning. | modal bottom sheet |
| System ringtone picker | OS UI. | Fallback on other OS: bundled tones + OS sound picker (`INVENTORY.md` §1.20) |

### 4.7 Lockscreen ring

| Piece | Spec |
|-------|------|
| Root | Full-size `Surface` `background` (forced dark scheme). Column SpaceBetween, 24 dp, status+nav padding. |
| Stop | Top, fill width, **56 dp** height, **error / onError**, 16 dp radius, 18 sp Bold. On press: haptic LongPress, scale **0.92** for **80 ms** unless reduce-motion, then `onStop`. If math enabled → open dialog instead of Stop. |
| Time | Center, 64 sp Bold `onBackground`, `HH:mm` captured once at composition. |
| Label | 24 sp Medium `primary`, centered. Optional TTS hint 14 sp `error`. |
| Snooze | Bottom, 56 dp, `secondaryContainer` / `onSecondaryContainer`, 16 dp radius, 18 sp SemiBold, label includes snooze minutes. Haptic Confirm. **No math.** |
| A11y | `a11y_stop_alarm` / `a11y_snooze_alarm` include alarm label; Role.Button. |

### 4.8 Settings / About / Feedback / onboarding

Material 3 lists, `FilterChip`s for theme (not ADR-0004 dropdowns — **current chrome is FilterChips**, `INVENTORY.md` §1.19). Location card + GPS skeleton (three muted bars). About: version, donations, bug/feature. Feedback: form fields → GitHub issue URL. Onboarding: permission rows with granted/missing.

---

## 5. Screens (layout + inventory cross-refs)

Eleven full-page destinations (`INVENTORY.md` §1.2). Dialogs/sheets: §1.3. Flows: §1.4.

### 5.1 Onboarding gate — inventory dest. 1

Non-cancelable dialog overlay on first launch (`PermissionNagDialog`). Permission rows; Continue locked until ring steps (notifications, exact alarm, FSI, battery — **not** location) are granted. Visual: default M3 dialog on themed scaffold. No Fastlane shot.

### 5.2 Alarms tab — dest. 2 · capture [`design/01-alarms.png`](design/01-alarms.png)

**Chrome:** TopAppBar “AstroAlarm” + gear. `PrimaryTabRow` Alarms selected (primary underline). FAB + (primary) lower-end.

**Column** (16 dp horizontal):

1. Optional `AlarmPermissionBanner`.
2. `TonightSkyCard` (not in the Fastlane shot — added later).
3. Empty: 96 dp brand mark + empty copy **or** LazyColumn of `AstroAlarmRow` spaced 10 dp.
4. Next Due / Grouped `FilterChip` row.
5. Pin upcoming-widget CTA when applicable.

**Stale vs shot:** Fastlane still shows text Edit/Delete and no Tonight card. **Code is canonical.**

### 5.3 Daily · 2D — dest. 3 · capture [`design/02-daily.png`](design/02-daily.png)

Segmented **2D | 3D** (2D selected). Below: circular night disk filling width; share control top-end; primary pill “Add 24-hour clock” (AddCircle); two-line 13 sp noon/midnight + zodiac names; overlay Surface with five switches.

**Stale vs shot:** Fastlane center is Earth; **code uses `DiskCenterHub`** (plain night disk, 3.5–9 px radius). Overlay list in the shot omits “Solar noon up”; code order is Event times → Hour marks → Solar noon up → Month ticks → Zodiac.

### 5.4 Daily · 3D — dest. 4

Same segment row (3D selected). Globe with terminator, transit ticks, moon; accelerometer parallax. Same overlay/share/pin pattern. **No screenshot in repo.**

### 5.5 Sky · Yearly — dest. 5 · capture [`design/03-sky.png`](design/03-sky.png)

Segmented **Yearly | Sol | Chart**. 24-sector jieqi wheel (north-ecliptic-pole frame: ♈ at +X, Dōngzhì at 12, Xiàzhì at 6). Pin “Add year wheel”. Compact / Show event times overlays. Tap sector → `SolarTermDetailSheet`. Southern latitude swaps season colors.

### 5.6 Sky · Sol — dest. 6

Inner-solar Kepler map on night canvas, 1 AU bar, wakes, natal ghosts. Pin Sol widget. **No screenshot.**

### 5.7 Sky · Chart — dest. 7

Natal wheel + profile controls + live Sun/Moon/Mercury toggles. Details dialog; glyph sheet. Pin natal-chart widget. **No screenshot.**

### 5.8 Settings — dest. 8

Back + “Settings”. Place/search/GPS, theme FilterChips (System/Light/Dark), TTS, math difficulty/count, crash-save, review permissions, About entry. **No screenshot.** Current chrome is chips, not dropdowns.

### 5.9 About — dest. 9

Version, donations, bug/feature, OpenShouter. **No screenshot.**

### 5.10 Feedback — dest. 10

Bug vs feature title from `showFeedback`. Form → GitHub URL. **No screenshot.**

### 5.11 Lockscreen ring — dest. 11

Forced dark. Layout §4.7. **No screenshot.** Notification shade Stop/Snooze skip math (`INVENTORY.md` §1.6, §1.16) — visual of the shade is OS, not this brief.

---

## 6. Navigation structure and transitions

There is **no NavHost**. Structure (`INVENTORY.md` §1.2):

```
MainActivity
 └─ GoldenPathApp (theme, onboarding, launch prompts)
     └─ GoldenPathScreen
         ├─ HOME     AstroScreen  (HorizontalPager 3 pages)
         │            0 Alarms · 1 Daily (2D|3D chips) · 2 Sky (Yearly|Sol|Chart)
         ├─ SETTINGS SettingsScreen
         ├─ ABOUT    AboutScreen
         └─ FEEDBACK FeedbackScreen
```

| Transition | Motion |
|------------|--------|
| Tab tap | `animateScrollToPage` unless reduce-motion → `scrollToPage` |
| Pager swipe | HorizontalPager default |
| Daily/Sky chip | Instant body replace (no crossfade) |
| FAB show/hide | 200 ms fade+scale; none if reduce-motion |
| Empty list | 120 ms fade |
| Settings/About/Feedback | Boolean overlay, **no** shared-axis animation specified |
| Back | `BackHandler`: Feedback → About → Settings → (system). Home has no nested back stack |
| Lockscreen | Separate Activity (`taskAffinity`), appears over keyguard; no in-app enter transition coded |
| Stop press | 80 ms scale 0.92 then dismiss |
| Yearly sector | 150 ms highlight |
| Widgets | Open `MainActivity` with **no tab extra** (restore last pager/chip) |
| Deep link | `goldenpath://settings/about/feedback` cold start only (`INVENTORY.md` §1.2) |

---

## 7. Interaction patterns

### 7.1 Gestures

| Gesture | Target | Behavior |
|---------|--------|----------|
| Horizontal swipe | Home pager | Alarms ↔ Daily ↔ Sky |
| Pinch / pan | Disks | `WheelZoomPan` — zoom and pan the painted canvas; TalkBack “reset” restores identity |
| Tap disk (Yearly) | Sector | Detail sheet |
| Tap glyph (Chart) | Body/house/angle | Explain sheet |
| Tap Wikipedia affordance | Zodiac | Opens sign URL (Daily 2D) |
| Long-press | not used as a distinct app gesture | — |
| Accelerometer | 3D only | Parallax; Omit on desktop (`INVENTORY.md` §1.20) |

### 7.2 Pickers

Editor (`INVENTORY.md` §1.3): type selector → event list dialogs / jieqi dialog / `ClockTimePicker` + `AstroNumberWheel` / offset before-exact-after / planet pairwise / occasion chips / natal compound. Repeat-day Surfaces. Tone: system `ACTION_RINGTONE_PICKER`. Math difficulty in Settings, not in the ring UI.

### 7.3 Dialogs and sheets

- Modal dialogs: M3 `AlertDialog` / custom `Dialog`+`Card`.
- Math: **cancel does not Stop**; wrong answer regenerates a problem and sets error; N problems in sequence (`MathPreferences`).
- Bottom sheets: jieqi detail, natal glyph explain.
- Onboarding dialog **non-cancelable**.
- Delete confirm before persist.

### 7.4 Haptics

| Event | Type | Source |
|-------|------|--------|
| Alarm switch | `HapticFeedbackType.Confirm` | `AstroAlarmRow` |
| Stop (no math) | `LongPress` then scale | `AstroAlarmLockscreenView` |
| Snooze | `Confirm` | same |
| Stop (opens math) | `Confirm` | same |

No custom vibration pattern in the UI layer; alarm vibration waveform is a fire-path concern (`INVENTORY.md` §1.16).

### 7.5 Animations

| Token | Value |
|-------|-------|
| `UxMotion.FAB_MS` | 200 |
| `UxMotion.ROW_MS` | 180 (defined; FAB/empty are the wired ones) |
| `UxMotion.EMPTY_MS` | 120 |
| `LockscreenStop.SCALE_MS` | 80 |
| `LockscreenStop.PRESSED_SCALE` | 0.92 |
| `YearlyHighlight.MS` / `SkyHandTick` | 150 |

Reduce motion: `Settings.Global.ANIMATOR_DURATION_SCALE` or `TRANSITION_ANIMATION_SCALE` == 0 (`ReduceMotion.kt`). High-refresh `preferredDisplayModeId` is applied in `onStart` (not a user setting).

### 7.6 Ringing + math-unlock experience

Canonical visual is the lockscreen Activity (`INVENTORY.md` §1.6, §1.16):

1. Tone plays from the **Activity** (channel is silent). Screen kept on while this UI is visible.
2. User sees Stop (error, top) / time + label / Snooze (secondaryContainer, bottom).
3. **Snooze** never asks math.
4. **Stop** with math off: haptic + 80 ms scale-down + stop.
5. **Stop** with math on: `MathChallengeDialog` — title, optional `n/m` Badge, 32 sp ExtraBold `expression = ?`, numeric field (digits and minus only), Submit / Cancel. Wrong → error copy, field cleared, **new** problem. Complete `problemCount` in a row → Stop.
6. **Product rule (Edward 2026-11-16):** notification Stop must **not** silence until math succeeds, wherever the OS can enforce it. Today’s Android shade Stop is a gap to close (M5), not the visual to copy. Snooze still skips math. iOS AlarmKit system Stop is **PENDING** (`TECH_DECISION.md` §3.1) — do not invent a second Stop chrome.

Fallback visual if lockscreen takeover is not available: keep this same Stop / time / Snooze / math layout in OS-honest chrome (time-sensitive notification + in-app ring UI). That chrome swap is still the §1.20 Fallback; the **math-before-silence** rule is approved product behavior, not a new look.

---

## 8. Widget visual specs (all seven)

Inventory table: `INVENTORY.md` §1.5. Shared disk layout: `res/layout/widget_astro.xml` (transparent `FrameLayout`, 8 dp pad, `ImageView` `fitCenter`). List cards: `bg_widget_upcoming.xml`. Update floor 30 min. Resize horizontal|vertical. Category home_screen|keyguard except as noted. **No** configure activity.

**First desktop / WidgetKit widget:** row 1 — Daily 2D **24-hour clock dial with solar noon pointing up** (`AstroClockWidgetProvider`, `isShowSolarNoonUp2D`). **Crisp AC (APPROVED):** every face is the **same design** as this table, rendered at native pixel size × density or as vector. Do not ship the old 200–600 px cap look. `fitCenter` must not upscale a small bitmap.

| # | Widget | Info XML | Layout | Min / target cells | Preview drawable | In-app look |
|---|--------|----------|--------|--------------------|------------------|-------------|
| 1 | 2D clock **(first desktop widget)** | `astro_widget_info.xml` | `widget_astro.xml` | 250×250 dp; minResize 140; **4×4** | `preview_widget_clock_wheel` | Same Daily 2D disk (plain hub, night canvas, solar noon up when that pref is on). Snapshot, not 1 Hz. Native-density/vector. |
| 2 | 3D clock | `astro_3d_widget_info.xml` | same | 250×250; 140 resize; **4×4** | `preview_widget_3d_clock` | Same 3D globe as Daily 3D, no live parallax in the widget. |
| 3 | Upcoming alarms | `astro_upcoming_widget_info.xml` | `widget_upcoming.xml` | 250×110; resize 140×70; **4×2** | `preview_widget_alarms` | Dark rounded card 16 dp, 1 dp `#33FFFFFF` stroke, 12 dp pad. Title `#FFD54F` 14 sp Bold + count `#B0BEC5` 12 sp. Up to 3 lines (bold white / `#E0E0E0` / `#CFD8DC`). Empty centered `#90A4AE`. 48 h horizon. |
| 4 | Solar terms | `astro_solarterm_widget_info.xml` | `widget_astro.xml` | 140×140; resize 110; **2×2** | `preview_widget_solar_terms` | Same Yearly jieqi wheel. |
| 5 | Sol | `astro_sol_widget_info.xml` | same | 140×140; 110; **2×2** | `preview_widget_sol` | Same Sol map. |
| 6 | Natal chart | `astro_natal_chart_widget_info.xml` | same | 140×140; 110; **2×2** | `preview_widget_natal` | Same Chart wheel. |
| 7 | Natal align | `astro_natal_align_widget_info.xml` | `widget_natal_align.xml` | 250×110; 140×70; **4×2**. **No** `initialKeyguardLayout`. **No** in-app pin. | `preview_widget_natal_align` | Same card chrome as upcoming. Title `#FFD54F`. Profile `#FFFFFF` 13 sp Bold. Double `#E0E0E0` 12 sp. Triple `#CFD8DC` 12 sp. Empty `#90A4AE`. |

Tap: each provider opens `MainActivity` (pending request codes 8830 / 8850 / 8840 / 8870 / 8880 / 8849 / 8848) **without** selecting a tab.

Keyguard layouts duplicate home layouts where `initialKeyguardLayout` is set. AOSP dropped keyguard app widgets around Lollipop; do not invent a lock-screen-only widget family. Ringing is an Activity, not a widget.

Pin CTA in-app: primary `Button` 12 dp radius (“Add … clock/wheel”). Android uses `requestPinAppWidget`; visual fallback is snackbar copy (`astro_widget_pin_manual_guide`).

---

## 9. Accessibility behaviors

Sources: `INVENTORY.md` §1.17, `WheelTalkBack.kt`, lockscreen semantics, overlay min height.

| Behavior | Spec |
|----------|------|
| TalkBack labels | Content descriptions on FAB, Settings, Back, Edit, Delete, pin buttons, Stop/Snooze (include alarm label). Overlay switches use the visible title as `contentDescription`. |
| Wheel custom actions | `nextLabel`, `resetLabel`, `shareLabel` (`Modifier.wheelTalkBack`). |
| Empty mark | Decorative (`contentDescription = null`). |
| Overlay hit target | **48 dp** min row height (`OverlayToggle.MIN_DP`). |
| Reduce motion | OS animator/transition scale 0 → no FAB/empty/pager animate, no Stop scale. |
| Segmented chips | Visible text, not icon-only (`daily-sky-segmented.md`). |
| Math field | Number keyboard, IME Done submits; error announced via `isError` + bodySmall error copy. |
| Lockscreen | Role.Button on Stop/Snooze; large 56 dp bars. |
| Locales | en/es/fr full UI; zh solar-term names; Wear English only. Layout must survive translation (DESIGN_GUIDE). |
| RTL | AutoMirrored back icon. Disks are sky geometry (not mirrored as text). |

---

## 10. Deviations

**Status:** mix of **PROPOSED** (not approved) and **APPROVED** (Edward 2026-11-16). Durable items are not deviations: they must match this brief.

Each PROPOSED row is the **minimal visual/UX change** that item would force. APPROVED rows may be implemented.

| Item | Path | Status | Visual / UX change (minimal) |
|------|------|--------|------------------------------|
| **Widget render quality** | Product (all platforms) | **APPROVED** | **Same design as §8**, rendered **crisp** at native widget pixel size × density, or as vector. Do **not** carry the current Android `ClockRenderSize` 200–600 px cap / `fitCenter` upscale. Applies to Android AppWidget, WidgetKit, Windows Adaptive Cards, Linux window/applet. |
| Full-screen lockscreen Activity (FSI, `showWhenLocked`) | Fallback | PROPOSED | Keep the **same** Stop / time / Snooze / math layout as §4.7, shown when the user opens a time-sensitive notification (or a focused alarm window on desktop). Do not draw over the OS lock UI. No extra chrome. |
| MAX channel, `bypassDnd`, silent channel + Activity-owned tone | Fallback | PROPOSED | Same in-app ring UI. OS Focus / DND may suppress or duck the tone; do not add a fake “bypass DND” banner. |
| Math unlock **on the lockscreen** (chrome) | Fallback | PROPOSED chrome; **product rule APPROVED** | Same `MathChallengeDialog` (20 dp card, 32 sp expression) in the in-app ring UI. **Stop (including notification Stop) must run math** where the OS can enforce it. iOS AlarmKit system Stop is **PENDING** — do not hide Stop or overlay SpringBoard. Do not inject a dialog onto SpringBoard / login screen. |
| Desktop alarms while asleep / logged out | Approved limitation | **APPROVED** | No extra visual. Session-only fire (Q22). Do not add a “hidden agent” status row. |
| `WAKE_LOCK` / `FLAG_KEEP_SCREEN_ON` while ringing | Fallback | PROPOSED | No extra control. Screen stays awake only while the ring UI is visible. |
| `requestPinAppWidget` | Fallback | PROPOSED | Same primary 12 dp-radius “Add … widget” button; action shows **instructions** (existing `astro_widget_pin_manual_guide` snackbar pattern) instead of the Android pin sheet. |
| Keyguard widget category | Fallback / Omit | PROPOSED | iOS: snapshot Lock Screen widgets using the **same disk/list art** as home widgets. Desktop: **omit** keyguard widgets; do not invent a lock-screen widget. |
| Live analog bitmap every tick | Fallback | PROPOSED | Same disk/list art as snapshots, updating on the host timeline (minutes, not a spinning second-hand). No 1 Hz RemoteViews clone. Still **crisp** (APPROVED row above). |
| Exported `SET_ALARM` / `DISMISS_ALARM` | Fallback | PROPOSED | No dedicated visual. App Intents / URL “set alarm” may open the existing `AstroEditDialog`. Else no UI. |
| System `ACTION_RINGTONE_PICKER` | Fallback | PROPOSED | Same editor row; opens the **platform** sound picker or a list of bundled tones. Do not restyle as Android’s ringtone UI. |
| Alarm `USAGE_ALARM` audio stream | Fallback | PROPOSED | No visual. Tone still accompanies the ring UI / notification. |
| TTS while locked | Fallback | PROPOSED | Same optional error-colored TTS hint on the ring UI when that UI is shown; may also speak from the notification. No extra caption chrome. |
| Home-screen widgets on Linux | Fallback | PROPOSED | Same seven faces as §8, **first** the 24h noon-up dial, hosted as a small always-available window or panel applet — **not** a fake `AppWidgetProvider` frame. |
| Wear OS analog watch face | Fallback / Omit | **APPROVED** substitute | Do not clone WearFaceService on watchOS. **Ship WidgetKit complications** (accessory families). Glance art may use night/gold tokens; system complication chrome is OS. Android Wear analog face stays as today. |
| Accelerometer 3D parallax | Omit (desktop) | PROPOSED | 3D globe is **static** (same as the 3D widget). No fake gyro. |
| High-refresh display mode | Omit where unsupported | PROPOSED | Same scrolling; no in-app toggle. |
| Android Auto Backup | Fallback | PROPOSED | Export/import is now a **stated** Settings feature (M12); use existing Settings section chrome, no new visual language. |
| `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM` onboarding | Omit | PROPOSED | Drop those rows from onboarding. Remaining permission rows keep current nag layout. |
| `USE_FULL_SCREEN_INTENT` onboarding | Omit | PROPOSED | Drop that row. Ring UI still matches §4.7 when shown. |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` onboarding | Omit | PROPOSED | Drop that row. No fake battery-whitelist screen. |
| `RECEIVE_BOOT_COMPLETED` broadcast | Omit | PROPOSED | No visual. OS still delivers pending notifications after reboot / login item. |
| FileProvider APK apply | Omit on iOS | PROPOSED | About screen stays without the apply button (`canApplyUpdate = false` already). Desktop: store/installer, not an in-app APK bar. |
| Home-network sync | New stated (M13) | PROPOSED chrome | Settings opt-in + pairing-code dialog; no account, no FCM, no ntfy status row in M13. |
| Location GPS on desktop | Fallback | PROPOSED | Settings Location card keeps city catalog + typed search; **no** GPS spinner on desktop. |

Durable §1.20 rows (in-app screens, tokens, TalkBack, share PNG, catalogs, WidgetKit/Windows snapshot widgets, etc.) are **in scope for pixel/UX match** and are not listed here.

---

## What could not be captured

| Gap | Why | How to fill later |
|-----|-----|-------------------|
| Live Compose previews / emulator screenshots of lockscreen, Settings, editor, 3D, Sol, Chart, onboarding, widgets | `ANDROID_HOME` unset in this environment | Run the app or `@Preview` and drop PNGs beside `docs/migration/design/` |
| Fastlane Alarms/Daily staleness | Shots from ~2026-09-15: Earth hub, text Edit/Delete, no Tonight card, overlay list incomplete | Treat **code + this brief** as canonical; replace shots when convenient |
| Widget picker preview rasters | Previews are XML drawables, not PNG | Render `preview_widget_*` on device |
| Earth/Moon/planet texture appearance | Large `drawable-nodpi` bitmaps; not duplicated here | Cite those files at paint time |
| Notification shade Stop/Snooze | OS chrome; math-before-silence is product, not pixels | Android M5 must present math before Stop silences |
| Wear face on-wrist photo | No device | `INVENTORY.md` §1.15 |
| Light-mode full-app screenshots | Fastlane set is dark-only | Theme Light and capture; tokens in §2 still apply |
| Exact M3 segmented/tab indicator metrics | Defaults from the Compose M3 version on the classpath | Match Material 3 defaults + primary color; do not invent a custom tab underline |

---

## Capture index

| File | What it shows |
|------|----------------|
| [`design/README.md`](design/README.md) | Provenance |
| [`design/01-alarms.png`](design/01-alarms.png) | Dark Alarms (stale details noted) |
| [`design/02-daily.png`](design/02-daily.png) | Dark Daily 2D (Earth hub stale) |
| [`design/03-sky.png`](design/03-sky.png) | Dark Sky Yearly |
| [`design/token-swatches.svg`](design/token-swatches.svg) | Exact token + widget hex |
| [`design/app-icon-512.svg`](design/app-icon-512.svg) | Launcher mark |
| [`design/splash.svg`](design/splash.svg) | Brand splash art (not an Activity) |
