package com.freeraspkmp.android.utils

import com.aheaditec.talsec_security.security.api.TalsecConfig
import com.freeraspkmp.model.config.ScanScope
import com.freeraspkmp.model.config.ReasonMode
import com.freeraspkmp.model.config.ScopeType
import com.freeraspkmp.model.config.SuspiciousAppDetectionConfig
import com.freeraspkmp.model.config.freeraspConfig
import com.freeraspkmp.model.exception.FreeraspKMPException
import com.aheaditec.talsec_security.security.api.SuspiciousAppDetectionConfig as NativeSuspiciousAppDetectionConfig
import com.aheaditec.talsec_security.security.api.MalwareScanScope as NativeMalwareScanScope
import com.aheaditec.talsec_security.security.api.ScopeType as NativeScopeType
import com.aheaditec.talsec_security.security.api.ReasonMode as NativeReasonMode

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

        androidConfig.suspiciousAppDetectionConfig?.let {
            suspiciousAppDetection(it.toNative())
        }
    }
    return builder.build()

}

internal fun SuspiciousAppDetectionConfig.toNative(): NativeSuspiciousAppDetectionConfig =
    NativeSuspiciousAppDetectionConfig(
        packageNames?.toSet(),
        hashes?.toSet(),
        requestedPermissions?.map { it.toSet() }?.toSet(),
        grantedPermissions?.map { it.toSet() }?.toSet(),
        scanScope.toNative(),
        reasonMode.toNative()
    )

internal fun ScanScope.toNative(): NativeMalwareScanScope =
    NativeMalwareScanScope(
        scopeType.toNative(),
        trustedInstallSources
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
