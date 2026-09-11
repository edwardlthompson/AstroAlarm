package dev.foss.goldenpath.push

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UnifiedPushConfigTest {
    @Test
    fun staysFossUntilDistributorExists() {
        assertNull(UnifiedPushConfig.endpointUrl())
        assertFalse(UnifiedPushConfig.usesProprietaryPush())
        assertTrue(UnifiedPushConfig.CONNECTOR_ACTION.startsWith("org.unifiedpush."))
        assertEquals(
            "org.unifiedpush.android.distributor.REGISTER",
            UnifiedPushConfig.REGISTER_ACTION,
        )
        assertFalse(UnifiedPushConfig.shouldRegister(emptyList()))
        assertTrue(UnifiedPushConfig.shouldRegister(listOf(UnifiedPushConfig.NTFY_PACKAGE)))
        assertTrue(UnifiedPushConfig.acceptsConnectorAction(UnifiedPushConfig.CONNECTOR_ACTION))
        assertFalse(UnifiedPushConfig.acceptsConnectorAction("other"))
    }
}
