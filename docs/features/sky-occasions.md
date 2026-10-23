# Feature: sky-occasions

Catalog one-shot alarms for named far-future sky events (2040 naked-eye grouping; 2492 all-planet cluster).

## Acceptance criteria

- ✅ User-visible behavior: create-alarm has a Sky occasions chip with two catalog rows; fires once then disables
- ✅ Offline/error behavior: 2040 uses sunset at `AstroPlace` or 18:00 local; 2492 uses 12:00 local; unknown `catalogId` skipped in JSON
- ✅ Accessibility: existing alarm row TalkBack; occasion labels are plain text
- ✅ i18n: `astro_tab_occasion`, catalog titles (en/es/fr)

## Smoke scenario

1. _Given_ a city is set
2. _When_ the user adds the 2040-09-08 occasion
3. _Then_ next fire is sunset on that date in the place zone

## Container map

| Layer | Path |
|-------|------|
| Logic | `SkyOccasionCatalog.kt`, `AstroAlarm.kt`, `AstroNextFire.kt`, `AstroAlarmTargetJson.kt` |
| View | `AstroCelestialPickers.kt`, `AstroEditTargetSection.kt`, `AlarmTargetCopy.kt` |
| Tests | `SkyOccasionCatalogTest`, JSON round-trip |
| Wiring | existing alarm store / scheduler |
## Tests

- Automated: yes — catalog Instant rules; JSON round-trip; unknown id skipped

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Not an `AllPlanetsAlign` stretch — catalog dates only; Kepler not used for fire times.
