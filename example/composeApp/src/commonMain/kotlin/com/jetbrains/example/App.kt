package com.jetbrains.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import api.freeraspKMP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import model.FreeRASPEvent
import model.config.AndroidConfig
import model.config.IOSConfig
import model.config.MalwareConfig
import model.config.freeraspConfig
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {

        val freeraspConfig = remember {
            freeraspConfig(
                watcherMail = "test@mail.app",
                isProd = true,
                androidConfig = AndroidConfig(
                    packageName = "com.jetbrains.example",
                    certificateHashes = listOf("K/iFV7+CypnATFWcrUVM6aUIB5gnU2xwzRJOiKJJqPw="),
                    malwareConfig = MalwareConfig(
                        blacklistedPackageNames = listOf("com.google.android.youtube")
                    )
                ),
                iosConfig = IOSConfig(
                    bundleIds = listOf("com.jetbrains.example.iosApp"),
                    teamId = "YOUR_TEAM_ID"
                )
            )
        }

        LaunchedEffect(Unit) {
            freeraspKMP.threatEvents.onEach { event ->
                when (event) {
                    is FreeRASPEvent.PrivilegedAccess -> println("freeraspKMP: PrivilegedAccess")
                    is FreeRASPEvent.Debug -> println("freeraspKMP: Debug")
                    is FreeRASPEvent.Simulator -> println("freeraspKMP: Simulator")
                    is FreeRASPEvent.AppIntegrity -> println("freeraspKMP: AppIntegrity")
                    is FreeRASPEvent.UnofficialStore -> println("freeraspKMP: UnofficialStore")
                    is FreeRASPEvent.Hooks -> println("freeraspKMP: Hooks")
                    is FreeRASPEvent.DeviceBinding -> println("freeraspKMP: DeviceBinding")
                    is FreeRASPEvent.ObfuscationIssues -> println("freeraspKMP: ObfuscationIssues")
                    is FreeRASPEvent.Screenshot -> println("freeraspKMP: Screenshot")
                    is FreeRASPEvent.ScreenRecording -> println("freeraspKMP: ScreenRecording")
                    is FreeRASPEvent.Passcode -> println("freeraspKMP: Passcode")
                    is FreeRASPEvent.SecureHardwareNotAvailable -> println("freeraspKMP: SecureHardwareNotAvailable")
                    is FreeRASPEvent.SystemVPN -> println("freeraspKMP: SystemVPN")
                    is FreeRASPEvent.DevMode -> println("freeraspKMP: DevMode")
                    is FreeRASPEvent.AdbEnabled -> println("freeraspKMP: AdbEnabled")
                    is FreeRASPEvent.MultiInstance -> println("freeraspKMP: MultiInstance")
                    is FreeRASPEvent.DeviceID -> println("freeraspKMP: DeviceID")
                    is FreeRASPEvent.Malware -> {
                        println("-------------------------------------------")
                        println("freeraspKMP: Malware")
                        println("${event.suspiciousAppInfo.size} suspicious apps found.")
                        event.suspiciousAppInfo.forEach { appInfo ->
                            println("App: ${appInfo.packageInfo.appName}")
                        }
                        println("-------------------------------------------")
                    }
                }
            }.flowOn(Dispatchers.IO)
                .launchIn(this)


            try {
                freeraspKMP.start(freeraspConfig)
                println("freeraspKMP background monitoring started.")


                freeraspKMP.blockScreenCapture(false)
                println("freeraspKMP screen capture protection has been enabled.")

                var isBlocked = freeraspKMP.isScreenCaptureBlocked()
                println("$isBlocked")

            } catch (e: Exception) {
                println("Error starting freeraspKMP: ${e.message}")
            }
        }

        var showContent by remember { mutableStateOf(true) }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Compose: $greeting")
                }
            }
        }
    }
}