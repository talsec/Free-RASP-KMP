package utils

import model.config.TalsecConfig

internal data class iosTalsecConfig(
    val appBundleIds: List<String>,
    val appTeamId: String,
    val watcherMail: String,
    val isProd: Boolean
)

internal fun TalsecConfig.toNativeConfig(): iosTalsecConfig {
    val iosConfig = this.iosConfig
        ?: throw IllegalArgumentException("IOSConfig is required on the iOS platform but was null.")

    return iosTalsecConfig(
        appBundleIds = iosConfig.bundleIds,
        appTeamId = iosConfig.teamId,
        watcherMail = this.watcherMail,
        isProd = this.isProd
    )
}