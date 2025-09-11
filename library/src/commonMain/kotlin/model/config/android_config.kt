package model.config

data class AndroidConfig(
    val packageName: String,
    val certificateHashes: List<String>,
    val supportedAlternativeStores: List<String> = emptyList(),
    val malwareConfig: MalwareConfig? = null
)
