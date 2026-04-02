package com.freeraspkmp.model

/**
 * Represents events detected by freeRASP.
 */
sealed class FreeRaspEvent {
    /**
     * Indicates that the device has privileged access (e.g., root or jailbreak).
     */
    data object PrivilegedAccess : FreeRaspEvent()

    /**
     * Indicates that the app is running in debug mode.
     */
    data object Debug : FreeRaspEvent()

    /**
     * Indicates that the app is running on a simulator or emulator.
     */
    data object Simulator : FreeRaspEvent()

    /**
     * Indicates that the app's integrity has been compromised.
     */
    data object AppIntegrity : FreeRaspEvent()

    /**
     * Indicates that the app was installed from an unofficial store.
     */
    data object UnofficialStore : FreeRaspEvent()

    /**
     * Indicates that hooking was detected.
     */
    data object Hooks : FreeRaspEvent()

    /**
     * Indicates that the device might change.
     */
    data object DeviceBinding : FreeRaspEvent()

    /**
     * Indicates that there are issues with code obfuscation.
     */
    data object ObfuscationIssues : FreeRaspEvent()

    /**
     * Indicates that a screenshot was taken.
     */
    data object Screenshot : FreeRaspEvent()

    /**
     * Indicates that screen recording is active.
     */
    data object ScreenRecording : FreeRaspEvent()

    /**
     * Indicates that the device passcode is not set.
     */
    data object Passcode : FreeRaspEvent()

    /**
     * Indicates that secure hardware is not available on the device.
     */
    data object SecureHardwareNotAvailable : FreeRaspEvent()

    /**
     * Indicates that a system VPN is active.
     */
    data object SystemVPN : FreeRaspEvent()

    /**
     * Indicates that developer mode is enabled on the device.
     */
    data object DevMode : FreeRaspEvent()

    /**
     * Indicates that ADB is enabled on the device.
     */
    data object AdbEnabled : FreeRaspEvent()

    /**
     * Indicates that multiple instances of the app are running.
     */
    data object MultiInstance : FreeRaspEvent()

    /**
     * Indicates that the device ID is changed.
     */
    data object DeviceID : FreeRaspEvent()

    /**
     * Indicates that the device is connected to an unsecure Wi-Fi network.
     */
    data object UnsecureWifi : FreeRaspEvent()

    /**
     * Indicates that device time spoofing is detected.
     */
    data object TimeSpoofing : FreeRaspEvent()

    /**
     * Indicates that device location spoofing is detected.
     */
    data object LocationSpoofing : FreeRaspEvent()

    /**
     * Indicates that automation framework (e.g. Appium) was detected. Android only.
     */
    data object Automation : FreeRaspEvent()

    /**
     * Indicates that malware was detected on the device.
     *
     * @param suspiciousAppInfo A list of suspicious apps found on the device.
     */
    data class Malware(val suspiciousAppInfo: List<SuspiciousAppInfo>) : FreeRaspEvent()
}