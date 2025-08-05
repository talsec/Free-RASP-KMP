package api

import kotlinx.coroutines.flow.Flow
import com.aheaditec.talsec_security.security.api.Talsec as NativeTalsec
import android.util.Log
import com.aheaditec.talsec_security.security.api.ThreatListener
import handlers.ThreatHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.TalsecEvent
import model.config.TalsecConfig
import providers.ActivityProvider
import threat.Threat
import threat.ThreatCallback
import utils.AppIconUtil
import providers.ContextProvider
import utils.toNativeConfig

actual object Talsec {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val eventFlow = MutableSharedFlow<TalsecEvent>()
    private var listenerJob: Job? = null
    private var activateCallback: ThreatCallback? = null
    private val threatHandler = ThreatHandler { event ->
        scope.launch { eventFlow.emit(event) }
    }

    private val nativeListener = ThreatListener(threatHandler, threatHandler)

    actual suspend fun start(config: TalsecConfig) {
        val context = ContextProvider.getApplicationContext()

        val nativeConfig = config.toNativeConfig()

        nativeListener.registerListener(context)

        NativeTalsec.start(context, nativeConfig)
    }

    actual fun onThreatDetected(): Flow<TalsecEvent> {
        return eventFlow.asSharedFlow()

    }

    actual fun attachListener(callback: ThreatCallback){
        detachListener()
        activateCallback = callback
        listenerJob = scope.launch {
            onThreatDetected().collect { event ->
                when(event){
                    is TalsecEvent.ThreatDetected -> {
                        when(event.threat) {
                            Threat.DEBUG -> activateCallback?.onDebug()
                            Threat.PRIVILEGED_ACCESS -> activateCallback?.onPrivilegedAccess()
                            Threat.SIMULATOR -> activateCallback?.onSimulator()
                            Threat.APP_INTEGRITY -> activateCallback?.onAppIntegrity()
                            Threat.UNOFFICIAL_STORE -> activateCallback?.onUnofficialStore()
                            Threat.HOOKS -> activateCallback?.onHooks()
                            Threat.DEVICE_BINDING -> activateCallback?.onDeviceBinding()
                            Threat.OBFUSCATION_ISSUES -> activateCallback?.onObfuscationIssues()
                            Threat.SCREENSHOT -> activateCallback?.onScreenshot()
                            Threat.SCREEN_RECORDING -> activateCallback?.onScreenRecording()
                            Threat.PASSCODE -> activateCallback?.onPasscode()
                            Threat.SECURE_HARDWARE_NOT_AVAILABLE -> activateCallback?.onSecureHardwareNotAvailable()
                            Threat.SYSTEM_VPN -> activateCallback?.onSystemVPN()
                            Threat.DEV_MODE -> activateCallback?.onDevMode()
                            Threat.ADB_ENABLED -> activateCallback?.onADBEnabled()
                            Threat.MULTI_INSTANCE -> activateCallback?.onMultiInstance()
                            Threat.DEVICE_ID -> activateCallback?.onDeviceID()
                        }
                    }
                    is TalsecEvent.MalwareDetected -> {
                        activateCallback?.onMalwareDetected(event.apps)
                    }
                }
            }
        }
    }

    actual fun detachListener() {
        listenerJob?.cancel()
        listenerJob = null
        activateCallback = null
    }

    actual suspend fun addToWhiteList(packageName: String) {
        withContext(Dispatchers.IO){
            val context = ContextProvider.getApplicationContext()

            NativeTalsec.addToWhitelist(context, packageName)
        }
    }

    actual suspend fun storeExternalId(data: String) {
        withContext(Dispatchers.IO){
            val context = ContextProvider.getApplicationContext()

            NativeTalsec.storeExternalId(context, data)
        }
    }

    actual suspend fun getAppIcon(packageName: String): String {
        return withContext(Dispatchers.IO) {
            val context = ContextProvider.getApplicationContext()

            AppIconUtil.getAppIconAsBase64String(context, packageName)
                ?: throw Exception("Could not get or encode app icon for package: $packageName")
        }
    }

    actual suspend fun blockScreenCapture(enable: Boolean) {
        withContext(Dispatchers.Main){
            val activity = ActivityProvider.getCurrentActivity()

            if(activity != null){
                NativeTalsec.blockScreenCapture(activity, enable)
            } else {
                Log.w("Talsec", "blockedScreenCapture called but no activity is in foreground.")
            }
        }
    }

    actual suspend fun isScreenCaptureBlocked(): Boolean {
        return NativeTalsec.isScreenCaptureBlocked()
    }
}

