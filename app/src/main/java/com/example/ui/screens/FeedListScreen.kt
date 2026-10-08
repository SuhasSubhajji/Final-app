package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DateRangeFilter
import com.example.data.model.ThreatCategory
import com.example.data.model.ThreatItem
import com.example.data.model.ThreatSeverity
import com.example.ui.components.CyberSearchBar
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.ThreatItemCard
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
import com.example.ui.components.CyberThemeToggleButton
import com.example.ui.components.ThemeSelectorDialog
import com.example.ui.viewmodel.ThreatFeedViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedListScreen(
    viewModel: ThreatFeedViewModel,
    onThreatClick: (String) -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToChatbot: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val threats by viewModel.filteredThreats.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val themeMode by viewModel.currentThemeMode.collectAsState()
    val isRlSortEnabled by viewModel.isRlSortEnabled.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }
    var showHistorySheet by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val theme = CyberTheme.colors

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
            currentMode = themeMode,
            onSelectMode = { selected ->
                viewModel.setThemeMode(selected)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    // Notification permission launcher for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.runThreatIntelScan(context)
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
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = theme.accentPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CyberShield",
                                color = theme.textPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(theme.accentSecondary)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "THREAT INTEL & DEVSECOPS",
                                    color = theme.accentSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Persistent Theme Mode Toggle
                    CyberThemeToggleButton(
                        currentMode = themeMode,
                        onClick = { showThemeDialog = true }
                    )

                    // Quick scan & push alert trigger
                    IconButton(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.runThreatIntelScan(context)
                            }
                        },
                        modifier = Modifier.testTag("run_scan_action_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Trigger threat push alerts",
                            tint = if (stats.unreadAlertsCount > 0) SeverityCritical else theme.accentPrimary
                        )
                    }

                    // Threat History Button
                    IconButton(
                        onClick = { showHistorySheet = true },
                        modifier = Modifier.testTag("open_threat_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Threat Investigation History",
                            tint = theme.accentPrimary
                        )
                    }

                    if (onNavigateToChatbot != null) {
                        IconButton(
                            onClick = onNavigateToChatbot,
                            modifier = Modifier.testTag("feed_gemini_chatbot_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini AI Chatbot",
                                tint = theme.accentPrimary
                            )
                        }
                    }

                    // Refresh feeds
                    IconButton(
                        onClick = { viewModel.refreshFeeds() },
                        modifier = Modifier.testTag("refresh_feeds_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh feeds",
                            tint = theme.textSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = theme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Linear progress when refreshing
            AnimatedVisibility(visible = isRefreshing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = theme.accentPrimary,
                    trackColor = theme.surfaceVariant
                )
            }

            // Stats summary bar
            StatsBar(
                stats = stats,
                onCriticalClick = {
                    viewModel.toggleSeverityFilter(ThreatSeverity.CRITICAL)
                },
                onDevSecOpsClick = {
                    viewModel.toggleCategoryFilter(ThreatCategory.DEVSECOPS)
                },
                onAlertsClick = onNavigateToAlerts
            )

            // Search bar
            CyberSearchBar(
                query = filterState.searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                onFilterClick = { showFilterSheet = true },
                activeFilterCount = filterState.activeFilterCount
            )

            // Horizontal quick filter pills
            QuickFilterPillsRow(
                filterState = filterState,
                isRlSortActive = isRlSortEnabled,
                onToggleRlSort = { viewModel.toggleRlSort() },
                onToggleCritical = { viewModel.toggleSeverityFilter(ThreatSeverity.CRITICAL) },
                onToggleDevSecOps = { viewModel.toggleCategoryFilter(ThreatCategory.DEVSECOPS) },
                onTogglePast24h = {
                    val target = if (filterState.dateRange == DateRangeFilter.PAST_24_HOURS) DateRangeFilter.ALL else DateRangeFilter.PAST_24_HOURS
                    viewModel.setDateRangeFilter(target)
                },
                onToggleBookmarks = { viewModel.toggleOnlyBookmarks() },
                onOpenHistory = { showHistorySheet = true },
                onClearAll = { viewModel.clearAllFilters() }
            )

            // Threat List
            if (threats.isEmpty()) {
                EmptyThreatState(
                    hasFilters = filterState.hasActiveFilters,
                    onResetFilters = { viewModel.clearAllFilters() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("threat_feed_list"),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = threats,
                        key = { it.id }
                    ) { item ->
                        ThreatItemCard(
                            threat = item,
                            onClick = { onThreatClick(item.id) },
                            onBookmarkToggle = { viewModel.toggleBookmark(item) },
                            rlMatchScore = viewModel.getThreatRelevanceScore(item)
                        )
                    }
                }
            }
        }
    }

    // Modal Filter Sheet
    if (showFilterSheet) {
        FilterBottomSheet(
            filterState = filterState,
            onFilterChange = { updated ->
                // Apply update directly
                viewModel.filterState.value = updated
            },
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    showFilterSheet = false
                }
            }
        )
    }
}

