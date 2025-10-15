package utils

import model.config.freeraspConfig
import model.exception.FreeRASPException
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Verifies the provided freeRASP configuration for Android.
 */
@Throws(FreeRASPException::class)
internal fun verifyConfig(config: freeraspConfig) {
    config.androidConfig ?: throw FreeRASPException("freeRASP: androidConfig must be provided on Android.")

    if (config.androidConfig.packageName.isBlank()) {
        throw FreeRASPException("freeRASP: packageName in androidConfig must not be blank.")
    }

    if (config.androidConfig.certificateHashes.isEmpty()) {
        throw FreeRASPException("freeRASP: certificateHashes in androidConfig must not be empty.")
    }

    verifyHashes(config.androidConfig.certificateHashes)
}

/**
 * Verifies the format and validity of SHA-256 hashes.
 * @param hashesEncoded The list of Base64-encoded hashes.
 */
@OptIn(ExperimentalEncodingApi::class)
@Throws(FreeRASPException::class)
private fun verifyHashes(hashesEncoded: List<String>) {
    hashesEncoded.forEach { hash ->
        try {
            val decodedHash = Base64.decode(hash)
            if (decodedHash.size != 32) {
                throw FreeRASPException("freeRASP: Invalid hash length: '$hash' is not 32 bytes long")
            }
        } catch (e: IllegalArgumentException) {
            throw FreeRASPException("freeRASP: Invalid Base64 format for hash: '$hash'")
        }
    }
}