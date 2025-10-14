package utils

import model.config.freeraspConfig
import model.exception.FreeRASPException

/**
 * Verifies the provided freeRASP configuration for iOS.
 */
@Throws(FreeRASPException::class)
internal fun verifyConfig(config: freeraspConfig) {
    config.iosConfig ?: throw FreeRASPException("freeRASP: iosConfig must be provided on iOS.")

    if (config.iosConfig.bundleIds.isEmpty()) {
        throw FreeRASPException("freeRASP: bundleIds in iosConfig must not be empty.")
    }

    if (config.iosConfig.teamId.isBlank()) {
        throw FreeRASPException("freeRASP: teamId in iosConfig must not be blank.")
    }
}