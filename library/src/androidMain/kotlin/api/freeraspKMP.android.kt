package api

import com.aheaditec.talsec_security.security.api.Talsec
import com.aheaditec.talsec_security.security.api.ThreatListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.freeraspEvent
import model.config.freeraspConfig
import utils.AppIconUtil
import utils.toNativeConfig
import providers.ContextProvider
import handlers.ThreatHandler
import kotlinx.coroutines.cancel
import model.exception.FreeRASPException
import providers.ActivityProvider

actual object freeraspKMP {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val eventCache = mutableListOf<freeraspEvent>()
    private val cacheLock = Any()
    private val _threatEvents = MutableSharedFlow<freeraspEvent>()
    actual val threatEvents: SharedFlow<freeraspEvent> = _threatEvents.asSharedFlow()

    init {
        scope.launch {
            _threatEvents.subscriptionCount.collect { count ->
                if (count > 0) {
                    val eventsToEmit: List<freeraspEvent>
                    synchronized(cacheLock) {
                        if (eventCache.isNotEmpty()) {
                            eventsToEmit = eventCache.toList()
                            eventCache.clear()
                        } else {
                            eventsToEmit = emptyList()
                        }
                    }
                    eventsToEmit.forEach {
                        _threatEvents.emit(it)
                    }
                }
            }
        }
    }

    private val threatHandler = ThreatHandler { event ->
        emitEvent(event)
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
                ?: throw FreeRASPException("Could not get or encode app icon for package: $packageName")
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
        if (_threatEvents.subscriptionCount.value == 0) {
            synchronized(cacheLock) {
                eventCache.add(event)
            }
        } else {
            scope.launch {
                _threatEvents.emit(event)
            }
        }
    }

    internal fun cleanup() {
        val context = ContextProvider.getApplicationContext()
        nativeListener.unregisterListener(context)
        scope.cancel()
    }
}
