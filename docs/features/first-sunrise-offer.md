# Feature: first-sunrise-offer

> After the first city is set, offer one tap to enable a sunrise alarm with the next fire time.

## Acceptance criteria

- ✅ User-visible: first city (search or GPS) can show “Wake at sunrise tomorrow (H:mm)?”; Add saves an enabled Sunrise alarm and reschedules
- ✅ Offline/error: no offer when place is invalid, polar night has no next sunrise, user already has a sunrise alarm, or they chose Not now
- ✅ Accessibility: dialog title includes the time; Add and Not now are text buttons
- ✅ i18n: `sunrise_offer_strings.xml` en/es/fr

## Smoke scenario

1. _Given_ no place, no sunrise alarm, offer not dismissed
2. _When_ the user picks a city
3. _Then_ returning to Alarms shows the offer; Add creates one Sunrise row

## Container map

| Layer | Path |
|-------|------|
| Logic | `astro/alarm/SunriseOffer.kt` |
| View | `SunriseOfferDialog.kt`, `AstroScreen.kt`, `SettingsScreen.kt` |
| Tests | `SunriseOfferTest.kt` |
| Wiring | none |

## Tests

- Automated: yes — shouldShow false when dismissed / existing sunrise / null place; next fire uses AstroNextFire
- Coverage: New York sample place has a next sunrise

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

See `docs/FEATURE_MODULES.md`.
