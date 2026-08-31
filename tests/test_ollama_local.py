"""Ollama tag parse, coder pick, and leftover close rules."""
from __future__ import annotations

import sys
import unittest
from unittest.mock import patch

from pathlib import Path

LIB = Path(__file__).resolve().parent.parent / "scripts" / "lib"
if str(LIB) not in sys.path:
    sys.path.insert(0, str(LIB))

from human_task_leftovers import automate_ollama  # noqa: E402
from ollama_local import (  # noqa: E402
    DEFAULT_CODER,
    coder_models,
    parse_tags,
    recommended_coder,
)


class ParseTagsTests(unittest.TestCase):
    def test_names_and_empty(self) -> None:
        self.assertEqual(parse_tags({"models": [{"name": "qwen2.5-coder:7b"}]}), ["qwen2.5-coder:7b"])
        self.assertEqual(parse_tags({"models": []}), [])
        self.assertEqual(parse_tags(None), [])
        self.assertEqual(parse_tags({"models": [{"name": ""}]}), [])

    def test_recommended_prefers_default(self) -> None:
        names = ["deepseek-r1:7b", DEFAULT_CODER]
        self.assertEqual(coder_models(names), [DEFAULT_CODER])
        self.assertEqual(recommended_coder(names), DEFAULT_CODER)
        self.assertEqual(recommended_coder(["deepseek-r1:7b"]), DEFAULT_CODER)


class AutomateOllamaTests(unittest.TestCase):
    def test_up_with_model_closes(self) -> None:
        with patch("human_task_leftovers.fetch_tags", return_value=["deepseek-r1:7b"]):
            result = automate_ollama(Path("."), {})
        self.assertEqual(result.exit_code, 0)
        self.assertFalse(result.backlog)

    def test_down_backlogs(self) -> None:
        with patch("human_task_leftovers.fetch_tags", return_value=None):
            result = automate_ollama(Path("."), {})
        self.assertEqual(result.exit_code, 1)
        self.assertTrue(result.backlog)

    def test_empty_tags_backlogs(self) -> None:
        with patch("human_task_leftovers.fetch_tags", return_value=[]):
            result = automate_ollama(Path("."), {})
        self.assertEqual(result.exit_code, 1)
        self.assertTrue(result.backlog)


if __name__ == "__main__":
    unittest.main()
