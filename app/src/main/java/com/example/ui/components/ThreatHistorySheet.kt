package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ThreatHistoryItem
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberNavyDark
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberNavySurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.SeverityHigh
import com.example.ui.theme.SeverityMedium
import com.example.ui.viewmodel.ThreatFeedViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreatHistorySheet(
    viewModel: ThreatFeedViewModel,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onThreatClick: (String) -> Unit
) {
    val context = LocalContext.current
    val historyItems by viewModel.threatHistory.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("ALL") } // ALL, INVESTIGATING, RESOLVED, ESCALATED, FALSE_POSITIVE
    var showClearDialog by remember { mutableStateOf(false) }

    var editingItem by remember { mutableStateOf<ThreatHistoryItem?>(null) }
    var editingStatus by remember { mutableStateOf("") }
    var editingNotes by remember { mutableStateOf("") }

    val filteredItems = remember(historyItems, searchQuery, statusFilter) {
        historyItems.filter { item ->
            val matchesSearch = if (searchQuery.isBlank()) true else {
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.source.displayName.contains(searchQuery, ignoreCase = true) ||
                item.category.displayName.contains(searchQuery, ignoreCase = true) ||
                item.analystNotes.contains(searchQuery, ignoreCase = true)
            }
            val matchesStatus = if (statusFilter == "ALL") true else {
                item.triageStatus.equals(statusFilter, ignoreCase = true)
            }
            matchesSearch && matchesStatus
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All Threat History?", color = CyberTextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently remove all investigation audit logs and history entries.", color = CyberTextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SeverityCritical)
                ) {
                    Text("Clear All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = CyberTextMuted)
                }
            },
            containerColor = CyberNavySurface
        )
    }

    // Edit Status & Notes Dialog
    if (editingItem != null) {
        val target = editingItem!!
        AlertDialog(
            onDismissRequest = { editingItem = null },
            title = {
                Text(
                    text = "Update Triage Status",
                    color = CyberTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = target.title,
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2
                    )

                    Text("Triage Status:", color = CyberTextSecondary, fontSize = 12.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("INVESTIGATING", "RESOLVED", "ESCALATED", "FALSE_POSITIVE").forEach { st ->
                            val isSel = editingStatus == st
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) CyberCyan.copy(alpha = 0.25f) else CyberNavySurfaceVariant,
                                border = BorderStroke(1.dp, if (isSel) CyberCyan else CyberBorder),
                                modifier = Modifier.clickable { editingStatus = st }
                            ) {
                                Text(
                                    text = st.replace("_", " "),
                                    color = if (isSel) CyberCyan else CyberTextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = editingNotes,
                        onValueChange = { editingNotes = it },
                        label = { Text("Analyst Notes") },
                        placeholder = { Text("Add investigation observations...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberBorder,
                            focusedContainerColor = CyberNavyDark,
                            unfocusedContainerColor = CyberNavyDark,
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateHistoryItem(target.id, editingStatus, editingNotes)
                        editingItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark)
                ) {
                    Text("Save Triage", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingItem = null }) {
                    Text("Cancel", color = CyberTextMuted)
                }
            },
            containerColor = CyberNavySurface
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CyberNavySurface,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(width = 38.dp, height = 4.dp),
                shape = CircleShape,
                color = CyberBorder
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CyberCyan.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Threat Investigation History",
                            color = CyberTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${historyItems.size} Audit Events Logged",
                            color = CyberTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Export summary button
                    IconButton(
                        onClick = {
                            if (historyItems.isNotEmpty()) {
                                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
                                val text = buildString {
                                    appendLine("=== SOC THREAT INVESTIGATION AUDIT LOG ===")
                                    appendLine("Exported: ${sdf.format(Date())}")
                                    appendLine("Total Events: ${historyItems.size}")
                                    appendLine()
                                    historyItems.forEachIndexed { i, item ->
                                        appendLine("[${i + 1}] ${sdf.format(Date(item.timestampMillis))} | ${item.severity.name} | ${item.triageStatus}")
                                        appendLine("Title: ${item.title}")
                                        appendLine("Source: ${item.source.displayName} | Action: ${item.actionType}")
                                        if (item.analystNotes.isNotBlank()) {
                                            appendLine("Notes: ${item.analystNotes}")
                                        }
                                        appendLine("---")
                                    }
                                }
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Threat History", text))
                                Toast.makeText(context, "Full history exported to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Export History",
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Clear history button
                    if (historyItems.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear History",
                                tint = SeverityCritical.copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CyberTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics Summary Row
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = CyberNavyDark,
                border = BorderStroke(1.dp, CyberBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val investigatingCount = historyItems.count { it.triageStatus == "INVESTIGATING" }
                    val resolvedCount = historyItems.count { it.triageStatus == "RESOLVED" }
                    val escalatedCount = historyItems.count { it.triageStatus == "ESCALATED" }

                    HistoryMetricPill("Total", historyItems.size.toString(), CyberCyan)
                    HistoryMetricPill("Investigating", investigatingCount.toString(), SeverityHigh)
                    HistoryMetricPill("Resolved", resolvedCount.toString(), CyberEmerald)
                    HistoryMetricPill("Escalated", escalatedCount.toString(), SeverityCritical)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search threats, sources, or notes...", color = CyberTextMuted, fontSize = 12.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = CyberTextMuted, modifier = Modifier.size(14.dp))
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_search_input"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = CyberBorder,
                    focusedContainerColor = CyberNavyDark,
                    unfocusedContainerColor = CyberNavyDark,
                    focusedTextColor = CyberTextPrimary,
                    unfocusedTextColor = CyberTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val filters = listOf(
                    "ALL" to "All Events",
                    "INVESTIGATING" to "🔍 Investigating",
                    "RESOLVED" to "✅ Resolved",
                    "ESCALATED" to "🚨 Escalated",
                    "FALSE_POSITIVE" to "⚪ False Positive"
                )
                filters.forEach { (key, label) ->
                    val isSel = statusFilter == key
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) CyberCyan.copy(alpha = 0.2f) else CyberNavyDark,
                        border = BorderStroke(1.dp, if (isSel) CyberCyan else CyberBorder),
                        modifier = Modifier.clickable { statusFilter = key }
                    ) {
                        Text(
                            text = label,
                            color = if (isSel) CyberCyan else CyberTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // History List
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = CyberTextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || statusFilter != "ALL") "No history matching filter" else "No investigation history yet",
                            color = CyberTextSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Threats investigated, analyzed, or bookmarked will appear here.",
                            color = CyberTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("threat_history_list"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        HistoryItemCard(
                            item = item,
                            onClick = {
                                onDismiss()
                                onThreatClick(item.threatId)
                            },
                            onEdit = {
                                editingItem = item
                                editingStatus = item.triageStatus
                                editingNotes = item.analystNotes
                            },
                            onDelete = {
                                viewModel.deleteHistoryItem(item.id)
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryMetricPill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = CyberTextMuted, fontSize = 10.sp)
    }
}

@Composable
private fun HistoryItemCard(
    item: ThreatHistoryItem,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = when (item.triageStatus.uppercase()) {
        "RESOLVED" -> CyberEmerald
        "ESCALATED" -> SeverityCritical
        "FALSE_POSITIVE" -> CyberTextMuted
        else -> SeverityHigh
    }

    val actionIcon = when (item.actionType.uppercase()) {
        "AI_ANALYSIS" -> Icons.Default.AutoAwesome
        "BOOKMARKED" -> Icons.Default.Bookmark
        "REMEDIATION_COPIED" -> Icons.Default.Code
        "STATUS_UPDATED" -> Icons.Default.CheckCircle
        else -> Icons.Default.Security
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CyberNavyDark,
        border = BorderStroke(1.dp, CyberBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: Action badge, timestamp, delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = CyberCyan.copy(alpha = 0.15f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(imageVector = actionIcon, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = item.actionType.replace("_", " "),
                                color = CyberCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = formatRelativeTime(item.timestampMillis),
                        color = CyberTextMuted,
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete",
                        tint = CyberTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title
            Text(
                text = item.title,
                color = CyberTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 17.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Source & Severity
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.source.displayName,
                    color = CyberTextSecondary,
                    fontSize = 11.sp
                )
                Text("•", color = CyberTextMuted, fontSize = 11.sp)
                Text(
                    text = item.severity.name,
                    color = item.severity.color,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (item.analystNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyberNavySurfaceVariant,
                    border = BorderStroke(1.dp, CyberBorder.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📝 ${item.analystNotes}",
                        color = CyberTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Status Bar & Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = item.triageStatus.replace("_", " "),
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, CyberBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Triage", fontSize = 10.sp)
                    }

                    OutlinedButton(
                        onClick = onClick,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text("View Threat", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(11.dp))
                    }
                }
            }
        }
    }
}
