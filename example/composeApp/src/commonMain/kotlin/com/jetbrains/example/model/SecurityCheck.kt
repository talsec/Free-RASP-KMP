package com.jetbrains.example.model

import com.freeraspkmp.model.FreeRaspEvent

enum class CheckId {
    PRIVILEGED_ACCESS, DEBUG, SIMULATOR, APP_INTEGRITY, UNOFFICIAL_STORE,
    HOOKS, DEVICE_BINDING, OBFUSCATION_ISSUES, SCREENSHOT, SCREEN_RECORDING,
    PASSCODE, SECURE_HARDWARE, SYSTEM_VPN, DEV_MODE, ADB_ENABLED,
    MULTI_INSTANCE, DEVICE_ID, TIME_SPOOFING, UNSECURE_WIFI,
    LOCATION_SPOOFING, AUTOMATION, MALWARE
}

data class SecurityCheck(
    val id: CheckId,
    val name: String,
    val description: String,
    val isDetected: Boolean = false,
)

fun FreeRaspEvent.toCheckId(): CheckId = when (this) {
    is FreeRaspEvent.PrivilegedAccess -> CheckId.PRIVILEGED_ACCESS
    is FreeRaspEvent.Debug -> CheckId.DEBUG
    is FreeRaspEvent.Simulator -> CheckId.SIMULATOR
    is FreeRaspEvent.AppIntegrity -> CheckId.APP_INTEGRITY
    is FreeRaspEvent.UnofficialStore -> CheckId.UNOFFICIAL_STORE
    is FreeRaspEvent.Hooks -> CheckId.HOOKS
    is FreeRaspEvent.DeviceBinding -> CheckId.DEVICE_BINDING
    is FreeRaspEvent.ObfuscationIssues -> CheckId.OBFUSCATION_ISSUES
    is FreeRaspEvent.Screenshot -> CheckId.SCREENSHOT
    is FreeRaspEvent.ScreenRecording -> CheckId.SCREEN_RECORDING
    is FreeRaspEvent.Passcode -> CheckId.PASSCODE
    is FreeRaspEvent.SecureHardwareNotAvailable -> CheckId.SECURE_HARDWARE
    is FreeRaspEvent.SystemVPN -> CheckId.SYSTEM_VPN
    is FreeRaspEvent.DevMode -> CheckId.DEV_MODE
    is FreeRaspEvent.AdbEnabled -> CheckId.ADB_ENABLED
    is FreeRaspEvent.MultiInstance -> CheckId.MULTI_INSTANCE
    is FreeRaspEvent.DeviceID -> CheckId.DEVICE_ID
    is FreeRaspEvent.TimeSpoofing -> CheckId.TIME_SPOOFING
    is FreeRaspEvent.UnsecureWifi -> CheckId.UNSECURE_WIFI
    is FreeRaspEvent.LocationSpoofing -> CheckId.LOCATION_SPOOFING
    is FreeRaspEvent.Automation -> CheckId.AUTOMATION
    is FreeRaspEvent.Malware -> CheckId.MALWARE
}

val initialChecks: List<SecurityCheck> = listOf(
    SecurityCheck(CheckId.PRIVILEGED_ACCESS, "Privileged Access", "Detects root or jailbreak"),
    SecurityCheck(CheckId.DEBUG, "Debug", "Detects debugger attachment"),
    SecurityCheck(CheckId.SIMULATOR, "Simulator", "Detects emulator or simulator"),
    SecurityCheck(CheckId.APP_INTEGRITY, "App Integrity", "Verifies app signature"),
    SecurityCheck(CheckId.UNOFFICIAL_STORE, "Unofficial Store", "Detects unofficial app source"),
    SecurityCheck(CheckId.HOOKS, "Hooks", "Detects runtime hooking frameworks"),
    SecurityCheck(CheckId.DEVICE_BINDING, "Device Binding", "Verifies device binding"),
    SecurityCheck(CheckId.OBFUSCATION_ISSUES, "Obfuscation Issues", "Verifies code obfuscation"),
    SecurityCheck(CheckId.SCREENSHOT, "Screenshot", "Detects screenshot activity"),
    SecurityCheck(CheckId.SCREEN_RECORDING, "Screen Recording", "Detects screen recording"),
    SecurityCheck(CheckId.PASSCODE, "Passcode", "Verifies screen lock is enabled"),
    SecurityCheck(CheckId.SECURE_HARDWARE, "Secure Hardware", "Verifies secure hardware availability"),
    SecurityCheck(CheckId.SYSTEM_VPN, "System VPN", "Detects active VPN connection"),
    SecurityCheck(CheckId.DEV_MODE, "Developer Mode", "Detects developer options enabled"),
    SecurityCheck(CheckId.ADB_ENABLED, "ADB Enabled", "Detects ADB debugging enabled"),
    SecurityCheck(CheckId.MULTI_INSTANCE, "Multi Instance", "Detects app cloning"),
    SecurityCheck(CheckId.DEVICE_ID, "Device ID", "Verifies device identity"),
    SecurityCheck(CheckId.TIME_SPOOFING, "Time Spoofing", "Detects system time manipulation"),
    SecurityCheck(CheckId.UNSECURE_WIFI, "Unsecure WiFi", "Detects insecure network connection"),
    SecurityCheck(CheckId.LOCATION_SPOOFING, "Location Spoofing", "Detects GPS spoofing"),
    SecurityCheck(CheckId.AUTOMATION, "Automation", "Detects automation tools"),
    SecurityCheck(CheckId.MALWARE, "Malware", "Detects malicious applications"),
)
