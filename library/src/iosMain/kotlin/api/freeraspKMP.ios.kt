@file:OptIn(ExperimentalForeignApi::class)

package api

import com.freeraspkmp.interop.TalsecApiBridge
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
import model.config.freeraspConfig
import model.freeraspEvent
import utils.mapStringToFreeraspEvent
import utils.toNativeConfig
import kotlin.coroutines.resume


actual object freeraspKMP {
    private val NativeTalsec = TalsecApiBridge.shared()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val _threatEvents = MutableSharedFlow<freeraspEvent>()
    actual val threatEvents: SharedFlow<freeraspEvent> = _threatEvents.asSharedFlow()


    init {
        NativeTalsec.setThreatDetectedCallback { threatString ->
            mapStringToFreeraspEvent(threatString)?.let {
                scope.launch {
                    _threatEvents.emit(it)
                }
            }
        }
    }

    actual suspend fun start(config: freeraspConfig) {
        val iosNativeConfig = config.toNativeConfig()

        // Note: The positional arguments (_1, _2, _3) are due to the Objective-C bridge generation.
        // This should be fixed in the native Swift library by using named arguments in the @objc attribute.
        NativeTalsec.start(
            appBundleIds = iosNativeConfig.appBundleIds,
            _1 = iosNativeConfig.appTeamId,
            _2 = iosNativeConfig.watcherMail,
            _3 = iosNativeConfig.isProd
        )
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