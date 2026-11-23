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
     * Replays every state of the current run in the order it occurred, so late subscribers
     * also receive the states emitted before they subscribed.
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
     * On Android, it reports the state of the activity in the foreground and returns `false` when there is none.
     *
     * @return `true` if screen capture is blocked, `false` otherwise.
     */
    suspend fun isScreenCaptureBlocked(): Boolean

    /**
     * Blocks or allows accessibility services reading or controlling the screen of the activity in the foreground.
     * Android only; on iOS, and on Android without an activity in the foreground, it logs a message and does nothing.
     *
     * @param enable `true` to block screen control, `false` to allow it.
     */
    suspend fun blockScreenControl(enable: Boolean)

    /**
     * Returns whether screen control is blocked for the activity in the foreground.
     * Android only; returns `false` on iOS and on Android without an activity in the foreground.
     *
     * @return `true` if screen control is blocked, `false` otherwise.
     */
    suspend fun isScreenControlBlocked(): Boolean

    /**
     * Starts the detection of cellular and VoIP calls for the activity in the foreground.
     * Calls are reported as [FreeRaspEvent.OngoingCall]. The detection follows the activity when it is recreated.
     * Android only; on iOS, and on Android without an activity in the foreground, it logs a message and does nothing.
     */
    suspend fun startOngoingCallDetection()

    /**
     * Stops the ongoing call detection. Android only; on iOS, it logs a message and does nothing.
     */
    suspend fun stopOngoingCallDetection()
}