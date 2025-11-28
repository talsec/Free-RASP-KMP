package com.freeraspkmp.model.config

/**
 * Configuration for iOS platform.
 *
 * @param bundleIds A list of expected bundle IDs.
 * @param teamId The expected team ID.
 */
data class IOSConfig (
    val bundleIds: List<String>,
    val teamId: String
)