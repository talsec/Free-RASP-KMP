package handlers

import android.util.Log
import com.aheaditec.talsec_security.security.api.SuspiciousAppInfo
import com.aheaditec.talsec_security.security.api.ThreatListener
import com.aheaditec.talsec_security.security.api.ThreatListener.RaspExecutionState
import model.FreeRaspEvent
import providers.ContextProvider
import utils.processMalwareData

internal class ThreatHandler(
    private val onEvent: (FreeRaspEvent) -> Unit
) : ThreatListener.ThreatDetected, ThreatListener.DeviceState, RaspExecutionState() {
    override fun onRootDetected() = onEvent(FreeRaspEvent.PrivilegedAccess)

    override fun onDebuggerDetected() = onEvent(FreeRaspEvent.Debug)

    override fun onEmulatorDetected() = onEvent(FreeRaspEvent.Simulator)

    override fun onTamperDetected() = onEvent(FreeRaspEvent.AppIntegrity)

    override fun onUntrustedInstallationSourceDetected() = onEvent(FreeRaspEvent.UnofficialStore)

    override fun onHookDetected() = onEvent(FreeRaspEvent.Hooks)

    override fun onDeviceBindingDetected() = onEvent(FreeRaspEvent.DeviceBinding)

    override fun onObfuscationIssuesDetected() = onEvent(FreeRaspEvent.ObfuscationIssues)

    override fun onMalwareDetected(p0: List<SuspiciousAppInfo?>) {
        if(p0.isNullOrEmpty()){
            return
        }

        val cleanList = p0.filterNotNull()

        if(cleanList.isEmpty()){
            return
        }

        val context = ContextProvider.getApplicationContext()
        val commonApps = processMalwareData(context, cleanList)

        if (commonApps.isNotEmpty()){
            onEvent(FreeRaspEvent.Malware(commonApps))
        }
    }

    override fun onScreenshotDetected() = onEvent(FreeRaspEvent.Screenshot)

    override fun onScreenRecordingDetected() = onEvent(FreeRaspEvent.ScreenRecording)

    override fun onMultiInstanceDetected() = onEvent(FreeRaspEvent.MultiInstance)

    override fun onUnsecureWifiDetected() = onEvent(FreeRaspEvent.UnsecureWifi)

    override fun onTimeSpoofingDetected() = onEvent(FreeRaspEvent.TimeSpoofing)

    override fun onLocationSpoofingDetected() = onEvent(FreeRaspEvent.LocationSpoofing)

    override fun onUnlockedDeviceDetected() = onEvent(FreeRaspEvent.Passcode)

    override fun onHardwareBackedKeystoreNotAvailableDetected() = onEvent(FreeRaspEvent.SecureHardwareNotAvailable)

    override fun onDeveloperModeDetected() = onEvent(FreeRaspEvent.DevMode)

    override fun onADBEnabledDetected() = onEvent(FreeRaspEvent.AdbEnabled)

    override fun onSystemVPNDetected() = onEvent(FreeRaspEvent.SystemVPN)

    override fun onAllChecksFinished() = onEvent(FreeRaspEvent.AllChecksFinished)

}