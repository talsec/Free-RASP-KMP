@file:OptIn(ExperimentalForeignApi::class)

package api

import com.aheaditec.talsec.interop.TalsecApiBridge
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

import model.TalsecEvent
import model.config.TalsecConfig
import threat.Threat
import threat.ThreatCallback
import utils.mapStringToThreat
import utils.toNativeConfig


actual object Talsec {
    private val NativeTalsec = TalsecApiBridge.shared()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val eventFlow = MutableSharedFlow<TalsecEvent>()

    private var listenerJob: Job? = null
    private var activeCallback: ThreatCallback? = null

    init {
        NativeTalsec.setThreatDetectedCallback { threatString ->
            val threat = mapStringToThreat(threatString)
            if (threat != null) {
                scope.launch {
                    eventFlow.emit(TalsecEvent.ThreatDetected(threat))
                }
            }
        }
    }

    actual suspend fun start(config: TalsecConfig) {
        val iosNativeConfig = config.toNativeConfig()

        NativeTalsec.start(
            appBundleIds = iosNativeConfig.appBundleIds,
            _1 = iosNativeConfig.appTeamId,
            _2 = iosNativeConfig.watcherMail,
            _3 = iosNativeConfig.isProd
        )
    }

    actual fun onThreatDetected(): Flow<TalsecEvent> {
        return eventFlow.asSharedFlow()
    }

    actual fun attachListener(callback: ThreatCallback) {
        detachListener()
        activeCallback = callback
        listenerJob = scope.launch {
            onThreatDetected().collect { event ->
                if(event is TalsecEvent.ThreatDetected) {
                    when(event.threat) {
                        Threat.PRIVILEGED_ACCESS -> activeCallback?.onPrivilegedAccess()
                        Threat.DEBUG -> activeCallback?.onDebug()
                        Threat.SIMULATOR -> activeCallback?.onSimulator()
                        Threat.APP_INTEGRITY -> activeCallback?.onAppIntegrity()
                        Threat.UNOFFICIAL_STORE -> activeCallback?.onUnofficialStore()
                        Threat.HOOKS -> activeCallback?.onUnofficialStore()
                        Threat.DEVICE_BINDING -> activeCallback?.onDeviceBinding()
                        Threat.PASSCODE -> activeCallback?.onPasscode()
                        Threat.SECURE_HARDWARE_NOT_AVAILABLE -> activeCallback?.onSecureHardwareNotAvailable()
                        Threat.SYSTEM_VPN -> activeCallback?.onSystemVPN()
                        Threat.DEVICE_ID -> activeCallback?.onDeviceID()
                        Threat.SCREENSHOT -> activeCallback?.onScreenshot()
                        Threat.SCREEN_RECORDING -> activeCallback?.onScreenRecording()
                        else -> Unit
                    }
                }
            }
        }
    }

    actual fun detachListener() {
        listenerJob?.cancel()
        listenerJob = null
        activeCallback = null
    }

    actual suspend fun storeExternalId(data: String) {
        NativeTalsec.storeExternalId(data)
    }

    actual suspend fun addToWhiteList(packageName: String) {
        println("freeRASP-KMP: addToWhiteList is not supported on iOS.")
    }

    actual suspend fun getAppIcon(packageName: String): String {
        println("freeRASP-KMP: getAppIcon is not supported on iOS.")
        return ""
    }

    actual suspend fun blockScreenCapture(enable: Boolean) {
        withContext(Dispatchers.Main){
            NativeTalsec.blockScreenCapture(enable)
        }
    }

    actual suspend fun isScreenCaptureBlocked(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            NativeTalsec.isScreenCaptureBlocked { isBlocked ->
                continuation.resume(isBlocked)
            }
        }
    }
}