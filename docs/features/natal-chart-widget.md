# Feature: natal-chart-widget

> Homescreen bitmap natal chart disk (Asc-left, whole-sign house numbers, natal + live luminaries).

## Acceptance criteria

- ✅ Active birth profile renders chart disk via `widget_astro` ImageView
- ✅ House numbers 1–12; Asc/MC when known; live Sun/Moon/Mercury
- ✅ Empty state when no profile
- ✅ Pin from Chart tab; refresh on profile save and scheduler; TIME_TICK for live overlay
- ✅ Distinct from Chart doubles text widget

## Smoke scenario

1. _Given_ saved birth profile
2. _When_ Natal chart widget added or pinned
3. _Then_ wheel shows house numbers and natal glyphs

## Container map

| Layer | Path |
|-------|------|
| Logic | `NatalWheelRenderer.renderBitmap` |
| View | `NatalChartWidgetProvider`, `NatalChartWidgetEmpty` |
| Tests | `NatalChartWidgetRenderTest` |

## Tests

- Automated: yes — non-null bitmap for fixture; empty bitmap
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
