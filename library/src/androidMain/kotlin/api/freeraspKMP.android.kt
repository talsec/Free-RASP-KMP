package com.freeraspkmp.api

import app.talsec.rasp.security.api.Talsec
import app.talsec.rasp.security.api.ThreatListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.freeraspkmp.model.FreeRaspEvent
import com.freeraspkmp.model.RaspExecutionStateEvent
import com.freeraspkmp.model.config.freeraspConfig
import com.freeraspkmp.android.utils.AppIconUtil
import com.freeraspkmp.android.utils.toNativeConfig
import com.freeraspkmp.android.providers.ContextProvider
import app.talsec.rasp.security.api.ExternalIdResult
import app.talsec.rasp.security.api.TalsecMode
import com.freeraspkmp.android.handlers.ThreatDetectedHandler
import com.freeraspkmp.android.handlers.DeviceStateHandler
import com.freeraspkmp.android.handlers.RaspExecutionStateHandler
import kotlinx.coroutines.cancel
import com.freeraspkmp.model.exception.FreeraspKMPException
import com.freeraspkmp.android.providers.ActivityProvider

import com.freeraspkmp.android.utils.verifyConfig

actual object FreeraspKMP {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val eventCache = mutableSetOf<FreeRaspEvent>()
    private val cacheLock = Any()
    private val _threatEvents = MutableSharedFlow<FreeRaspEvent>()
    actual val threatEvents: SharedFlow<FreeRaspEvent> = _threatEvents.asSharedFlow()
    private val _raspExecutionStateEvents = MutableSharedFlow<RaspExecutionStateEvent>(replay = 1)
    actual val raspExecutionStateEvents: SharedFlow<RaspExecutionStateEvent> = _raspExecutionStateEvents.asSharedFlow()

    init {
        scope.launch {
            _threatEvents.subscriptionCount.collect { count ->
                if (count > 0) {
                    val eventsToEmit: List<FreeRaspEvent>
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

    private val threatDetectedHandler = ThreatDetectedHandler { event -> emitEvent(event) }
    private val deviceStateHandler = DeviceStateHandler { event -> emitEvent(event) }
    private val raspExecutionStateHandler = RaspExecutionStateHandler {
        scope.launch { _raspExecutionStateEvents.emit(RaspExecutionStateEvent.AllChecksFinished) }
    }

    private val nativeListener = ThreatListener(threatDetectedHandler, deviceStateHandler, raspExecutionStateHandler)

    actual suspend fun start(config: freeraspConfig) {
        verifyConfig(config)
        val nativeConfig = withContext(Dispatchers.Default){
            config.toNativeConfig()
        }

        withContext(Dispatchers.Main){
            val context = ContextProvider.getApplicationContext()

            nativeListener.registerListener(context)

            Talsec.start(context, nativeConfig, TalsecMode.BACKGROUND)
        }
    }

    actual suspend fun removeExternalId() {
        withContext(Dispatchers.IO) {
            val context = ContextProvider.getApplicationContext()
            Talsec.removeExternalId(context)
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

            when (val result = Talsec.storeExternalId(context, data)) {
                is ExternalIdResult.Success -> Unit
                is ExternalIdResult.Error -> throw com.freeraspkmp.model.exception.FreeraspKMPException(result.errorMsg)
            }
        }
    }

    actual suspend fun getAppIcon(packageName: String): String {
        return withContext(Dispatchers.IO) {
            val context = ContextProvider.getApplicationContext()

            AppIconUtil.getAppIconAsBase64String(context, packageName)
                ?: throw FreeraspKMPException("Could not get or encode app icon for package: $packageName")
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

    internal fun emitEvent(event: FreeRaspEvent){
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
