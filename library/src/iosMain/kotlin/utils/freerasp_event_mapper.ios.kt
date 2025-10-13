package utils

import model.FreeRaspEvent

internal fun mapStringToFreeraspEvent(threatString: String?): FreeRaspEvent? {
    return when (threatString) {
        "appIntegrity" -> FreeRaspEvent.AppIntegrity
        "jailbreak" -> FreeRaspEvent.PrivilegedAccess
        "debug" -> FreeRaspEvent.Debug
        "runtimeManipulation" -> FreeRaspEvent.Hooks
        "passcode" -> FreeRaspEvent.Passcode
        "passcodeChange" -> FreeRaspEvent.Passcode // Or a new event if you want to distinguish
        "simulator" -> FreeRaspEvent.Simulator
        "missingSecureEnclave" -> FreeRaspEvent.SecureHardwareNotAvailable
        "systemVPN" -> FreeRaspEvent.SystemVPN
        "deviceChange" -> FreeRaspEvent.DeviceBinding // Assuming 'deviceChange' maps to DeviceBinding
        "deviceID" -> FreeRaspEvent.DeviceID
        "unofficialStore" -> FreeRaspEvent.UnofficialStore
        "screenshot" -> FreeRaspEvent.Screenshot
        "screenRecording" -> FreeRaspEvent.ScreenRecording
        else -> {
            println("Received unknown threat ($threatString)")
            null
        }
    }
}