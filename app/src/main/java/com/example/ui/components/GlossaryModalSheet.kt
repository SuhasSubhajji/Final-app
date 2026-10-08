package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.glossary.GlossaryTerm
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberNavyDark
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberNavySurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlossaryModalSheet(
    terms: List<GlossaryTerm>,
    isQueryingGemini: Boolean,
    onLookupTermWithGemini: (String) -> Unit,
    sheetState: SheetState,
    initialSearchTerm: String = "",
    onDismissRequest: () -> Unit
) {
    var searchQuery by remember { mutableStateOf(initialSearchTerm) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var expandedTerm by remember { mutableStateOf<String?>(initialSearchTerm.ifBlank { null }) }

    val filteredTerms = terms.filter { term ->
        val matchesQuery = searchQuery.isBlank() ||
                term.term.contains(searchQuery, ignoreCase = true) ||
                term.definition.contains(searchQuery, ignoreCase = true) ||
                term.industryStandard.contains(searchQuery, ignoreCase = true)

        val matchesCategory = selectedCategory == null || term.category.equals(selectedCategory, ignoreCase = true)

        matchesQuery && matchesCategory
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = CyberNavyDark,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(CyberBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CyberCyan.copy(alpha = 0.15f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Cybersecurity Glossary",
                            color = CyberTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "NIST • MITRE ATT&CK • OWASP • CISA Standards",
                            color = CyberEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("glossary_search_input"),
                placeholder = {
                    Text("Search or lookup any security term (e.g. BOLA, RCE, eBPF)...", color = CyberTextMuted, fontSize = 13.sp)
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CyberCyan)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = CyberTextMuted)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CyberNavySurfaceVariant,
                    unfocusedContainerColor = CyberNavySurface,
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = CyberBorder,
                    focusedTextColor = CyberTextPrimary,
                    unfocusedTextColor = CyberTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val categories = listOf("All", "AppSec", "DevSecOps", "Cloud Security", "Offensive", "Threat Intel", "Architecture")
                categories.forEach { cat ->
                    val isSelected = (cat == "All" && selectedCategory == null) || (selectedCategory == cat)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategory = if (cat == "All") null else cat
                        },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                            selectedLabelColor = CyberCyan
                        ),
                        modifier = Modifier.height(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // List of Glossary Terms
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .testTag("glossary_terms_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filteredTerms.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Term \"$searchQuery\" not in local library",
                                color = CyberTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Query Gemini AI to generate an industry-standard definition based on NIST/MITRE frameworks.",
                                color = CyberTextMuted,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { onLookupTermWithGemini(searchQuery.trim()) },
                                enabled = !isQueryingGemini && searchQuery.isNotBlank(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark),
                                modifier = Modifier.testTag("gemini_lookup_glossary_button")
                            ) {
                                if (isQueryingGemini) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CyberNavyDark, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text("Look up \"$searchQuery\" with Gemini", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    items(filteredTerms, key = { it.term }) { item ->
                        val isExpanded = expandedTerm.equals(item.term, ignoreCase = true)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedTerm = if (isExpanded) null else item.term
                                }
                                .testTag("glossary_item_${item.term}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isExpanded) CyberCyan.copy(alpha = 0.5f) else CyberBorder
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.term,
                                            color = CyberTextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (item.isAiGenerated) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = CyberCyan.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "AI Verified",
                                                    color = CyberCyan,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Surface(
                                        color = CyberNavySurfaceVariant,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = item.industryStandard,
                                            color = CyberEmerald,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = item.definition,
                                    color = CyberTextSecondary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )

                                AnimatedVisibility(visible = isExpanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(CyberNavyDark)
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = "Practical Real-World Scenario:",
                                            color = CyberCyan,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.practicalExample,
                                            color = CyberTextPrimary,
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
        }
    }
}
