package com.freeraspkmp.ios.utils

import com.freeraspkmp.model.config.freeraspConfig
import com.freeraspkmp.model.exception.FreeraspKMPException

internal data class iosFreeraspConfig(
    val appBundleIds: List<String>,
    val appTeamId: String,
    val watcherMail: String,
    val isProd: Boolean
)

internal fun freeraspConfig.toNativeConfig(): iosFreeraspConfig {
    val iosConfig = this.iosConfig
        ?: throw FreeraspKMPException("IOSConfig is required on the iOS platform but was null.")

    return iosFreeraspConfig(
        appBundleIds = iosConfig.bundleIds,
        appTeamId = iosConfig.teamId,
        watcherMail = this.watcherMail,
        isProd = this.isProd
    )
}