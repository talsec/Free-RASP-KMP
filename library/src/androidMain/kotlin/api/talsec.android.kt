package api

import kotlinx.coroutines.flow.Flow
import com.aheaditec.talsec_security.security.api.Talsec as NativeTalsec
import com.aheaditec.talsec_security.security.api.TalsecConfig as NativeTalsecConfig
import android.content.Context
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
import model.TalsecConfig
import threat.Threat
import threat.ThreatCallback
import utils.ContextProvider



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
    private val eventFlow = MutableSharedFlow<Threat>()
    private var listenerJob: Job? = null
    private var activateCallback: ThreatCallback? = null
    private val threatHandler = ThreatHandler { threat ->
        scope.launch { eventFlow.emit(threat) }
    }

    private val nativeListener = ThreatListener(threatHandler, threatHandler)

    actual suspend fun start(config: TalsecConfig) {
        val context = ContextProvider.getApplicationContext()

        val nativeConfig = config.toNativeConfig()

        nativeListener.registerListener(context)

        NativeTalsec.start(context, nativeConfig)
    }

    fun initialize(context: Context){
        ContextProvider.initialize(context)
    }

    actual fun onThreatDetected(): Flow<Threat> {
        return eventFlow.asSharedFlow()

    }

    actual fun attachListener(callback: ThreatCallback): Unit {
        detachListener()
        activateCallback = callback
        listenerJob = scope.launch {
            onThreatDetected().collect { threat ->
                when(threat) {
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
        }
    }
    actual fun detachListener(): Unit {
        listenerJob?.cancel()
        listenerJob = null
        activateCallback = null
    }

    actual suspend fun addToWhiteList(packageName: String): Unit {
        withContext(Dispatchers.IO){
            val context = ContextProvider.getApplicationContext()

            NativeTalsec.addToWhitelist(context, packageName)
        }
    }

    actual suspend fun storeExternalId(data: String): Unit {
        withContext(Dispatchers.IO){
            val context = ContextProvider.getApplicationContext()

            NativeTalsec.storeExternalId(context, data)
        }
    }


}

