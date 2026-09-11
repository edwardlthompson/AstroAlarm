#!/usr/bin/env bash
# README must link the GitHub Pages demo URL from branding/product.json.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

if [ ! -f "$ROOT/examples/web/package.json" ]; then
  echo "SKIP: android-only child pages demo optional"
  exit 0
fi
cd "$ROOT"
# shellcheck source=lib/resolve-python.sh
. "$(cd "$(dirname "$0")" && pwd)/lib/resolve-python.sh"
exec "$PY" "$ROOT/scripts/lib/pages_demo_health.py"
