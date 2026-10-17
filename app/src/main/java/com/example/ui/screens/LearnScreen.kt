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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.learning.Sc200ExamData
import com.example.ui.components.ActiveDirectorySection
import com.example.ui.components.QuizEngineComposable
import com.example.ui.components.Sc200ExamSection
import com.example.ui.components.Sc500ExamSection
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberTheme
import com.example.ui.viewmodel.ThreatFeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    viewModel: ThreatFeedViewModel,
    onNavigateToChatbot: (() -> Unit)? = null,
    onNavigateToAlertsLibrary: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val theme = CyberTheme.colors
    var selectedSubTab by remember { mutableIntStateOf(0) }

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
                                color = CyberEmerald.copy(alpha = 0.15f),
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = "Learning & Certifications",
                                        tint = CyberEmerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Cyber Knowledge & Cert Labs",
                                    color = theme.textPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Fundamentals • Protocols • Algorithms • SC-200 • SC-500",
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
                    contentColor = CyberEmerald,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        if (selectedSubTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                                color = CyberEmerald
                            )
                        }
                    }
                ) {
                    Tab(
                        selected = selectedSubTab == 0,
                        onClick = { selectedSubTab = 0 },
                        text = { Text("Fundamentals & Algos", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_learn_fundamentals")
                    )
                    Tab(
                        selected = selectedSubTab == 1,
                        onClick = { selectedSubTab = 1 },
                        text = { Text("Roadmaps", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_learn_roadmaps")
                    )
                    Tab(
                        selected = selectedSubTab == 2,
                        onClick = { selectedSubTab = 2 },
                        text = { Text("SC-200 Exam", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_learn_sc200")
                    )
                    Tab(
                        selected = selectedSubTab == 3,
                        onClick = { selectedSubTab = 3 },
                        text = { Text("SC-500 Exam", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_learn_sc500")
                    )
                    Tab(
                        selected = selectedSubTab == 4,
                        onClick = { selectedSubTab = 4 },
                        text = { Text("AD & Red Team Labs", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_learn_ad_labs")
                    )
                    Tab(
                        selected = selectedSubTab == 5,
                        onClick = { selectedSubTab = 5 },
                        text = { Text("Interactive Quiz", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_learn_quiz")
                    )
                    Tab(
                        selected = selectedSubTab == 6,
                        onClick = { onNavigateToAlertsLibrary?.invoke() },
                        text = { Text("Alerts Library", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_learn_alerts_library")
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
                    modifier = Modifier.testTag("fab_ask_gemini_learn")
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
                    com.example.ui.components.CyberFundamentalsSection()
                }
                1 -> {
                    LearningRoadmapScreen(
                        viewModel = viewModel
                    )
                }
                2 -> {
                    Sc200ExamSection(
                        modifier = Modifier.padding(16.dp)
                    )
                }
                3 -> {
                    Sc500ExamSection(
                        modifier = Modifier.padding(16.dp)
                    )
                }
                4 -> {
                    ActiveDirectorySection(
                        modifier = Modifier.padding(16.dp)
                    )
                }
                5 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        QuizEngineComposable(
                            title = "SC-200 & SC-500 Practice Assessment Engine",
                            questions = Sc200ExamData.practiceQuestions
                        )
                    }
                }
            }
        }
    }
}
