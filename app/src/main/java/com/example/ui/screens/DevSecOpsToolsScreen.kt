package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.data.feeds.FeedCatalogItem
import com.example.data.feeds.FeedCatalogRepository
import com.example.data.feeds.FeedDomainCategory
import com.example.ui.components.CyberThemeToggleButton
import com.example.ui.components.ThemeSelectorDialog
import com.example.ui.components.AttackScenarioCard
import com.example.data.model.FeedSource
import com.example.data.model.ThreatItem
import com.example.ui.components.ThreatItemCard
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberTheme
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberNavyDark
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberNavySurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.SeverityCritical
import com.example.ui.viewmodel.ThreatFeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevSecOpsToolsScreen(
    viewModel: ThreatFeedViewModel,
    onThreatClick: (String) -> Unit,
    onNavigateToChatbot: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("200+ Feed Catalog", "Attack Scenarios (Top 1000)", "MITRE ATT&CK Matrix", "Security Baseline", "Bookmarks")

    val threats by viewModel.filteredThreats.collectAsState()
    val bookmarkedThreats = threats.filter { it.isBookmarked }

    val theme = CyberTheme.colors
    val themeMode by viewModel.currentThemeMode.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }

    if (showThemeDialog) {
        ThemeSelectorDialog(
            currentMode = themeMode,
            onSelectMode = { viewModel.setThemeMode(it) },
            onDismiss = { showThemeDialog = false }
        )
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
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = theme.accentPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "DevSecOps & Intel Hub",
                                color = theme.textPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "215+ FEEDS • MITRE MATRIX • SECURITY BASELINE",
                                color = theme.accentSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.4.sp
                            )
                        }
                    }
                },
                actions = {
                    if (onNavigateToChatbot != null) {
                        IconButton(
                            onClick = onNavigateToChatbot,
                            modifier = Modifier.testTag("hub_gemini_chatbot_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini AI Chatbot",
                                tint = theme.accentPrimary
                            )
                        }
                    }

                    CyberThemeToggleButton(
                        currentMode = themeMode,
                        onClick = { showThemeDialog = true }
                    )
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
            if (onNavigateToChatbot != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = theme.accentPrimary.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .clickable { onNavigateToChatbot() }
                        .testTag("hub_gemini_banner_card")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = theme.accentPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Gemini AI Assistant • All Models Available",
                                color = theme.textPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Select from all Gemini models for CVE reasoning, Sigma generation & Q&A",
                                color = theme.textSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Button(
                            onClick = onNavigateToChatbot,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = theme.accentPrimary,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = theme.surface,
                contentColor = CyberCyan,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CyberCyan
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) CyberCyan else CyberTextSecondary,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.testTag("tab_hub_$index")
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            when (selectedTab) {
                0 -> ComprehensiveFeedCatalogTab(onOpenUrl = { url ->
                    try {
                        val safeUrl = if (url.startsWith("http")) url else "https://$url"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(safeUrl)))
                    } catch (e: Exception) {
                        Toast.makeText(context, "Cannot open browser: ${e.localizedMessage ?: "No browser app installed"}", Toast.LENGTH_SHORT).show()
                    }
                })
                1 -> AttackScenariosTab(viewModel = viewModel)
                2 -> MitreMatrixScreen()
                3 -> SecurityBaselineTab()
                4 -> BookmarksTab(
                    bookmarks = bookmarkedThreats,
                    onThreatClick = onThreatClick,
                    onBookmarkToggle = { viewModel.toggleBookmark(it) }
                )
            }
        }
    }
}

