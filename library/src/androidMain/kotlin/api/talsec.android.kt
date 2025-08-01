package api

import android.app.Application
import kotlinx.coroutines.flow.Flow
import com.aheaditec.talsec_security.security.api.Talsec as NativeTalsec
import com.aheaditec.talsec_security.security.api.TalsecConfig as NativeTalsecConfig
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


fun TalsecConfig.toNativeConfig(): NativeTalsecConfig {
    val androidConfig = this.androidConfig ?: throw IllegalArgumentException("AndroidConfig is required on the Android platform but was null.")

    val builder = NativeTalsecConfig.Builder(
        androidConfig.packageName,
        androidConfig.signingCertHashes.toTypedArray()
    )

    builder.apply {
        watcherMail(this@toNativeConfig.watcherMail)
        prod(this@toNativeConfig.isProd)

        androidConfig.supportedStores?.let {
            supportedAlternativeStores(it.toTypedArray())
        }
    }
    return builder.build()

}

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

    fun initialize(application: Application){
        ContextProvider.initialize(application)

        application.registerActivityLifecycleCallbacks(ActivityProvider)
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
                            Threat.DEBUG -> activateCallback?.onDebug?.invoke()
                            Threat.PRIVILEGED_ACCESS -> activateCallback?.onPrivilegedAccess?.invoke()
                            Threat.SIMULATOR -> activateCallback?.onSimulator?.invoke()
                            Threat.APP_INTEGRITY -> activateCallback?.onAppIntegrity?.invoke()
                            Threat.UNOFFICIAL_STORE -> activateCallback?.onUnofficialStore?.invoke()
                            Threat.HOOKS -> activateCallback?.onHooks?.invoke()
                            Threat.DEVICE_BINDING -> activateCallback?.onDeviceBinding?.invoke()
                            Threat.OBFUSCATION_ISSUES -> activateCallback?.onObfuscationIssues?.invoke()
                            Threat.SCREENSHOT -> activateCallback?.onScreenshot?.invoke()
                            Threat.SCREEN_RECORDING -> activateCallback?.onScreenRecording?.invoke()
                            Threat.PASSCODE -> activateCallback?.onPasscode?.invoke()
                            Threat.SECURE_HARDWARE_NOT_AVAILABLE -> activateCallback?.onSecureHardwareNotAvailable?.invoke()
                            Threat.SYSTEM_VPN -> activateCallback?.onSystemVPN?.invoke()
                            Threat.DEV_MODE -> activateCallback?.onDevMode?.invoke()
                            Threat.ADB_ENABLED -> activateCallback?.onADBEnabled?.invoke()
                            Threat.MULTI_INSTANCE -> activateCallback?.onMultiInstance?.invoke()
                            Threat.DEVICE_ID -> activateCallback?.onDeviceID?.invoke()
                        }
                    }
                    is TalsecEvent.MalwareDetected -> {
                        activateCallback?.onMalwareDetected?.invoke(event.apps)
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

