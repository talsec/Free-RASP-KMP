package utils

import threat.Threat

internal fun mapStringToThreat(threatString: String?): Threat? {
    return when (threatString) {
        "jailbreak" -> Threat.PRIVILEGED_ACCESS
        "debug" -> Threat.DEBUG
        "simulator" -> Threat.SIMULATOR
        "appIntegrity" -> Threat.APP_INTEGRITY
        "unofficialStore" -> Threat.UNOFFICIAL_STORE
        "runtimeManipulation", "hook" -> Threat.HOOKS
        "deviceBinding" -> Threat.DEVICE_BINDING
        "passcode" -> Threat.PASSCODE
        "missingSecureEnclave" -> Threat.SECURE_HARDWARE_NOT_AVAILABLE
        "systemVPN" -> Threat.SYSTEM_VPN
        "deviceID" -> Threat.DEVICE_ID
        "screenshot" -> Threat.SCREENSHOT
        "screenRecording" -> Threat.SCREEN_RECORDING
        else -> {
            println("Recieved unknown threat ($threatString)")
            null
        }
    }
}