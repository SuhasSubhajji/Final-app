package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.mitre.MitreMatrixRepository
import com.example.data.mitre.MitreTactic
import com.example.data.mitre.MitreTtpItem
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberNavyDark
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberNavySurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberTheme
import com.example.ui.theme.SeverityCritical

enum class MitreGraphViewMode(val label: String, val iconEmoji: String) {
    ATTACK("ATT&CK Graph", "⚔️"),
    DEFEND("D3FEND Graph", "🛡️"),
    UNIFIED("Unified Flow", "🔗"),
    D3_CANVAS("D3.js Flow", "🌐")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MitreInteractiveGraphComposable(
    initialMode: String = "ATTACK",
    progressMap: Map<String, String> = emptyMap(),
    onUpdateProgress: (String, String) -> Unit = { _, _ -> },
    onClose: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val theme = CyberTheme.colors
    var selectedViewMode by remember {
        mutableStateOf(
            when (initialMode) {
                "DEFEND" -> MitreGraphViewMode.DEFEND
                "UNIFIED" -> MitreGraphViewMode.UNIFIED
                else -> MitreGraphViewMode.ATTACK
            }
        )
    }
    var selectedTacticFilter by remember { mutableStateOf<MitreTactic?>(null) }
    var selectedTtpForInspection by remember { mutableStateOf<MitreTtpItem?>(null) }

    val allTtps = remember { MitreMatrixRepository.allTtps }
    val filteredTtps = remember(selectedTacticFilter) {
        if (selectedTacticFilter == null) allTtps else allTtps.filter { it.tactic == selectedTacticFilter }
    }

    val studiedCount = remember(progressMap) { allTtps.count { progressMap[it.techniqueId] == "STUDIED" } }
    val masteredCount = remember(progressMap) { allTtps.count { progressMap[it.techniqueId] == "MASTERED" } }
    val totalCount = allTtps.size
    val progressRatio = (studiedCount + masteredCount).toFloat() / totalCount.coerceAtLeast(1)

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.bg)
    ) {
        // --- Header Controls & Progress Bar ---
        Surface(
            color = theme.surface,
            tonalElevation = 4.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, theme.border)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${selectedViewMode.iconEmoji} ${selectedViewMode.label}",
                            color = theme.textPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = theme.accentPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${filteredTtps.size} TTPs",
                                color = theme.accentPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (onClose != null) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.testTag("close_mitre_graph_dialog_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Graph",
                                tint = theme.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Tracker Banner
                Surface(
                    color = CyberNavyDark,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🏆 Learning Mastery: $masteredCount Mastered • $studiedCount Studied",
                                color = CyberEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${(progressRatio * 100).toInt()}% Complete",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progressRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = CyberEmerald,
                            trackColor = CyberNavySurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Mode Selector Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(theme.surfaceVariant, RoundedCornerShape(10.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MitreGraphViewMode.entries.forEach { mode ->
                        val selected = selectedViewMode == mode
                        val accentColor = when (mode) {
                            MitreGraphViewMode.ATTACK -> SeverityCritical
                            MitreGraphViewMode.DEFEND -> CyberEmerald
                            MitreGraphViewMode.UNIFIED -> CyberCyan
                            MitreGraphViewMode.D3_CANVAS -> Color(0xFFA855F7)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) accentColor.copy(alpha = 0.2f) else Color.Transparent)
                                .border(1.dp, if (selected) accentColor else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable { selectedViewMode = mode }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${mode.iconEmoji} ${mode.label}",
                                color = if (selected) accentColor else theme.textSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tactic Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedTacticFilter == null,
                        onClick = { selectedTacticFilter = null },
                        label = { Text("ALL 14 TACTICS", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = theme.accentPrimary.copy(alpha = 0.2f),
                            selectedLabelColor = theme.accentPrimary
                        ),
                        modifier = Modifier.height(28.dp)
                    )

                    MitreTactic.entries.forEach { tactic ->
                        val selected = selectedTacticFilter == tactic
                        FilterChip(
                            selected = selected,
                            onClick = { selectedTacticFilter = if (selected) null else tactic },
                            label = { Text(tactic.displayName, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = tactic.color.copy(alpha = 0.25f),
                                selectedLabelColor = tactic.color
                            ),
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }
            }
        }

        // --- Main Visual Graph / D3 Canvas Area ---
        if (selectedViewMode == MitreGraphViewMode.D3_CANVAS) {
            com.example.ui.screens.MitreFlowChartWebView(
                initialFocusMode = when (initialMode) {
                    "DEFEND" -> "DEFEND"
                    "UNIFIED" -> "ALL"
                    else -> "ATTACK"
                },
                onTechniqueSelected = { techId ->
                    val found = allTtps.firstOrNull { it.techniqueId == techId }
                    if (found != null) {
                        selectedTtpForInspection = found
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTtps, key = { it.techniqueId }) { ttp ->
                    val currentStatus = progressMap[ttp.techniqueId] ?: "UNSTARTED"
                    MitreGraphTtpCard(
                        ttp = ttp,
                        viewMode = selectedViewMode,
                        status = currentStatus,
                        onUpdateStatus = { newStatus -> onUpdateProgress(ttp.techniqueId, newStatus) },
                        onClick = { selectedTtpForInspection = ttp }
                    )
                }
            }
        }
    }

    // --- Modal Inspection Bottom Sheet ---
    if (selectedTtpForInspection != null) {
        val ttp = selectedTtpForInspection!!
        val currentStatus = progressMap[ttp.techniqueId] ?: "UNSTARTED"

        ModalBottomSheet(
            onDismissRequest = { selectedTtpForInspection = null },
            sheetState = sheetState,
            containerColor = theme.surface,
            scrimColor = Color.Black.copy(alpha = 0.6f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header TTP Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ttp.tactic.color.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ttp.tactic.color)
                            ) {
                                Text(
                                    text = "${ttp.tactic.tacticId} • ${ttp.tactic.displayName}",
                                    color = ttp.tactic.color,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${ttp.techniqueId}: ${ttp.name}",
                            color = theme.textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp
                        )
                    }
                    IconButton(onClick = { selectedTtpForInspection = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = theme.textMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive Progress Toggle Bar
                Surface(
                    color = CyberNavyDark,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Technique Study Status", color = CyberTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = when (currentStatus) {
                                    "MASTERED" -> "🏆 Mastered"
                                    "STUDIED" -> "📖 Studied"
                                    else -> "⚪ Unstudied"
                                },
                                color = when (currentStatus) {
                                    "MASTERED" -> CyberEmerald
                                    "STUDIED" -> CyberCyan
                                    else -> CyberTextPrimary
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = currentStatus == "STUDIED",
                                onClick = {
                                    val next = if (currentStatus == "STUDIED") "UNSTARTED" else "STUDIED"
                                    onUpdateProgress(ttp.techniqueId, next)
                                    Toast.makeText(context, "Status: $next", Toast.LENGTH_SHORT).show()
                                },
                                label = { Text("📖 Studied", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                                    selectedLabelColor = CyberCyan
                                )
                            )

                            FilterChip(
                                selected = currentStatus == "MASTERED",
                                onClick = {
                                    val next = if (currentStatus == "MASTERED") "UNSTARTED" else "MASTERED"
                                    onUpdateProgress(ttp.techniqueId, next)
                                    Toast.makeText(context, "Status: $next", Toast.LENGTH_SHORT).show()
                                },
                                label = { Text("🏆 Mastered", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberEmerald.copy(alpha = 0.25f),
                                    selectedLabelColor = CyberEmerald
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = ttp.description,
                    color = theme.textSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- ⚔️ OFFENSIVE ATT&CK SECTION ---
                Surface(
                    color = CyberNavyDark,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SeverityCritical.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Terminal, contentDescription = null, tint = SeverityCritical, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("⚔️ Red Team Offensive TTP Command", color = SeverityCritical, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Attack Command", ttp.attackCodeCommand))
                                    Toast.makeText(context, "Copied Attack Command!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Command", tint = SeverityCritical, modifier = Modifier.size(16.dp))
                            }
                        }

                        Text(ttp.attackExampleTitle, color = theme.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 4.dp))

                        Surface(
                            color = Color(0xFF070B14),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.border),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Text(
                                text = ttp.attackCodeCommand.trimIndent(),
                                color = Color(0xFFF8FAFC),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // --- 🛡️ DEFENSIVE D3FEND SECTION ---
                Surface(
                    color = CyberNavyDark,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("🛡️ D3FEND: ${ttp.d3fendId} ${ttp.d3fendName}", color = CyberEmerald, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("D3FEND Rule", ttp.d3fendRuleOrFix))
                                    Toast.makeText(context, "Copied Defensive Rule!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Rule", tint = CyberEmerald, modifier = Modifier.size(16.dp))
                            }
                        }

                        Text(ttp.d3fendDescription, color = theme.textSecondary, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))

                        Surface(
                            color = Color(0xFF070B14),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.border),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Text(
                                text = ttp.d3fendRuleOrFix.trimIndent(),
                                color = CyberEmerald,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun MitreGraphTtpCard(
    ttp: MitreTtpItem,
    viewMode: MitreGraphViewMode,
    status: String,
    onUpdateStatus: (String) -> Unit,
    onClick: () -> Unit
) {
    val theme = CyberTheme.colors

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("mitre_graph_ttp_card_${ttp.techniqueId}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when (status) {
                "MASTERED" -> CyberEmerald.copy(alpha = 0.6f)
                "STUDIED" -> CyberCyan.copy(alpha = 0.6f)
                else -> when (viewMode) {
                    MitreGraphViewMode.ATTACK -> SeverityCritical.copy(alpha = 0.4f)
                    MitreGraphViewMode.DEFEND -> CyberEmerald.copy(alpha = 0.4f)
                    MitreGraphViewMode.UNIFIED -> CyberCyan.copy(alpha = 0.4f)
                    MitreGraphViewMode.D3_CANVAS -> Color(0xFFA855F7).copy(alpha = 0.4f)
                }
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Tactic, TTP ID, Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = ttp.tactic.color.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, ttp.tactic.color)
                    ) {
                        Text(
                            text = ttp.tactic.displayName,
                            color = ttp.tactic.color,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = ttp.techniqueId,
                        color = SeverityCritical,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Status Badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (status) {
                            "MASTERED" -> CyberEmerald.copy(alpha = 0.2f)
                            "STUDIED" -> CyberCyan.copy(alpha = 0.2f)
                            else -> theme.surfaceVariant
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            0.6.dp,
                            when (status) {
                                "MASTERED" -> CyberEmerald
                                "STUDIED" -> CyberCyan
                                else -> theme.border
                            }
                        )
                    ) {
                        Text(
                            text = when (status) {
                                "MASTERED" -> "🏆 Mastered"
                                "STUDIED" -> "📖 Studied"
                                else -> "⚪ Unstarted"
                            },
                            color = when (status) {
                                "MASTERED" -> CyberEmerald
                                "STUDIED" -> CyberCyan
                                else -> theme.textMuted
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = ttp.name,
                color = theme.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = ttp.description,
                color = theme.textSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Row: Quick Toggle Studied & Mastered
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = CyberNavyDark,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).padding(end = 6.dp)
                ) {
                    Text(
                        text = "🛡️ ${ttp.d3fendId} ${ttp.d3fendName}",
                        color = CyberEmerald,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(
                        selected = status == "STUDIED",
                        onClick = {
                            val next = if (status == "STUDIED") "UNSTARTED" else "STUDIED"
                            onUpdateStatus(next)
                        },
                        label = { Text("📖 Studied", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                            selectedLabelColor = CyberCyan
                        ),
                        modifier = Modifier.height(26.dp)
                    )

                    FilterChip(
                        selected = status == "MASTERED",
                        onClick = {
                            val next = if (status == "MASTERED") "UNSTARTED" else "MASTERED"
                            onUpdateStatus(next)
                        },
                        label = { Text("🏆 Mastered", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberEmerald.copy(alpha = 0.25f),
                            selectedLabelColor = CyberEmerald
                        ),
                        modifier = Modifier.height(26.dp)
                    )
                }
            }
        }
    }
}
