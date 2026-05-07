package com.freeraspkmp.android.utils

import com.aheaditec.talsec_security.security.api.TalsecConfig
import com.freeraspkmp.model.config.MalwareScanScope
import com.freeraspkmp.model.config.ReasonMode
import com.freeraspkmp.model.config.ScopeType
import com.freeraspkmp.model.config.SuspiciousAppDetectionConfig
import com.freeraspkmp.model.config.freeraspConfig
import com.freeraspkmp.model.exception.FreeraspKMPException
import com.aheaditec.talsec_security.security.api.SuspiciousAppDetectionConfig as NativeSuspiciousAppDetectionConfig
import com.aheaditec.talsec_security.security.api.MalwareScanScope as NativeMalwareScanScope
import com.aheaditec.talsec_security.security.api.ScopeType as NativeScopeType
import com.aheaditec.talsec_security.security.api.ReasonMode as NativeReasonMode

@Suppress("DEPRECATION")
fun freeraspConfig.toNativeConfig(): TalsecConfig {
    val androidConfig = this.androidConfig ?: throw FreeraspKMPException("AndroidConfig is required on the Android platform but was null.")

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
                whitelistedInstallationSources(malware.whitelistedInstallationSources.toTypedArray())
            }
        }

        androidConfig.suspiciousAppDetectionConfig?.let {
            suspiciousAppDetection(it.toNative())
        }
    }
    return builder.build()

}

internal fun SuspiciousAppDetectionConfig.toNative(): NativeSuspiciousAppDetectionConfig =
    NativeSuspiciousAppDetectionConfig(
        packageNames = packageNames?.toSet(),
        hashes = hashes?.toSet(),
        requestedPermissions = requestedPermissions?.map { it.toSet() }?.toSet(),
        grantedPermissions = grantedPermissions?.map { it.toSet() }?.toSet(),
        malwareScanScope = malwareScanScope?.toNative(),
        reasonMode = reasonMode?.toNative()
    )

internal fun MalwareScanScope.toNative(): NativeMalwareScanScope =
    NativeMalwareScanScope(
        scanScope = scanScope.toNative(),
        trustedInstallSources = trustedInstallSources
    )

internal fun ScopeType.toNative(): NativeScopeType =
    when (this) {
        ScopeType.SIDELOADED_ONLY -> NativeScopeType.SIDELOADED_ONLY
        ScopeType.SIDELOADED_AND_SYSTEM_EXCLUDE_OEM -> NativeScopeType.SIDELOADED_AND_SYSTEM_EXCLUDE_OEM
        ScopeType.SIDELOADED_AND_OEM -> NativeScopeType.SIDELOADED_AND_OEM
        ScopeType.SIDELOADED_AND_SYSTEM_AND_OEM -> NativeScopeType.SIDELOADED_AND_SYSTEM_AND_OEM
        ScopeType.ALL -> NativeScopeType.ALL
    }

internal fun ReasonMode.toNative(): NativeReasonMode =
    when (this) {
        ReasonMode.ALL -> NativeReasonMode.ALL
        ReasonMode.HIGHEST_CONFIDENCE -> NativeReasonMode.HIGHEST_CONFIDENCE
    }
