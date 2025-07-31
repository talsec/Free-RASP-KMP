package model

data class SuspiciousAppInfo(
    val packageInfo: PackageInfo,
    val reason: String
)

data class PackageInfo(
    val packageName: String,
    val appName: String?,
    val version: String?,
    val appIcon: String?,
    val installerStore: String?
)