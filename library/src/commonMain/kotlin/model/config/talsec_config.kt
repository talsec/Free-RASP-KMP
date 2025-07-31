package model.config

data class TalsecConfig(
    val watcherMail: String,
    val isProd: Boolean = true,
    val androidConfig: AndroidConfig? = null,
    val iosConfig: IOSConfig? = null
)
