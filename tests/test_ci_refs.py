"""CI ref sanitizer (OSPS-BR) unit tests."""
from __future__ import annotations

import sys
import unittest
from pathlib import Path

LIB = Path(__file__).resolve().parent.parent / "scripts" / "lib"
if str(LIB) not in sys.path:
    sys.path.insert(0, str(LIB))

from ci_refs import check_env, is_safe_ref, is_safe_tag  # noqa: E402

ROOT = Path(__file__).resolve().parent.parent


class CiRefsTests(unittest.TestCase):
    def test_rejects_unsafe_tag(self) -> None:
        self.assertFalse(is_safe_tag("v1;echo"))
        self.assertFalse(is_safe_ref("main;rm"))
        self.assertTrue(is_safe_tag("v1.2.3"))
        self.assertTrue(is_safe_ref("chore/template-catchup-v1.4.0"))

    def test_check_env(self) -> None:
        self.assertEqual(check_env({"GITHUB_REF_NAME": "main"}), [])
        errs = check_env({"INPUT_TAG": "v1;echo"})
        self.assertTrue(errs)

    def test_wiring(self) -> None:
        vb = (ROOT / "scripts" / "validate-bootstrap.sh").read_text(encoding="utf-8")
        self.assertIn("check-ci-refs.sh", vb)
        self.assertTrue((ROOT / ".bestpractices.json").is_file())
        readme = (ROOT / "README.md").read_text(encoding="utf-8")
        self.assertIn("bestpractices.dev/projects/14564", readme)


if __name__ == "__main__":
    unittest.main()
