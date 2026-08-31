#!/usr/bin/env python3
"""Pull the default local coder when Ollama is up but has no coder model."""
from __future__ import annotations

import shutil
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
LIB = ROOT / "scripts" / "lib"
if str(LIB) not in sys.path:
    sys.path.insert(0, str(LIB))

from ollama_local import (  # noqa: E402
    DEFAULT_CODER,
    coder_models,
    fetch_tags,
    recommended_coder,
)


def _pull(name: str) -> int:
    binary = shutil.which("ollama")
    if not binary:
        print("FAIL: ollama binary not on PATH", file=sys.stderr)
        return 1
    print(f"pulling {name}")
    proc = subprocess.run([binary, "pull", name], cwd=ROOT)
    return proc.returncode


def main() -> int:
    names = fetch_tags()
    if names is None:
        print("FAIL: Ollama not answering on 127.0.0.1:11434", file=sys.stderr)
        print("See docs/LOCAL_MODELS.md", file=sys.stderr)
        return 1
    if coder_models(names):
        pick = recommended_coder(names)
        print(f"ollama=up coder={pick} models={','.join(names)}")
        return 0
    target = DEFAULT_CODER
    print(f"ollama=up models={','.join(names) if names else 'none'}; need {target}")
    code = _pull(target)
    if code != 0:
        return code
    after = fetch_tags() or []
    if target not in after and not coder_models(after):
        print(f"FAIL: pull finished but {target} is still missing", file=sys.stderr)
        return 1
    print(f"ollama=up coder={recommended_coder(after)} models={','.join(after)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
