#!/usr/bin/env bash
# Fail when sanitizer fixtures drift across Golden Path stacks.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
# shellcheck source=lib/resolve-python.sh
. "$(cd "$(dirname "$0")" && pwd)/lib/resolve-python.sh"

if [ ! -f "$ROOT/examples/web/package.json" ] && [ ! -d "$ROOT/examples/android" ]; then
  echo "SKIP: sanitizer stack examples pruned"
  exit 0
fi
exec "$PY" "$ROOT/scripts/lib/sanitize_fixtures.py"
