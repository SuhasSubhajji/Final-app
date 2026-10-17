package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.learning.FundamentalCategory
import com.example.data.learning.FundamentalTopic
import com.example.data.learning.FundamentalsData
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberTheme
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.SeverityHigh

@Composable
fun CyberFundamentalsSection(
    modifier: Modifier = Modifier
) {
    val theme = CyberTheme.colors
    var selectedCategory by remember { mutableStateOf<FundamentalCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var expandedTopicId by remember { mutableStateOf<String?>(null) }

    val filteredTopics = remember(selectedCategory, searchQuery) {
        FundamentalsData.topics.filter { topic ->
            val matchesCategory = selectedCategory == null || topic.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    topic.title.contains(searchQuery, ignoreCase = true) ||
                    topic.summary.contains(searchQuery, ignoreCase = true) ||
                    topic.tag.contains(searchQuery, ignoreCase = true) ||
                    topic.technicalBreakdown.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("cyber_fundamentals_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header Banner ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = theme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = CyberEmerald.copy(alpha = 0.15f),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Core Fundamentals",
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Foundational Knowledge Hub",
                                color = theme.textPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Cybersecurity • Networking Protocols • Cryptography Algorithms",
                                color = theme.textMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Master the deep mathematical and protocol foundations underpinning modern security: from the CIA Triad and OSI packet encapsulations to AES-256-GCM, RSA modular arithmetic, Diffie-Hellman key exchange, SHA-256 bitwise compression, and Post-Quantum Cryptography (ML-KEM/ML-DSA).",
                        color = theme.textSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // --- Search Bar ---
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_fundamentals_input"),
                placeholder = {
                    Text("Search algorithms, protocols, formulas (e.g. RSA, AES, TCP, PQC)...", fontSize = 13.sp, color = theme.textMuted)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = theme.textMuted)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = theme.surfaceVariant,
                    focusedTextColor = theme.textPrimary,
                    unfocusedTextColor = theme.textPrimary,
                    cursorColor = CyberCyan
                )
            )
        }

        // --- Category Filter Chips ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All Pillars (${FundamentalsData.topics.size})", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                        selectedLabelColor = CyberCyan,
                        containerColor = theme.surface,
                        labelColor = theme.textSecondary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("chip_all_pillars")
                )

                FundamentalCategory.values().forEach { category ->
                    val isSelected = selectedCategory == category
                    val count = FundamentalsData.topics.count { it.category == category }
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategory = if (isSelected) null else category
                        },
                        label = {
                            Text("${category.displayName} ($count)", fontSize = 12.sp)
                        },
                        leadingIcon = {
                            val icon = when (category) {
                                FundamentalCategory.CYBERSECURITY -> Icons.Default.Security
                                FundamentalCategory.NETWORKING -> Icons.Default.Lan
                                FundamentalCategory.CRYPTOGRAPHY -> Icons.Default.Key
                            }
                            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (isSelected) category.color else theme.textMuted)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = category.color.copy(alpha = 0.2f),
                            selectedLabelColor = category.color,
                            containerColor = theme.surface,
                            labelColor = theme.textSecondary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("chip_category_${category.name.lowercase()}")
                    )
                }
            }
        }

        // --- Topic Cards ---
        if (filteredTopics.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = theme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = theme.textMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No matching foundational topics found",
                            color = theme.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Try clearing search filters or searching for terms like 'RSA', 'AES', 'Subnet', 'TCP', or 'PQC'.",
                            color = theme.textMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(filteredTopics, key = { it.id }) { topic ->
                FundamentalTopicCard(
                    topic = topic,
                    isExpanded = expandedTopicId == topic.id,
                    onToggleExpand = {
                        expandedTopicId = if (expandedTopicId == topic.id) null else topic.id
                    }
                )
            }
        }
    }
}

@Composable
fun FundamentalTopicCard(
    topic: FundamentalTopic,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val theme = CyberTheme.colors
    val clipboardManager = LocalClipboardManager.current
    var isCodeCopied by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onToggleExpand() }
            .animateContentSize()
            .testTag("card_topic_${topic.id}"),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isExpanded) topic.category.color.copy(alpha = 0.5f) else theme.surfaceVariant
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // --- Badges & Category Header ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = topic.category.color.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = topic.category.displayName,
                            color = topic.category.color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        color = theme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = topic.tag,
                            color = theme.textSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = theme.textMuted,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- Title & Summary ---
            Text(
                text = topic.title,
                color = theme.textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = topic.summary,
                color = theme.textSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            // --- Expanded Detailed View ---
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    // 1. Core Concept Box
                    Surface(
                        color = theme.bg,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        border = androidx.compose.foundation.BorderStroke(1.dp, theme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = topic.category.color, modifier = Modifier.size(16.dp))
                                Text("Core Concept", color = topic.category.color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(topic.coreConcept, color = theme.textPrimary, fontSize = 12.sp, lineHeight = 18.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Mathematical Formula / Logic Banner (if applicable)
                    topic.mathematicalFormula?.let { formula ->
                        Surface(
                            color = topic.category.color.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, topic.category.color.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Functions, contentDescription = "Formula", tint = topic.category.color, modifier = Modifier.size(20.dp))
                                Column {
                                    Text("Mathematical Expression / Algorithm Model", color = topic.category.color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = formula,
                                        color = theme.textPrimary,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // 3. Technical Breakdown
                    Text("Technical Breakdown & Mechanics", color = theme.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = theme.bg,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = topic.technicalBreakdown,
                            color = theme.textSecondary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4. Algorithm Step-by-Step Flow
                    if (topic.algorithmSteps.isNotEmpty()) {
                        Text("Algorithm Step-by-Step Execution", color = theme.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            topic.algorithmSteps.forEach { step ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        color = topic.category.color,
                                        shape = CircleShape,
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${step.stepNumber}",
                                                color = Color.Black,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(step.title, color = theme.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        step.formulaOrCommand?.let { formula ->
                                            Text(
                                                text = formula,
                                                color = CyberCyan,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                modifier = Modifier.padding(vertical = 2.dp)
                                            )
                                        }
                                        Text(step.explanation, color = theme.textSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // 5. Practical Implementation Snippet
                    topic.practicalCodeSnippet?.let { snippet ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Code, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                Text("Implementation & Simulation Snippet", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(snippet))
                                    isCodeCopied = true
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCodeCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copy Snippet",
                                    tint = if (isCodeCopied) CyberEmerald else theme.textMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            color = Color(0xFF0F172A),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                        ) {
                            Text(
                                text = snippet,
                                color = Color(0xFFE2E8F0),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 17.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // 6. Attack Vector & Defensive Takeaway Grid
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Threat / Attack Vector Box
                        Surface(
                            color = SeverityCritical.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SeverityCritical.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = "Threat", tint = SeverityCritical, modifier = Modifier.size(18.dp))
                                Column {
                                    Text("Threat Vectors & Exploitation", color = SeverityCritical, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(topic.attackVectorOrVulnerability, color = theme.textPrimary, fontSize = 11.sp, lineHeight = 16.sp)
                                }
                            }
                        }

                        // Defensive Takeaway Box
                        Surface(
                            color = CyberEmerald.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = "Defense", tint = CyberEmerald, modifier = Modifier.size(18.dp))
                                Column {
                                    Text("Defensive Engineering & Mitigation", color = CyberEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(topic.defensiveTakeaway, color = theme.textPrimary, fontSize = 11.sp, lineHeight = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
