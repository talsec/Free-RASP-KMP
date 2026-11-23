package com.freeraspkmp.ios.utils

import com.freeraspkmp.model.config.freeraspConfig
import com.freeraspkmp.model.exception.FreeraspKMPException

/**
 * Verifies the provided freeRASP configuration for iOS.
 */
@Throws(FreeraspKMPException::class)
internal fun verifyConfig(config: freeraspConfig) {
    config.iosConfig ?: throw FreeraspKMPException("freeRASP: iosConfig must be provided on iOS.")

    if (config.iosConfig.appBundleIds.isEmpty()) {
        throw FreeraspKMPException("freeRASP: appBundleIds in iosConfig must not be empty.")
    }

    if (config.iosConfig.appTeamId.isBlank()) {
        throw FreeraspKMPException("freeRASP: appTeamId in iosConfig must not be blank.")
    }
}