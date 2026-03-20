package com.freeraspkmp.model

/**
 * Contains information about a suspicious app.
 *
 * @param packageInfo Information about the suspicious package.
 * @param reason The reason why the app is considered suspicious.
 * @param permissions A set of suspicious permissions held by the app. Populated when reason is `suspiciousPermission`.
 */
data class SuspiciousAppInfo(
    val packageInfo: PackageInfo,
    val reason: String,
    val permissions: Set<String> = emptySet()
)

/**
 * Contains information about an app package.
 *
 * @param packageName The package name of the app.
 * @param appName The name of the app.
 * @param version The version of the app.
 * @param appIcon The app icon as a Base64 encoded string.
 * @param installerStore The store from which the app was installed.
 */
data class PackageInfo(
    val packageName: String,
    val appName: String?,
    val version: String?,
    val appIcon: String?,
    val installerStore: String?
)