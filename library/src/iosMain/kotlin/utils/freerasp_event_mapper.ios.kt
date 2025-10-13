package utils

import model.FreeRASPEvent

internal fun mapStringToFreeraspEvent(threatString: String?): FreeRASPEvent? {
    return when (threatString) {
        "appIntegrity" -> FreeRASPEvent.AppIntegrity
        "jailbreak" -> FreeRASPEvent.PrivilegedAccess
        "debug" -> FreeRASPEvent.Debug
        "runtimeManipulation" -> FreeRASPEvent.Hooks
        "passcode" -> FreeRASPEvent.Passcode
        "passcodeChange" -> FreeRASPEvent.Passcode // Or a new event if you want to distinguish
        "simulator" -> FreeRASPEvent.Simulator
        "missingSecureEnclave" -> FreeRASPEvent.SecureHardwareNotAvailable
        "systemVPN" -> FreeRASPEvent.SystemVPN
        "deviceChange" -> FreeRASPEvent.DeviceBinding // Assuming 'deviceChange' maps to DeviceBinding
        "deviceID" -> FreeRASPEvent.DeviceID
        "unofficialStore" -> FreeRASPEvent.UnofficialStore
        "screenshot" -> FreeRASPEvent.Screenshot
        "screenRecording" -> FreeRASPEvent.ScreenRecording
        else -> {
            println("Received unknown threat ($threatString)")
            null
        }
    }
}