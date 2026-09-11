"""UnifiedPush sample stays FOSS and wires manifest queries/receiver."""
from __future__ import annotations

import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
MANIFEST = ROOT / "examples" / "android" / "app" / "src" / "main" / "AndroidManifest.xml"
PUSH = (
    ROOT
    / "examples"
    / "android"
    / "app"
    / "src"
    / "main"
    / "java"
    / "dev"
    / "foss"
    / "goldenpath"
    / "push"
    / "UnifiedPushConfig.kt"
)


class UnifiedPushSampleTests(unittest.TestCase):
    def test_config_constants(self) -> None:
        text = PUSH.read_text(encoding="utf-8")
        self.assertIn("org.unifiedpush.android.distributor.REGISTER", text)
        self.assertIn("org.unifiedpush.android.connector.MESSAGE", text)
        self.assertIn("usesProprietaryPush", text)

    def test_manifest_queries_and_receiver(self) -> None:
        text = MANIFEST.read_text(encoding="utf-8")
        self.assertIn("org.unifiedpush.android.distributor.REGISTER", text)
        self.assertIn("UnifiedPushMessageReceiver", text)
        self.assertIn("org.unifiedpush.android.connector.MESSAGE", text)
        self.assertNotIn("firebase", text.lower())
        self.assertNotIn("com.google.firebase", text)


if __name__ == "__main__":
    unittest.main()
