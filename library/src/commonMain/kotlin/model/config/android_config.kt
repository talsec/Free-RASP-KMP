package model.config

/**
 * Configuration for Android platform.
 *
 * @param packageName The expected package name of the app.
 * @param certificateHashes A list of expected certificate hashes in base64.
 * @param supportedAlternativeStores A list of package names for supported alternative stores.
 * @param malwareConfig Configuration for malware detection.
 */
data class AndroidConfig(
    val packageName: String,
    val certificateHashes: List<String>,
    val supportedAlternativeStores: List<String> = emptyList(),
    val malwareConfig: MalwareConfig? = null
)
