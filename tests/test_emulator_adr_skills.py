"""Emulator and ADR companion skills stay wired."""
from __future__ import annotations

import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent


class EmulatorAdrSkillsTests(unittest.TestCase):
    def test_skill_files_and_see_also(self) -> None:
        emu = (ROOT / ".cursor" / "skills" / "emulator" / "SKILL.md").read_text(
            encoding="utf-8"
        )
        adr = (ROOT / ".cursor" / "skills" / "adr" / "SKILL.md").read_text(encoding="utf-8")
        self.assertIn("See also:", emu)
        self.assertIn("emulator.md", emu)
        self.assertIn("See also:", adr)
        self.assertIn("adr.md", adr)

    def test_commands_point_at_skills(self) -> None:
        emu_cmd = (ROOT / ".cursor" / "commands" / "emulator.md").read_text(encoding="utf-8")
        adr_cmd = (ROOT / ".cursor" / "commands" / "adr.md").read_text(encoding="utf-8")
        self.assertIn("skills/emulator", emu_cmd)
        self.assertIn("skills/adr", adr_cmd)

    def test_integrations_require_skills(self) -> None:
        text = (ROOT / "scripts" / "lib" / "check_cursor_integrations.py").read_text(
            encoding="utf-8"
        )
        self.assertIn('"emulator"', text)
        self.assertIn('"adr"', text)


if __name__ == "__main__":
    unittest.main()