@Composable
private fun ComprehensiveFeedCatalogTab(onOpenUrl: (String) -> Unit) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<FeedDomainCategory?>(null) }

    val filteredFeeds by remember(searchQuery, selectedCategory) {
        derivedStateOf {
            FeedCatalogRepository.filterFeeds(searchQuery, selectedCategory)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("feed_catalog_search_input"),
            placeholder = {
                Text(
                    text = "Search 215+ feeds (DevSecOps, Red, Blue, Grey, Dark Web...)",
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

        // Category filter chips
        val categories = listOf(null) + FeedDomainCategory.entries
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) (category?.badgeColor ?: CyberCyan).copy(alpha = 0.2f) else CyberNavySurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) (category?.badgeColor ?: CyberCyan) else CyberBorder
                    ),
                    modifier = Modifier.clickable { selectedCategory = category }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        if (category != null) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(category.badgeColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = category?.displayName ?: "All (${FeedCatalogRepository.allFeeds.size})",
                            color = if (isSelected) (category?.badgeColor ?: CyberCyan) else CyberTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Stats strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberNavySurfaceVariant.copy(alpha = 0.4f))
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredFeeds.size} Feeds Indexed",
                color = CyberTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            if (selectedCategory != null || searchQuery.isNotEmpty()) {
                Text(
                    text = "Clear Filters",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        searchQuery = ""
                        selectedCategory = null
                    }
                )
            }
        }

        // List of Feeds
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredFeeds, key = { it.id }) { item ->
                FeedCatalogCard(
                    feed = item,
                    onOpenUrl = onOpenUrl,
                    onCopyRss = { rssUrl ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("RSS Feed URL", rssUrl)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied RSS URL: $rssUrl", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
private fun FeedCatalogCard(
    feed: FeedCatalogItem,
    onOpenUrl: (String) -> Unit,
    onCopyRss: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = feed.category.badgeColor.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, feed.category.badgeColor.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = feed.category.displayName,
                                color = feed.category.badgeColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CyberNavyDark
                        ) {
                            Text(
                                text = feed.updateFrequency,
                                color = CyberTextMuted,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = feed.name,
                        color = CyberTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onCopyRss(feed.feedUrl) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RssFeed,
                            contentDescription = "Copy RSS Feed",
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { onOpenUrl(feed.websiteUrl) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = "Open website",
                            tint = CyberTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = feed.description,
                color = CyberTextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = feed.feedUrl,
                color = CyberTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun BookmarksTab(
    bookmarks: List<ThreatItem>,
    onThreatClick: (String) -> Unit,
    onBookmarkToggle: (ThreatItem) -> Unit
) {
    if (bookmarks.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = CyberTextMuted,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No saved threat bookmarks",
                    color = CyberTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap the bookmark icon on any advisory card in the feed or dashboard to save it for offline review.",
                    color = CyberTextSecondary,
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
        ) {
            items(bookmarks, key = { it.id }) { threat ->
                ThreatItemCard(
                    threat = threat,
                    onClick = { onThreatClick(threat.id) },
                    onBookmarkToggle = { onBookmarkToggle(threat) }
                )
            }
        }
    }
}

@Composable
private fun SecurityBaselineTab() {
    val baselines = listOf(
        BaselineCheck("CIS Kubernetes Benchmark v1.8", "Control 5.1.3: Minimize container root capabilities", "Automate Kyverno / OPA admission controller enforcing non-root user UID > 1000 and dropping ALL Linux capabilities except NET_BIND_SERVICE.", "CRITICAL"),
        BaselineCheck("SLSA Build Level 3 Provenance", "Build Environment Hermeticity & Attestation", "Build on ephemeral GitHub runners, sign container images with Cosign keyless signatures, and generate SLSA provenance attestations.", "HIGH"),
        BaselineCheck("OWASP Top 10 API Security", "API3:2023 Broken Object Property Level Authorization", "Implement schema validation and explicit field level authorization checks on all GraphQL and REST microservice endpoints.", "HIGH"),
        BaselineCheck("NIST SP 800-207 Zero Trust", "Mutual TLS (mTLS) Inter-service Communication", "Enforce SPIFFE identity issuance and Istio/Linkerd mTLS encryption for all east-west pod-to-pod network traffic.", "HIGH"),
        BaselineCheck("PCI DSS 4.0 Requirement 6.4", "Automate Vulnerability Scanning in CI/CD", "Integrate SAST and container image vulnerability scanners into PR gates, failing builds that introduce CVSS 7.0+ vulnerabilities.", "MEDIUM")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
    ) {
        item {
            Text(
                text = "Architectural Hardening Standards",
                color = CyberTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Industry-standard benchmarks for hardening cloud clusters, CI/CD pipelines, and microservice meshes.",
                color = CyberTextSecondary,
                fontSize = 12.sp
            )
        }

        items(baselines) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        Text(
                            text = item.standard,
                            color = CyberCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (item.severity == "CRITICAL") SeverityCritical.copy(alpha = 0.2f) else CyberEmerald.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = item.severity,
                                color = if (item.severity == "CRITICAL") SeverityCritical else CyberEmerald,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.title,
                        color = CyberTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.remediation,
                        color = CyberTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

private data class BaselineCheck(
    val standard: String,
    val title: String,
    val remediation: String,
    val severity: String
)

@Composable
private fun AttackScenariosTab(viewModel: ThreatFeedViewModel) {
    val scenarios by viewModel.allAttackScenarios.collectAsState()
    val isUpdating by viewModel.isUpdatingScenarios.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredScenarios = remember(scenarios, searchQuery) {
        if (searchQuery.isBlank()) {
            scenarios
        } else {
            scenarios.filter {
                it.organization.contains(searchQuery, ignoreCase = true) ||
                it.industry.contains(searchQuery, ignoreCase = true) ||
                it.attackVector.contains(searchQuery, ignoreCase = true) ||
                it.scenarioDetails.contains(searchQuery, ignoreCase = true) ||
                it.threatActor.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search & Refresh Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("attack_scenarios_search_input"),
                placeholder = {
                    Text(text = "Search top breaches & attack vectors...", fontSize = 12.sp, color = CyberTextMuted)
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
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

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { viewModel.updateAttackScenarios() },
                enabled = !isUpdating,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(50.dp)
                    .testTag("update_breach_scenarios_button")
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
                } else {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }

        if (isUpdating) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), color = CyberCyan)
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (filteredScenarios.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No attack scenarios matching filters.", color = CyberTextSecondary, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Surface(
                        color = CyberCyan.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "🔒 Attack Scenarios (Top 1000 Breaches)",
                                color = CyberTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Explore technical playbooks of history's largest data breaches. Tap any card to unpack attack vectors, lateral movement, TTPs, and concrete defensive rules.",
                                color = CyberTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                items(filteredScenarios, key = { it.id }) { scenario ->
                    AttackScenarioCard(scenario = scenario)
                }
            }
        }
    }
}


