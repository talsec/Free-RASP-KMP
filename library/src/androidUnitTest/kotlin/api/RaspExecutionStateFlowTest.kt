package com.freeraspkmp.api

import com.freeraspkmp.model.RaspExecutionStateEvent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RaspExecutionStateFlowTest {

    @Test
    fun replaysEveryStateInTheOrderItWasEmitted() {
        val flow = raspExecutionStateFlow()
        val states = listOf(
            RaspExecutionStateEvent.CriticalChecksFinished,
            RaspExecutionStateEvent.FastChecksFinished,
            RaspExecutionStateEvent.PostponedChecksFinished,
        )

        states.forEach { assertTrue(flow.tryEmit(it)) }

        assertEquals(states, flow.replayCache)
    }
}
