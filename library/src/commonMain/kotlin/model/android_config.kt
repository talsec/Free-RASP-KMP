package model

data class AndroidConfig(
    val packageName: String,
    val signingCertHashes: List<String>,
    val supportedStores: List<String> = emptyList(),
    val malwareConfig: MalwareConfig? = null
)
