package com.freeraspkmp.android.utils

import com.freeraspkmp.model.config.freeraspConfig
import com.freeraspkmp.model.exception.FreeraspKMPException
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Verifies the provided freeRASP configuration for Android.
 */
@Throws(FreeraspKMPException::class)
internal fun verifyConfig(config: freeraspConfig) {
    config.androidConfig ?: throw FreeraspKMPException("freeRASP: androidConfig must be provided on Android.")

    if (config.androidConfig.packageName.isBlank()) {
        throw FreeraspKMPException("freeRASP: packageName in androidConfig must not be blank.")
    }

    if (config.androidConfig.certificateHashes.isEmpty()) {
        throw FreeraspKMPException("freeRASP: certificateHashes in androidConfig must not be empty.")
    }

    verifyHashes(config.androidConfig.certificateHashes)
}

/**
 * Verifies the format and validity of SHA-256 hashes.
 * @param hashesEncoded The list of Base64-encoded hashes.
 */
@OptIn(ExperimentalEncodingApi::class)
@Throws(FreeraspKMPException::class)
private fun verifyHashes(hashesEncoded: List<String>) {
    hashesEncoded.forEach { hash ->
        try {
            val decodedHash = Base64.decode(hash)
            if (decodedHash.size != 32) {
                throw FreeraspKMPException("freeRASP: Invalid hash length: '$hash' is not 32 bytes long")
            }
        } catch (e: IllegalArgumentException) {
            throw FreeraspKMPException("freeRASP: Invalid Base64 format for hash: '$hash'")
        }
    }
}