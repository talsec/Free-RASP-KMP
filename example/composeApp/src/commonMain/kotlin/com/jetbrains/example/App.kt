package com.jetbrains.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.freeraspkmp.api.FreeraspKMP
import com.freeraspkmp.model.FreeRaspEvent
import com.freeraspkmp.model.SuspiciousAppInfo
import com.freeraspkmp.model.config.AndroidConfig
import com.freeraspkmp.model.config.IOSConfig
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
                        packageNames = listOf("com.google.android.youtube")
                    )
                ),
                iosConfig = IOSConfig(
                    bundleIds = listOf("com.jetbrains.example.iosApp"),
                    teamId = "YOUR_TEAM_ID"
                )
            )
        }

        var checks by remember { mutableStateOf(initialChecks) }
        var malwareApps by remember { mutableStateOf<List<SuspiciousAppInfo>>(emptyList()) }
        var allChecksFinished by remember { mutableStateOf(false) }
        var isScreenCaptureBlocked by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            launch {
                FreeraspKMP.threatEvents.collect { event ->
                    val checkId = event.toCheckId()
                    checks = checks.map { check ->
                        if (check.id == checkId) check.copy(isDetected = true) else check
                    }
                    if (event is FreeRaspEvent.Malware) {
                        malwareApps = event.suspiciousAppInfo
                    }
                }
            }

            launch {
                FreeraspKMP.raspExecutionStateEvents.collect {
                    allChecksFinished = true
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
            allChecksFinished = allChecksFinished,
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
