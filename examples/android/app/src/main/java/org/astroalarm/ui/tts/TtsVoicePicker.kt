package org.astroalarm.ui.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.goldenpath.R
import org.astroalarm.tts.*
import java.util.Locale

@Composable
fun TtsVoicePicker(
    voice: TtsVoice,
    onVoiceChange: (TtsVoice) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var ttsInstance by remember { mutableStateOf<TextToSpeech?>(null) }
    var installedLanguages by remember { mutableStateOf<List<String>>(emptyList()) }
    var voiceCandidates by remember { mutableStateOf<List<TtsVoiceCandidate>>(emptyList()) }
    val engines = remember { TtsEngines.installed(context) }
    val installedPkgs = remember(engines) { engines.map { it.packageName }.toSet() }

    DisposableEffect(voice.engine) {
        val tts = if (voice.engine.isNotBlank()) {
            TextToSpeech(context, { status ->
                if (status == TextToSpeech.SUCCESS) {
                    installedLanguages = ttsInstance?.availableLanguages.orEmpty().map { it.toLanguageTag() }
                    voiceCandidates = TtsEngines.voices(ttsInstance)
                }
            }, voice.engine)
        } else {
            TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    installedLanguages = ttsInstance?.availableLanguages.orEmpty().map { it.toLanguageTag() }
                    voiceCandidates = TtsEngines.voices(ttsInstance)
                }
            }
        }
        ttsInstance = tts
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    val installedTags = remember(installedLanguages, voiceCandidates) {
        (installedLanguages + voiceCandidates.map { it.languageTag }).filter { it.isNotBlank() }.distinct()
    }
    val tags = remember(installedTags, voiceCandidates, voice.minQuality) {
        TtsLangCatalog.filterTags(TtsLangCatalog.merge(installedTags), voiceCandidates, voice.minQuality)
    }

    val uiLocale = Locale.getDefault()
    val qualities = listOf(
        TtsVoice.QUALITY_VERY_HIGH to stringResource(R.string.tts_quality_very_high),
        TtsVoice.QUALITY_HIGH to stringResource(R.string.tts_quality_high),
        TtsVoice.QUALITY_NORMAL to stringResource(R.string.tts_quality_normal),
        TtsVoice.QUALITY_AUTO to stringResource(R.string.tts_quality_auto),
    )

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = stringResource(R.string.tts_section_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        TtsDropdown(
            label = stringResource(R.string.tts_quality),
            text = qualities.firstOrNull { it.first == voice.minQuality }?.second ?: qualities.first().second,
            options = qualities.map { it.first.toString() to it.second },
            onSelect = { raw ->
                val quality = raw.toIntOrNull() ?: TtsVoice.QUALITY_VERY_HIGH
                val keep = TtsLangCatalog.meets(voice.languageTag, voiceCandidates, quality)
                onVoiceChange(
                    voice.copy(
                        minQuality = quality,
                        voiceName = "",
                        languageTag = if (keep) voice.languageTag else "",
                    ).clamp()
                )
            }
        )

        val selectedLang = TtsLocaleMenu.languageCode(voice.languageTag)
        val langOptions = TtsLocaleMenu.languages(tags, uiLocale)
        val defaultLang = stringResource(R.string.tts_language_default)

        TtsDropdown(
            label = stringResource(R.string.tts_language_menu),
            text = if (selectedLang.isEmpty()) defaultLang else TtsLocaleMenu.displayLanguage(selectedLang, uiLocale),
            options = listOf("" to defaultLang) + langOptions.map { it to TtsLocaleMenu.displayLanguage(it, uiLocale) },
            onSelect = { code ->
                val next = if (code.isEmpty()) "" else TtsLocaleMenu.accents(tags, code, uiLocale).firstOrNull().orEmpty()
                onVoiceChange(
                    voice.copy(
                        languageTag = next,
                        voiceName = "",
                        engine = TtsLangCatalog.keepOrPrefer(voice.engine, next, installedTags, installedPkgs, voice.minQuality)
                    ).clamp()
                )
            }
        )

        val accentOptions = if (selectedLang.isNotEmpty()) TtsLocaleMenu.accents(tags, selectedLang, uiLocale) else emptyList()
        if (selectedLang.isNotEmpty() && accentOptions.isNotEmpty()) {
            TtsDropdown(
                label = stringResource(R.string.tts_accent),
                text = TtsLocaleMenu.displayAccent(voice.languageTag, uiLocale).ifBlank { voice.languageTag },
                options = accentOptions.map { it to TtsLocaleMenu.displayAccent(it, uiLocale).ifBlank { it } },
                onSelect = { tag ->
                    onVoiceChange(
                        voice.copy(
                            languageTag = tag,
                            voiceName = "",
                            engine = TtsLangCatalog.keepOrPrefer(voice.engine, tag, installedTags, installedPkgs, voice.minQuality)
                        ).clamp()
                    )
                }
            )
        }

        val namedVoices = TtsLocaleMenu.voicesFor(voiceCandidates, voice.languageTag)
            .filter { voice.minQuality <= 0 || it.quality >= voice.minQuality }
        if (namedVoices.isNotEmpty()) {
            val auto = stringResource(R.string.tts_voice_auto)
            TtsDropdown(
                label = stringResource(R.string.tts_voice_pick),
                text = namedVoices.firstOrNull { it.name == voice.voiceName }?.let { TtsLocaleMenu.shortName(it.name) } ?: auto,
                options = listOf("" to auto) + namedVoices.map { it.name to TtsLocaleMenu.shortName(it.name) },
                onSelect = { name ->
                    onVoiceChange(voice.copy(voiceName = name).clamp())
                }
            )
        }

        val systemEngine = stringResource(R.string.tts_source_system)
        val engineOptions = listOf("" to systemEngine) + engines.map { it.packageName to it.label }
        TtsDropdown(
            label = stringResource(R.string.tts_source),
            text = engineOptions.firstOrNull { it.first == voice.engine }?.second ?: systemEngine,
            options = engineOptions,
            onSelect = { pkg ->
                onVoiceChange(voice.copy(engine = pkg).clamp())
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.tts_pitch) + ": ${String.format(Locale.US, "%.1fx", voice.pitch)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                FilledTonalButton(
                    onClick = { onVoiceChange(voice.copy(pitch = voice.pitch - 0.1f).clamp()) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(stringResource(R.string.tts_pitch_minus))
                }
                FilledTonalButton(
                    onClick = { onVoiceChange(voice.copy(pitch = 1.0f).clamp()) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(stringResource(R.string.tts_pitch_reset))
                }
                FilledTonalButton(
                    onClick = { onVoiceChange(voice.copy(pitch = voice.pitch + 0.1f).clamp()) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(stringResource(R.string.tts_pitch_plus))
                }
            }
        }

        Button(
            onClick = {
                val tts = ttsInstance ?: return@Button
                tts.setPitch(voice.pitch)
                if (voice.languageTag.isNotBlank()) {
                    tts.language = Locale.forLanguageTag(voice.languageTag)
                }
                if (voice.voiceName.isNotBlank()) {
                    val targetVoice = tts.voices?.firstOrNull { it.name == voice.voiceName }
                    if (targetVoice != null) tts.voice = targetVoice
                }
                val sampleText = context.getString(R.string.tts_test_phrase)
                tts.speak(sampleText, TextToSpeech.QUEUE_FLUSH, null, "astro_tts_test")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.tts_test))
        }
    }
}
