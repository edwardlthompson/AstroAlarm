"""Parse Ollama /api/tags and pick a RAM-fit coder model. Never raises to callers."""
from __future__ import annotations

import json
import urllib.error
import urllib.request

from local_resources import OLLAMA_URL

DEFAULT_CODER = "qwen2.5-coder:7b"
CODER_TOKENS = ("coder", "codellama", "codestral")


def parse_tags(payload: object) -> list[str]:
    if not isinstance(payload, dict):
        return []
    models = payload.get("models")
    if not isinstance(models, list):
        return []
    names: list[str] = []
    for item in models:
        if not isinstance(item, dict):
            continue
        name = item.get("name")
        if isinstance(name, str) and name.strip():
            names.append(name.strip())
    return names


def fetch_tags(timeout: float = 1.0) -> list[str] | None:
    try:
        opener = urllib.request.build_opener(urllib.request.ProxyHandler({}))
        with opener.open(OLLAMA_URL, timeout=timeout) as resp:
            raw = resp.read()
        return parse_tags(json.loads(raw.decode("utf-8")))
    except (
        urllib.error.URLError,
        TimeoutError,
        OSError,
        ValueError,
        json.JSONDecodeError,
        UnicodeDecodeError,
    ):
        return None


def is_coder(name: str) -> bool:
    lower = name.lower()
    return any(token in lower for token in CODER_TOKENS)


def coder_models(names: list[str]) -> list[str]:
    return [name for name in names if is_coder(name)]


def recommended_coder(names: list[str]) -> str:
    if DEFAULT_CODER in names:
        return DEFAULT_CODER
    found = coder_models(names)
    return found[0] if found else DEFAULT_CODER
