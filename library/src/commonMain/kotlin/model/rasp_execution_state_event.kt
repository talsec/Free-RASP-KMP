package com.freeraspkmp.model

/**
 * Represents events related to the RASP execution lifecycle.
 */
sealed class RaspExecutionStateEvent {
    /**
     * Indicates that all security checks have finished.
     */
    data object AllChecksFinished : RaspExecutionStateEvent()
}
