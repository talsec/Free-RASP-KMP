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

        androidConfig.malwareConfig?.let { malware ->
            if(malware.blacklistedPackageNames.isNotEmpty())
            {
                blacklistedPackageNames(malware.blacklistedPackageNames.toTypedArray())
            }
            if(malware.blacklistedHashes.isNotEmpty())
            {
                blacklistedHashes(malware.blacklistedHashes.toTypedArray())
            }
            if (malware.suspiciousPermissions.isNotEmpty())
            {
                val nativePermissions = malware.suspiciousPermissions
                    .map { innerList -> innerList.toTypedArray() }
                    .toTypedArray()

                suspiciousPermissions(nativePermissions)
            }
            if(malware.whitelistedInstallationSources.isNotEmpty())
            {
                whitelistedInstallationSources(malware.blacklistedHashes.toTypedArray())
            }
        }
    }
    return builder.build()

}