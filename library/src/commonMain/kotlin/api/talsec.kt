package api

import kotlinx.coroutines.flow.Flow
import model.TalsecConfig
import threat.*

expect object Talsec {
    suspend fun start(config: TalsecConfig)

    fun onThreatDetected(): Flow<Threat>

    suspend fun addToWhiteList(packageName: String)

    suspend fun storeExternalId(data: String)

    fun attachListener(callback: ThreatCallback)

    fun detachListener()

    suspend fun getAppIcon(packageName: String): String

    suspend fun blockScreenCapture(enable: Boolean)

    suspend fun isScreenCaptureBlocked(): Boolean

    //TODO: možno aj tieto zvyšné metódy

    //onInvalidCallback

    //addListener

    //removeListeners



}