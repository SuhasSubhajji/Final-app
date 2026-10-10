package com.example.ui.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.example.data.learning.LearningRepository
import com.example.data.learning.LearningTrack
import com.example.data.learning.RoadmapStage
import com.example.data.local.DynamicRoadmapTopicEntity
import com.example.data.mitre.MitreMatrixRepository
import com.example.data.mitre.MitreTactic
import com.example.data.mitre.MitreTtpItem
import com.example.data.model.FeedSource
import com.example.data.model.ThreatCategory
import com.example.data.model.ThreatItem
import com.example.data.model.ThreatSeverity
import com.example.ui.components.GlossaryModalSheet
import com.example.ui.components.RemediationCodeBox
import com.example.ui.components.CyberThemeToggleButton
import com.example.ui.components.ThemeSelectorDialog
import com.example.ui.components.AttackScenarioCard
import com.example.ui.components.Sc200ExamSection
import com.example.ui.components.ActiveDirectorySection
import com.example.ui.components.ActiveDirectoryAuthSection
import com.example.ui.components.ActiveDirectoryRedTeamSection
import com.example.ui.components.Sc500ExamSection
import com.example.ui.theme.CyberBorder
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
import com.example.ui.viewmodel.ThreatFeedViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningRoadmapScreen(
    viewModel: ThreatFeedViewModel,
    modifier: Modifier = Modifier
) {
    val attackTrack = remember { LearningRepository.tracks.first { it.id == "mitre_attack" } }
    val defendTrack = remember { LearningRepository.tracks.first { it.id == "mitre_defend" } }
    val careerTracks = remember { LearningRepository.tracks.filter { it.id != "mitre_attack" && it.id != "mitre_defend" } }

    // 0: MITRE ATT&CK, 1: MITRE D3FEND, 2: Career Pathways
    var activeMainSection by remember { mutableIntStateOf(0) }
    var selectedCareerTrackIndex by remember { mutableIntStateOf(0) }

    val currentTrack = when (activeMainSection) {
        0 -> attackTrack
        1 -> defendTrack
        else -> careerTracks[selectedCareerTrackIndex.coerceIn(0, careerTracks.lastIndex)]
    }

    val themeMode by viewModel.currentThemeMode.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    val rlState by viewModel.rlAgentState.collectAsState()
    val ttpProgressMap by viewModel.ttpProgressMap.collectAsState()
    val theme = CyberTheme.colors

    var userCustomQuestion by remember { mutableStateOf("") }
    val customQuestionAnswer by viewModel.customQuestionAnswer.collectAsState()
    val isAskingQuestion by viewModel.isAskingQuestion.collectAsState()

    val glossaryTerms by viewModel.glossaryTerms.collectAsState()
    val isQueryingGlossary by viewModel.isQueryingGlossary.collectAsState()
    val dynamicTopics: List<DynamicRoadmapTopicEntity> by viewModel.getDynamicTopicsForTrack(currentTrack.id).collectAsState(initial = emptyList())
    val isEnrichingRoadmap by viewModel.isEnrichingRoadmap.collectAsState()
    val attackScenarios by viewModel.allAttackScenarios.collectAsState()
    val isUpdatingScenarios by viewModel.isUpdatingScenarios.collectAsState()

    var scenarioSearchQuery by remember { mutableStateOf("") }
    var selectedScenarioFilter by remember { mutableStateOf("ALL") }

    val filteredScenarios = remember(attackScenarios, scenarioSearchQuery, selectedScenarioFilter) {
        attackScenarios.filter { item ->
            val matchesSearch = if (scenarioSearchQuery.isBlank()) true else {
                item.organization.contains(scenarioSearchQuery, ignoreCase = true) ||
                item.attackVector.contains(scenarioSearchQuery, ignoreCase = true) ||
                item.industry.contains(scenarioSearchQuery, ignoreCase = true) ||
                item.cveReferences.contains(scenarioSearchQuery, ignoreCase = true) ||
                item.threatActor.contains(scenarioSearchQuery, ignoreCase = true) ||
                item.scenarioDetails.contains(scenarioSearchQuery, ignoreCase = true)
            }
            val matchesFilter = when (selectedScenarioFilter) {
                "IDENTITY" -> item.attackVector.contains("Kerberos", ignoreCase = true) ||
                              item.attackVector.contains("Credential", ignoreCase = true) ||
                              item.attackVector.contains("OAuth", ignoreCase = true) ||
                              item.attackVector.contains("Session", ignoreCase = true)
                "RANSOMWARE" -> item.attackVector.contains("Ransomware", ignoreCase = true) ||
                                item.scenarioDetails.contains("Ransomware", ignoreCase = true)
                "SUPPLY_CHAIN" -> item.attackVector.contains("Supply Chain", ignoreCase = true) ||
                                  item.attackVector.contains("Build", ignoreCase = true)
                "ZERO_DAY" -> item.attackVector.contains("Zero-Day", ignoreCase = true) ||
                              item.attackVector.contains("RCE", ignoreCase = true)
                "CLOUD" -> item.attackVector.contains("Cloud", ignoreCase = true) ||
                           item.attackVector.contains("IAM", ignoreCase = true) ||
                           item.industry.contains("Cloud", ignoreCase = true)
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    var showGlossarySheet by remember { mutableStateOf(false) }
    val glossarySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Modal flow chart mode: "ATTACK" or "DEFEND"
    var activeFlowChartMode by remember { mutableStateOf<String?>(null) }

    if (showThemeDialog) {
        ThemeSelectorDialog(
            currentMode = themeMode,
            onSelectMode = { selected ->
                viewModel.setThemeMode(selected)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    if (activeFlowChartMode != null) {
        val mode = activeFlowChartMode!!
        Dialog(
            onDismissRequest = { activeFlowChartMode = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            com.example.ui.components.MitreInteractiveGraphComposable(
                initialMode = mode,
                progressMap = ttpProgressMap,
                onUpdateProgress = { techniqueId, status -> viewModel.updateTtpProgress(techniqueId, status) },
                onClose = { activeFlowChartMode = null },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = theme.bg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = theme.accentPrimary.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = theme.accentPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Cybersecurity Learning Hub",
                                color = theme.textPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "OFFENSIVE • DEFENSIVE • ARCHITECTURE",
                                color = theme.accentPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.4.sp
                            )
                        }
                    }
                },
                actions = {
                    // Persistent Theme Mode Toggle
                    CyberThemeToggleButton(
                        currentMode = themeMode,
                        onClick = { showThemeDialog = true }
                    )

                    IconButton(
                        onClick = { showGlossarySheet = true },
                        modifier = Modifier.testTag("open_glossary_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = "Cybersecurity Glossary",
                            tint = theme.accentPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // --- Top-Level Segmented Section Selector with Scrollable Capsule Chips ---
            Surface(
                color = theme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Section 0: MITRE ATT&CK
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (activeMainSection == 0) Color(0xFFF43F5E).copy(alpha = 0.2f) else theme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (activeMainSection == 0) Color(0xFFF43F5E) else theme.border
                        ),
                        modifier = Modifier
                            .clickable { activeMainSection = 0 }
                            .testTag("section_mitre_attack")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("⚔️ ATT&CK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (activeMainSection == 0) Color(0xFFF43F5E) else theme.textSecondary)
                            Text("Offensive", fontSize = 9.sp, color = theme.textMuted)
                        }
                    }

                    // Section 1: MITRE D3FEND
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (activeMainSection == 1) Color(0xFF06B6D4).copy(alpha = 0.2f) else theme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (activeMainSection == 1) Color(0xFF06B6D4) else theme.border
                        ),
                        modifier = Modifier
                            .clickable { activeMainSection = 1 }
                            .testTag("section_mitre_defend")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🛡️ D3FEND", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (activeMainSection == 1) Color(0xFF06B6D4) else theme.textSecondary)
                            Text("Defensive", fontSize = 9.sp, color = theme.textMuted)
                        }
                    }

                    // Section 3: Attack Scenarios (Top 1000)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (activeMainSection == 3) theme.accentPrimary.copy(alpha = 0.2f) else theme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (activeMainSection == 3) theme.accentPrimary else theme.border
                        ),
                        modifier = Modifier
                            .clickable { activeMainSection = 3 }
                            .testTag("section_scenarios")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("💥 Breaches", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (activeMainSection == 3) theme.accentPrimary else theme.textSecondary)
                            Text("Top 1000", fontSize = 9.sp, color = theme.textMuted)
                        }
                    }

                    // Section 4: SC-200 Exam Preparation
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (activeMainSection == 4) Color(0xFF0078D4).copy(alpha = 0.2f) else theme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (activeMainSection == 4) Color(0xFF0078D4) else theme.border
                        ),
                        modifier = Modifier
                            .clickable { activeMainSection = 4 }
                            .testTag("section_sc200")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🎓 SC-200 Exam", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (activeMainSection == 4) Color(0xFF0078D4) else theme.textSecondary)
                            Text("Microsoft SOC", fontSize = 9.sp, color = theme.textMuted)
                        }
                    }

                    // Section 5: Active Directory & Red Teaming Mastery
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (activeMainSection == 5) Color(0xFFF97316).copy(alpha = 0.2f) else theme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (activeMainSection == 5) Color(0xFFF97316) else theme.border
                        ),
                        modifier = Modifier
                            .clickable { activeMainSection = 5 }
                            .testTag("section_ad_mastery")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🏰 AD & Red Teaming", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (activeMainSection == 5) Color(0xFFF97316) else theme.textSecondary)
                            Text("Kerberos & Red Team", fontSize = 9.sp, color = theme.textMuted)
                        }
                    }

                    // Section 7: SC-500 Exam Preparation
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (activeMainSection == 7) Color(0xFF38BDF8).copy(alpha = 0.2f) else theme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (activeMainSection == 7) Color(0xFF38BDF8) else theme.border
                        ),
                        modifier = Modifier
                            .clickable { activeMainSection = 7 }
                            .testTag("section_sc500")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("☁️ SC-500 Exam", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (activeMainSection == 7) Color(0xFF38BDF8) else theme.textSecondary)
                            Text("Azure Security", fontSize = 9.sp, color = theme.textMuted)
                        }
                    }

                    // Section 2: Career Pathways
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (activeMainSection == 2) theme.accentSecondary.copy(alpha = 0.2f) else theme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (activeMainSection == 2) theme.accentSecondary else theme.border
                        ),
                        modifier = Modifier
                            .clickable { activeMainSection = 2 }
                            .testTag("section_career_pathways")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🚀 Pathways", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (activeMainSection == 2) theme.accentSecondary else theme.textSecondary)
                            Text("5 Roles", fontSize = 9.sp, color = theme.textMuted)
                        }
                    }
                }
            }

            // Sub-tabs for Career Pathways (when Section 2 is active)
            if (activeMainSection == 2) {
                ScrollableTabRow(
                    selectedTabIndex = selectedCareerTrackIndex,
                    containerColor = theme.surfaceVariant,
                    contentColor = currentTrack.themeColor,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedCareerTrackIndex]),
                            color = currentTrack.themeColor
                        )
                    }
                ) {
                    careerTracks.forEachIndexed { i, track ->
                        Tab(
                            selected = selectedCareerTrackIndex == i,
                            onClick = { selectedCareerTrackIndex = i },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(track.themeColor)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = track.title,
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedCareerTrackIndex == i) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedCareerTrackIndex == i) track.themeColor else theme.textSecondary
                                    )
                                }
                            },
                            modifier = Modifier.testTag("tab_career_track_${track.id}")
                        )
                    }
                }
            }

            // --- Track Content Details (LazyColumn) ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // TTP Mastery Progress Tracker Card
                item {
                    val allTtps = remember { com.example.data.mitre.MitreMatrixRepository.allTtps }
                    val studiedCount = remember(ttpProgressMap) { allTtps.count { ttpProgressMap[it.techniqueId] == "STUDIED" } }
                    val masteredCount = remember(ttpProgressMap) { allTtps.count { ttpProgressMap[it.techniqueId] == "MASTERED" } }
                    val totalCount = allTtps.size
                    val progressRatio = (studiedCount + masteredCount).toFloat() / totalCount.coerceAtLeast(1)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("learning_ttp_progress_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = theme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = CyberEmerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "MITRE Technique Mastery Tracker",
                                            color = theme.textPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "$masteredCount Mastered • $studiedCount Studied of $totalCount Techniques",
                                            color = CyberEmerald,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CyberEmerald.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${(progressRatio * 100).toInt()}%",
                                        color = CyberEmerald,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            LinearProgressIndicator(
                                progress = { progressRatio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = CyberEmerald,
                                trackColor = CyberNavySurfaceVariant
                            )
                        }
                    }
                }
                // RL Recommendation Banner
                item {
                    val recommendedTrackId = viewModel.getRecommendedRoadmapTrackId()
                    val isCurrentRecommended = when (recommendedTrackId) {
                        "mitre_attack" -> activeMainSection == 0
                        "mitre_defend" -> activeMainSection == 1
                        else -> activeMainSection == 2
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = theme.accentPrimary.copy(alpha = 0.10f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentPrimary.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = theme.accentPrimary.copy(alpha = 0.2f),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = theme.accentPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🤖 RL Adaptive Learning Recommendation",
                                    color = theme.accentPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Top priority: ${rlState.topDomain.label}. Epsilon: ${"%.2f".format(rlState.explorationRateEpsilon)}",
                                    color = theme.textMuted,
                                    fontSize = 11.sp
                                )
                            }
                            if (!isCurrentRecommended) {
                                Button(
                                    onClick = {
                                        when (recommendedTrackId) {
                                            "mitre_attack" -> activeMainSection = 0
                                            "mitre_defend" -> activeMainSection = 1
                                            else -> {
                                                activeMainSection = 2
                                                val foundIdx = careerTracks.indexOfFirst { it.id == recommendedTrackId }
                                                if (foundIdx >= 0) selectedCareerTrackIndex = foundIdx
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = theme.accentPrimary, contentColor = Color.Black),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("jump_to_rl_recommendation_button")
                                ) {
                                    Text("Focus", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Header Banner
                if (activeMainSection != 3 && activeMainSection != 4 && activeMainSection != 5 && activeMainSection != 6 && activeMainSection != 7) {
                    item {
                        TrackHeaderCard(track = currentTrack)
                    }
                }

                // ==================== SECTION 3: TOP 1000 BREACHES & ATTACK SCENARIOS ====================
                if (activeMainSection == 3) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = theme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentPrimary.copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Top 1000 Security Breaches & Scenarios",
                                    color = theme.accentPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Deep-dive analysis of real-world enterprise compromise scenarios, attack paths, threat actor TTPs, and concrete defensive hardening guidelines.",
                                    color = theme.textSecondary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    item {
                        com.example.ui.components.BreachTrendChart(scenarios = attackScenarios)
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = theme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, theme.border)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Live Database Auto-Sync",
                                        color = theme.accentPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Auto-update breaches and scenarios when new threat intelligence becomes available.",
                                        color = theme.textMuted,
                                        fontSize = 11.sp
                                    )
                                }
                                
                                Button(
                                    onClick = { viewModel.updateAttackScenarios() },
                                    enabled = !isUpdatingScenarios,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = theme.accentPrimary, contentColor = Color.Black)
                                ) {
                                    if (isUpdatingScenarios) {
                                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.Black, strokeWidth = 2.dp)
                                    } else {
                                        Text("Sync Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Search and Filter Bar
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = scenarioSearchQuery,
                                onValueChange = { scenarioSearchQuery = it },
                                placeholder = { Text("Search 1000 breaches, actors, CVEs or vectors...", color = theme.textMuted, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = theme.accentPrimary, modifier = Modifier.size(18.dp))
                                },
                                trailingIcon = {
                                    if (scenarioSearchQuery.isNotEmpty()) {
                                        IconButton(onClick = { scenarioSearchQuery = "" }) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = theme.textMuted, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("scenario_search_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = theme.accentPrimary,
                                    unfocusedBorderColor = theme.border,
                                    focusedContainerColor = theme.surface,
                                    unfocusedContainerColor = theme.surface,
                                    focusedTextColor = theme.textPrimary,
                                    unfocusedTextColor = theme.textPrimary
                                )
                            )

                            // Category filter chips
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val filters = listOf(
                                    "ALL" to "All Attacks",
                                    "IDENTITY" to "🔑 Identity & Kerberos",
                                    "RANSOMWARE" to "💀 Ransomware",
                                    "SUPPLY_CHAIN" to "📦 Supply Chain",
                                    "ZERO_DAY" to "⚡ Zero-Day/RCE",
                                    "CLOUD" to "☁️ Cloud / IAM"
                                )
                                filters.forEach { (key, label) ->
                                    val isSelected = selectedScenarioFilter == key
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) theme.accentPrimary.copy(alpha = 0.2f) else theme.surface,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) theme.accentPrimary else theme.border
                                        ),
                                        modifier = Modifier.clickable { selectedScenarioFilter = key }
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) theme.accentPrimary else theme.textSecondary,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Attack Scenarios (${filteredScenarios.size})",
                                color = theme.textPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Interactive Case Studies",
                                color = theme.textMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (filteredScenarios.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = theme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, theme.border),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier.padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (scenarioSearchQuery.isNotBlank() || selectedScenarioFilter != "ALL") 
                                            "No attack scenarios match the filter. Try clearing your search query."
                                        else 
                                            "No attack scenarios currently cached. Use Sync Now to fetch from Gemini.",
                                        color = theme.textSecondary,
                                        fontSize = 13.sp,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(filteredScenarios, key = { it.id }) { scenario ->
                            AttackScenarioCard(scenario = scenario)
                        }
                    }
                }

                // ==================== SECTION 4: SC-200 EXAM PREPARATION ====================
                if (activeMainSection == 4) {
                    item {
                        Sc200ExamSection()
                    }
                }

                // ==================== SECTION 7: SC-500 EXAM PREPARATION ====================
                if (activeMainSection == 7) {
                    item {
                        Sc500ExamSection()
                    }
                }

                // ==================== SECTION 5: ACTIVE DIRECTORY & RED TEAMING MASTERY ====================
                if (activeMainSection == 5) {
                    item {
                        ActiveDirectorySection()
                    }
                }

                // ==================== SECTION 6: ACTIVE DIRECTORY RED TEAMING ====================
                if (activeMainSection == 6) {
                    item {
                        ActiveDirectoryRedTeamSection()
                    }
                }

                // ==================== SECTION 0: DEDICATED MITRE ATT&CK ====================
                if (activeMainSection == 0) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = theme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF43F5E).copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
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
                                            tint = Color(0xFFF43F5E),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "⚔️ MITRE ATT&CK Matrix & Graph",
                                                color = Color(0xFFF43F5E),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Interactive TTP Nodes & Adversary Tactics",
                                                color = theme.textSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { activeFlowChartMode = "ATTACK" },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E), contentColor = Color.White),
                                        modifier = Modifier.testTag("launch_mitre_attack_flow_modal_button")
                                    ) {
                                        Text("Fullscreen", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Embedded Interactive Graph
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.border),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(480.dp)
                                ) {
                                    com.example.ui.components.MitreInteractiveGraphComposable(
                                        initialMode = "ATTACK",
                                        progressMap = ttpProgressMap,
                                        onUpdateProgress = { techId, status -> viewModel.updateTtpProgress(techId, status) },
                                        onClose = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }

                    // 14 Enterprise Tactics Quick Overview
                    item {
                        Text(
                            text = "14 Enterprise Tactics (Adversary Lifecycle)",
                            color = theme.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (tactic in MitreTactic.entries) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = tactic.color.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, tactic.color.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "${tactic.tacticId} • ${tactic.displayName}",
                                        color = tactic.color,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // ==================== SECTION 1: DEDICATED MITRE D3FEND ====================
                if (activeMainSection == 1) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = theme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF06B6D4).copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
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
                                            tint = Color(0xFF06B6D4),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "🛡️ MITRE D3FEND Defend Graph",
                                                color = Color(0xFF06B6D4),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Model • Harden • Detect • Isolate • Deceive",
                                                color = theme.textSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { activeFlowChartMode = "DEFEND" },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4), contentColor = Color.Black),
                                        modifier = Modifier.testTag("launch_mitre_defend_flow_modal_button")
                                    ) {
                                        Text("Fullscreen", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Embedded Interactive Defend Graph
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.border),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(480.dp)
                                ) {
                                    com.example.ui.components.MitreInteractiveGraphComposable(
                                        initialMode = "DEFEND",
                                        progressMap = ttpProgressMap,
                                        onUpdateProgress = { techId, status -> viewModel.updateTtpProgress(techId, status) },
                                        onClose = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }

                    // 5 D3FEND Pillars Overview
                    item {
                        Text(
                            text = "5 Core Countermeasure Pillars",
                            color = theme.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "Model" to Color(0xFF38BDF8),
                                "Harden" to Color(0xFF10B981),
                                "Detect" to Color(0xFFF59E0B),
                                "Isolate" to Color(0xFFA855F7),
                                "Deceive" to Color(0xFFEC4899)
                            ).forEach { (pillar, color) ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = color.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "D3-$pillar",
                                        color = color,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // --- 4/5-Stage Learning Roadmap Stages ---
                if (activeMainSection != 3) {
                    item {
                        Text(
                            text = "Curriculum & Roadmap Stages",
                            color = theme.textPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Step-by-step technical progression from core fundamentals to senior execution",
                            color = theme.textMuted,
                            fontSize = 12.sp
                        )
                    }

                    items(currentTrack.roadmapStages) { stage ->
                        RoadmapStageCard(
                            stage = stage,
                            trackColor = currentTrack.themeColor,
                            onCompleteStage = {
                                viewModel.recordRoadmapStageCompleted(currentTrack.id, stage.phaseNumber)
                                Toast.makeText(context, "Stage ${stage.phaseNumber} mastered! (+1.5 RL Reward)", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                // Dedicated Labs Section for MITRE ATT&CK
                if (activeMainSection == 0) {
                    item {
                        Text(
                            text = "Core Adversary TTPs & Atomic Red Team Labs",
                            color = theme.textPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Offensive technique walkthroughs with Atomic Red Team payloads and execution commands",
                            color = theme.textMuted,
                            fontSize = 12.sp
                        )
                    }

                    items(MitreMatrixRepository.allTtps.take(6), key = { it.techniqueId }) { ttp ->
                        RoadmapTtpLabCard(
                            ttp = ttp,
                            onCopy = { text, label ->
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(label, text)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                // Dedicated Labs Section for MITRE D3FEND
                if (activeMainSection == 1) {
                    item {
                        Text(
                            text = "Model-Driven Countermeasures & Sigma Rules",
                            color = theme.textPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Engineered defense matrices featuring verified Sigma rules and EDR behavioral detections",
                            color = theme.textMuted,
                            fontSize = 12.sp
                        )
                    }

                    items(MitreMatrixRepository.allTtps.take(6), key = { "defend_${it.techniqueId}" }) { ttp ->
                        RoadmapTtpLabCard(
                            ttp = ttp,
                            onCopy = { text, label ->
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(label, text)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                // AI Dynamically Generated Modules
                if (activeMainSection != 3 && dynamicTopics.isNotEmpty()) {
                    item {
                        Text(
                            text = "AI-Generated Advanced Modules (${dynamicTopics.size})",
                            color = theme.accentPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(dynamicTopics, key = { it.id }) { dyn ->
                        RoadmapStageCard(
                            stage = RoadmapStage(
                                phaseNumber = dyn.phaseNumber,
                                title = "${dyn.title} (AI Dynamic)",
                                description = dyn.description,
                                toolsAndSkills = dyn.toolsAndSkillsJson.split(",").map { it.trim() }
                            ),
                            trackColor = theme.accentPrimary,
                            onCompleteStage = {
                                viewModel.recordRoadmapStageCompleted(currentTrack.id, dyn.phaseNumber)
                                Toast.makeText(context, "AI Module mastered! (+1.5 RL Reward)", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                // AI Curriculum Expansion Action
                if (activeMainSection != 3) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = theme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentPrimary.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = theme.accentPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Auto-Enrich Curriculum with Gemini",
                                        color = theme.accentPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Query Gemini AI to generate next-phase modules with hands-on CLI scripts based on latest industry threats.",
                                    color = theme.textMuted,
                                    fontSize = 11.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { viewModel.enrichRoadmapWithGemini(currentTrack) },
                                    enabled = !isEnrichingRoadmap,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = theme.accentPrimary, contentColor = Color.Black),
                                    modifier = Modifier.testTag("enrich_roadmap_button")
                                ) {
                                    if (isEnrichingRoadmap) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                    } else {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Text("Generate Next Phase with AI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Practical Real-World Example & Hands-on Code
                if (activeMainSection != 3) {
                    item {
                        Text(
                            text = "Hands-on Real-World Example",
                            color = theme.textPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = theme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, currentTrack.themeColor.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Code,
                                        contentDescription = null,
                                        tint = currentTrack.themeColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = currentTrack.practicalExampleTitle,
                                        color = currentTrack.themeColor,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = currentTrack.practicalExampleDescription,
                                    color = theme.textSecondary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                RemediationCodeBox(
                                    code = currentTrack.handsOnCodeSnippet,
                                    title = "Practical Execution / Configuration"
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Defensive Countermeasure
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = theme.surfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = CyberEmerald,
                                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Key Architectural Countermeasure:",
                                                color = CyberEmerald,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = currentTrack.defensiveTakeaway,
                                                color = theme.textPrimary,
                                                fontSize = 12.sp,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Interactive AI Mentor Section
                if (activeMainSection != 3) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = theme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentPrimary.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = theme.accentPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Ask Gemini AI About This Track",
                                        color = theme.accentPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Ask for hands-on lab setups, certification advice, mock technical interview questions, or architecture reviews.",
                                    color = theme.textSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Prompt chips
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(
                                        "Suggest home lab setup",
                                        "Top 3 interview questions",
                                        "Common junior mistakes"
                                    ).forEach { prompt ->
                                        Surface(
                                            color = theme.surfaceVariant,
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.border),
                                            onClick = {
                                                userCustomQuestion = prompt
                                                val dummyThreat = ThreatItem(
                                                    id = currentTrack.id,
                                                    title = currentTrack.title,
                                                    description = currentTrack.overview,
                                                    source = FeedSource.DEVSECOPS_WEEKLY,
                                                    link = "https://cisa.gov",
                                                    pubDateMillis = System.currentTimeMillis(),
                                                    severity = ThreatSeverity.HIGH,
                                                    category = ThreatCategory.DEVSECOPS
                                                )
                                                viewModel.askCustomQuestion(dummyThreat, prompt)
                                            }
                                        ) {
                                            Text(
                                                text = prompt,
                                                color = theme.accentPrimary,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = userCustomQuestion,
                                        onValueChange = { userCustomQuestion = it },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("ask_track_ai_input"),
                                        placeholder = {
                                            Text("Ask anything about ${currentTrack.title}...", color = theme.textMuted, fontSize = 13.sp)
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = theme.surfaceVariant,
                                            unfocusedContainerColor = theme.surfaceVariant,
                                            focusedBorderColor = theme.accentPrimary,
                                            unfocusedBorderColor = theme.border,
                                            focusedTextColor = theme.textPrimary,
                                            unfocusedTextColor = theme.textPrimary
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = {
                                            if (userCustomQuestion.isNotBlank()) {
                                                val dummyThreat = ThreatItem(
                                                    id = currentTrack.id,
                                                    title = currentTrack.title,
                                                    description = currentTrack.overview,
                                                    source = FeedSource.DEVSECOPS_WEEKLY,
                                                    link = "https://cisa.gov",
                                                    pubDateMillis = System.currentTimeMillis(),
                                                    severity = ThreatSeverity.HIGH,
                                                    category = ThreatCategory.DEVSECOPS
                                                )
                                                viewModel.askCustomQuestion(dummyThreat, userCustomQuestion)
                                            }
                                        },
                                        enabled = userCustomQuestion.isNotBlank() && !isAskingQuestion,
                                        modifier = Modifier
                                            .background(theme.accentPrimary, RoundedCornerShape(10.dp))
                                            .size(48.dp)
                                            .testTag("send_track_ai_button")
                                    ) {
                                        if (isAskingQuestion) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                color = Color.Black,
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Send,
                                                contentDescription = "Send",
                                                tint = Color.Black
                                            )
                                        }
                                    }
                                }

                                val answer = customQuestionAnswer
                                if (answer != null) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Surface(
                                        color = theme.surfaceVariant,
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, theme.border),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "🤖 Gemini AI Track Guidance",
                                                color = theme.accentPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = answer,
                                                color = theme.textPrimary,
                                                fontSize = 12.sp,
                                                lineHeight = 17.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showGlossarySheet) {
                GlossaryModalSheet(
                    terms = glossaryTerms,
                    isQueryingGemini = isQueryingGlossary,
                    onLookupTermWithGemini = { term -> viewModel.lookupOrGenerateGlossaryTerm(term) },
                    sheetState = glossarySheetState,
                    onDismissRequest = {
                        scope.launch { glossarySheetState.hide() }.invokeOnCompletion {
                            showGlossarySheet = false
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TrackHeaderCard(track: LearningTrack) {
    val theme = CyberTheme.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, track.themeColor.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = track.themeColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, track.themeColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = track.roleBadge,
                        color = track.themeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = track.title,
                color = theme.textPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = track.tagline,
                color = track.themeColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = track.overview,
                color = theme.textSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun RoadmapStageCard(
    stage: com.example.data.learning.RoadmapStage,
    trackColor: Color,
    onCompleteStage: (() -> Unit)? = null
) {
    val theme = CyberTheme.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, theme.border)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = trackColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(26.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${stage.phaseNumber}",
                                color = trackColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stage.title,
                        color = theme.textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (onCompleteStage != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = trackColor.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, trackColor.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clickable { onCompleteStage() }
                            .testTag("complete_stage_${stage.phaseNumber}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = trackColor, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Mastered", color = trackColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stage.description,
                color = theme.textSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tool and Skill Tags
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                stage.toolsAndSkills.forEach { tool ->
                    Surface(
                        color = theme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(0.6.dp, theme.border)
                    ) {
                        Text(
                            text = tool,
                            color = trackColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoadmapTtpLabCard(
    ttp: MitreTtpItem,
    onCopy: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ttp.tactic.color.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ttp.tactic.color.copy(alpha = 0.5f))
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
                    Text(
                        text = ttp.tactic.displayName,
                        color = CyberTextMuted,
                        fontSize = 11.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyberEmerald.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = ttp.d3fendId,
                        color = CyberEmerald,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = ttp.name,
                color = CyberTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = ttp.description,
                color = CyberTextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Attack Simulation Snippet
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
                        Text(
                            text = "⚔️ Attack Simulation: ${ttp.attackExampleTitle}",
                            color = SeverityCritical,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { onCopy(ttp.attackCodeCommand, "Attack Command") },
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = CyberTextMuted, modifier = Modifier.size(13.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ttp.attackCodeCommand,
                        color = Color(0xFFF87171),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF090D16), RoundedCornerShape(4.dp))
                            .padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // D3FEND Countermeasure Snippet
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
                        Text(
                            text = "🛡️ D3FEND Defense: ${ttp.d3fendName}",
                            color = CyberEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { onCopy(ttp.d3fendRuleOrFix, "D3FEND Rule") },
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = CyberTextMuted, modifier = Modifier.size(13.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ttp.d3fendRuleOrFix,
                        color = CyberEmerald,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF090D16), RoundedCornerShape(4.dp))
                            .padding(6.dp)
                    )
                }
            }
        }
    }
}
