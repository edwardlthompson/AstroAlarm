#!/usr/bin/env bash
# Fail when sanitizer fixtures drift across Golden Path stacks.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

if [ ! -f "$ROOT/examples/web/package.json" ]; then
  echo "SKIP: examples/web pruned"
  exit 0
fi
cd "$ROOT"
# shellcheck source=lib/resolve-python.sh
. "$(cd "$(dirname "$0")" && pwd)/lib/resolve-python.sh"
exec "$PY" "$ROOT/scripts/lib/sanitize_fixtures.py"
