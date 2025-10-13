package handlers

import com.aheaditec.talsec_security.security.api.SuspiciousAppInfo
import com.aheaditec.talsec_security.security.api.ThreatListener
import model.FreeRASPEvent
import providers.ContextProvider
import utils.processMalwareData

internal class ThreatHandler(
    private val onEvent: (FreeRASPEvent) -> Unit
) : ThreatListener.ThreatDetected, ThreatListener.DeviceState {
    override fun onRootDetected() = onEvent(FreeRASPEvent.PrivilegedAccess)

    override fun onDebuggerDetected() = onEvent(FreeRASPEvent.Debug)

    override fun onEmulatorDetected() = onEvent(FreeRASPEvent.Simulator)

    override fun onTamperDetected() = onEvent(FreeRASPEvent.AppIntegrity)

    override fun onUntrustedInstallationSourceDetected() = onEvent(FreeRASPEvent.UnofficialStore)

    override fun onHookDetected() = onEvent(FreeRASPEvent.Hooks)

    override fun onDeviceBindingDetected() = onEvent(FreeRASPEvent.DeviceBinding)

    override fun onObfuscationIssuesDetected() = onEvent(FreeRASPEvent.ObfuscationIssues)

    override fun onMalwareDetected(nativeSuspiciousAppInfo: List<SuspiciousAppInfo>?) {

        if (nativeSuspiciousAppInfo.isNullOrEmpty()) {
            return
        }

        val context = ContextProvider.getApplicationContext()
        val commonApps = processMalwareData(context, nativeSuspiciousAppInfo)

        if (commonApps.isNotEmpty()) {
            onEvent(FreeRASPEvent.Malware(commonApps))
        }
    }

    override fun onScreenshotDetected() = onEvent(FreeRASPEvent.Screenshot)

    override fun onScreenRecordingDetected() = onEvent(FreeRASPEvent.ScreenRecording)

    override fun onMultiInstanceDetected() = onEvent(FreeRASPEvent.MultiInstance)

    override fun onUnlockedDeviceDetected() = onEvent(FreeRASPEvent.Passcode)

    override fun onHardwareBackedKeystoreNotAvailableDetected() =
        onEvent(FreeRASPEvent.SecureHardwareNotAvailable)

    override fun onDeveloperModeDetected() = onEvent(FreeRASPEvent.DevMode)

    override fun onADBEnabledDetected() = onEvent(FreeRASPEvent.AdbEnabled)

    override fun onSystemVPNDetected() = onEvent(FreeRASPEvent.SystemVPN)

}