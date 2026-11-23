# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [3.0.0] - 2026-11-23

- Android SDK version: 20.0.1
- iOS SDK version: 9.0.0

### Breaking

- `RaspExecutionStateEvent.AllChecksFinished` replaced by `FastChecksFinished`, triggered when the fast checks are completed while the postponed checks may still run. `CriticalChecksFinished` and `PostponedChecksFinished` are new. All three are triggered on both platforms
- `IOSConfig.bundleIds` renamed to `appBundleIds` and `IOSConfig.teamId` renamed to `appTeamId`, matching the iOS SDK and the other freeRASP plugins
- `FreeRaspEvent.OngoingCall` added, so an exhaustive `when` over `FreeRaspEvent` must handle it
- The Android SDK is now published as `app.talsec.rasp:TalsecRasp-Community-KMP`. Set `sdkVersion = "20.0.1"` in the `talsec { }` block of the `app.talsec.plugin` Gradle plugin

### Kotlin Multiplatform

#### Added

- Added `app.talsec.kmp.ios` Gradle plugin for iOS integration without Xcode setup
- Added `FreeRaspEvent.OngoingCall` with the new `CallState` enum (`STARTED`, `ONGOING`, `STOPPED`), driven by `startOngoingCallDetection()` and `stopOngoingCallDetection()` (Android only)
- Added `blockScreenControl()` and `isScreenControlBlocked()` to prevent accessibility services from reading or controlling the app screen (Android only)
- Added `blockOverlayWindows()` and `isOverlayWindowsBlocked()` to hide overlay windows of other apps (Android 12+ only, requires the `HIDE_OVERLAY_WINDOWS` permission)

#### Changed

- `raspExecutionStateEvents` replays every execution state of the run in the order it occurred, so late subscribers receive all of them
- On Android, `isScreenCaptureBlocked()` reports the protection state of the activity in the foreground
- Example app uses the `app.talsec.kmp.ios` plugin

### Android

#### Changed

- Deprecated the freeRASP Maven coordinates (`com.aheaditec.talsec.security:TalsecSecurity-{flavor}`)
- Improved Bootloader detection
- Extended emulator detection with additional device-property and ABI checks
- Improved logging structure and data enrichment for better Portal insights/reports
- Refactoring of the time spoofing detection which improves the performance of the freeRASP check execution
- Improved the performance of the App integrity check
- Updated CA bundle

#### Fixed

- Improved hook detection to prevent collisions with running VPN services and to better work with the device resources (threads/RAM)
- Fixed a release crash during root detection when a checked package is not installed
- Fixed a crash in `ScreenProtector.unregisterScreenCallbacks` on Android 15+ when several activities registered screen callbacks
- Fixed intermittent build failures of apps using the SDK (`NullPointerException: Cannot read field "d"`, shown by Gradle as `[CIRCULAR REFERENCE]`) caused by invalid bytecode in some SDK builds
- Fixed `killOnBypass` closing the app when the same threat was reported twice in quick succession

### iOS

#### Added

- Improved app integrity detection.

#### Changed

- Improved jailbreak detection.
- Updated CA bundle.
- Reduced the SDK size.

#### Fixed

- Fixed stack overflow crash when `blockScreenCapture` is enabled.
- Fixed missing app integrity detection details in logs for builds with a provisioning profile.
- Fixed placement of app integrity certificate details in logs.

## [2.2.0] - 2026-10-22

- Android SDK version: 19.3.1
- iOS SDK version: 8.0.3

### Kotlin Multiplatform

#### Added

- Added support for the `app.talsec.plugin` Gradle plugin
- Added Swift Package Manager support

#### Fixed

- `TalsecBridge` no longer embeds `TalsecRuntime`
- `TalsecBridge` now supports iOS 13.0+

### Android

#### Changed

- Changed Talsec integration from Maven dependency to the `app.talsec.plugin` Gradle plugin
- The Talsec Gradle plugin requires the app to build with Android Gradle Plugin 8.3 or newer

#### Fixed

- Root detection related bugs causing false positives

### iOS

#### Added

- Improved jailbreak detection
- Improved hook detection
- Improved data collection

#### Changed

- Improved logging structure and data enrichment for better Portal insights/reports
- Updated `OpenSSL` dependency to `3.5.8`
- Updated `curl` dependency to `8.22.0`

#### Fixed

- Fixed usage of `_dyld_get_shared_cache_range`
- Fixed crash in Frida port scanning

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
