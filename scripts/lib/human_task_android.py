"""ADB / Android HUMAN automation handlers."""
from __future__ import annotations

import os
import shutil
import subprocess
from pathlib import Path

from human_task_core import AttemptResult, run_cmd


def _adb_bin() -> str:
    env = os.environ.get("ADB", "")
    if env and Path(env).is_file():
        return env
    found = shutil.which("adb")
    if found:
        return found
    win = os.environ.get("LOCALAPPDATA", "")
    candidate = Path(win) / "Android/Sdk/platform-tools/adb.exe"
    if candidate.is_file():
        return str(candidate)
    return env or "adb"


def first_serial() -> str | None:
    adb = _adb_bin()
    try:
        out = subprocess.run([adb, "devices"], capture_output=True, text=True, check=False)
    except FileNotFoundError:
        return None
    if out.returncode != 0:
        return None
    for line in out.stdout.splitlines()[1:]:
        parts = line.split()
        if len(parts) >= 2 and parts[1] == "device":
            return parts[0]
    return None


def adb_authorized(root: Path) -> bool:
    return first_serial() is not None


def _gradle_cmd(root: Path, *args: str) -> tuple[int, str] | None:
    cwd = root / "examples/android"
    bat = cwd / "gradlew.bat"
    unix = cwd / "gradlew"
    if os.name == "nt" and bat.is_file():
        return run_cmd(root, [str(bat), *args], cwd=cwd)
    if unix.is_file():
        return run_cmd(root, ["bash", str(unix), *args], cwd=cwd)
    return None


def automate_adb_instrumented(root: Path, _cfg: dict) -> AttemptResult:
    if adb_authorized(root):
        verify = root / "scripts/verify-android-insets.sh"
        if verify.is_file():
            code, tail = run_cmd(root, ["bash", str(verify)])
            if code == 0:
                return AttemptResult(0, "verify-android-insets", "ADB instrumented tests passed", False)
            return AttemptResult(1, "verify-android-insets", tail or f"exit {code}", True)
        ran = _gradle_cmd(root, "connectedDebugAndroidTest")
        if ran:
            code, tail = ran
            if code == 0:
                return AttemptResult(0, "connectedDebugAndroidTest", "connectedDebugAndroidTest passed", False)
            return AttemptResult(1, "connectedDebugAndroidTest", tail or f"exit {code}", True)
    _gradle_cmd(root, "test")
    return AttemptResult(
        1,
        "adb-unavailable",
        "no_authorized_device; unit tests run if Android tree present",
        True,
    )


def automate_fdroid_dry_run(root: Path, _cfg: dict) -> AttemptResult:
    script = root / "scripts/fdroid-device-dry-run.sh"
    if not script.is_file():
        return AttemptResult(1, "fdroid-dry-run", "fdroid-device-dry-run.sh missing", True)
    if not adb_authorized(root):
        return AttemptResult(1, "fdroid-dry-run", "no_authorized_device", True)
    code, tail = run_cmd(root, ["bash", str(script)])
    if code == 0:
        return AttemptResult(0, "fdroid-dry-run", "F-Droid device dry-run passed", False)
    return AttemptResult(1, "fdroid-dry-run", tail or f"exit {code}", True)


def automate_android_sdk_smoke(root: Path, _cfg: dict) -> AttemptResult:
    ran = _gradle_cmd(root, "testDebugUnitTest")
    if ran and ran[0] != 0:
        return AttemptResult(1, "gradle-test", ran[1] or f"exit {ran[0]}", True)
    serial = first_serial()
    if serial:
        code, _ = run_cmd(root, [_adb_bin(), "-s", serial, "shell", "getprop", "ro.build.version.sdk"])
        if code == 0:
            return AttemptResult(0, "adb-getprop", "Gradle tests + adb getprop smoke", False)
    if (root / "examples/android").is_dir():
        return AttemptResult(1, "adb-unavailable", "no_authorized_device after unit tests", True)
    return AttemptResult(1, "android-sdk", "No Android example tree", True)
