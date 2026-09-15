# Feature: canvas-astro-tokens

> Disk paint uses `astro.night` / `astro.gold` from `design-tokens.json` `canvas`. No new hex at Kotlin call sites.

## Acceptance criteria

- ✅ User-visible: Sol sky and Yearly hub sun use canvas night/gold; 2D hub fill uses night
- ✅ Offline/error: tokens are local JSON; missing keys fail unit tests
- ✅ Accessibility: contrast unchanged vs brand ink/gold
- ✅ i18n: none

## Smoke scenario

1. _Given_ Daily Sol or Yearly disk
2. _When_ the canvas paints
3. _Then_ fill/sun colors match `canvas.night` / `canvas.gold`

## Container map

| Layer | Path |
|-------|------|
| Logic | `AstroCanvas.kt` |
| View | `SolRenderer.kt`, `SolarTermHubRenderer.kt`, `DiskCenterHub.kt` |
| Tests | `AstroCanvasTest.kt` |
| Wiring | `design-tokens/design-tokens.json` `canvas` (not `color`) |

## Tests

- Automated: yes — packed ARGB matches token hex; JSON `canvas` keys present
- Coverage: night `#1a1a2e`, gold `#C9A227`

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md`.
