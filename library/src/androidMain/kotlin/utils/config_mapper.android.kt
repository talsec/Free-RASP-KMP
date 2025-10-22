package utils

import com.aheaditec.talsec_security.security.api.TalsecConfig
import model.config.freeraspConfig
import model.exception.FreeRASPException

fun freeraspConfig.toNativeConfig(): TalsecConfig {
    val androidConfig = this.androidConfig ?: throw FreeRASPException("AndroidConfig is required on the Android platform but was null.")

    val builder = TalsecConfig.Builder(
        androidConfig.packageName,
        androidConfig.certificateHashes.toTypedArray()
    )

    builder.apply {
        watcherMail(this@toNativeConfig.watcherMail)
        prod(this@toNativeConfig.isProd)
        killOnBypass(this@toNativeConfig.killOnBypass)

        androidConfig.supportedAlternativeStores?.let {
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