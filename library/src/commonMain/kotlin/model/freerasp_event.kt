package model

/**
 * Represents events detected by freerasp.
 */
sealed class FreeRaspEvent {
    /**
     * Indicates that the device has privileged access (e.g., root or jailbreak).
     */
    object PrivilegedAccess : FreeRaspEvent()

    /**
     * Indicates that the app is running in debug mode.
     */
    object Debug : FreeRaspEvent()

    /**
     * Indicates that the app is running on a simulator or emulator.
     */
    object Simulator : FreeRaspEvent()

    /**
     * Indicates that the app's integrity has been compromised.
     */
    object AppIntegrity : FreeRaspEvent()

    /**
     * Indicates that the app was installed from an unofficial store.
     */
    object UnofficialStore : FreeRaspEvent()

    /**
     * Indicates that method hooking was detected.
     */
    object Hooks : FreeRaspEvent()

    /**
     * Indicates that the device binding does not match.
     */
    object DeviceBinding : FreeRaspEvent()

    /**
     * Indicates that there are issues with code obfuscation.
     */
    object ObfuscationIssues : FreeRaspEvent()

    /**
     * Indicates that a screenshot was taken.
     */
    object Screenshot : FreeRaspEvent()

    /**
     * Indicates that screen recording is active.
     */
    object ScreenRecording : FreeRaspEvent()

    /**
     * Indicates that the device passcode is not set.
     */
    object Passcode : FreeRaspEvent()

    /**
     * Indicates that secure hardware is not available on the device.
     */
    object SecureHardwareNotAvailable : FreeRaspEvent()

    /**
     * Indicates that a system VPN is active.
     */
    object SystemVpn : FreeRaspEvent()

    /**
     * Indicates that developer mode is enabled on the device.
     */
    object DevMode : FreeRaspEvent()

    /**
     * Indicates that ADB is enabled on the device.
     */
    object AdbEnabled : FreeRaspEvent()

    /**
     * Indicates that multiple instances of the app are running.
     */
    object MultiInstance : FreeRaspEvent()

    /**
     * Indicates that the device ID is not available.
     */
    object DeviceId : FreeRaspEvent()

    /**
     * Indicates that malware was detected on the device.
     *
     * @param suspiciousAppInfo A list of suspicious apps found on the device.
     */
    data class MalwareDetected(val suspiciousAppInfo: List<SuspiciousAppInfo>) : FreeRaspEvent()
}