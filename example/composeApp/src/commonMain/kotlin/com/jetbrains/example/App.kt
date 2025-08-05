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

import api.Talsec
import model.SuspiciousAppInfo
import model.config.AndroidConfig
import model.config.IOSConfig
import model.config.MalwareConfig
import model.config.TalsecConfig
import threat.ThreatCallback

/*class ThreatHandler(
    private val onNewThreat: (String) -> Unit
)*/
class ThreatHandler: ThreatCallback {
    override fun onHooks() {
        val threatName = "onHooks"
        println("Talsec: $threatName")
        //onNewThreat(threatName)
    }

    override fun onDebug() {
        val threatName = "onDebug"
        println("Talsec: $threatName")
        //onNewThreat(threatName)
    }

    override fun onPasscode() {
        val threatName = "onPasscode"
        println("Talsec: $threatName")
        //onNewThreat(threatName)
    }

    override fun onDeviceID() {
        val threatName = "onDeviceID"
        println("Talsec: $threatName")
        //onNewThreat(threatName)
    }

    override fun onSimulator() {
        val threatName = "onSimulator"
        println("Talsec: $threatName")
        //onNewThreat(threatName)
    }

    override fun onAppIntegrity() {
        val threatName = "onAppIntegrity"
        println("Talsec: $threatName")
        //onNewThreat(threatName)
    }

    override fun onObfuscationIssues() {
        val threatName = "onObfuscationIssues"
        println("Talsec: $threatName")
        //onNewThreat(threatName)
    }

    override fun onDeviceBinding() {
        val threatName = "onDeviceBinding"
        println("Talsec: $threatName")
        //onNewThreat(threatName)
    }

    override fun onUnofficialStore() {
        val threatName = "onUnofficialStore"
        println("Talsec: $threatName")
        //onNewThreat(threatName)
    }

    override fun onPrivilegedAccess() {
        val threatName = "onPrivilegedAccess"
        println("Talsec: $threatName")
        //onNewThreat(threatName)
    }

    override fun onSecureHardwareNotAvailable() {
        val threatName = "onSecureHardwareNotAvailable"
        println("Talsec: $threatName")
        //onNewThreat(threatName)
    }

    override fun onSystemVPN() {
        val threatName = "onSystemVPN"
        println("Talsec: $threatName")
        //onNewThreat(threatName)
    }

    override fun onDevMode() {
        val threatName = "onDevMode"
        println("Talsec: $threatName")
        //onNewThreat(threatName)
    }

    override fun onADBEnabled() {
        val threatName = "onADBEnabled"
        println("Talsec: $threatName")
        //onNewThreat(threatName)
        }

    override fun onMalwareDetected(suspiciousAppInfo: List<SuspiciousAppInfo>) {
        val threatName = "onMalwareDetected"
        println("-------------------------------------------")
        println("Talsec: $threatName")
        println("${suspiciousAppInfo.size} suspicious apps found.")

        suspiciousAppInfo.forEach { appInfo ->
            println("App: ${appInfo.packageInfo.appName}")
        }
        println("-------------------------------------------")
        //onNewThreat(threatName)
    }

    override fun onScreenshot() {
        val threatName = "onScreenshot"
        println("Talsec: $threatName")
        //onNewThreat(threatName)
    }

    override fun onScreenRecording() {
        val threatName = "onScreenRecording"
        println("Talsec: $threatName")
        //onNewThreat(threatName)
    }

    override fun onMultiInstance() {
        val threatName = "onMultiInstance"
        println("Talsec: $threatName")
        //onNewThreat(threatName)
    }

}

    @Composable
@Preview
fun App() {
    MaterialTheme {

        val talsecConfig = remember {
            TalsecConfig(
                watcherMail = "martin.zigrai@gmail.com",
                isProd = true,
                androidConfig = AndroidConfig(
                    packageName = "com.jetbrains.example",
                    signingCertHashes = listOf("K/iFV7+CypnATFWcrUVM6aUIB5gnU2xwzRJOiKJJqPw="),
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
            Talsec.attachListener(threatHandler)
            try {
                Talsec.start(talsecConfig)
                println("Talsec background monitoring started.")

                /*
                Talsec.blockScreenCapture(false)
                println("Talsec screen capture protection has been enabled.")

                var isBlocked = Talsec.isScreenCaptureBlocked()
                println("$isBlocked")*/

            } catch (e: Exception) {
                println("Error starting Talsec: ${e.message}")
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