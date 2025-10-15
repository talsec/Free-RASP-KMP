package model.config

/**
 * The main configuration for freeRASP.
 *
 * @param watcherMail The email address to which security reports will be sent.
 * @param isProd `true` if the app is in a production environment, `false` otherwise.
 * @param androidConfig Android-specific configuration.
 * @param iosConfig iOS-specific configuration.
 */
data class freeraspConfig(
    val watcherMail: String,
    val isProd: Boolean = true,
    val androidConfig: AndroidConfig? = null,
    val iosConfig: IOSConfig? = null
)
