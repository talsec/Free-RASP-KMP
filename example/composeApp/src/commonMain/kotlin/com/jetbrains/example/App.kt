package com.jetbrains.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.freeraspkmp.api.FreeraspKMP
import com.freeraspkmp.model.CallState
import com.freeraspkmp.model.FreeRaspEvent
import com.freeraspkmp.model.RaspExecutionStateEvent
import com.freeraspkmp.model.SuspiciousAppInfo
import com.freeraspkmp.model.config.AndroidConfig
import com.freeraspkmp.model.config.IOSConfig
import com.freeraspkmp.model.config.ScanScope
import com.freeraspkmp.model.config.ReasonMode
import com.freeraspkmp.model.config.ScopeType
import com.freeraspkmp.model.config.SuspiciousAppDetectionConfig
import com.freeraspkmp.model.config.freeraspConfig
import com.jetbrains.example.model.initialChecks
import com.jetbrains.example.model.toCheckId
import com.jetbrains.example.ui.FreeraspTheme
import com.jetbrains.example.ui.SecurityDashboard
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    FreeraspTheme {
        val freeraspConfig = remember {
            freeraspConfig(
                watcherMail = "test@mail.app",
                isProd = true,
                killOnBypass = true,
                androidConfig = AndroidConfig(
                    packageName = "com.jetbrains.example",
                    certificateHashes = listOf("K/iFV7+CypnATFWcrUVM6aUIB5gnU2xwzRJOiKJJqPw="),
                    suspiciousAppDetectionConfig = SuspiciousAppDetectionConfig(
                        packageNames = listOf("com.google.android.youtube"),
                        hashes = listOf("FgvSehLMM91E7lX/Zqp3u4jMmd0A7hH/Iqozu0TMVd0u"),
                        requestedPermissions = listOf(
                            listOf(
                                "android.permission.INTERNET",
                                "android.permission.ACCESS_COARSE_LOCATION",
                            ),
                            listOf("android.permission.BLUETOOTH"),
                            listOf("android.permission.BATTERY_STATS"),
                        ),
                        grantedPermissions = listOf(
                            listOf("android.permission.ACCESS_FINE_LOCATION"),
                        ),
                        scanScope = ScanScope(
                            scopeType = ScopeType.SIDELOADED_AND_SYSTEM_EXCLUDE_OEM,
                            trustedInstallSources = listOf("com.apkpure.aegon"),
                        ),
                        reasonMode = ReasonMode.HIGHEST_CONFIDENCE,
                    )
                ),
                iosConfig = IOSConfig(
                    appBundleIds = listOf("com.jetbrains.example.iosApp"),
                    appTeamId = "YOUR_TEAM_ID"
                )
            )
        }

        var checks by remember { mutableStateOf(initialChecks) }
        var malwareApps by remember { mutableStateOf<List<SuspiciousAppInfo>>(emptyList()) }
        var finishedStates by remember { mutableStateOf(emptySet<RaspExecutionStateEvent>()) }
        var isScreenCaptureBlocked by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            launch {
                FreeraspKMP.threatEvents.collect { event ->
                    val checkId = event.toCheckId()
                    // A call that has ended is no longer a threat
                    val isDetected = event !is FreeRaspEvent.OngoingCall || event.callState != CallState.STOPPED
                    checks = checks.map { check ->
                        if (check.id == checkId) check.copy(isDetected = isDetected) else check
                    }
                    if (event is FreeRaspEvent.Malware) {
                        malwareApps = event.suspiciousAppInfo
                    }
                }
            }

            launch {
                FreeraspKMP.raspExecutionStateEvents.collect { state ->
                    println("freeRASP: $state")
                    finishedStates = finishedStates + state
                }
            }

            try {
                FreeraspKMP.start(freeraspConfig)
                FreeraspKMP.blockScreenCapture(true)
                isScreenCaptureBlocked = FreeraspKMP.isScreenCaptureBlocked()
            } catch (e: Exception) {
                println("freeraspKMP: Error starting: ${e.message}")
            }
        }

        SecurityDashboard(
            checks = checks,
            finishedStates = finishedStates,
            isScreenCaptureBlocked = isScreenCaptureBlocked,
            malwareApps = malwareApps,
            onToggleScreenCapture = {
                scope.launch {
                    val newValue = !isScreenCaptureBlocked
                    FreeraspKMP.blockScreenCapture(newValue)
                    isScreenCaptureBlocked = FreeraspKMP.isScreenCaptureBlocked()
                }
            },
            onStoreExternalId = { id, onResult ->
                scope.launch {
                    try {
                        FreeraspKMP.storeExternalId(id)
                        onResult("External ID stored", true)
                    } catch (e: Exception) {
                        onResult(e.message ?: "Failed to store External ID", false)
                    }
                }
            },
            onRemoveExternalId = { onDone ->
                scope.launch {
                    FreeraspKMP.removeExternalId()
                    onDone()
                }
            },
        )
    }
}
