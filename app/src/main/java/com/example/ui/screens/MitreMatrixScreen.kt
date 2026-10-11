package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.components.RemediationCodeBox
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MitreMatrixScreen(
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedTactic by remember { mutableStateOf<MitreTactic?>(null) }
    var selectedFilterMode by remember { mutableStateOf("ALL") } // ALL, ATTACK, DEFEND
    var mainViewTab by remember { mutableIntStateOf(0) } // 0: Matrix Catalog, 1: D3.js Flow Chart
    var activeFullscreenMode by remember { mutableStateOf<String?>(null) }

    val filteredTtps = remember(searchQuery, selectedTactic) {
        MitreMatrixRepository.filterTtps(searchQuery, selectedTactic)
    }

    if (activeFullscreenMode != null) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { activeFullscreenMode = null },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            com.example.ui.components.MitreInteractiveGraphComposable(
                initialMode = activeFullscreenMode!!,
                onClose = { activeFullscreenMode = null },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberNavyDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SeverityCritical.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SeverityCritical.copy(alpha = 0.5f)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = SeverityCritical,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "MITRE ATT&CK & D3FEND",
                                color = CyberTextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "14 ENTERPRISE TACTICS • ATTACK & DEFEND TTPS",
                                color = CyberCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.4.sp
                            )
                        }
                    }
                },
                actions = {
                    Button(
                        onClick = { activeFullscreenMode = "UNIFIED" },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("mitre_top_fullscreen_button")
                    ) {
                        Text("🖥️ Fullscreen Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberNavyDark)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Matrix Catalog
            Spacer(modifier = Modifier.height(8.dp))
            
            // Interactive Flow Chart Preview Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
            ) {
                // ... (Banner content)
            }

            if (mainViewTab == 1) {
                MitreFlowChartWebView(
                    modifier = Modifier.fillMaxSize(),
                    onTechniqueSelected = { /* sync or highlight if needed */ }
                )
            } else {
                // Interactive Flow Chart Preview Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Interactive Visual Flow Chart (D3.js)",
                                    color = CyberCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Drill down Tactics -> Techniques -> D3FEND with pan & zoom",
                                    color = CyberTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { mainViewTab = 1 },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberNavySurfaceVariant, contentColor = CyberCyan),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("open_d3_chart_button")
                            ) {
                                Text("Launch D3.js", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { activeFullscreenMode = "UNIFIED" },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("launch_fullscreen_mitre_button")
                            ) {
                                Text("🖥️ Fullscreen", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("mitre_search_input"),
                placeholder = {
                    Text(
                        text = "Search TTPs, T1190, D3-ITF, APT groups, Sigma, KQL...",
                        fontSize = 12.sp,
                        color = CyberTextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = CyberTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = CyberBorder,
                    focusedContainerColor = CyberNavySurface,
                    unfocusedContainerColor = CyberNavySurface,
                    focusedTextColor = CyberTextPrimary,
                    unfocusedTextColor = CyberTextPrimary
                )
            )

            // View Mode Chips (All, Attack Only, Defend Only)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "View Mode:",
                    color = CyberTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                listOf(
                    "ALL" to "All Matrix",
                    "ATTACK" to "Offensive TTPs",
                    "DEFEND" to "D3FEND Rules"
                ).forEach { (mode, label) ->
                    val isSelected = selectedFilterMode == mode
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) CyberCyan.copy(alpha = 0.2f) else CyberNavySurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) CyberCyan else CyberBorder
                        ),
                        modifier = Modifier
                            .clickable { selectedFilterMode = mode }
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) CyberCyan else CyberTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Horizontal Tactic Tabs (All 14 Tactics)
            val tactics = listOf(null) + MitreTactic.entries
            val currentTacticIndex = tactics.indexOf(selectedTactic)

            ScrollableTabRow(
                selectedTabIndex = if (currentTacticIndex >= 0) currentTacticIndex else 0,
                containerColor = CyberNavyDark,
                contentColor = CyberCyan,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    if (currentTacticIndex in tabPositions.indices) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[currentTacticIndex]),
                            color = selectedTactic?.color ?: CyberCyan
                        )
                    }
                }
            ) {
                tactics.forEachIndexed { index, tactic ->
                    val isSelected = (selectedTactic == tactic)
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTactic = tactic },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (tactic != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(tactic.color)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = tactic?.displayName ?: "All (14 Tactics)",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) (tactic?.color ?: CyberCyan) else CyberTextSecondary
                                )
                            }
                        },
                        modifier = Modifier.testTag("tab_mitre_tactic_${tactic?.tacticId ?: "all"}")
                    )
                }
            }

            // Summary bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberNavySurfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredTtps.size} Techniques & Mapped Countermeasures",
                    color = CyberTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                if (selectedTactic != null || searchQuery.isNotEmpty()) {
                    Text(
                        text = "Reset Filters",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            searchQuery = ""
                            selectedTactic = null
                        }
                    )
                }
            }

            // List of TTPs
            if (filteredTtps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = CyberTextMuted,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching MITRE TTPs found",
                            color = CyberTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try searching by Technique ID (e.g., T1059, T1190) or clear filters.",
                            color = CyberTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredTtps, key = { it.techniqueId }) { ttp ->
                        MitreTtpCard(
                            ttp = ttp,
                            viewMode = selectedFilterMode,
                            onAptClick = { apt -> searchQuery = apt },
                            onCopyText = { text, label ->
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(label, text)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
            }
        }
    }
}

