#!/usr/bin/env bash
# Compare on-device ephemeris to the published NOAA almanac table (30 dates).
# Writes docs/ACCURACY.md via EphemerisAccuracyTest.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT/examples/android"
if [[ -z "${JAVA_HOME:-}" ]]; then
  for candidate in \
    "/mnt/c/Program Files/Android/Android Studio/jbr" \
    "/c/Program Files/Android/Android Studio/jbr" \
    "C:/Program Files/Android/Android Studio/jbr"
  do
    if [[ -d "$candidate" ]]; then
      export JAVA_HOME="$candidate"
      break
    fi
  done
fi
if [[ -f ./gradlew.bat ]] && command -v cmd.exe >/dev/null 2>&1; then
  cmd.exe /c "gradlew.bat :app:testDebugUnitTest --tests org.astroalarm.astro.EphemerisAccuracyTest"
elif [[ -f ./gradlew ]]; then
  ./gradlew :app:testDebugUnitTest --tests org.astroalarm.astro.EphemerisAccuracyTest
elif [[ -f ./gradlew.bat ]]; then
  ./gradlew.bat :app:testDebugUnitTest --tests org.astroalarm.astro.EphemerisAccuracyTest
else
  echo "FAIL: gradlew missing under examples/android"
  exit 1
fi
if [[ ! -f "$ROOT/docs/ACCURACY.md" ]]; then
  echo "FAIL: docs/ACCURACY.md was not written"
  exit 1
fi
echo "OK: docs/ACCURACY.md updated"
tail -n 5 "$ROOT/docs/ACCURACY.md"
