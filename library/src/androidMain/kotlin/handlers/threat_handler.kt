package handlers

import com.aheaditec.talsec_security.security.api.SuspiciousAppInfo
import com.aheaditec.talsec_security.security.api.ThreatListener
import model.FreeRaspEvent
import providers.ContextProvider
import utils.processMalwareData

internal class ThreatHandler(
    private val onEvent: (FreeRaspEvent) -> Unit
) : ThreatListener.ThreatDetected, ThreatListener.DeviceState {
    override fun onRootDetected() = onEvent(FreeRaspEvent.PrivilegedAccess)

    override fun onDebuggerDetected() = onEvent(FreeRaspEvent.Debug)

    override fun onEmulatorDetected() = onEvent(FreeRaspEvent.Simulator)

    override fun onTamperDetected() = onEvent(FreeRaspEvent.AppIntegrity)

    override fun onUntrustedInstallationSourceDetected() = onEvent(FreeRaspEvent.UnofficialStore)

    override fun onHookDetected() = onEvent(FreeRaspEvent.Hooks)

    override fun onDeviceBindingDetected() = onEvent(FreeRaspEvent.DeviceBinding)

    override fun onObfuscationIssuesDetected() = onEvent(FreeRaspEvent.ObfuscationIssues)

    override fun onMalwareDetected(nativeSuspiciousAppInfo: List<SuspiciousAppInfo>?) {

        if (nativeSuspiciousAppInfo.isNullOrEmpty()) {
            return
        }

        val context = ContextProvider.getApplicationContext()
        val commonApps = processMalwareData(context, nativeSuspiciousAppInfo)

        if (commonApps.isNotEmpty()) {
            onEvent(FreeRaspEvent.Malware(commonApps))
        }
    }

    override fun onScreenshotDetected() = onEvent(FreeRaspEvent.Screenshot)

    override fun onScreenRecordingDetected() = onEvent(FreeRaspEvent.ScreenRecording)

    override fun onMultiInstanceDetected() = onEvent(FreeRaspEvent.MultiInstance)

    override fun onUnlockedDeviceDetected() = onEvent(FreeRaspEvent.Passcode)

    override fun onHardwareBackedKeystoreNotAvailableDetected() =
        onEvent(FreeRaspEvent.SecureHardwareNotAvailable)

    override fun onDeveloperModeDetected() = onEvent(FreeRaspEvent.DevMode)

    override fun onADBEnabledDetected() = onEvent(FreeRaspEvent.AdbEnabled)

    override fun onSystemVPNDetected() = onEvent(FreeRaspEvent.SystemVPN)

}