package com.freeraspkmp.android.handlers

import app.talsec.rasp.security.api.SuspiciousAppInfo
import app.talsec.rasp.security.api.ThreatListener
import com.freeraspkmp.model.FreeRaspEvent
import com.freeraspkmp.android.providers.ContextProvider
import com.freeraspkmp.android.utils.processMalwareData

internal class ThreatDetectedHandler(
    private val onEvent: (FreeRaspEvent) -> Unit
) : ThreatListener.ThreatDetected() {

    override fun onPrivilegedAccess() = onEvent(FreeRaspEvent.PrivilegedAccess)

    override fun onDebug() = onEvent(FreeRaspEvent.Debug)

    override fun onSimulator() = onEvent(FreeRaspEvent.Simulator)

    override fun onAppIntegrity() = onEvent(FreeRaspEvent.AppIntegrity)

    override fun onUnofficialStore() = onEvent(FreeRaspEvent.UnofficialStore)

    override fun onHooks() = onEvent(FreeRaspEvent.Hooks)

    override fun onDeviceBinding() = onEvent(FreeRaspEvent.DeviceBinding)

    override fun onObfuscationIssues() = onEvent(FreeRaspEvent.ObfuscationIssues)

    override fun onMalware(suspiciousApps: List<SuspiciousAppInfo>) {
        if (suspiciousApps.isEmpty()) return

        val context = ContextProvider.getApplicationContext()
        val commonApps = processMalwareData(context, suspiciousApps)

        if (commonApps.isNotEmpty()) {
            onEvent(FreeRaspEvent.Malware(commonApps))
        }
    }

    override fun onScreenshot() = onEvent(FreeRaspEvent.Screenshot)

    override fun onScreenRecording() = onEvent(FreeRaspEvent.ScreenRecording)

    override fun onMultiInstance() = onEvent(FreeRaspEvent.MultiInstance)

    override fun onUnsecureWifi() = onEvent(FreeRaspEvent.UnsecureWifi)

    override fun onTimeSpoofing() = onEvent(FreeRaspEvent.TimeSpoofing)

    override fun onLocationSpoofing() = onEvent(FreeRaspEvent.LocationSpoofing)

    override fun onAutomation() = onEvent(FreeRaspEvent.Automation)

    override fun onBootloader() = onEvent(FreeRaspEvent.Bootloader)
}

internal class DeviceStateHandler(
    private val onEvent: (FreeRaspEvent) -> Unit
) : ThreatListener.DeviceState() {

    override fun onPasscode() = onEvent(FreeRaspEvent.Passcode)

    override fun onSecureHardwareNotAvailable() = onEvent(FreeRaspEvent.SecureHardwareNotAvailable)

    override fun onDevMode() = onEvent(FreeRaspEvent.DevMode)

    override fun onAdbEnabled() = onEvent(FreeRaspEvent.AdbEnabled)

    override fun onSystemVpn() = onEvent(FreeRaspEvent.SystemVPN)
}

internal class RaspExecutionStateHandler(
    private val callback: () -> Unit
) : ThreatListener.RaspExecutionState() {

    override fun onAllChecksFinished() = callback()
}
