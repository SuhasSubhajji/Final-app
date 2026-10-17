package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import com.example.data.model.DateRangeFilter
import com.example.data.model.SecurityAlert
import com.example.ui.components.CyberThemeToggleButton
import com.example.ui.components.ThemeSelectorDialog
import com.example.ui.components.SeverityBadge
import com.example.ui.components.formatRelativeTime
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberTheme
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberNavyDark
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberNavySurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.SeverityCritical
import com.example.ui.viewmodel.ThreatFeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    viewModel: ThreatFeedViewModel,
    onNavigateToThreat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val alerts by viewModel.alerts.collectAsState()
    var permissionRequested by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        permissionRequested = true
        viewModel.runThreatIntelScan(context)
    }

    val theme = CyberTheme.colors
    val themeMode by viewModel.currentThemeMode.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }

    if (showThemeDialog) {
        ThemeSelectorDialog(
            currentMode = themeMode,
            onSelectMode = { viewModel.setThemeMode(it) },
            onDismiss = { showThemeDialog = false }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = theme.bg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = SeverityCritical,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Security Push Alerts",
                            color = theme.textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    CyberThemeToggleButton(
                        currentMode = themeMode,
                        onClick = { showThemeDialog = true }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Action Banner to trigger Push Alerts Scan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("run_threat_scan_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = theme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(SeverityCritical)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Actionable Push Alerts Engine",
                            color = theme.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Scans incoming feeds for Critical/High CVEs and triggers heads-up Android notifications with interactive 'Review Fix' and 'Acknowledge' buttons.",
                        color = CyberTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.runThreatIntelScan(context)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("trigger_push_scan_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SeverityCritical,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan Feeds & Trigger Alert Check", fontWeight = FontWeight.Bold)
                    }
                }
            }

            var selectedDateRange by remember { mutableStateOf(DateRangeFilter.ALL) }

            val filteredAlerts = remember(alerts, selectedDateRange) {
                if (selectedDateRange == DateRangeFilter.ALL || selectedDateRange.durationMillis == null) {
                    alerts
                } else {
                    val cutoff = System.currentTimeMillis() - selectedDateRange.durationMillis!!
                    alerts.filter { it.timestampMillis >= cutoff }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Alert History (${filteredAlerts.size})",
                    color = CyberTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                if (selectedDateRange != DateRangeFilter.ALL) {
                    Surface(
                        color = CyberCyan.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        onClick = { selectedDateRange = DateRangeFilter.ALL }
                    ) {
                        Text(
                            text = "Reset Filter",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date Range Filter Chips Bar (Observed 2 Days Ago, 1 Week, 1 Month, etc.)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DateRangeFilter.entries.forEach { range ->
                    val isSelected = selectedDateRange == range
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedDateRange = range },
                        label = {
                            Text(
                                text = when (range) {
                                    DateRangeFilter.ALL -> "All Time"
                                    DateRangeFilter.PAST_24_HOURS -> "⏱️ Past 24h"
                                    DateRangeFilter.PAST_48_HOURS -> "📅 Observed 2 Days Ago"
                                    DateRangeFilter.PAST_1_WEEK -> "🗓️ 1 Week"
                                    DateRangeFilter.PAST_1_MONTH -> "📆 1 Month"
                                    DateRangeFilter.PAST_3_MONTHS -> "⏳ 3 Months"
                                },
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                            selectedLabelColor = CyberCyan,
                            containerColor = theme.surface,
                            labelColor = CyberTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = CyberBorder,
                            selectedBorderColor = CyberCyan
                        ),
                        modifier = Modifier.testTag("alerts_date_filter_${range.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredAlerts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = CyberTextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (alerts.isEmpty()) "No Push Alerts Triggered Yet" else "No Alerts in Selected Timeframe",
                            color = CyberTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (alerts.isEmpty()) {
                                "Tap 'Trigger Critical CVE Scan' above to simulate a high-priority threat alert with notification tray actions."
                            } else {
                                "No security alert matches '${selectedDateRange.displayName}'. Try selecting 'All Time' or a broader filter."
                            },
                            color = CyberTextMuted,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("alerts_history_list"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredAlerts, key = { it.id }) { alert ->
                        AlertCard(
                            alert = alert,
                            onReviewClick = { onNavigateToThreat(alert.threatItemId) },
                            onAcknowledgeClick = { viewModel.acknowledgeAlert(alert.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertCard(
    alert: SecurityAlert,
    onReviewClick: () -> Unit,
    onAcknowledgeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("alert_item_${alert.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (!alert.isAcknowledged) SeverityCritical.copy(alpha = 0.5f) else CyberBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SeverityBadge(severity = alert.severity)

                if (alert.isAcknowledged) {
                    Surface(
                        color = CyberEmerald.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Acknowledged", color = CyberEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Surface(
                        color = SeverityCritical.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "ACTIVE ALERT",
                            color = SeverityCritical,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = alert.title,
                color = CyberTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = alert.snippet,
                color = CyberTextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatRelativeTime(alert.timestampMillis),
                    color = CyberTextMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.weight(1f)
                )

                if (!alert.isAcknowledged) {
                    OutlinedButton(
                        onClick = onAcknowledgeClick,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp).testTag("ack_button_${alert.id}")
                    ) {
                        Text("Acknowledge", fontSize = 12.sp, color = CyberEmerald)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = onReviewClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark),
                    modifier = Modifier.height(36.dp).testTag("review_fix_button_${alert.id}")
                ) {
                    Text("Review Fix", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
