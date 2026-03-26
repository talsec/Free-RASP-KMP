# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2025-03-25

- Android SDK version: 18.0.4
- iOS SDK version: 6.14.4

### Kotlin Multiplatform

#### Added
- Added `raspExecutionStateEvents: SharedFlow<RaspExecutionStateEvent>` to `FreeraspKMP` — a dedicated channel for RASP execution lifecycle events
- Added `RaspExecutionStateEvent` sealed class with `AllChecksFinished` event, indicating that all security checks have completed
- Added `FreeRaspEvent.Automation` for detecting automation frameworks (e.g. Appium) — Android only
- Added `removeExternalId()` to `FreeraspKMP` — removes a previously stored external ID (Android only; no-op on iOS)
- Added `permissions` field to `SuspiciousAppInfo`

#### Changed
- `AllChecksFinished` moved from `FreeRaspEvent` to `RaspExecutionStateEvent` — **breaking change**, update your event handling accordingly
- `storeExternalId()` now throws `FreeraspKMPException` on failure instead of silently failing — **breaking change**

#### Fixed
- Fixed `whitelistedInstallationSources` not being correctly passed to the native Android config

### Android

#### Added
- Added new detection check for KernelSU

#### Fixed
- Fixed memory management issues in the native code

### iOS

#### Fixed
- Fixed new jailbreak checks false positives on iOS 14 and 13

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
