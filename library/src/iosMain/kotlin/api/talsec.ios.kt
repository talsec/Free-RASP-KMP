package api

actual object Talsec {
    actual suspend fun start(config: model.config.TalsecConfig) {
    }

    actual fun onThreatDetected(): kotlinx.coroutines.flow.Flow<model.TalsecEvent> {
        TODO("Not yet implemented")
    }

    actual suspend fun addToWhiteList(packageName: String) {
    }

    actual suspend fun storeExternalId(data: String) {
    }

    actual fun attachListener(callback: threat.ThreatCallback) {
    }

    actual fun detachListener() {
    }

    actual suspend fun getAppIcon(packageName: String): String {
        TODO("Not yet implemented")
    }

    actual suspend fun blockScreenCapture(enable: Boolean) {
    }

    actual suspend fun isScreenCaptureBlocked(): Boolean {
        TODO("Not yet implemented")
    }
}