package com.jetbrains.example.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class FeedbackType { Success, Error }

data class FeedbackSnackbarVisuals(
    override val message: String,
    val type: FeedbackType,
    override val actionLabel: String? = null,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    override val withDismissAction: Boolean = false,
) : SnackbarVisuals

val SafeGreen = Color(0xFF2E7D32)
val SafeGreenContainer = Color(0xFFE8F5E9)
val ThreatRed = Color(0xFFB71C1C)
val ThreatRedContainer = Color(0xFFFFEBEE)
val PendingGray = Color(0xFF616161)
val PendingGrayContainer = Color(0xFFF5F5F5)

private val FreeraspColorScheme = lightColorScheme(
    primary = Color(0xFF7F52FF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE7FF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF5383EC),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE3EEFF),
    onSecondaryContainer = Color(0xFF001849),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
)

@Composable
fun FreeraspTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FreeraspColorScheme,
        content = content,
    )
}
