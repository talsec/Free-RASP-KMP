package api

import kotlinx.coroutines.flow.SharedFlow
import model.config.freeraspConfig
import model.FreeRaspEvent

/**
 * Singleton object providing access to freerasp KMP functionality.
 */
expect object freeraspKMP {
    /**
     * A [SharedFlow] of [FreeRaspEvent] which emits events about security threats.
     */
    val threatEvents: SharedFlow<FreeRaspEvent>

    /**
     * Starts the freerasp protection with the given [config].
     *
     * @param config The configuration for freerasp.
     */
    suspend fun start(config: freeraspConfig)

    /**
     * Adds the given [packageName] to the whitelist.
     *
     * @param packageName The package name to add to the whitelist.
     */
    suspend fun addToWhiteList(packageName: String)

    /**
     * Stores the given [data] as an external ID.
     *
     * @param data The external ID to store.
     */
    suspend fun storeExternalId(data: String)

    /**
     * Returns the icon of the app with the given [packageName] as a Base64 encoded string.
     *
     * @param packageName The package name of the app.
     * @return The app icon as a Base64 encoded string.
     */
    suspend fun getAppIcon(packageName: String): String

    /**
     * Enables or disables screen capture blocking.
     *
     * @param enable `true` to block screen capture, `false` to allow it.
     */
    suspend fun blockScreenCapture(enable: Boolean)

    /**
     * Returns whether screen capture is currently blocked.
     *
     * @return `true` if screen capture is blocked, `false` otherwise.
     */
    suspend fun isScreenCaptureBlocked(): Boolean
}