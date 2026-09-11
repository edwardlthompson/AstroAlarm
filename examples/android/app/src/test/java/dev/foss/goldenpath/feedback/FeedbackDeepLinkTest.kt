package dev.foss.goldenpath.feedback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedbackDeepLinkTest {
    @Test
    fun parsesFeedbackWithSanitizedSubject() {
        val route = FeedbackDeepLink.parse(
            "goldenpath://settings/about/feedback?kind=feature&subject=%3Cscript%3Ehi",
        )!!
        assertEquals("feature", route.kind)
        assertEquals("scripthi", route.subject)
        assertEquals("scripthi", FeedbackDeepLink.sanitizeSubject("<script>hi"))
    }

    @Test
    fun invalidFallsBackToNull() {
        assertNull(FeedbackDeepLink.parse("https://example.com"))
        assertNull(FeedbackDeepLink.parse(null))
    }

    @Test
    fun defaultKindIsBug() {
        val route = FeedbackDeepLink.parse("goldenpath://settings/about/feedback")!!
        assertEquals("bug", route.kind)
        assertTrue(route.subject.isEmpty())
    }
}
