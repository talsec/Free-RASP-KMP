package handlers

import com.aheaditec.talsec_security.security.api.SuspiciousAppInfo
import com.aheaditec.talsec_security.security.api.ThreatListener
import threat.Threat

internal class ThreatHandler (
    private val onThreat: (Threat) -> Unit
): ThreatListener.ThreatDetected, ThreatListener.DeviceState {
    override fun onRootDetected() = onThreat(Threat.PRIVILEGED_ACCESS)

    override fun onDebuggerDetected()  = onThreat(Threat.DEBUG)

    override fun onEmulatorDetected() = onThreat(Threat.SIMULATOR)

    override fun onTamperDetected() = onThreat(Threat.APP_INTEGRITY)

    override fun onUntrustedInstallationSourceDetected() = onThreat(Threat.UNOFFICIAL_STORE)

    override fun onHookDetected() = onThreat(Threat.HOOKS)

    override fun onDeviceBindingDetected() = onThreat(Threat.DEVICE_BINDING)

    override fun onObfuscationIssuesDetected() = onThreat(Threat.OBFUSCATION_ISSUES)

    override fun onMalwareDetected(p0: List<SuspiciousAppInfo?>?) = onThreat(Threat.SYSTEM_VPN)

    override fun onScreenshotDetected() = onThreat(Threat.SCREENSHOT)

    override fun onScreenRecordingDetected() = onThreat(Threat.SCREEN_RECORDING)

    override fun onMultiInstanceDetected() = onThreat(Threat.MULTI_INSTANCE)

    override fun onUnlockedDeviceDetected() = onThreat(Threat.PASSCODE)

    override fun onHardwareBackedKeystoreNotAvailableDetected() = onThreat(Threat.SECURE_HARDWARE_NOT_AVAILABLE)

    override fun onDeveloperModeDetected() = onThreat(Threat.DEV_MODE)

    override fun onADBEnabledDetected() = onThreat(Threat.ADB_ENABLED)

    override fun onSystemVPNDetected() = onThreat(Threat.SYSTEM_VPN)

}