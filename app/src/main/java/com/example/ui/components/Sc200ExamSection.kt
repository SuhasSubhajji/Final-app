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
import com.example.data.learning.KqlQueryItem
import com.example.data.learning.Sc200Domain
import com.example.data.learning.Sc200ExamData
import com.example.data.learning.Sc200PracticeQuestion
import com.example.ui.theme.*

@Composable
fun Sc200ExamSection(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("📋 Domains", "📝 Practice Quiz", "⚡ KQL Query Lab", "🔗 Official Links")

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0078D4).copy(alpha = 0.6f))
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
                            color = Color(0xFF0078D4).copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = Color(0xFF0078D4),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SC-200 Exam Preparation",
                                color = CyberTextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Microsoft Security Operations Analyst",
                                color = Color(0xFF0078D4),
                                fontSize = 12.sp,
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

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Master Microsoft Defender XDR (Endpoint, Identity, Cloud, Office 365) and Microsoft Sentinel SIEM/SOAR. Includes interactive exam domains, realistic practice questions, and high-yield KQL hunting queries.",
                    color = CyberTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // Sub-tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberNavySurface, shape = RoundedCornerShape(12.dp))
                .border(1.dp, CyberBorder, shape = RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEachIndexed { idx, label ->
                val selected = selectedTab == idx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (selected) Color(0xFF0078D4).copy(alpha = 0.2f) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (selected) Color(0xFF0078D4).copy(alpha = 0.6f) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedTab = idx }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (selected) Color(0xFF0078D4) else CyberTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Sc200DomainsList(domains = Sc200ExamData.domains)
                    RoadmapResourceLinksSection(
                        sectionTitle = "SC-200 Official Links & Study Portals",
                        accentColor = Color(0xFF0078D4),
                        resources = Sc200ExamData.resourceLinks
                    )
                }
            }
            1 -> Sc200QuizView(questions = Sc200ExamData.practiceQuestions)
            2 -> Sc200KqlLab(queries = Sc200ExamData.kqlCheatSheet)
            3 -> {
                RoadmapResourceLinksSection(
                    sectionTitle = "SC-200 Official Microsoft & Ninja Training Links",
                    accentColor = Color(0xFF0078D4),
                    resources = Sc200ExamData.resourceLinks
                )
            }
        }
    }
}

@Composable
private fun Sc200DomainsList(domains: List<Sc200Domain>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        domains.forEach { domain ->
            var expanded by remember { mutableStateOf(false) }

            Surface(
                color = CyberNavySurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (expanded) Color(0xFF0078D4) else CyberBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = domain.title,
                            color = CyberTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF0078D4).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(0.6.dp, Color(0xFF0078D4).copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = domain.percentageWeight,
                                color = Color(0xFF0078D4),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = domain.description,
                        color = CyberTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    AnimatedVisibility(visible = expanded) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            HorizontalDivider(color = CyberBorder, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "🎯 KEY SKILLS TESTED",
                                color = Color(0xFF0078D4),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            domain.keySkills.forEach { skill ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("•", color = Color(0xFF0078D4), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = skill, color = CyberTextPrimary, fontSize = 12.sp, lineHeight = 16.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "💡 HIGH-YIELD EXAM TIPS",
                                color = CyberEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            domain.highYieldTips.forEach { tip ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("⚡", fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = tip, color = CyberTextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (expanded) "Tap to collapse" else "Tap to view skills & exam tips",
                        color = CyberTextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun Sc200QuizView(questions: List<Sc200PracticeQuestion>) {
    var userAnswers by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    val correctCount = userAnswers.count { (qId, answerIdx) ->
        val q = questions.firstOrNull { it.id == qId }
        q != null && q.correctOptionIndex == answerIdx
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Score banner
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
                Column {
                    Text(
                        text = "Practice Test Progress",
                        color = CyberTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Score: $correctCount / ${questions.size} correct (${if (questions.isNotEmpty()) (correctCount * 100 / questions.size) else 0}%)",
                        color = if (correctCount >= 4) CyberEmerald else CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (userAnswers.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { userAnswers = emptyMap() },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Reset Quiz", fontSize = 11.sp)
                    }
                }
            }
        }

        questions.forEachIndexed { index, question ->
            val selectedOption = userAnswers[question.id]
            val hasAnswered = selectedOption != null
            val isCorrect = selectedOption == question.correctOptionIndex

            Surface(
                color = CyberNavySurface,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (hasAnswered) {
                        if (isCorrect) CyberEmerald.copy(alpha = 0.8f) else SeverityCritical.copy(alpha = 0.8f)
                    } else CyberBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question ${index + 1} of ${questions.size}",
                            color = Color(0xFF0078D4),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (hasAnswered) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isCorrect) CyberEmerald.copy(alpha = 0.2f) else SeverityCritical.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (isCorrect) "✓ Correct" else "✗ Incorrect",
                                    color = if (isCorrect) CyberEmerald else SeverityCritical,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = question.question,
                        color = CyberTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Option Buttons
                    question.options.forEachIndexed { optIndex, optText ->
                        val isThisSelected = selectedOption == optIndex
                        val isThisCorrect = question.correctOptionIndex == optIndex

                        val optionBg = when {
                            !hasAnswered -> if (isThisSelected) CyberCyan.copy(alpha = 0.15f) else CyberNavySurfaceVariant
                            isThisCorrect -> CyberEmerald.copy(alpha = 0.2f)
                            isThisSelected -> SeverityCritical.copy(alpha = 0.2f)
                            else -> CyberNavySurfaceVariant.copy(alpha = 0.5f)
                        }

                        val optionBorder = when {
                            !hasAnswered -> if (isThisSelected) CyberCyan else CyberBorder
                            isThisCorrect -> CyberEmerald
                            isThisSelected -> SeverityCritical
                            else -> CyberBorder.copy(alpha = 0.5f)
                        }

                        Surface(
                            color = optionBg,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, optionBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    if (!hasAnswered) {
                                        userAnswers = userAnswers + (question.id to optIndex)
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val letter = ('A' + optIndex).toString()
                                Surface(
                                    shape = CircleShape,
                                    color = if (isThisSelected) CyberCyan else CyberBorder,
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = letter,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isThisSelected) Color.Black else CyberTextPrimary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = optText,
                                    color = CyberTextPrimary,
                                    fontSize = 13.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    if (hasAnswered) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = CyberNavyDark,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "📖 Explanation:",
                                    color = CyberCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = question.explanation,
                                    color = CyberTextSecondary,
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

@Composable
private fun Sc200KqlLab(queries: List<KqlQueryItem>) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Essential KQL Hunting Queries for Microsoft Sentinel",
            color = CyberTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        queries.forEach { item ->
            Surface(
                color = CyberNavySurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.title,
                            color = CyberTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF0078D4).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = item.category,
                                color = Color(0xFF0078D4),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.description,
                        color = CyberTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Code box
                    Surface(
                        color = CyberNavyDark,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "KQL QUERY",
                                    color = CyberCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("KQL Query", item.query)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "KQL copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy KQL",
                                        tint = CyberCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.query,
                                color = Color(0xFFE2E8F0),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Outcome: ${item.expectedOutcome}",
                        color = CyberEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
