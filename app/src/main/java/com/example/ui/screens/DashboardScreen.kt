package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import com.example.data.rl.SecurityDomain
import com.example.ui.components.SocReinforcementLearningCard
import com.example.ui.components.ThemeSelectorDialog
import com.example.ui.theme.CyberTheme
import com.example.ui.theme.CyberThemeMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ThreatItem
import com.example.data.model.ThreatSeverity
import com.example.data.model.TopicOfTheDay
import com.example.ui.components.AlertTrendChart
import com.example.ui.components.CvssSeverityBadge
import com.example.ui.components.GlossaryModalSheet
import com.example.ui.components.SourceBreakdownChart
import com.example.ui.components.TopVulnerabilitiesChart
import com.example.ui.components.formatRelativeTime
import kotlinx.coroutines.launch
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
import com.example.ui.viewmodel.ThreatFeedViewModel

enum class DashboardTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    LIVE_FEED("Live Intel", Icons.Default.Security),
    SOC_ANALYTICS("Telemetry & SOC", Icons.Default.Psychology)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ThreatFeedViewModel,
    onThreatClick: (String) -> Unit,
    onNavigateToRoadmaps: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToHub: (() -> Unit)? = null,
    onNavigateToChatbot: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val stats by viewModel.stats.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val threats by viewModel.filteredThreats.collectAsState()
    val points7Days by viewModel.alertTrends7Days.collectAsState()
    val points30Days by viewModel.alertTrends30Days.collectAsState()
    val cvePoints7Days by viewModel.cveTrends7Days.collectAsState()
    val cvePoints30Days by viewModel.cveTrends30Days.collectAsState()
    val cveSummary by viewModel.cveBreakdownSummary.collectAsState()
    val topVulns by viewModel.topVulnerabilities.collectAsState()
    val sourceBreakdown by viewModel.sourceBreakdown.collectAsState()
    val offlineCacheStatus by viewModel.offlineCacheStatus.collectAsState()
    val dailyTopic by viewModel.topicOfTheDay.collectAsState()
    val topicExplanation by viewModel.topicExplanation.collectAsState()
    val isExplainingTopic by viewModel.isExplainingTopic.collectAsState()

    var showTopicDetails by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(DashboardTab.LIVE_FEED) }

    val glossaryTerms by viewModel.glossaryTerms.collectAsState()
    val isQueryingGlossary by viewModel.isQueryingGlossary.collectAsState()
    var showGlossarySheet by remember { mutableStateOf(false) }
    val glossarySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val currentThemeMode by viewModel.currentThemeMode.collectAsState()
    val rlState by viewModel.rlAgentState.collectAsState()
    val historyItems by viewModel.threatHistory.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }

    var showHistorySheet by remember { mutableStateOf(false) }
    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showHistorySheet) {
        com.example.ui.components.ThreatHistorySheet(
            viewModel = viewModel,
            sheetState = historySheetState,
            onDismiss = { showHistorySheet = false },
            onThreatClick = onThreatClick
        )
    }

    if (showThemeDialog) {
        ThemeSelectorDialog(
            currentMode = currentThemeMode,
            onSelectMode = { viewModel.setThemeMode(it) },
            onDismiss = { showThemeDialog = false }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberTheme.colors.bg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CyberCyan.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Dashboard,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Security Command Center",
                                color = CyberTheme.colors.textPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(CyberEmerald)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "LOCAL INTELLIGENCE ACTIVE",
                                    color = CyberEmerald,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.4.sp
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Persistent SOC Theme Toggle
                    IconButton(
                        onClick = { showThemeDialog = true },
                        modifier = Modifier.testTag("dashboard_theme_button")
                    ) {
                        val themeIcon = when (currentThemeMode) {
                            CyberThemeMode.TACTICAL_DARK -> Icons.Default.DarkMode
                            CyberThemeMode.OLED_BLACK -> Icons.Default.Brightness2
                            CyberThemeMode.CYBER_MATRIX -> Icons.Default.Terminal
                            CyberThemeMode.LIGHT_ANALYST -> Icons.Default.LightMode
                        }
                        Icon(
                            imageVector = themeIcon,
                            contentDescription = "Theme Display Mode: ${currentThemeMode.displayName}",
                            tint = CyberTheme.colors.accentPrimary
                        )
                    }

                    IconButton(
                        onClick = { showGlossarySheet = true },
                        modifier = Modifier.testTag("dashboard_glossary_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = "Cyber Glossary",
                            tint = CyberCyan
                        )
                    }

                    if (onNavigateToChatbot != null) {
                        IconButton(
                            onClick = onNavigateToChatbot,
                            modifier = Modifier.testTag("dashboard_gemini_chatbot_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini AI Chatbot",
                                tint = CyberTheme.colors.accentPrimary
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.refreshFeeds() },
                        modifier = Modifier.testTag("dashboard_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = CyberTheme.colors.textSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberTheme.colors.bg)
            )
        },
        floatingActionButton = {
            if (onNavigateToChatbot != null) {
                androidx.compose.material3.ExtendedFloatingActionButton(
                    onClick = onNavigateToChatbot,
                    containerColor = CyberTheme.colors.accentPrimary,
                    contentColor = Color.Black,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("dashboard_gemini_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ask Gemini",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("dashboard_scroll_content"),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Linear progress on refresh
            if (isRefreshing) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = CyberCyan,
                        trackColor = CyberNavySurfaceVariant
                    )
                }
            }

            // Custom Tactical Tab Selector
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberTheme.colors.surface, shape = RoundedCornerShape(12.dp))
                        .border(1.dp, CyberTheme.colors.border, shape = RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    DashboardTab.entries.forEach { tab ->
                        val selected = selectedTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (selected) CyberTheme.colors.accentPrimary.copy(alpha = 0.15f) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (selected) CyberTheme.colors.accentPrimary.copy(alpha = 0.4f) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedTab = tab }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = null,
                                    tint = if (selected) CyberTheme.colors.accentPrimary else CyberTheme.colors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tab.label,
                                    color = if (selected) CyberTheme.colors.accentPrimary else CyberTheme.colors.textSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            if (selectedTab == DashboardTab.LIVE_FEED) {
                // Room Database Offline Caching & Telemetry Sync Banner
                item {
                    com.example.ui.components.OfflineCacheBanner(
                        cacheStatus = offlineCacheStatus,
                        onSyncClick = { viewModel.refreshFeeds() }
                    )
                }

                // SECTION 1: Key Metrics Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "Total Threats",
                            count = stats.totalCount.toString(),
                            accentColor = CyberCyan,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Critical Zero-Days",
                            count = stats.criticalCount.toString(),
                            accentColor = SeverityCritical,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "DevSecOps Alerts",
                            count = stats.devSecOpsCount.toString(),
                            accentColor = CyberEmerald,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Active Push Alerts",
                            count = stats.unreadAlertsCount.toString(),
                            accentColor = SeverityHigh,
                            onClick = onNavigateToAlerts,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // SECTION 2: Topic of the Day Card
                item {
                    TopicOfTheDayCard(
                        topic = dailyTopic,
                        isExplaining = isExplainingTopic,
                        explanation = topicExplanation,
                        expanded = showTopicDetails,
                        onToggleExpand = { showTopicDetails = !showTopicDetails },
                        onExplainWithGemini = { viewModel.explainTopicWithGemini(dailyTopic) },
                        onNavigateToRoadmaps = onNavigateToRoadmaps
                    )
                }

                // SECTION 6: Recent Vulnerabilities Feed Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Recent Security Vulnerabilities",
                                color = CyberTextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Real-time feed with CVSS severity badges & AI analysis",
                                color = CyberTextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                items(threats.take(6), key = { it.id }) { item ->
                    val rlScore = viewModel.getThreatRelevanceScore(item)
                    DashboardThreatCard(
                        threat = item,
                        rlScore = rlScore,
                        onClick = {
                            viewModel.recordThreatClick(item)
                            onThreatClick(item.id)
                        },
                        onBookmarkToggle = {
                            viewModel.recordThreatBookmark(item)
                            viewModel.toggleBookmark(item)
                        },
                        onSummarizeClick = {
                            viewModel.recordThreatClick(item)
                            onThreatClick(item.id)
                            viewModel.requestAiSummary(item)
                        },
                        onFeedback = { isPositive ->
                            viewModel.recordThreatFeedback(item, isPositive)
                        }
                    )
                }
            } else {
                // SECTION: Adaptive SOC Reinforcement Learning Agent
                item {
                    SocReinforcementLearningCard(
                        state = rlState,
                        onSimulateStep = {
                            val sampleDomain = SecurityDomain.entries.random()
                            viewModel.rlEngine.recordReward(sampleDomain, 1.2f, "Simulated Threat Telemetry Stream")
                        },
                        onResetAgent = {
                            viewModel.resetRlAgent()
                        }
                    )
                }

                // Quick Intel Navigation: MITRE Matrix & 215+ Feeds Directory
                if (onNavigateToHub != null) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = CyberNavySurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SeverityCritical.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToHub() }
                                    .testTag("dashboard_nav_mitre")
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = SeverityCritical,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "MITRE MATRIX",
                                            color = SeverityCritical,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "14 Tactics & Defend Rules",
                                        color = CyberTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Explore 30+ Enterprise TTPs",
                                        color = CyberTextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = CyberNavySurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToHub() }
                                    .testTag("dashboard_nav_feeds_catalog")
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = CyberCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "215+ FEEDS",
                                            color = CyberCyan,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Global Threat Directory",
                                        color = CyberTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "DevOps, Red/Blue, Dark Web",
                                        color = CyberTextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // SECTION 3: Dedicated D3 / Recharts CVE Frequency & Severity Trend Engine
                item {
                    com.example.ui.components.CveTrendVisualizationCard(
                        points7Days = cvePoints7Days,
                        points30Days = cvePoints30Days,
                        summary = cveSummary
                    )
                }

                // SECTION 3b: General Alert Volume Trends
                item {
                    AlertTrendChart(
                        points7Days = points7Days,
                        points30Days = points30Days
                    )
                }

                // SECTION 4: Top 5 Vulnerability Types Chart
                item {
                    TopVulnerabilitiesChart(vulns = topVulns)
                }

                // SECTION 5: Breakdown of Alerts by Source
                item {
                    SourceBreakdownChart(sources = sourceBreakdown)
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

@Composable
private fun MetricCard(
    title: String,
    count: String,
    accentColor: Color,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CyberNavySurface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = modifier.height(84.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = CyberTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
            }
            Text(
                text = count,
                color = accentColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TopicOfTheDayCard(
    topic: TopicOfTheDay,
    isExplaining: Boolean,
    explanation: String?,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onExplainWithGemini: () -> Unit,
    onNavigateToRoadmaps: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("topic_of_the_day_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = CyberEmerald.copy(alpha = 0.15f),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = CyberEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "TOPIC OF THE DAY",
                            color = CyberEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = topic.category,
                            color = CyberTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyberNavySurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, CyberBorder),
                    onClick = onNavigateToRoadmaps
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Roadmaps", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = topic.title,
                color = CyberTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = topic.summary,
                color = CyberTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "Attack Vector:",
                        color = SeverityCritical,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = topic.attackVector,
                        color = CyberTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Defense Strategy:",
                        color = CyberEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = topic.defenseStrategy,
                        color = CyberTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Key Takeaways:",
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    topic.keyTakeaways.forEach { takeaway ->
                        Text(
                            text = "• $takeaway",
                            color = CyberTextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }

                    if (explanation != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CyberNavyDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "🤖 Gemini AI Deep Dive Analysis",
                                    color = CyberCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = explanation,
                                    color = CyberTextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onToggleExpand,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberTextSecondary),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(if (expanded) "Show Less" else "Deep Dive", fontSize = 11.sp)
                }

                Button(
                    onClick = onExplainWithGemini,
                    enabled = !isExplaining,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald, contentColor = CyberNavyDark),
                    modifier = Modifier.height(34.dp).testTag("explain_topic_gemini_button")
                ) {
                    if (isExplaining) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = CyberNavyDark, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text("Teach with Gemini AI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DashboardThreatCard(
    threat: ThreatItem,
    rlScore: Int = 85,
    onClick: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onSummarizeClick: () -> Unit,
    onFeedback: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val theme = CyberTheme.colors

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("dashboard_threat_${threat.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (threat.severity == ThreatSeverity.CRITICAL) SeverityCritical.copy(alpha = 0.5f) else theme.border
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top: Source, Date, RL Match Badge, and Visual Color-Coded CVSS Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = threat.source.displayName,
                        color = theme.accentPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${formatRelativeTime(threat.pubDateMillis)}",
                        color = theme.textMuted,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // RL Adaptive Match Score Badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = theme.accentSecondary.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(0.6.dp, theme.accentSecondary.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = theme.accentSecondary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${rlScore}% RL",
                                color = theme.accentSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Visual color-coded badge based on severity / CVSS
                    CvssSeverityBadge(severity = threat.severity)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = threat.title,
                color = theme.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 19.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Concise Human-Readable Gemini Summary or Description Snippet
            if (!threat.aiSummary.isNullOrBlank()) {
                Surface(
                    color = CyberNavyDark,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "GEMINI SUMMARY",
                                color = CyberCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.4.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = threat.aiSummary,
                            color = CyberTextPrimary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            } else {
                Text(
                    text = threat.description,
                    color = theme.textSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // CVE Tag or Category
                if (threat.cveIds.isNotEmpty()) {
                    Surface(
                        color = theme.accentPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = threat.cveIds.first(),
                            color = theme.accentPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Text(
                        text = threat.category.displayName,
                        color = theme.textMuted,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // RL Agent Direct Feedback (Thumbs Up / Thumbs Down)
                    if (onFeedback != null) {
                        IconButton(
                            onClick = { onFeedback(true) },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("rl_up_${threat.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Prioritize Vector in RL Agent",
                                tint = theme.accentSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        IconButton(
                            onClick = { onFeedback(false) },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("rl_down_${threat.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbDown,
                                contentDescription = "Deprioritize Vector in RL Agent",
                                tint = theme.textMuted,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    // Quick Gemini Summarize Action
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = theme.accentPrimary.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.accentPrimary.copy(alpha = 0.4f)),
                        onClick = onSummarizeClick,
                        modifier = Modifier.testTag("ai_summarize_btn_${threat.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = theme.accentPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI Summarize",
                                color = theme.accentPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (threat.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (threat.isBookmarked) theme.accentPrimary else theme.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
