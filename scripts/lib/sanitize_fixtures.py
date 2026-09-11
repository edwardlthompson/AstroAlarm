"""Canonical sanitizer fixtures must match stack copies and shared needles."""
from __future__ import annotations

from pathlib import Path

CANON = Path("schemas") / "golden-path" / "sanitize-fixtures.json"
COPIES = (
    Path("examples/web/src/privacy-report/sanitize-fixtures.json"),
    Path("examples/android/app/src/test/resources/sanitize-fixtures.json"),
)
STACK_TESTS = (
    Path("examples/python/tests/test_crash.py"),
    Path("examples/node/src/crash.test.ts"),
    Path("examples/rust/src/crash.rs"),
    Path("examples/go/about_test.go"),
)
NEEDLES = ("Ignore previous", "<redacted-injection>")
CI_SNIPPETS = (
    "sanitize-fixtures:",
    "check-sanitize-fixtures.sh",
    "Sanitizer fixture parity",
)


def _stack_present(root: Path, rel: Path) -> bool:
    parts = rel.parts
    if len(parts) < 2 or parts[0] != "examples":
        return False
    stack = root / parts[0] / parts[1]
    if not stack.is_dir():
        return False
    # Stub leftover dirs (e.g. pruned web with only README) are not active stacks.
    if parts[1] == "web":
        return (stack / "package.json").is_file()
    if parts[1] == "android":
        return (stack / "app").is_dir()
    if parts[1] == "python":
        return (stack / "pyproject.toml").is_file()
    if parts[1] == "node":
        return (stack / "package.json").is_file()
    if parts[1] in {"rust", "go"}:
        return (stack / "Cargo.toml").is_file() or (stack / "go.mod").is_file()
    return True


def check_repo(root: Path) -> list[str]:
    errors: list[str] = []
    canon = root / CANON
    if not canon.is_file():
        return [f"MISSING: {CANON.as_posix()}"]
    text = canon.read_text(encoding="utf-8")
    for needle in NEEDLES:
        if needle not in text:
            errors.append(f"{CANON.as_posix()} must include {needle}")
    blob = canon.read_bytes()
    for rel in COPIES:
        if not _stack_present(root, rel):
            continue
        path = root / rel
        if not path.is_file():
            errors.append(f"MISSING: {rel.as_posix()}")
        elif path.read_bytes() != blob:
            errors.append(f"{rel.as_posix()} must match {CANON.as_posix()}")
    for rel in STACK_TESTS:
        if not _stack_present(root, rel):
            continue
        path = root / rel
        if not path.is_file():
            errors.append(f"MISSING: {rel.as_posix()}")
            continue
        body = path.read_text(encoding="utf-8")
        for needle in NEEDLES:
            if needle not in body:
                errors.append(f"{rel.as_posix()} must test {needle}")
    ci = root / ".github" / "workflows" / "ci.yml"
    if ci.is_file():
        yaml = ci.read_text(encoding="utf-8")
        for snip in CI_SNIPPETS:
            if snip not in yaml:
                errors.append(f"ci.yml sanitizer job must include {snip}")
        ok = yaml.split("ci-ok:", 1)[-1]
        if "sanitize-fixtures" not in ok:
            errors.append("ci-ok must require the always-on sanitizer fixture job")
    return errors


def main() -> int:
    errors = check_repo(Path.cwd())
    if errors:
        print("Sanitizer fixture check failed:")
        for item in errors:
            print(f"  {item}")
        return 1
    print("Sanitizer fixtures match across stacks")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
