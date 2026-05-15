# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2026-05-15

- Android SDK version: 18.3.0
- iOS SDK version: 6.14.4

### Breaking

- `SuspiciousAppInfo.reason` (String) renamed to `reasons` (Set<String>)
- Value `"blacklist"` in `reasons` renamed to `"blocklist"`
- Removed `MalwareConfig` and `AndroidConfig.malwareConfig`
- `SuspiciousAppDetectionConfig.malwareScanScope` and `reasonMode` are now non-null with defaults `MalwareScanScope(ScopeType.SIDELOADED_ONLY)` and `ReasonMode.HIGHEST_CONFIDENCE`

### Android

#### Added

- Added a new sub-check for `HMA` detection to the root detector
- Added a new sub-check for `KernelSU` detection to the root detector
- Added a new sub-check for `Frida Server` detection to the hook detector
- Added Huawei App Market provider to HMA detection queries
- New API class `SuspiciousAppDetectionConfig` that can be used to configure malware detection
- New API for malware detection configuration in `TalsecConfig`, see `TalsecConfig.Builder#suspiciousAppDetection`

#### Fixed

- Fixed `VerifyError` caused by `JaCoCo` bytecode instrumentation
- Fixed a potential cause of crash in the multi-instance detector
- Fixed crash caused by unhandled `SecurityException` thrown by `UsageStatsManager` in root detection
- Fixed manifest merge conflicts in HMA detection providers
- Fixed Java interoperability of `ScreenProtector` methods
- Fixed Kotlin classpath conflicts in SDK dependency resolution (Kotlin 2.0.0)

#### Changed

- Fine-tuned `KernelSU` detection
- Fine-tuned hook detection
- Fine-tuned location spoofing detection
- Modified malware incident log structure for better aggregation

## [1.1.0] - 2025-03-25

- Android SDK version: 18.0.4
- iOS SDK version: 6.14.4

### Kotlin Multiplatform

#### Added
- Added `raspExecutionStateEvents: SharedFlow<RaspExecutionStateEvent>` to `FreeraspKMP` — a dedicated channel for RASP execution lifecycle events
- Added `RaspExecutionStateEvent` sealed class with `AllChecksFinished` event, indicating that all security checks have completed
- Added `FreeRaspEvent.Automation` for detecting automation frameworks (e.g. Appium) — Android only
- Added `removeExternalId()` to `FreeraspKMP` — removes a previously stored external ID
- Added `permissions` field to `SuspiciousAppInfo`

#### Changed
- `AllChecksFinished` moved from `FreeRaspEvent` to `RaspExecutionStateEvent` — **breaking change**, update your event handling accordingly
- `storeExternalId()` now throws `FreeraspKMPException` on failure instead of silently failing — **breaking change**

#### Fixed
- Fixed `whitelistedInstallationSources` not being correctly passed to the native Android config

### Android

#### Added
- Added Automation detection for detecting automation frameworks (e.g. Appium)
- Added new detection check for KernelSU
- Added wireless ADB detection to existing ADB detections

#### Changed
- Improved `HMA` and root detection capabilities

#### Fixed
- Fixed memory management issues in the native code
- Patched possibility of `getInstalledPackages` throwing `DeadSystemException`
- Patched possibility of `getNetworkCapabilities` throwing `SecurityException`
- Fixed well-known issue of `Cipher.init` throwing `KeyStoreConnectException`

#### Removed
- Removed deprecated `monitoring` feature

### iOS

#### Changed
- Improved `timeSpoofing` detection methods

#### Fixed
- Fixed new jailbreak checks false positives on iOS 14 and 13
- Fixed false positives with jailbreak on iOS 15 and 16
- Fixed issue with app crashing on screenshot/screen recording
- Fixed retrigger jailbreak issue on iOS 15 and 16

## [1.0.0] - 2025-12-05

- Android SDK version: 17.0.0
- iOS SDK version: 6.13.0

### Kotlin Multiplatform

#### Added
- Added `killOnBypass` to `freeraspConfig` — configures whether the app should be terminated when threat callbacks are suppressed or hooked by an attacker (Android only) ([Issue 65](https://github.com/talsec/Free-RASP-Android/issues/65))
- Added `FreeRaspEvent.TimeSpoofing` for detecting device time tampering (Android only)
- Added `FreeRaspEvent.LocationSpoofing` for detecting location spoofing (Android only)
- Added `FreeRaspEvent.UnsecureWifi` for detecting unsecured Wi-Fi connections (Android only)
- Added `FreeRaspEvent.AllChecksFinished` — notifies when all security checks have completed

### Android

#### Removed
- Removed deprecated `Pbkdf2Native` and both related native libraries (`libpbkdf2_native.so` and `libpolarssl.so`)

#### Changed
- Updated internal dependencies

### iOS

#### Changed
- Updated internal dependencies
