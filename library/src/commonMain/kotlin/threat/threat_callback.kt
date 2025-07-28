package threat

expect class SuspiciousAppInfo //TODO
//or
//data class SuspiciousAppInfo(val packageName: String, val appName: String)

data class ThreatCallback(
    val onHooks: (() -> Unit)? = null,
    val onDebug: (() -> Unit)? = null,
    val onPasscode: (() -> Unit)? = null,
    val onDeviceID: (() -> Unit)? = null,
    val onSimulator: (() -> Unit)? = null,
    val onAppIntegrity: (() -> Unit)? = null,
    val onObfuscationIssues: (() -> Unit)? = null,
    val onDeviceBinding: (() -> Unit)? = null,
    val onUnofficialStore: (() -> Unit)? = null,
    val onPrivilegedAccess: (() -> Unit)? = null,
    val onSecureHardwareNotAvailable: (() -> Unit)? = null,
    val onSystemVPN: (() -> Unit)? = null,
    val onDevMode: (() -> Unit)? = null,
    val onADBEnabled: (() -> Unit)? = null,
    val onMalware: ((List<SuspiciousAppInfo?>) -> Unit)? = null,
    val onScreenshot: (() -> Unit)? = null,
    val onScreenRecording: (() -> Unit)? = null,
    val onMultiInstance: (() -> Unit)? = null
)