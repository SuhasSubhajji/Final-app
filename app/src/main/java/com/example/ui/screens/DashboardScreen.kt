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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.example.data.rl.SecurityDomain
import com.example.ui.components.AppUpdateBanner
import com.example.ui.components.GitHubUpdateSettingsDialog
import com.example.ui.components.SocReinforcementLearningCard
import com.example.ui.components.ThemeSelectorDialog
import com.example.ui.theme.CyberTheme
import com.example.ui.theme.CyberThemeMode
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
    val cveSearchQuery by viewModel.cveSearchQuery.collectAsState()
    val searchedCveThreats by viewModel.searchedCveThreats.collectAsState()
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

    val appUpdateInfo by viewModel.appUpdateInfo.collectAsState()
    val configuredRepo by viewModel.configuredRepo.collectAsState()
    val isCheckingUpdate by viewModel.isCheckingUpdate.collectAsState()
    val updateCheckResult by viewModel.updateCheckResult.collectAsState()
    var showUpdateSettingsDialog by remember { mutableStateOf(false) }

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

    if (showUpdateSettingsDialog) {
        GitHubUpdateSettingsDialog(
            currentRepo = configuredRepo,
            currentVersionName = com.example.BuildConfig.VERSION_NAME,
            currentVersionCode = com.example.BuildConfig.VERSION_CODE,
            isChecking = isCheckingUpdate,
            checkResult = updateCheckResult,
            onCheckNow = { viewModel.checkForAppUpdates(isUserInitiated = true) },
            onSaveRepo = { viewModel.setGitHubRepository(it) },
            onDismiss = {
                viewModel.clearUpdateCheckResult()
                showUpdateSettingsDialog = false
            }
        )
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

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
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Dashboard,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CyberShield SOC",
                                color = CyberTheme.colors.textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "INTELLIGENCE ACTIVE",
                                color = CyberEmerald,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
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
                            contentDescription = "Theme Display Mode",
                            tint = CyberTheme.colors.accentPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { showUpdateSettingsDialog = true },
                        modifier = Modifier.testTag("dashboard_update_settings_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (appUpdateInfo != null) {
                                    Badge(
                                        containerColor = SeverityCritical,
                                        modifier = Modifier.size(8.dp)
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.SystemUpdate,
                                contentDescription = "Updates",
                                tint = if (appUpdateInfo != null) CyberCyan else CyberTheme.colors.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { showGlossarySheet = true },
                        modifier = Modifier.testTag("dashboard_glossary_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = "Glossary",
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.refreshFeeds() },
                        modifier = Modifier.testTag("dashboard_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = CyberTheme.colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
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
                .testTag("dashboard_scroll_content")
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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

            // In-app update alert banner if a new release is available
            appUpdateInfo?.let { update ->
                item {
                    AppUpdateBanner(
                        updateInfo = update,
                        onUpdateClick = { viewModel.downloadAndInstallUpdate(update) },
                        onDismiss = { viewModel.dismissAppUpdate() }
                    )
                }
            }

            // SECTION 1: Key Command Center Metrics (More Compact)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Threats",
                        count = stats.totalCount.toString(),
                        accentColor = CyberCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Critical",
                        count = stats.criticalCount.toString(),
                        accentColor = SeverityCritical,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "DevSecOps",
                        count = stats.devSecOpsCount.toString(),
                        accentColor = CyberEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                MetricCard(
                    title = "Security Alerts Library (Investigation & Remediation)",
                    count = "150+ Curated Scenarios",
                    accentColor = CyberCyan,
                    onClick = onNavigateToAlerts,
                    modifier = Modifier.fillMaxWidth(),
                    compact = true
                )
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

                // SECTION 6: Searchable CVE Query List View
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "CVE Threat Database Search",
                                    color = CyberTextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Query CVE identifiers, CVSS severity, or attack vectors in real-time",
                                    color = CyberTextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Search Input Field
                        androidx.compose.material3.OutlinedTextField(
                            value = cveSearchQuery,
                            onValueChange = { viewModel.setCveSearchQuery(it) },
                            placeholder = {
                                Text(
                                    text = "Search CVEs (e.g. CVE-2025-0282, Log4j, RCE)...",
                                    color = CyberTextMuted,
                                    fontSize = 12.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.AutoAwesome,
                                    contentDescription = "Search CVEs",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                if (cveSearchQuery.isNotBlank()) {
                                    IconButton(onClick = { viewModel.setCveSearchQuery("") }) {
                                        Icon(
                                            imageVector = androidx.compose.material.icons.Icons.Default.Refresh,
                                            contentDescription = "Clear Search",
                                            tint = CyberTextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("cve_search_input_field"),
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CyberNavySurface,
                                unfocusedContainerColor = CyberNavySurface,
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary,
                                cursorColor = CyberCyan
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Search Shortcut Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val quickQueries = listOf(
                                "ALL",
                                "CVE-2025",
                                "CVE-2024",
                                "RCE",
                                "Zero-Day",
                                "Ivanti",
                                "npm"
                            )
                            quickQueries.forEach { q ->
                                val isSelected = (q == "ALL" && cveSearchQuery.isBlank()) || (q != "ALL" && cveSearchQuery.equals(q, true))
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.setCveSearchQuery(if (q == "ALL") "" else q)
                                    },
                                    label = { Text(q, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                                        selectedLabelColor = CyberCyan
                                    ),
                                    modifier = Modifier.height(30.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (cveSearchQuery.isNotBlank()) {
                                "Found ${searchedCveThreats.size} CVE items matching \"$cveSearchQuery\""
                            } else {
                                "Displaying ${searchedCveThreats.take(8).size} active CVE advisories"
                            },
                            color = if (cveSearchQuery.isNotBlank()) CyberCyan else CyberTextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                val itemsToDisplay = if (cveSearchQuery.isNotBlank()) searchedCveThreats else searchedCveThreats.take(8)

                if (itemsToDisplay.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CyberTheme.colors.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberTheme.colors.border),
                            modifier = Modifier.fillMaxWidth().testTag("cve_search_empty_state")
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = CyberTheme.colors.accentPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No CVE Advisories Found",
                                    color = CyberTheme.colors.textPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "No active threats match \"$cveSearchQuery\". Try searching by year (\"CVE-2025\"), keyword (\"RCE\"), or reset query.",
                                    color = CyberTheme.colors.textSecondary,
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedButton(
                                    onClick = { viewModel.setCveSearchQuery("") },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberTheme.colors.accentPrimary),
                                    modifier = Modifier.testTag("cve_search_clear_button")
                                ) {
                                    Text("Clear Search Query", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                } else {
                    items(itemsToDisplay, key = { it.id }) { item ->
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
                            },
                            onCveClick = { cveId ->
                                viewModel.selectCve(cveId, item)
                            }
                        )
                    }
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
                        summary = cveSummary,
                        onCveClick = { cveId ->
                            viewModel.selectCve(cveId)
                        }
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
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    val theme = CyberTheme.colors
    Surface(
        color = theme.surface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, theme.border),
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = modifier.height(if (compact) 54.dp else 70.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(if (compact) 8.dp else 10.dp)
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
                    color = theme.textSecondary,
                    fontSize = if (compact) 10.sp else 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
            }
            Text(
                text = count,
                color = accentColor,
                fontSize = if (compact) 16.sp else 18.sp,
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
    onCveClick: ((String) -> Unit)? = null,
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
                    val cveId = threat.cveIds.first()
                    Surface(
                        color = theme.accentPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(0.6.dp, theme.accentPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clickable(enabled = onCveClick != null) { onCveClick?.invoke(cveId) }
                            .testTag("dashboard_cve_tag_$cveId")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cveId,
                                color = theme.accentPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (onCveClick != null) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "CVE Info",
                                    tint = theme.accentPrimary.copy(alpha = 0.8f),
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
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
