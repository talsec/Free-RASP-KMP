package com.freeraspkmp.ios.utils

import com.freeraspkmp.model.FreeRaspEvent
import com.freeraspkmp.model.RaspExecutionStateEvent

internal fun mapStringToFreeraspEvent(threatString: String?): FreeRaspEvent? {
    return when (threatString) {
        "appIntegrity" -> FreeRaspEvent.AppIntegrity
        "jailbreak" -> FreeRaspEvent.PrivilegedAccess
        "debug" -> FreeRaspEvent.Debug
        "runtimeManipulation" -> FreeRaspEvent.Hooks
        "passcode" -> FreeRaspEvent.Passcode
        "passcodeChange" -> FreeRaspEvent.Passcode
        "simulator" -> FreeRaspEvent.Simulator
        "missingSecureEnclave" -> FreeRaspEvent.SecureHardwareNotAvailable
        "systemVPN" -> FreeRaspEvent.SystemVPN
        "deviceChange" -> FreeRaspEvent.DeviceBinding
        "deviceID" -> FreeRaspEvent.DeviceID
        "unofficialStore" -> FreeRaspEvent.UnofficialStore
        "screenshot" -> FreeRaspEvent.Screenshot
        "screenRecording" -> FreeRaspEvent.ScreenRecording
        "timeSpoofing" -> FreeRaspEvent.TimeSpoofing
        else -> {
            println("Received unknown threat ($threatString)")
            null
        }
    }
}

// TalsecBridge sends the execution states through the threat callback, under these names
internal fun mapStringToRaspExecutionStateEvent(stateString: String?): RaspExecutionStateEvent? {
    return when (stateString) {
        "criticalChecksFinished" -> RaspExecutionStateEvent.CriticalChecksFinished
        "fastChecksFinished" -> RaspExecutionStateEvent.FastChecksFinished
        "postponedChecksFinished" -> RaspExecutionStateEvent.PostponedChecksFinished
        else -> null
    }
}
