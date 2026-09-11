"""Resume handoff digest + command wiring."""
from __future__ import annotations

import sys
import unittest
from pathlib import Path
from unittest import mock

LIB = Path(__file__).resolve().parent.parent / "scripts" / "lib"
if str(LIB) not in sys.path:
    sys.path.insert(0, str(LIB))

from resume_digest import cursor_prs, format_digest  # noqa: E402

ROOT = Path(__file__).resolve().parent.parent


class ResumeHandoffTests(unittest.TestCase):
    def test_cursor_pr_filter(self) -> None:
        prs = [
            {"headRefName": "cursor/foo", "number": 1, "title": "a", "url": "u"},
            {"headRefName": "dependabot/npm", "number": 2, "title": "b", "url": "u"},
        ]
        out = cursor_prs(prs)
        self.assertEqual(len(out), 1)
        self.assertEqual(out[0]["number"], 1)

    def test_format_digest_contents(self) -> None:
        text = format_digest(
            ROOT,
            sync_inner="_None_",
            synced_prs=[],
            cloud_prs=[{"number": 9, "title": "cloud", "url": "https://x", "headRefName": "cursor/x"}],
            gh_error=None,
            fetch_note="ok",
            branch_notes=[],
            ci_line="CI: green",
        )
        self.assertIn("Resume handoff", text)
        self.assertIn("Next BUILD_PLAN row:", text)
        self.assertIn("cursor/x", text)
        self.assertIn("Do not rely on /compact", text)

    def test_command_and_session_nudge(self) -> None:
        cmd = (ROOT / ".cursor" / "commands" / "resume.md").read_text(encoding="utf-8")
        self.assertIn("resume-handoff", cmd)
        hook = (ROOT / ".cursor" / "hooks" / "session_start_context.py").read_text(
            encoding="utf-8"
        )
        self.assertIn("/resume", hook)


if __name__ == "__main__":
    unittest.main()
