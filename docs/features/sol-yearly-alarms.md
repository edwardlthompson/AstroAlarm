# Feature: sol-yearly-alarms

Show Yearly jieqi and Sun solstice/equinox alarms on Sol, on Earth's orbit, with the next fire date and time.

## Acceptance criteria

- ✅ User-visible behavior: an enabled Xiàzhì or June Solstice alarm draws a red mark on Earth's path at the fire instant; the label is icon + `MMM d HH:mm`; matching Sun/Yearly peers collapse to one mark; planet alarms stay on their own paths
- ✅ Offline/error behavior: Solar seasonal without a place has no Sol mark; jieqi still marks Earth; empty alarms draw no marks; Show event times off hides dots and labels
- ✅ Accessibility: Sol disk content description is unchanged; labels are visible text on the map when the existing Show event times toggle is on
- ✅ i18n: date pattern matches Yearly (`MMM d HH:mm`); no new string keys

## Smoke scenario

1. _Given_ an enabled June Solstice or Xiàzhì alarm and Show event times on
2. _When_ the user opens Sol
3. _Then_ one mark sits on Earth's orbit with the next fire date/time

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../astro/alarm/AlarmWidgetScope.kt` (`onSol`, `solBodies`, `solMarks`) |
| View | `examples/android/.../ui/sol/SolAlarmOverlay.kt`, `SolRenderer.kt` |
| Tests | `AlarmWidgetScopeTest.kt`, `SolRendererTest.kt` |
| Wiring | In-app Sol and `SolWidgetProvider` already call `SolRenderer.render` |

## Tests

- Automated: yes — files above
- Coverage: yearly on Earth, sunrise excluded, peer collapse, empty marks, jieqi bitmap + label

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Critique resolutions

- Null/empty: `solMarks` / `nextInstant` return empty; Solar seasonal needs a place
- Network: N/A — on-device Kepler + NOAA
- Race: collapse uses the same fire key as one-fire peers
- Exceptions: Kepler `state` is total; overlay skips a body if radius is ~0

## Definition of Done

See `docs/FEATURE_MODULES.md` per-feature checklist.

## Notes

- Related planet is Earth for all Tropical-year Sun events (jieqi, seasonal, cardinal zodiac Beginning)
- Supersedes the alarm-calendar-peers line that kept Sol planet-only