@Composable
fun MitreTtpCard(
    ttp: MitreTtpItem,
    viewMode: String,
    onAptClick: (String) -> Unit,
    onCopyText: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Technique ID, Name, Tactic Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ttp.tactic.color.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ttp.tactic.color.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = ttp.techniqueId,
                                color = ttp.tactic.color,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyberNavySurfaceVariant
                        ) {
                            Text(
                                text = ttp.tactic.displayName,
                                color = CyberTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = ttp.name,
                        color = CyberTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Description
            Text(
                text = ttp.description,
                color = CyberTextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            // Known APT Groups
            if (ttp.knownAptGroups.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Observed Actors: ",
                        color = CyberTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    ttp.knownAptGroups.forEach { apt ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CyberNavyDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                            modifier = Modifier
                                .clickable { onAptClick(apt) }
                                .padding(end = 6.dp)
                        ) {
                            Text(
                                text = apt,
                                color = CyberCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SECTION A: Offensive Attack Walkthrough (if viewMode != DEFEND)
            if (viewMode != "DEFEND") {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyberNavyDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SeverityCritical.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(SeverityCritical)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ATTACK EMULATION",
                                    color = SeverityCritical,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            IconButton(
                                onClick = { onCopyText(ttp.attackCodeCommand, "Attack Emulation Command") },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Attack Command",
                                    tint = CyberTextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Text(
                            text = ttp.attackExampleTitle,
                            color = CyberTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Text(
                            text = ttp.attackCodeCommand,
                            color = Color(0xFFF87171),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF090D16), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        )
                    }
                }
            }

            if (viewMode == "ALL") {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // SECTION B: MITRE D3FEND Countermeasure & Detection Rule (if viewMode != ATTACK)
            if (viewMode != "ATTACK") {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyberNavyDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(CyberEmerald)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "MITRE D3FEND DEFENSE • ${ttp.d3fendId}",
                                    color = CyberEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            IconButton(
                                onClick = { onCopyText(ttp.d3fendRuleOrFix, "D3FEND Countermeasure Rule") },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Rule",
                                    tint = CyberTextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Text(
                            text = ttp.d3fendName,
                            color = CyberTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Text(
                            text = ttp.d3fendDescription,
                            color = CyberTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        Text(
                            text = ttp.d3fendRuleOrFix,
                            color = CyberEmerald,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF090D16), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        )
                    }
                }
            }

            // SECTION C: Cyber Kill Chain Stage & Scenario Example
            ttp.cyberKillChain?.let { killChain ->
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E1B4B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF818CF8).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF6366F1).copy(alpha = 0.25f),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFF818CF8))
                        ) {
                            Text(
                                text = "⚡ CYBER KILL CHAIN • ${killChain.stage.uppercase()}",
                                color = Color(0xFFA5B4FC),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = killChain.description,
                            color = CyberTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "Incident Scenario Example:",
                            color = Color(0xFFC7D2FE),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = killChain.scenarioExample,
                            color = Color(0xFFE0E7FF),
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp)
                                .background(Color(0xFF0F172A), RoundedCornerShape(4.dp))
                                .padding(6.dp)
                        )
                    }
                }
            }

            // SECTION D: Sub-Techniques & Variants
            if (ttp.subTechniquesList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "🧩 Sub-Techniques (${ttp.subTechniquesList.size}):",
                    color = CyberTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ttp.subTechniquesList.forEach { sub ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyberNavyDark,
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, CyberCyan.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "${sub.id} - ${sub.name}",
                                    color = CyberCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = sub.description,
                                    color = CyberTextSecondary,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // SECTION E: NIST Control Mapping
            if (ttp.nistControls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "🏛️ NIST SP 800-53 / Cybersecurity Framework Controls:",
                    color = CyberTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ttp.nistControls.forEach { nist ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF0284C7).copy(alpha = 0.25f),
                                        border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFF38BDF8))
                                    ) {
                                        Text(
                                            text = "NIST ${nist.controlId}",
                                            color = Color(0xFF38BDF8),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = nist.controlName,
                                        color = CyberTextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "${nist.familyOrFunction} • ${nist.description}",
                                    color = CyberTextSecondary,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Text(
                                    text = "Impl: ${nist.exampleImplementation}",
                                    color = CyberEmerald,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

