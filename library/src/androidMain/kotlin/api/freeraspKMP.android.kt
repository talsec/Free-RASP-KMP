package api


import com.aheaditec.talsec_security.security.api.Talsec
import com.aheaditec.talsec_security.security.api.ThreatListener

import android.util.Log

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import model.freeraspEvent
import model.config.freeraspConfig
import threat.Threat
import threat.ThreatCallback
import utils.AppIconUtil
import utils.toNativeConfig
import providers.ActivityProvider
import providers.ContextProvider
import handlers.ThreatHandler
import kotlinx.coroutines.cancel


actual object freeraspKMP {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val eventFlow = MutableSharedFlow<freeraspEvent>()
    private var listenerJob: Job? = null
    private var activateCallback: ThreatCallback? = null
    private val threatHandler = ThreatHandler { event ->
        scope.launch { eventFlow.emit(event) }
    }

    private val nativeListener = ThreatListener(threatHandler, threatHandler)

    actual suspend fun start(config: freeraspConfig) {
        val nativeConfig = withContext(Dispatchers.Default){
            config.toNativeConfig()
        }

        withContext(Dispatchers.Main){
            val context = ContextProvider.getApplicationContext()

            nativeListener.registerListener(context)

            Talsec.start(context, nativeConfig)
        }
    }

    actual fun onThreatDetected(): Flow<freeraspEvent> {
        return eventFlow.asSharedFlow()
    }

    private fun handleThreatEvent(threat: Threat){
        when(threat){
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

    actual fun attachListener(callback: ThreatCallback){
        detachListener()
        activateCallback = callback
        listenerJob = scope.launch {
            onThreatDetected().collect { event ->
                when(event){
                    is freeraspEvent.ThreatDetected -> handleThreatEvent(event.threat)
                    is freeraspEvent.MalwareDetected -> activateCallback?.onMalwareDetected(event.apps)
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

            Talsec.addToWhitelist(context, packageName)
        }
    }

    actual suspend fun storeExternalId(data: String) {
        withContext(Dispatchers.IO){
            val context = ContextProvider.getApplicationContext()

            Talsec.storeExternalId(context, data)
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
                Talsec.blockScreenCapture(activity, enable)
            } else {
                Log.w("freeraspKMP", "blockedScreenCapture called but no activity is in foreground.")
            }
        }
    }

    actual suspend fun isScreenCaptureBlocked(): Boolean {
        return Talsec.isScreenCaptureBlocked()
    }

    internal fun emitEvent(event: freeraspEvent){
        scope.launch {
            eventFlow.emit(event)
        }
    }

    internal fun cleanup() {
        scope.cancel()
    }
}

