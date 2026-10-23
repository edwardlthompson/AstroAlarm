# Feature: alarm-tone-tts

Fix OP13 sunrise playing a tone when No Tone is selected, and sunset TTS failing to speak. Shared lockscreen playback path for all solar events.

## Acceptance criteria

- ✅ User-visible behavior: `toneEnabled == false` never starts a Ringtone; empty URI with tone on still uses Default Alarm Sound; TTS uses the Settings preferred engine (retry default once); when both tone and TTS are on, the ringtone pauses during each utterance; missing alarm id does not steal another alarm’s flags
- ✅ Offline/error behavior: notification channel `astroalarm_alarm_v3` is silent; legacy `astroalarm_alarm` / `astroalarm_alarm_v2` / `astroalarm_channel` deleted on ensure; TTS init failure shows a lockscreen hint
- ✅ Accessibility: lockscreen TTS-unavailable hint is visible text; tone toggle description clarifies No Tone vs Default Alarm Sound
- ✅ i18n: `astro_desc_play_tone`, `astro_tts_unavailable` (en/es/fr)

## Smoke scenario

1. _Given_ a sunrise alarm with tone off and a sunset alarm with TTS on
2. _When_ each fires on OP13 after upgrade
3. _Then_ sunrise is silent (no channel default tone) and sunset speaks with the preferred engine

## Container map

| Layer | Path |
|-------|------|
| Logic | `AlarmOutputPolicy.kt`, `AlarmNotificationChannel.kt`, `AlarmTtsSession.kt` |
| View | `AstroAlarmActivity.kt`, `AstroAlarmLockscreenView.kt`, `AstroDialogSections.kt` |
| Tests | `AlarmNotificationChannelTest`, `AlarmOutputPolicyTest`, `AlarmTtsSessionTest` |
| Wiring | existing receiver / scheduler |
## Tests

- Automated: yes — channel v2 null sound + legacy ids deleted; missing id muted; preferred engine package passed; utterance pause callbacks

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Android does not update sound on an existing notification channel; bumping the id is required for in-place OP13 upgrades.
- Sunrise and sunset share one playback path; no solar-event audio fork.
