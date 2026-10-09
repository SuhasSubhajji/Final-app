package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import com.example.ui.theme.CyberTheme
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DateRangeFilter
import com.example.data.model.FeedFilterState
import com.example.data.model.FeedSource
import com.example.data.model.ThreatCategory
import com.example.data.model.ThreatItem
import com.example.data.model.ThreatSeverity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CyberSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    activeFilterCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .testTag("search_bar_input"),
            placeholder = {
                Text(
                    text = "Search CVEs, threats, DevSecOps...",
                    color = CyberTextMuted,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = CyberCyan
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.testTag("clear_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = CyberTextSecondary
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CyberNavySurfaceVariant,
                unfocusedContainerColor = CyberNavySurface,
                focusedBorderColor = CyberCyan,
                unfocusedBorderColor = CyberBorder,
                focusedTextColor = CyberTextPrimary,
                unfocusedTextColor = CyberTextPrimary,
                cursorColor = CyberCyan
            )
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Filter button with badge
        Surface(
            onClick = onFilterClick,
            shape = RoundedCornerShape(14.dp),
            color = if (activeFilterCount > 0) CyberCyan.copy(alpha = 0.15f) else CyberNavySurface,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (activeFilterCount > 0) CyberCyan else CyberBorder
            ),
            modifier = Modifier
                .size(54.dp)
                .testTag("filter_dialog_button")
        ) {
            Box(contentAlignment = Alignment.Center) {
                BadgedBox(
                    badge = {
                        if (activeFilterCount > 0) {
                            Badge(
                                containerColor = CyberCyan,
                                contentColor = CyberNavyDark
                            ) {
                                Text("$activeFilterCount", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter feeds",
                        tint = if (activeFilterCount > 0) CyberCyan else CyberTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun SeverityBadge(
    severity: ThreatSeverity,
    modifier: Modifier = Modifier
) {
    Surface(
        color = severity.containerColor,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, severity.color.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(severity.color)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = severity.label,
                color = severity.color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun ThreatItemCard(
    threat: ThreatItem,
    onClick: () -> Unit,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier,
    rlMatchScore: Int? = null,
    onSummarizeCveClick: (() -> Unit)? = null,
    isSummarizing: Boolean = false
) {
    val theme = CyberTheme.colors

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("threat_item_${threat.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = theme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (threat.severity == ThreatSeverity.CRITICAL) SeverityCritical.copy(alpha = 0.5f) else theme.border
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Source, Date, Severity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(theme.accentPrimary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = threat.source.displayName,
                        color = theme.accentPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${formatRelativeTime(threat.pubDateMillis)}",
                        color = theme.textMuted,
                        fontSize = 12.sp
                    )
                }

                SeverityBadge(severity = threat.severity)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = threat.title,
                color = theme.textPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Concise Human-Readable Gemini Summary Banner or Raw Description
            if (!threat.aiSummary.isNullOrBlank()) {
                Surface(
                    color = CyberNavyDark,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "GEMINI CVE THREAT SUMMARY",
                                    color = CyberCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.4.sp
                                )
                            }

                            if (threat.cveIds.isNotEmpty()) {
                                Text(
                                    text = threat.cveIds.first(),
                                    color = CyberTextMuted,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = threat.aiSummary,
                            color = CyberTextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            } else {
                // Raw Snippet Description
                Text(
                    text = threat.description,
                    color = theme.textSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tags, CVE pills & Quick Summarize Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Chip
                    Surface(
                        color = theme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = threat.category.displayName,
                            color = theme.textSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // RL Match Score badge
                    if (rlMatchScore != null) {
                        Surface(
                            color = theme.accentPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.accentPrimary.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = theme.accentPrimary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "$rlMatchScore% RL Match",
                                    color = theme.accentPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // CVE IDs
                    threat.cveIds.take(2).forEach { cve ->
                        Surface(
                            color = theme.accentPrimary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.accentPrimary.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = cve,
                                color = theme.accentPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // On-demand Summarize CVE Action if not summarized yet
                    if (threat.aiSummary.isNullOrBlank() && onSummarizeCveClick != null) {
                        Surface(
                            color = CyberCyan.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, CyberCyan.copy(alpha = 0.6f)),
                            modifier = Modifier.clickable(enabled = !isSummarizing) { onSummarizeCveClick() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isSummarizing) {
                                    androidx.compose.material3.CircularProgressIndicator(
                                        modifier = Modifier.size(10.dp),
                                        color = CyberCyan,
                                        strokeWidth = 1.5.dp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Summarizing...", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = CyberCyan,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Summarize with Gemini", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Bookmark toggle
                IconButton(
                    onClick = onBookmarkToggle,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("bookmark_toggle_${threat.id}")
                ) {
                    Icon(
                        imageVector = if (threat.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (threat.isBookmarked) "Remove bookmark" else "Bookmark threat",
                        tint = if (threat.isBookmarked) theme.accentPrimary else theme.textMuted
                    )
                }
            }
        }
    }
}

@Composable
fun RemediationCodeBox(
    code: String,
    title: String = "Remediation Script / Rule",
    modifier: Modifier = Modifier,
    onCopied: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF070B14))
            .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                Spacer(modifier = Modifier.width(6.dp))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                Spacer(modifier = Modifier.width(6.dp))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    color = CyberTextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Remediation", code))
                    onCopied?.invoke()
                    Toast.makeText(context, "Command copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(32.dp)
                    .testTag("copy_remediation_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy code",
                    tint = CyberCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Text(
            text = code,
            color = Color(0xFF38BDF8),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .horizontalScroll(rememberScrollState())
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    filterState: FeedFilterState,
    onFilterChange: (FeedFilterState) -> Unit,
    sheetState: SheetState,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = CyberNavyDark,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(CyberBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Intelligence Feeds",
                    color = CyberTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = {
                        onFilterChange(FeedFilterState())
                    },
                    modifier = Modifier.testTag("clear_filters_sheet_button")
                ) {
                    Text("Reset All", color = CyberCyan)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 1: Alert Severity
            Text(
                text = "Alert Severity",
                color = CyberTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThreatSeverity.entries.forEach { severity ->
                    val isSelected = severity in filterState.selectedSeverities
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val newSet = if (isSelected) {
                                filterState.selectedSeverities - severity
                            } else {
                                filterState.selectedSeverities + severity
                            }
                            onFilterChange(filterState.copy(selectedSeverities = newSet))
                        },
                        label = {
                            Text(severity.label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        },
                        leadingIcon = {
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = severity.containerColor,
                            selectedLabelColor = severity.color,
                            selectedLeadingIconColor = severity.color,
                            containerColor = CyberNavySurface,
                            labelColor = CyberTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = CyberBorder,
                            selectedBorderColor = severity.color
                        ),
                        modifier = Modifier.testTag("filter_chip_severity_${severity.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: Feed Sources
            Text(
                text = "Feed Source",
                color = CyberTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FeedSource.entries.forEach { source ->
                    val isSelected = source in filterState.selectedSources
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val newSet = if (isSelected) {
                                filterState.selectedSources - source
                            } else {
                                filterState.selectedSources + source
                            }
                            onFilterChange(filterState.copy(selectedSources = newSet))
                        },
                        label = {
                            Text(source.displayName, fontSize = 12.sp)
                        },
                        leadingIcon = {
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan.copy(alpha = 0.15f),
                            selectedLabelColor = CyberCyan,
                            selectedLeadingIconColor = CyberCyan,
                            containerColor = CyberNavySurface,
                            labelColor = CyberTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = CyberBorder,
                            selectedBorderColor = CyberCyan
                        ),
                        modifier = Modifier.testTag("filter_chip_source_${source.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 3: Date Range
            Text(
                text = "Publication Date",
                color = CyberTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DateRangeFilter.entries.forEach { range ->
                    val isSelected = filterState.dateRange == range
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onFilterChange(filterState.copy(dateRange = range))
                        },
                        label = {
                            Text(range.displayName, fontSize = 12.sp)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberEmerald.copy(alpha = 0.15f),
                            selectedLabelColor = CyberEmerald,
                            containerColor = CyberNavySurface,
                            labelColor = CyberTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = CyberBorder,
                            selectedBorderColor = CyberEmerald
                        ),
                        modifier = Modifier.testTag("filter_chip_date_${range.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 4: DevSecOps Categories
            Text(
                text = "Threat Domain / Category",
                color = CyberTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThreatCategory.entries.forEach { cat ->
                    val isSelected = cat in filterState.selectedCategories
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val newSet = if (isSelected) {
                                filterState.selectedCategories - cat
                            } else {
                                filterState.selectedCategories + cat
                            }
                            onFilterChange(filterState.copy(selectedCategories = newSet))
                        },
                        label = {
                            Text(cat.displayName, fontSize = 12.sp)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6366F1).copy(alpha = 0.2f),
                            selectedLabelColor = Color(0xFFA5B4FC),
                            containerColor = CyberNavySurface,
                            labelColor = CyberTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = CyberBorder,
                            selectedBorderColor = Color(0xFF6366F1)
                        ),
                        modifier = Modifier.testTag("filter_chip_category_${cat.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick toggles: Bookmarks & Unread
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterState.onlyBookmarks,
                    onClick = {
                        onFilterChange(filterState.copy(onlyBookmarks = !filterState.onlyBookmarks))
                    },
                    label = { Text("Bookmarked Only") },
                    leadingIcon = {
                        Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(14.dp))
                    },
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = filterState.onlyUnread,
                    onClick = {
                        onFilterChange(filterState.copy(onlyUnread = !filterState.onlyUnread))
                    },
                    label = { Text("Unread Only") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

fun formatRelativeTime(millis: Long): String {
    val diff = System.currentTimeMillis() - millis
    val minutes = diff / (60 * 1000)
    val hours = diff / (60 * 60 * 1000)
    val days = diff / (24 * 60 * 60 * 1000)

    return when {
        minutes < 5 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        else -> SimpleDateFormat("MMM d", Locale.US).format(Date(millis))
    }
}
