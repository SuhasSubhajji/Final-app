package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.learning.Sc500Domain
import com.example.data.learning.Sc500ExamData
import com.example.data.learning.Sc500PracticeQuestion
import com.example.data.learning.Sc500SecurityScript
import com.example.ui.theme.*

@Composable
fun Sc500ExamSection(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("📋 Domains", "📝 Practice Quiz", "⚡ Azure Scripts", "🔗 Official Links")

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF38BDF8).copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SC-500 EXAM PREPARATION",
                                color = CyberTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Microsoft Azure Security Technologies",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = CyberEmerald.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "PASS: 700 / 1000",
                            color = CyberEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Complete preparation track for Azure Security Engineer certification. Master Entra ID, PIM, Conditional Access, NSGs, Azure Firewall Premium, Private Endpoints, Key Vault Purge Protection, Defender for Cloud, and Sentinel governance.",
                    color = CyberTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatPill("4 Domains", Color(0xFF38BDF8))
                    StatPill("100% Practical", CyberEmerald)
                    StatPill("Cloud Architecture", CyberCyan)
                }
            }
        }

        // Tab Selector
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = CyberNavySurfaceVariant,
            contentColor = Color(0xFF38BDF8),
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFF38BDF8)
                )
            }
        ) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == idx) Color(0xFF38BDF8) else CyberTextSecondary
                        )
                    },
                    modifier = Modifier.testTag("tab_sc500_$idx")
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Sc500DomainsView(domains = Sc500ExamData.domains)
                    RoadmapResourceLinksSection(
                        sectionTitle = "SC-500 Official Documentation & Benchmark Portals",
                        accentColor = Color(0xFF38BDF8),
                        resources = Sc500ExamData.resourceLinks
                    )
                }
            }
            1 -> Sc500QuizView(questions = Sc500ExamData.practiceQuestions)
            2 -> Sc500ScriptsView(scripts = Sc500ExamData.securityScripts)
            3 -> {
                RoadmapResourceLinksSection(
                    sectionTitle = "SC-500 Official Microsoft & Azure Security Benchmark Links",
                    accentColor = Color(0xFF38BDF8),
                    resources = Sc500ExamData.resourceLinks
                )
            }
        }
    }
}

@Composable
private fun StatPill(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(0.6.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun Sc500DomainsView(domains: List<Sc500Domain>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        domains.forEach { domain ->
            var expanded by remember { mutableStateOf(false) }

            Surface(
                color = CyberNavySurface,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (expanded) Color(0xFF38BDF8) else CyberBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF38BDF8).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(0.6.dp, Color(0xFF38BDF8).copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "WEIGHT: ${domain.percentageWeight}",
                                color = Color(0xFF38BDF8),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = CyberTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = domain.title,
                        color = CyberTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = domain.description,
                        color = CyberTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    AnimatedVisibility(visible = expanded) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            HorizontalDivider(color = CyberBorder, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "🎯 OBJECTIVES & MEASURED SKILLS",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            domain.keySkills.forEach { skill ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("• ", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = skill,
                                        color = CyberTextPrimary,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "💡 HIGH-YIELD EXAM INSIGHTS",
                                color = CyberEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            domain.highYieldTips.forEach { tip ->
                                Surface(
                                    color = CyberNavySurfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(0.6.dp, CyberBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = CyberEmerald,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = tip,
                                            color = CyberTextSecondary,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp
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

@Composable
private fun Sc500QuizView(questions: List<Sc500PracticeQuestion>) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var showExplanation by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    val question = questions[currentIndex]

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Progress Card
        Surface(
            color = CyberNavySurface,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question ${currentIndex + 1} of ${questions.size}",
                    color = Color(0xFF38BDF8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Score: $score / ${questions.size}",
                    color = CyberEmerald,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Question Card
        Surface(
            color = CyberNavySurface,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = question.question,
                    color = CyberTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                question.options.forEachIndexed { idx, option ->
                    val isSelected = selectedOption == idx
                    val isCorrect = idx == question.correctOptionIndex

                    val borderColor = when {
                        !showExplanation && isSelected -> Color(0xFF38BDF8)
                        showExplanation && isCorrect -> CyberEmerald
                        showExplanation && isSelected && !isCorrect -> SeverityCritical
                        else -> CyberBorder
                    }

                    val bgColor = when {
                        !showExplanation && isSelected -> Color(0xFF38BDF8).copy(alpha = 0.12f)
                        showExplanation && isCorrect -> CyberEmerald.copy(alpha = 0.15f)
                        showExplanation && isSelected && !isCorrect -> SeverityCritical.copy(alpha = 0.15f)
                        else -> CyberNavyDark
                    }

                    Surface(
                        color = bgColor,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable(enabled = !showExplanation) {
                                selectedOption = idx
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) Color(0xFF38BDF8) else CyberNavySurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = ('A' + idx).toString(),
                                        color = if (isSelected) Color.Black else CyberTextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = option,
                                color = CyberTextPrimary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action button
                if (!showExplanation) {
                    Button(
                        onClick = {
                            if (selectedOption != null) {
                                showExplanation = true
                                if (selectedOption == question.correctOptionIndex) {
                                    score++
                                }
                            }
                        },
                        enabled = selectedOption != null,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8), contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm Answer", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Explanation box
                    Surface(
                        color = CyberNavySurfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "EXPLANATION",
                                color = if (selectedOption == question.correctOptionIndex) CyberEmerald else Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = question.explanation,
                                color = CyberTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (currentIndex < questions.size - 1) {
                                currentIndex++
                                selectedOption = null
                                showExplanation = false
                            } else {
                                // Restart
                                currentIndex = 0
                                selectedOption = null
                                showExplanation = false
                                score = 0
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8), contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (currentIndex < questions.size - 1) "Next Question ➔" else "Restart Quiz 🔄",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Sc500ScriptsView(scripts: List<Sc500SecurityScript>) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        scripts.forEach { script ->
            Surface(
                color = CyberNavySurface,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF38BDF8).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = script.category.uppercase(),
                                color = Color(0xFF38BDF8),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Script", script.commandOrPolicy)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Command copied!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Script",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = script.title,
                        color = CyberTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = script.description,
                        color = CyberTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = CyberNavyDark,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = script.commandOrPolicy,
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🔎 Verification / Audit Command:",
                        color = CyberEmerald,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(
                        color = CyberNavyDark.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = script.verificationCommand,
                            color = CyberTextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
        }
    }
}
