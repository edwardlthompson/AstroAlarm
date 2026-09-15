# Brand voice

AstroAlarm is an alarm clock that knows the sky. Write for a 6am user who wants Stop, Snooze, and sunrise — not a planetarium brochure.

## Tone

- **Calm and specific** — clock, sunrise, lockscreen, widgets; one job until they ask for more
- **FOSS-first** — on-device ephemeris, no Play Services, GitHub Releases / F-Droid
- **Confident, not hype** — no “revolutionary” or “ultimate”
- **Midnight-gold** — match `product.json` name and tagline; never Golden Path stub copy

## Pitch rules

1. Lead with the wake-up outcome, then the observatory.
2. Elevator pitch is the `product.json` tagline (one sentence).
3. Store bullets are user benefits from `product.json` `features`, not Gradle internals.
4. README hero and Fastlane text must still read if images fail.

## Do / don’t

| Do | Don’t |
|----|-------|
| Use AstroAlarm, the tagline, and MIT / no-trackers | Invent a second product name or keep Golden Path Android |
| Name sunrise, snooze, math unlock, widgets | Promise Play Store SDKs or cloud ephemeris |
| Drive Fastlane/F-Droid copy from `product.json` | Leave template stub descriptions |
