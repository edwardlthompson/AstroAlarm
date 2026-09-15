# Feature: alarms-first-nav

> Three tabs: Alarms, Daily, Sky. Daily chips pick 2D or 3D. Sky chips pick Yearly, Sol, or Chart. Share stays on each square via `SkyShareButton`.

## Acceptance criteria

- ✅ User-visible: PrimaryTabRow has three labels; no 11sp six-tab row
- ✅ Offline/error: last Daily chip and last Sky chip persist in `AstroNavPreferences`
- ✅ Accessibility: tab labels use default type size
- ✅ i18n: `astro_tab_sky` in `astro_nav_strings.xml` (en/es/fr)

## Smoke scenario

1. _Given_ the user is on Sky → Sol
2. _When_ they leave the app and return to Sky
3. _Then_ Sol is still selected and still has Share

## Container map

| Layer | Path |
|-------|------|
| Logic | `AstroNavPreferences.kt` |
| View | `AstroScreen.kt`, `AstroHubPages.kt` |
| Tests | `AstroNavPreferencesTest.kt` |
| Wiring | none (same six screens) |

## Tests

- Automated: yes — chip enums persist across new preference instances
- Coverage: default Daily=2D, Sky=Yearly

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md`.
