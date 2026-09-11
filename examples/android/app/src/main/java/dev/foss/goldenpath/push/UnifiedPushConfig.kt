package dev.foss.goldenpath.push

/**
 * FOSS push hook: register with a UnifiedPush distributor, never FCM / Play Services.
 * Disabled until a distributor is present on the device.
 */
object UnifiedPushConfig {
    const val REGISTER_ACTION = "org.unifiedpush.android.distributor.REGISTER"
    const val CONNECTOR_ACTION = "org.unifiedpush.android.connector.MESSAGE"
    const val NTFY_PACKAGE = "io.heckel.ntfy"

    fun endpointUrl(): String? = null

    fun usesProprietaryPush(): Boolean = false

    /** Registration stays off until a known distributor package is installed. */
    fun shouldRegister(installedPackages: Collection<String>): Boolean =
        !usesProprietaryPush() && installedPackages.any { it == NTFY_PACKAGE || it.contains("unifiedpush") }

    fun acceptsConnectorAction(action: String?): Boolean =
        action == CONNECTOR_ACTION
}
