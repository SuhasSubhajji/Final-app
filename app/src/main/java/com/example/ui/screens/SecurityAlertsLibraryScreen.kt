package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.learning.SecurityAlertExample
import com.example.ui.theme.*
import com.example.ui.viewmodel.ThreatFeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityAlertsLibraryScreen(
    viewModel: ThreatFeedViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alerts by viewModel.securityAlertExamples.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredAlerts = remember(alerts, searchQuery) {
        if (searchQuery.isBlank()) {
            alerts
        } else {
            alerts.filter { 
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true) ||
                it.mitreTactic.orEmpty().contains(searchQuery, ignoreCase = true) ||
                it.mitreTechniqueId.orEmpty().contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberTheme.colors.bg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Security Alerts Library",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTheme.colors.textPrimary
                        )
                        Text(
                            "Investigation & Remediation Guide",
                            fontSize = 11.sp,
                            color = CyberTheme.colors.textSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CyberTheme.colors.textPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberTheme.colors.bg)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Search Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CyberTheme.colors.bg,
                shadowElevation = 4.dp
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search alerts, tactics, techniques...", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("alerts_library_search"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CyberTheme.colors.border,
                        focusedContainerColor = CyberTheme.colors.surface,
                        unfocusedContainerColor = CyberTheme.colors.surface
                    )
                )
            }

            if (filteredAlerts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.History, 
                            contentDescription = null, 
                            modifier = Modifier.size(48.dp), 
                            tint = CyberTextMuted
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No matching alerts found", color = CyberTextMuted)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            "Showing ${filteredAlerts.size} curated security alert scenarios",
                            fontSize = 12.sp,
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(filteredAlerts, key = { it.id }) { alert ->
                        SecurityAlertLibraryItem(alert = alert)
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityAlertLibraryItem(alert: SecurityAlertExample) {
    var expanded by remember { mutableStateOf(false) }
    val severityColor = when (alert.severity.lowercase()) {
        "critical" -> SeverityCritical
        "high" -> SeverityHigh
        "medium" -> SeverityMedium
        else -> CyberCyan
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("alert_item_${alert.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberTheme.colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (expanded) CyberCyan.copy(alpha = 0.5f) else CyberTheme.colors.border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(severityColor, shape = RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = alert.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTheme.colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = CyberTheme.colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = alert.severity.uppercase(),
                    color = severityColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "|",
                    color = CyberTheme.colors.border,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = alert.source,
                    color = CyberTheme.colors.textSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    HorizontalDivider(color = CyberTheme.colors.border, modifier = Modifier.padding(bottom = 16.dp))
                    
                    SectionTitle("Description")
                    Text(alert.description, fontSize = 13.sp, color = CyberTheme.colors.textSecondary)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SectionTitle("Detection Logic")
                    Surface(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = alert.detectionLogic,
                            modifier = Modifier.padding(12.dp),
                            color = CyberEmerald,
                            fontSize = 12.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            SectionTitle("Investigation Steps")
                            alert.investigationSteps.forEachIndexed { index, step ->
                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text("${index + 1}. ", fontSize = 12.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                                    Text(step, fontSize = 12.sp, color = CyberTheme.colors.textSecondary)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            SectionTitle("Remediation")
                            alert.remediationSteps.forEachIndexed { index, step ->
                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text("• ", fontSize = 12.sp, color = SeverityHigh, fontWeight = FontWeight.Bold)
                                    Text(step, fontSize = 12.sp, color = CyberTheme.colors.textSecondary)
                                }
                            }
                        }
                    }

                    if (alert.mitreTactic != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "MITRE ATT&CK: ${alert.mitreTactic} (${alert.mitreTechniqueId})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                        }
                    }
                    
                    if (alert.logsNeeded.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Required Logs: ${alert.logsNeeded.joinToString(", ")}",
                            fontSize = 10.sp,
                            color = CyberTheme.colors.textSecondary,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = CyberCyan,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}
