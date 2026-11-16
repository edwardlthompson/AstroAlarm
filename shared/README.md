# Shared KMP modules (Phase 4)

`:astro-domain`, `:astro-ephemeris`, and `:astro-ui` sit beside `examples/android`. The Android APK is still the production binary (`applicationId org.astroalarm`).

| Module | What lives here | Source set |
|--------|-----------------|------------|
| `astro-domain` | Models, JSON codecs, catalogs, fire identity, math generator | `jvmMain` (`java.time` / `org.json`) |
| `astro-ephemeris` | NOAA / Kepler / suncalc façade, `AstroNextFire` | `jvmMain` (`commons-suncalc` JVM) |
| `astro-ui` | Empty CMP shell until M3 | `commonMain` |

iOS targets are declared so the skeleton compiles on macOS. Linux CI sets `kotlin.native.ignoreDisabledTargets=true`. Native Kotlin cannot link `commons-suncalc`; iOS `actual` ephemeris is M6.

`kmp-android-variant.gradle.kts` exposes the JVM jar as `androidApiElements` / `androidRuntimeElements` **and** `extendsFrom` the JVM consumable configs so `api()` libraries (suncalc) are packaged. The app also declares suncalc directly until `androidTarget` exists.
