package com.freeraspkmp.model

/**
 * Represents events related to the RASP execution lifecycle.
 */
sealed class RaspExecutionStateEvent {
    /**
     * Indicates that the critical security checks have finished.
     */
    data object CriticalChecksFinished : RaspExecutionStateEvent()

    /**
     * Indicates that the fast security checks have finished. The postponed checks may still be running.
     */
    data object FastChecksFinished : RaspExecutionStateEvent()

    /**
     * Indicates that the postponed security checks have finished.
     */
    data object PostponedChecksFinished : RaspExecutionStateEvent()
}
