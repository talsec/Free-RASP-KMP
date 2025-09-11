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
import org.jetbrains.compose.ui.tooling.preview.Preview

import api.freeraspKMP
import model.SuspiciousAppInfo
import model.config.AndroidConfig
import model.config.IOSConfig
import model.config.MalwareConfig
import model.config.freeraspConfig
import threat.ThreatCallback

/*class ThreatHandler(
    private val onNewThreat: (String) -> Unit
)*/
class ThreatHandler: ThreatCallback {
    override fun onHooks() {
        val threatName = "onHooks"
        println("freeraspKMP: $threatName")
        //onNewThreat(threatName)
    }

    override fun onDebug() {
        val threatName = "onDebug"
        println("freeraspKMP: $threatName")
        //onNewThreat(threatName)
    }

    override fun onPasscode() {
        val threatName = "onPasscode"
        println("freeraspKMP: $threatName")
        //onNewThreat(threatName)
    }

    override fun onDeviceID() {
        val threatName = "onDeviceID"
        println("freeraspKMP: $threatName")
        //onNewThreat(threatName)
    }

    override fun onSimulator() {
        val threatName = "onSimulator"
        println("freeraspKMP: $threatName")
        //onNewThreat(threatName)
    }

    override fun onAppIntegrity() {
        val threatName = "onAppIntegrity"
        println("freeraspKMP: $threatName")
        //onNewThreat(threatName)
    }

    override fun onObfuscationIssues() {
        val threatName = "onObfuscationIssues"
        println("freeraspKMP: $threatName")
        //onNewThreat(threatName)
    }

    override fun onDeviceBinding() {
        val threatName = "onDeviceBinding"
        println("freeraspKMP: $threatName")
        //onNewThreat(threatName)
    }

    override fun onUnofficialStore() {
        val threatName = "onUnofficialStore"
        println("freeraspKMP: $threatName")
        //onNewThreat(threatName)
    }

    override fun onPrivilegedAccess() {
        val threatName = "onPrivilegedAccess"
        println("freeraspKMP: $threatName")
        //onNewThreat(threatName)
    }

    override fun onSecureHardwareNotAvailable() {
        val threatName = "onSecureHardwareNotAvailable"
        println("freeraspKMP: $threatName")
        //onNewThreat(threatName)
    }

    override fun onSystemVPN() {
        val threatName = "onSystemVPN"
        println("freeraspKMP: $threatName")
        //onNewThreat(threatName)
    }

    override fun onDevMode() {
        val threatName = "onDevMode"
        println("freeraspKMP: $threatName")
        //onNewThreat(threatName)
    }

    override fun onADBEnabled() {
        val threatName = "onADBEnabled"
        println("freeraspKMP: $threatName")
        //onNewThreat(threatName)
        }

    override fun onMalwareDetected(suspiciousAppInfo: List<SuspiciousAppInfo>) {
        val threatName = "onMalwareDetected"
        println("-------------------------------------------")
        println("freeraspKMP: $threatName")
        println("${suspiciousAppInfo.size} suspicious apps found.")

        suspiciousAppInfo.forEach { appInfo ->
            println("App: ${appInfo.packageInfo.appName}")
        }
        println("-------------------------------------------")
        //onNewThreat(threatName)
    }

    override fun onScreenshot() {
        val threatName = "onScreenshot"
        println("freeraspKMP: $threatName")
        //onNewThreat(threatName)
    }

    override fun onScreenRecording() {
        val threatName = "onScreenRecording"
        println("freeraspKMP: $threatName")
        //onNewThreat(threatName)
    }

    override fun onMultiInstance() {
        val threatName = "onMultiInstance"
        println("freeraspKMP: $threatName")
        //onNewThreat(threatName)
    }
}

    @Composable
@Preview
fun App() {
    MaterialTheme {

        val freeraspConfig = remember {
            freeraspConfig(
                watcherMail = "martin.zigrai@gmail.com",
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

        var threatHandler = remember { ThreatHandler() }


        LaunchedEffect(Unit) {
            freeraspKMP.attachListener(threatHandler)
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