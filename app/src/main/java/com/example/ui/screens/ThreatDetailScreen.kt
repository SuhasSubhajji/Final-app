package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.data.model.ThreatItem
import com.example.ui.components.RemediationCodeBox
import com.example.ui.components.SeverityBadge
import com.example.ui.components.formatRelativeTime
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
import com.example.ui.viewmodel.AiAnalysisUiState
import com.example.ui.viewmodel.ThreatFeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreatDetailScreen(
    threatId: String,
    viewModel: ThreatFeedViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val threat by viewModel.selectedThreat.collectAsState()
    val aiState by viewModel.aiAnalysisState.collectAsState()
    val customAnswer by viewModel.customQuestionAnswer.collectAsState()
    val isAskingQuestion by viewModel.isAskingQuestion.collectAsState()
    val thisThreatHistory by viewModel.getHistoryForThreat(threatId).collectAsState(initial = emptyList())

    var userQuestion by remember { mutableStateOf("") }
    val checkedRemediationIndices = remember { mutableStateListOf<Int>() }
    val theme = CyberTheme.colors

    var showHistorySheet by remember { mutableStateOf(false) }
    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showHistorySheet) {
        com.example.ui.components.ThreatHistorySheet(
            viewModel = viewModel,
            sheetState = historySheetState,
            onDismiss = { showHistorySheet = false },
            onThreatClick = { id ->
                if (id != threatId) {
                    viewModel.selectThreat(id)
                }
                showHistorySheet = false
            }
        )
    }

    LaunchedEffect(threatId) {
        viewModel.selectThreat(threatId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = theme.bg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Threat Intelligence & Fix",
                        color = theme.textPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = theme.textPrimary
                        )
                    }
                },
                actions = {
                    threat?.let { item ->
                        IconButton(
                            onClick = { showHistorySheet = true },
                            modifier = Modifier.testTag("detail_history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "View Investigation History",
                                tint = theme.accentPrimary
                            )
                        }

                        IconButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "${item.title}\n\n${item.link}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Threat Advisory"))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = theme.textSecondary
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleBookmark(item) },
                            modifier = Modifier.testTag("detail_bookmark_button")
                        ) {
                            Icon(
                                imageVector = if (item.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (item.isBookmarked) theme.accentPrimary else theme.textSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.surface)
            )
        }
    ) { innerPadding ->
        if (threat == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CyberCyan)
            }
        } else {
            val item = threat!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header: Source, Time, Severity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = item.source.displayName,
                            color = CyberCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatRelativeTime(item.pubDateMillis),
                            color = CyberTextMuted,
                            fontSize = 12.sp
                        )
                    }
                    SeverityBadge(severity = item.severity)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                Text(
                    text = item.title,
                    color = CyberTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // CVE and Category Tags
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = CyberNavySurfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.category.displayName,
                            color = CyberTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    item.cveIds.forEach { cve ->
                        Surface(
                            color = CyberCyan.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = cve,
                                color = CyberCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Room DB Offline Available Badge
                    Surface(
                        color = CyberEmerald.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = CyberEmerald,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Cached in Room DB",
                                color = CyberEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Dispatch Actionable Push Alert Button (Key user feature)
                OutlinedButton(
                    onClick = {
                        viewModel.triggerActionablePushNotification(context, item)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trigger_push_alert_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SeverityCritical
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SeverityCritical.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = SeverityCritical,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Dispatch Actionable Alert to Notification Tray",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // On-Device SOC RL Relevance Feedback Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rl_relevance_feedback_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = theme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.border)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = theme.accentPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SOC Reinforcement Learning Feedback",
                                color = theme.textPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Was this threat telemetry relevant to your operations? Rating helps the on-device RL Q-agent personalize priority scores.",
                            color = theme.textSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    item.let {
                                        viewModel.rlEngine.recordUserFeedback(it, true)
                                        Toast.makeText(context, "Marked High Priority! (+2.5 RL reward)", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("rl_feedback_positive_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberEmerald.copy(alpha = 0.2f),
                                    contentColor = CyberEmerald
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Relevant (+2.5)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    item.let {
                                        viewModel.rlEngine.recordUserFeedback(it, false)
                                        Toast.makeText(context, "Deprioritized. (-1.0 RL penalty)", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("rl_feedback_negative_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SeverityCritical.copy(alpha = 0.2f),
                                    contentColor = SeverityCritical
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.ThumbDown, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Irrelevant (-1.0)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // GEMINI AI SECTION
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gemini_ai_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = CyberCyan.copy(alpha = 0.2f),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = CyberCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Gemini Threat Intelligence",
                                    color = CyberCyan,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (aiState is AiAnalysisUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = CyberCyan,
                                    strokeWidth = 2.dp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        when (aiState) {
                            is AiAnalysisUiState.Idle -> {
                                Text(
                                    text = "Generate deep vulnerability analysis, DevSecOps blast radius assessment, and copyable remediation steps.",
                                    color = CyberTextSecondary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { viewModel.requestAiSummary(item) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("request_ai_analysis_button"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CyberCyan,
                                        contentColor = CyberNavyDark
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Analyze with Gemini", fontWeight = FontWeight.Bold)
                                }
                            }

                            is AiAnalysisUiState.Loading -> {
                                Text(
                                    text = "Analyzing advisory text, extracting attack vectors, and synthesizing DevSecOps remediation...",
                                    color = CyberTextMuted,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = CyberCyan,
                                    trackColor = CyberNavySurfaceVariant
                                )
                            }

                            is AiAnalysisUiState.Success -> {
                                val result = (aiState as AiAnalysisUiState.Success).result

                                // Risk Score Meter
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CyberNavySurfaceVariant)
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Risk Assessment Score",
                                        color = CyberTextSecondary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${result.riskScore} / 10",
                                            color = if (result.riskScore >= 8) SeverityCritical else if (result.riskScore >= 6) Color(0xFFF59E0B) else CyberEmerald,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (result.riskScore >= 8) "CRITICAL" else if (result.riskScore >= 6) "HIGH" else "MODERATE",
                                            color = CyberTextMuted,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Executive Summary
                                Text(
                                    text = "Executive Summary",
                                    color = CyberTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = result.summary,
                                    color = CyberTextSecondary,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Actionable Remediation Checklist
                                Text(
                                    text = "Actionable DevSecOps Remediation Checklist",
                                    color = CyberEmerald,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                result.remediationSteps.forEachIndexed { index, step ->
                                    val isChecked = index in checkedRemediationIndices
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                if (isChecked) {
                                                    checkedRemediationIndices.remove(index)
                                                } else {
                                                    checkedRemediationIndices.add(index)
                                                }
                                            }
                                            .padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint = if (isChecked) CyberEmerald else CyberTextMuted,
                                            modifier = Modifier
                                                .size(20.dp)
                                                .padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = step,
                                            color = if (isChecked) CyberTextMuted else CyberTextPrimary,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }

                                if (result.mitigationCode.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    RemediationCodeBox(
                                        code = result.mitigationCode,
                                        title = "Copyable Fix / Detection Command",
                                        onCopied = {
                                            viewModel.rlEngine.recordRemediationCopied(item)
                                        }
                                    )
                                }
                            }

                            is AiAnalysisUiState.Error -> {
                                Text(
                                    text = "Analysis error: ${(aiState as AiAnalysisUiState.Error).message}",
                                    color = SeverityCritical,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.requestAiSummary(item) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                                ) {
                                    Text("Retry Analysis")
                                }
                            }
                        }

                        // ASK GEMINI SECTION
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "Ask Gemini About This Threat",
                            color = CyberTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Quick prompt suggestions
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "Generate Kubernetes mitigation",
                                "WAF ModSecurity rule",
                                "Draft team advisory email"
                            ).forEach { prompt ->
                                Surface(
                                    color = CyberNavySurfaceVariant,
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                                    onClick = {
                                        userQuestion = prompt
                                        viewModel.askCustomQuestion(item, prompt)
                                    }
                                ) {
                                    Text(
                                        text = prompt,
                                        color = CyberCyan,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = userQuestion,
                                onValueChange = { userQuestion = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ask_gemini_input"),
                                placeholder = {
                                    Text("Ask follow-up question...", color = CyberTextMuted, fontSize = 13.sp)
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = CyberNavyDark,
                                    unfocusedContainerColor = CyberNavyDark,
                                    focusedBorderColor = CyberCyan,
                                    unfocusedBorderColor = CyberBorder,
                                    focusedTextColor = CyberTextPrimary,
                                    unfocusedTextColor = CyberTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (userQuestion.isNotBlank()) {
                                        viewModel.askCustomQuestion(item, userQuestion)
                                    }
                                },
                                enabled = !isAskingQuestion && userQuestion.isNotBlank(),
                                modifier = Modifier
                                    .background(CyberCyan, RoundedCornerShape(10.dp))
                                    .size(48.dp)
                                    .testTag("send_gemini_question_button")
                            ) {
                                if (isAskingQuestion) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = CyberNavyDark,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send question",
                                        tint = CyberNavyDark
                                    )
                                }
                            }
                        }

                        // Answer Box
                        if (customAnswer != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = CyberNavyDark,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = customAnswer!!,
                                    color = CyberTextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SOC Investigation & Threat History Card
                val latestHistory = thisThreatHistory.firstOrNull()
                var selectedTriageStatus by remember(latestHistory) {
                    mutableStateOf(latestHistory?.triageStatus ?: "INVESTIGATING")
                }
                var analystNotesInput by remember(latestHistory) {
                    mutableStateOf(latestHistory?.analystNotes ?: "")
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("threat_triage_history_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CyberCyan.copy(alpha = 0.15f),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = CyberCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "SOC Investigation & Triage History",
                                        color = CyberTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${thisThreatHistory.size} Action Events Recorded",
                                        color = CyberTextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            TextButton(
                                onClick = { showHistorySheet = true },
                                modifier = Modifier.testTag("open_all_history_button")
                            ) {
                                Text("Full Log ↗", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Set Triage State:",
                            color = CyberTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val statuses = listOf(
                                "INVESTIGATING" to "🔍 Investigating",
                                "RESOLVED" to "✅ Resolved",
                                "ESCALATED" to "🚨 Escalated",
                                "FALSE_POSITIVE" to "⚪ False Positive"
                            )
                            statuses.forEach { (st, label) ->
                                val isSelected = selectedTriageStatus.equals(st, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) CyberCyan.copy(alpha = 0.2f) else CyberNavyDark,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) CyberCyan else CyberBorder
                                    ),
                                    modifier = Modifier.clickable { selectedTriageStatus = st }
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) CyberCyan else CyberTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = analystNotesInput,
                            onValueChange = { analystNotesInput = it },
                            label = { Text("Analyst Investigation Notes", fontSize = 11.sp) },
                            placeholder = { Text("Add forensic findings, containment actions, or tickets...", color = CyberTextMuted, fontSize = 11.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("analyst_notes_input"),
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberBorder,
                                focusedContainerColor = CyberNavyDark,
                                unfocusedContainerColor = CyberNavyDark,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                viewModel.updateThreatTriage(item.id, selectedTriageStatus, analystNotesInput)
                                Toast.makeText(context, "Triage status saved to threat history", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark),
                            modifier = Modifier
                                .align(Alignment.End)
                                .testTag("save_triage_status_button")
                        ) {
                            Text("Save Triage Status & Note", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // Recent Events Timeline for this threat
                        if (thisThreatHistory.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Threat Activity Timeline:",
                                color = CyberTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                thisThreatHistory.take(4).forEach { hist ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = CyberNavyDark,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder.copy(alpha = 0.5f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .clip(CircleShape)
                                                        .background(CyberCyan)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = hist.actionType.replace("_", " "),
                                                    color = CyberCyan,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                if (hist.triageStatus.isNotBlank()) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "(${hist.triageStatus})",
                                                        color = CyberTextMuted,
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            }

                                            Text(
                                                text = formatRelativeTime(hist.timestampMillis),
                                                color = CyberTextMuted,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Full Advisory Content Section
                Text(
                    text = "Full Feed Advisory Text",
                    color = CyberTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = CyberNavySurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.fullContent.ifEmpty { item.description },
                        color = CyberTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Open External Source Link
                OutlinedButton(
                    onClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
                        context.startActivity(browserIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_advisory_link_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Read Original Advisory at ${item.source.displayName}", color = CyberCyan)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
