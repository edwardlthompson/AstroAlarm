"""Release R8 + memory-budget structure tests (scripts/check-android-r8.sh)."""
from __future__ import annotations

import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
ANDROID = ROOT / "examples" / "android"
APP = ANDROID / "app"
GRADLE = APP / "build.gradle.kts"
PROGUARD = APP / "proguard-rules.pro"
MANIFEST = APP / "src" / "main" / "AndroidManifest.xml"
PROPS = ANDROID / "gradle.properties"
MEMORY = (
    APP
    / "src"
    / "main"
    / "java"
    / "dev"
    / "foss"
    / "goldenpath"
    / "memory"
    / "MemoryBudget.kt"
)
GP_APP = (
    APP
    / "src"
    / "main"
    / "java"
    / "dev"
    / "foss"
    / "goldenpath"
    / "GoldenPathApplication.kt"
)
MAIN = (
    APP
    / "src"
    / "main"
    / "java"
    / "dev"
    / "foss"
    / "goldenpath"
    / "MainActivity.kt"
)
ASTRO_APP = APP / "src" / "main" / "java" / "org" / "astroalarm" / "AstroAlarmApp.kt"


class AndroidRuntimeBudgetTests(unittest.TestCase):
    def test_release_minify_and_shrink(self) -> None:
        if not GRADLE.is_file():
            self.skipTest("android example pruned")
        text = GRADLE.read_text(encoding="utf-8")
        self.assertIn("isMinifyEnabled = true", text)
        self.assertIn("isShrinkResources = true", text)
        self.assertIn("proguard-android-optimize.txt", text)
        self.assertIn("proguard-rules.pro", text)
        self.assertNotIn("isMinifyEnabled = false", text)

    def test_proguard_rules_narrow(self) -> None:
        if not PROGUARD.is_file():
            self.skipTest("proguard-rules.pro missing")
        text = PROGUARD.read_text(encoding="utf-8")
        self.assertNotIn("-keep public class *", text)
        self.assertNotIn("-dontoptimize", text)
        self.assertNotIn("-dontobfuscate", text)

    def test_no_large_heap(self) -> None:
        if not MANIFEST.is_file():
            self.skipTest("android example pruned")
        text = MANIFEST.read_text(encoding="utf-8")
        self.assertNotIn("largeHeap", text)
        self.assertIn('android:name="org.astroalarm.AstroAlarmApp"', text)

    def test_r8_full_mode_left_on(self) -> None:
        if not PROPS.is_file():
            self.skipTest("android example pruned")
        text = PROPS.read_text(encoding="utf-8")
        self.assertNotIn("android.enableR8.fullMode=false", text)

    def test_memory_wiring(self) -> None:
        if not MEMORY.is_file():
            self.skipTest("memory package missing")
        self.assertTrue(GP_APP.is_file())
        gp = GP_APP.read_text(encoding="utf-8")
        self.assertIn("CrashCapture.install", gp)
        self.assertIn("MemoryBudget.logRecentLimiterKills", gp)
        self.assertIn("MemoryBudget.onTrimMemory", gp)
        main = MAIN.read_text(encoding="utf-8")
        self.assertNotIn("CrashCapture.install", main)
        astro = ASTRO_APP.read_text(encoding="utf-8")
        self.assertIn("@HiltAndroidApp", astro)
        self.assertIn("GoldenPathApplication", astro)
        body = MEMORY.read_text(encoding="utf-8")
        self.assertIn("isLimiterKill", body)
        self.assertIn("TRIM_MEMORY_UI_HIDDEN", body)


if __name__ == "__main__":
    unittest.main()
