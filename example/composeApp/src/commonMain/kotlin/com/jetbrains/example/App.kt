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
import com.freeraspkmp.api.FreeraspKMP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.freeraspkmp.model.FreeRaspEvent
import com.freeraspkmp.model.config.AndroidConfig
import com.freeraspkmp.model.config.IOSConfig
import com.freeraspkmp.model.config.MalwareConfig
import com.freeraspkmp.model.config.freeraspConfig
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val freeraspConfig = remember {
            freeraspConfig(
                watcherMail = "test@mail.app",
                isProd = true,
                killOnBypass = true,
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
            FreeraspKMP.threatEvents.onEach { event ->
                when (event) {
                    is FreeRaspEvent.PrivilegedAccess -> println("freeraspKMP: PrivilegedAccess")
                    is FreeRaspEvent.Debug -> println("freeraspKMP: Debug")
                    is FreeRaspEvent.Simulator -> println("freeraspKMP: Simulator")
                    is FreeRaspEvent.AppIntegrity -> println("freeraspKMP: AppIntegrity")
                    is FreeRaspEvent.UnofficialStore -> println("freeraspKMP: UnofficialStore")
                    is FreeRaspEvent.Hooks -> println("freeraspKMP: Hooks")
                    is FreeRaspEvent.DeviceBinding -> println("freeraspKMP: DeviceBinding")
                    is FreeRaspEvent.ObfuscationIssues -> println("freeraspKMP: ObfuscationIssues")
                    is FreeRaspEvent.Screenshot -> println("freeraspKMP: Screenshot")
                    is FreeRaspEvent.ScreenRecording -> println("freeraspKMP: ScreenRecording")
                    is FreeRaspEvent.Passcode -> println("freeraspKMP: Passcode")
                    is FreeRaspEvent.SecureHardwareNotAvailable -> println("freeraspKMP: SecureHardwareNotAvailable")
                    is FreeRaspEvent.SystemVPN -> println("freeraspKMP: SystemVPN")
                    is FreeRaspEvent.DevMode -> println("freeraspKMP: DevMode")
                    is FreeRaspEvent.AdbEnabled -> println("freeraspKMP: AdbEnabled")
                    is FreeRaspEvent.MultiInstance -> println("freeraspKMP: MultiInstance")
                    is FreeRaspEvent.DeviceID -> println("freeraspKMP: DeviceID")
                    is FreeRaspEvent.Malware -> {
                        println("-------------------------------------------")
                        println("freeraspKMP: Malware")
                        println("${event.suspiciousAppInfo.size} suspicious apps found.")
                        event.suspiciousAppInfo.forEach { appInfo ->
                            println("App: ${appInfo.packageInfo.appName}")
                        }
                        println("-------------------------------------------")
                    }
                    is FreeRaspEvent.TimeSpoofing -> println("freeraspKMP: TimeSpoofing")
                    is FreeRaspEvent.UnsecureWifi -> println("freeraspKMP: UnsecureWifi")
                    is FreeRaspEvent.LocationSpoofing -> println("freeraspKMP: LocationSpoofing")
                    is FreeRaspEvent.AllChecksFinished -> println("freeraspKMP: AllChecksFinished")
                }
            }.flowOn(Dispatchers.IO)
                .launchIn(this)


            try {
                FreeraspKMP.start(freeraspConfig)
                println("freeraspKMP background monitoring started.")


                FreeraspKMP.blockScreenCapture(true)
                println("freeraspKMP screen capture protection has been enabled.")

                var isBlocked = FreeraspKMP.isScreenCaptureBlocked()
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