package api

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import model.FreeRaspEvent
import model.config.freeraspConfig

actual object freeraspKMP {
    private val _threatEvents = MutableSharedFlow<FreeRaspEvent>()
    actual val threatEvents: SharedFlow<FreeRaspEvent> = _threatEvents.asSharedFlow()

    actual suspend fun start(config: freeraspConfig) {
        // TODO: Implement Talsec start for iOS
    }

    actual suspend fun addToWhiteList(packageName: String) {
        // TODO: Implement addToWhiteList for iOS
    }

    actual suspend fun storeExternalId(data: String) {
        // TODO: Implement storeExternalId for iOS
    }

    actual suspend fun getAppIcon(packageName: String): String {
        TODO("Not yet implemented")
    }

    actual suspend fun blockScreenCapture(enable: Boolean) {
        // TODO: Implement blockScreenCapture for iOS
    }

    actual suspend fun isScreenCaptureBlocked(): Boolean {
        TODO("Not yet implemented")
    }
}
