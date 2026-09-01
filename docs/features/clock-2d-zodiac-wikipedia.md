# Feature: clock-2d-zodiac-wikipedia

Tap a 2D in-app zodiac bubble to open that sign's English Wikipedia page.

## Acceptance criteria

- ✅ User-visible behavior: with the zodiac ring on, tapping a 2D clock sign opens `https://en.wikipedia.org/wiki/{Sign}_(astrology)` in the browser; yellow rim ticks mark each sign cusp (beginning/end) and hide with the same toggle
- ✅ Offline/error behavior: missing browser is ignored (`runCatching`); taps miss when zodiac is hidden
- ✅ Accessibility: existing 2D clock content description unchanged; switch still named
- ✅ i18n: N/A — Wikipedia English astrology URLs

## Smoke scenario

1. _Given_ the 2D clock tab with the zodiac ring shown
2. _When_ the user taps Virgo
3. _Then_ the device opens the Virgo (astrology) Wikipedia page

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../widget/ZodiacRingLayout.kt` |
| View | `AstroClockScreen.kt` |
| Tests | `ZodiacRingLayoutTest.kt` |
| Wiring | `pointerInput` on the 2D disk `Image` |

## Tests

- Automated: yes — `ZodiacRingLayoutTest.kt`

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
