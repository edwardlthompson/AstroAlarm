# News Inbox

Auto-collected inbox of news, upstream updates, security fixes, and ideas relevant to this repo, appended daily by News Bot.
A Cursor planning agent should review these items and fold the useful ones into BUILD_PLAN; entries are append-only, newest at the bottom.


## 2026-11-13

**News and updates**

- Leonid meteor shower peaks Nov 17–18 with the Moon set by midnight. [source](https://www.timeanddate.com/news/astronomy/leonid-meteor-shower-2026)
- Kotlin 2.5.0-RC released (fixes a reified Enum<T> inference regression); Gradle 9.9.1 / 8.14.7 patch releases. [source](https://github.com/JetBrains/kotlin/releases/tag/v2.5.0-RC)

**Tasks for the planning agent**

- [ ] Add a meteor-shower peak alarm preset — Leonids peak Nov 17–18 with a moon-down window, a natural showcase. Label: `[AGENT]` · Priority: P3 · https://www.timeanddate.com/news/astronomy/leonid-meteor-shower-2026
- [ ] Smoke-test Kotlin 2.5.0-RC and bump Gradle wrapper — Catches compiler regressions before stable. Label: `[AGENT]` · Priority: P3 · https://github.com/JetBrains/kotlin/releases/tag/v2.5.0-RC

## 2026-11-19

**News and updates**

- Jetpack Compose November '26 release is stable: Compose BOM 2026.11.01 (Compose 1.13) with Material3 1.5.0. The BOM needs Kotlin 2.2+ and compileSdk 37.1. Material3 1.5.0 breaks Slider/RangeSlider source (stateless overloads deprecated, `onValueChange` now required, RangeSliderState activeRangeStart/End renamed to startValue/endValue); SecureTextField now defaults to system obfuscation, and clickables play platform click sounds by default. [source](https://developer.android.com/blog/posts/jetpack-compose-november-2026-release) · [Material3 notes](https://developer.android.com/jetpack/androidx/releases/compose-material3)

**Tasks for the planning agent**

- [x] Bump Compose BOM 2026.09.00 to 2026.11.01 (Compose 1.13 / Material3 1.5.0) and fix Slider/RangeSlider call sites — Material3 1.5.0 makes `onValueChange` required on Slider/RangeSlider (no Slider/RangeSlider calls found, but check other deprecations) and the BOM requires Kotlin 2.2+ and compileSdk 37.1, so a Dependabot/Argus BOM bump will fail unless AGP accepts 37.1 and the call sites are fixed first. Label: `[AGENT]` · Priority: P2 · https://developer.android.com/blog/posts/jetpack-compose-november-2026-release (synced to BUILD_PLAN)

## 2026-11-20

**News and updates**

- Gradle 9.9.2 and 8.14.8 (Nov 19) fix four High-severity build-tool bugs: SFTP repositories didn't verify host keys, transitive snapshot dependencies skipped dependency verification, TestKit used a predictable temp directory, and the daemon registry was briefly readable by other local users. 9.9.2 also fixes a 9.9.0 plugin-resolution regression with registries that answer 401. Wrappers older than 9.9.1/8.14.7 also miss the earlier Critical fix (version-catalog values run as code). [9.9.2 notes](https://docs.gradle.org/9.9.2/release-notes.html) · [8.14.8 notes](https://docs.gradle.org/8.14.8/release-notes.html)
- Android developer verification goes global from January 2027 and will cover all install sources on certified devices: it starts with Play and six other distributors, then expands country by country over several months. Developers who ship only outside Play are told to create an Android Developer Console account and register package names plus signing-key SHA-256 fingerprints (a new open-source guide covers re-signing repos such as F-Droid). Unregistered apps stay installable only through a one-time "advanced flow" on each device; hobbyist accounts now allow 50 devices. F-Droid publicly opposes the scheme. [Android blog](https://developer.android.com/blog/posts/android-developer-verification-2027) · [open-source registration guide](https://developer.android.com/developer-verification/guides/open-source-app-registration) · [F-Droid open letter](https://f-droid.org/2026/02/24/open-letter-opposing-developer-verification.html)
- Sky this week: the Nov 24 Full Moon (10:54 AST) is the year's second supermoon (perigee Nov 25, 359,347 km) and occults the Pleiades before dawn (timing depends on location); Uranus reaches opposition Nov 25, Mercury is at greatest western elongation Nov 20, and Comet 161P/Hartley-IRAS reaches perihelion Nov 27 (telescope only). [source](https://www.astronomy.com/the-sky-this-week/the-sky-this-week-from-november-20-to-27-2026/)

**Tasks for the planning agent**

- [x] Bump the Gradle wrapper 9.8.0 to 9.9.2 and commit the regenerated wrapper jar and distributionSha256Sum — the patch fixes four High build-tool vulnerabilities (SFTP host keys, snapshot dependency verification, TestKit temp dir, daemon registry). It also picks up the 9.3 Critical version-catalog code-execution fix this wrapper lacks. Run ./gradlew wrapper --gradle-version=<ver> in the wrapper (examples/android/gradle/wrapper/). Label: `[AGENT]` · Priority: P2 · https://docs.gradle.org/9.9.2/release-notes.html (synced to BUILD_PLAN)
- [ ] Decide whether to verify your identity and register this app with Google before January 2027 — if yes, create an Android Developer Console account and register the package name(s) with the SHA-256 of the GitHub Releases signing key (plus F-Droid's key if F-Droid re-signs it); if no, add an "advanced flow" install note to the README. The app is sideloaded (GitHub Releases/Obtainium/F-Droid), exactly the installs the rollout gates, and this is a personal identity/policy call. Label: `[HUMAN]` · Priority: P2 · https://developer.android.com/blog/posts/android-developer-verification-2027 (inbox only: no /build-parsed human group on this board)
- [ ] Add a "Supermoon" tag to Full Moon alarms (full moon within about 24–36 h of perigee, computed on-device from the existing suncalc moon distance) and consider an optional "Moon occults a bright star/cluster" sky occasion — a cheap, glanceable upgrade to the lunar-phase alarms with a real test date on Nov 24; skip if the planned sky-occasions work already covers it. Label: `[AGENT]` · Priority: P3 · https://www.astronomy.com/the-sky-this-week/the-sky-this-week-from-november-20-to-27-2026/

## 2026-11-26

**News and updates**

- Sky, Nov 26 – Dec 3: Venus is at its brightest of 2026 (about mag −4.9, greatest brilliancy around Nov 29 per EarthSky) low in the pre-dawn east-southeast; Uranus reached opposition Nov 25 (7:00 PM AST); the Moon groups with Jupiter, Mars and Regulus before dawn Nov 29 – Dec 1; last quarter Moon is 2026-12-01 06:09 UTC (2:09 AM AST); the crescent Moon sits above Venus and Spica on Dec 2–3. Times cross-checked with skyfield + de440s. [EarthSky planets](https://earthsky.org/astronomy-essentials/visible-planets-tonight-mars-jupiter-venus-saturn-mercury/) · [Astroblog week guide](http://astroblogger.blogspot.com/2026/11/thursday-november-26-to-thursday.html)

**Tasks for the planning agent**

- [ ] Golden-test the Dec 1 last-quarter alarm time against skyfield (06:09 UTC / 02:09 AST) — A cheap check that commons-suncalc phase times stay within a minute of a reference ephemeris across a quarter phase; if sky occasions ship, Venus greatest brilliancy and outer-planet oppositions are good seeds. Label: `[AGENT]` · Priority: P3 · https://earthsky.org/astronomy-essentials/visible-planets-tonight-mars-jupiter-venus-saturn-mercury/

## 2026-11-27

**News and updates**

- Sky events after the logged week: the waning crescent Moon passes Venus and Spica on the mornings of Dec 4–5 and sits low beside Venus, Mercury and Spica before sunrise Dec 7 ([EarthSky](https://earthsky.org/astronomy-essentials/visible-planets-tonight-mars-jupiter-venus-saturn-mercury/)). New Moon is 2026-12-09 00:51:51Z = 8:51 PM AST on Dec 8, a UTC-vs-local date flip. The Geminids are active Dec 4–20 and peak Dec 14 ~14h UT (10 AM AST; best local nights Dec 13–14 and 14–15), ZHR 150, with only a waxing crescent up ([IMO 2026 calendar](https://www.imo.net/files/meteor-shower/cal2026.pdf)). Further out, the Dec 24 01:28Z full moon (9:28 PM AST Dec 23) is a supermoon (skyfield 1.55 + de440s).

**Tasks for the planning agent**

- [ ] When the open meteor-shower alarm P3 is picked up, use the 2026 Geminids (active Dec 4–20, max Dec 14 14h UT, ZHR 150, moon-free) as the first preset, and add the Dec 8/9 new-moon date-boundary case (00:52Z = 20:52 AST) to NextFireGoldenTest — SkyOccasionCatalog has only the 2040/2492 entries, and a UTC-vs-local date flip is the classic bug in day-of alarm labels; extends the open 11-14 and 11-26 P3s rather than adding new work. Label: `[AGENT]` · Priority: P3 · https://www.imo.net/files/meteor-shower/cal2026.pdf
