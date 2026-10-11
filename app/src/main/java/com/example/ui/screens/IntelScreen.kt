package com.example.ui.screens

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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DateRangeFilter
import com.example.ui.components.CveTrendVisualizationCard
import com.example.ui.components.CyberSearchBar
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.OfflineCacheBanner
import com.example.ui.components.ThreatItemCard
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.CyberTheme
import com.example.ui.viewmodel.ThreatFeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntelScreen(
    viewModel: ThreatFeedViewModel,
    onThreatClick: (String) -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToAlertsLibrary: () -> Unit,
    onNavigateToChatbot: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val threats by viewModel.filteredThreats.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val isOfflineMode by viewModel.isOfflineMode.collectAsStateWithLifecycle()
    val lastSyncTime by viewModel.lastSyncTimeFormatted.collectAsStateWithLifecycle()
    val cvePoints7Days by viewModel.cveTrends7Days.collectAsStateWithLifecycle()
    val cvePoints30Days by viewModel.cveTrends30Days.collectAsStateWithLifecycle()
    val cveSummary by viewModel.cveBreakdownSummary.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val theme = CyberTheme.colors

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = theme.bg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = CyberCyan.copy(alpha = 0.15f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Threat Intel",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "CyberShield Intel",
                                color = theme.textPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Live Feeds • CVE Search • Vulnerability Trends",
                                color = theme.textMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshFeeds() },
                        modifier = Modifier.testTag("btn_refresh_intel")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Threat Feeds",
                            tint = CyberCyan
                        )
                    }
                    IconButton(
                        onClick = { showFilterSheet = true },
                        modifier = Modifier.testTag("btn_open_filters")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter Options",
                            tint = if (filterState.selectedCategories.isNotEmpty() || filterState.selectedSeverities.isNotEmpty() || filterState.onlyBookmarks) CyberEmerald else theme.textSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.surface)
            )
        },
        floatingActionButton = {
            onNavigateToChatbot?.let { launchChat ->
                FloatingActionButton(
                    onClick = { launchChat() },
                    containerColor = CyberCyan,
                    contentColor = Color.Black,
                    modifier = Modifier.testTag("fab_ask_gemini_intel")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Ask Gemini AI",
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Ask AI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(theme.bg)
        ) {
            if (isRefreshing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = CyberCyan,
                    trackColor = theme.surface
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("intel_feed_list"),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    OfflineCacheBanner(
                        cacheStatus = com.example.data.model.OfflineCacheStatus(
                            totalCachedThreats = stats.totalCount,
                            isOfflineMode = isOfflineMode,
                            lastSyncMessage = lastSyncTime
                        ),
                        onSyncClick = { viewModel.refreshFeeds() }
                    )

                    CyberSearchBar(
                        query = filterState.searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        onFilterClick = { showFilterSheet = true },
                        activeFilterCount = filterState.activeFilterCount,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("intel_search_bar")
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = filterState.onlyBookmarks,
                            onClick = { viewModel.toggleOnlyBookmarks() },
                            label = {
                                Text(
                                    text = if (filterState.onlyBookmarks) "Bookmarks" else "Bookmarks",
                                    fontSize = 12.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Saved Bookmarks",
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                                selectedLabelColor = CyberCyan,
                                selectedLeadingIconColor = CyberCyan
                            ),
                            modifier = Modifier.testTag("chip_intel_bookmarks")
                        )

                        DateRangeFilter.entries.forEach { dateRange ->
                            FilterChip(
                                selected = filterState.dateRange == dateRange,
                                onClick = { viewModel.setDateRangeFilter(dateRange) },
                                label = { Text(dateRange.displayName, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberEmerald.copy(alpha = 0.2f),
                                    selectedLabelColor = CyberEmerald
                                )
                            )
                        }

                        if (filterState.hasActiveFilters) {
                            FilterChip(
                                selected = false,
                                onClick = { viewModel.clearAllFilters() },
                                label = { Text("Clear All", fontSize = 12.sp, color = SeverityCritical) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear Filters",
                                        tint = SeverityCritical,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        IntelSummaryCard(
                            totalThreats = stats.totalCount,
                            criticalCount = stats.criticalCount,
                            cveCount = cveSummary.totalCveRecords,
                            unreadAlerts = stats.unreadAlertsCount,
                            onAlertsClick = onNavigateToAlerts
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Investigation & Remediation Guide Shortcut
                        Surface(
                            onClick = onNavigateToAlertsLibrary,
                            color = CyberTheme.colors.surface,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Alert Investigation & Remediation Guide", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyberTheme.colors.textPrimary)
                                    Text("150+ Expert SOC Scenarios & Playbooks", fontSize = 10.sp, color = CyberTheme.colors.textSecondary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        CveTrendVisualizationCard(
                            points7Days = cvePoints7Days,
                            points30Days = cvePoints30Days,
                            summary = cveSummary,
                            onCveClick = { cveId ->
                                viewModel.selectCve(cveId)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LIVE THREAT STREAM (${threats.size})",
                                color = theme.textMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = filterState.dateRange.displayName,
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                if (threats.isEmpty() && !isRefreshing) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "No Threats Found",
                                    tint = theme.textMuted,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "No threat advisories match current filters",
                                    color = theme.textSecondary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                OutlinedButton(
                                    onClick = { viewModel.clearAllFilters() },
                                    modifier = Modifier.testTag("btn_intel_reset_empty")
                                ) {
                                    Text("Reset Filters")
                                }
                            }
                        }
                    }
                }

                items(
                    items = threats,
                    key = { it.id }
                ) { threat ->
                    ThreatItemCard(
                        threat = threat,
                        onClick = { onThreatClick(threat.id) },
                        onBookmarkToggle = { viewModel.toggleBookmark(threat) },
                        onCveClick = { cveId -> viewModel.selectCve(cveId, threat) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            filterState = filterState,
            sheetState = sheetState,
            onDismissRequest = { showFilterSheet = false },
            onFilterChange = { updated -> viewModel.filterState.value = updated }
        )
    }
}

@Composable
private fun IntelSummaryCard(
    totalThreats: Int,
    criticalCount: Int,
    cveCount: Int,
    unreadAlerts: Int,
    onAlertsClick: () -> Unit
) {
    val theme = CyberTheme.colors

    Surface(
        color = theme.surface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$totalThreats",
                    color = CyberCyan,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Active Threats",
                    color = theme.textMuted,
                    fontSize = 10.sp
                )
            }

            Box(
                modifier = Modifier
                    .height(28.dp)
                    .width(1.dp)
                    .background(CyberBorder)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$criticalCount",
                    color = SeverityCritical,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Critical RCE",
                    color = theme.textMuted,
                    fontSize = 10.sp
                )
            }

            Box(
                modifier = Modifier
                    .height(28.dp)
                    .width(1.dp)
                    .background(CyberBorder)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$cveCount",
                    color = CyberEmerald,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "CVE Identifiers",
                    color = theme.textMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
