package handlers

import android.util.Log
import com.aheaditec.talsec_security.security.api.SuspiciousAppInfo as NativeSuspiciousAppInfo
import com.aheaditec.talsec_security.security.api.ThreatListener
import model.PackageInfo
import model.SuspiciousAppInfo
import model.TalsecEvent
import providers.ContextProvider
import threat.Threat
import utils.AppIconUtil
import utils.processMalwareData

internal class ThreatHandler (
    private val onEvent: (TalsecEvent) -> Unit
): ThreatListener.ThreatDetected, ThreatListener.DeviceState {
    override fun onRootDetected() = onEvent(TalsecEvent.ThreatDetected(Threat.PRIVILEGED_ACCESS))

    override fun onDebuggerDetected()  = onEvent(TalsecEvent.ThreatDetected(Threat.DEBUG))

    override fun onEmulatorDetected() = onEvent(TalsecEvent.ThreatDetected(Threat.SIMULATOR))

    override fun onTamperDetected() = onEvent(TalsecEvent.ThreatDetected(Threat.APP_INTEGRITY))

    override fun onUntrustedInstallationSourceDetected() = onEvent(TalsecEvent.ThreatDetected(Threat.UNOFFICIAL_STORE))

    override fun onHookDetected() = onEvent(TalsecEvent.ThreatDetected(Threat.HOOKS))

    override fun onDeviceBindingDetected() = onEvent(TalsecEvent.ThreatDetected(Threat.DEVICE_BINDING))

    override fun onObfuscationIssuesDetected() = onEvent(TalsecEvent.ThreatDetected(Threat.OBFUSCATION_ISSUES))

    override fun onMalwareDetected(nativeSuspiciousAppInfo: List<NativeSuspiciousAppInfo>?) {

        val nonNullNativeList = nativeSuspiciousAppInfo ?: emptyList()

        if(nonNullNativeList.isEmpty()) return

        val context = ContextProvider.getApplicationContext()
        val commonApps = processMalwareData(context, nonNullNativeList)

        if(commonApps.isNotEmpty()) {
            onEvent(TalsecEvent.MalwareDetected(commonApps))
        }
    }

    override fun onScreenshotDetected() = onEvent(TalsecEvent.ThreatDetected(Threat.SCREENSHOT))

    override fun onScreenRecordingDetected() = onEvent(TalsecEvent.ThreatDetected(Threat.SCREEN_RECORDING))

    override fun onMultiInstanceDetected() = onEvent(TalsecEvent.ThreatDetected(Threat.MULTI_INSTANCE))

    override fun onUnlockedDeviceDetected() = onEvent(TalsecEvent.ThreatDetected(Threat.PASSCODE))

    override fun onHardwareBackedKeystoreNotAvailableDetected() = onEvent(TalsecEvent.ThreatDetected(Threat.SECURE_HARDWARE_NOT_AVAILABLE))

    override fun onDeveloperModeDetected() = onEvent(TalsecEvent.ThreatDetected(Threat.DEV_MODE))

    override fun onADBEnabledDetected() = onEvent(TalsecEvent.ThreatDetected(Threat.ADB_ENABLED))

    override fun onSystemVPNDetected() = onEvent(TalsecEvent.ThreatDetected(Threat.SYSTEM_VPN))

}