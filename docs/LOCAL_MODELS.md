# Local models (no cloud keys)

Use a local OpenAI-compatible server so Cursor Chat can stay on this machine. The template never stores keys and never writes the dummy GUI string into git.

## What this is (and is not)

- **Chat / inline edit** can talk to `http://127.0.0.1:11434/v1` (Ollama) or `http://127.0.0.1:1234/v1` (LM Studio).
- **Tab completion and Agent/tool quality** may still use Cursor's own models. A local 7B model is not a drop-in for `/ship`.
- Do **not** set a global OpenAI base-URL override (it hijacks cloud Agent/Chat). Add the local model in the Models picker and select it when you want local inference.
- Do **not** set `CURSOR_API_KEY`, create OpenAI dashboard keys, or use tunnels.

## Workflow (This Computer)

1. Probe: `python3 scripts/agent-run.py check-local-compute` (or `just local-compute`).
2. If `ollama=up` but `coder=missing`, pull the default: `python3 scripts/agent-run.py ensure-local-model` (or `just ensure-local-model`). Default: `qwen2.5-coder:7b` (8G stay at 7B; 16G+ can try larger).
3. `/coach` and session-start print `ollama=up/<coder>` when a coder is pulled.
4. `/best-of-n` and `/gates` start with the same probe. Prefer a listed **coder** in the Cursor picker. One Ollama server is enough -- do not start a second per worktree.
5. `/ship` never requires Ollama.

Keep the server on loopback only: `ollama serve` on **127.0.0.1:11434**.

## Cursor Settings (GUI)

1. Cursor Settings -> Models.
2. **Add model** with the exact name from `ollama list` or the `models=` line (example: `qwen2.5-coder:7b`).
3. The form may refuse an empty key. **type this in the GUI** (not a secret; never commit; never put in `.env`):

```
ollama

```

4. Select that local model for Chat / inline only. Leave Cursor Agent models checked for `/build` and `/ship`.

If Cursor reports a CORS error: keep Ollama on localhost; update Cursor; **do not** bind the LAN or open a tunnel.

## LM Studio (optional)

Same picker steps with base URL `http://127.0.0.1:1234/v1` and the same GUI dummy string.

## Probe

```bash
python3 scripts/agent-run.py check-local-compute
python3 scripts/agent-run.py ensure-local-model

```

`ollama=up` means the loopback API responded. `coder=` is the recommended pulled coder.

## Never

- GitHub Actions secrets for this path
- Copying the GUI dummy string into `.env`, `.env.example`, or `.cursor/mcp.json`
- Starting a second Ollama per `/best-of-n` worker
- Overriding the global OpenAI base URL so Agent traffic hits localhost
