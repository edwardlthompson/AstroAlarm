"""Android signing runbook + Gradle/gitignore structure tests."""
from __future__ import annotations

import sys
import unittest
from pathlib import Path

LIB = Path(__file__).resolve().parent.parent / "scripts" / "lib"
if str(LIB) not in sys.path:
    sys.path.insert(0, str(LIB))

from android_signing_runbook import check  # noqa: E402

ROOT = Path(__file__).resolve().parent.parent


class AndroidSigningRunbookTests(unittest.TestCase):
    def test_repo(self) -> None:
        self.assertEqual(check(ROOT), [])

    def test_feature_gate_hook(self) -> None:
        text = (ROOT / "scripts" / "feature-gate.sh").read_text(encoding="utf-8")
        self.assertIn("check-android-signing-runbook.sh", text)

    def test_android_fallback_documented(self) -> None:
        runbook = (ROOT / "docs" / "ANDROID_SIGNING.md").read_text(encoding="utf-8")
        self.assertIn("ANDROID_KEYSTORE_FILE", runbook)
        gradle = (ROOT / "examples" / "android" / "app" / "build.gradle.kts").read_text(
            encoding="utf-8"
        )
        self.assertIn("ANDROID_KEYSTORE_FILE", gradle)


if __name__ == "__main__":
    unittest.main()
