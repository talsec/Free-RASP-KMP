# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.1.1] - 2026-08-25

- Android SDK version: 19.2.3
- iOS SDK version: 7.1.2

### Android

#### Fixed

- Fixed the reporting issue in root detection
- Fixed the reporting issue in hook detection

## [2.1.0] - 2026-08-07

- Android SDK version: 19.2.1
- iOS SDK version: 7.1.2

### Kotlin Multiplatform

#### Added

- Added `FreeRaspEvent.Bootloader`, reporting an unlocked or compromised bootloader — Android only

### Android

#### Added

- Added bootloader detection (unlocked/compromised) with `onBootloader()` callback
- Added option to fetch JitPack dependencies from our own Talsec repository (`https://europe-west3-maven.pkg.dev/talsec-artifact-repository/common`)

#### Fixed

- Fixed native crash caused by std::terminate() race condition
- Fixed periodic hook and root check overwriting
- Fixed root detection crash in obfuscated release builds
- Fixed hardware-backed keystore detection failing with `NoSuchMethodError` on some Android 12+ devices

#### Changed

- Improved KernelSU detection
- Improved hook detection
- Improved Frida detection
- Improved root detection capabilities

### iOS

#### Added

- Improved jailbreak detection
- Added support for postponed checks, therefore, due to slower execution, some subchecks are run after initial startup checks.
- Improved hook detection.

#### Fixed

- Fixed issue with app's color scheme initialization.
- Fixed bad memory access in jaibreak check.

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

- New API class `SuspiciousAppDetectionConfig` that can be used to configure malware detection
- New API for malware detection configuration in `TalsecConfig`, see `TalsecConfig.Builder#suspiciousAppDetection`

#### Fixed

- Fixed `VerifyError` caused by `JaCoCo` bytecode instrumentation
- Fixed a potential cause of crash in the multi-instance detector
- Fixed Java interoperability of `ScreenProtector` methods
- Fixed Kotlin classpath conflicts in SDK dependency resolution (Kotlin 2.0.0)

#### Changed

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
