#!/usr/bin/env bash
# Gate: child .gitattributes must carry Linguist scaffolding attribution.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
# shellcheck disable=SC1091
source scripts/lib/resolve-python.sh
"$PY" scripts/lib/check_linguist_attribution.py "$@"
