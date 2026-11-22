"""Repo-root Fastlane and strip-safe signing for F-Droid."""
from __future__ import annotations

import json
import struct
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
FASTLANE = ROOT / "fastlane" / "metadata" / "android" / "en-US"
GRADLE = ROOT / "examples" / "android" / "app" / "build.gradle.kts"
VERSION = ROOT / "schemas" / "golden-path" / "app-version.json"


def _png_size(path: Path) -> tuple[int, int]:
    data = path.read_bytes()
    if len(data) < 24 or data[:8] != b"\x89PNG\r\n\x1a\n":
        raise AssertionError(f"{path} is not a PNG")
    return struct.unpack(">II", data[16:24])


class FdroidSourceReadyTests(unittest.TestCase):
    def test_root_listing_files(self) -> None:
        for name in ("title.txt", "short_description.txt", "full_description.txt"):
            path = FASTLANE / name
            self.assertTrue(path.is_file() and path.stat().st_size > 0, msg=path)
        title = (FASTLANE / "title.txt").read_text(encoding="utf-8")
        self.assertIn("AstroAlarm", title)
        short = (FASTLANE / "short_description.txt").read_text(encoding="utf-8")
        self.assertNotIn("Golden Path FOSS Android stub", short)
        appfile = (ROOT / "fastlane" / "Appfile").read_text(encoding="utf-8")
        self.assertIn('package_name("org.astroalarm")', appfile)

    def test_icon_and_screenshots(self) -> None:
        icon = FASTLANE / "images" / "icon.png"
        self.assertEqual(_png_size(icon), (512, 512))
        shots = list((FASTLANE / "images" / "phoneScreenshots").glob("*.png"))
        self.assertGreaterEqual(len(shots), 3, msg=shots)
        for shot in shots:
            width, height = _png_size(shot)
            self.assertGreater(min(width, height), 8, msg=shot)

    def test_changelog_matches_version_code(self) -> None:
        payload = json.loads(VERSION.read_text(encoding="utf-8"))
        code = payload["versionCode"]
        path = FASTLANE / "changelogs" / f"{code}.txt"
        self.assertTrue(path.is_file() and path.stat().st_size > 0, msg=path)

    def test_signing_strip_tolerant(self) -> None:
        text = GRADLE.read_text(encoding="utf-8")
        self.assertNotIn('signingConfigs.getByName("release")', text)
        self.assertIn("findByName", text)
        self.assertIn("maybeCreate", text)
        self.assertIn("readGoldenPathAppVersionCode", text)


if __name__ == "__main__":
    unittest.main()
