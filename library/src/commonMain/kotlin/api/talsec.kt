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


}