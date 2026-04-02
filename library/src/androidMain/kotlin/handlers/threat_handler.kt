package com.freeraspkmp.android.handlers

import com.aheaditec.talsec_security.security.api.SuspiciousAppInfo
import com.aheaditec.talsec_security.security.api.ThreatListener
import com.freeraspkmp.model.FreeRaspEvent
import com.freeraspkmp.android.providers.ContextProvider
import com.freeraspkmp.android.utils.processMalwareData

internal class ThreatDetectedHandler(
    private val onEvent: (FreeRaspEvent) -> Unit
) : ThreatListener.ThreatDetected() {

    override fun onRootDetected() = onEvent(FreeRaspEvent.PrivilegedAccess)

    override fun onDebuggerDetected() = onEvent(FreeRaspEvent.Debug)

    override fun onEmulatorDetected() = onEvent(FreeRaspEvent.Simulator)

    override fun onTamperDetected() = onEvent(FreeRaspEvent.AppIntegrity)

    override fun onUntrustedInstallationSourceDetected() = onEvent(FreeRaspEvent.UnofficialStore)

    override fun onHookDetected() = onEvent(FreeRaspEvent.Hooks)

    override fun onDeviceBindingDetected() = onEvent(FreeRaspEvent.DeviceBinding)

    override fun onObfuscationIssuesDetected() = onEvent(FreeRaspEvent.ObfuscationIssues)

    override fun onMalwareDetected(suspiciousApps: List<SuspiciousAppInfo>) {
        if (suspiciousApps.isEmpty()) return

        val context = ContextProvider.getApplicationContext()
        val commonApps = processMalwareData(context, suspiciousApps)

        if (commonApps.isNotEmpty()) {
            onEvent(FreeRaspEvent.Malware(commonApps))
        }
    }

    override fun onScreenshotDetected() = onEvent(FreeRaspEvent.Screenshot)

    override fun onScreenRecordingDetected() = onEvent(FreeRaspEvent.ScreenRecording)

    override fun onMultiInstanceDetected() = onEvent(FreeRaspEvent.MultiInstance)

    override fun onUnsecureWifiDetected() = onEvent(FreeRaspEvent.UnsecureWifi)

    override fun onTimeSpoofingDetected() = onEvent(FreeRaspEvent.TimeSpoofing)

    override fun onLocationSpoofingDetected() = onEvent(FreeRaspEvent.LocationSpoofing)

    override fun onAutomationDetected() = onEvent(FreeRaspEvent.Automation)
}

internal class DeviceStateHandler(
    private val onEvent: (FreeRaspEvent) -> Unit
) : ThreatListener.DeviceState() {

    override fun onUnlockedDeviceDetected() = onEvent(FreeRaspEvent.Passcode)

    override fun onHardwareBackedKeystoreNotAvailableDetected() = onEvent(FreeRaspEvent.SecureHardwareNotAvailable)

    override fun onDeveloperModeDetected() = onEvent(FreeRaspEvent.DevMode)

    override fun onADBEnabledDetected() = onEvent(FreeRaspEvent.AdbEnabled)

    override fun onSystemVPNDetected() = onEvent(FreeRaspEvent.SystemVPN)
}

internal class RaspExecutionStateHandler(
    private val callback: () -> Unit
) : ThreatListener.RaspExecutionState() {

    override fun onAllChecksFinished() = callback()
}
