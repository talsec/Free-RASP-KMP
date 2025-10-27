package utils

import model.FreeRaspEvent

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
        "allChecksFinished" -> FreeRaspEvent.AllChecksFinished
        else -> {
            println("Received unknown threat ($threatString)")
            null
        }
    }
}