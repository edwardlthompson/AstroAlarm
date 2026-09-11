# AstroAlarm / Golden Path — keep rules for release R8 (no broad keep-all).
# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.* <methods>;
}
-keep class **_HiltModules { *; }
-keep class **_HiltModules$* { *; }

# Application + receivers / widgets referenced from the manifest
-keep class org.astroalarm.AstroAlarmApp { *; }
-keep class org.astroalarm.astro.alarm.AstroAlarmReceiver { *; }
-keep class org.astroalarm.widget.** { *; }
-keep class dev.foss.goldenpath.MainActivity { *; }

# Crash / feedback JSON models used via reflection-light paths
-keepclassmembers class * {
    @com.squareup.moshi.* <fields>;
}

# Optional compile-only annotations referenced by commons-suncalc
-dontwarn edu.umd.cs.findbugs.annotations.Nullable
