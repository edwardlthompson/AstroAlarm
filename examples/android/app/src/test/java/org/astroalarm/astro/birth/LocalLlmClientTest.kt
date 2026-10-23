package org.astroalarm.astro.birth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.net.SocketTimeoutException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class LocalLlmClientTest {
    @Test
    fun mockedTimeoutReturnsTypedError() {
        val client = LocalLlmClient(
            transport = LocalLlmTransport { _, _, _ -> throw SocketTimeoutException("slow") },
            timeoutMs = 10,
        )
        val result = client.ask(LocalLlmPrefs.DEFAULT_URL, "hello")
        assertTrue(result is LocalLlmResult.Err)
        assertEquals("timeout", (result as LocalLlmResult.Err).reason)
    }

    @Test
    fun okParsesOllamaResponse() {
        val client = LocalLlmClient(
            transport = LocalLlmTransport { _, _, _ ->
                """{"response":"A calm profile.","done":true}"""
            },
        )
        val result = client.ask("http://127.0.0.1:11434", "prompt")
        assertTrue(result is LocalLlmResult.Ok)
        assertEquals("A calm profile.", (result as LocalLlmResult.Ok).text)
    }
}
