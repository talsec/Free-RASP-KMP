package utils

import model.freeraspEvent

internal fun mapStringToFreeraspEvent(threatString: String?): freeraspEvent? {
    return when (threatString) {
        "jailbreak" -> freeraspEvent.PrivilegedAccess
        "debug" -> freeraspEvent.Debug
        "simulator" -> freeraspEvent.Simulator
        "appIntegrity" -> freeraspEvent.AppIntegrity
        "unofficialStore" -> freeraspEvent.UnofficialStore
        "runtimeManipulation", "hook" -> freeraspEvent.Hooks
        "deviceBinding" -> freeraspEvent.DeviceBinding
        "passcode" -> freeraspEvent.Passcode
        "missingSecureEnclave" -> freeraspEvent.SecureHardwareNotAvailable
        "systemVPN" -> freeraspEvent.SystemVPN
        "deviceID" -> freeraspEvent.DeviceID
        "screenshot" -> freeraspEvent.Screenshot
        "screenRecording" -> freeraspEvent.ScreenRecording
        else -> {
            println("Received unknown threat ($threatString)")
            null
        }
    }
}