package dev.foss.goldenpath.feedback

/**
 * Parse `goldenpath://settings/about/feedback` deep links with optional subject.
 * Pure string parsing so JVM unit tests need no Robolectric.
 */
object FeedbackDeepLink {
    const val SCHEME = "goldenpath"
    const val DEFAULT_KIND = "bug"

    fun parse(uriString: String?): FeedbackRoute? {
        if (uriString.isNullOrBlank()) return null
        val trimmed = uriString.trim()
        val schemeSep = trimmed.indexOf("://")
        if (schemeSep <= 0) return null
        val scheme = trimmed.substring(0, schemeSep).lowercase()
        if (scheme != SCHEME) return null
        val rest = trimmed.substring(schemeSep + 3)
        val querySep = rest.indexOf('?')
        val authorityPath = if (querySep >= 0) rest.substring(0, querySep) else rest
        val query = if (querySep >= 0) rest.substring(querySep + 1) else ""
        val slash = authorityPath.indexOf('/')
        val host = if (slash >= 0) authorityPath.substring(0, slash) else authorityPath
        val path = if (slash >= 0) authorityPath.substring(slash).trimEnd('/') else ""
        if (host != "settings") return null
        if (path != "/about/feedback" && !path.startsWith("/about/feedback/")) return null
        val params = queryParams(query)
        val subject = sanitizeSubject(params["subject"].orEmpty())
        val kind = when (params["kind"]?.lowercase()) {
            "feature" -> "feature"
            else -> DEFAULT_KIND
        }
        return FeedbackRoute(kind = kind, subject = subject)
    }

    fun sanitizeSubject(raw: String): String {
        val trimmed = raw.trim().take(120)
        if (trimmed.isEmpty()) return ""
        return trimmed
            .replace(Regex("[\\r\\n\\t]+"), " ")
            .replace(Regex("[<>\"'`]"), "")
            .trim()
    }

    private fun queryParams(query: String): Map<String, String> {
        if (query.isEmpty()) return emptyMap()
        return query.split('&').mapNotNull { part ->
            val eq = part.indexOf('=')
            if (eq <= 0) return@mapNotNull null
            val key = part.substring(0, eq)
            val value = part.substring(eq + 1)
            key to java.net.URLDecoder.decode(value, Charsets.UTF_8)
        }.toMap()
    }
}

data class FeedbackRoute(val kind: String, val subject: String)
