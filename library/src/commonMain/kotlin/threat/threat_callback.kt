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
