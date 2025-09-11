package api

import kotlinx.coroutines.flow.Flow
import model.config.freeraspConfig
import model.freeraspEvent
import threat.*

expect object freeraspKMP {
    suspend fun start(config: freeraspConfig)

    fun onThreatDetected(): Flow<freeraspEvent>

    suspend fun addToWhiteList(packageName: String)

    suspend fun storeExternalId(data: String)

    fun attachListener(callback: ThreatCallback)

    fun detachListener()

    suspend fun getAppIcon(packageName: String): String

    suspend fun blockScreenCapture(enable: Boolean)

    suspend fun isScreenCaptureBlocked(): Boolean
}