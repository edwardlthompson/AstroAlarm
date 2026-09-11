"""modules/android/MODULE.md must name F-Droid Golden Path paths."""
from __future__ import annotations

import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
MODULE = ROOT / "modules" / "android" / "MODULE.md"

REQUIRED = (
    "examples/android/metadata/",
    "Fastlane",
    "antifeatures.yml",
    "ANDROID_SIGNING.md",
    "UnifiedPush",
)


class AndroidModuleFdroidTests(unittest.TestCase):
    def test_checklist_phrases(self) -> None:
        text = MODULE.read_text(encoding="utf-8")
        for needle in REQUIRED:
            self.assertIn(needle, text, msg=f"missing {needle}")

    def test_verify_script_present(self) -> None:
        self.assertTrue((ROOT / "scripts" / "verify-fdroid-metadata.sh").is_file())


if __name__ == "__main__":
    unittest.main()
