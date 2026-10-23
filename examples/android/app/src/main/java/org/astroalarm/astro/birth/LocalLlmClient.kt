package org.astroalarm.astro.birth

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException

sealed class LocalLlmResult {
    data class Ok(val text: String) : LocalLlmResult()
    data class Err(val reason: String) : LocalLlmResult()
}

fun interface LocalLlmTransport {
    /** POST JSON body; throw [SocketTimeoutException] on timeout. */
    fun post(url: String, jsonBody: String, timeoutMs: Int): String
}

/** Default HttpURLConnection transport for Ollama-compatible `/api/generate`. */
class HttpLocalLlmTransport : LocalLlmTransport {
    override fun post(url: String, jsonBody: String, timeoutMs: Int): String {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.connectTimeout = timeoutMs
        conn.readTimeout = timeoutMs
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/json")
        conn.outputStream.bufferedWriter().use { it.write(jsonBody) }
        if (conn.responseCode != HttpURLConnection.HTTP_OK) {
            error("http_${conn.responseCode}")
        }
        return conn.inputStream.bufferedReader().use { it.readText() }
    }
}

/**
 * Optional user-owned local HTTP LLM (e.g. Ollama). Default timeout 10s.
 * No cloud vendor and no API key.
 */
class LocalLlmClient(
    private val transport: LocalLlmTransport = HttpLocalLlmTransport(),
    private val timeoutMs: Int = 10_000,
    private val model: String = "llama3.2",
) {
    fun ask(baseUrl: String, prompt: String): LocalLlmResult {
        if (prompt.isBlank()) return LocalLlmResult.Err("empty_prompt")
        val root = baseUrl.trimEnd('/')
        val body = JSONObject()
            .put("model", model)
            .put("prompt", prompt)
            .put("stream", false)
            .toString()
        return runCatching {
            val raw = transport.post("$root/api/generate", body, timeoutMs)
            val text = JSONObject(raw).optString("response", "").trim()
            if (text.isEmpty()) LocalLlmResult.Err("empty_response")
            else LocalLlmResult.Ok(text)
        }.getOrElse { e ->
            when {
                e is SocketTimeoutException || e.cause is SocketTimeoutException ->
                    LocalLlmResult.Err("timeout")
                e is UnknownHostException || e.cause is UnknownHostException ->
                    LocalLlmResult.Err("unreachable")
                e.message?.startsWith("http_") == true ->
                    LocalLlmResult.Err(e.message!!)
                else -> LocalLlmResult.Err("unreachable")
            }
        }
    }
}
