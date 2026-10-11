package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.BuildConfig
import com.example.data.feeds.FeedCatalogRepository
import com.example.ui.components.ThemeSelectorDialog
import com.example.ui.components.ThreatItemCard
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberTheme
import com.example.ui.viewmodel.ThreatFeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: ThreatFeedViewModel,
    onThreatClick: (String) -> Unit,
    onNavigateToChatbot: (() -> Unit)? = null,
    onNavigateToTechStack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val theme = CyberTheme.colors
    var selectedSubTab by remember { mutableIntStateOf(0) }
    var showThemeDialog by remember { mutableStateOf(false) }

    val bookmarkedThreats by viewModel.bookmarkedThreats.collectAsStateWithLifecycle()
    val themeMode by viewModel.currentThemeMode.collectAsStateWithLifecycle()
    val configuredRepo by viewModel.configuredRepo.collectAsStateWithLifecycle()
    val isCheckingUpdate by viewModel.isCheckingUpdate.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = theme.bg,
        topBar = {
            Column {
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
                                        imageVector = Icons.Default.FolderSpecial,
                                        contentDescription = "Library & Settings",
                                        tint = CyberCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Library & Settings",
                                    color = theme.textPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Bookmarks • Feed Sources • App Preferences",
                                    color = theme.textMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.surface)
                )

                ScrollableTabRow(
                    selectedTabIndex = selectedSubTab,
                    containerColor = theme.surface,
                    contentColor = CyberCyan,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        if (selectedSubTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                                color = CyberCyan
                            )
                        }
                    }
                ) {
                    Tab(
                        selected = selectedSubTab == 0,
                        onClick = { selectedSubTab = 0 },
                        text = { Text("Bookmarks (${bookmarkedThreats.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_library_bookmarks")
                    )
                    Tab(
                        selected = selectedSubTab == 1,
                        onClick = { selectedSubTab = 1 },
                        text = { Text("200+ Feed Catalog", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_library_feeds")
                    )
                    Tab(
                        selected = selectedSubTab == 2,
                        onClick = { selectedSubTab = 2 },
                        text = { Text("Settings & Updates", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_library_settings")
                    )
                }
            }
        },
        floatingActionButton = {
            onNavigateToChatbot?.let { launchChat ->
                FloatingActionButton(
                    onClick = { launchChat() },
                    containerColor = CyberCyan,
                    contentColor = Color.Black,
                    modifier = Modifier.testTag("fab_ask_gemini_library")
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
                        Text("Ask AI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
            when (selectedSubTab) {
                0 -> {
                    if (bookmarkedThreats.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "No Bookmarks",
                                    tint = theme.textMuted,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No saved bookmarks yet",
                                    color = theme.textSecondary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Tap the bookmark icon on any threat item in Intel stream to save it here for offline reading.",
                                    color = theme.textMuted,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                        ) {
                            items(bookmarkedThreats) { threat ->
                                ThreatItemCard(
                                    threat = threat,
                                    onClick = { onThreatClick(threat.id) },
                                    onBookmarkToggle = { viewModel.toggleBookmark(threat) },
                                    onCveClick = { cveId -> viewModel.selectCve(cveId, threat) }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    val allFeeds = remember { FeedCatalogRepository.allFeeds }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Surface(
                                color = theme.surface,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RssFeed,
                                        contentDescription = "Rss Catalog",
                                        tint = CyberCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "200+ Aggregated Threat Feeds",
                                            color = theme.textPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "NIST NVD, CISA KEV, GitHub Security, AWS, Azure, GCP, DevSecOps Weekly & Vendor Advisories.",
                                            color = theme.textMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }

                        items(allFeeds.take(25)) { feed ->
                            Surface(
                                color = theme.surface,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = feed.name,
                                            color = theme.textPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "${feed.category} • ${feed.feedUrl}",
                                            color = theme.textMuted,
                                            fontSize = 10.sp,
                                            maxLines = 1
                                        )
                                    }
                                    Surface(
                                        color = CyberEmerald.copy(alpha = 0.15f),
                                        shape = CircleShape
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            color = CyberEmerald,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Surface(
                                color = theme.surface,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Palette,
                                                contentDescription = "Theme",
                                                tint = CyberCyan,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Appearance Theme",
                                                color = theme.textPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        OutlinedButton(
                                            onClick = { showThemeDialog = true },
                                            modifier = Modifier.testTag("btn_change_theme")
                                        ) {
                                            Text(themeMode.displayName)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Surface(
                                color = theme.surface,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Key,
                                            contentDescription = "API Key",
                                            tint = CyberEmerald,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Gemini AI Status",
                                            color = theme.textPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    val hasKey = BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY"

                                    Text(
                                        text = if (hasKey) "Status: Live Cloud Inference Enabled (Gemini 3.5 Flash / Pro)" else "Status: Local Cybersecurity Model Intelligence Active",
                                        color = if (hasKey) CyberEmerald else CyberCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Text(
                                        text = "To configure custom API keys securely, use the AI Studio Secrets panel.",
                                        color = theme.textMuted,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }

                        item {
                            Surface(
                                color = theme.surface,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Key,
                                                contentDescription = "Tech Stack",
                                                tint = CyberCyan,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "Tech Stack Profile",
                                                    color = theme.textPrimary,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "Configure your infrastructure stack",
                                                    color = theme.textMuted,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = { onNavigateToTechStack?.invoke() },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black),
                                            modifier = Modifier.testTag("btn_configure_tech_stack")
                                        ) {
                                            Text("Configure")
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Surface(
                                color = theme.surface,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.SystemUpdate,
                                                contentDescription = "Update Server",
                                                tint = CyberCyan,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "App Updates",
                                                    color = theme.textPrimary,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "Installed: v${BuildConfig.VERSION_NAME} (code ${BuildConfig.VERSION_CODE})",
                                                    color = theme.textMuted,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = { viewModel.checkForAppUpdates(isUserInitiated = true) },
                                            enabled = !isCheckingUpdate,
                                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black),
                                            modifier = Modifier.testTag("btn_check_updates_settings")
                                        ) {
                                            Text(if (isCheckingUpdate) "Checking..." else "Check Update")
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Update Repo: $configuredRepo",
                                        color = theme.textMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showThemeDialog) {
        ThemeSelectorDialog(
            currentMode = themeMode,
            onSelectMode = { newMode ->
                viewModel.setThemeMode(newMode)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }
}
