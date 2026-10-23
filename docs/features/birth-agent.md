# Feature: birth-agent

Copy agent instructions + paste/save interpretation + optional local HTTP LLM on the published Chart tab (`astro.birth` / `ui.birth`). Uses the existing birth chart calc stack — not a second natal calculator.

## Acceptance criteria

- ✅ User-visible behavior: Chart details offers Copy agent instructions (personality prompt + whole-sign placement table), paste/save/clear interpretation on the active `BirthProfile`, and opt-in Ask local model
- ✅ Offline/error behavior: Copy disabled without place + exact time + ascendant; local LLM 10s timeout with typed error; no cloud vendor / API key
- ✅ Accessibility: Copy has TalkBack content description; toasts for copy/LLM outcomes
- ✅ i18n: `natal_copy_agent*`, `natal_local_llm*`, paste/save/clear/privacy keys in `birth_strings.xml` (en/es/fr)

## Smoke scenario

1. _Given_ Chart tab with a saved birth profile (date, time, place)
2. _When_ user opens details, taps Copy agent instructions, pastes a reply, saves
3. _Then_ clipboard holds the prompt and `savedInterpretation` persists on the profile

## Container map

| Layer | Path |
|-------|------|
| Logic | `astro/birth/NatalAgentPrompt.kt`, `LocalLlmClient.kt`, `LocalLlmPrefs.kt` |
| View | `ui/birth/BirthAgentSection.kt` (wired from `BirthChartDetailsDialog`) |
| Tests | `NatalAgentPromptTest`, `LocalLlmClientTest` |
| Wiring | ≤10 lines in `BirthChartDetailsDialog` |

## Tests

- Automated: yes — prompt determinism + personality instruction; incomplete no-copy; mocked LLM timeout/OK
- Coverage: `BirthProfile` + `NatalChart` + `WholeSignHouses` (not park `NatalChart` / snippets)

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Interpretation text and LLM prefs stay on-device (`docs/PRIVACY.md`); default LLM URL `http://127.0.0.1:11434`, off by default
- Do not vendor an LLM in the APK; do not reintroduce park `NatalChartCalc` / `NatalSignSnippets`
