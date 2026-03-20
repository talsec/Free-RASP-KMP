package com.jetbrains.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.freeraspkmp.model.SuspiciousAppInfo
import com.jetbrains.example.model.SecurityCheck
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityDashboard(
    checks: List<SecurityCheck>,
    allChecksFinished: Boolean,
    isScreenCaptureBlocked: Boolean,
    malwareApps: List<SuspiciousAppInfo>,
    onToggleScreenCapture: () -> Unit,
    onStoreExternalId: (id: String, onResult: (message: String, isSuccess: Boolean) -> Unit) -> Unit,
    onRemoveExternalId: (onDone: () -> Unit) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarScope = rememberCoroutineScope()
    val showFeedback: (String, FeedbackType) -> Unit = { message, type ->
        snackbarScope.launch {
            snackbarHostState.showSnackbar(FeedbackSnackbarVisuals(message = message, type = type))
        }
    }

    var showMalwareSheet by remember { mutableStateOf(false) }
    var externalIdValue by remember { mutableStateOf("") }
    var externalIdExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(malwareApps) {
        if (malwareApps.isNotEmpty()) showMalwareSheet = true
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val visuals = data.visuals as? FeedbackSnackbarVisuals
                if (visuals != null) {
                    FeedbackSnackbar(visuals = visuals)
                } else {
                    Snackbar(data)
                }
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                            modifier = Modifier.size(26.dp),
                        )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Light)) {
                                    append("free")
                                }
                                withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                                    append("RASP")
                                }
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            item {
                StatusCard(checks = checks, allChecksFinished = allChecksFinished)
            }
            item {
                ScreenCaptureCard(
                    isBlocked = isScreenCaptureBlocked,
                    onToggle = onToggleScreenCapture,
                )
            }
            item {
                ExternalIdCard(
                    expanded = externalIdExpanded,
                    onExpandToggle = { externalIdExpanded = !externalIdExpanded },
                    value = externalIdValue,
                    onValueChange = { externalIdValue = it },
                    onStore = {
                        if (externalIdValue.isNotBlank()) {
                            onStoreExternalId(externalIdValue) { message, isSuccess ->
                                showFeedback(
                                    message,
                                    if (isSuccess) FeedbackType.Success else FeedbackType.Error,
                                )
                            }
                        }
                    },
                    onRemove = {
                        onRemoveExternalId {
                            externalIdValue = ""
                            showFeedback("External ID removed", FeedbackType.Success)
                        }
                    },
                )
            }
            item {
                Text(
                    text = "Security Checks",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            items(checks, key = { it.id }) { check ->
                SecurityCheckRow(check = check)
            }
        }
    }

    if (showMalwareSheet) {
        MalwareBottomSheet(
            apps = malwareApps,
            onDismiss = { showMalwareSheet = false },
        )
    }
}

@Composable
private fun StatusCard(
    checks: List<SecurityCheck>,
    allChecksFinished: Boolean,
) {
    val detectedCount = checks.count { it.isDetected }
    val isAllSafe = detectedCount == 0

    val containerColor by animateColorAsState(
        targetValue = when {
            !allChecksFinished -> PendingGrayContainer
            isAllSafe -> SafeGreenContainer
            else -> ThreatRedContainer
        },
        animationSpec = tween(500),
        label = "statusContainerColor",
    )
    val contentColor = when {
        !allChecksFinished -> PendingGray
        isAllSafe -> SafeGreen
        else -> ThreatRed
    }
    val icon: ImageVector = when {
        !allChecksFinished -> Icons.Default.Security
        isAllSafe -> Icons.Default.VerifiedUser
        else -> Icons.Default.GppBad
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(48.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = when {
                        !allChecksFinished -> "Running checks\u2026"
                        isAllSafe -> "All Secure"
                        else -> "$detectedCount Threat${if (detectedCount > 1) "s" else ""} Detected"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                )
                Text(
                    text = when {
                        !allChecksFinished -> "Security checks in progress"
                        isAllSafe -> "No threats found"
                        else -> "Review the checks below"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.8f),
                )
            }
        }
    }
}

@Composable
private fun ScreenCaptureCard(
    isBlocked: Boolean,
    onToggle: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = if (isBlocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = if (isBlocked) SafeGreen else ThreatRed,
                )
                Column {
                    Text(
                        text = "Screen Capture",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = if (isBlocked) "Protected" else "Unprotected",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isBlocked) SafeGreen else ThreatRed,
                    )
                }
            }
            Switch(
                checked = isBlocked,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = SafeGreen,
                ),
            )
        }
    }
}

@Composable
private fun ExternalIdCard(
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    onStore: () -> Unit,
    onRemove: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandToggle() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.VpnKey,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "External ID",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (expanded) {
                HorizontalDivider()
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        label = { Text("External ID") },
                        placeholder = { Text("Enter external ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onStore() }),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(
                            onClick = onStore,
                            modifier = Modifier.weight(1f),
                            enabled = value.isNotBlank(),
                        ) {
                            Text("Store")
                        }
                        OutlinedButton(
                            onClick = onRemove,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Remove")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedbackSnackbar(visuals: FeedbackSnackbarVisuals) {
    val isSuccess = visuals.type == FeedbackType.Success
    val bgColor = if (isSuccess) SafeGreen else ThreatRed
    val icon = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = visuals.message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SecurityCheckRow(check: SecurityCheck) {
    val containerColor = if (check.isDetected) ThreatRedContainer else MaterialTheme.colorScheme.surface
    val iconTint = if (check.isDetected) ThreatRed else SafeGreen
    val icon: ImageVector = if (check.isDetected) Icons.Default.Warning else Icons.Default.CheckCircle
    val statusText = if (check.isDetected) "Threat" else "Safe"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = check.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = check.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = iconTint,
            )
        }
    }
}
