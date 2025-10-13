package model

/**
 * Represents events detected by freerasp.
 */
sealed class FreeRASPEvent {
    /**
     * Indicates that the device has privileged access (e.g., root or jailbreak).
     */
    data object PrivilegedAccess : FreeRASPEvent()

    /**
     * Indicates that the app is running in debug mode.
     */
    data object Debug : FreeRASPEvent()

    /**
     * Indicates that the app is running on a simulator or emulator.
     */
    data object Simulator : FreeRASPEvent()

    /**
     * Indicates that the app's integrity has been compromised.
     */
    data object AppIntegrity : FreeRASPEvent()

    /**
     * Indicates that the app was installed from an unofficial store.
     */
    data object UnofficialStore : FreeRASPEvent()

    /**
     * Indicates that hooking was detected.
     */
    data object Hooks : FreeRASPEvent()

    /**
     * Indicates that the device might change.
     */
    data object DeviceBinding : FreeRASPEvent()

    /**
     * Indicates that there are issues with code obfuscation.
     */
    data object ObfuscationIssues : FreeRASPEvent()

    /**
     * Indicates that a screenshot was taken.
     */
    data object Screenshot : FreeRASPEvent()

    /**
     * Indicates that screen recording is active.
     */
    data object ScreenRecording : FreeRASPEvent()

    /**
     * Indicates that the device passcode is not set.
     */
    data object Passcode : FreeRASPEvent()

    /**
     * Indicates that secure hardware is not available on the device.
     */
    data object SecureHardwareNotAvailable : FreeRASPEvent()

    /**
     * Indicates that a system VPN is active.
     */
    data object SystemVPN : FreeRASPEvent()

    /**
     * Indicates that developer mode is enabled on the device.
     */
    data object DevMode : FreeRASPEvent()

    /**
     * Indicates that ADB is enabled on the device.
     */
    data object AdbEnabled : FreeRASPEvent()

    /**
     * Indicates that multiple instances of the app are running.
     */
    data object MultiInstance : FreeRASPEvent()

    /**
     * Indicates that the device ID is changed.
     */
    data object DeviceID : FreeRASPEvent()

    /**
     * Indicates that malware was detected on the device.
     *
     * @param suspiciousAppInfo A list of suspicious apps found on the device.
     */
    data class Malware(val suspiciousAppInfo: List<SuspiciousAppInfo>) : FreeRASPEvent()
}