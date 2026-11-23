package com.freeraspkmp.model

/**
 * State of a call reported by [FreeRaspEvent.OngoingCall].
 */
enum class CallState {
    /** A call has started. */
    STARTED,

    /** A call is in progress. */
    ONGOING,

    /** The call has ended. */
    STOPPED,
}