@Composable
private fun StatsBar(
    stats: com.example.data.model.ThreatStats,
    onCriticalClick: () -> Unit,
    onDevSecOpsClick: () -> Unit,
    onAlertsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatPill(
            label = "Threats",
            count = stats.totalCount.toString(),
            color = CyberCyan,
            modifier = Modifier.weight(1f)
        )
        StatPill(
            label = "Critical",
            count = stats.criticalCount.toString(),
            color = SeverityCritical,
            onClick = onCriticalClick,
            modifier = Modifier.weight(1f)
        )
        StatPill(
            label = "DevSecOps",
            count = stats.devSecOpsCount.toString(),
            color = CyberEmerald,
            onClick = onDevSecOpsClick,
            modifier = Modifier.weight(1.2f)
        )
        StatPill(
            label = "Push Alerts",
            count = stats.unreadAlertsCount.toString(),
            color = Color(0xFFF59E0B),
            onClick = onAlertsClick,
            modifier = Modifier.weight(1.2f)
        )
    }
}

@Composable
private fun StatPill(
    label: String,
    count: String,
    color: Color,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CyberNavySurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = modifier.height(46.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                color = color,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = CyberTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun QuickFilterPillsRow(
    filterState: com.example.data.model.FeedFilterState,
    isRlSortActive: Boolean,
    onToggleRlSort: () -> Unit,
    onToggleCritical: () -> Unit,
    onToggleDevSecOps: () -> Unit,
    onTogglePast24h: () -> Unit,
    onToggleBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = CyberTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // RL Smart Sort Pill
        FilterChip(
            selected = isRlSortActive,
            onClick = onToggleRlSort,
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isRlSortActive) "🤖 RL Active" else "🤖 RL Priority", fontSize = 12.sp)
                }
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = theme.accentPrimary.copy(alpha = 0.25f),
                selectedLabelColor = theme.accentPrimary,
                selectedLeadingIconColor = theme.accentPrimary
            ),
            modifier = Modifier.testTag("quick_filter_rl_priority")
        )

        if (filterState.hasActiveFilters) {
            Surface(
                color = SeverityCritical.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SeverityCritical.copy(alpha = 0.5f)),
                onClick = onClearAll,
                modifier = Modifier.testTag("quick_filter_clear_all")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear all",
                        tint = SeverityCritical,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Reset (${filterState.activeFilterCount})",
                        color = SeverityCritical,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        val isCriticalActive = ThreatSeverity.CRITICAL in filterState.selectedSeverities
        FilterChip(
            selected = isCriticalActive,
            onClick = onToggleCritical,
            label = { Text("🚨 Critical Only", fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = SeverityCritical.copy(alpha = 0.2f),
                selectedLabelColor = SeverityCritical
            ),
            modifier = Modifier.testTag("quick_filter_critical")
        )

        val isDevSecOpsActive = ThreatCategory.DEVSECOPS in filterState.selectedCategories
        FilterChip(
            selected = isDevSecOpsActive,
            onClick = onToggleDevSecOps,
            label = { Text("⚡ DevSecOps", fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = CyberEmerald.copy(alpha = 0.2f),
                selectedLabelColor = CyberEmerald
            ),
            modifier = Modifier.testTag("quick_filter_devsecops")
        )

        val is24hActive = filterState.dateRange == DateRangeFilter.PAST_24_HOURS
        FilterChip(
            selected = is24hActive,
            onClick = onTogglePast24h,
            label = { Text("⏱️ Past 24h", fontSize = 12.sp) },
            modifier = Modifier.testTag("quick_filter_24h")
        )

        FilterChip(
            selected = filterState.onlyBookmarks,
            onClick = onToggleBookmarks,
            label = { Text("Saved", fontSize = 12.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
            },
            modifier = Modifier.testTag("quick_filter_saved")
        )

        FilterChip(
            selected = false,
            onClick = onOpenHistory,
            label = { Text("📜 History", fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
                containerColor = CyberCyan.copy(alpha = 0.1f),
                labelColor = CyberCyan
            ),
            modifier = Modifier.testTag("quick_filter_history")
        )
    }
}

@Composable
private fun EmptyThreatState(
    hasFilters: Boolean,
    onResetFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = CyberCyan,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (hasFilters) "No Matching Threats Found" else "No Threat Feeds Available",
            color = CyberTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (hasFilters) "Try adjusting your search keywords, feed sources, or alert severities." else "Connecting to live security feeds...",
            color = CyberTextSecondary,
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        if (hasFilters) {
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(
                onClick = onResetFilters,
                modifier = Modifier.testTag("reset_filters_empty_state_button")
            ) {
                Text("Reset All Filters", color = CyberCyan)
            }
        }
    }
}
