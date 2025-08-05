package threat

import model.SuspiciousAppInfo

interface ThreatCallback{
    fun onHooks()
    fun onDebug()
    fun onPasscode()
    fun onDeviceID()
    fun onSimulator()
    fun onAppIntegrity()
    fun onObfuscationIssues()
    fun onDeviceBinding()
    fun onUnofficialStore()
    fun onPrivilegedAccess()
    fun onSecureHardwareNotAvailable()
    fun onSystemVPN()
    fun onDevMode()
    fun onADBEnabled()
    fun onMalwareDetected(suspiciousAppInfo: List<SuspiciousAppInfo>)
    fun onScreenshot()
    fun onScreenRecording()
    fun onMultiInstance()
}

//toto možno prerobiť tak aby to bolo v common code override
/*data class ThreatCallback(
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
    val onMalwareDetected: ((List<SuspiciousAppInfo?>) -> Unit)? = null,
    val onScreenshot: (() -> Unit)? = null,
    val onScreenRecording: (() -> Unit)? = null,
    val onMultiInstance: (() -> Unit)? = null
)*/