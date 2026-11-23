package com.freeraspkmp.model.config

/**
 * Configuration for iOS platform.
 *
 * @param appBundleIds A list of expected bundle IDs.
 * @param appTeamId The expected team ID.
 */
data class IOSConfig (
    val appBundleIds: List<String>,
    val appTeamId: String
)