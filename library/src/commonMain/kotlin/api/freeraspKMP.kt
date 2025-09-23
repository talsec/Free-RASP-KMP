package api

import kotlinx.coroutines.flow.SharedFlow
import model.config.freeraspConfig
import model.FreeRaspEvent

expect object freeraspKMP {
    val threatEvents: SharedFlow<FreeRaspEvent>

    suspend fun start(config: freeraspConfig)

    suspend fun addToWhiteList(packageName: String)

    suspend fun storeExternalId(data: String)

    suspend fun getAppIcon(packageName: String): String

    suspend fun blockScreenCapture(enable: Boolean)

    suspend fun isScreenCaptureBlocked(): Boolean
}