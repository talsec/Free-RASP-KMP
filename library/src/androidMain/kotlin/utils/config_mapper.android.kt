package utils

import com.aheaditec.talsec_security.security.api.TalsecConfig as NativeTalsecConfig
import model.config.TalsecConfig

fun TalsecConfig.toNativeConfig(): NativeTalsecConfig {
    val androidConfig = this.androidConfig ?: throw IllegalArgumentException("AndroidConfig is required on the Android platform but was null.")

    val builder = NativeTalsecConfig.Builder(
        androidConfig.packageName,
        androidConfig.signingCertHashes.toTypedArray()
    )

    builder.apply {
        watcherMail(this@toNativeConfig.watcherMail)
        prod(this@toNativeConfig.isProd)

        androidConfig.supportedStores?.let {
            supportedAlternativeStores(it.toTypedArray())
        }
    }
    return builder.build()

}