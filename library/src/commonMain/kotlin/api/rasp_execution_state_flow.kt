package com.freeraspkmp.api

import com.freeraspkmp.model.RaspExecutionStateEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

// One replay slot per execution state, so a late subscriber still receives every state of the run in order
internal fun raspExecutionStateFlow() = MutableSharedFlow<RaspExecutionStateEvent>(
    replay = 3,
    onBufferOverflow = BufferOverflow.DROP_OLDEST,
)
