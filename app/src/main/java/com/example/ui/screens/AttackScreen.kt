package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.ui.components.MitreInteractiveGraphComposable
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberTheme
import com.example.ui.viewmodel.ThreatFeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttackScreen(
    viewModel: ThreatFeedViewModel,
    onNavigateToChatbot: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val theme = CyberTheme.colors
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: ATT&CK 14-Col Matrix, 1: D3FEND 7-Col Matrix, 2: Catalog List, 3: D3 Flow Graph

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = theme.bg,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = CyberCyan.copy(alpha = 0.15f),
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "MITRE ATT&CK",
                                        tint = CyberCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "MITRE ATT&CK & D3FEND Matrices",
                                    color = theme.textPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "15 Enterprise Tactics (v19.2) • Mobile & ICS • 7 D3FEND Tactics",
                                    color = theme.textMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.surface)
                )

                TabRow(
                    selectedTabIndex = selectedSubTab,
                    containerColor = theme.surface,
                    contentColor = CyberCyan,
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
                        text = { Text("📊 ATT&CK Matrix", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_attack_14col_matrix")
                    )
                    Tab(
                        selected = selectedSubTab == 1,
                        onClick = { selectedSubTab = 1 },
                        text = { Text("🛡️ D3FEND Matrix", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_d3fend_7col_matrix")
                    )
                    Tab(
                        selected = selectedSubTab == 2,
                        onClick = { selectedSubTab = 2 },
                        text = { Text("📋 TTP Catalog", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_attack_catalog")
                    )
                    Tab(
                        selected = selectedSubTab == 3,
                        onClick = { selectedSubTab = 3 },
                        text = { Text("🌐 D3.js Flow", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_attack_d3_flow")
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
                    modifier = Modifier.testTag("fab_ask_gemini_attack")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp),
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(theme.bg)
        ) {
            when (selectedSubTab) {
                0 -> {
                    MitreMatrixScreen(displayMode = 0)
                }
                1 -> {
                    MitreMatrixScreen(displayMode = 1)
                }
                2 -> {
                    MitreMatrixScreen(displayMode = 2)
                }
                3 -> {
                    MitreInteractiveGraphComposable(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
