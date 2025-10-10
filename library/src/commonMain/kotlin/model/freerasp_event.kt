package model

/**
 * Represents events detected by freerasp.
 */
sealed class freeraspEvent {
    /**
     * Indicates that the device has privileged access (e.g., root or jailbreak).
     */
    data object PrivilegedAccess : freeraspEvent()

    /**
     * Indicates that the app is running in debug mode.
     */
    data object Debug : freeraspEvent()

    /**
     * Indicates that the app is running on a simulator or emulator.
     */
    data object Simulator : freeraspEvent()

    /**
     * Indicates that the app's integrity has been compromised.
     */
    data object AppIntegrity : freeraspEvent()

    /**
     * Indicates that the app was installed from an unofficial store.
     */
    data object UnofficialStore : freeraspEvent()

    /**
     * Indicates that hooking was detected.
     */
    data object Hooks : freeraspEvent()

    /**
     * Indicates that the device might change.
     */
    data object DeviceBinding : freeraspEvent()

    /**
     * Indicates that there are issues with code obfuscation.
     */
    data object ObfuscationIssues : freeraspEvent()

    /**
     * Indicates that a screenshot was taken.
     */
    data object Screenshot : freeraspEvent()

    /**
     * Indicates that screen recording is active.
     */
    data object ScreenRecording : freeraspEvent()

    /**
     * Indicates that the device passcode is not set.
     */
    data object Passcode : freeraspEvent()

    /**
     * Indicates that secure hardware is not available on the device.
     */
    data object SecureHardwareNotAvailable : freeraspEvent()

    /**
     * Indicates that a system VPN is active.
     */
    data object SystemVPN : freeraspEvent()

    /**
     * Indicates that developer mode is enabled on the device.
     */
    data object DevMode : freeraspEvent()

    /**
     * Indicates that ADB is enabled on the device.
     */
    data object AdbEnabled : freeraspEvent()

    /**
     * Indicates that multiple instances of the app are running.
     */
    data object MultiInstance : freeraspEvent()

    /**
     * Indicates that the device ID is changed.
     */
    data object DeviceID : freeraspEvent()

    /**
     * Indicates that malware was detected on the device.
     *
     * @param suspiciousAppInfo A list of suspicious apps found on the device.
     */
    data class Malware(val suspiciousAppInfo: List<SuspiciousAppInfo>) : freeraspEvent()
}