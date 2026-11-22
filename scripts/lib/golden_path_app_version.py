"""Assert Golden Path demo app versions share one source of truth."""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]


def check(root: Path | None = None) -> list[str]:
    base = root or ROOT
    sot_path = base / "schemas/golden-path/app-version.json"
    errors: list[str] = []
    if not sot_path.is_file():
        return ["MISSING: schemas/golden-path/app-version.json"]
    payload = json.loads(sot_path.read_text(encoding="utf-8"))
    expected_raw = payload.get("version")
    if not isinstance(expected_raw, str) or not expected_raw.strip():
        return ["app-version.json version must be a non-empty string"]
    expected = expected_raw.strip()
    code_raw = payload.get("versionCode")
    if not isinstance(code_raw, int) or code_raw < 1:
        errors.append("app-version.json versionCode must be a positive int")

    web = base / "examples/web/package.json"
    if web.is_file():
        got = str(json.loads(web.read_text(encoding="utf-8")).get("version", "")).strip()
        if got != expected:
            errors.append(f"examples/web/package.json version {got!r} != app-version.json {expected!r}")

    pyproject = base / "examples/python/pyproject.toml"
    py_ver: str | None = None
    if pyproject.is_file():
        match = re.search(r'(?m)^version\s*=\s*"([^"]+)"', pyproject.read_text(encoding="utf-8"))
        py_ver = match.group(1) if match else None
        if py_ver != expected:
            errors.append(
                f"examples/python/pyproject.toml version {py_ver!r} != app-version.json {expected!r}"
            )

    about = base / "examples/python/src/hello/about.py"
    if about.is_file():
        text = about.read_text(encoding="utf-8")
        if "importlib.metadata" not in text and 'version("golden-path-python")' not in text:
            match = re.search(r'APP_VERSION\s*=\s*"([^"]+)"', text)
            got = match.group(1) if match else None
            if got != expected:
                errors.append(
                    f"examples/python/src/hello/about.py APP_VERSION {got!r} != "
                    f"app-version.json {expected!r}"
                )

    gradle = base / "examples/android/app/build.gradle.kts"
    if gradle.is_file():
        text = gradle.read_text(encoding="utf-8")
        if "app-version.json" not in text or "readGoldenPathAppVersion" not in text:
            errors.append(
                "examples/android/app/build.gradle.kts must read versionName via "
                "readGoldenPathAppVersion() from schemas/golden-path/app-version.json"
            )
        if "readGoldenPathAppVersionCode" not in text:
            errors.append(
                "examples/android/app/build.gradle.kts must read versionCode via "
                "readGoldenPathAppVersionCode() from schemas/golden-path/app-version.json"
            )
        if 'signingConfigs.getByName("release")' in text:
            errors.append(
                'examples/android/app/build.gradle.kts must not call '
                'signingConfigs.getByName("release") (F-Droid strips that block)'
            )
        if "findByName(" not in text or "maybeCreate(" not in text:
            errors.append(
                "examples/android/app/build.gradle.kts must use findByName/maybeCreate "
                "for the release signingConfig"
            )

    return errors


def main() -> int:
    errors = check()
    if errors:
        print("\n".join(errors))
        return 1
    payload = json.loads((ROOT / "schemas/golden-path/app-version.json").read_text(encoding="utf-8"))
    print(
        "Golden Path app version sync OK "
        f"({payload['version']} / {payload['versionCode']})"
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
