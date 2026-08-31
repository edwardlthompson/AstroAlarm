#!/usr/bin/env bash
# Pull a RAM-fit local coder when Ollama is up. Never starts a second server.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
# shellcheck source=lib/resolve-python.sh
. "$ROOT/scripts/lib/resolve-python.sh"
exec "$PY" "$ROOT/scripts/ensure_local_model.py" "$@"
