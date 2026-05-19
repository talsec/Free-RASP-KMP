package com.freeraspkmp.model.config

/**
 * Scope of installed apps included in the malware scan.
 */
enum class ScopeType {
    SIDELOADED_ONLY,
    SIDELOADED_AND_SYSTEM_EXCLUDE_OEM,
    SIDELOADED_AND_OEM,
    SIDELOADED_AND_SYSTEM_AND_OEM,
    ALL
}

/**
 * Controls how detection reasons are reported on a suspicious app.
 */
enum class ReasonMode { ALL, HIGHEST_CONFIDENCE }

/**
 * Defines which installed apps should be scanned for malware.
 *
 * @param scanScope The set of apps to include in the scan.
 * @param trustedInstallSources Installation sources whose apps should be excluded from the scan.
 */
data class ScanScope(
    val scanScope: ScopeType,
    val trustedInstallSources: List<String>? = null
)

/**
 * Configuration for malware detection.
 *
 * @param packageNames Package names of known malicious apps.
 * @param hashes Certificate hashes of known malicious apps.
 * @param requestedPermissions Groups of permissions an app must request to be flagged as suspicious.
 * @param grantedPermissions Groups of permissions an app must be granted to be flagged as suspicious.
 * @param scanScope Defines which apps are scanned.
 *   Defaults to [ScanScope] with [ScopeType.SIDELOADED_ONLY].
 * @param reasonMode Controls how detection reasons are reported.
 *   Defaults to [ReasonMode.HIGHEST_CONFIDENCE].
 */
data class SuspiciousAppDetectionConfig(
    val packageNames: List<String>? = null,
    val hashes: List<String>? = null,
    val requestedPermissions: List<List<String>>? = null,
    val grantedPermissions: List<List<String>>? = null,
    val scanScope: ScanScope = ScanScope(ScopeType.SIDELOADED_ONLY),
    val reasonMode: ReasonMode = ReasonMode.HIGHEST_CONFIDENCE
)
