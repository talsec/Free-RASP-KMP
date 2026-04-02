package com.freeraspkmp.api

import kotlinx.coroutines.flow.SharedFlow
import com.freeraspkmp.model.config.freeraspConfig
import com.freeraspkmp.model.FreeRaspEvent
import com.freeraspkmp.model.RaspExecutionStateEvent

/**
 * Singleton object providing access to freeRASP KMP functionality.
 */
expect object FreeraspKMP {
    /**
     * A [SharedFlow] of [FreeRaspEvent] which emits events about security threats.
     */
    val threatEvents: SharedFlow<FreeRaspEvent>

    /**
     * A [SharedFlow] of [RaspExecutionStateEvent] which emits events about the RASP execution lifecycle.
     * Uses replay = 1, so late subscribers receive [RaspExecutionStateEvent.AllChecksFinished] even if
     * it was emitted before they subscribed.
     */
    val raspExecutionStateEvents: SharedFlow<RaspExecutionStateEvent>

    /**
     * Starts the freeRASP protection with the given [config].
     *
     * @param config The configuration for freeRASP.
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
     * @throws [com.freeraspkmp.model.exception.FreeraspKMPException] if the data does not match the required format.
     */
    suspend fun storeExternalId(data: String)

    /**
     * Removes the stored external ID.
     */
    suspend fun removeExternalId()

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