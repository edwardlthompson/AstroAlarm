"""Crash inbox stub stays disabled until DPIA."""
from __future__ import annotations

import json
import sys
import tempfile
import unittest
from pathlib import Path

LIB = Path(__file__).resolve().parent.parent / "scripts" / "lib"
if str(LIB) not in sys.path:
    sys.path.insert(0, str(LIB))

from crash_inbox import check_repo, check_stub  # noqa: E402

ROOT = Path(__file__).resolve().parent.parent


class CrashInboxTests(unittest.TestCase):
    def test_repo_stub_off(self) -> None:
        self.assertEqual(check_repo(ROOT), [])

    def test_rejects_enabled_or_dsn(self) -> None:
        errs = check_stub({"enabled": True, "provider": "none", "dsn": "", "endpoint": ""}, "x")
        self.assertTrue(any("enabled" in e for e in errs))
        errs = check_stub(
            {"enabled": False, "provider": "glitchtip", "dsn": "https://x", "endpoint": ""},
            "x",
        )
        self.assertTrue(any("DSN" in e or "dsn" in e.lower() for e in errs))

    def test_validate_bootstrap_wires_check(self) -> None:
        text = (ROOT / "scripts" / "validate-bootstrap.sh").read_text(encoding="utf-8")
        self.assertIn("check-crash-inbox.sh", text)
        example = json.loads(
            (ROOT / "schemas" / "golden-path" / "crash-inbox.example.json").read_text(
                encoding="utf-8"
            )
        )
        self.assertFalse(example.get("enabled"))


if __name__ == "__main__":
    unittest.main()
