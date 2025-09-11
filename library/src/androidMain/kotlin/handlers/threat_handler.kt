package handlers

import com.aheaditec.talsec_security.security.api.SuspiciousAppInfo
import com.aheaditec.talsec_security.security.api.ThreatListener
import model.freeraspEvent
import providers.ContextProvider
import threat.Threat
import utils.processMalwareData

internal class ThreatHandler (
    private val onEvent: (freeraspEvent) -> Unit
): ThreatListener.ThreatDetected, ThreatListener.DeviceState {
    override fun onRootDetected() = onEvent(freeraspEvent.ThreatDetected(Threat.PRIVILEGED_ACCESS))

    override fun onDebuggerDetected()  = onEvent(freeraspEvent.ThreatDetected(Threat.DEBUG))

    override fun onEmulatorDetected() = onEvent(freeraspEvent.ThreatDetected(Threat.SIMULATOR))

    override fun onTamperDetected() = onEvent(freeraspEvent.ThreatDetected(Threat.APP_INTEGRITY))

    override fun onUntrustedInstallationSourceDetected() = onEvent(freeraspEvent.ThreatDetected(Threat.UNOFFICIAL_STORE))

    override fun onHookDetected() = onEvent(freeraspEvent.ThreatDetected(Threat.HOOKS))

    override fun onDeviceBindingDetected() = onEvent(freeraspEvent.ThreatDetected(Threat.DEVICE_BINDING))

    override fun onObfuscationIssuesDetected() = onEvent(freeraspEvent.ThreatDetected(Threat.OBFUSCATION_ISSUES))

    override fun onMalwareDetected(nativeSuspiciousAppInfo: List<SuspiciousAppInfo>?) {

        if(nativeSuspiciousAppInfo.isNullOrEmpty()) {
            return
        }

        val context = ContextProvider.getApplicationContext()
        val commonApps = processMalwareData(context, nativeSuspiciousAppInfo)

        if(commonApps.isNotEmpty()) {
            onEvent(freeraspEvent.MalwareDetected(commonApps))
        }
    }

    override fun onScreenshotDetected() = onEvent(freeraspEvent.ThreatDetected(Threat.SCREENSHOT))

    override fun onScreenRecordingDetected() = onEvent(freeraspEvent.ThreatDetected(Threat.SCREEN_RECORDING))

    override fun onMultiInstanceDetected() = onEvent(freeraspEvent.ThreatDetected(Threat.MULTI_INSTANCE))

    override fun onUnlockedDeviceDetected() = onEvent(freeraspEvent.ThreatDetected(Threat.PASSCODE))

    override fun onHardwareBackedKeystoreNotAvailableDetected() = onEvent(freeraspEvent.ThreatDetected(Threat.SECURE_HARDWARE_NOT_AVAILABLE))

    override fun onDeveloperModeDetected() = onEvent(freeraspEvent.ThreatDetected(Threat.DEV_MODE))

    override fun onADBEnabledDetected() = onEvent(freeraspEvent.ThreatDetected(Threat.ADB_ENABLED))

    override fun onSystemVPNDetected() = onEvent(freeraspEvent.ThreatDetected(Threat.SYSTEM_VPN))

}