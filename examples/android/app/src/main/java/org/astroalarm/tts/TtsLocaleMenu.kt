package org.astroalarm.tts

import java.util.Locale

object TtsLocaleMenu {
    fun languageCode(tag: String): String {
        val raw = tag.trim()
        if (raw.isEmpty()) return ""
        val fromLocale = Locale.forLanguageTag(raw).language
        return fromLocale.ifBlank { raw.substringBefore('-') }.lowercase(Locale.ROOT)
    }

    fun languages(tags: List<String>, ui: Locale = Locale.US): List<String> =
        tags.map { languageCode(it) }.filter { it.isNotEmpty() }.distinct()
            .sortedBy { displayLanguage(it, ui).lowercase(ui) }

    fun accents(tags: List<String>, language: String, ui: Locale = Locale.US): List<String> {
        val want = language.lowercase(Locale.ROOT)
        if (want.isEmpty()) return emptyList()
        return tags.filter { languageCode(it) == want }.distinct()
            .sortedBy { displayAccent(it, ui).lowercase(ui) }
    }

    fun voicesFor(voices: List<TtsVoiceCandidate>, tag: String): List<TtsVoiceCandidate> {
        val want = tag.trim()
        if (want.isEmpty()) return emptyList()
        val exact = voices.filter { it.languageTag.equals(want, ignoreCase = true) }
        val pool = exact.ifEmpty {
            voices.filter { languageCode(it.languageTag) == languageCode(want) }
        }
        return pool.sortedWith(
            compareBy<TtsVoiceCandidate> { it.networkRequired }
                .thenByDescending { it.quality }
                .thenBy { it.name },
        )
    }

    fun displayLanguage(code: String, ui: Locale = Locale.US): String {
        val raw = code.trim()
        if (raw.isEmpty()) return ""
        return Locale.forLanguageTag(raw).getDisplayLanguage(ui).ifBlank { raw }
    }

    fun displayAccent(tag: String, ui: Locale = Locale.US): String {
        val loc = Locale.forLanguageTag(tag.trim())
        val country = loc.getDisplayCountry(ui)
        val script = loc.getDisplayScript(ui)
        return listOf(country, script).filter { it.isNotBlank() }.joinToString(" · ").ifBlank { tag }
    }

    fun shortName(name: String): String {
        val parts = name.split('-').filter { it.isNotEmpty() }
        return if (parts.size <= 2) name else parts.takeLast(2).joinToString("-")
    }
}

object TtsVoicePick {
    fun best(
        candidates: List<TtsVoiceCandidate>,
        preferredTag: String,
        minQuality: Int = 0,
        preferredName: String = "",
    ): TtsVoiceCandidate? {
        if (candidates.isEmpty()) return null
        val named = preferredName.trim()
        val usable = if (minQuality > 0) candidates.filter { it.quality >= minQuality } else candidates
        if (usable.isEmpty()) return null
        if (named.isNotEmpty()) {
            return usable.firstOrNull { it.name == named }
        }
        val pref = preferredTag.trim()
        val localeMatch = if (pref.isEmpty()) {
            usable
        } else {
            usable.filter { matchesLocale(it.languageTag, pref) }.ifEmpty { usable }
        }
        val local = localeMatch.filterNot { it.networkRequired }.ifEmpty { localeMatch }
        return local.maxWithOrNull(
            compareBy<TtsVoiceCandidate> { it.quality }
                .thenBy { -it.latency }
                .thenBy { it.name },
        )
    }

    private fun matchesLocale(tag: String, preferred: String): Boolean {
        val have = tag.trim()
        val want = preferred.trim()
        if (have.isEmpty() || want.isEmpty()) return false
        return have.equals(want, ignoreCase = true) ||
            have.startsWith("$want-", ignoreCase = true) ||
            want.startsWith("$have-", ignoreCase = true)
    }
}
