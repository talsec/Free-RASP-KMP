package handlers

import com.aheaditec.talsec_security.security.api.SuspiciousAppInfo as NativeSuspiciousAppInfo
import com.aheaditec.talsec_security.security.api.ThreatListener
import model.freeraspEvent
import providers.ContextProvider
import utils.processMalwareData

internal class ThreatHandler (
    private val onEvent: (freeraspEvent) -> Unit
): ThreatListener.ThreatDetected, ThreatListener.DeviceState {
    override fun onRootDetected() = onEvent(freeraspEvent.PrivilegedAccess)

    override fun onDebuggerDetected()  = onEvent(freeraspEvent.Debug)

    override fun onEmulatorDetected() = onEvent(freeraspEvent.Simulator)

    override fun onTamperDetected() = onEvent(freeraspEvent.AppIntegrity)

    override fun onUntrustedInstallationSourceDetected() = onEvent(freeraspEvent.UnofficialStore)

    override fun onHookDetected() = onEvent(freeraspEvent.Hooks)

    override fun onDeviceBindingDetected() = onEvent(freeraspEvent.DeviceBinding)

    override fun onObfuscationIssuesDetected() = onEvent(freeraspEvent.ObfuscationIssues)

    override fun onMalwareDetected(nativeSuspiciousAppInfo: List<NativeSuspiciousAppInfo>?) {

        val nonNullNativeList = nativeSuspiciousAppInfo ?: emptyList()

        if(nonNullNativeList.isEmpty()) return

        val context = ContextProvider.getApplicationContext()
        val commonApps = processMalwareData(context, nonNullNativeList)

        if(commonApps.isNotEmpty()) {
            onEvent(freeraspEvent.Malware(commonApps))
        }
    }

    override fun onScreenshotDetected() = onEvent(freeraspEvent.Screenshot)

    override fun onScreenRecordingDetected() = onEvent(freeraspEvent.ScreenRecording)

    override fun onMultiInstanceDetected() = onEvent(freeraspEvent.MultiInstance)

    override fun onUnlockedDeviceDetected() = onEvent(freeraspEvent.Passcode)

    override fun onHardwareBackedKeystoreNotAvailableDetected() = onEvent(freeraspEvent.SecureHardwareNotAvailable)

    override fun onDeveloperModeDetected() = onEvent(freeraspEvent.DevMode)

    override fun onADBEnabledDetected() = onEvent(freeraspEvent.AdbEnabled)

    override fun onSystemVPNDetected() = onEvent(freeraspEvent.SystemVPN)

}