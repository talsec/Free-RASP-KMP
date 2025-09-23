package model

sealed class FreeRaspEvent {
    object PrivilegedAccess : FreeRaspEvent()
    object Debug : FreeRaspEvent()
    object Simulator : FreeRaspEvent()
    object AppIntegrity : FreeRaspEvent()
    object UnofficialStore : FreeRaspEvent()
    object Hooks : FreeRaspEvent()
    object DeviceBinding : FreeRaspEvent()
    object ObfuscationIssues : FreeRaspEvent()
    object Screenshot : FreeRaspEvent()
    object ScreenRecording : FreeRaspEvent()
    object Passcode : FreeRaspEvent()
    object SecureHardwareNotAvailable : FreeRaspEvent()
    object SystemVpn : FreeRaspEvent()
    object DevMode : FreeRaspEvent()
    object AdbEnabled : FreeRaspEvent()
    object MultiInstance : FreeRaspEvent()
    object DeviceId : FreeRaspEvent()
    data class MalwareDetected(val suspiciousAppInfo: List<SuspiciousAppInfo>) : FreeRaspEvent()
}