@file:OptIn(ExperimentalForeignApi::class)

package com.freeraspkmp.api

import com.aheaditec.talsec.interop.TalsecApiBridge
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.freeraspkmp.model.config.freeraspConfig
import com.freeraspkmp.model.FreeRaspEvent
import com.freeraspkmp.model.RaspExecutionStateEvent
import com.freeraspkmp.ios.utils.mapStringToFreeraspEvent
import com.freeraspkmp.ios.utils.toNativeConfig
import kotlin.coroutines.resume

import com.freeraspkmp.ios.utils.verifyConfig

actual object FreeraspKMP {
    private val NativeTalsec = TalsecApiBridge.shared()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val eventCache = mutableSetOf<FreeRaspEvent>()
    private val cacheMutex = Mutex()
    private val _threatEvents = MutableSharedFlow<FreeRaspEvent>()
    actual val threatEvents: SharedFlow<FreeRaspEvent> = _threatEvents.asSharedFlow()
    private val _raspExecutionStateEvents = MutableSharedFlow<RaspExecutionStateEvent>(replay = 1)
    actual val raspExecutionStateEvents: SharedFlow<RaspExecutionStateEvent> = _raspExecutionStateEvents.asSharedFlow()

    init {
        NativeTalsec.setThreatDetectedCallback { threatString ->
            if (threatString == "allChecksFinished") {
                scope.launch { _raspExecutionStateEvents.emit(RaspExecutionStateEvent.AllChecksFinished) }
            } else {
                mapStringToFreeraspEvent(threatString)?.let { event ->
                    emitEvent(event)
                }
            }
        }

        scope.launch {
            _threatEvents.subscriptionCount.collect { count ->
                if (count > 0) {
                    cacheMutex.withLock {
                        val eventsToEmit = eventCache.toList()
                        eventCache.clear()
                        eventsToEmit.forEach { event ->
                            _threatEvents.emit(event)
                        }
                    }
                }
            }
        }
    }

    actual suspend fun start(config: freeraspConfig) {
        verifyConfig(config)
        val iosNativeConfig = config.toNativeConfig()
        NativeTalsec.startWithAppBundleIds(
            appBundleIds = iosNativeConfig.appBundleIds,
            appTeamId = iosNativeConfig.appTeamId,
            watcherMailAddress = iosNativeConfig.watcherMail,
            isProd = iosNativeConfig.isProd
        )
    }

    private fun emitEvent(event: FreeRaspEvent) {
        scope.launch {
            if (_threatEvents.subscriptionCount.value == 0) {
                cacheMutex.withLock {
                    eventCache.add(event)
                }
            } else {
                _threatEvents.emit(event)
            }
        }
    }

    actual suspend fun storeExternalId(data: String) {
        NativeTalsec.storeExternalId(data)
    }

    actual suspend fun removeExternalId() {
        NativeTalsec.removeExternalId()
    }

    actual suspend fun addToWhiteList(packageName: String) {
        //throw UnsupportedOperationException("freeraspKMP: addToWhiteList is not supported on iOS.")
        println("freeraspKMP: addToWhiteList is not supported on iOS.")
    }

    actual suspend fun getAppIcon(packageName: String): String {
        //throw UnsupportedOperationException("freeraspKMP: getAppIcon is not supported on iOS.")
        println("freeraspKMP: getAppIcon is not supported on iOS.")
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
