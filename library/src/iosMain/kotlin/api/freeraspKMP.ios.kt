@file:OptIn(ExperimentalForeignApi::class)

package api

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
import model.config.freeraspConfig
import model.FreeRASPEvent
import utils.mapStringToFreeraspEvent
import utils.toNativeConfig
import kotlin.coroutines.resume

actual object freeraspKMP {
    private val NativeTalsec = TalsecApiBridge.shared()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    private val eventCache = mutableListOf<FreeRASPEvent>()
    private val cacheMutex = Mutex()
    private val _threatEvents = MutableSharedFlow<FreeRASPEvent>()
    actual val threatEvents: SharedFlow<FreeRASPEvent> = _threatEvents.asSharedFlow()

    init {
        // Set up the native callback
        NativeTalsec.setThreatDetectedCallback { threatString ->
            mapStringToFreeraspEvent(threatString)?.let { event ->
                emitEvent(event)
            }
        }

        // Start collecting subscription counts to drain the cache
        scope.launch {
            _threatEvents.subscriptionCount.collect { count ->
                if (count > 0) {
                    cacheMutex.withLock {
                        val eventsToEmit = eventCache.toList()
                        eventCache.clear()
                        eventsToEmit.forEach { event ->
                            scope.launch { _threatEvents.emit(event) }
                        }
                    }
                }
            }
        }
    }

    actual suspend fun start(config: freeraspConfig) {
        val iosNativeConfig = config.toNativeConfig()
        NativeTalsec.start(
            appBundleIds = iosNativeConfig.appBundleIds,
            _1 = iosNativeConfig.appTeamId,
            _2 = iosNativeConfig.watcherMail,
            _3 = iosNativeConfig.isProd
        )
    }

    private fun emitEvent(event: FreeRASPEvent) {
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

    actual suspend fun addToWhiteList(packageName: String) {
        throw UnsupportedOperationException("freeraspKMP: addToWhiteList is not supported on iOS.")
    }

    actual suspend fun getAppIcon(packageName: String): String {
        throw UnsupportedOperationException("freeraspKMP: getAppIcon is not supported on iOS.")
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