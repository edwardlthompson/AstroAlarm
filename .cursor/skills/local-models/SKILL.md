---
name: local-models
description: Point Cursor Chat at localhost Ollama/LM Studio without cloud API keys.
disable-model-invocation: false
---

# Local models (no keys)

See also: `docs/LOCAL_MODELS.md`, `scripts/check_local_compute.py`, `scripts/ensure_local_model.py`

Bind 127.0.0.1 only. Do not use ngrok, CURSOR_API_KEY, or LAN bind. Cursor may require typing `ollama` in the Models GUI (not a secret; never commit).

```bash
python3 scripts/agent-run.py check-local-compute
python3 scripts/agent-run.py ensure-local-model

```

Add the `coder=` name in Cursor Models and select it for Chat/inline only. Do not set a global OpenAI base-URL override.
