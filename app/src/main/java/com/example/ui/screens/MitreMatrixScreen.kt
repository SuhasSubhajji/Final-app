package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.mitre.db.D3fendTacticEntity
import com.example.data.mitre.db.D3fendTechniqueEntity
import com.example.data.mitre.db.TacticEntity
import com.example.data.mitre.db.TechniqueEntity
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberNavyDark
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberNavySurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.SeverityHigh
import com.example.ui.viewmodel.MitreViewModel
import com.example.ui.viewmodel.TechniqueDetailState

sealed interface MitreNavNode {
    data object MainHub : MitreNavNode
    data class AttackSection(val initialSubTab: Int = 0) : MitreNavNode // 0: Tactics, 1: Techniques
    data class AttackTechniquesForTactic(val tacticId: String, val tacticName: String, val tacticShortname: String) : MitreNavNode
    data class DefendSection(val initialSubTab: Int = 0) : MitreNavNode // 0: Tactics, 1: Techniques
    data class DefendTechniquesForTactic(val tacticId: String, val tacticName: String) : MitreNavNode
    data object KillChainSection : MitreNavNode
    data class KillChainStageDetail(val stageNumber: Int, val stageName: String, val stageDescription: String, val sampleTtpIds: List<String>) : MitreNavNode
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MitreMatrixScreen(
    displayMode: Int = 0,
    onNavigateBack: (() -> Unit)? = null,
    mitreViewModel: MitreViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val navStack = remember { mutableStateListOf<MitreNavNode>(MitreNavNode.MainHub) }
    val currentNode = navStack.lastOrNull() ?: MitreNavNode.MainHub

    BackHandler(enabled = navStack.size > 1) {
        navStack.removeAt(navStack.size - 1)
    }

    val searchQuery by mitreViewModel.searchQuery.collectAsState()
    val selectedPlatform by mitreViewModel.selectedPlatformFilter.collectAsState()
    val expandedTechniqueIds by mitreViewModel.expandedTechniqueIds.collectAsState()

    val tactics by mitreViewModel.tactics.collectAsState()
    val allTechniques by mitreViewModel.allTechniques.collectAsState()
    val d3fendTactics by mitreViewModel.allD3fendTactics.collectAsState()
    val d3fendTechniques by mitreViewModel.allD3fendTechniques.collectAsState()

    val selectedDetailState by mitreViewModel.selectedTechniqueDetail.collectAsState()

    selectedDetailState?.let { detail ->
        MitreTechniqueDetailBottomSheet(
            detailState = detail,
            onDismiss = { mitreViewModel.closeTechniqueDetail() },
            onCopyText = { text, label ->
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(label, text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberNavyDark)
    ) {
        // Top Breadcrumb Navigation Header
        Surface(
            color = CyberNavySurface,
            tonalElevation = 3.dp,
            border = BorderStroke(1.dp, CyberBorder)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (navStack.size > 1) {
                            IconButton(
                                onClick = { navStack.removeAt(navStack.size - 1) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        Column {
                            Text(
                                text = when (currentNode) {
                                    is MitreNavNode.MainHub -> "MITRE Framework Navigator"
                                    is MitreNavNode.AttackSection -> "1. MITRE ATT&CK (Offensive TTPs)"
                                    is MitreNavNode.AttackTechniquesForTactic -> "ATT&CK > ${(currentNode as MitreNavNode.AttackTechniquesForTactic).tacticName}"
                                    is MitreNavNode.DefendSection -> "2. MITRE D3FEND (Defensive Controls)"
                                    is MitreNavNode.DefendTechniquesForTactic -> "D3FEND > ${(currentNode as MitreNavNode.DefendTechniquesForTactic).tacticName}"
                                    is MitreNavNode.KillChainSection -> "3. Cyber Kill Chain (7 Stages)"
                                    is MitreNavNode.KillChainStageDetail -> "Kill Chain > Stage ${(currentNode as MitreNavNode.KillChainStageDetail).stageNumber}"
                                },
                                color = CyberTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "v19.2 Enterprise • Mobile • ICS • D3FEND v0.10",
                                color = CyberTextMuted,
                                fontSize = 9.5.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    if (currentNode !is MitreNavNode.MainHub) {
                        Button(
                            onClick = {
                                navStack.clear()
                                navStack.add(MitreNavNode.MainHub)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan.copy(alpha = 0.15f), contentColor = CyberCyan),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Main Hub", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (currentNode !is MitreNavNode.MainHub) {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { mitreViewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mitre_nested_search_input"),
                        placeholder = {
                            Text(
                                text = "Search T1134, T1059, Mimikatz, Phishing, D3-NTA...",
                                fontSize = 11.sp,
                                color = CyberTextMuted
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { mitreViewModel.setSearchQuery("") }, modifier = Modifier.size(24.dp)) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = CyberTextSecondary, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
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
            }
        }

        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            when (currentNode) {
                is MitreNavNode.MainHub -> {
                    MitreMainFrameworkHub(
                        onSelectAttackTactics = { navStack.add(MitreNavNode.AttackSection(initialSubTab = 0)) },
                        onSelectAttackTechniques = { navStack.add(MitreNavNode.AttackSection(initialSubTab = 1)) },
                        onSelectDefendTactics = { navStack.add(MitreNavNode.DefendSection(initialSubTab = 0)) },
                        onSelectDefendTechniques = { navStack.add(MitreNavNode.DefendSection(initialSubTab = 1)) },
                        onSelectKillChain = { navStack.add(MitreNavNode.KillChainSection) }
                    )
                }

                is MitreNavNode.AttackSection -> {
                    var attackTab by remember { mutableIntStateOf((currentNode as MitreNavNode.AttackSection).initialSubTab) }

                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (attackTab == 0) CyberCyan.copy(alpha = 0.25f) else CyberNavySurface,
                                border = BorderStroke(1.dp, if (attackTab == 0) CyberCyan else CyberBorder),
                                modifier = Modifier.weight(1f).clickable { attackTab = 0 }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(imageVector = Icons.Default.GridView, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Tactics", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = CyberTextPrimary)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (attackTab == 1) CyberCyan.copy(alpha = 0.25f) else CyberNavySurface,
                                border = BorderStroke(1.dp, if (attackTab == 1) CyberCyan else CyberBorder),
                                modifier = Modifier.weight(1f).clickable { attackTab = 1 }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Techniques", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = CyberTextPrimary)
                                }
                            }
                        }

                        if (attackTab == 0) {
                            MitreAttackTacticsNestedPage(
                                tactics = tactics,
                                onTacticClick = { t ->
                                    navStack.add(MitreNavNode.AttackTechniquesForTactic(t.id, t.name, t.shortname))
                                }
                            )
                        } else {
                            MitreCatalogListView(
                                tactics = tactics,
                                allTechniques = allTechniques,
                                searchQuery = searchQuery,
                                selectedPlatform = selectedPlatform,
                                selectedTacticFilter = null,
                                expandedTechniqueIds = expandedTechniqueIds,
                                onSelectTacticFilter = {},
                                onToggleExpand = { mitreViewModel.toggleSubtechniqueExpanded(it) },
                                onSelectTechnique = { mitreViewModel.openTechniqueDetail(it) }
                            )
                        }
                    }
                }

                is MitreNavNode.AttackTechniquesForTactic -> {
                    val targetTactic = currentNode as MitreNavNode.AttackTechniquesForTactic
                    val tacticTechsFlow = remember(targetTactic.tacticShortname) {
                        mitreViewModel.getTechniquesForTactic(targetTactic.tacticShortname)
                    }
                    val tacticTechs by tacticTechsFlow.collectAsState(initial = emptyList())
                    val filteredTacticTechs = remember(tacticTechs, searchQuery, selectedPlatform) {
                        tacticTechs.filter { tech ->
                            !tech.isSubtechnique &&
                            (searchQuery.isBlank() || tech.id.contains(searchQuery, ignoreCase = true) || tech.name.contains(searchQuery, ignoreCase = true) || tech.description.contains(searchQuery, ignoreCase = true)) &&
                            (selectedPlatform == "ALL" || tech.platforms.contains(selectedPlatform, ignoreCase = true))
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                                border = BorderStroke(1.dp, CyberCyan)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Tactic: ${targetTactic.tacticName}", color = CyberCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = CyberCyan.copy(alpha = 0.2f),
                                            border = BorderStroke(1.dp, CyberCyan)
                                        ) {
                                            Text(targetTactic.tacticId, color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Showing ${filteredTacticTechs.size} primary techniques mapped under this objective. Tap any technique to view real-world procedures, mitigations, and D3FEND countermeasures.", color = CyberTextSecondary, fontSize = 10.5.sp)
                                }
                            }
                        }

                        if (filteredTacticTechs.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Text("No matching techniques found in this tactic.", color = CyberTextMuted, fontSize = 12.sp)
                                }
                            }
                        } else {
                            items(filteredTacticTechs, key = { it.id }) { tech ->
                                val subtechs = remember(allTechniques, tech.id) {
                                    allTechniques.filter { it.isSubtechnique && it.parentTechniqueId == tech.id }
                                }
                                val isExpanded = expandedTechniqueIds.contains(tech.id)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { mitreViewModel.openTechniqueDetail(tech.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                                    border = BorderStroke(1.dp, CyberBorder)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = CyberCyan.copy(alpha = 0.2f),
                                                    border = BorderStroke(1.dp, CyberCyan)
                                                ) {
                                                    Text(
                                                        text = tech.id,
                                                        color = CyberCyan,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = FontFamily.Monospace,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = tech.name,
                                                    color = CyberTextPrimary,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            if (subtechs.isNotEmpty()) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = CyberCyan.copy(alpha = 0.15f),
                                                    modifier = Modifier.clickable { mitreViewModel.toggleSubtechniqueExpanded(tech.id) }
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                    ) {
                                                        Text(
                                                            text = "${subtechs.size} sub-techniques",
                                                            color = CyberCyan,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Icon(
                                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                            contentDescription = "Expand",
                                                            tint = CyberCyan,
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = tech.description,
                                            color = CyberTextSecondary,
                                            fontSize = 10.sp,
                                            lineHeight = 13.sp,
                                            maxLines = 2
                                        )

                                        if (isExpanded && subtechs.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                subtechs.forEach { sub ->
                                                    Surface(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable { mitreViewModel.openTechniqueDetail(sub.id) },
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = CyberNavyDark,
                                                        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f))
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(8.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    text = sub.id,
                                                                    color = CyberCyan,
                                                                    fontSize = 9.5.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontFamily = FontFamily.Monospace
                                                                )
                                                                Spacer(modifier = Modifier.width(6.dp))
                                                                Text(
                                                                    text = sub.name,
                                                                    color = CyberTextSecondary,
                                                                    fontSize = 10.sp,
                                                                    maxLines = 1
                                                                )
                                                            }
                                                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                is MitreNavNode.DefendSection -> {
                    var defendTab by remember { mutableIntStateOf((currentNode as MitreNavNode.DefendSection).initialSubTab) }

                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (defendTab == 0) CyberEmerald.copy(alpha = 0.25f) else CyberNavySurface,
                                border = BorderStroke(1.dp, if (defendTab == 0) CyberEmerald else CyberBorder),
                                modifier = Modifier.weight(1f).clickable { defendTab = 0 }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("7 Defensive Tactics", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = CyberTextPrimary)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (defendTab == 1) CyberEmerald.copy(alpha = 0.25f) else CyberNavySurface,
                                border = BorderStroke(1.dp, if (defendTab == 1) CyberEmerald else CyberBorder),
                                modifier = Modifier.weight(1f).clickable { defendTab = 1 }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Terminal, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("D3FEND Rules", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = CyberTextPrimary)
                                }
                            }
                        }

                        if (defendTab == 0) {
                            MitreDefendTacticsNestedPage(
                                tactics = d3fendTactics,
                                onTacticClick = { t ->
                                    navStack.add(MitreNavNode.DefendTechniquesForTactic(t.id, t.name))
                                }
                            )
                        } else {
                            MitreD3fendMatrixView(
                                d3fendTactics = d3fendTactics,
                                d3fendTechniques = d3fendTechniques,
                                searchQuery = searchQuery,
                                onCopyRule = { rule ->
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("D3FEND Rule", rule)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "D3FEND rule copied!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }

                is MitreNavNode.DefendTechniquesForTactic -> {
                    val targetDefendTactic = currentNode as MitreNavNode.DefendTechniquesForTactic
                    val d3Items = remember(d3fendTechniques, searchQuery, targetDefendTactic.tacticId) {
                        d3fendTechniques.filter { item ->
                            (item.tacticId == targetDefendTactic.tacticId || item.tacticName.contains(targetDefendTactic.tacticName, ignoreCase = true)) &&
                            (searchQuery.isBlank() || item.id.contains(searchQuery, ignoreCase = true) || item.name.contains(searchQuery, ignoreCase = true) || item.description.contains(searchQuery, ignoreCase = true))
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                                border = BorderStroke(1.dp, CyberEmerald)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("D3FEND Objective: ${targetDefendTactic.tacticName}", color = CyberEmerald, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = CyberEmerald.copy(alpha = 0.2f),
                                            border = BorderStroke(1.dp, CyberEmerald)
                                        ) {
                                            Text(targetDefendTactic.tacticId, color = CyberEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Defensive engineering techniques, digital artifacts, and detection rules mapped to this defensive pillar.", color = CyberTextSecondary, fontSize = 10.5.sp)
                                }
                            }
                        }

                        if (d3Items.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Text("No defensive techniques registered under this tactic yet.", color = CyberTextMuted, fontSize = 12.sp)
                                }
                            }
                        } else {
                            items(d3Items, key = { it.id }) { item ->
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "${item.id}: ${item.name}", color = CyberEmerald, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                            if (item.ruleSnippet.isNotBlank()) {
                                                IconButton(
                                                    onClick = {
                                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                        clipboard.setPrimaryClip(ClipData.newPlainText("D3FEND Rule", item.ruleSnippet))
                                                        Toast.makeText(context, "Rule snippet copied!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy Rule", tint = CyberEmerald, modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = item.description, color = CyberTextSecondary, fontSize = 10.5.sp, lineHeight = 14.sp)

                                        if (item.artifacts.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(text = "Digital Artifacts: ${item.artifacts}", color = CyberCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                        }

                                        if (item.counteredAttackIds.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text("Counters ATT&CK Techniques:", color = CyberTextMuted, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                item.counteredAttackIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { attackId ->
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = CyberCyan.copy(alpha = 0.15f),
                                                        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                                                        modifier = Modifier.clickable { mitreViewModel.openTechniqueDetail(attackId) }
                                                    ) {
                                                        Text(
                                                            text = attackId,
                                                            color = CyberCyan,
                                                            fontSize = 9.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            fontFamily = FontFamily.Monospace,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        if (item.ruleSnippet.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = CyberNavyDark,
                                                border = BorderStroke(1.dp, CyberBorder)
                                            ) {
                                                Text(
                                                    text = item.ruleSnippet,
                                                    color = CyberEmerald,
                                                    fontSize = 9.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                is MitreNavNode.KillChainSection -> {
                    MitreCyberKillChainNestedPage(
                        onStageClick = { stageNum, name, desc, ttps ->
                            navStack.add(MitreNavNode.KillChainStageDetail(stageNum, name, desc, ttps))
                        }
                    )
                }

                is MitreNavNode.KillChainStageDetail -> {
                    val stageDetail = currentNode as MitreNavNode.KillChainStageDetail

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                                border = BorderStroke(1.dp, SeverityHigh)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Stage ${stageDetail.stageNumber}: ${stageDetail.stageName}", color = SeverityHigh, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = SeverityHigh.copy(alpha = 0.2f),
                                            border = BorderStroke(1.dp, SeverityHigh)
                                        ) {
                                            Text("CYBER KILL CHAIN", color = SeverityHigh, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(stageDetail.stageDescription, color = CyberTextSecondary, fontSize = 10.5.sp, lineHeight = 14.sp)
                                }
                            }
                        }

                        item {
                            Text("Mapped ATT&CK Techniques for Stage ${stageDetail.stageNumber}:", color = CyberTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        items(stageDetail.sampleTtpIds) { ttpId ->
                            val matchedTech = remember(allTechniques, ttpId) {
                                allTechniques.find { it.id == ttpId }
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { mitreViewModel.openTechniqueDetail(ttpId) },
                                shape = RoundedCornerShape(6.dp),
                                color = CyberNavySurface,
                                border = BorderStroke(1.dp, CyberBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = ttpId, color = CyberCyan, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                            if (matchedTech != null) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(text = matchedTech.name, color = CyberTextPrimary, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        if (matchedTech != null) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(text = matchedTech.description, color = CyberTextSecondary, fontSize = 9.5.sp, maxLines = 2)
                                        }
                                    }
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Main Framework Hub Screen Composable
 */
@Composable
fun MitreMainFrameworkHub(
    onSelectAttackTactics: () -> Unit,
    onSelectAttackTechniques: () -> Unit,
    onSelectDefendTactics: () -> Unit,
    onSelectDefendTechniques: () -> Unit,
    onSelectKillChain: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                border = BorderStroke(1.5.dp, CyberCyan.copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.GridView, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("1. MITRE ATT&CK", color = CyberTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Surface(shape = RoundedCornerShape(4.dp), color = CyberCyan.copy(alpha = 0.2f)) {
                            Text("OFFENSIVE TTPs", color = CyberCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Documents offensive adversary behaviors, threat group procedures, and real-world execution tactics.",
                        color = CyberTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onSelectAttackTactics,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark)
                        ) {
                            Text("Tactics", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onSelectAttackTechniques,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberNavyDark, contentColor = CyberCyan),
                            border = BorderStroke(1.dp, CyberCyan)
                        ) {
                            Text("Techniques", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                border = BorderStroke(1.5.dp, CyberEmerald.copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("2. MITRE D3FEND", color = CyberTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Surface(shape = RoundedCornerShape(4.dp), color = CyberEmerald.copy(alpha = 0.2f)) {
                            Text("DEFENSIVE COUNTERMEASURES", color = CyberEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Catalogs defensive cybersecurity mechanisms, hardening rules, and artifact-centric detection controls.",
                        color = CyberTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onSelectDefendTactics,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald, contentColor = CyberNavyDark)
                        ) {
                            Text("Tactics", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onSelectDefendTechniques,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberNavyDark, contentColor = CyberEmerald),
                            border = BorderStroke(1.dp, CyberEmerald)
                        ) {
                            Text("Techniques", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                border = BorderStroke(1.5.dp, SeverityHigh.copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = SeverityHigh, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("3. Cyber Kill Chain", color = CyberTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Surface(shape = RoundedCornerShape(4.dp), color = SeverityHigh.copy(alpha = 0.2f)) {
                            Text("7 SEQUENTIAL STAGES", color = SeverityHigh, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Lockheed Martin 7-Stage Cyber Attack Lifecycle mapped directly to ATT&CK TTPs.",
                        color = CyberTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onSelectKillChain,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SeverityHigh, contentColor = CyberNavyDark)
                    ) {
                        Text("Explore 7 Stages & Mapped TTPs", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * ATT&CK Tactics Nested Page
 */
@Composable
fun MitreAttackTacticsNestedPage(
    tactics: List<TacticEntity>,
    onTacticClick: (TacticEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tactics, key = { it.id }) { tactic ->
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { onTacticClick(tactic) },
                shape = RoundedCornerShape(8.dp),
                color = CyberNavySurface,
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "${tactic.id}: ${tactic.name}", color = CyberCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = tactic.description, color = CyberTextSecondary, fontSize = 10.5.sp, maxLines = 2)
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

/**
 * D3FEND Tactics Nested Page
 */
@Composable
fun MitreDefendTacticsNestedPage(
    tactics: List<D3fendTacticEntity>,
    onTacticClick: (D3fendTacticEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tactics, key = { it.id }) { tactic ->
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { onTacticClick(tactic) },
                shape = RoundedCornerShape(8.dp),
                color = CyberNavySurface,
                border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "${tactic.id}: ${tactic.name}", color = CyberEmerald, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = tactic.description, color = CyberTextSecondary, fontSize = 10.5.sp)
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

/**
 * Cyber Kill Chain 7 Stages Nested Page
 */
@Composable
fun MitreCyberKillChainNestedPage(
    onStageClick: (Int, String, String, List<String>) -> Unit
) {
    val stages = listOf(
        Tuple4(1, "Reconnaissance", "Harvesting victim network topology, DNS records, active IP blocks, and employee email profiles.", listOf("T1595", "T1595.001", "T1595.002", "T1590", "T1598")),
        Tuple4(2, "Weaponization", "Coupling backdoor malware with exploits into deliverable packages (typosquatted domains, compromised accounts).", listOf("T1583", "T1583.001", "T1586", "T1588")),
        Tuple4(3, "Delivery", "Transmitting weaponized bundle to victim via spearphishing attachments, phishing links, or vulnerable public apps.", listOf("T1566", "T1566.001", "T1566.002", "T1190", "T1078", "T1133")),
        Tuple4(4, "Exploitation", "Triggering adversary code execution via command shells, PowerShell, or client-side application flaws.", listOf("T1059", "T1059.001", "T1059.003", "T1059.004", "T1204", "T1203", "T1027")),
        Tuple4(5, "Installation", "Installing persistent access mechanisms (scheduled tasks, registry run keys, services, token duplication).", listOf("T1053", "T1053.005", "T1547", "T1547.001", "T1543", "T1543.003", "T1134", "T1068", "T1055.001")),
        Tuple4(6, "Command & Control", "Establishing covert two-way communications over HTTP/S, DNS tunneling, credential dumping, and lateral movement.", listOf("T1071", "T1071.001", "T1071.004", "T1105", "T1003", "T1003.001", "T1021.001", "T1021.002", "T1570")),
        Tuple4(7, "Actions on Objectives", "Executing mission goals: data encryption for ransom, corporate intellectual property exfiltration, and service disruption.", listOf("T1486", "T1485", "T1489", "T1041", "T1567", "T1560", "T1005"))
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(stages) { (stageNum, name, desc, ttps) ->
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { onStageClick(stageNum, name, desc, ttps) },
                shape = RoundedCornerShape(8.dp),
                color = CyberNavySurface,
                border = BorderStroke(1.dp, SeverityHigh.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Stage $stageNum: $name", color = SeverityHigh, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = desc, color = CyberTextSecondary, fontSize = 10.5.sp)
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = SeverityHigh, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

/**
 * Catalog List View Composable
 */
@Composable
fun MitreCatalogListView(
    tactics: List<TacticEntity>,
    allTechniques: List<TechniqueEntity>,
    searchQuery: String,
    selectedPlatform: String,
    selectedTacticFilter: String?,
    expandedTechniqueIds: Set<String>,
    onSelectTacticFilter: (String?) -> Unit,
    onToggleExpand: (String) -> Unit,
    onSelectTechnique: (String) -> Unit
) {
    val filteredParents = remember(allTechniques, searchQuery, selectedPlatform, selectedTacticFilter) {
        allTechniques.filter { tech ->
            !tech.isSubtechnique &&
            (searchQuery.isBlank() ||
                tech.id.contains(searchQuery, ignoreCase = true) ||
                tech.name.contains(searchQuery, ignoreCase = true) ||
                tech.description.contains(searchQuery, ignoreCase = true)) &&
            (selectedPlatform == "ALL" || tech.platforms.contains(selectedPlatform, ignoreCase = true))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (tactics.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = if (selectedTacticFilter == null) 0 else (tactics.indexOfFirst { it.shortname == selectedTacticFilter } + 1).coerceAtLeast(0),
                containerColor = CyberNavyDark,
                contentColor = CyberCyan,
                edgePadding = 12.dp
            ) {
                Tab(
                    selected = selectedTacticFilter == null,
                    onClick = { onSelectTacticFilter(null) },
                    text = { Text("All Tactics (${tactics.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                tactics.forEach { tactic ->
                    val isSel = selectedTacticFilter == tactic.shortname
                    Tab(
                        selected = isSel,
                        onClick = { onSelectTacticFilter(tactic.shortname) },
                        text = {
                            Text(
                                text = tactic.name,
                                fontSize = 11.sp,
                                color = if (isSel) CyberCyan else CyberTextSecondary
                            )
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberNavySurfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Displaying ${filteredParents.size} Techniques",
                color = CyberTextSecondary,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium
            )

            if (selectedTacticFilter != null || searchQuery.isNotBlank()) {
                Text(
                    text = "Clear Filters",
                    color = CyberCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSelectTacticFilter(null) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredParents, key = { it.id }) { tech ->
                val subtechs = remember(allTechniques, tech.id) {
                    allTechniques.filter { it.isSubtechnique && it.parentTechniqueId == tech.id }
                }
                val isExpanded = expandedTechniqueIds.contains(tech.id)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectTechnique(tech.id) },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                    border = BorderStroke(1.dp, CyberBorder)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = CyberCyan.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, CyberCyan)
                                ) {
                                    Text(
                                        text = tech.id,
                                        color = CyberCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = tech.name,
                                    color = CyberTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (subtechs.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = CyberCyan.copy(alpha = 0.15f),
                                    modifier = Modifier.clickable { onToggleExpand(tech.id) }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "${subtechs.size} sub-techniques",
                                            color = CyberCyan,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = "Expand",
                                            tint = CyberCyan,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tech.description,
                            color = CyberTextSecondary,
                            fontSize = 10.sp,
                            lineHeight = 13.sp,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

/**
 * D3FEND Countermeasures View
 */
@Composable
fun MitreD3fendMatrixView(
    d3fendTactics: List<D3fendTacticEntity>,
    d3fendTechniques: List<D3fendTechniqueEntity>,
    searchQuery: String,
    onCopyRule: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(d3fendTactics, key = { it.id }) { tactic ->
            val itemsForTactic = remember(d3fendTechniques, searchQuery, tactic.id) {
                d3fendTechniques.filter { item ->
                    (item.tacticId == tactic.id || item.tacticName.contains(tactic.name, ignoreCase = true)) &&
                    (searchQuery.isBlank() || item.id.contains(searchQuery, ignoreCase = true) || item.name.contains(searchQuery, ignoreCase = true))
                }
            }

            Column(
                modifier = Modifier.width(200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(CyberEmerald)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tactic.id,
                            color = CyberEmerald,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = tactic.name,
                            color = CyberTextPrimary,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(itemsForTactic, key = { it.id }) { item ->
                        Card(
                            shape = RoundedCornerShape(6.dp),
                            colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                            border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "${item.id}: ${item.name}",
                                    color = CyberEmerald,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.description,
                                    color = CyberTextSecondary,
                                    fontSize = 9.5.sp,
                                    maxLines = 3
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 6-Tab Technique Detail Inspection Modal Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MitreTechniqueDetailBottomSheet(
    detailState: TechniqueDetailState,
    onDismiss: () -> Unit,
    onCopyText: (String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CyberNavySurface,
        contentColor = CyberTextPrimary,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CyberCyan.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, CyberCyan)
                    ) {
                        Text(
                            text = detailState.technique.id,
                            color = CyberCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = detailState.technique.name,
                        color = CyberTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Close", tint = CyberTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = CyberNavyDark,
                contentColor = CyberCyan,
                edgePadding = 0.dp
            ) {
                Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }, text = { Text("Overview", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
                Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }, text = { Text("Procedures (${detailState.procedures.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
                Tab(selected = selectedTabIndex == 2, onClick = { selectedTabIndex = 2 }, text = { Text("Mitigations (${detailState.mitigations.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
                Tab(selected = selectedTabIndex == 3, onClick = { selectedTabIndex = 3 }, text = { Text("Detections (${detailState.detections.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
                Tab(selected = selectedTabIndex == 4, onClick = { selectedTabIndex = 4 }, text = { Text("D3FEND (${detailState.d3fendCountermeasures.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
                Tab(selected = selectedTabIndex == 5, onClick = { selectedTabIndex = 5 }, text = { Text("Related", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        item {
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = CyberNavyDark),
                                border = BorderStroke(1.dp, CyberBorder)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Tactics:", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        detailState.tactics.forEach { t ->
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = CyberCyan.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = t.name,
                                                    color = CyberCyan,
                                                    fontSize = 10.sp,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Platforms: ${detailState.technique.platforms}", color = CyberTextSecondary, fontSize = 10.5.sp)
                                    Text("Data Sources: ${detailState.technique.dataSources}", color = CyberTextMuted, fontSize = 10.sp)

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Description:", color = CyberTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = detailState.technique.description,
                                        color = CyberTextSecondary,
                                        fontSize = 10.5.sp,
                                        lineHeight = 14.sp
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(detailState.technique.url))
                                            context.startActivity(intent)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("View Official MITRE Page", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        if (detailState.procedures.isEmpty()) {
                            item { Text("No procedure mappings available.", color = CyberTextMuted, fontSize = 11.sp) }
                        } else {
                            items(detailState.procedures, key = { it.id }) { proc ->
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = CyberNavyDark),
                                    border = BorderStroke(1.dp, CyberBorder)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = proc.sourceName, color = CyberCyan, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = proc.description, color = CyberTextSecondary, fontSize = 10.sp, lineHeight = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        if (detailState.mitigations.isEmpty()) {
                            item { Text("No mitigations mapped directly.", color = CyberTextMuted, fontSize = 11.sp) }
                        } else {
                            items(detailState.mitigations, key = { it.id }) { mit ->
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = CyberNavyDark),
                                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = "${mit.id}: ${mit.name}", color = CyberEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = mit.description, color = CyberTextSecondary, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        if (detailState.detections.isEmpty()) {
                            item { Text("No detection strategies mapped.", color = CyberTextMuted, fontSize = 11.sp) }
                        } else {
                            items(detailState.detections, key = { it.id }) { det ->
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = CyberNavyDark),
                                    border = BorderStroke(1.dp, CyberBorder)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = "Strategy: ${det.strategy}", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "Data Components: ${det.dataComponents}", color = CyberTextMuted, fontSize = 9.5.sp)
                                    }
                                }
                            }
                        }
                    }
                    4 -> {
                        if (detailState.d3fendCountermeasures.isEmpty()) {
                            item { Text("No D3FEND countermeasures mapped.", color = CyberTextMuted, fontSize = 11.sp) }
                        } else {
                            items(detailState.d3fendCountermeasures, key = { it.id }) { d3 ->
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = CyberNavyDark),
                                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = "${d3.id}: ${d3.name}", color = CyberEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = d3.description, color = CyberTextSecondary, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                    5 -> {
                        item {
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = CyberNavyDark),
                                border = BorderStroke(1.dp, CyberBorder)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Connected App Modules", color = CyberTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("• Linked to Threat Intelligence Feed", color = CyberTextSecondary, fontSize = 10.5.sp)
                                    Text("• Included in SOC Knowledge Glossary", color = CyberTextSecondary, fontSize = 10.5.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

@Composable
fun MitreTechniqueRowCard(
    technique: TechniqueEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = BorderStroke(1.dp, CyberBorder)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = technique.id, color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = technique.name, color = CyberTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = technique.description, color = CyberTextSecondary, fontSize = 9.5.sp, maxLines = 2)
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
        }
    }
}
